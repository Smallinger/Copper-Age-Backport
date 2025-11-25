package com.github.smallinger.copperagebackport.client;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.client.model.CopperGolemModel;
import com.github.smallinger.copperagebackport.client.renderer.CopperChestRenderer;
import com.github.smallinger.copperagebackport.client.renderer.CopperGolemRenderer;
import com.github.smallinger.copperagebackport.client.renderer.CopperGolemStatueRenderer;
import com.github.smallinger.copperagebackport.client.renderer.ShelfRenderer;
import com.github.smallinger.copperagebackport.registry.ModBlockEntities;
import com.github.smallinger.copperagebackport.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CopperAgeBackportForgeClientRenderers {
    
    /**
     * Register model layers for statue rendering and entity rendering.
     */
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Register statue model layers for item rendering
        event.registerLayerDefinition(CopperGolemModel.STATUE_STANDING, CopperGolemModel::createStandingStatueBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_RUNNING, CopperGolemModel::createRunningPoseBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_SITTING, CopperGolemModel::createSittingPoseBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_STAR, CopperGolemModel::createStarPoseBodyLayer);
        
        // Register entity model layer
        event.registerLayerDefinition(CopperGolemModel.LAYER_LOCATION, CopperGolemModel::createBodyLayer);
    }
    
    /**
     * Register entity and block entity renderers.
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register entity renderer for Copper Golem
        event.registerEntityRenderer(ModEntities.COPPER_GOLEM.get(), CopperGolemRenderer::new);
        
        // Register block entity renderers
        event.registerBlockEntityRenderer(ModBlockEntities.COPPER_CHEST_BLOCK_ENTITY.get(), CopperChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.COPPER_GOLEM_STATUE_BLOCK_ENTITY.get(), CopperGolemStatueRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF_BLOCK_ENTITY.get(), ShelfRenderer::new);
    }
}
