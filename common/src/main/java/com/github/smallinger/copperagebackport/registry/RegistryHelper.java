package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Base registry helper that works across both Fabric and Forge.
 * Implementations are provided in the loader-specific modules.
 */
public abstract class RegistryHelper {
    
    private static RegistryHelper instance;
    
    public static RegistryHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("RegistryHelper not initialized!");
        }
        return instance;
    }
    
    public static void setInstance(RegistryHelper helper) {
        if (instance != null) {
            throw new IllegalStateException("RegistryHelper already initialized!");
        }
        instance = helper;
    }
    
    protected final List<Runnable> registrationCallbacks = new ArrayList<>();
    
    public abstract <T> Supplier<T> register(ResourceKey<? extends Registry<? super T>> registry, String name, Supplier<T> supplier);
    
    public void onRegisterComplete(Runnable callback) {
        registrationCallbacks.add(callback);
    }
    
    protected void fireRegistrationCallbacks() {
        registrationCallbacks.forEach(Runnable::run);
        Constants.LOG.info("Fired {} registration callbacks", registrationCallbacks.size());
    }
    
    protected ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
