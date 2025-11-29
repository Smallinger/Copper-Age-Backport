package com.github.smallinger.copperagebackport.compat;

/**
 * Base interface for mod compatibility modules.
 * Implement this interface to add compatibility with other mods.
 * 
 * Modules should be registered in ModCompat.register() during mod initialization.
 */
public interface IModCompatModule {
    
    /**
     * Get the mod ID this module provides compatibility for.
     */
    String getModId();
    
    /**
     * Initialize the compatibility module.
     * Called once after the mod is confirmed to be loaded.
     * 
     * Use this to:
     * - Register additional blocks to tags
     * - Set up event handlers
     * - Initialize any mod-specific APIs
     */
    void init();
    
    /**
     * Called on client-side only for client-specific initialization.
     * Default implementation does nothing.
     */
    default void initClient() {
        // Override if client-side initialization is needed
    }
}
