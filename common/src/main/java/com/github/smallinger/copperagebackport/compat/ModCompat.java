package com.github.smallinger.copperagebackport.compat;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.Predicate;

/**
 * Central mod compatibility manager.
 * Handles registration and initialization of mod-specific compatibility modules.
 * 
 * Usage:
 * 1. Register compat modules during mod initialization
 * 2. Call init() after all modules are registered
 * 3. Use helper methods for cross-mod functionality
 */
public class ModCompat {
    
    private static final Map<String, IModCompatModule> MODULES = new HashMap<>();
    private static final List<IChestHandler> CHEST_HANDLERS = new ArrayList<>();
    private static final List<IContainerCompat> CONTAINER_HANDLERS = new ArrayList<>();
    private static boolean initialized = false;
    
    /**
     * Register a compatibility module.
     * Should be called during mod construction, before init().
     * 
     * @param modId The mod ID this module provides compatibility for
     * @param moduleSupplier Supplier that creates the module (lazy loading)
     */
    public static void register(String modId, Supplier<IModCompatModule> moduleSupplier) {
        if (initialized) {
            Constants.LOG.warn("ModCompat: Tried to register module for '{}' after initialization!", modId);
            return;
        }
        
        if (MODULES.containsKey(modId)) {
            Constants.LOG.warn("ModCompat: Module for '{}' already registered, skipping duplicate", modId);
            return;
        }
        
        // Only create and register if the mod is loaded
        if (Services.PLATFORM.isModLoaded(modId)) {
            try {
                IModCompatModule module = moduleSupplier.get();
                MODULES.put(modId, module);
                Constants.LOG.info("ModCompat: Registered compatibility module for '{}'", modId);
            } catch (Exception e) {
                Constants.LOG.error("ModCompat: Failed to create module for '{}': {}", modId, e.getMessage());
                e.printStackTrace();
            }
        } else {
            Constants.LOG.info("ModCompat: Mod '{}' not loaded, skipping compatibility module", modId);
        }
    }
    
    /**
     * Initialize all registered compatibility modules.
     * Should be called once during mod setup, after all modules are registered.
     */
    public static void init() {
        if (initialized) {
            Constants.LOG.warn("ModCompat: Already initialized!");
            return;
        }
        
        Constants.LOG.info("ModCompat: Initializing {} compatibility modules...", MODULES.size());
        
        for (Map.Entry<String, IModCompatModule> entry : MODULES.entrySet()) {
            try {
                entry.getValue().init();
                
                // Collect chest handlers
                if (entry.getValue() instanceof IChestHandler handler) {
                    CHEST_HANDLERS.add(handler);
                }
                
                // Collect container handlers (superset of chest handlers for container detection)
                if (entry.getValue() instanceof IContainerCompat containerCompat) {
                    CONTAINER_HANDLERS.add(containerCompat);
                }
                
                Constants.LOG.info("ModCompat: Initialized compatibility for '{}'", entry.getKey());
            } catch (Exception e) {
                Constants.LOG.error("ModCompat: Failed to initialize module for '{}': {}", entry.getKey(), e.getMessage());
            }
        }
        
        initialized = true;
        Constants.LOG.info("ModCompat: Initialization complete. {} chest handlers, {} container handlers registered.", 
            CHEST_HANDLERS.size(), CONTAINER_HANDLERS.size());
    }
    
    /**
     * Check if a specific mod's compatibility module is loaded and active.
     */
    public static boolean isModCompatLoaded(String modId) {
        return MODULES.containsKey(modId);
    }
    
    /**
     * Get a specific compatibility module.
     * 
     * @param modId The mod ID
     * @param type The expected module type
     * @return The module, or null if not found or wrong type
     */
    @SuppressWarnings("unchecked")
    public static <T extends IModCompatModule> T getModule(String modId, Class<T> type) {
        IModCompatModule module = MODULES.get(modId);
        if (module != null && type.isInstance(module)) {
            return (T) module;
        }
        return null;
    }
    
    // ==================== Chest Handling ====================
    
    /**
     * Try to open/close a chest using registered handlers.
     * Falls back to vanilla behavior if no handler matches.
     * 
     * @param level The world
     * @param pos The block position
     * @param state The block state
     * @param open True to open, false to close
     * @return True if a handler processed this chest, false to use default behavior
     */
    public static boolean handleChestOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        for (IChestHandler handler : CHEST_HANDLERS) {
            if (handler.canHandle(state)) {
                handler.setOpen(level, pos, state, open);
                return true;
            }
        }
        return false; // Use default vanilla handling
    }
    
    /**
     * Check if any registered handler can handle this chest type.
     */
    public static boolean hasChestHandler(BlockState state) {
        for (IChestHandler handler : CHEST_HANDLERS) {
            if (handler.canHandle(state)) {
                return true;
            }
        }
        return false;
    }
    
    // ==================== Container Validation ====================
    
    /**
     * Check if the given block state is a valid destination container from any registered mod.
     * Used by Copper Golem AI to determine valid item deposit targets.
     * 
     * @param state The block state to check
     * @return True if any mod compat module accepts this as a valid container
     */
    public static boolean isValidModContainer(BlockState state) {
        if (CONTAINER_HANDLERS.isEmpty()) {
            return false;
        }
        
        for (IContainerCompat containerCompat : CONTAINER_HANDLERS) {
            if (containerCompat.isValidContainer(state)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get a Container wrapper for the given block entity from registered mod compat modules.
     * Used to access mod containers that don't use vanilla Container interface.
     * 
     * @param blockEntity The block entity
     * @param level The level
     * @param pos The position
     * @return A Container wrapper, or null if no handler can provide one
     */
    @Nullable
    public static Container getModContainer(BlockEntity blockEntity, Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for (IContainerCompat containerCompat : CONTAINER_HANDLERS) {
            if (containerCompat.isValidContainer(state)) {
                Container container = containerCompat.getContainer(blockEntity, level, pos);
                if (container != null) {
                    return container;
                }
            }
        }
        return null;
    }
    
    /**
     * Get a predicate that checks if a block state is a valid mod container.
     * Useful for combining with vanilla container checks.
     * 
     * @return A predicate for mod container validation
     */
    public static Predicate<BlockState> getModContainerPredicate() {
        return ModCompat::isValidModContainer;
    }
}
