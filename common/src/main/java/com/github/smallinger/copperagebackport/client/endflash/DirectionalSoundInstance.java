package com.github.smallinger.copperagebackport.client.endflash;

import net.minecraft.client.Camera;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * A sound instance that plays from a specific direction relative to the camera.
 * Ported from Minecraft 1.21.10 to 1.21.1
 * 
 * Used for the End flash sound to play from the direction of the flash.
 */
public class DirectionalSoundInstance extends AbstractTickableSoundInstance {
    private final Camera camera;
    private final float xAngle;
    private final float yAngle;

    public DirectionalSoundInstance(SoundEvent soundEvent, SoundSource source, RandomSource random, Camera camera, float xAngle, float yAngle) {
        super(soundEvent, source, random);
        this.camera = camera;
        this.xAngle = xAngle;
        this.yAngle = yAngle;
        this.setPosition();
    }

    private void setPosition() {
        Vec3 vec3 = Vec3.directionFromRotation(this.xAngle, this.yAngle).scale(10.0);
        this.x = this.camera.getPosition().x + vec3.x;
        this.y = this.camera.getPosition().y + vec3.y;
        this.z = this.camera.getPosition().z + vec3.z;
        this.attenuation = SoundInstance.Attenuation.NONE;
    }

    @Override
    public void tick() {
        this.setPosition();
    }
}
