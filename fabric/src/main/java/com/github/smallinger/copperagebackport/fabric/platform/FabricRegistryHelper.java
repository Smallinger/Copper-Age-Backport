package com.github.smallinger.copperagebackport.fabric.platform;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Fabric implementation of the shared RegistryHelper.
 * Fabric registers content immediately against the built-in registries.
 */
public class FabricRegistryHelper extends RegistryHelper {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(ResourceKey<? extends Registry<? super T>> registryKey,
                                    String name,
                                    Supplier<T> supplier) {
        return registerWithNamespace(registryKey, Constants.MOD_ID, name, supplier);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> registerWithNamespace(ResourceKey<? extends Registry<? super T>> registryKey,
                                                  String namespace,
                                                  String name,
                                                  Supplier<T> supplier) {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location());
        if (registry == null) {
            throw new IllegalArgumentException("Unknown registry: " + registryKey.location());
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, name);
        T registered = net.minecraft.core.Registry.register(registry, id, supplier.get());
        
        Constants.LOG.debug("Registered {} under namespace {}", name, namespace);
        
        return () -> registered;
    }

    @Override
    public void fireRegistrationCallbacks() {
        super.fireRegistrationCallbacks();
    }
}
