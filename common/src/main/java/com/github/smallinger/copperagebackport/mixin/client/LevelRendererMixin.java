package com.github.smallinger.copperagebackport.mixin.client;

import com.github.smallinger.copperagebackport.client.endflash.EndFlashAccessor;
import com.github.smallinger.copperagebackport.client.endflash.EndFlashState;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to render the End Flash visual effect in the sky.
 * Ported from Minecraft 1.21.10 to 1.20.1
 */
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    
    @Shadow
    @Final
    private Minecraft minecraft;
    
    @Unique
    private static final ResourceLocation END_FLASH_LOCATION = new ResourceLocation("minecraft", "textures/environment/end_flash.png");
    
    @Unique
    private static final float END_FLASH_HEIGHT = 100.0F;
    
    @Unique
    private static final float END_FLASH_SCALE = 60.0F;
    
    /**
     * Inject after renderEndSky to render the End Flash
     */
    @Inject(method = "renderEndSky", at = @At("RETURN"))
    private void copperagebackport$renderEndFlash(PoseStack poseStack, CallbackInfo ci) {
        ClientLevel level = this.minecraft.level;
        if (level == null) return;
        
        EndFlashState endFlashState = EndFlashAccessor.get(level);
        if (endFlashState == null) return;
        
        float partialTick = this.minecraft.getFrameTime();
        float intensity = endFlashState.getIntensity(partialTick);
        
        // Only render if flash is active
        if (intensity <= 0.0001F) return;
        
        // Check if lightning flash is hidden
        if (this.minecraft.options.hideLightningFlash().get()) return;
        
        // While the ender dragon fog effect is active, the flash source in the sky is not visible
        if (this.minecraft.gui.getBossOverlay().shouldCreateWorldFog()) return;
        
        float xAngle = endFlashState.getXAngle();
        float yAngle = endFlashState.getYAngle();
        
        // Render the flash quad - reusing the posestack from renderEndSky
        poseStack.pushPose();
        
        // Rotate to face the flash direction (like 1.21.10)
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yAngle));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - xAngle));
        
        // Move to flash position and scale
        poseStack.translate(0.0F, END_FLASH_HEIGHT, 0.0F);
        poseStack.scale(END_FLASH_SCALE, 1.0F, END_FLASH_SCALE);
        
        Matrix4f matrix4f = poseStack.last().pose();
        
        // Set up rendering state - use additive blending like 1.21.10 celestial shader
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 1, 1, 0); // SRC_ALPHA, ONE, ONE, ZERO
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, END_FLASH_LOCATION);
        
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix4f, -1.0F, 0.0F, -1.0F).uv(0.0F, 0.0F).color(intensity, intensity, intensity, intensity).endVertex();
        bufferbuilder.vertex(matrix4f, 1.0F, 0.0F, -1.0F).uv(1.0F, 0.0F).color(intensity, intensity, intensity, intensity).endVertex();
        bufferbuilder.vertex(matrix4f, 1.0F, 0.0F, 1.0F).uv(1.0F, 1.0F).color(intensity, intensity, intensity, intensity).endVertex();
        bufferbuilder.vertex(matrix4f, -1.0F, 0.0F, 1.0F).uv(0.0F, 1.0F).color(intensity, intensity, intensity, intensity).endVertex();
        
        tesselator.end();
        
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        
        poseStack.popPose();
    }
}
