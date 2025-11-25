package com.github.smallinger.copperagebackport.client.renderer.layers;

import com.github.smallinger.copperagebackport.client.model.CopperGolemModel;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.entity.CopperGolemOxidationLevels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class CopperGolemEyesLayer extends RenderLayer<CopperGolemEntity, CopperGolemModel> {
    
    public CopperGolemEyesLayer(RenderLayerParent<CopperGolemEntity, CopperGolemModel> renderer) {
        super(renderer);
    }
    
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, 
                      CopperGolemEntity entity, float limbSwing, float limbSwingAmount, 
                      float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        
        if (entity.isInvisible()) {
            return;
        }
        
        // Get the eye texture for current oxidation level
        ResourceLocation eyeTexture = CopperGolemOxidationLevels
            .getOxidationLevel(entity.getWeatherState())
            .eyeTexture();
        
        // Render with eyes RenderType (glows in dark, full bright)
        RenderType renderType = RenderType.eyes(eyeTexture);
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
        
        // Use the parent's model which already has all the correct transformations and animations
        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 15728880, 
                                             OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}

