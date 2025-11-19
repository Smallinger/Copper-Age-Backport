package com.github.smallinger.coppergolemlegacy;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.Set;

/**
 * Registriert custom Memory Module Types für den Copper Golem Brain AI
 * Diese Memory Types werden für Item Transport und andere Behaviors benötigt
 */
public class ModMemoryTypes {
    
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = 
        DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, CopperGolemLegacy.MODID);
    
    /**
     * Cooldown Timer für Item Transport zwischen Containern
     * Copper Golem wartet 60-100 Ticks zwischen Transport-Aktionen
     */
    public static final RegistryObject<MemoryModuleType<Integer>> TRANSPORT_ITEMS_COOLDOWN_TICKS = 
        MEMORY_MODULE_TYPES.register("transport_items_cooldown_ticks", 
            () -> new MemoryModuleType<>(Optional.of(Codec.INT)));
    
    /**
     * Set von Block-Positionen die der Copper Golem bereits besucht hat
     * Verhindert dass er immer wieder die gleichen Blöcke ansteuert
     */
    public static final RegistryObject<MemoryModuleType<Set<GlobalPos>>> VISITED_BLOCK_POSITIONS = 
        MEMORY_MODULE_TYPES.register("visited_block_positions",
            () -> new MemoryModuleType<>(Optional.of(
                GlobalPos.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList)
            )));
    
    /**
     * Set von Block-Positionen die der Copper Golem nicht erreichen kann
     * Verhindert dass er immer wieder versucht unerreichbare Blöcke anzusteuern
     */
    public static final RegistryObject<MemoryModuleType<Set<GlobalPos>>> UNREACHABLE_TRANSPORT_BLOCK_POSITIONS = 
        MEMORY_MODULE_TYPES.register("unreachable_transport_block_positions",
            () -> new MemoryModuleType<>(Optional.of(
                GlobalPos.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList)
            )));
    
    /**
     * Cooldown Timer für Gaze/Look Behavior
     * Copper Golem schaut für eine bestimmte Zeit in eine Richtung
     */
    public static final RegistryObject<MemoryModuleType<Integer>> GAZE_COOLDOWN_TICKS = 
        MEMORY_MODULE_TYPES.register("gaze_cooldown_ticks",
            () -> new MemoryModuleType<>(Optional.of(Codec.INT)));
    
    /**
     * Flag das anzeigt ob der Copper Golem gerade einen Button drückt
     * Verhindert dass andere Behaviors (wie Transport) das Button-Drücken unterbrechen
     */
    public static final RegistryObject<MemoryModuleType<Boolean>> IS_PRESSING_BUTTON = 
        MEMORY_MODULE_TYPES.register("is_pressing_button",
            () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));
}

