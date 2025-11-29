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
        
        // Register loot table modifications
        com.github.smallinger.copperagebackport.fabric.loot.FabricLootTableModifier.register();

        RegistryHelper.getInstance().flushRegistrationCallbacks();
        
        // Legacy namespace migration is handled by Mixins (IdRemapMixin, PlayerDataRemapMixin)
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
            // Copper Chests after normal chest
            entries.addAfter(Items.CHEST, ModItems.COPPER_CHEST_ITEM.get(), ModItems.EXPOSED_COPPER_CHEST_ITEM.get(), ModItems.WEATHERED_COPPER_CHEST_ITEM.get(), ModItems.OXIDIZED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get());
            // Lightning Rods after vanilla lightning rod
            entries.addAfter(Items.LIGHTNING_ROD, ModItems.EXPOSED_LIGHTNING_ROD_ITEM.get(), ModItems.WEATHERED_LIGHTNING_ROD_ITEM.get(), ModItems.OXIDIZED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_EXPOSED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_WEATHERED_LIGHTNING_ROD_ITEM.get(), ModItems.WAXED_OXIDIZED_LIGHTNING_ROD_ITEM.get());
            // Copper Golem Statues (keep at end)
            entries.accept(ModItems.COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            entries.accept(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            // Copper Lanterns after soul lantern
            entries.addAfter(Items.SOUL_LANTERN, ModItems.COPPER_LANTERN_ITEM.get(), ModItems.EXPOSED_COPPER_LANTERN_ITEM.get(), ModItems.WEATHERED_COPPER_LANTERN_ITEM.get(), ModItems.OXIDIZED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_LANTERN_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_LANTERN_ITEM.get());
            // Copper Torch after soul torch
            entries.addAfter(Items.SOUL_TORCH, ModItems.COPPER_TORCH_ITEM.get());
            // Shelves after chiseled bookshelf
            entries.addAfter(Items.CHISELED_BOOKSHELF, ModItems.OAK_SHELF_ITEM.get(), ModItems.SPRUCE_SHELF_ITEM.get(), ModItems.BIRCH_SHELF_ITEM.get(), ModItems.JUNGLE_SHELF_ITEM.get(), ModItems.ACACIA_SHELF_ITEM.get(), ModItems.DARK_OAK_SHELF_ITEM.get(), ModItems.MANGROVE_SHELF_ITEM.get(), ModItems.CHERRY_SHELF_ITEM.get(), ModItems.BAMBOO_SHELF_ITEM.get(), ModItems.CRIMSON_SHELF_ITEM.get(), ModItems.WARPED_SHELF_ITEM.get());
            // Pale Oak Shelf - only if VanillaBackport is loaded
            if (ModItems.PALE_OAK_SHELF_ITEM != null) {
                entries.addAfter(ModItems.WARPED_SHELF_ITEM.get(), ModItems.PALE_OAK_SHELF_ITEM.get());
            }
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
            // Copper Buttons after stone button
            entries.addAfter(Items.STONE_BUTTON, ModItems.COPPER_BUTTON_ITEM.get(), ModItems.EXPOSED_COPPER_BUTTON_ITEM.get(), ModItems.WEATHERED_COPPER_BUTTON_ITEM.get(), ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_BUTTON_ITEM.get());
        });
        
        // Add copper tools after stone tools
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.addAfter(Items.STONE_HOE, ModItems.COPPER_SHOVEL.get(), ModItems.COPPER_PICKAXE.get(), ModItems.COPPER_AXE.get(), ModItems.COPPER_HOE.get());
        });
        
        // Add copper sword after stone sword, copper armor after chainmail armor
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.addAfter(Items.STONE_SWORD, ModItems.COPPER_SWORD.get());
            entries.addAfter(Items.CHAINMAIL_BOOTS, ModItems.COPPER_HELMET.get(), ModItems.COPPER_CHESTPLATE.get(), ModItems.COPPER_LEGGINGS.get(), ModItems.COPPER_BOOTS.get());
            // Add copper horse armor after leather horse armor (before iron horse armor)
            entries.addAfter(Items.LEATHER_HORSE_ARMOR, ModItems.COPPER_HORSE_ARMOR.get());
        });
        
        // Add copper nugget after iron nugget
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.IRON_NUGGET, ModItems.COPPER_NUGGET.get());
        });
        
        // Add copper bars and chains after cut copper slabs in building blocks
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
            // After cut copper slab
            entries.addAfter(Items.CUT_COPPER_SLAB, ModItems.COPPER_BARS_ITEM.get(), ModItems.COPPER_CHAIN_ITEM.get());
            // After exposed cut copper slab
            entries.addAfter(Items.EXPOSED_CUT_COPPER_SLAB, ModItems.EXPOSED_COPPER_BARS_ITEM.get(), ModItems.EXPOSED_COPPER_CHAIN_ITEM.get());
            // After weathered cut copper slab
            entries.addAfter(Items.WEATHERED_CUT_COPPER_SLAB, ModItems.WEATHERED_COPPER_BARS_ITEM.get(), ModItems.WEATHERED_COPPER_CHAIN_ITEM.get());
            // After oxidized cut copper slab
            entries.addAfter(Items.OXIDIZED_CUT_COPPER_SLAB, ModItems.OXIDIZED_COPPER_BARS_ITEM.get(), ModItems.OXIDIZED_COPPER_CHAIN_ITEM.get());
            // After waxed cut copper slab
            entries.addAfter(Items.WAXED_CUT_COPPER_SLAB, ModItems.WAXED_COPPER_BARS_ITEM.get(), ModItems.WAXED_COPPER_CHAIN_ITEM.get());
            // After waxed exposed cut copper slab
            entries.addAfter(Items.WAXED_EXPOSED_CUT_COPPER_SLAB, ModItems.WAXED_EXPOSED_COPPER_BARS_ITEM.get(), ModItems.WAXED_EXPOSED_COPPER_CHAIN_ITEM.get());
            // After waxed weathered cut copper slab
            entries.addAfter(Items.WAXED_WEATHERED_CUT_COPPER_SLAB, ModItems.WAXED_WEATHERED_COPPER_BARS_ITEM.get(), ModItems.WAXED_WEATHERED_COPPER_CHAIN_ITEM.get());
            // After waxed oxidized cut copper slab
            entries.addAfter(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB, ModItems.WAXED_OXIDIZED_COPPER_BARS_ITEM.get(), ModItems.WAXED_OXIDIZED_COPPER_CHAIN_ITEM.get());
        });
    }
}
