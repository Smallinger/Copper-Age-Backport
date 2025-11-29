package com.github.smallinger.copperagebackport.platform;

import com.github.smallinger.copperagebackport.forge.compat.FastChestForgeCompat;
import com.github.smallinger.copperagebackport.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }
    
    @Override
    public boolean isFastChestSimplifiedEnabled() {
        return FastChestForgeCompat.isSimplifiedChestEnabled();
    }
}
