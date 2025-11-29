package com.github.smallinger.copperagebackport.forge.compat;

import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Compatibility helper for FastChest-Reforged mod on Forge.
 * Uses reflection to avoid hard dependency.
 * 
 * Note: FastChest-Reforged uses ForgeConfigSpec, so we need to access the
 * ConfigValue directly via get() method, not the cached boolean field.
 */
public class FastChestForgeCompat {
    
    private static final boolean FASTCHEST_LOADED;
    private static Object simplifiedChestConfigValue; // ForgeConfigSpec.BooleanValue
    private static Method getMethod; // .get() method on ConfigValue
    
    static {
        FASTCHEST_LOADED = ModList.get().isLoaded("fastchest");
        
        if (FASTCHEST_LOADED) {
            // Try FastChest-Reforged config class - need to get the SIMPLIFIED_CHEST ConfigValue
            try {
                Class<?> configClass = Class.forName("com.globalista.fastchest.Config");
                // SIMPLIFIED_CHEST is private, so we need getDeclaredField
                Field configValueField = configClass.getDeclaredField("SIMPLIFIED_CHEST");
                configValueField.setAccessible(true);
                simplifiedChestConfigValue = configValueField.get(null);
                
                if (simplifiedChestConfigValue != null) {
                    // Get the get() method from ForgeConfigSpec.ConfigValue
                    getMethod = simplifiedChestConfigValue.getClass().getMethod("get");
                }
            } catch (Exception e) {
                // Fallback: try the cached boolean field (may not work if called too early)
                try {
                    Class<?> configClass = Class.forName("com.globalista.fastchest.Config");
                    Field boolField = configClass.getField("simplifiedChest");
                    // Store the field for fallback, but prefer ConfigValue
                    simplifiedChestConfigValue = null;
                    getMethod = null;
                } catch (Exception e2) {
                    simplifiedChestConfigValue = null;
                    getMethod = null;
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
        if (!FASTCHEST_LOADED) {
            return false;
        }
        
        // Try to get value from ForgeConfigSpec.ConfigValue via get() method
        if (simplifiedChestConfigValue != null && getMethod != null) {
            try {
                Object result = getMethod.invoke(simplifiedChestConfigValue);
                if (result instanceof Boolean) {
                    return (Boolean) result;
                }
            } catch (Exception e) {
                // Fall through to fallback
            }
        }
        
        // Fallback: try the cached boolean field directly
        try {
            Class<?> configClass = Class.forName("com.globalista.fastchest.Config");
            Field boolField = configClass.getField("simplifiedChest");
            return boolField.getBoolean(null);
        } catch (Exception e) {
            return false;
        }
    }
}
