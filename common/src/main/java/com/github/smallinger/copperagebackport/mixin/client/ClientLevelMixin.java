package com.github.smallinger.copperagebackport.mixin.client;

import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.client.endflash.DirectionalSoundInstance;
import com.github.smallinger.copperagebackport.client.endflash.EndFlashAccessor;
import com.github.smallinger.copperagebackport.client.endflash.EndFlashState;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * Mixin to add End Flash functionality to ClientLevel.
 * Ported from Minecraft 1.21.10 to 1.21.1
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements EndFlashAccessor {
    
    @Shadow
    public abstract DimensionSpecialEffects effects();
    
    @Shadow
    @Final
    private Minecraft minecraft;
    
    @Unique
    private EndFlashState copperagebackport$endFlashState;
    
    protected ClientLevelMixin() {
        super(null, null, null, null, null, false, false, 0, 0);
    }
    
    /**
     * Initialize EndFlashState for End dimension
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void copperagebackport$onInit(CallbackInfo ci) {
        // Check if we're in the End dimension (SkyType.END means it has end effects)
        // and if End Flash is enabled in config
        if (this.effects().skyType() == DimensionSpecialEffects.SkyType.END && CommonConfig.endFlashEnabled()) {
            this.copperagebackport$endFlashState = new EndFlashState();
        }
    }
    
    /**
     * Tick the EndFlashState and play sound when flash starts
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void copperagebackport$onTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        if (this.copperagebackport$endFlashState != null) {
            this.copperagebackport$endFlashState.tick(this.getGameTime());
            
            // Play directional sound when flash starts
            if (this.copperagebackport$endFlashState.flashStartedThisTick()) {
                this.minecraft.getSoundManager().playDelayed(
                    new DirectionalSoundInstance(
                        ModSounds.WEATHER_END_FLASH.get(),
                        SoundSource.WEATHER,
                        this.random,
                        this.minecraft.gameRenderer.getMainCamera(),
                        this.copperagebackport$endFlashState.getXAngle(),
                        this.copperagebackport$endFlashState.getYAngle()
                    ),
                    EndFlashState.SOUND_DELAY_IN_TICKS
                );
            }
        }
    }
    
    /**
     * Accessor for the EndFlashState
     */
    @Override
    @Unique
    public EndFlashState copperagebackport$getEndFlashState() {
        return this.copperagebackport$endFlashState;
    }
}
