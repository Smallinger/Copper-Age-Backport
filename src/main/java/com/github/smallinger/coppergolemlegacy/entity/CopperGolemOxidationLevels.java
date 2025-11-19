package com.github.smallinger.coppergolemlegacy.entity;

import com.github.smallinger.coppergolemlegacy.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.Map;

public class CopperGolemOxidationLevels {
    private static final CopperGolemOxidationLevel UNAFFECTED = new CopperGolemOxidationLevel(
        ModSounds.COPPER_GOLEM_HEAD_SPIN_UNAFFECTED.get(),
        ModSounds.COPPER_GOLEM_HURT_UNAFFECTED.get(),
        ModSounds.COPPER_GOLEM_DEATH_UNAFFECTED.get(),
        ModSounds.COPPER_GOLEM_STEP_UNAFFECTED.get(),
        new ResourceLocation("coppergolemlegacy", "textures/entity/copper_golem/copper_golem.png"),
        new ResourceLocation("coppergolemlegacy", "textures/entity/copper_golem/copper_golem_eyes.png")
    );
    
    private static final CopperGolemOxidationLevel EXPOSED = new CopperGolemOxidationLevel(
        ModSounds.COPPER_GOLEM_HEAD_SPIN_EXPOSED.get(),
        ModSounds.COPPER_GOLEM_HURT_EXPOSED.get(),
        ModSounds.COPPER_GOLEM_DEATH_EXPOSED.get(),
        ModSounds.COPPER_GOLEM_STEP_EXPOSED.get(),
        new ResourceLocation("coppergolemlegacy", "textures/entity/copper_golem/exposed_copper_golem.png"),
        new ResourceLocation("coppergolemlegacy", "textures/entity/copper_golem/exposed_copper_golem_eyes.png")
    );
    
    private static final CopperGolemOxidationLevel WEATHERED = new CopperGolemOxidationLevel(
        ModSounds.COPPER_GOLEM_HEAD_SPIN_WEATHERED.get(),
        ModSounds.COPPER_GOLEM_HURT_WEATHERED.get(),
        ModSounds.COPPER_GOLEM_DEATH_WEATHERED.get(),
        ModSounds.COPPER_GOLEM_STEP_WEATHERED.get(),
        new ResourceLocation("coppergolemlegacy", "textures/entity/copper_golem/weathered_copper_golem.png"),
        new ResourceLocation("coppergolemlegacy", "textures/entity/copper_golem/weathered_copper_golem_eyes.png")
    );
    
    private static final CopperGolemOxidationLevel OXIDIZED = new CopperGolemOxidationLevel(
        ModSounds.COPPER_GOLEM_HEAD_SPIN_OXIDIZED.get(),
        ModSounds.COPPER_GOLEM_HURT_OXIDIZED.get(),
        ModSounds.COPPER_GOLEM_DEATH_OXIDIZED.get(),
        ModSounds.COPPER_GOLEM_STEP_OXIDIZED.get(),
        new ResourceLocation("coppergolemlegacy", "textures/entity/copper_golem/oxidized_copper_golem.png"),
        new ResourceLocation("coppergolemlegacy", "textures/entity/copper_golem/oxidized_copper_golem_eyes.png")
    );
    
    private static final Map<WeatheringCopper.WeatherState, CopperGolemOxidationLevel> WEATHERED_STATES = Map.of(
        WeatheringCopper.WeatherState.UNAFFECTED, UNAFFECTED,
        WeatheringCopper.WeatherState.EXPOSED, EXPOSED,
        WeatheringCopper.WeatherState.WEATHERED, WEATHERED,
        WeatheringCopper.WeatherState.OXIDIZED, OXIDIZED
    );

    public static CopperGolemOxidationLevel getOxidationLevel(WeatheringCopper.WeatherState weatherState) {
        return WEATHERED_STATES.get(weatherState);
    }
}

