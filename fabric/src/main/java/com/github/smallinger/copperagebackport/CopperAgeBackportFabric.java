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
        
        // Add copper chests after normal chest
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            content.addAfter(Items.CHEST, ModItems.COPPER_CHEST_ITEM.get(), ModItems.EXPOSED_COPPER_CHEST_ITEM.get(), ModItems.WEATHERED_COPPER_CHEST_ITEM.get(), ModItems.OXIDIZED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get());
            // Lightning Rods after vanilla lightning rod
            content.addAfter(Items.LIGHTNING_ROD, ModItems.EXPOSED_LIGHTNING_ROD_ITEM.get(), ModItems.WEATHERED_LIGHTNING_ROD_ITEM.get(), ModItems.OXIDIZED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_EXPOSED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_WEATHERED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_OXIDIZED_LIGHTNING_ROD_ITEM.get());
            // Copper Golem Statues (keep at end)
            content.accept(ModItems.COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            // Copper Lanterns after soul lantern
            content.addAfter(Items.SOUL_LANTERN, ModItems.COPPER_LANTERN_ITEM.get(), ModItems.EXPOSED_COPPER_LANTERN_ITEM.get(), ModItems.WEATHERED_COPPER_LANTERN_ITEM.get(), ModItems.OXIDIZED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_LANTERN_ITEM.get());
            // Copper Torch after soul torch
            content.addAfter(Items.SOUL_TORCH, ModItems.COPPER_TORCH_ITEM.get());
            // Shelves after chiseled bookshelf
            content.addAfter(Items.CHISELED_BOOKSHELF, ModItems.OAK_SHELF_ITEM.get(), ModItems.SPRUCE_SHELF_ITEM.get(), ModItems.BIRCH_SHELF_ITEM.get(), ModItems.JUNGLE_SHELF_ITEM.get(), ModItems.ACACIA_SHELF_ITEM.get(), ModItems.DARK_OAK_SHELF_ITEM.get(), ModItems.MANGROVE_SHELF_ITEM.get(), ModItems.CHERRY_SHELF_ITEM.get(), ModItems.BAMBOO_SHELF_ITEM.get(), ModItems.CRIMSON_SHELF_ITEM.get(), ModItems.WARPED_SHELF_ITEM.get());
            // Pale Oak Shelf - only if VanillaBackport is loaded
            if (ModItems.PALE_OAK_SHELF_ITEM != null) {
                content.addAfter(ModItems.WARPED_SHELF_ITEM.get(), ModItems.PALE_OAK_SHELF_ITEM.get());
            }
        });
        
        // Add copper buttons after stone button
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.addAfter(Items.STONE_BUTTON, ModItems.COPPER_BUTTON_ITEM.get(), ModItems.EXPOSED_COPPER_BUTTON_ITEM.get(), ModItems.WEATHERED_COPPER_BUTTON_ITEM.get(), ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_BUTTON_ITEM.get());
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
        
        // Add copper bars and chains after cut copper slabs in building blocks
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
            // After cut copper slab
            content.addAfter(Items.CUT_COPPER_SLAB, ModItems.COPPER_BARS_ITEM.get(), ModItems.COPPER_CHAIN_ITEM.get());
            // After exposed cut copper slab
            content.addAfter(Items.EXPOSED_CUT_COPPER_SLAB, ModItems.EXPOSED_COPPER_BARS_ITEM.get(), ModItems.EXPOSED_COPPER_CHAIN_ITEM.get());
            // After weathered cut copper slab
            content.addAfter(Items.WEATHERED_CUT_COPPER_SLAB, ModItems.WEATHERED_COPPER_BARS_ITEM.get(), ModItems.WEATHERED_COPPER_CHAIN_ITEM.get());
            // After oxidized cut copper slab
            content.addAfter(Items.OXIDIZED_CUT_COPPER_SLAB, ModItems.OXIDIZED_COPPER_BARS_ITEM.get(), ModItems.OXIDIZED_COPPER_CHAIN_ITEM.get());
            // After waxed cut copper slab
            content.addAfter(Items.WAXED_CUT_COPPER_SLAB, ModItems.WAXED_COPPER_BARS_ITEM.get(), ModItems.WAXED_COPPER_CHAIN_ITEM.get());
            // After waxed exposed cut copper slab
            content.addAfter(Items.WAXED_EXPOSED_CUT_COPPER_SLAB, ModItems.WAXED_EXPOSED_COPPER_BARS_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_CHAIN_ITEM.get());
            // After waxed weathered cut copper slab
            content.addAfter(Items.WAXED_WEATHERED_CUT_COPPER_SLAB, ModItems.WAXED_WEATHERED_COPPER_BARS_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_CHAIN_ITEM.get());
            // After waxed oxidized cut copper slab
            content.addAfter(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB, ModItems.WAXED_OXIDIZED_COPPER_BARS_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_CHAIN_ITEM.get());
        });
    }
}
