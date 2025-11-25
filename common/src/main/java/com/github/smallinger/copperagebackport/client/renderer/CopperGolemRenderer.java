package com.github.smallinger.copperagebackport.client.renderer;

import com.github.smallinger.copperagebackport.client.model.CopperGolemModel;
import com.github.smallinger.copperagebackport.client.renderer.layers.CopperGolemEyesLayer;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.entity.CopperGolemOxidationLevels;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class CopperGolemRenderer extends MobRenderer<CopperGolemEntity, CopperGolemModel> {
    
    public CopperGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new CopperGolemModel(context.bakeLayer(CopperGolemModel.LAYER_LOCATION)), 0.5F);
        
        // Add glowing eyes layer (uses the same model as the parent for correct animations)
        this.addLayer(new CopperGolemEyesLayer(this));
        
        // Add item in hand layer for rendering items
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(CopperGolemEntity entity) {
        return CopperGolemOxidationLevels.getOxidationLevel(entity.getWeatherState()).texture();
    }
    
    @Override
    protected void scale(CopperGolemEntity entity, PoseStack poseStack, float partialTick) {
        // Apply Y translation to match original model coordinate system
        poseStack.translate(0.0D, 1.5D, 0.0D);
    }
}

