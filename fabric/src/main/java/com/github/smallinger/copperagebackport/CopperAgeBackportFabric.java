package com.github.smallinger.copperagebackport;

import com.github.smallinger.copperagebackport.CommonClass;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.event.CopperGolemSpawnLogic;
import com.github.smallinger.copperagebackport.event.PlayerJoinHandler;
import com.github.smallinger.copperagebackport.platform.FabricRegistryHelper;
import com.github.smallinger.copperagebackport.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionResult;

public class CopperAgeBackportFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        // Initialize config first
        CommonConfig.init(FabricLoader.getInstance().getConfigDir());

        // Initialize the registry helper for Fabric
        RegistryHelper.setInstance(new FabricRegistryHelper());
        
        // Initialize common mod content
        CommonClass.init();
        
        // Register player join event for preview build message
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            PlayerJoinHandler.onPlayerJoin(handler.getPlayer())
        );

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(world instanceof ServerLevel serverLevel) || player == null) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getItemInHand(hand);
            if (!stack.is(Items.CARVED_PUMPKIN)) {
                return InteractionResult.PASS;
            }

            BlockPlaceContext placeContext = new BlockPlaceContext(serverLevel, player, hand, stack, hitResult);
            BlockPos placePos = placeContext.getClickedPos();

            serverLevel.getServer().execute(() -> {
                if (!serverLevel.isLoaded(placePos)) {
                    return;
                }

                BlockState placedState = serverLevel.getBlockState(placePos);
                if (placedState.is(Blocks.CARVED_PUMPKIN)) {
                    Direction direction = Direction.fromYRot(player.getYRot());
                    CopperGolemSpawnLogic.handleBlockPlaced(serverLevel, placePos, placedState, direction);
                }
            });

            return InteractionResult.PASS;
        });
        
        // Register entity attributes
        FabricDefaultAttributeRegistry.register(
            ModEntities.COPPER_GOLEM.get(),
            CommonClass.getCopperGolemAttributes()
        );
        
        // Add items to creative tabs
        registerCreativeTabs();
        
        // Register loot table modifications
        com.github.smallinger.copperagebackport.fabric.loot.FabricLootTableModifier.register();
        
        // Fire registration callbacks (like button waxed references)
        if (RegistryHelper.getInstance() instanceof FabricRegistryHelper helper) {
            helper.fireRegistrationCallbacks();
        }
        
        // Legacy namespace migration is handled by Mixins (IdRemapMixin, PlayerDataRemapMixin)
    }
    
    private void registerCreativeTabs() {
        // Add spawn egg to spawn eggs tab
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> {
            content.accept(ModItems.COPPER_GOLEM_SPAWN_EGG.get());
        });
        
        // FUNCTIONAL_BLOCKS - following 1.21.10 order
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            // Copper Chests after normal chest
            content.addAfter(Items.CHEST, ModItems.COPPER_CHEST_ITEM.get(), ModItems.EXPOSED_COPPER_CHEST_ITEM.get(), ModItems.WEATHERED_COPPER_CHEST_ITEM.get(), ModItems.OXIDIZED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get());
            // Copper Golem Statues (keep at end - these are mod-specific)
            content.accept(ModItems.COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            // Soul Torch -> Copper Torch
            content.addAfter(Items.SOUL_TORCH, ModItems.COPPER_TORCH_ITEM.get());
            // Soul Lantern -> All Copper Lanterns
            content.addAfter(Items.SOUL_LANTERN, ModItems.COPPER_LANTERN_ITEM.get(), ModItems.EXPOSED_COPPER_LANTERN_ITEM.get(), ModItems.WEATHERED_COPPER_LANTERN_ITEM.get(), ModItems.OXIDIZED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_LANTERN_ITEM.get());
            // Iron Chain -> All Copper Chains
            content.addAfter(Items.CHAIN, ModItems.COPPER_CHAIN_ITEM.get(), ModItems.EXPOSED_COPPER_CHAIN_ITEM.get(), ModItems.WEATHERED_COPPER_CHAIN_ITEM.get(), ModItems.OXIDIZED_COPPER_CHAIN_ITEM.get(), ModItems.WAXED_COPPER_CHAIN_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_CHAIN_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_CHAIN_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_CHAIN_ITEM.get());
            // Redstone Lamp -> All Copper Bulbs
            content.addAfter(Items.REDSTONE_LAMP, ModItems.COPPER_BULB_ITEM.get(), ModItems.EXPOSED_COPPER_BULB_ITEM.get(), ModItems.WEATHERED_COPPER_BULB_ITEM.get(), ModItems.OXIDIZED_COPPER_BULB_ITEM.get(), ModItems.WAXED_COPPER_BULB_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_BULB_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_BULB_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_BULB_ITEM.get());
            // Lightning Rod -> Waxed Lightning Rod
            content.addAfter(Items.LIGHTNING_ROD, ModItems.EXPOSED_LIGHTNING_ROD_ITEM.get(), ModItems.WEATHERED_LIGHTNING_ROD_ITEM.get(), ModItems.OXIDIZED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_EXPOSED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_WEATHERED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_OXIDIZED_LIGHTNING_ROD_ITEM.get());
            // Shelves after chiseled bookshelf
            content.addAfter(Items.CHISELED_BOOKSHELF, ModItems.OAK_SHELF_ITEM.get(), ModItems.SPRUCE_SHELF_ITEM.get(), ModItems.BIRCH_SHELF_ITEM.get(), ModItems.JUNGLE_SHELF_ITEM.get(), ModItems.ACACIA_SHELF_ITEM.get(), ModItems.DARK_OAK_SHELF_ITEM.get(), ModItems.MANGROVE_SHELF_ITEM.get(), ModItems.CHERRY_SHELF_ITEM.get(), ModItems.BAMBOO_SHELF_ITEM.get(), ModItems.CRIMSON_SHELF_ITEM.get(), ModItems.WARPED_SHELF_ITEM.get());
            // Pale Oak Shelf - only if VanillaBackport is loaded
            if (ModItems.PALE_OAK_SHELF_ITEM != null) {
                content.addAfter(ModItems.WARPED_SHELF_ITEM.get(), ModItems.PALE_OAK_SHELF_ITEM.get());
            }
        });
        
        // Add copper buttons after stone button - following 1.21.10 order
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            // After Target: Waxed Copper Bulbs
            content.addAfter(Items.TARGET, ModItems.WAXED_COPPER_BULB_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_BULB_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_BULB_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_BULB_ITEM.get());
            // After stone button: Copper Buttons
            content.addAfter(Items.STONE_BUTTON, ModItems.COPPER_BUTTON_ITEM.get(), ModItems.EXPOSED_COPPER_BUTTON_ITEM.get(), ModItems.WEATHERED_COPPER_BUTTON_ITEM.get(), ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_BUTTON_ITEM.get());
            // Waxed Lightning Rod after daylight detector
            content.addAfter(Items.DAYLIGHT_DETECTOR, ModItems.WAXED_LIGHTNING_ROD_ITEM.get());
            // Waxed Copper Chest after chest
            content.addAfter(Items.CHEST, ModItems.WAXED_COPPER_CHEST_ITEM.get());
        });
        
        // Add copper tools after stone tools
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.addAfter(Items.STONE_HOE, ModItems.COPPER_SHOVEL.get(), ModItems.COPPER_PICKAXE.get(), ModItems.COPPER_AXE.get(), ModItems.COPPER_HOE.get());
        });
        
        // Add copper sword after stone sword, copper armor after chainmail armor
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> {
            content.addAfter(Items.STONE_SWORD, ModItems.COPPER_SWORD.get());
            content.addAfter(Items.CHAINMAIL_BOOTS, ModItems.COPPER_HELMET.get(), ModItems.COPPER_CHESTPLATE.get(), ModItems.COPPER_LEGGINGS.get(), ModItems.COPPER_BOOTS.get());
            // Add copper horse armor after leather horse armor (before iron horse armor)
            content.addAfter(Items.LEATHER_HORSE_ARMOR, ModItems.COPPER_HORSE_ARMOR.get());
        });
        
        // Add copper nugget after iron nugget
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> {
            content.addAfter(Items.IRON_NUGGET, ModItems.COPPER_NUGGET.get());
        });
        
        // Add copper items in building blocks - following 1.21.10 order
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
            // Copper Block -> Chiseled Copper -> Copper Grate
            content.addAfter(Items.COPPER_BLOCK, ModItems.CHISELED_COPPER_ITEM.get(), ModItems.COPPER_GRATE_ITEM.get());
            // Cut Copper Slab -> Copper Bars -> Copper Door -> Copper Trapdoor -> Copper Bulb -> Copper Chain
            content.addAfter(Items.CUT_COPPER_SLAB, ModItems.COPPER_BARS_ITEM.get(), ModItems.COPPER_DOOR_ITEM.get(), ModItems.COPPER_TRAPDOOR_ITEM.get(), ModItems.COPPER_BULB_ITEM.get(), ModItems.COPPER_CHAIN_ITEM.get());
            
            // Exposed Copper -> Exposed Chiseled Copper -> Exposed Copper Grate
            content.addAfter(Items.EXPOSED_COPPER, ModItems.EXPOSED_CHISELED_COPPER_ITEM.get(), ModItems.EXPOSED_COPPER_GRATE_ITEM.get());
            // Exposed Cut Copper Slab -> Exposed Copper Bars -> Exposed Copper Door -> Exposed Copper Trapdoor -> Exposed Copper Bulb -> Exposed Copper Chain
            content.addAfter(Items.EXPOSED_CUT_COPPER_SLAB, ModItems.EXPOSED_COPPER_BARS_ITEM.get(), ModItems.EXPOSED_COPPER_DOOR_ITEM.get(), ModItems.EXPOSED_COPPER_TRAPDOOR_ITEM.get(), ModItems.EXPOSED_COPPER_BULB_ITEM.get(), ModItems.EXPOSED_COPPER_CHAIN_ITEM.get());
            
            // Weathered Copper -> Weathered Chiseled Copper -> Weathered Copper Grate
            content.addAfter(Items.WEATHERED_COPPER, ModItems.WEATHERED_CHISELED_COPPER_ITEM.get(), ModItems.WEATHERED_COPPER_GRATE_ITEM.get());
            // Weathered Cut Copper Slab -> Weathered Copper Bars -> Weathered Copper Door -> Weathered Copper Trapdoor -> Weathered Copper Bulb -> Weathered Copper Chain
            content.addAfter(Items.WEATHERED_CUT_COPPER_SLAB, ModItems.WEATHERED_COPPER_BARS_ITEM.get(), ModItems.WEATHERED_COPPER_DOOR_ITEM.get(), ModItems.WEATHERED_COPPER_TRAPDOOR_ITEM.get(), ModItems.WEATHERED_COPPER_BULB_ITEM.get(), ModItems.WEATHERED_COPPER_CHAIN_ITEM.get());
            
            // Oxidized Copper -> Oxidized Chiseled Copper -> Oxidized Copper Grate
            content.addAfter(Items.OXIDIZED_COPPER, ModItems.OXIDIZED_CHISELED_COPPER_ITEM.get(), ModItems.OXIDIZED_COPPER_GRATE_ITEM.get());
            // Oxidized Cut Copper Slab -> Oxidized Copper Bars -> Oxidized Copper Door -> Oxidized Copper Trapdoor -> Oxidized Copper Bulb -> Oxidized Copper Chain
            content.addAfter(Items.OXIDIZED_CUT_COPPER_SLAB, ModItems.OXIDIZED_COPPER_BARS_ITEM.get(), ModItems.OXIDIZED_COPPER_DOOR_ITEM.get(), ModItems.OXIDIZED_COPPER_TRAPDOOR_ITEM.get(), ModItems.OXIDIZED_COPPER_BULB_ITEM.get(), ModItems.OXIDIZED_COPPER_CHAIN_ITEM.get());
            
            // Waxed Copper Block -> Waxed Chiseled Copper -> Waxed Copper Grate
            content.addAfter(Items.WAXED_COPPER_BLOCK, ModItems.WAXED_CHISELED_COPPER_ITEM.get(), ModItems.WAXED_COPPER_GRATE_ITEM.get());
            // Waxed Cut Copper Slab -> Waxed Copper Bars -> Waxed Copper Door -> Waxed Copper Trapdoor -> Waxed Copper Bulb -> Waxed Copper Chain
            content.addAfter(Items.WAXED_CUT_COPPER_SLAB, ModItems.WAXED_COPPER_BARS_ITEM.get(), ModItems.WAXED_COPPER_DOOR_ITEM.get(), ModItems.WAXED_COPPER_TRAPDOOR_ITEM.get(), ModItems.WAXED_COPPER_BULB_ITEM.get(), ModItems.WAXED_COPPER_CHAIN_ITEM.get());
            
            // Waxed Exposed Copper -> Waxed Exposed Chiseled Copper -> Waxed Exposed Copper Grate
            content.addAfter(Items.WAXED_EXPOSED_COPPER, ModItems.WAXED_EXPOSED_CHISELED_COPPER_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_GRATE_ITEM.get());
            // Waxed Exposed Cut Copper Slab -> Waxed Exposed Copper Bars -> Waxed Exposed Copper Door -> Waxed Exposed Copper Trapdoor -> Waxed Exposed Copper Bulb -> Waxed Exposed Copper Chain
            content.addAfter(Items.WAXED_EXPOSED_CUT_COPPER_SLAB, ModItems.WAXED_EXPOSED_COPPER_BARS_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_DOOR_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_TRAPDOOR_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_BULB_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_CHAIN_ITEM.get());
            
            // Waxed Weathered Copper -> Waxed Weathered Chiseled Copper -> Waxed Weathered Copper Grate
            content.addAfter(Items.WAXED_WEATHERED_COPPER, ModItems.WAXED_WEATHERED_CHISELED_COPPER_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_GRATE_ITEM.get());
            // Waxed Weathered Cut Copper Slab -> Waxed Weathered Copper Bars -> Waxed Weathered Copper Door -> Waxed Weathered Copper Trapdoor -> Waxed Weathered Copper Bulb -> Waxed Weathered Copper Chain
            content.addAfter(Items.WAXED_WEATHERED_CUT_COPPER_SLAB, ModItems.WAXED_WEATHERED_COPPER_BARS_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_DOOR_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_TRAPDOOR_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_BULB_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_CHAIN_ITEM.get());
            
            // Waxed Oxidized Copper -> Waxed Oxidized Chiseled Copper -> Waxed Oxidized Copper Grate
            content.addAfter(Items.WAXED_OXIDIZED_COPPER, ModItems.WAXED_OXIDIZED_CHISELED_COPPER_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_GRATE_ITEM.get());
            // Waxed Oxidized Cut Copper Slab -> Waxed Oxidized Copper Bars -> Waxed Oxidized Copper Door -> Waxed Oxidized Copper Trapdoor -> Waxed Oxidized Copper Bulb -> Waxed Oxidized Copper Chain
            content.addAfter(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB, ModItems.WAXED_OXIDIZED_COPPER_BARS_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_DOOR_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_TRAPDOOR_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_BULB_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_CHAIN_ITEM.get());
        });
    }
}
