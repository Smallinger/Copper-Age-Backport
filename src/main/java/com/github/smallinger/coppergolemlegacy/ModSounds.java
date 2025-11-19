package com.github.smallinger.coppergolemlegacy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CopperGolemLegacy.MODID);

    // Copper Golem sounds - Unaffected (Regular)
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_DEATH_UNAFFECTED = registerSound("entity.copper_golem.death.unaffected");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_HURT_UNAFFECTED = registerSound("entity.copper_golem.hurt.unaffected");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_STEP_UNAFFECTED = registerSound("entity.copper_golem.step.unaffected");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_HEAD_SPIN_UNAFFECTED = registerSound("entity.copper_golem.head_spin.unaffected");
    
    // Copper Golem sounds - Exposed
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_DEATH_EXPOSED = registerSound("entity.copper_golem.death.exposed");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_HURT_EXPOSED = registerSound("entity.copper_golem.hurt.exposed");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_STEP_EXPOSED = registerSound("entity.copper_golem.step.exposed");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_HEAD_SPIN_EXPOSED = registerSound("entity.copper_golem.head_spin.exposed");
    
    // Copper Golem sounds - Weathered
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_DEATH_WEATHERED = registerSound("entity.copper_golem.death.weathered");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_HURT_WEATHERED = registerSound("entity.copper_golem.hurt.weathered");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_STEP_WEATHERED = registerSound("entity.copper_golem.step.weathered");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_HEAD_SPIN_WEATHERED = registerSound("entity.copper_golem.head_spin.weathered");
    
    // Copper Golem sounds - Oxidized
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_DEATH_OXIDIZED = registerSound("entity.copper_golem.death.oxidized");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_HURT_OXIDIZED = registerSound("entity.copper_golem.hurt.oxidized");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_STEP_OXIDIZED = registerSound("entity.copper_golem.step.oxidized");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_HEAD_SPIN_OXIDIZED = registerSound("entity.copper_golem.head_spin.oxidized");
    
    // Copper Golem sounds - General
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_SPAWN = registerSound("entity.copper_golem.spawn");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_BECOME_STATUE = registerSound("entity.copper_golem.become_statue");
    
    // Copper Golem sounds - Item Interaction
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_ITEM_DROP = registerSound("entity.copper_golem.item_drop");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_ITEM_NO_DROP = registerSound("entity.copper_golem.item_no_drop");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_ITEM_GET = registerSound("entity.copper_golem.no_item_get");
    public static final RegistryObject<SoundEvent> COPPER_GOLEM_ITEM_NO_GET = registerSound("entity.copper_golem.no_item_no_get");
    
    // Copper Chest sounds
    public static final RegistryObject<SoundEvent> COPPER_CHEST_CLOSE = registerSound("block.copper_chest.close");
    public static final RegistryObject<SoundEvent> COPPER_CHEST_OPEN = registerSound("block.copper_chest.open");
    
    // Copper Statue sounds
    public static final RegistryObject<SoundEvent> COPPER_STATUE_HIT = registerSound("block.copper_statue.hit");
    public static final RegistryObject<SoundEvent> COPPER_STATUE_BREAK = registerSound("block.copper_statue.break");
    public static final RegistryObject<SoundEvent> COPPER_STATUE_PLACE = registerSound("block.copper_statue.place");
    public static final RegistryObject<SoundEvent> COPPER_STATUE_BECOME = registerSound("block.copper_statue.become_statue");

    private static RegistryObject<SoundEvent> registerSound(String name) {
        ResourceLocation id = new ResourceLocation(CopperGolemLegacy.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}

