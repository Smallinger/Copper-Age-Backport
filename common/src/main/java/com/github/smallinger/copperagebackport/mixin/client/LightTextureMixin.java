package com.github.smallinger.copperagebackport.mixin.client;

import com.github.smallinger.copperagebackport.client.endflash.EndFlashAccessor;
import com.github.smallinger.copperagebackport.client.endflash.EndFlashState;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to add End Flash purple lighting effect to LightTexture.
 * Ported from Minecraft 1.21.10 to 1.21.1
 * 
 * Block lighting becomes purple tinted during the flash.
 */
@Mixin(LightTexture.class)
public abstract class LightTextureMixin {
    
    @Shadow
    @Final
    private Minecraft minecraft;
    
    @Shadow
    @Final
    private GameRenderer renderer;
    
    @Shadow
    @Final
    private NativeImage lightPixels;
    
    // Purple tint color for End flash (from 1.21.10)
    @Unique
    private static final Vector3f END_FLASH_SKY_LIGHT_COLOR = new Vector3f(0.9F, 0.5F, 1.0F);
    
    @Unique
    private float copperagebackport$endFlashIntensity = 0.0F;
    
    /**
     * Calculate End flash intensity before updating light texture
     */
    @Inject(method = "updateLightTexture", at = @At("HEAD"))
    private void copperagebackport$captureEndFlashIntensity(float partialTicks, CallbackInfo ci) {
        this.copperagebackport$endFlashIntensity = 0.0F;
        ClientLevel clientlevel = this.minecraft.level;
        
        if (clientlevel != null && clientlevel.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
            EndFlashState endFlashState = EndFlashAccessor.get(clientlevel);
            if (endFlashState != null && !this.minecraft.options.hideLightningFlash().get()) {
                float intensity = endFlashState.getIntensity(partialTicks);
                
                // If ender dragon fog is active, reduce intensity
                if (this.minecraft.gui.getBossOverlay().shouldCreateWorldFog()) {
                    this.copperagebackport$endFlashIntensity = intensity / 3.0F;
                } else {
                    this.copperagebackport$endFlashIntensity = intensity;
                }
            }
        }
    }
    
    /**
     * After the lightmap is calculated but before upload, apply the purple tint.
     * We inject right before the upload() call and modify the pixel data.
     */
    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"))
    private void copperagebackport$applyEndFlashTint(float partialTicks, CallbackInfo ci) {
        if (this.copperagebackport$endFlashIntensity <= 0.0F) return;
        
        ClientLevel clientlevel = this.minecraft.level;
        if (clientlevel == null || clientlevel.effects().skyType() != DimensionSpecialEffects.SkyType.END) return;
        
        float intensity = this.copperagebackport$endFlashIntensity;
        
        // Apply purple tint to all pixels in the lightmap
        for (int skyLight = 0; skyLight < 16; ++skyLight) {
            for (int blockLight = 0; blockLight < 16; ++blockLight) {
                int pixel = this.lightPixels.getPixelRGBA(blockLight, skyLight);
                
                // Extract RGB components (ABGR format in NativeImage)
                int a = (pixel >> 24) & 0xFF;
                int b = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int r = pixel & 0xFF;
                
                // Lerp towards purple tint based on intensity
                // Purple: R=0.9, G=0.5, B=1.0 (scaled to 255)
                float newR = r + (230 - r) * intensity; // 0.9 * 255 = 230
                float newG = g + (128 - g) * intensity; // 0.5 * 255 = 128
                float newB = b + (255 - b) * intensity; // 1.0 * 255 = 255
                
                // Also boost overall brightness based on intensity
                float brightnessFactor = 1.0F + intensity * 0.5F;
                newR = Math.min(255, newR * brightnessFactor);
                newG = Math.min(255, newG * brightnessFactor);
                newB = Math.min(255, newB * brightnessFactor);
                
                int newPixel = (a << 24) | ((int)newB << 16) | ((int)newG << 8) | (int)newR;
                this.lightPixels.setPixelRGBA(blockLight, skyLight, newPixel);
            }
        }
    }
}
