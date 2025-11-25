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
        
        // Fire registration callbacks (like button waxed references)
        if (RegistryHelper.getInstance() instanceof FabricRegistryHelper helper) {
            helper.fireRegistrationCallbacks();
        }
    }
    
    private void registerCreativeTabs() {
        // Add spawn egg to spawn eggs tab
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> {
            content.accept(ModItems.COPPER_GOLEM_SPAWN_EGG.get());
        });
        
        // Add copper chests and statues to functional blocks tab
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            content.accept(ModItems.COPPER_CHEST_ITEM.get());
            content.accept(ModItems.EXPOSED_COPPER_CHEST_ITEM.get());
            content.accept(ModItems.WEATHERED_COPPER_CHEST_ITEM.get());
            content.accept(ModItems.OXIDIZED_COPPER_CHEST_ITEM.get());
            content.accept(ModItems.WAXED_COPPER_CHEST_ITEM.get());
            content.accept(ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get());
            content.accept(ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get());
            content.accept(ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get());
            content.accept(ModItems.COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            content.accept(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            // Shelves
            content.accept(ModItems.OAK_SHELF_ITEM.get());
            content.accept(ModItems.SPRUCE_SHELF_ITEM.get());
            content.accept(ModItems.BIRCH_SHELF_ITEM.get());
            content.accept(ModItems.JUNGLE_SHELF_ITEM.get());
            content.accept(ModItems.ACACIA_SHELF_ITEM.get());
            content.accept(ModItems.DARK_OAK_SHELF_ITEM.get());
            content.accept(ModItems.MANGROVE_SHELF_ITEM.get());
            content.accept(ModItems.CHERRY_SHELF_ITEM.get());
            content.accept(ModItems.BAMBOO_SHELF_ITEM.get());
            content.accept(ModItems.CRIMSON_SHELF_ITEM.get());
            content.accept(ModItems.WARPED_SHELF_ITEM.get());
            // Pale Oak Shelf - only if VanillaBackport is loaded
            if (ModItems.PALE_OAK_SHELF_ITEM != null) {
                content.accept(ModItems.PALE_OAK_SHELF_ITEM.get());
            }
        });
        
        // Add copper buttons to redstone blocks tab
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.accept(ModItems.COPPER_BUTTON_ITEM.get());
            content.accept(ModItems.EXPOSED_COPPER_BUTTON_ITEM.get());
            content.accept(ModItems.WEATHERED_COPPER_BUTTON_ITEM.get());
            content.accept(ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get());
            content.accept(ModItems.WAXED_COPPER_BUTTON_ITEM.get());
            content.accept(ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get());
            content.accept(ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get());
            content.accept(ModItems.WAXED_OXIDIZED_COPPER_BUTTON_ITEM.get());
            // Copper Torch
            content.accept(ModItems.COPPER_TORCH_ITEM.get());
        });
        
        // Add copper tools to tools tab
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.accept(ModItems.COPPER_SHOVEL.get());
            content.accept(ModItems.COPPER_PICKAXE.get());
            content.accept(ModItems.COPPER_AXE.get());
            content.accept(ModItems.COPPER_HOE.get());
        });
        
        // Add copper sword to combat tab
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> {
            content.accept(ModItems.COPPER_SWORD.get());
            content.accept(ModItems.COPPER_HELMET.get());
            content.accept(ModItems.COPPER_CHESTPLATE.get());
            content.accept(ModItems.COPPER_LEGGINGS.get());
            content.accept(ModItems.COPPER_BOOTS.get());
        });
        
        // Add copper nugget to ingredients tab
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> {
            content.accept(ModItems.COPPER_NUGGET.get());
        });
    }
}
