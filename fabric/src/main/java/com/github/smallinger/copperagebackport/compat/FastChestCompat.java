package com.github.smallinger.copperagebackport.compat;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;

/**
 * Compatibility helper for FastChest mod.
 * Uses reflection to avoid hard dependency.
 */
public class FastChestCompat {
    
    private static final boolean FASTCHEST_LOADED;
    private static Field simplifiedChestField;
    
    static {
        FASTCHEST_LOADED = FabricLoader.getInstance().isModLoaded("fastchest");
        
        if (FASTCHEST_LOADED) {
            try {
                Class<?> configClass = Class.forName("re.domi.fastchest.config.Config");
                simplifiedChestField = configClass.getField("simplifiedChest");
            } catch (ClassNotFoundException | NoSuchFieldException e) {
                // FastChest config not found or changed
                simplifiedChestField = null;
            }
        }
    }
    
    /**
     * Check if FastChest is loaded.
     */
    public static boolean isFastChestLoaded() {
        return FASTCHEST_LOADED;
    }
    
    /**
     * Check if FastChest's simplified chest mode is enabled.
     * Returns false if FastChest is not loaded or if the config cannot be read.
     */
    public static boolean isSimplifiedChestEnabled() {
        if (!FASTCHEST_LOADED || simplifiedChestField == null) {
            return false;
        }
        
        try {
            return simplifiedChestField.getBoolean(null);
        } catch (IllegalAccessException e) {
            return false;
        }
    }
}
