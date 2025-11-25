package com.github.smallinger.copperagebackport;

import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Registriert custom Memory Module Types für den Copper Golem Brain AI
 * Diese Memory Types werden für Item Transport und andere Behaviors benötigt
 */
public class ModMemoryTypes {
    
    /**
     * Cooldown Timer für Item Transport zwischen Containern
     * Copper Golem wartet 60-100 Ticks zwischen Transport-Aktionen
     */
    public static Supplier<MemoryModuleType<Integer>> TRANSPORT_ITEMS_COOLDOWN_TICKS;
    
    /**
     * Set von Block-Positionen die der Copper Golem bereits besucht hat
     * Verhindert dass er immer wieder die gleichen Blöcke ansteuert
     */
    public static Supplier<MemoryModuleType<Set<GlobalPos>>> VISITED_BLOCK_POSITIONS;
    
    /**
     * Set von Block-Positionen die der Copper Golem nicht erreichen kann
     * Verhindert dass er immer wieder versucht unerreichbare Blöcke anzusteuern
     */
    public static Supplier<MemoryModuleType<Set<GlobalPos>>> UNREACHABLE_TRANSPORT_BLOCK_POSITIONS;
    
    /**
     * Cooldown Timer für Gaze/Look Behavior
     * Copper Golem schaut für eine bestimmte Zeit in eine Richtung
     */
    public static Supplier<MemoryModuleType<Integer>> GAZE_COOLDOWN_TICKS;
    
    /**
     * Flag das anzeigt ob der Copper Golem gerade einen Button drückt
     * Verhindert dass andere Behaviors (wie Transport) das Button-Drücken unterbrechen
     */
    public static Supplier<MemoryModuleType<Boolean>> IS_PRESSING_BUTTON;
    
    /**
     * Timestamp wann die letzte Copper Chest leer war
     * Wird verwendet um nach Transport-Ende eine 20% Chance für Button-Press zu triggern
     */
    public static Supplier<MemoryModuleType<Long>> LAST_CONTAINER_EMPTY;
    
    public static void register() {
        Constants.LOG.info("Registering memory module types for {}", Constants.MOD_NAME);
        
        RegistryHelper helper = RegistryHelper.getInstance();
        
        TRANSPORT_ITEMS_COOLDOWN_TICKS = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "transport_items_cooldown_ticks",
            () -> new MemoryModuleType<>(Optional.of(Codec.INT))
        );
        
        VISITED_BLOCK_POSITIONS = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "visited_block_positions",
            () -> new MemoryModuleType<>(Optional.of(
                GlobalPos.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList)
            ))
        );
        
        UNREACHABLE_TRANSPORT_BLOCK_POSITIONS = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "unreachable_transport_block_positions",
            () -> new MemoryModuleType<>(Optional.of(
                GlobalPos.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList)
            ))
        );
        
        GAZE_COOLDOWN_TICKS = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "gaze_cooldown_ticks",
            () -> new MemoryModuleType<>(Optional.of(Codec.INT))
        );
        
        IS_PRESSING_BUTTON = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "is_pressing_button",
            () -> new MemoryModuleType<>(Optional.of(Codec.BOOL))
        );
        
        LAST_CONTAINER_EMPTY = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "last_container_empty",
            () -> new MemoryModuleType<>(Optional.of(Codec.LONG))
        );
    }
}

