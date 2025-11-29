package com.github.smallinger.copperagebackport.neoforge.compat;

import net.neoforged.fml.ModList;

import java.lang.reflect.Field;

/**
 * Compatibility helper for FastChest mod on NeoForge.
 * Uses reflection to avoid hard dependency.
 */
public class FastChestNeoForgeCompat {
    
    private static final boolean FASTCHEST_LOADED;
    private static Field simplifiedChestField;
    
    static {
        FASTCHEST_LOADED = ModList.get().isLoaded("fastchest");
        
        if (FASTCHEST_LOADED) {
            // Try different possible config class locations
            String[] configClasses = {
                "re.domi.fastchest.config.Config",      // Original FastChest
                "com.globalista.fastchest.Config"        // FastChest-Reforged
            };
            
            for (String className : configClasses) {
                try {
                    Class<?> configClass = Class.forName(className);
                    simplifiedChestField = configClass.getField("simplifiedChest");
                    break;
                } catch (ClassNotFoundException | NoSuchFieldException e) {
                    // Try next
                }
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
