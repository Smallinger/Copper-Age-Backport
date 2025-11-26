package com.github.smallinger.copperagebackport.entity.ai.behavior;

import com.github.smallinger.copperagebackport.ModMemoryTypes;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.google.common.collect.ImmutableMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;

public class TransportItemsBetweenContainers extends Behavior<PathfinderMob> {
    public static final int TARGET_INTERACTION_TIME = 60;
    private static final int VISITED_POSITIONS_MEMORY_TIME = 6000;
    private static final int TRANSPORTED_ITEM_MAX_STACK_SIZE = 16;
    private static final int MAX_VISITED_POSITIONS = 10;
    private static final int MAX_UNREACHABLE_POSITIONS = 50;
    private static final int PASSENGER_MOB_TARGET_SEARCH_DISTANCE = 1;
    private static final int IDLE_COOLDOWN = 140;
    private static final double CLOSE_ENOUGH_TO_START_QUEUING_DISTANCE = 3.0;
    private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_DISTANCE = 0.5;
    private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_PATH_END_DISTANCE = 1.0;
    private static final double CLOSE_ENOUGH_TO_CONTINUE_INTERACTING_WITH_TARGET = 2.0;
    private final float speedModifier;
    private final int horizontalSearchDistance;
    private final int verticalSearchDistance;
    private final Predicate<BlockState> sourceBlockType;
    private final Predicate<BlockState> destinationBlockType;
    private final Predicate<TransportItemsBetweenContainers.TransportItemTarget> shouldQueueForTarget;
    private final Consumer<PathfinderMob> onStartTravelling;
    private final Map<TransportItemsBetweenContainers.ContainerInteractionState, TransportItemsBetweenContainers.OnTargetReachedInteraction> onTargetInteractionActions;
    @Nullable
    private TransportItemsBetweenContainers.TransportItemTarget target = null;
    private TransportItemsBetweenContainers.TransportItemState state;
    @Nullable
    private TransportItemsBetweenContainers.ContainerInteractionState interactionState;
    private int ticksSinceReachingTarget;

    public TransportItemsBetweenContainers(
        float speedModifier,
        Predicate<BlockState> sourceBlockType,
        Predicate<BlockState> destinationBlockType,
        int horizontalSearchDistance,
        int verticalSearchDistance,
        Map<TransportItemsBetweenContainers.ContainerInteractionState, TransportItemsBetweenContainers.OnTargetReachedInteraction> onTargetInteractionActions,
        Consumer<PathfinderMob> onStartTravelling,
        Predicate<TransportItemsBetweenContainers.TransportItemTarget> shouldQueueForTarget
    ) {
        super(
            ImmutableMap.of(
                ModMemoryTypes.VISITED_BLOCK_POSITIONS.get(),
                MemoryStatus.REGISTERED,
                ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get(),
                MemoryStatus.REGISTERED,
                ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(),
                MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.IS_PANICKING,
                MemoryStatus.VALUE_ABSENT
            )
        );
        this.speedModifier = speedModifier;
        this.sourceBlockType = sourceBlockType;
        this.destinationBlockType = destinationBlockType;
        this.horizontalSearchDistance = horizontalSearchDistance;
        this.verticalSearchDistance = verticalSearchDistance;
        this.onStartTravelling = onStartTravelling;
        this.shouldQueueForTarget = shouldQueueForTarget;
        this.onTargetInteractionActions = onTargetInteractionActions;
        this.state = TransportItemsBetweenContainers.TransportItemState.TRAVELLING;
    }

    protected void start(ServerLevel level, PathfinderMob mob, long gameTime) {
        // 1.20.1 doesn't have setCanPathToTargetsBelowSurface
    }

    protected boolean checkExtraStartConditions(ServerLevel level, PathfinderMob mob) {
        // Verhindere Start wenn ButtonPress aktiv ist (darf nicht unterbrochen werden)
        if (mob.getBrain().getMemory(ModMemoryTypes.IS_PRESSING_BUTTON.get()).orElse(false)) {
            return false;
        }
        return !mob.isLeashed();
    }

    protected boolean canStillUse(ServerLevel level, PathfinderMob mob, long gameTime) {
        return mob.getBrain().getMemory(ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get()).isEmpty() 
            && mob.getBrain().getMemory(MemoryModuleType.IS_PANICKING).isEmpty() 
            && !mob.isLeashed();
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    protected void tick(ServerLevel level, PathfinderMob mob, long gameTime) {
        boolean flag = this.updateInvalidTarget(level, mob);
        if (this.target == null) {
            this.stop(level, mob, gameTime);
        } else if (!flag) {
            if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.QUEUING)) {
                this.onQueuingForTarget(this.target, level, mob);
            }

            if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.TRAVELLING)) {
                this.onTravelToTarget(this.target, level, mob);
            }

            if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.INTERACTING)) {
                this.onReachedTarget(this.target, level, mob);
            }
        }
    }

    private boolean updateInvalidTarget(ServerLevel level, PathfinderMob mob) {
        if (!this.hasValidTarget(level, mob)) {
            this.stopTargetingCurrentTarget(mob);
            Optional<TransportItemsBetweenContainers.TransportItemTarget> optional = this.getTransportTarget(level, mob);
            if (optional.isPresent()) {
                this.target = optional.get();
                this.onStartTravelling(mob);
                this.setVisitedBlockPos(mob, level, this.target.pos);
                return true;
            } else {
                this.enterCooldownAfterNoMatchingTargetFound(mob);
                return true;
            }
        } else {
            return false;
        }
    }

    private void onQueuingForTarget(TransportItemsBetweenContainers.TransportItemTarget target, Level level, PathfinderMob mob) {
        if (!this.isAnotherMobInteractingWithTarget(target, level)) {
            this.resumeTravelling(mob);
        }
    }

    protected void onTravelToTarget(TransportItemsBetweenContainers.TransportItemTarget target, Level level, PathfinderMob mob) {
        if (this.isWithinTargetDistance(3.0, target, level, mob, this.getCenterPos(mob))
            && this.isAnotherMobInteractingWithTarget(target, level)) {
            this.startQueuing(mob);
        } else if (this.isWithinTargetDistance(getInteractionRange(mob), target, level, mob, this.getCenterPos(mob))) {
            this.startOnReachedTargetInteraction(target, mob);
        } else {
            this.walkTowardsTarget(mob);
        }
    }

    private Vec3 getCenterPos(PathfinderMob mob) {
        return this.setMiddleYPosition(mob, mob.position());
    }

    protected void onReachedTarget(TransportItemsBetweenContainers.TransportItemTarget target, Level level, PathfinderMob mob) {
        if (!this.isWithinTargetDistance(2.0, target, level, mob, this.getCenterPos(mob))) {
            this.onStartTravelling(mob);
        } else {
            this.ticksSinceReachingTarget++;
            this.onTargetInteraction(target, mob);
            if (this.ticksSinceReachingTarget >= 60) {
                this.doReachedTargetInteraction(
                    mob,
                    target.container,
                    this::pickUpItems,
                    (mobParam, containerParam) -> this.stopTargetingCurrentTarget(mob),
                    this::putDownItem,
                    (mobParam, containerParam) -> this.stopTargetingCurrentTarget(mob)
                );
                this.onStartTravelling(mob);
            }
        }
    }

    private void startQueuing(PathfinderMob mob) {
        this.stopInPlace(mob);
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.QUEUING);
    }

    private void resumeTravelling(PathfinderMob mob) {
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.TRAVELLING);
        this.walkTowardsTarget(mob);
    }

    private void walkTowardsTarget(PathfinderMob mob) {
        if (this.target != null) {
            BehaviorUtils.setWalkAndLookTargetMemories(mob, this.target.pos, this.speedModifier, 0);
        }
    }

    private void startOnReachedTargetInteraction(TransportItemsBetweenContainers.TransportItemTarget target, PathfinderMob mob) {
        this.doReachedTargetInteraction(
            mob,
            target.container,
            this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_ITEM),
            this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_NO_ITEM),
            this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PLACE_ITEM),
            this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PLACE_NO_ITEM)
        );
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.INTERACTING);
    }

    private void onStartTravelling(PathfinderMob mob) {
        this.onStartTravelling.accept(mob);
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.TRAVELLING);
        this.interactionState = null;
        this.ticksSinceReachingTarget = 0;
    }

    private BiConsumer<PathfinderMob, Container> onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState interactionState) {
        return (mob, container) -> this.setInteractionState(interactionState);
    }

    private void setTransportingState(TransportItemsBetweenContainers.TransportItemState transportingState) {
        this.state = transportingState;
    }

    private void setInteractionState(TransportItemsBetweenContainers.ContainerInteractionState interactionState) {
        this.interactionState = interactionState;
    }

    private void onTargetInteraction(TransportItemsBetweenContainers.TransportItemTarget target, PathfinderMob mob) {
        mob.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(target.pos));
        this.stopInPlace(mob);
        if (this.interactionState != null) {
            Optional.ofNullable(this.onTargetInteractionActions.get(this.interactionState))
                .ifPresent(action -> action.accept(mob, target, this.ticksSinceReachingTarget));
        }
    }

    private void doReachedTargetInteraction(
        PathfinderMob mob,
        Container container,
        BiConsumer<PathfinderMob, Container> pickupItem,
        BiConsumer<PathfinderMob, Container> pickupNoItem,
        BiConsumer<PathfinderMob, Container> placeItem,
        BiConsumer<PathfinderMob, Container> placeNoItem
    ) {
        if (isPickingUpItems(mob)) {
            if (matchesGettingItemsRequirement(container)) {
                pickupItem.accept(mob, container);
            } else {
                pickupNoItem.accept(mob, container);
            }
        } else if (matchesLeavingItemsRequirement(mob, container)) {
            placeItem.accept(mob, container);
        } else {
            placeNoItem.accept(mob, container);
        }
    }

    private Optional<TransportItemsBetweenContainers.TransportItemTarget> getTransportTarget(ServerLevel level, PathfinderMob mob) {
        AABB aabb = this.getTargetSearchArea(mob);
        Set<GlobalPos> set = getVisitedPositions(mob);
        Set<GlobalPos> set1 = getUnreachablePositions(mob);
        List<ChunkPos> list = ChunkPos.rangeClosed(new ChunkPos(mob.blockPosition()), Math.floorDiv(this.getHorizontalSearchDistance(mob), 16) + 1)
            .toList();
        TransportItemsBetweenContainers.TransportItemTarget transportitemsbetweencontainers$transportitemtarget = null;
        double d0 = Float.MAX_VALUE;

        for (ChunkPos chunkpos : list) {
            LevelChunk levelchunk = level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
            if (levelchunk != null) {
                for (BlockEntity blockentity : levelchunk.getBlockEntities().values()) {
                    if (blockentity instanceof ChestBlockEntity chestblockentity) {
                        double d1 = chestblockentity.getBlockPos().distToCenterSqr(mob.position());
                        if (d1 < d0) {
                            TransportItemsBetweenContainers.TransportItemTarget transportitemsbetweencontainers$transportitemtarget1 = this.isTargetValidToPick(
                                mob, level, chestblockentity, set, set1, aabb
                            );
                            if (transportitemsbetweencontainers$transportitemtarget1 != null) {
                                transportitemsbetweencontainers$transportitemtarget = transportitemsbetweencontainers$transportitemtarget1;
                                d0 = d1;
                            }
                        }
                    }
                }
            }
        }

        return transportitemsbetweencontainers$transportitemtarget == null
            ? Optional.empty()
            : Optional.of(transportitemsbetweencontainers$transportitemtarget);
    }

    @Nullable
    private TransportItemsBetweenContainers.TransportItemTarget isTargetValidToPick(
        PathfinderMob mob, Level level, BlockEntity blockEntity, Set<GlobalPos> visited, Set<GlobalPos> unreachable, AABB searchArea
    ) {
        BlockPos blockpos = blockEntity.getBlockPos();
        boolean flag = searchArea.contains(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        if (!flag) {
            return null;
        } else {
            TransportItemsBetweenContainers.TransportItemTarget transportitemsbetweencontainers$transportitemtarget = TransportItemsBetweenContainers.TransportItemTarget.tryCreatePossibleTarget(
                blockEntity, level
            );
            if (transportitemsbetweencontainers$transportitemtarget == null) {
                return null;
            } else {
                boolean flag1 = this.isWantedBlock(mob, transportitemsbetweencontainers$transportitemtarget.state)
                    && !this.isPositionAlreadyVisited(visited, unreachable, transportitemsbetweencontainers$transportitemtarget, level)
                    && !this.isContainerLocked(transportitemsbetweencontainers$transportitemtarget);
                return flag1 ? transportitemsbetweencontainers$transportitemtarget : null;
            }
        }
    }

    private boolean isContainerLocked(TransportItemsBetweenContainers.TransportItemTarget target) {
        // 1.20.1 BaseContainerBlockEntity doesn't have isLocked()
        return false;
    }

    private boolean hasValidTarget(Level level, PathfinderMob mob) {
        boolean flag = this.target != null && this.isWantedBlock(mob, this.target.state) && this.targetHasNotChanged(level, this.target);
        if (flag && !this.isTargetBlocked(level, this.target)) {
            if (!this.state.equals(TransportItemsBetweenContainers.TransportItemState.TRAVELLING)) {
                return true;
            }

            if (this.hasValidTravellingPath(level, this.target, mob)) {
                return true;
            }

            this.markVisitedBlockPosAsUnreachable(mob, level, this.target.pos);
        }

        return false;
    }

    private boolean hasValidTravellingPath(Level level, TransportItemsBetweenContainers.TransportItemTarget target, PathfinderMob mob) {
        Path path = mob.getNavigation().getPath() == null ? mob.getNavigation().createPath(target.pos, 0) : mob.getNavigation().getPath();
        Vec3 vec3 = this.getPositionToReachTargetFrom(path, mob);
        boolean flag = this.isWithinTargetDistance(getInteractionRange(mob), target, level, mob, vec3);
        boolean flag1 = path == null && !flag;
        return flag1 || this.targetIsReachableFromPosition(level, flag, vec3, target, mob);
    }

    private Vec3 getPositionToReachTargetFrom(@Nullable Path path, PathfinderMob mob) {
        boolean flag = path == null || path.getEndNode() == null;
        // 1.20.1 doesn't have getBottomCenter() - use Vec3.atBottomCenterOf or manual calculation
        Vec3 vec3 = flag ? mob.position() : Vec3.atBottomCenterOf(path.getEndNode().asBlockPos());
        return this.setMiddleYPosition(mob, vec3);
    }

    private Vec3 setMiddleYPosition(PathfinderMob mob, Vec3 pos) {
        return pos.add(0.0, mob.getBoundingBox().getYsize() / 2.0, 0.0);
    }

    private boolean isTargetBlocked(Level level, TransportItemsBetweenContainers.TransportItemTarget target) {
        return ChestBlock.isChestBlockedAt(level, target.pos);
    }

    private boolean targetHasNotChanged(Level level, TransportItemsBetweenContainers.TransportItemTarget target) {
        return target.blockEntity.equals(level.getBlockEntity(target.pos));
    }

    private Stream<TransportItemsBetweenContainers.TransportItemTarget> getConnectedTargets(
        TransportItemsBetweenContainers.TransportItemTarget target, Level level
    ) {
        if (target.state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            // 1.20.1 doesn't have ChestBlock.getConnectedBlockPos - calculate manually
            BlockPos connectedPos = getConnectedChestPos(target.pos, target.state);
            TransportItemsBetweenContainers.TransportItemTarget transportitemsbetweencontainers$transportitemtarget = 
                TransportItemsBetweenContainers.TransportItemTarget.tryCreatePossibleTarget(connectedPos, level);
            return transportitemsbetweencontainers$transportitemtarget != null
                ? Stream.of(target, transportitemsbetweencontainers$transportitemtarget)
                : Stream.of(target);
        } else {
            return Stream.of(target);
        }
    }

    private BlockPos getConnectedChestPos(BlockPos pos, BlockState state) {
        Direction direction = ChestBlock.getConnectedDirection(state);
        return pos.relative(direction);
    }

    private AABB getTargetSearchArea(PathfinderMob mob) {
        int i = this.getHorizontalSearchDistance(mob);
        return new AABB(mob.blockPosition()).inflate(i, this.getVerticalSearchDistance(mob), i);
    }

    private int getHorizontalSearchDistance(PathfinderMob mob) {
        return mob.isPassenger() ? 1 : this.horizontalSearchDistance;
    }

    private int getVerticalSearchDistance(PathfinderMob mob) {
        return mob.isPassenger() ? 1 : this.verticalSearchDistance;
    }

    private static Set<GlobalPos> getVisitedPositions(PathfinderMob mob) {
        return mob.getBrain().getMemory(ModMemoryTypes.VISITED_BLOCK_POSITIONS.get()).orElse(Set.of());
    }

    private static Set<GlobalPos> getUnreachablePositions(PathfinderMob mob) {
        return mob.getBrain().getMemory(ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get()).orElse(Set.of());
    }

    private boolean isPositionAlreadyVisited(
        Set<GlobalPos> visited, Set<GlobalPos> unreachable, TransportItemsBetweenContainers.TransportItemTarget target, Level level
    ) {
        return this.getConnectedTargets(target, level)
            .map(t -> GlobalPos.of(level.dimension(), t.pos))
            .anyMatch(globalPos -> visited.contains(globalPos) || unreachable.contains(globalPos));
    }

    private static boolean hasFinishedPath(PathfinderMob mob) {
        return mob.getNavigation().getPath() != null && mob.getNavigation().getPath().isDone();
    }

    protected void setVisitedBlockPos(PathfinderMob mob, Level level, BlockPos pos) {
        Set<GlobalPos> set = new HashSet<>(getVisitedPositions(mob));
        set.add(GlobalPos.of(level.dimension(), pos));
        if (set.size() > 10) {
            this.enterCooldownAfterNoMatchingTargetFound(mob);
        } else {
            mob.getBrain().setMemoryWithExpiry(ModMemoryTypes.VISITED_BLOCK_POSITIONS.get(), set, 6000L);
        }
    }

    protected void markVisitedBlockPosAsUnreachable(PathfinderMob mob, Level level, BlockPos pos) {
        Set<GlobalPos> set = new HashSet<>(getVisitedPositions(mob));
        set.remove(GlobalPos.of(level.dimension(), pos));
        Set<GlobalPos> set1 = new HashSet<>(getUnreachablePositions(mob));
        set1.add(GlobalPos.of(level.dimension(), pos));
        if (set1.size() > 50) {
            this.enterCooldownAfterNoMatchingTargetFound(mob);
        } else {
            mob.getBrain().setMemoryWithExpiry(ModMemoryTypes.VISITED_BLOCK_POSITIONS.get(), set, 6000L);
            mob.getBrain().setMemoryWithExpiry(ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get(), set1, 6000L);
        }
    }

    private boolean isWantedBlock(PathfinderMob mob, BlockState state) {
        return isPickingUpItems(mob) ? this.sourceBlockType.test(state) : this.destinationBlockType.test(state);
    }

    private static double getInteractionRange(PathfinderMob mob) {
        return hasFinishedPath(mob) ? 1.0 : 0.5;
    }

    private boolean isWithinTargetDistance(
        double distance, TransportItemsBetweenContainers.TransportItemTarget target, Level level, PathfinderMob mob, Vec3 center
    ) {
        AABB aabb = mob.getBoundingBox();
        AABB aabb1 = AABB.ofSize(center, aabb.getXsize(), aabb.getYsize(), aabb.getZsize());
        return target.state.getCollisionShape(level, target.pos).bounds().inflate(distance, 0.5, distance).move(target.pos).intersects(aabb1);
    }

    private boolean targetIsReachableFromPosition(
        Level level, boolean withinDistance, Vec3 targetPos, TransportItemsBetweenContainers.TransportItemTarget target, PathfinderMob mob
    ) {
        return withinDistance && this.canSeeAnyTargetSide(target, level, mob, targetPos);
    }

    private boolean canSeeAnyTargetSide(TransportItemsBetweenContainers.TransportItemTarget target, Level level, PathfinderMob mob, Vec3 pos) {
        Vec3 vec3 = target.pos.getCenter();
        return Direction.stream()
            .map(direction -> vec3.add(0.5 * direction.getStepX(), 0.5 * direction.getStepY(), 0.5 * direction.getStepZ()))
            .map(targetVec -> level.clip(new ClipContext(pos, targetVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)))
            .anyMatch(hitResult -> hitResult.getType() == HitResult.Type.BLOCK && hitResult.getBlockPos().equals(target.pos));
    }

    private boolean isAnotherMobInteractingWithTarget(TransportItemsBetweenContainers.TransportItemTarget target, Level level) {
        return this.getConnectedTargets(target, level).anyMatch(this.shouldQueueForTarget);
    }

    private static boolean isPickingUpItems(PathfinderMob mob) {
        return mob.getMainHandItem().isEmpty();
    }

    private static boolean matchesGettingItemsRequirement(Container container) {
        return !container.isEmpty();
    }

    private static boolean matchesLeavingItemsRequirement(PathfinderMob mob, Container container) {
        return container.isEmpty() || hasItemMatchingHandItem(mob, container);
    }

    private static boolean hasItemMatchingHandItem(PathfinderMob mob, Container container) {
        ItemStack itemstack = mob.getMainHandItem();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemstack1 = container.getItem(i);
            if (ItemStack.isSameItem(itemstack1, itemstack)) {
                return true;
            }
        }

        return false;
    }

    private void pickUpItems(PathfinderMob mob, Container container) {
        mob.setItemSlot(EquipmentSlot.MAINHAND, pickupItemFromContainer(container));
        mob.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        container.setChanged();
        this.clearMemoriesAfterMatchingTargetFound(mob);
    }

    private void putDownItem(PathfinderMob mob, Container container) {
        ItemStack itemstack = addItemsToContainer(mob, container);
        container.setChanged();
        mob.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
        if (itemstack.isEmpty()) {
            this.clearMemoriesAfterMatchingTargetFound(mob);
        } else {
            this.stopTargetingCurrentTarget(mob);
        }
    }

    private static ItemStack pickupItemFromContainer(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemstack = container.getItem(i);
            if (!itemstack.isEmpty()) {
                int j = Math.min(itemstack.getCount(), CommonConfig.golemTransportStackSize());
                return container.removeItem(i, j);
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack addItemsToContainer(PathfinderMob mob, Container container) {
        ItemStack itemstack = mob.getMainHandItem();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemstack1 = container.getItem(i);
            if (itemstack1.isEmpty()) {
                container.setItem(i, itemstack);
                return ItemStack.EMPTY;
            }

            if (ItemStack.isSameItemSameTags(itemstack1, itemstack) && itemstack1.getCount() < itemstack1.getMaxStackSize()) {
                int j = itemstack1.getMaxStackSize() - itemstack1.getCount();
                int k = Math.min(j, itemstack.getCount());
                itemstack1.setCount(itemstack1.getCount() + k);
                itemstack.setCount(itemstack.getCount() - j);
                container.setItem(i, itemstack1);
                if (itemstack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return itemstack;
    }

    protected void stopTargetingCurrentTarget(PathfinderMob mob) {
        this.ticksSinceReachingTarget = 0;
        this.target = null;
        mob.getNavigation().stop();
        mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    protected void clearMemoriesAfterMatchingTargetFound(PathfinderMob mob) {
        this.stopTargetingCurrentTarget(mob);
        mob.getBrain().eraseMemory(ModMemoryTypes.VISITED_BLOCK_POSITIONS.get());
        mob.getBrain().eraseMemory(ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get());
    }

    private void enterCooldownAfterNoMatchingTargetFound(PathfinderMob mob) {
        this.stopTargetingCurrentTarget(mob);
        mob.getBrain().setMemory(ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(), 140);
        mob.getBrain().eraseMemory(ModMemoryTypes.VISITED_BLOCK_POSITIONS.get());
        mob.getBrain().eraseMemory(ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get());
        // Setze LAST_CONTAINER_EMPTY Memory für ButtonPress-Trigger (20% Chance)
        mob.getBrain().setMemory(ModMemoryTypes.LAST_CONTAINER_EMPTY.get(), mob.level().getGameTime());
    }

    protected void stop(ServerLevel level, PathfinderMob mob, long gameTime) {
        this.onStartTravelling(mob);
        // 1.20.1 doesn't have setCanPathToTargetsBelowSurface
    }

    private void stopInPlace(PathfinderMob mob) {
        mob.getNavigation().stop();
        mob.setXxa(0.0F);
        mob.setYya(0.0F);
        mob.setSpeed(0.0F);
        mob.setDeltaMovement(0.0, mob.getDeltaMovement().y, 0.0);
    }

    public static enum ContainerInteractionState {
        PICKUP_ITEM,
        PICKUP_NO_ITEM,
        PLACE_ITEM,
        PLACE_NO_ITEM;
    }

    @FunctionalInterface
    public interface OnTargetReachedInteraction {
        void accept(PathfinderMob mob, TransportItemsBetweenContainers.TransportItemTarget target, Integer ticksSinceReachingTarget);
    }

    public static enum TransportItemState {
        TRAVELLING,
        QUEUING,
        INTERACTING;
    }

    public record TransportItemTarget(BlockPos pos, Container container, BlockEntity blockEntity, BlockState state) {
        @Nullable
        public static TransportItemsBetweenContainers.TransportItemTarget tryCreatePossibleTarget(BlockEntity blockEntity, Level level) {
            BlockPos blockpos = blockEntity.getBlockPos();
            BlockState blockstate = blockEntity.getBlockState();
            Container container = getBlockEntityContainer(blockEntity, blockstate, level, blockpos);
            return container != null ? new TransportItemsBetweenContainers.TransportItemTarget(blockpos, container, blockEntity, blockstate) : null;
        }

        @Nullable
        public static TransportItemsBetweenContainers.TransportItemTarget tryCreatePossibleTarget(BlockPos pos, Level level) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            return blockentity == null ? null : tryCreatePossibleTarget(blockentity, level);
        }

        @Nullable
        private static Container getBlockEntityContainer(BlockEntity blockEntity, BlockState state, Level level, BlockPos pos) {
            if (state.getBlock() instanceof ChestBlock chestblock) {
                return ChestBlock.getContainer(chestblock, state, level, pos, false);
            } else {
                return blockEntity instanceof Container container ? container : null;
            }
        }
    }
}
