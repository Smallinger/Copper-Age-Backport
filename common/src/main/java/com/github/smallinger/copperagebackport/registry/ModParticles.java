package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.Constants;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

import static net.minecraft.core.registries.Registries.PARTICLE_TYPE;

/**
 * Handles registration of all particles for the mod.
 */
public class ModParticles {
    
    public static Supplier<SimpleParticleType> COPPER_FIRE_FLAME;
    
    public static void register() {
        Constants.LOG.info("Registering particles for {}", Constants.MOD_NAME);
        
        RegistryHelper helper = RegistryHelper.getInstance();
        
        COPPER_FIRE_FLAME = helper.register(PARTICLE_TYPE, "copper_fire_flame",
            () -> new SimpleParticleType(false) {});
    }
}
