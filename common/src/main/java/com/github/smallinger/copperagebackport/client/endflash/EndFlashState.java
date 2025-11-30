package com.github.smallinger.copperagebackport.client.endflash;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Manages the state of the End dimension flash effect.
 * Ported from Minecraft 1.21.10 to 1.21.1
 * 
 * The End flash makes the sky periodically emit purple glow and play various sounds.
 * - There is a 30 second delay between flashes (600 ticks)
 * - Each flash lasts between 5 and 19 seconds (100-380 ticks)
 * - Block lighting becomes purple tinted during the flash
 */
public class EndFlashState {
    public static final int SOUND_DELAY_IN_TICKS = 30;
    private static final int FLASH_INTERVAL_IN_TICKS = 600;
    private static final int MAX_FLASH_OFFSET_IN_TICKS = 200;
    private static final int MIN_FLASH_DURATION_IN_TICKS = 100;
    private static final int MAX_FLASH_DURATION_IN_TICKS = 380;
    
    private long flashSeed;
    private int offset;
    private int duration;
    private float intensity;
    private float oldIntensity;
    private float xAngle;
    private float yAngle;

    public void tick(long gametime) {
        this.calculateFlashParameters(gametime);
        this.oldIntensity = this.intensity;
        this.intensity = this.calculateIntensity(gametime);
    }

    private void calculateFlashParameters(long gametime) {
        long i = gametime / FLASH_INTERVAL_IN_TICKS;
        if (i != this.flashSeed) {
            RandomSource randomsource = RandomSource.create(i);
            randomsource.nextFloat();
            this.offset = Mth.randomBetweenInclusive(randomsource, 0, MAX_FLASH_OFFSET_IN_TICKS);
            this.duration = Mth.randomBetweenInclusive(randomsource, MIN_FLASH_DURATION_IN_TICKS, 
                    Math.min(MAX_FLASH_DURATION_IN_TICKS, FLASH_INTERVAL_IN_TICKS - this.offset));
            this.xAngle = Mth.randomBetween(randomsource, -60.0F, 10.0F);
            this.yAngle = Mth.randomBetween(randomsource, -180.0F, 180.0F);
            this.flashSeed = i;
        }
    }

    private float calculateIntensity(long gametime) {
        long i = gametime % FLASH_INTERVAL_IN_TICKS;
        return i >= this.offset && i <= this.offset + this.duration 
                ? Mth.sin((float)(i - this.offset) * (float) Math.PI / this.duration) 
                : 0.0F;
    }

    public float getXAngle() {
        return this.xAngle;
    }

    public float getYAngle() {
        return this.yAngle;
    }

    public float getIntensity(float partialTick) {
        return Mth.lerp(partialTick, this.oldIntensity, this.intensity);
    }

    public boolean flashStartedThisTick() {
        return this.intensity > 0.0F && this.oldIntensity <= 0.0F;
    }
}
