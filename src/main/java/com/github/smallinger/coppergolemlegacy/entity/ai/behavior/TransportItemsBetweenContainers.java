package com.github.smallinger.coppergolemlegacy.entity.ai.behavior;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.ModMemoryTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Behavior fÃƒÂ¼r Transport von Items zwischen Containern
 * Portiert von Minecraft 1.21.10 nach NeoForge 1.21.1
 */
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
    private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_PATH_END_DISTANCE = 1.1;
    private static final double CLOSE_ENOUGH_TO_CONTINUE_INTERACTING_WITH_TARGET = 2.0;
    
    private final float speedModifier;
    private final int horizontalSearchDistance;
    private final int verticalSearchDistance;
    private final Predicate<BlockState> sourceBlockType;
    private final Predicate<BlockState> destinationBlockType;
    private final Predicate<TransportItemTarget> shouldQueueForTarget;
    private final Consumer<PathfinderMob> onStartTravelling;
    private final Map<ContainerInteractionState, OnTargetReachedInteraction> onTargetInteractionActions;
    
    @Nullable
    private TransportItemTarget target = null;
    private TransportItemState state;
    @Nullable
    private ContainerInteractionState interactionState;
    private int ticksSinceReachingTarget;

    public TransportItemsBetweenContainers(
        float speedModifier,
        Predicate<BlockState> sourceBlockType,
        Predicate<BlockState> destinationBlockType,
        int horizontalSearchDistance,
        int verticalSearchDistance,
        Map<ContainerInteractionState, OnTargetReachedInteraction> onTargetInteractionActions,
        Consumer<PathfinderMob> onStartTravelling,
        Predicate<TransportItemTarget> shouldQueueForTarget
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
                MemoryStatus.VALUE_ABSENT,
                ModMemoryTypes.IS_PRESSING_BUTTON.get(),
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
        this.state = TransportItemState.TRAVELLING;
    }

    @Override
    protected void start(ServerLevel level, PathfinderMob mob, long gameTime) {
        // Note: setCanPathToTargetsBelowSurface() doesn't exist in 1.21.1
        // This was a minor pathfinding optimization in 1.21.10
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PathfinderMob mob) {
        return !mob.isLeashed();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, PathfinderMob mob, long gameTime) {
        return mob.getBrain().getMemory(ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get()).isEmpty() 
            && mob.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET) == false 
            && !mob.isLeashed();
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    @Override
    protected void tick(ServerLevel level, PathfinderMob mob, long gameTime) {
        boolean invalidTarget = this.updateInvalidTarget(level, mob);
        if (this.target == null) {
            this.stop(level, mob, gameTime);
        } else if (!invalidTarget) {
            if (this.state.equals(TransportItemState.QUEUING)) {
                this.onQueuingForTarget(this.target, level, mob);
            }

            if (this.state.equals(TransportItemState.TRAVELLING)) {
                this.onTravelToTarget(this.target, level, mob);
            }

            if (this.state.equals(TransportItemState.INTERACTING)) {
                this.onReachedTarget(this.target, level, mob);
            }
        }
    }

    private boolean updateInvalidTarget(ServerLevel level, PathfinderMob mob) {
        if (!this.hasValidTarget(level, mob)) {
            this.stopTargetingCurrentTarget(mob);
            Optional<TransportItemTarget> optional = this.getTransportTarget(level, mob);
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

    private void onQueuingForTarget(TransportItemTarget target, Level level, PathfinderMob mob) {
        if (!this.isAnotherMobInteractingWithTarget(target, level)) {
            this.resumeTravelling(mob);
        }
    }

    protected void onTravelToTarget(TransportItemTarget target, Level level, PathfinderMob mob) {
        double interactionRange = getInteractionRange(mob);
        
        if (this.isWithinTargetDistance(CLOSE_ENOUGH_TO_START_QUEUING_DISTANCE, target, level, mob, this.getCenterPos(mob))
            && this.isAnotherMobInteractingWithTarget(target, level)) {
            this.startQueuing(mob);
        } else if (this.isWithinTargetDistance(interactionRange, target, level, mob, this.getCenterPos(mob))) {
            this.startOnReachedTargetInteraction(target, mob);
        } else {
            this.walkTowardsTarget(mob);
        }
    }

    private Vec3 getCenterPos(PathfinderMob mob) {
        return this.setMiddleYPosition(mob, mob.position());
    }

    protected void onReachedTarget(TransportItemTarget target, Level level, PathfinderMob mob) {
        if (!this.isWithinTargetDistance(CLOSE_ENOUGH_TO_CONTINUE_INTERACTING_WITH_TARGET, target, level, mob, this.getCenterPos(mob))) {
            this.onStartTravelling(mob);
        } else {
            this.ticksSinceReachingTarget++;
            this.onTargetInteraction(target, mob);
            if (this.ticksSinceReachingTarget >= TARGET_INTERACTION_TIME) {
                this.doReachedTargetInteraction(
                    mob,
                    target.container,
                    this::pickUpItems,
                    (p1, p2) -> this.stopTargetingCurrentTarget(mob),
                    this::putDownItem,
                    (p1, p2) -> this.stopTargetingCurrentTarget(mob)
                );
                this.onStartTravelling(mob);
            }
        }
    }

    private void startQueuing(PathfinderMob mob) {
        this.stopInPlace(mob);
        this.setTransportingState(TransportItemState.QUEUING);
    }

    private void resumeTravelling(PathfinderMob mob) {
        this.setTransportingState(TransportItemState.TRAVELLING);
        this.walkTowardsTarget(mob);
    }

    private void walkTowardsTarget(PathfinderMob mob) {
        if (this.target != null) {
            BehaviorUtils.setWalkAndLookTargetMemories(mob, this.target.pos, this.speedModifier, 0);
        }
    }

    private void startOnReachedTargetInteraction(TransportItemTarget target, PathfinderMob mob) {
        this.doReachedTargetInteraction(
            mob,
            target.container,
            this.onReachedInteraction(ContainerInteractionState.PICKUP_ITEM),
            this.onReachedInteraction(ContainerInteractionState.PICKUP_NO_ITEM),
            this.onReachedInteraction(ContainerInteractionState.PLACE_ITEM),
            this.onReachedInteraction(ContainerInteractionState.PLACE_NO_ITEM)
        );
        this.setTransportingState(TransportItemState.INTERACTING);
    }

    private void onStartTravelling(PathfinderMob mob) {
        this.onStartTravelling.accept(mob);
        this.setTransportingState(TransportItemState.TRAVELLING);
        this.interactionState = null;
        this.ticksSinceReachingTarget = 0;
    }

    private BiConsumer<PathfinderMob, Container> onReachedInteraction(ContainerInteractionState interactionState) {
        return (mob, container) -> this.setInteractionState(interactionState);
    }

    private void setTransportingState(TransportItemState transportingState) {
        this.state = transportingState;
    }

    private void setInteractionState(ContainerInteractionState interactionState) {
        this.interactionState = interactionState;
    }

    private void onTargetInteraction(TransportItemTarget target, PathfinderMob mob) {
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

    private Optional<TransportItemTarget> getTransportTarget(ServerLevel level, PathfinderMob mob) {
        AABB aabb = this.getTargetSearchArea(mob);
        Set<GlobalPos> visited = getVisitedPositions(mob);
        Set<GlobalPos> unreachable = getUnreachablePositions(mob);
        
        // Manual chunk range calculation for 1.20.1
        ChunkPos centerChunk = new ChunkPos(mob.blockPosition());
        int chunkRadius = Math.floorDiv(this.getHorizontalSearchDistance(mob), 16) + 1;
        List<ChunkPos> chunks = new ArrayList<>();
        for (int x = centerChunk.x - chunkRadius; x <= centerChunk.x + chunkRadius; x++) {
            for (int z = centerChunk.z - chunkRadius; z <= centerChunk.z + chunkRadius; z++) {
                chunks.add(new ChunkPos(x, z));
            }
        }
        
        TransportItemTarget bestTarget = null;
        double bestDistance = Float.MAX_VALUE;
        int chestsFound = 0;
        int vanillaChestsFound = 0;

        for (ChunkPos chunkPos : chunks) {
            LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
            if (chunk != null) {
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    // Support all container types (BaseContainerBlockEntity) - not just chests
                    if (blockEntity instanceof BaseContainerBlockEntity containerBlockEntity) {
                        chestsFound++;
                        BlockState state = containerBlockEntity.getBlockState();
                        if (state.getBlock() == net.minecraft.world.level.block.Blocks.CHEST) {
                            vanillaChestsFound++;
                        }
                        
                        double distance = containerBlockEntity.getBlockPos().distToCenterSqr(mob.position());
                        if (distance < bestDistance) {
                            TransportItemTarget candidateTarget = this.isTargetValidToPick(
                                mob, level, containerBlockEntity, visited, unreachable, aabb
                            );
                            if (candidateTarget != null) {
                                bestTarget = candidateTarget;
                                bestDistance = distance;
                            }
                        }
                    }
                }
            }
        }
        return bestTarget == null ? Optional.empty() : Optional.of(bestTarget);
    }

    @Nullable
    private TransportItemTarget isTargetValidToPick(
        PathfinderMob mob, Level level, BlockEntity blockEntity, 
        Set<GlobalPos> visited, Set<GlobalPos> unreachable, AABB searchArea
    ) {
        BlockPos blockpos = blockEntity.getBlockPos();
        boolean inArea = searchArea.contains(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        if (!inArea) {
            return null;
        }
        
        TransportItemTarget target = TransportItemTarget.tryCreatePossibleTarget(blockEntity, level);
        if (target == null) {
            return null;
        }
        
        boolean isWanted = this.isWantedBlock(mob, target.state);
        boolean notVisited = !this.isPositionAlreadyVisited(visited, unreachable, target, level);
        boolean notLocked = !this.isContainerLocked(target);
        
        boolean isValid = isWanted && notVisited && notLocked;
        return isValid ? target : null;
    }

    private boolean isContainerLocked(TransportItemTarget target) {
        // Note: BaseContainerBlockEntity.isLocked() doesn't exist in 1.21.1
        // Locked chests will fail to open anyway, so we can skip this check
        return false;
    }

    private boolean hasValidTarget(Level level, PathfinderMob mob) {
        boolean hasTarget = this.target != null 
            && this.isWantedBlock(mob, this.target.state) 
            && this.targetHasNotChanged(level, this.target);
            
        if (hasTarget && !this.isTargetBlocked(level, this.target)) {
            if (!this.state.equals(TransportItemState.TRAVELLING)) {
                return true;
            }

            if (this.hasValidTravellingPath(level, this.target, mob)) {
                return true;
            }

            this.markVisitedBlockPosAsUnreachable(mob, level, this.target.pos);
        }

        return false;
    }

    private boolean hasValidTravellingPath(Level level, TransportItemTarget target, PathfinderMob mob) {
        Path path = mob.getNavigation().getPath() == null 
            ? mob.getNavigation().createPath(target.pos, 0) 
            : mob.getNavigation().getPath();
        Vec3 vec3 = this.getPositionToReachTargetFrom(path, mob);
        boolean withinRange = this.isWithinTargetDistance(getInteractionRange(mob), target, level, mob, vec3);
        boolean noPathAndNotInRange = path == null && !withinRange;
        boolean reachable = this.targetIsReachableFromPosition(level, withinRange, vec3, target, mob);
        
        // If we have a path, the target is reachable (even if not in range yet)
        boolean hasValidPath = path != null;
        
        return hasValidPath || noPathAndNotInRange || reachable;
    }

    private Vec3 getPositionToReachTargetFrom(@Nullable Path path, PathfinderMob mob) {
        boolean noPath = path == null || path.getEndNode() == null;
        Vec3 vec3 = noPath ? mob.position() : new BlockPos(path.getEndNode().x, path.getEndNode().y, path.getEndNode().z).getCenter();
        return this.setMiddleYPosition(mob, vec3);
    }

    private Vec3 setMiddleYPosition(PathfinderMob mob, Vec3 pos) {
        return pos.add(0.0, mob.getBbHeight() / 2.0, 0.0);
    }

    private boolean isTargetBlocked(Level level, TransportItemTarget target) {
        // Chests need space above, but barrels only need one accessible side
        if (target.state.getBlock() instanceof ChestBlock) {
            return ChestBlock.isChestBlockedAt(level, target.pos);
        } else if (target.state.is(com.github.smallinger.coppergolemlegacy.ModTags.Blocks.GOLEM_TARGET_BARRELS)) {
            // Barrel is accessible if at least one horizontal side is free
            // Check if the front face (FACING property) is accessible
            if (target.state.hasProperty(net.minecraft.world.level.block.BarrelBlock.FACING)) {
                Direction facing = target.state.getValue(net.minecraft.world.level.block.BarrelBlock.FACING);
                // Only check horizontal directions (barrels can face up/down but we treat them as accessible)
                if (facing.getAxis().isHorizontal()) {
                    BlockPos frontPos = target.pos.relative(facing);
                    BlockState frontState = level.getBlockState(frontPos);
                    // If front is not a full block, barrel is accessible
                    return frontState.isCollisionShapeFullBlock(level, frontPos);
                }
            }
            // If facing is vertical or property missing, assume accessible
            return false;
        }
        // Other containers (copper chests, etc.) don't have blocked checks
        return false;
    }

    private boolean targetHasNotChanged(Level level, TransportItemTarget target) {
        return target.blockEntity.equals(level.getBlockEntity(target.pos));
    }

    private Stream<TransportItemTarget> getConnectedTargets(TransportItemTarget target, Level level) {
        // Check if this is a double chest using getValue instead of getValueOrElse
        if (target.state.hasProperty(ChestBlock.TYPE) 
            && target.state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            // Manual implementation of getConnectedBlockPos for 1.21.1
            Direction facing = target.state.getValue(ChestBlock.FACING);
            ChestType chestType = target.state.getValue(ChestBlock.TYPE);
            Direction connectedDir = chestType == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
            BlockPos connectedPos = target.pos.relative(connectedDir);
            
            TransportItemTarget connectedTarget = TransportItemTarget.tryCreatePossibleTarget(
                level.getBlockEntity(connectedPos), level
            );
            return connectedTarget != null 
                ? Stream.of(target, connectedTarget) 
                : Stream.of(target);
        } else {
            return Stream.of(target);
        }
    }

    private AABB getTargetSearchArea(PathfinderMob mob) {
        int horizontal = this.getHorizontalSearchDistance(mob);
        return new AABB(mob.blockPosition()).inflate(horizontal, this.getVerticalSearchDistance(mob), horizontal);
    }

    private int getHorizontalSearchDistance(PathfinderMob mob) {
        return mob.isPassenger() ? PASSENGER_MOB_TARGET_SEARCH_DISTANCE : this.horizontalSearchDistance;
    }

    private int getVerticalSearchDistance(PathfinderMob mob) {
        return mob.isPassenger() ? PASSENGER_MOB_TARGET_SEARCH_DISTANCE : this.verticalSearchDistance;
    }

    private static Set<GlobalPos> getVisitedPositions(PathfinderMob mob) {
        return mob.getBrain().getMemory(ModMemoryTypes.VISITED_BLOCK_POSITIONS.get()).orElse(Set.of());
    }

    private static Set<GlobalPos> getUnreachablePositions(PathfinderMob mob) {
        return mob.getBrain().getMemory(ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get()).orElse(Set.of());
    }

    private boolean isPositionAlreadyVisited(
        Set<GlobalPos> visited, Set<GlobalPos> unreachable, TransportItemTarget target, Level level
    ) {
        return this.getConnectedTargets(target, level)
            .map(t -> GlobalPos.of(level.dimension(), t.pos))
            .anyMatch(globalPos -> visited.contains(globalPos) || unreachable.contains(globalPos));
    }

    private static boolean hasFinishedPath(PathfinderMob mob) {
        return mob.getNavigation().getPath() != null && mob.getNavigation().getPath().isDone();
    }

    protected void setVisitedBlockPos(PathfinderMob mob, Level level, BlockPos pos) {
        Set<GlobalPos> visited = new HashSet<>(getVisitedPositions(mob));
        visited.add(GlobalPos.of(level.dimension(), pos));
        if (visited.size() > MAX_VISITED_POSITIONS) {
            this.enterCooldownAfterNoMatchingTargetFound(mob);
        } else {
            mob.getBrain().setMemoryWithExpiry(ModMemoryTypes.VISITED_BLOCK_POSITIONS.get(), visited, VISITED_POSITIONS_MEMORY_TIME);
        }
    }

    protected void markVisitedBlockPosAsUnreachable(PathfinderMob mob, Level level, BlockPos pos) {
        Set<GlobalPos> visited = new HashSet<>(getVisitedPositions(mob));
        visited.remove(GlobalPos.of(level.dimension(), pos));
        Set<GlobalPos> unreachable = new HashSet<>(getUnreachablePositions(mob));
        unreachable.add(GlobalPos.of(level.dimension(), pos));
        if (unreachable.size() > MAX_UNREACHABLE_POSITIONS) {
            this.enterCooldownAfterNoMatchingTargetFound(mob);
        } else {
            mob.getBrain().setMemoryWithExpiry(ModMemoryTypes.VISITED_BLOCK_POSITIONS.get(), visited, VISITED_POSITIONS_MEMORY_TIME);
            mob.getBrain().setMemoryWithExpiry(ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get(), unreachable, VISITED_POSITIONS_MEMORY_TIME);
        }
    }

    private boolean isWantedBlock(PathfinderMob mob, BlockState state) {
        boolean picking = isPickingUpItems(mob);
        boolean result = picking ? this.sourceBlockType.test(state) : this.destinationBlockType.test(state);
        return result;
    }

    private static double getInteractionRange(PathfinderMob mob) {
        return hasFinishedPath(mob) 
            ? CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_PATH_END_DISTANCE 
            : CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_DISTANCE;
    }

    private boolean isWithinTargetDistance(
        double distance, TransportItemTarget target, Level level, PathfinderMob mob, Vec3 center
    ) {
        AABB mobBounds = mob.getBoundingBox();
        double width = mobBounds.getXsize();
        double height = mob.getBbHeight();
        AABB centerBounds = new AABB(
            center.x - width / 2, center.y - height / 2, center.z - width / 2,
            center.x + width / 2, center.y + height / 2, center.z + width / 2
        );
        return target.state.getCollisionShape(level, target.pos)
            .bounds()
            .inflate(distance, distance, distance)
            .move(target.pos)
            .intersects(centerBounds);
    }

    private boolean targetIsReachableFromPosition(
        Level level, boolean withinDistance, Vec3 targetPos, TransportItemTarget target, PathfinderMob mob
    ) {
        return withinDistance && this.canSeeAnyTargetSide(target, level, mob, targetPos);
    }

    private boolean canSeeAnyTargetSide(TransportItemTarget target, Level level, PathfinderMob mob, Vec3 pos) {
        Vec3 targetCenter = target.pos.getCenter();
        return Arrays.stream(Direction.values())
            .map(dir -> targetCenter.add(0.5 * dir.getStepX(), 0.5 * dir.getStepY(), 0.5 * dir.getStepZ()))
            .map(sidePos -> level.clip(new ClipContext(pos, sidePos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)))
            .anyMatch(hitResult -> hitResult.getType() == HitResult.Type.BLOCK && hitResult.getBlockPos().equals(target.pos));
    }

    private boolean isAnotherMobInteractingWithTarget(TransportItemTarget target, Level level) {
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
        ItemStack handItem = mob.getMainHandItem();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack containerItem = container.getItem(i);
            if (ItemStack.isSameItem(containerItem, handItem)) {
                return true;
            }
        }
        return false;
    }

    private void pickUpItems(PathfinderMob mob, Container container) {
        ItemStack pickedUp = pickupItemFromContainer(container);
        mob.setItemSlot(EquipmentSlot.MAINHAND, pickedUp);
        mob.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        container.setChanged();
        this.clearMemoriesAfterMatchingTargetFound(mob);
    }

    private void putDownItem(PathfinderMob mob, Container container) {
        ItemStack remainingItems = addItemsToContainer(mob, container);
        container.setChanged();
        mob.setItemSlot(EquipmentSlot.MAINHAND, remainingItems);
        if (remainingItems.isEmpty()) {
            this.clearMemoriesAfterMatchingTargetFound(mob);
        } else {
            this.stopTargetingCurrentTarget(mob);
        }
    }

    private static ItemStack pickupItemFromContainer(Container container) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack itemStack = container.getItem(slot);
            if (!itemStack.isEmpty()) {
                int count = Math.min(itemStack.getCount(), TRANSPORTED_ITEM_MAX_STACK_SIZE);
                return container.removeItem(slot, count);
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack addItemsToContainer(PathfinderMob mob, Container container) {
        ItemStack handItem = mob.getMainHandItem();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack containerItem = container.getItem(slot);
            if (containerItem.isEmpty()) {
                container.setItem(slot, handItem);
                return ItemStack.EMPTY;
            }

            if (ItemStack.isSameItemSameTags(containerItem, handItem) 
                && containerItem.getCount() < containerItem.getMaxStackSize()) {
                int spaceLeft = containerItem.getMaxStackSize() - containerItem.getCount();
                int toAdd = Math.min(spaceLeft, handItem.getCount());
                containerItem.setCount(containerItem.getCount() + toAdd);
                handItem.setCount(handItem.getCount() - toAdd);
                container.setItem(slot, containerItem);
                if (handItem.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return handItem;
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
        mob.getBrain().setMemory(ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(), IDLE_COOLDOWN);
        mob.getBrain().eraseMemory(ModMemoryTypes.VISITED_BLOCK_POSITIONS.get());
        mob.getBrain().eraseMemory(ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get());
    }

    @Override
    protected void stop(ServerLevel level, PathfinderMob mob, long gameTime) {
        this.onStartTravelling(mob);
        // Note: setCanPathToTargetsBelowSurface() doesn't exist in 1.21.1
    }

    private void stopInPlace(PathfinderMob mob) {
        mob.getNavigation().stop();
        mob.setXxa(0.0F);
        mob.setYya(0.0F);
        mob.setSpeed(0.0F);
        mob.setDeltaMovement(0.0, mob.getDeltaMovement().y, 0.0);
    }

    // Enums und Records
    
    public enum ContainerInteractionState {
        PICKUP_ITEM,
        PICKUP_NO_ITEM,
        PLACE_ITEM,
        PLACE_NO_ITEM
    }

    public interface OnTargetReachedInteraction {
        void accept(PathfinderMob mob, TransportItemTarget target, Integer ticks);
    }

    public enum TransportItemState {
        TRAVELLING,
        QUEUING,
        INTERACTING
    }

    public record TransportItemTarget(BlockPos pos, Container container, BlockEntity blockEntity, BlockState state) {
        @Nullable
        public static TransportItemTarget tryCreatePossibleTarget(BlockEntity blockEntity, Level level) {
            BlockPos blockpos = blockEntity.getBlockPos();
            BlockState blockstate = blockEntity.getBlockState();
            Container container = getBlockEntityContainer(blockEntity, blockstate, level, blockpos);
            return container != null ? new TransportItemTarget(blockpos, container, blockEntity, blockstate) : null;
        }

        @Nullable
        public static TransportItemTarget tryCreatePossibleTarget(BlockPos pos, Level level) {
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



