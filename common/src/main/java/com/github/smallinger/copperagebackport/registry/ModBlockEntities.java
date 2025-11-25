package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.block.entity.CopperChestBlockEntity;
import com.github.smallinger.copperagebackport.block.entity.CopperGolemStatueBlockEntity;
import com.github.smallinger.copperagebackport.block.shelf.ShelfBlockEntity;
import com.github.smallinger.copperagebackport.platform.Services;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE;

/**
 * Handles registration of all block entities for the mod.
 */
public class ModBlockEntities {
    
    public static Supplier<BlockEntityType<CopperChestBlockEntity>> COPPER_CHEST_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<CopperGolemStatueBlockEntity>> COPPER_GOLEM_STATUE_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<ShelfBlockEntity>> SHELF_BLOCK_ENTITY;
    
    public static void register() {
        Constants.LOG.info("Registering block entities for {}", Constants.MOD_NAME);
        
        RegistryHelper helper = RegistryHelper.getInstance();
        
        // Register Copper Chest Block Entity
        COPPER_CHEST_BLOCK_ENTITY = helper.register(BLOCK_ENTITY_TYPE, "copper_chest",
            () -> Services.BLOCK_ENTITY.createBlockEntityType(
                CopperChestBlockEntity::new,
                ModBlocks.COPPER_CHEST.get(),
                ModBlocks.EXPOSED_COPPER_CHEST.get(),
                ModBlocks.WEATHERED_COPPER_CHEST.get(),
                ModBlocks.OXIDIZED_COPPER_CHEST.get(),
                ModBlocks.WAXED_COPPER_CHEST.get(),
                ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get(),
                ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get(),
                ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get()
            ));
        
        // Register Copper Golem Statue Block Entity
        COPPER_GOLEM_STATUE_BLOCK_ENTITY = helper.register(BLOCK_ENTITY_TYPE, "copper_golem_statue",
            () -> Services.BLOCK_ENTITY.createBlockEntityType(
                CopperGolemStatueBlockEntity::new,
                ModBlocks.COPPER_GOLEM_STATUE.get(),
                ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get(),
                ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get(),
                ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE.get(),
                ModBlocks.WAXED_COPPER_GOLEM_STATUE.get(),
                ModBlocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE.get(),
                ModBlocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE.get(),
                ModBlocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE.get()
            ));
        
        // Register Shelf Block Entity - dynamically include Pale Oak if VanillaBackport is loaded
        SHELF_BLOCK_ENTITY = helper.register(BLOCK_ENTITY_TYPE, "shelf",
            () -> {
                List<Block> shelfBlocks = new ArrayList<>();
                shelfBlocks.add(ModBlocks.OAK_SHELF.get());
                shelfBlocks.add(ModBlocks.SPRUCE_SHELF.get());
                shelfBlocks.add(ModBlocks.BIRCH_SHELF.get());
                shelfBlocks.add(ModBlocks.JUNGLE_SHELF.get());
                shelfBlocks.add(ModBlocks.ACACIA_SHELF.get());
                shelfBlocks.add(ModBlocks.DARK_OAK_SHELF.get());
                shelfBlocks.add(ModBlocks.MANGROVE_SHELF.get());
                shelfBlocks.add(ModBlocks.CHERRY_SHELF.get());
                shelfBlocks.add(ModBlocks.BAMBOO_SHELF.get());
                shelfBlocks.add(ModBlocks.CRIMSON_SHELF.get());
                shelfBlocks.add(ModBlocks.WARPED_SHELF.get());
                
                // Add Pale Oak Shelf if VanillaBackport is loaded
                if (ModBlocks.PALE_OAK_SHELF != null) {
                    shelfBlocks.add(ModBlocks.PALE_OAK_SHELF.get());
                }
                
                return Services.BLOCK_ENTITY.createBlockEntityType(
                    ShelfBlockEntity::new,
                    shelfBlocks.toArray(new Block[0])
                );
            });
    }
}
