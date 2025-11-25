package com.github.smallinger.copperagebackport.fabric;

import com.github.smallinger.copperagebackport.CommonClass;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.event.CopperGolemSpawnLogic;
import com.github.smallinger.copperagebackport.event.PlayerJoinHandler;
import com.github.smallinger.copperagebackport.fabric.platform.FabricRegistryHelper;
import com.github.smallinger.copperagebackport.registry.ModEntities;
import com.github.smallinger.copperagebackport.registry.ModItems;
import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fabric entrypoint that bootstraps the shared Copper Age Backport logic.
 */
public class CopperAgeBackportFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // Initialize config first
        CommonConfig.init(FabricLoader.getInstance().getConfigDir());
        
        RegistryHelper.setInstance(new FabricRegistryHelper());

        CommonClass.init();
        registerEvents();
        registerCreativeTabs();
        registerEntityAttributes();

        RegistryHelper.getInstance().flushRegistrationCallbacks();
    }

    private void registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(
            ModEntities.COPPER_GOLEM.get(),
            CommonClass.getCopperGolemAttributes()
        );
    }

    private void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            PlayerJoinHandler.onPlayerJoin(handler.getPlayer())
        );

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (player == null || level.isClientSide()) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getItemInHand(hand);
            if (!stack.is(Items.CARVED_PUMPKIN)) {
                return InteractionResult.PASS;
            }

            if (!(level instanceof ServerLevel serverLevel)) {
                return InteractionResult.PASS;
            }

            BlockPlaceContext context = new BlockPlaceContext(level, player, hand, stack, hitResult);
            BlockPos placePos = context.getClickedPos();
            Direction direction = Direction.fromYRot(player.getYRot());

            serverLevel.getServer().execute(() -> {
                if (!serverLevel.isLoaded(placePos)) {
                    return;
                }

                BlockState placedState = serverLevel.getBlockState(placePos);
                if (!placedState.is(Blocks.CARVED_PUMPKIN)) {
                    return;
                }

                CopperGolemSpawnLogic.handleBlockPlaced(serverLevel, placePos, placedState, direction);
            });

            return InteractionResult.PASS;
        });
    }

    private void registerCreativeTabs() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries ->
            entries.accept(ModItems.COPPER_GOLEM_SPAWN_EGG.get())
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(ModItems.COPPER_CHEST_ITEM.get());
            entries.accept(ModItems.EXPOSED_COPPER_CHEST_ITEM.get());
            entries.accept(ModItems.WEATHERED_COPPER_CHEST_ITEM.get());
            entries.accept(ModItems.OXIDIZED_COPPER_CHEST_ITEM.get());
            entries.accept(ModItems.WAXED_COPPER_CHEST_ITEM.get());
            entries.accept(ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get());
            entries.accept(ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get());
            entries.accept(ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get());
            entries.accept(ModItems.COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            // Copper Lanterns
            entries.accept(ModItems.COPPER_LANTERN_ITEM.get());
            entries.accept(ModItems.EXPOSED_COPPER_LANTERN_ITEM.get());
            entries.accept(ModItems.WEATHERED_COPPER_LANTERN_ITEM.get());
            entries.accept(ModItems.OXIDIZED_COPPER_LANTERN_ITEM.get());
            entries.accept(ModItems.WAXED_COPPER_LANTERN_ITEM.get());
            entries.accept(ModItems.WAXED_EXPOSED_COPPER_LANTERN_ITEM.get());
            entries.accept(ModItems.WAXED_WEATHERED_COPPER_LANTERN_ITEM.get());
            entries.accept(ModItems.WAXED_OXIDIZED_COPPER_LANTERN_ITEM.get());
            // Shelves
            entries.accept(ModItems.OAK_SHELF_ITEM.get());
            entries.accept(ModItems.BIRCH_SHELF_ITEM.get());
            entries.accept(ModItems.SPRUCE_SHELF_ITEM.get());
            entries.accept(ModItems.JUNGLE_SHELF_ITEM.get());
            entries.accept(ModItems.ACACIA_SHELF_ITEM.get());
            entries.accept(ModItems.DARK_OAK_SHELF_ITEM.get());
            entries.accept(ModItems.MANGROVE_SHELF_ITEM.get());
            entries.accept(ModItems.CHERRY_SHELF_ITEM.get());
            entries.accept(ModItems.BAMBOO_SHELF_ITEM.get());
            entries.accept(ModItems.CRIMSON_SHELF_ITEM.get());
            entries.accept(ModItems.WARPED_SHELF_ITEM.get());
            // Pale Oak Shelf - only if VanillaBackport is loaded
            if (ModItems.PALE_OAK_SHELF_ITEM != null) {
                entries.accept(ModItems.PALE_OAK_SHELF_ITEM.get());
            }
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
            entries.accept(ModItems.COPPER_BUTTON_ITEM.get());
            entries.accept(ModItems.EXPOSED_COPPER_BUTTON_ITEM.get());
            entries.accept(ModItems.WEATHERED_COPPER_BUTTON_ITEM.get());
            entries.accept(ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get());
            entries.accept(ModItems.WAXED_COPPER_BUTTON_ITEM.get());
            entries.accept(ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get());
            entries.accept(ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get());
            entries.accept(ModItems.WAXED_OXIDIZED_COPPER_BUTTON_ITEM.get());
            // Copper Torch
            entries.accept(ModItems.COPPER_TORCH_ITEM.get());
        });
        
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(ModItems.COPPER_SHOVEL.get());
            entries.accept(ModItems.COPPER_PICKAXE.get());
            entries.accept(ModItems.COPPER_AXE.get());
            entries.accept(ModItems.COPPER_HOE.get());
        });
        
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(ModItems.COPPER_SWORD.get());
            entries.accept(ModItems.COPPER_HELMET.get());
            entries.accept(ModItems.COPPER_CHESTPLATE.get());
            entries.accept(ModItems.COPPER_LEGGINGS.get());
            entries.accept(ModItems.COPPER_BOOTS.get());
        });
        
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.accept(ModItems.COPPER_NUGGET.get());
        });
    }
}
