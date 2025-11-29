package com.github.smallinger.copperagebackport.entity.ai;

import com.github.smallinger.copperagebackport.ModMemoryTypes;
import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.ModTags;
import com.github.smallinger.copperagebackport.compat.ModCompat;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.entity.CopperGolemState;
import com.github.smallinger.copperagebackport.entity.ai.behavior.InteractWithDoor;
import com.github.smallinger.copperagebackport.entity.ai.behavior.PressRandomCopperButton;
import com.github.smallinger.copperagebackport.entity.ai.behavior.TransportItemsBetweenContainers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * AI Brain System für Copper Golem
 * Basierend auf der Original-Implementation aus Minecraft 1.21.10
 */
public class CopperGolemAi {
    
    // Sensor Types für Brain-System
    private static final ImmutableList<SensorType<? extends Sensor<? super CopperGolemEntity>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES,
        SensorType.HURT_BY
    );
    
    // Memory Module Types für Brain-System
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.PATH,
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.HURT_BY,
        MemoryModuleType.HURT_BY_ENTITY,
        MemoryModuleType.IS_PANICKING,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.NEAREST_LIVING_ENTITIES,
        MemoryModuleType.DOORS_TO_CLOSE,  // Für Tür-Interaktionen
        ModMemoryTypes.GAZE_COOLDOWN_TICKS.get(),
        ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(),
        ModMemoryTypes.VISITED_BLOCK_POSITIONS.get(),
        ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get(),
        ModMemoryTypes.IS_PRESSING_BUTTON.get(),
        ModMemoryTypes.LAST_CONTAINER_EMPTY.get()
        // MemoryModuleType.DOORS_TO_CLOSE - requires InteractWithDoor from 1.21.10+
    );
    
    /**
     * Erstellt Brain.Provider für Copper Golem
     */
    public static Brain.Provider<CopperGolemEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }
    
    /**
     * Erstellt und konfiguriert Brain für Copper Golem
     */
    public static Brain<CopperGolemEntity> makeBrain(Brain<CopperGolemEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }
    
    /**
     * Core Activity - Grundlegende Behaviors die immer aktiv sind
     * WICHTIG: Kein Swim Behavior! Copper Golems ertrinken im Wasser (wie im Original)
     */
    private static void initCoreActivity(Brain<CopperGolemEntity> brain) {
        brain.addActivity(
            Activity.CORE,
            0,
            ImmutableList.<BehaviorControl<? super CopperGolemEntity>>of(
                new AnimalPanic(1.5F),  // Panik-Verhalten wenn beschädigt (nicht generisch in 1.20.1)
                new LookAtTargetSink(45, 90),  // Schaut zum Look-Target
                new MoveToTargetSink(),  // Bewegt sich zum Walk-Target
                InteractWithDoor.create(),  // Türen öffnen und schließen
                new CountDownCooldownTicks(ModMemoryTypes.GAZE_COOLDOWN_TICKS.get()),  // Gaze Cooldown
                new CountDownCooldownTicks(ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get())  // Transport Cooldown
            )
        );
    }
    
    /**
     * Idle Activity - Behaviors wenn Golem nichts Spezielles tut
     * Priority 0: Item Transport (höchste Priorität)
     * Priority 1: Press Random Copper Button (wenn Config aktiviert, darf nach Start nicht unterbrochen werden)
     * Priority 2: Schaue manchmal Spieler an
     * Priority 3: Herumlaufen oder Stillstehen (wenn Cooldown aktiv)
     */
    private static void initIdleActivity(Brain<CopperGolemEntity> brain) {
        ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super CopperGolemEntity>>> behaviorsBuilder = ImmutableList.builder();
        
        // Prio 0: Item Transport zwischen Copper Chests und Regular Chests/Barrels
        behaviorsBuilder.add(Pair.of(0, new TransportItemsBetweenContainers(
            1.0F,  // Speed Modifier
            state -> state.is(ModTags.Blocks.COPPER_CHESTS),  // Source: Copper Chests
            state -> isValidDestinationContainer(state),  // Destination: Vanilla Chests and Barrels
            32,  // Horizontal Search Distance
            8,   // Vertical Search Distance
            getTargetReachedInteractions(),  // Interaction callbacks
            onTravelling(),  // On start travelling callback
            shouldQueueForTarget()  // Should queue predicate
        )));
        
        // Prio 1: Press Random Copper Button (enabled by config)
        if (CommonConfig.golemPressesButtons()) {
            behaviorsBuilder.add(Pair.of(1, new PressRandomCopperButton(
                1.0F,  // Speed Modifier (normale Geschwindigkeit)
                16,    // Horizontal Search Distance (16 Blöcke)
                4,     // Vertical Search Distance (4 Blöcke)
                150    // Base Press Interval (150 ticks = 7.5 seconds)
            )));
        }
        
        // Prio 2: Schaue manchmal Spieler an (6 Blöcke Reichweite, 40-80 Ticks Interval)
        behaviorsBuilder.add(Pair.of(2, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(40, 80))));
        
        // Prio 3: Herumlaufen oder Stillstehen
        // Nur wenn kein Walk-Target gesetzt ist UND Transport-Cooldown aktiv ist
        behaviorsBuilder.add(Pair.of(3, new RunOne<>(
            ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(), MemoryStatus.VALUE_PRESENT
            ),
            ImmutableList.of(
                // 50% Chance: Zufällig herumlaufen (1.0 Speed, max 2 Blöcke horizontal, 2 Blöcke vertikal)
                Pair.of(RandomStroll.stroll(1.0F, 2, 2), 1),
                // 50% Chance: Stillstehen für 30-60 Ticks
                Pair.of(new DoNothing(30, 60), 1)
            )
        )));
        
        brain.addActivity(Activity.IDLE, behaviorsBuilder.build());
    }
    
    /**
     * Erstellt die Map mit Interaktions-Callbacks für Container-Interaktionen
     */
    private static Map<TransportItemsBetweenContainers.ContainerInteractionState, TransportItemsBetweenContainers.OnTargetReachedInteraction> getTargetReachedInteractions() {
        return Map.of(
            TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_ITEM,
            onReachedTargetInteraction(CopperGolemState.GETTING_ITEM, ModSounds.COPPER_GOLEM_ITEM_GET.get()),
            TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_NO_ITEM,
            onReachedTargetInteraction(CopperGolemState.GETTING_NO_ITEM, ModSounds.COPPER_GOLEM_ITEM_NO_GET.get()),
            TransportItemsBetweenContainers.ContainerInteractionState.PLACE_ITEM,
            onReachedTargetInteraction(CopperGolemState.DROPPING_ITEM, ModSounds.COPPER_GOLEM_ITEM_DROP.get()),
            TransportItemsBetweenContainers.ContainerInteractionState.PLACE_NO_ITEM,
            onReachedTargetInteraction(CopperGolemState.DROPPING_NO_ITEM, ModSounds.COPPER_GOLEM_ITEM_NO_DROP.get())
        );
    }
    
    /**
     * Callback wenn Golem ein Target erreicht hat
     * Setzt Animation State und öffnet/schließt Container
     */
    private static TransportItemsBetweenContainers.OnTargetReachedInteraction onReachedTargetInteraction(
        CopperGolemState state, @Nullable net.minecraft.sounds.SoundEvent sound
    ) {
        return (mob, target, tick) -> {
            if (mob instanceof CopperGolemEntity copperGolem) {
                if (tick == 1) {
                    // Container öffnen mit Sound
                    playChestSound(copperGolem, target.pos(), true);
                    copperGolem.setOpenedChestPos(target.pos());
                    copperGolem.setState(state);
                }
                
                // Tick 9: Item-Interaction Sound abspielen
                if (tick == 9 && sound != null) {
                    copperGolem.playSound(sound, 1.0F, 1.0F);
                }
                
                if (tick == 60) {
                    // Container schließen mit Sound
                    playChestSound(copperGolem, target.pos(), false);
                    copperGolem.clearOpenedChestPos();
                }
            }
        };
    }
    
    /**
     * Spielt den Chest Open/Close Sound ab und triggert die Animation
     * Unterstützt: Copper Chests, Barrels, Regular Chests, und mod-kompatible Container
     */
    private static void playChestSound(CopperGolemEntity golem, BlockPos pos, boolean open) {
        Level level = golem.level();
        BlockState blockState = level.getBlockState(pos);
        
        // Try ModCompat first (SophisticatedStorage, etc.)
        if (ModCompat.handleChestOpen(level, pos, blockState, open)) {
            // ModCompat handled the sound and animation
            level.gameEvent(golem, 
                open ? net.minecraft.world.level.gameevent.GameEvent.CONTAINER_OPEN : net.minecraft.world.level.gameevent.GameEvent.CONTAINER_CLOSE, 
                pos);
            return;
        }
        
        // Determine sound based on container type (vanilla handling)
        net.minecraft.sounds.SoundEvent soundEvent;
        if (blockState.is(ModTags.Blocks.COPPER_CHESTS)) {
            // Copper Chest Sound
            soundEvent = open ? ModSounds.COPPER_CHEST_OPEN.get() : ModSounds.COPPER_CHEST_CLOSE.get();
        } else if (blockState.is(Blocks.BARREL)) {
            // Barrel Sound
            soundEvent = open ? SoundEvents.BARREL_OPEN : SoundEvents.BARREL_CLOSE;
        } else {
            // Regular Chest Sound (default)
            soundEvent = open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE;
        }
        
        level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        
        // Trigger container animation
        net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            if (blockState.is(Blocks.BARREL)) {
                // Barrels use BlockState property for animation
                level.setBlock(pos, blockState.setValue(net.minecraft.world.level.block.BarrelBlock.OPEN, open), 3);
            } else {
                // Try blockEvent for chest-like containers (vanilla chests)
                level.blockEvent(pos, blockState.getBlock(), 1, open ? 1 : 0);
            }
        }
        
        // GameEvent for other systems
        level.gameEvent(golem, 
            open ? net.minecraft.world.level.gameevent.GameEvent.CONTAINER_OPEN : net.minecraft.world.level.gameevent.GameEvent.CONTAINER_CLOSE, 
            pos);
    }
    
    /**
     * Callback wenn Golem zu einem Target läuft
     * Setzt State zurück auf IDLE
     */
    private static Consumer<PathfinderMob> onTravelling() {
        return mob -> {
            if (mob instanceof CopperGolemEntity copperGolem) {
                copperGolem.clearOpenedChestPos();
                copperGolem.setState(CopperGolemState.IDLE);
            }
        };
    }
    
    /**
     * Prüft ob ein anderer Mob bereits mit dem Target interagiert
     * Wenn ja, sollte der Golem in eine Warteschlange gehen
     */
    private static Predicate<TransportItemsBetweenContainers.TransportItemTarget> shouldQueueForTarget() {
        return target -> {
            // Queue wenn ein anderer Golem bereits mit der Kiste interagiert
            // Einfache Implementation: kein Queueing da getEntitiesWithContainerOpen() nicht verfügbar in 1.21.1
            return false;
        };
    }
    
    /**
     * Prüft ob ein BlockState ein gültiges Ziel für Item-Transport ist.
     * Unterstützt Vanilla Chests, Barrels und mod-kompatible Container.
     */
    private static boolean isValidDestinationContainer(BlockState state) {
        // Vanilla containers
        if (state.is(Blocks.CHEST) || state.is(Blocks.TRAPPED_CHEST) || state.is(Blocks.BARREL)) {
            return true;
        }
        // ModCompat containers (SophisticatedStorage, etc.)
        return ModCompat.isValidModContainer(state);
    }
    
    /**
     * Update Activity - Wird jeden Tick aufgerufen
     */
    public static void updateActivity(CopperGolemEntity golem) {
        golem.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }
}

