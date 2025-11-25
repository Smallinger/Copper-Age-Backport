package com.github.smallinger.copperagebackport;

import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.platform.Services;
import com.github.smallinger.copperagebackport.registry.*;

/**
 * Main initialization class for Copper-Age-Backport.
 * This class is shared between all loaders and contains the common initialization logic.
 */
public class CommonClass {

    /**
     * Called during mod initialization.
     * Register all game content here.
     */
    public static void init() {
        Constants.LOG.info("Initializing {} on {}", Constants.MOD_NAME, Services.PLATFORM.getPlatformName());
        
        // Register all content
        ModSounds.register();
        ModMemoryTypes.register();
        ModParticles.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModItems.register();
        
        Constants.LOG.info("{} initialized successfully!", Constants.MOD_NAME);
    }
    
    /**
     * Called to register entity attributes.
     * Must be called from loader-specific code.
     */
    public static void registerEntityAttributes() {
        // This will be called from platform-specific code
        Constants.LOG.info("Registering entity attributes for {}", Constants.MOD_NAME);
    }
    
    /**
     * Get the attributes for the Copper Golem entity.
     */
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder getCopperGolemAttributes() {
        return CopperGolemEntity.createAttributes();
    }
}
