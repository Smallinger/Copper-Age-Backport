package com.github.smallinger.copperagebackport.neoforge.platform;

import com.github.smallinger.copperagebackport.neoforge.compat.FastChestNeoForgeCompat;
import com.github.smallinger.copperagebackport.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

/**
 * NeoForge implementation of the platform helper service.
 */
public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        ModList modList = ModList.get();
        return modList != null && modList.isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
    
    @Override
    public boolean isFastChestSimplifiedEnabled() {
        return FastChestNeoForgeCompat.isSimplifiedChestEnabled();
    }
}
