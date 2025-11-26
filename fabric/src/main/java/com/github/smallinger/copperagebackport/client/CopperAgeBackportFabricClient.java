package com.github.smallinger.copperagebackport.client;

import com.github.smallinger.copperagebackport.client.model.CopperGolemModel;
import com.github.smallinger.copperagebackport.client.renderer.CopperChestRenderer;
import com.github.smallinger.copperagebackport.client.renderer.CopperGolemRenderer;
import com.github.smallinger.copperagebackport.client.renderer.CopperGolemStatueRenderer;
import com.github.smallinger.copperagebackport.client.renderer.CopperItemRenderer;
import com.github.smallinger.copperagebackport.client.renderer.ShelfRenderer;
import com.github.smallinger.copperagebackport.registry.ModBlockEntities;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.registry.ModEntities;
import com.github.smallinger.copperagebackport.registry.ModItems;
import com.github.smallinger.copperagebackport.registry.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

/**
 * Fabric client initializer for Copper-Age-Backport.
 * Registers client-side rendering for 3D items.
 */
public class CopperAgeBackportFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register particle providers
        ParticleFactoryRegistry.getInstance().register(ModParticles.COPPER_FIRE_FLAME.get(), FlameParticle.Provider::new);
        
        // Register model layers for statue rendering
        EntityModelLayerRegistry.registerModelLayer(CopperGolemModel.STATUE_STANDING, CopperGolemModel::createStandingStatueBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CopperGolemModel.STATUE_RUNNING, CopperGolemModel::createRunningPoseBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CopperGolemModel.STATUE_SITTING, CopperGolemModel::createSittingPoseBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CopperGolemModel.STATUE_STAR, CopperGolemModel::createStarPoseBodyLayer);

        // Register entity model layer
        EntityModelLayerRegistry.registerModelLayer(CopperGolemModel.LAYER_LOCATION, CopperGolemModel::createBodyLayer);

        // Register entity renderer
        EntityRendererRegistry.register(ModEntities.COPPER_GOLEM.get(), CopperGolemRenderer::new);

        // Register block entity renderers
        BlockEntityRenderers.register(ModBlockEntities.COPPER_CHEST_BLOCK_ENTITY.get(), CopperChestRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.COPPER_GOLEM_STATUE_BLOCK_ENTITY.get(), CopperGolemStatueRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.SHELF_BLOCK_ENTITY.get(), ShelfRenderer::new);

        registerBlockRenderLayers();

        // Create single renderer instance for all 3D items
        CopperItemRenderer renderer = new CopperItemRenderer();

        // Register custom renderer for all copper chest items
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.COPPER_CHEST_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.EXPOSED_COPPER_CHEST_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WEATHERED_COPPER_CHEST_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.OXIDIZED_COPPER_CHEST_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WAXED_COPPER_CHEST_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get(), renderer);

        // Register custom renderer for all golem statue items
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.COPPER_GOLEM_STATUE_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get(), renderer);
    }

    private void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
            ModBlocks.COPPER_TORCH.get(),
            ModBlocks.COPPER_WALL_TORCH.get(),
            // Copper Lanterns
            ModBlocks.COPPER_LANTERN.get(),
            ModBlocks.EXPOSED_COPPER_LANTERN.get(),
            ModBlocks.WEATHERED_COPPER_LANTERN.get(),
            ModBlocks.OXIDIZED_COPPER_LANTERN.get(),
            ModBlocks.WAXED_COPPER_LANTERN.get(),
            ModBlocks.WAXED_EXPOSED_COPPER_LANTERN.get(),
            ModBlocks.WAXED_WEATHERED_COPPER_LANTERN.get(),
            ModBlocks.WAXED_OXIDIZED_COPPER_LANTERN.get(),
            // Copper Chains
            ModBlocks.COPPER_CHAIN.get(),
            ModBlocks.EXPOSED_COPPER_CHAIN.get(),
            ModBlocks.WEATHERED_COPPER_CHAIN.get(),
            ModBlocks.OXIDIZED_COPPER_CHAIN.get(),
            ModBlocks.WAXED_COPPER_CHAIN.get(),
            ModBlocks.WAXED_EXPOSED_COPPER_CHAIN.get(),
            ModBlocks.WAXED_WEATHERED_COPPER_CHAIN.get(),
            ModBlocks.WAXED_OXIDIZED_COPPER_CHAIN.get(),
            // Copper Grates
            ModBlocks.COPPER_GRATE.get(),
            ModBlocks.EXPOSED_COPPER_GRATE.get(),
            ModBlocks.WEATHERED_COPPER_GRATE.get(),
            ModBlocks.OXIDIZED_COPPER_GRATE.get(),
            ModBlocks.WAXED_COPPER_GRATE.get(),
            ModBlocks.WAXED_EXPOSED_COPPER_GRATE.get(),
            ModBlocks.WAXED_WEATHERED_COPPER_GRATE.get(),
            ModBlocks.WAXED_OXIDIZED_COPPER_GRATE.get());
    }
}

