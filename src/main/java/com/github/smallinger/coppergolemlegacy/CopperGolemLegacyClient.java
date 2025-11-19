package com.github.smallinger.coppergolemlegacy;

import com.github.smallinger.coppergolemlegacy.client.model.CopperGolemModel;
import com.github.smallinger.coppergolemlegacy.client.renderer.CopperChestRenderer;
import com.github.smallinger.coppergolemlegacy.client.renderer.CopperGolemRenderer;
import com.github.smallinger.coppergolemlegacy.client.renderer.CopperGolemStatueRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod.EventBusSubscriber(modid = CopperGolemLegacy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CopperGolemLegacyClient {
    public static void init() {
        // Config screen removed - Forge 1.20.1 doesn't have ConfigScreenHandler
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        CopperGolemLegacy.LOGGER.info("HELLO FROM CLIENT SETUP");
        CopperGolemLegacy.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
    
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CopperGolemLegacy.COPPER_GOLEM.get(), CopperGolemRenderer::new);
    }
    
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CopperGolemModel.LAYER_LOCATION, CopperGolemModel::createBodyLayer);
        
        // Register statue pose layers
        event.registerLayerDefinition(CopperGolemModel.STATUE_STANDING, CopperGolemModel::createStandingStatueBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_RUNNING, CopperGolemModel::createRunningPoseBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_SITTING, CopperGolemModel::createSittingPoseBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_STAR, CopperGolemModel::createStarPoseBodyLayer);
    }
    
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CopperGolemLegacy.COPPER_CHEST_BLOCK_ENTITY.get(), CopperChestRenderer::new);
        event.registerBlockEntityRenderer(CopperGolemLegacy.COPPER_GOLEM_STATUE_BLOCK_ENTITY.get(), CopperGolemStatueRenderer::new);
    }
}

