package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.item.tools.CopperAxeItem;
import com.github.smallinger.copperagebackport.item.tools.CopperHoeItem;
import com.github.smallinger.copperagebackport.item.tools.CopperPickaxeItem;
import com.github.smallinger.copperagebackport.item.tools.CopperShovelItem;
import com.github.smallinger.copperagebackport.item.tools.CopperSwordItem;
import com.github.smallinger.copperagebackport.item.tools.CopperTier;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.github.smallinger.copperagebackport.platform.Services;

import java.util.function.Supplier;

import static net.minecraft.core.registries.Registries.ITEM;

/**
 * Handles registration of all items for the mod.
 */
public class ModItems {
    
    // Spawn Egg
    public static Supplier<Item> COPPER_GOLEM_SPAWN_EGG;
    
    // Copper Tools
    public static Supplier<AxeItem> COPPER_AXE;
    public static Supplier<PickaxeItem> COPPER_PICKAXE;
    public static Supplier<ShovelItem> COPPER_SHOVEL;
    public static Supplier<HoeItem> COPPER_HOE;
    public static Supplier<SwordItem> COPPER_SWORD;
    
    // Copper Materials
    public static Supplier<Item> COPPER_NUGGET;
    
    // Copper Armor
    public static Supplier<ArmorItem> COPPER_HELMET;
    public static Supplier<ArmorItem> COPPER_CHESTPLATE;
    public static Supplier<ArmorItem> COPPER_LEGGINGS;
    public static Supplier<ArmorItem> COPPER_BOOTS;
    public static Supplier<Item> COPPER_HORSE_ARMOR;
    
    // Copper Chest Items
    public static Supplier<BlockItem> COPPER_CHEST_ITEM;
    public static Supplier<BlockItem> EXPOSED_COPPER_CHEST_ITEM;
    public static Supplier<BlockItem> WEATHERED_COPPER_CHEST_ITEM;
    public static Supplier<BlockItem> OXIDIZED_COPPER_CHEST_ITEM;
    
    // Waxed Copper Chest Items
    public static Supplier<BlockItem> WAXED_COPPER_CHEST_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_COPPER_CHEST_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_COPPER_CHEST_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_COPPER_CHEST_ITEM;
    
    // Copper Button Items
    public static Supplier<BlockItem> COPPER_BUTTON_ITEM;
    public static Supplier<BlockItem> EXPOSED_COPPER_BUTTON_ITEM;
    public static Supplier<BlockItem> WEATHERED_COPPER_BUTTON_ITEM;
    public static Supplier<BlockItem> OXIDIZED_COPPER_BUTTON_ITEM;
    
    // Waxed Copper Button Items
    public static Supplier<BlockItem> WAXED_COPPER_BUTTON_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_COPPER_BUTTON_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_COPPER_BUTTON_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_COPPER_BUTTON_ITEM;
    
    // Copper Golem Statue Items
    public static Supplier<BlockItem> COPPER_GOLEM_STATUE_ITEM;
    public static Supplier<BlockItem> EXPOSED_COPPER_GOLEM_STATUE_ITEM;
    public static Supplier<BlockItem> WEATHERED_COPPER_GOLEM_STATUE_ITEM;
    public static Supplier<BlockItem> OXIDIZED_COPPER_GOLEM_STATUE_ITEM;
    
    // Waxed Copper Golem Statue Items
    public static Supplier<BlockItem> WAXED_COPPER_GOLEM_STATUE_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM;
    
    // Shelf Items
    public static Supplier<BlockItem> OAK_SHELF_ITEM;
    public static Supplier<BlockItem> SPRUCE_SHELF_ITEM;
    public static Supplier<BlockItem> BIRCH_SHELF_ITEM;
    public static Supplier<BlockItem> JUNGLE_SHELF_ITEM;
    public static Supplier<BlockItem> ACACIA_SHELF_ITEM;
    public static Supplier<BlockItem> DARK_OAK_SHELF_ITEM;
    public static Supplier<BlockItem> MANGROVE_SHELF_ITEM;
    public static Supplier<BlockItem> CHERRY_SHELF_ITEM;
    public static Supplier<BlockItem> BAMBOO_SHELF_ITEM;
    public static Supplier<BlockItem> CRIMSON_SHELF_ITEM;
    public static Supplier<BlockItem> WARPED_SHELF_ITEM;
    public static Supplier<BlockItem> PALE_OAK_SHELF_ITEM; // Requires VanillaBackport for crafting
    
    // Copper Torch Item
    public static Supplier<StandingAndWallBlockItem> COPPER_TORCH_ITEM;
    
    // Copper Lantern Items
    public static Supplier<BlockItem> COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> EXPOSED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> WEATHERED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> OXIDIZED_COPPER_LANTERN_ITEM;
    
    // Waxed Copper Lantern Items
    public static Supplier<BlockItem> WAXED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_COPPER_LANTERN_ITEM;
    
    // Copper Chain Items
    public static Supplier<BlockItem> COPPER_CHAIN_ITEM;
    public static Supplier<BlockItem> EXPOSED_COPPER_CHAIN_ITEM;
    public static Supplier<BlockItem> WEATHERED_COPPER_CHAIN_ITEM;
    public static Supplier<BlockItem> OXIDIZED_COPPER_CHAIN_ITEM;
    
    // Waxed Copper Chain Items
    public static Supplier<BlockItem> WAXED_COPPER_CHAIN_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_COPPER_CHAIN_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_COPPER_CHAIN_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_COPPER_CHAIN_ITEM;
    
    // Copper Bars Items (Weathering)
    public static Supplier<BlockItem> COPPER_BARS_ITEM;
    public static Supplier<BlockItem> EXPOSED_COPPER_BARS_ITEM;
    public static Supplier<BlockItem> WEATHERED_COPPER_BARS_ITEM;
    public static Supplier<BlockItem> OXIDIZED_COPPER_BARS_ITEM;
    
    // Waxed Copper Bars Items
    public static Supplier<BlockItem> WAXED_COPPER_BARS_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_COPPER_BARS_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_COPPER_BARS_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_COPPER_BARS_ITEM;
    
    // Copper Lightning Rod Items (Weathering)
    // Note: Base lightning_rod item is vanilla
    public static Supplier<BlockItem> EXPOSED_LIGHTNING_ROD_ITEM;
    public static Supplier<BlockItem> WEATHERED_LIGHTNING_ROD_ITEM;
    public static Supplier<BlockItem> OXIDIZED_LIGHTNING_ROD_ITEM;
    
    // Waxed Copper Lightning Rod Items
    public static Supplier<BlockItem> WAXED_LIGHTNING_ROD_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_LIGHTNING_ROD_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_LIGHTNING_ROD_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_LIGHTNING_ROD_ITEM;
    
    // Copper Trapdoor Items (Weathering)
    public static Supplier<BlockItem> COPPER_TRAPDOOR_ITEM;
    public static Supplier<BlockItem> EXPOSED_COPPER_TRAPDOOR_ITEM;
    public static Supplier<BlockItem> WEATHERED_COPPER_TRAPDOOR_ITEM;
    public static Supplier<BlockItem> OXIDIZED_COPPER_TRAPDOOR_ITEM;
    
    // Waxed Copper Trapdoor Items
    public static Supplier<BlockItem> WAXED_COPPER_TRAPDOOR_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_COPPER_TRAPDOOR_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_COPPER_TRAPDOOR_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_COPPER_TRAPDOOR_ITEM;

    public static void register(){
        Constants.LOG.info("Registering items for {}", Constants.MOD_NAME);
        
        RegistryHelper helper = RegistryHelper.getInstance();
        
        // Register Spawn Egg (platform-specific implementation)
        COPPER_GOLEM_SPAWN_EGG = helper.registerAuto(ITEM, "copper_golem_spawn_egg",
            ModItemHelper::createSpawnEggItem);
        
        // Register Copper Chest Items
        COPPER_CHEST_ITEM = helper.registerAuto(ITEM, "copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.COPPER_CHEST.get(), new Item.Properties()));
        
        EXPOSED_COPPER_CHEST_ITEM = helper.registerAuto(ITEM, "exposed_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.EXPOSED_COPPER_CHEST.get(), new Item.Properties()));
        
        WEATHERED_COPPER_CHEST_ITEM = helper.registerAuto(ITEM, "weathered_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WEATHERED_COPPER_CHEST.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_CHEST_ITEM = helper.registerAuto(ITEM, "oxidized_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.OXIDIZED_COPPER_CHEST.get(), new Item.Properties()));
        
        // Register Waxed Copper Chest Items
        WAXED_COPPER_CHEST_ITEM = helper.registerAuto(ITEM, "waxed_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_COPPER_CHEST.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_CHEST_ITEM = helper.registerAuto(ITEM, "waxed_exposed_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_CHEST_ITEM = helper.registerAuto(ITEM, "waxed_weathered_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_CHEST_ITEM = helper.registerAuto(ITEM, "waxed_oxidized_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get(), new Item.Properties()));
        
        // Register Copper Button Items
        COPPER_BUTTON_ITEM = helper.register(ITEM, "copper_button",
            () -> new BlockItem(ModBlocks.COPPER_BUTTON.get(), new Item.Properties()));
        
        EXPOSED_COPPER_BUTTON_ITEM = helper.register(ITEM, "exposed_copper_button",
            () -> new BlockItem(ModBlocks.EXPOSED_COPPER_BUTTON.get(), new Item.Properties()));
        
        WEATHERED_COPPER_BUTTON_ITEM = helper.register(ITEM, "weathered_copper_button",
            () -> new BlockItem(ModBlocks.WEATHERED_COPPER_BUTTON.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_BUTTON_ITEM = helper.register(ITEM, "oxidized_copper_button",
            () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_BUTTON.get(), new Item.Properties()));
        
        // Register Waxed Copper Button Items
        WAXED_COPPER_BUTTON_ITEM = helper.register(ITEM, "waxed_copper_button",
            () -> new BlockItem(ModBlocks.WAXED_COPPER_BUTTON.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_BUTTON_ITEM = helper.register(ITEM, "waxed_exposed_copper_button",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_BUTTON_ITEM = helper.register(ITEM, "waxed_weathered_copper_button",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_BUTTON_ITEM = helper.register(ITEM, "waxed_oxidized_copper_button",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON.get(), new Item.Properties()));
        
        // Register Copper Golem Statue Items
        COPPER_GOLEM_STATUE_ITEM = helper.registerAuto(ITEM, "copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        EXPOSED_COPPER_GOLEM_STATUE_ITEM = helper.registerAuto(ITEM, "exposed_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        WEATHERED_COPPER_GOLEM_STATUE_ITEM = helper.registerAuto(ITEM, "weathered_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_GOLEM_STATUE_ITEM = helper.registerAuto(ITEM, "oxidized_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        // Register Waxed Copper Golem Statue Items
        WAXED_COPPER_GOLEM_STATUE_ITEM = helper.registerAuto(ITEM, "waxed_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM = helper.registerAuto(ITEM, "waxed_exposed_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM = helper.registerAuto(ITEM, "waxed_weathered_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM = helper.registerAuto(ITEM, "waxed_oxidized_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        // Register Shelf Items
        OAK_SHELF_ITEM = helper.registerAuto(ITEM, "oak_shelf",
            () -> new BlockItem(ModBlocks.OAK_SHELF.get(), new Item.Properties()));
        
        SPRUCE_SHELF_ITEM = helper.registerAuto(ITEM, "spruce_shelf",
            () -> new BlockItem(ModBlocks.SPRUCE_SHELF.get(), new Item.Properties()));
        
        BIRCH_SHELF_ITEM = helper.registerAuto(ITEM, "birch_shelf",
            () -> new BlockItem(ModBlocks.BIRCH_SHELF.get(), new Item.Properties()));
        
        JUNGLE_SHELF_ITEM = helper.registerAuto(ITEM, "jungle_shelf",
            () -> new BlockItem(ModBlocks.JUNGLE_SHELF.get(), new Item.Properties()));
        
        ACACIA_SHELF_ITEM = helper.registerAuto(ITEM, "acacia_shelf",
            () -> new BlockItem(ModBlocks.ACACIA_SHELF.get(), new Item.Properties()));
        
        DARK_OAK_SHELF_ITEM = helper.registerAuto(ITEM, "dark_oak_shelf",
            () -> new BlockItem(ModBlocks.DARK_OAK_SHELF.get(), new Item.Properties()));
        
        MANGROVE_SHELF_ITEM = helper.registerAuto(ITEM, "mangrove_shelf",
            () -> new BlockItem(ModBlocks.MANGROVE_SHELF.get(), new Item.Properties()));
        
        CHERRY_SHELF_ITEM = helper.registerAuto(ITEM, "cherry_shelf",
            () -> new BlockItem(ModBlocks.CHERRY_SHELF.get(), new Item.Properties()));
        
        BAMBOO_SHELF_ITEM = helper.registerAuto(ITEM, "bamboo_shelf",
            () -> new BlockItem(ModBlocks.BAMBOO_SHELF.get(), new Item.Properties()));
        
        CRIMSON_SHELF_ITEM = helper.registerAuto(ITEM, "crimson_shelf",
            () -> new BlockItem(ModBlocks.CRIMSON_SHELF.get(), new Item.Properties()));
        
        WARPED_SHELF_ITEM = helper.registerAuto(ITEM, "warped_shelf",
            () -> new BlockItem(ModBlocks.WARPED_SHELF.get(), new Item.Properties()));
        PALE_OAK_SHELF_ITEM = helper.registerAuto(ITEM, "pale_oak_shelf",
            () -> new BlockItem(ModBlocks.PALE_OAK_SHELF.get(), new Item.Properties()));
        
        // Register Copper Torch Item
        COPPER_TORCH_ITEM = helper.registerAuto(ITEM, "copper_torch",
            () -> new StandingAndWallBlockItem(
                ModBlocks.COPPER_TORCH.get(),
                ModBlocks.COPPER_WALL_TORCH.get(),
                new Item.Properties(),
                Direction.DOWN));
        
        // Register Copper Lantern Items
        COPPER_LANTERN_ITEM = helper.registerAuto(ITEM, "copper_lantern",
            () -> new BlockItem(ModBlocks.COPPER_LANTERN.get(), new Item.Properties()));
        
        EXPOSED_COPPER_LANTERN_ITEM = helper.registerAuto(ITEM, "exposed_copper_lantern",
            () -> new BlockItem(ModBlocks.EXPOSED_COPPER_LANTERN.get(), new Item.Properties()));
        
        WEATHERED_COPPER_LANTERN_ITEM = helper.registerAuto(ITEM, "weathered_copper_lantern",
            () -> new BlockItem(ModBlocks.WEATHERED_COPPER_LANTERN.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_LANTERN_ITEM = helper.registerAuto(ITEM, "oxidized_copper_lantern",
            () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_LANTERN.get(), new Item.Properties()));
        
        // Register Waxed Copper Lantern Items
        WAXED_COPPER_LANTERN_ITEM = helper.registerAuto(ITEM, "waxed_copper_lantern",
            () -> new BlockItem(ModBlocks.WAXED_COPPER_LANTERN.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_LANTERN_ITEM = helper.registerAuto(ITEM, "waxed_exposed_copper_lantern",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_LANTERN.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_LANTERN_ITEM = helper.registerAuto(ITEM, "waxed_weathered_copper_lantern",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_LANTERN.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_LANTERN_ITEM = helper.registerAuto(ITEM, "waxed_oxidized_copper_lantern",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_LANTERN.get(), new Item.Properties()));
        
        // Register Copper Chain Items
        COPPER_CHAIN_ITEM = helper.registerAuto(ITEM, "copper_chain",
            () -> new BlockItem(ModBlocks.COPPER_CHAIN.get(), new Item.Properties()));
        
        EXPOSED_COPPER_CHAIN_ITEM = helper.registerAuto(ITEM, "exposed_copper_chain",
            () -> new BlockItem(ModBlocks.EXPOSED_COPPER_CHAIN.get(), new Item.Properties()));
        
        WEATHERED_COPPER_CHAIN_ITEM = helper.registerAuto(ITEM, "weathered_copper_chain",
            () -> new BlockItem(ModBlocks.WEATHERED_COPPER_CHAIN.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_CHAIN_ITEM = helper.registerAuto(ITEM, "oxidized_copper_chain",
            () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_CHAIN.get(), new Item.Properties()));
        
        // Register Waxed Copper Chain Items
        WAXED_COPPER_CHAIN_ITEM = helper.registerAuto(ITEM, "waxed_copper_chain",
            () -> new BlockItem(ModBlocks.WAXED_COPPER_CHAIN.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_CHAIN_ITEM = helper.registerAuto(ITEM, "waxed_exposed_copper_chain",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_CHAIN.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_CHAIN_ITEM = helper.registerAuto(ITEM, "waxed_weathered_copper_chain",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_CHAIN.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_CHAIN_ITEM = helper.registerAuto(ITEM, "waxed_oxidized_copper_chain",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_CHAIN.get(), new Item.Properties()));
        
        // Register Copper Bars Items (Weathering)
        COPPER_BARS_ITEM = helper.registerAuto(ITEM, "copper_bars",
            () -> new BlockItem(ModBlocks.COPPER_BARS.get(), new Item.Properties()));
        
        EXPOSED_COPPER_BARS_ITEM = helper.registerAuto(ITEM, "exposed_copper_bars",
            () -> new BlockItem(ModBlocks.EXPOSED_COPPER_BARS.get(), new Item.Properties()));
        
        WEATHERED_COPPER_BARS_ITEM = helper.registerAuto(ITEM, "weathered_copper_bars",
            () -> new BlockItem(ModBlocks.WEATHERED_COPPER_BARS.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_BARS_ITEM = helper.registerAuto(ITEM, "oxidized_copper_bars",
            () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_BARS.get(), new Item.Properties()));
        
        // Register Waxed Copper Bars Items
        WAXED_COPPER_BARS_ITEM = helper.registerAuto(ITEM, "waxed_copper_bars",
            () -> new BlockItem(ModBlocks.WAXED_COPPER_BARS.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_BARS_ITEM = helper.registerAuto(ITEM, "waxed_exposed_copper_bars",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_BARS.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_BARS_ITEM = helper.registerAuto(ITEM, "waxed_weathered_copper_bars",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_BARS.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_BARS_ITEM = helper.registerAuto(ITEM, "waxed_oxidized_copper_bars",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_BARS.get(), new Item.Properties()));

        // Register Copper Lightning Rod Items
        // Note: Base lightning_rod item is vanilla
        EXPOSED_LIGHTNING_ROD_ITEM = helper.registerAuto(ITEM, "exposed_lightning_rod",
            () -> new BlockItem(ModBlocks.EXPOSED_LIGHTNING_ROD.get(), new Item.Properties()));
        
        WEATHERED_LIGHTNING_ROD_ITEM = helper.registerAuto(ITEM, "weathered_lightning_rod",
            () -> new BlockItem(ModBlocks.WEATHERED_LIGHTNING_ROD.get(), new Item.Properties()));
        
        OXIDIZED_LIGHTNING_ROD_ITEM = helper.registerAuto(ITEM, "oxidized_lightning_rod",
            () -> new BlockItem(ModBlocks.OXIDIZED_LIGHTNING_ROD.get(), new Item.Properties()));
        
        // Register Waxed Copper Lightning Rod Items
        WAXED_LIGHTNING_ROD_ITEM = helper.registerAuto(ITEM, "waxed_lightning_rod",
            () -> new BlockItem(ModBlocks.WAXED_LIGHTNING_ROD.get(), new Item.Properties()));
        
        WAXED_EXPOSED_LIGHTNING_ROD_ITEM = helper.registerAuto(ITEM, "waxed_exposed_lightning_rod",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_LIGHTNING_ROD.get(), new Item.Properties()));
        
        WAXED_WEATHERED_LIGHTNING_ROD_ITEM = helper.registerAuto(ITEM, "waxed_weathered_lightning_rod",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_LIGHTNING_ROD.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_LIGHTNING_ROD_ITEM = helper.registerAuto(ITEM, "waxed_oxidized_lightning_rod",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_LIGHTNING_ROD.get(), new Item.Properties()));
        
        // Register Copper Trapdoor Items (Weathering)
        COPPER_TRAPDOOR_ITEM = helper.registerAuto(ITEM, "copper_trapdoor",
            () -> new BlockItem(ModBlocks.COPPER_TRAPDOOR.get(), new Item.Properties()));
        
        EXPOSED_COPPER_TRAPDOOR_ITEM = helper.registerAuto(ITEM, "exposed_copper_trapdoor",
            () -> new BlockItem(ModBlocks.EXPOSED_COPPER_TRAPDOOR.get(), new Item.Properties()));
        
        WEATHERED_COPPER_TRAPDOOR_ITEM = helper.registerAuto(ITEM, "weathered_copper_trapdoor",
            () -> new BlockItem(ModBlocks.WEATHERED_COPPER_TRAPDOOR.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_TRAPDOOR_ITEM = helper.registerAuto(ITEM, "oxidized_copper_trapdoor",
            () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_TRAPDOOR.get(), new Item.Properties()));
        
        // Register Waxed Copper Trapdoor Items
        WAXED_COPPER_TRAPDOOR_ITEM = helper.registerAuto(ITEM, "waxed_copper_trapdoor",
            () -> new BlockItem(ModBlocks.WAXED_COPPER_TRAPDOOR.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_TRAPDOOR_ITEM = helper.registerAuto(ITEM, "waxed_exposed_copper_trapdoor",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_TRAPDOOR_ITEM = helper.registerAuto(ITEM, "waxed_weathered_copper_trapdoor",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_TRAPDOOR_ITEM = helper.registerAuto(ITEM, "waxed_oxidized_copper_trapdoor",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR.get(), new Item.Properties()));

        // Register Copper Tools
        // Copper Axe: 7.0 base + 1.0 material bonus + 1 player base = 9 attack damage, -3.2 attack speed (official MC 1.21.10 values)
        COPPER_AXE = helper.registerAuto(ITEM, "copper_axe",
            () -> new CopperAxeItem(CopperTier.INSTANCE, 7.0F, -3.2F, new Item.Properties().stacksTo(1)));
        
        // Copper Pickaxe: 1.0 attack damage, -2.8 attack speed
        // In 1.20.1: PickaxeItem takes int for attackDamage
        COPPER_PICKAXE = helper.registerAuto(ITEM, "copper_pickaxe",
            () -> new CopperPickaxeItem(CopperTier.INSTANCE, 1, -2.8F, new Item.Properties().stacksTo(1)));
        
        // Copper Shovel: 1.5 attack damage, -3.0 attack speed
        COPPER_SHOVEL = helper.registerAuto(ITEM, "copper_shovel",
            () -> new CopperShovelItem(CopperTier.INSTANCE, 1.5F, -3.0F, new Item.Properties().stacksTo(1)));
        
        // Copper Hoe: -1 attack damage, -2.0 attack speed (official MC 1.21.10 values)
        COPPER_HOE = helper.registerAuto(ITEM, "copper_hoe",
            () -> new CopperHoeItem(CopperTier.INSTANCE, -1, -2.0F, new Item.Properties().stacksTo(1)));
        
        // Copper Sword: 3 attack damage, -2.4 attack speed
        COPPER_SWORD = helper.registerAuto(ITEM, "copper_sword",
            () -> new CopperSwordItem(CopperTier.INSTANCE, 3, -2.4F, new Item.Properties().stacksTo(1)));
        
        // Copper Nugget
        COPPER_NUGGET = helper.registerAuto(ITEM, "copper_nugget",
            () -> new Item(new Item.Properties()));
        
        // Copper Armor
        COPPER_HELMET = helper.registerAuto(ITEM, "copper_helmet",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)));
        
        COPPER_CHESTPLATE = helper.registerAuto(ITEM, "copper_chestplate",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));
        
        COPPER_LEGGINGS = helper.registerAuto(ITEM, "copper_leggings",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)));
        
        COPPER_BOOTS = helper.registerAuto(ITEM, "copper_boots",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)));
        
        // Copper Horse Armor: 4 protection (between leather 3 and iron 5)
        // 1.20.1 uses custom CopperHorseArmorItem because vanilla HorseArmorItem only supports minecraft namespace
        // -> overrides getTexture() to return: copperagebackport:textures/entity/horse/armor/horse_armor_copper.png
        // Note: 1.21.1 uses AnimalArmorItem which derives texture from ArmorMaterial key automatically
        COPPER_HORSE_ARMOR = helper.registerAuto(ITEM, "copper_horse_armor",
            () -> new com.github.smallinger.copperagebackport.item.CopperHorseArmorItem(4, new Item.Properties().stacksTo(1)));
    }
}
