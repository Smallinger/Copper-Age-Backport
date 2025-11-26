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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
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
        
        // Register loot modifiers
        com.github.smallinger.copperagebackport.neoforge.loot.NeoForgeLootTableModifier.register(modEventBus);

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
            // Copper Chests after normal chest
            event.insertAfter(Items.CHEST.getDefaultInstance(), ModItems.COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.EXPOSED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.EXPOSED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WEATHERED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WEATHERED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.OXIDIZED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.OXIDIZED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WAXED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Copper Golem Statues (keep at end)
            event.accept(ModItems.COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            // Copper Lanterns after soul lantern
            event.insertAfter(Items.SOUL_LANTERN.getDefaultInstance(), ModItems.COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.EXPOSED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.EXPOSED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WEATHERED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WEATHERED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.OXIDIZED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.OXIDIZED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WAXED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_EXPOSED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_WEATHERED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Copper Torch after soul torch
            event.insertAfter(Items.SOUL_TORCH.getDefaultInstance(), ModItems.COPPER_TORCH_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Shelves after chiseled bookshelf
            event.insertAfter(Items.CHISELED_BOOKSHELF.getDefaultInstance(), ModItems.OAK_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.OAK_SHELF_ITEM.get().getDefaultInstance(), ModItems.SPRUCE_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.SPRUCE_SHELF_ITEM.get().getDefaultInstance(), ModItems.BIRCH_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.BIRCH_SHELF_ITEM.get().getDefaultInstance(), ModItems.JUNGLE_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.JUNGLE_SHELF_ITEM.get().getDefaultInstance(), ModItems.ACACIA_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.ACACIA_SHELF_ITEM.get().getDefaultInstance(), ModItems.DARK_OAK_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.DARK_OAK_SHELF_ITEM.get().getDefaultInstance(), ModItems.MANGROVE_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.MANGROVE_SHELF_ITEM.get().getDefaultInstance(), ModItems.CHERRY_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.CHERRY_SHELF_ITEM.get().getDefaultInstance(), ModItems.BAMBOO_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.BAMBOO_SHELF_ITEM.get().getDefaultInstance(), ModItems.CRIMSON_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.CRIMSON_SHELF_ITEM.get().getDefaultInstance(), ModItems.WARPED_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Pale Oak Shelf - only if VanillaBackport is loaded
            if (ModItems.PALE_OAK_SHELF_ITEM != null) {
                event.insertAfter(ModItems.WARPED_SHELF_ITEM.get().getDefaultInstance(), ModItems.PALE_OAK_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }

        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            // Copper Buttons after stone button
            event.insertAfter(Items.STONE_BUTTON.getDefaultInstance(), ModItems.COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.EXPOSED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.EXPOSED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WEATHERED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WEATHERED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WAXED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        // Add copper tools after stone tools
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.insertAfter(Items.STONE_HOE.getDefaultInstance(), ModItems.COPPER_SHOVEL.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_SHOVEL.get().getDefaultInstance(), ModItems.COPPER_PICKAXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_PICKAXE.get().getDefaultInstance(), ModItems.COPPER_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_AXE.get().getDefaultInstance(), ModItems.COPPER_HOE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        // Add copper sword after stone sword, copper armor after chainmail armor
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.insertAfter(Items.STONE_SWORD.getDefaultInstance(), ModItems.COPPER_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Items.CHAINMAIL_BOOTS.getDefaultInstance(), ModItems.COPPER_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_HELMET.get().getDefaultInstance(), ModItems.COPPER_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_CHESTPLATE.get().getDefaultInstance(), ModItems.COPPER_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_LEGGINGS.get().getDefaultInstance(), ModItems.COPPER_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Add copper horse armor after leather horse armor (before iron horse armor)
            event.insertAfter(Items.LEATHER_HORSE_ARMOR.getDefaultInstance(), ModItems.COPPER_HORSE_ARMOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        // Add copper nugget after iron nugget
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(Items.IRON_NUGGET.getDefaultInstance(), ModItems.COPPER_NUGGET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        // Add copper bars and chains after cut copper slabs in building blocks
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // After cut copper slab
            event.insertAfter(Items.CUT_COPPER_SLAB.getDefaultInstance(), ModItems.COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After exposed cut copper slab
            event.insertAfter(Items.EXPOSED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.EXPOSED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.EXPOSED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.EXPOSED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After weathered cut copper slab
            event.insertAfter(Items.WEATHERED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WEATHERED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WEATHERED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WEATHERED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After oxidized cut copper slab
            event.insertAfter(Items.OXIDIZED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.OXIDIZED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.OXIDIZED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.OXIDIZED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After waxed cut copper slab
            event.insertAfter(Items.WAXED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WAXED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WAXED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After waxed exposed cut copper slab
            event.insertAfter(Items.WAXED_EXPOSED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_EXPOSED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After waxed weathered cut copper slab
            event.insertAfter(Items.WAXED_WEATHERED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_WEATHERED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After waxed oxidized cut copper slab
            event.insertAfter(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ModItems.WAXED_OXIDIZED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
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
