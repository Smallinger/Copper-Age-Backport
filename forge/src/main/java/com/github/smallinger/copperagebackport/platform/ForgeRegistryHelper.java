package com.github.smallinger.copperagebackport.platform;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeRegistryHelper extends RegistryHelper {
    
    private final Map<ResourceKey<?>, DeferredRegister<?>> registers = new HashMap<>();
    private final IEventBus modEventBus;
    
    public ForgeRegistryHelper() {
        this.modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String name, Supplier<T> supplier) {
        DeferredRegister<T> register = (DeferredRegister<T>) registers.computeIfAbsent(registryKey, key -> {
            // Safe cast: We know the key matches the registry type T
            ResourceKey<? extends Registry<T>> typedKey = (ResourceKey<? extends Registry<T>>) key;
            DeferredRegister<T> newRegister = DeferredRegister.create(typedKey, Constants.MOD_ID);
            newRegister.register(modEventBus);
            Constants.LOG.debug("Created DeferredRegister for registry: {}", key.location());
            return newRegister;
        });
        
        RegistryObject<T> registryObject = register.register(name, supplier);
        return registryObject;
    }
    
    @Override
    public void fireRegistrationCallbacks() {
        super.fireRegistrationCallbacks();
    }
}
