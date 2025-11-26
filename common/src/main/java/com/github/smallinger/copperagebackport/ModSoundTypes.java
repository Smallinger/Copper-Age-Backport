package com.github.smallinger.copperagebackport;

import net.minecraft.world.level.block.SoundType;

public class ModSoundTypes {
    public static final SoundType COPPER_STATUE = new SoundType(
        1.0F, // volume
        1.0F, // pitch
        ModSounds.COPPER_STATUE_BREAK.get(),
        ModSounds.COPPER_STATUE_PLACE.get(), // step sound
        ModSounds.COPPER_STATUE_PLACE.get(),
        ModSounds.COPPER_STATUE_HIT.get(),
        ModSounds.COPPER_STATUE_PLACE.get()  // fall sound
    );
}

