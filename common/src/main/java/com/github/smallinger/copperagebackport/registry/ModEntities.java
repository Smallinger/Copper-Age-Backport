package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

/**
 * Handles registration of all entities for the mod.
 */
public class ModEntities {
    
    // Copper Golem Entity
    public static Supplier<EntityType<CopperGolemEntity>> COPPER_GOLEM;
    
    public static void register() {
        Constants.LOG.info("Registering entities for {}", Constants.MOD_NAME);
        
        RegistryHelper helper = RegistryHelper.getInstance();
        
        COPPER_GOLEM = helper.registerAuto(
            Registries.ENTITY_TYPE,
            "copper_golem",
            () -> EntityType.Builder.of(CopperGolemEntity::new, MobCategory.MISC)
                .sized(0.49F, 0.98F)
                .eyeHeight(0.8125F)
                .clientTrackingRange(10)
                .build("copper_golem")
        );
    }
}
