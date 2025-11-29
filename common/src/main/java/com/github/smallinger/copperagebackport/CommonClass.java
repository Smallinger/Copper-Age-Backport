package com.github.smallinger.copperagebackport;

import com.github.smallinger.copperagebackport.compat.ModCompat;
import com.github.smallinger.copperagebackport.compat.modules.FastChestCompat;
import com.github.smallinger.copperagebackport.compat.modules.IronChestsCompat;
import com.github.smallinger.copperagebackport.compat.modules.SophisticatedStorageCompat;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial;
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
        
        // Register sounds first (armor material needs the equip sound)
        ModSounds.register();
        
        // Register armor material (needs to be in registry before items)
        CopperArmorMaterial.init();
        
        // Register all content
        ModMemoryTypes.register();
        ModParticles.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModItems.register();
        
        // Register mod compatibility modules
        registerCompatModules();
        
        // Initialize mod compatibility
        ModCompat.init();
        
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
    
    /**
     * Register all mod compatibility modules.
     * Add new compat modules here.
     */
    private static void registerCompatModules() {
        // FastChest - static chest rendering for better performance
        ModCompat.register(FastChestCompat.MOD_ID, FastChestCompat::new);
        
        // SophisticatedStorage - enhanced storage blocks as Copper Golem destinations
        ModCompat.register(SophisticatedStorageCompat.MOD_ID, SophisticatedStorageCompat::new);
        
        // IronChests - metal chest variants as Copper Golem destinations
        ModCompat.register(IronChestsCompat.MOD_ID, IronChestsCompat::new);
    }
}
