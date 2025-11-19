package com.github.smallinger.coppergolemlegacy.entity.ai.behavior;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Behavior fÃ¼r TÃ¼r-Interaktionen - Portiert von Minecraft 1.21.10
 * Ã–ffnet TÃ¼ren automatisch wenn der Mob durch sie hindurch geht
 * und schlieÃŸt sie wieder nach dem Durchgang.
 */
public class InteractWithDoor {
    private static final int COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE = 20;
    private static final double SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN = 3.0;
    private static final double MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS = 2.0;

    public static BehaviorControl<LivingEntity> create() {
        MutableObject<Node> lastPathNode = new MutableObject<>(null);
        MutableInt cooldown = new MutableInt(0);
        
        return BehaviorBuilder.create(
            instance -> instance.group(
                    instance.present(MemoryModuleType.PATH),
                    instance.registered(MemoryModuleType.DOORS_TO_CLOSE),
                    instance.registered(MemoryModuleType.NEAREST_LIVING_ENTITIES)
                )
                .apply(
                    instance,
                    (pathMemory, doorsToCloseMemory, nearestEntitiesMemory) -> (level, entity, gameTime) -> {
                        Path path = instance.get(pathMemory);
                        Optional<Set<GlobalPos>> doorsToClose = instance.tryGet(doorsToCloseMemory);
                        
                        if (!path.notStarted() && !path.isDone()) {
                            // Cooldown um nicht stÃ¤ndig dieselbe TÃ¼r zu checken
                            if (Objects.equals(lastPathNode.getValue(), path.getNextNode())) {
                                cooldown.setValue(COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE);
                            } else if (cooldown.decrementAndGet() > 0) {
                                return false;
                            }

                            lastPathNode.setValue(path.getNextNode());
                            Node previousNode = path.getPreviousNode();
                            Node nextNode = path.getNextNode();
                            
                            // PrÃ¼fe vorherigen Node auf TÃ¼r
                            BlockPos previousPos = previousNode.asBlockPos();
                            BlockState previousState = level.getBlockState(previousPos);
                            if (previousState.is(BlockTags.WOODEN_DOORS) && previousState.getBlock() instanceof DoorBlock) {
                                DoorBlock door = (DoorBlock) previousState.getBlock();
                                if (!door.isOpen(previousState)) {
                                    door.setOpen(entity, level, previousState, previousPos, true);
                                }
                                doorsToClose = rememberDoorToClose(doorsToCloseMemory, doorsToClose, level, previousPos);
                            }

                            // PrÃ¼fe nÃ¤chsten Node auf TÃ¼r
                            BlockPos nextPos = nextNode.asBlockPos();
                            BlockState nextState = level.getBlockState(nextPos);
                            if (nextState.is(BlockTags.WOODEN_DOORS) && nextState.getBlock() instanceof DoorBlock) {
                                DoorBlock door = (DoorBlock) nextState.getBlock();
                                if (!door.isOpen(nextState)) {
                                    door.setOpen(entity, level, nextState, nextPos, true);
                                    doorsToClose = rememberDoorToClose(doorsToCloseMemory, doorsToClose, level, nextPos);
                                }
                            }

                            // SchlieÃŸe TÃ¼ren die bereits durchquert wurden
                            doorsToClose.ifPresent(
                                doors -> closeDoorsThatIHaveOpenedOrPassedThrough(
                                    level, entity, previousNode, nextNode, doors, instance.tryGet(nearestEntitiesMemory)
                                )
                            );
                            return true;
                        }
                        return false;
                    }
                )
        );
    }

    public static void closeDoorsThatIHaveOpenedOrPassedThrough(
        ServerLevel level,
        LivingEntity entity,
        @Nullable Node previous,
        @Nullable Node next,
        Set<GlobalPos> doorPositions,
        Optional<List<LivingEntity>> nearestLivingEntities
    ) {
        Iterator<GlobalPos> iterator = doorPositions.iterator();

        while (iterator.hasNext()) {
            GlobalPos doorPos = iterator.next();
            BlockPos blockPos = doorPos.pos();
            
            // Nicht schlieÃŸen wenn wir gerade an dieser TÃ¼r sind
            if ((previous == null || !previous.asBlockPos().equals(blockPos)) 
                && (next == null || !next.asBlockPos().equals(blockPos))) {
                
                if (isDoorTooFarAway(level, entity, doorPos)) {
                    iterator.remove();
                } else {
                    BlockState blockState = level.getBlockState(blockPos);
                    if (!blockState.is(BlockTags.WOODEN_DOORS) || !(blockState.getBlock() instanceof DoorBlock)) {
                        iterator.remove();
                    } else {
                        DoorBlock door = (DoorBlock) blockState.getBlock();
                        if (!door.isOpen(blockState)) {
                            iterator.remove();
                        } else if (areOtherMobsComingThroughDoor(entity, blockPos, nearestLivingEntities)) {
                            // TÃ¼r offen lassen wenn andere Mobs durchkommen
                            iterator.remove();
                        } else {
                            // TÃ¼r schlieÃŸen
                            door.setOpen(entity, level, blockState, blockPos, false);
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    private static boolean areOtherMobsComingThroughDoor(
        LivingEntity entity, 
        BlockPos doorPos, 
        Optional<List<LivingEntity>> nearestLivingEntities
    ) {
        if (nearestLivingEntities.isEmpty()) {
            return false;
        }
        
        return nearestLivingEntities.get()
            .stream()
            .filter(mob -> mob.getType() == entity.getType())
            .filter(mob -> doorPos.closerToCenterThan(mob.position(), MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS))
            .anyMatch(mob -> isMobComingThroughDoor(mob.getBrain(), doorPos));
    }

    private static boolean isMobComingThroughDoor(Brain<?> brain, BlockPos doorPos) {
        if (!brain.hasMemoryValue(MemoryModuleType.PATH)) {
            return false;
        }
        
        Path path = brain.getMemory(MemoryModuleType.PATH).get();
        if (path.isDone()) {
            return false;
        }
        
        Node previousNode = path.getPreviousNode();
        if (previousNode == null) {
            return false;
        }
        
        Node nextNode = path.getNextNode();
        return doorPos.equals(previousNode.asBlockPos()) || doorPos.equals(nextNode.asBlockPos());
    }

    private static boolean isDoorTooFarAway(ServerLevel level, LivingEntity entity, GlobalPos doorPos) {
        return doorPos.dimension() != level.dimension() 
            || !doorPos.pos().closerToCenterThan(entity.position(), SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN);
    }

    private static Optional<Set<GlobalPos>> rememberDoorToClose(
        net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor<?, Set<GlobalPos>> doorsToCloseMemory,
        Optional<Set<GlobalPos>> doorPositions,
        ServerLevel level,
        BlockPos doorPos
    ) {
        GlobalPos globalPos = GlobalPos.of(level.dimension(), doorPos);
        return Optional.of(
            doorPositions.map(doors -> {
                doors.add(globalPos);
                return doors;
            }).orElseGet(() -> {
                Set<GlobalPos> newDoors = Sets.newHashSet(globalPos);
                doorsToCloseMemory.set(newDoors);
                return newDoors;
            })
        );
    }
}

