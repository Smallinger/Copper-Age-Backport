package com.github.smallinger.copperagebackport.platform;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class FabricRegistryHelper extends RegistryHelper {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String name, Supplier<T> supplier) {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location());

        if (registry == null) {
            throw new IllegalArgumentException("Unknown registry: " + registryKey.location());
        }

        ResourceLocation id = id(name);
        T registered = Registry.register(registry, id, supplier.get());

        return () -> registered;
    }

    @Override
    public void fireRegistrationCallbacks() {
        super.fireRegistrationCallbacks();
    }
}

