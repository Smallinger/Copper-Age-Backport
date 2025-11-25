package com.github.smallinger.copperagebackport.neoforge;

import com.github.smallinger.copperagebackport.CommonClass;
import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.client.model.CopperGolemModel;
import com.github.smallinger.copperagebackport.client.renderer.CopperChestRenderer;
import com.github.smallinger.copperagebackport.client.renderer.CopperGolemRenderer;
import com.github.smallinger.copperagebackport.client.renderer.CopperGolemStatueRenderer;
import com.github.smallinger.copperagebackport.client.renderer.ShelfRenderer;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.event.CopperGolemSpawnLogic;
import com.github.smallinger.copperagebackport.event.PlayerJoinHandler;
import com.github.smallinger.copperagebackport.neoforge.platform.NeoForgeRegistryHelper;
import com.github.smallinger.copperagebackport.registry.ModBlockEntities;
import com.github.smallinger.copperagebackport.registry.ModEntities;
import com.github.smallinger.copperagebackport.registry.ModItems;
import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * NeoForge entrypoint for Copper Age Backport.
 */
@Mod(Constants.MOD_ID)
public class CopperAgeBackportNeoForge {

    public CopperAgeBackportNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // Initialize config first
        CommonConfig.init(FMLPaths.CONFIGDIR.get());
        
        RegistryHelper.setInstance(new NeoForgeRegistryHelper(modEventBus));

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(this::onBuildCreativeTabs);
        modEventBus.addListener(this::registerLayerDefinitions);
        modEventBus.addListener(this::registerRenderers);

        NeoForge.EVENT_BUS.register(this);

        CommonClass.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> RegistryHelper.getInstance().flushRegistrationCallbacks());
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.COPPER_GOLEM.get(), CommonClass.getCopperGolemAttributes().build());
    }

    private void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.COPPER_GOLEM_SPAWN_EGG.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.COPPER_CHEST_ITEM.get());
            event.accept(ModItems.EXPOSED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WEATHERED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.OXIDIZED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WAXED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            // Shelves
            event.accept(ModItems.OAK_SHELF_ITEM.get());
            event.accept(ModItems.BIRCH_SHELF_ITEM.get());
            event.accept(ModItems.SPRUCE_SHELF_ITEM.get());
            event.accept(ModItems.JUNGLE_SHELF_ITEM.get());
            event.accept(ModItems.ACACIA_SHELF_ITEM.get());
            event.accept(ModItems.DARK_OAK_SHELF_ITEM.get());
            event.accept(ModItems.MANGROVE_SHELF_ITEM.get());
            event.accept(ModItems.CHERRY_SHELF_ITEM.get());
            event.accept(ModItems.BAMBOO_SHELF_ITEM.get());
            event.accept(ModItems.CRIMSON_SHELF_ITEM.get());
            event.accept(ModItems.WARPED_SHELF_ITEM.get());
            // Pale Oak Shelf - only if VanillaBackport is loaded
            if (ModItems.PALE_OAK_SHELF_ITEM != null) {
                event.accept(ModItems.PALE_OAK_SHELF_ITEM.get());
            }
        }

        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModItems.COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.EXPOSED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WEATHERED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WAXED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WAXED_OXIDIZED_COPPER_BUTTON_ITEM.get());
        }
        
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.COPPER_SHOVEL.get());
            event.accept(ModItems.COPPER_PICKAXE.get());
            event.accept(ModItems.COPPER_AXE.get());
            event.accept(ModItems.COPPER_HOE.get());
        }
        
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.COPPER_SWORD.get());
            event.accept(ModItems.COPPER_HELMET.get());
            event.accept(ModItems.COPPER_CHESTPLATE.get());
            event.accept(ModItems.COPPER_LEGGINGS.get());
            event.accept(ModItems.COPPER_BOOTS.get());
        }
        
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.COPPER_NUGGET.get());
        }
    }

    private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CopperGolemModel.LAYER_LOCATION, CopperGolemModel::createBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_STANDING, CopperGolemModel::createStandingStatueBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_RUNNING, CopperGolemModel::createRunningPoseBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_SITTING, CopperGolemModel::createSittingPoseBodyLayer);
        event.registerLayerDefinition(CopperGolemModel.STATUE_STAR, CopperGolemModel::createStarPoseBodyLayer);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.COPPER_GOLEM.get(), CopperGolemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.COPPER_CHEST_BLOCK_ENTITY.get(), CopperChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.COPPER_GOLEM_STATUE_BLOCK_ENTITY.get(), CopperGolemStatueRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF_BLOCK_ENTITY.get(), ShelfRenderer::new);
    }


    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        Direction direction = Direction.NORTH;
        if (event.getEntity() != null) {
            direction = Direction.fromYRot(event.getEntity().getYRot());
        }

        BlockState placedState = event.getPlacedBlock();
        CopperGolemSpawnLogic.handleBlockPlaced(serverLevel, event.getPos(), placedState, direction);
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerJoinHandler.onPlayerJoin(serverPlayer);
        }
    }
}
