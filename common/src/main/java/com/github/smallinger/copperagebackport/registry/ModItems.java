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
import net.minecraft.core.Direction;
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
    
    // Copper Grate Items (Weathering)
    public static Supplier<BlockItem> COPPER_GRATE_ITEM;
    public static Supplier<BlockItem> EXPOSED_COPPER_GRATE_ITEM;
    public static Supplier<BlockItem> WEATHERED_COPPER_GRATE_ITEM;
    public static Supplier<BlockItem> OXIDIZED_COPPER_GRATE_ITEM;
    
    // Waxed Copper Grate Items
    public static Supplier<BlockItem> WAXED_COPPER_GRATE_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_COPPER_GRATE_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_COPPER_GRATE_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_COPPER_GRATE_ITEM;
    
    public static void register() {
        Constants.LOG.info("Registering items for {}", Constants.MOD_NAME);
        
        RegistryHelper helper = RegistryHelper.getInstance();
        
        // Register Spawn Egg (platform-specific implementation)
        COPPER_GOLEM_SPAWN_EGG = helper.register(ITEM, "copper_golem_spawn_egg",
            ModItemHelper::createSpawnEggItem);
        
        // Register Copper Chest Items
        COPPER_CHEST_ITEM = helper.register(ITEM, "copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.COPPER_CHEST.get(), new Item.Properties()));
        
        EXPOSED_COPPER_CHEST_ITEM = helper.register(ITEM, "exposed_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.EXPOSED_COPPER_CHEST.get(), new Item.Properties()));
        
        WEATHERED_COPPER_CHEST_ITEM = helper.register(ITEM, "weathered_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WEATHERED_COPPER_CHEST.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_CHEST_ITEM = helper.register(ITEM, "oxidized_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.OXIDIZED_COPPER_CHEST.get(), new Item.Properties()));
        
        // Register Waxed Copper Chest Items
        WAXED_COPPER_CHEST_ITEM = helper.register(ITEM, "waxed_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_COPPER_CHEST.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_CHEST_ITEM = helper.register(ITEM, "waxed_exposed_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_CHEST_ITEM = helper.register(ITEM, "waxed_weathered_copper_chest",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_CHEST_ITEM = helper.register(ITEM, "waxed_oxidized_copper_chest",
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
        COPPER_GOLEM_STATUE_ITEM = helper.register(ITEM, "copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        EXPOSED_COPPER_GOLEM_STATUE_ITEM = helper.register(ITEM, "exposed_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        WEATHERED_COPPER_GOLEM_STATUE_ITEM = helper.register(ITEM, "weathered_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_GOLEM_STATUE_ITEM = helper.register(ITEM, "oxidized_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        // Register Waxed Copper Golem Statue Items
        WAXED_COPPER_GOLEM_STATUE_ITEM = helper.register(ITEM, "waxed_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM = helper.register(ITEM, "waxed_exposed_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM = helper.register(ITEM, "waxed_weathered_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM = helper.register(ITEM, "waxed_oxidized_copper_golem_statue",
            () -> ModItemHelper.create3DBlockItemForPlatform(ModBlocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
        
        // Register Shelf Items
        OAK_SHELF_ITEM = helper.register(ITEM, "oak_shelf",
            () -> new BlockItem(ModBlocks.OAK_SHELF.get(), new Item.Properties()));
        
        SPRUCE_SHELF_ITEM = helper.register(ITEM, "spruce_shelf",
            () -> new BlockItem(ModBlocks.SPRUCE_SHELF.get(), new Item.Properties()));
        
        BIRCH_SHELF_ITEM = helper.register(ITEM, "birch_shelf",
            () -> new BlockItem(ModBlocks.BIRCH_SHELF.get(), new Item.Properties()));
        
        JUNGLE_SHELF_ITEM = helper.register(ITEM, "jungle_shelf",
            () -> new BlockItem(ModBlocks.JUNGLE_SHELF.get(), new Item.Properties()));
        
        ACACIA_SHELF_ITEM = helper.register(ITEM, "acacia_shelf",
            () -> new BlockItem(ModBlocks.ACACIA_SHELF.get(), new Item.Properties()));
        
        DARK_OAK_SHELF_ITEM = helper.register(ITEM, "dark_oak_shelf",
            () -> new BlockItem(ModBlocks.DARK_OAK_SHELF.get(), new Item.Properties()));
        
        MANGROVE_SHELF_ITEM = helper.register(ITEM, "mangrove_shelf",
            () -> new BlockItem(ModBlocks.MANGROVE_SHELF.get(), new Item.Properties()));
        
        CHERRY_SHELF_ITEM = helper.register(ITEM, "cherry_shelf",
            () -> new BlockItem(ModBlocks.CHERRY_SHELF.get(), new Item.Properties()));
        
        BAMBOO_SHELF_ITEM = helper.register(ITEM, "bamboo_shelf",
            () -> new BlockItem(ModBlocks.BAMBOO_SHELF.get(), new Item.Properties()));
        
        CRIMSON_SHELF_ITEM = helper.register(ITEM, "crimson_shelf",
            () -> new BlockItem(ModBlocks.CRIMSON_SHELF.get(), new Item.Properties()));
        
        WARPED_SHELF_ITEM = helper.register(ITEM, "warped_shelf",
            () -> new BlockItem(ModBlocks.WARPED_SHELF.get(), new Item.Properties()));
        
        // Pale Oak Shelf Item - only register if VanillaBackport is loaded
        if (Services.PLATFORM.isModLoaded("vanillabackport")) {
            PALE_OAK_SHELF_ITEM = helper.register(ITEM, "pale_oak_shelf",
                () -> new BlockItem(ModBlocks.PALE_OAK_SHELF.get(), new Item.Properties()));
        }
        
        // Register Copper Torch Item
        COPPER_TORCH_ITEM = helper.register(ITEM, "copper_torch",
            () -> new StandingAndWallBlockItem(
                ModBlocks.COPPER_TORCH.get(),
                ModBlocks.COPPER_WALL_TORCH.get(),
                new Item.Properties(),
                Direction.DOWN));
        
        // Register Copper Lantern Items
        COPPER_LANTERN_ITEM = helper.register(ITEM, "copper_lantern",
            () -> new BlockItem(ModBlocks.COPPER_LANTERN.get(), new Item.Properties()));
        
        EXPOSED_COPPER_LANTERN_ITEM = helper.register(ITEM, "exposed_copper_lantern",
            () -> new BlockItem(ModBlocks.EXPOSED_COPPER_LANTERN.get(), new Item.Properties()));
        
        WEATHERED_COPPER_LANTERN_ITEM = helper.register(ITEM, "weathered_copper_lantern",
            () -> new BlockItem(ModBlocks.WEATHERED_COPPER_LANTERN.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_LANTERN_ITEM = helper.register(ITEM, "oxidized_copper_lantern",
            () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_LANTERN.get(), new Item.Properties()));
        
        // Register Waxed Copper Lantern Items
        WAXED_COPPER_LANTERN_ITEM = helper.register(ITEM, "waxed_copper_lantern",
            () -> new BlockItem(ModBlocks.WAXED_COPPER_LANTERN.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_LANTERN_ITEM = helper.register(ITEM, "waxed_exposed_copper_lantern",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_LANTERN.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_LANTERN_ITEM = helper.register(ITEM, "waxed_weathered_copper_lantern",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_LANTERN.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_LANTERN_ITEM = helper.register(ITEM, "waxed_oxidized_copper_lantern",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_LANTERN.get(), new Item.Properties()));
        
        // Register Copper Chain Items
        COPPER_CHAIN_ITEM = helper.register(ITEM, "copper_chain",
            () -> new BlockItem(ModBlocks.COPPER_CHAIN.get(), new Item.Properties()));
        
        EXPOSED_COPPER_CHAIN_ITEM = helper.register(ITEM, "exposed_copper_chain",
            () -> new BlockItem(ModBlocks.EXPOSED_COPPER_CHAIN.get(), new Item.Properties()));
        
        WEATHERED_COPPER_CHAIN_ITEM = helper.register(ITEM, "weathered_copper_chain",
            () -> new BlockItem(ModBlocks.WEATHERED_COPPER_CHAIN.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_CHAIN_ITEM = helper.register(ITEM, "oxidized_copper_chain",
            () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_CHAIN.get(), new Item.Properties()));
        
        // Register Waxed Copper Chain Items
        WAXED_COPPER_CHAIN_ITEM = helper.register(ITEM, "waxed_copper_chain",
            () -> new BlockItem(ModBlocks.WAXED_COPPER_CHAIN.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_CHAIN_ITEM = helper.register(ITEM, "waxed_exposed_copper_chain",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_CHAIN.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_CHAIN_ITEM = helper.register(ITEM, "waxed_weathered_copper_chain",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_CHAIN.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_CHAIN_ITEM = helper.register(ITEM, "waxed_oxidized_copper_chain",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_CHAIN.get(), new Item.Properties()));
        
        // Register Copper Grate Items
        COPPER_GRATE_ITEM = helper.register(ITEM, "copper_grate",
            () -> new BlockItem(ModBlocks.COPPER_GRATE.get(), new Item.Properties()));
        
        EXPOSED_COPPER_GRATE_ITEM = helper.register(ITEM, "exposed_copper_grate",
            () -> new BlockItem(ModBlocks.EXPOSED_COPPER_GRATE.get(), new Item.Properties()));
        
        WEATHERED_COPPER_GRATE_ITEM = helper.register(ITEM, "weathered_copper_grate",
            () -> new BlockItem(ModBlocks.WEATHERED_COPPER_GRATE.get(), new Item.Properties()));
        
        OXIDIZED_COPPER_GRATE_ITEM = helper.register(ITEM, "oxidized_copper_grate",
            () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_GRATE.get(), new Item.Properties()));
        
        // Register Waxed Copper Grate Items
        WAXED_COPPER_GRATE_ITEM = helper.register(ITEM, "waxed_copper_grate",
            () -> new BlockItem(ModBlocks.WAXED_COPPER_GRATE.get(), new Item.Properties()));
        
        WAXED_EXPOSED_COPPER_GRATE_ITEM = helper.register(ITEM, "waxed_exposed_copper_grate",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_GRATE.get(), new Item.Properties()));
        
        WAXED_WEATHERED_COPPER_GRATE_ITEM = helper.register(ITEM, "waxed_weathered_copper_grate",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_GRATE.get(), new Item.Properties()));
        
        WAXED_OXIDIZED_COPPER_GRATE_ITEM = helper.register(ITEM, "waxed_oxidized_copper_grate",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_GRATE.get(), new Item.Properties()));
        
        // Register Copper Tools
        // Copper Axe: 6.0 base + 1.0 material bonus = 7.0 attack damage, -3.2 attack speed
        // In 1.20.1: base damage for axe is 6.0F, attack speed is -3.2F
        COPPER_AXE = helper.register(ITEM, "copper_axe",
            () -> new CopperAxeItem(CopperTier.INSTANCE, 6.0F, -3.2F, new Item.Properties().stacksTo(1)));
        
        // Copper Pickaxe: 1.0 attack damage, -2.8 attack speed
        // In 1.20.1: PickaxeItem takes int for attackDamage
        COPPER_PICKAXE = helper.register(ITEM, "copper_pickaxe",
            () -> new CopperPickaxeItem(CopperTier.INSTANCE, 1, -2.8F, new Item.Properties().stacksTo(1)));
        
        // Copper Shovel: 1.5 attack damage, -3.0 attack speed
        COPPER_SHOVEL = helper.register(ITEM, "copper_shovel",
            () -> new CopperShovelItem(CopperTier.INSTANCE, 1.5F, -3.0F, new Item.Properties().stacksTo(1)));
        
        // Copper Hoe: -2 attack damage, -1.0 attack speed (fast)
        COPPER_HOE = helper.register(ITEM, "copper_hoe",
            () -> new CopperHoeItem(CopperTier.INSTANCE, -2, -1.0F, new Item.Properties().stacksTo(1)));
        
        // Copper Sword: 3 attack damage, -2.4 attack speed
        COPPER_SWORD = helper.register(ITEM, "copper_sword",
            () -> new CopperSwordItem(CopperTier.INSTANCE, 3, -2.4F, new Item.Properties().stacksTo(1)));
        
        // Copper Nugget
        COPPER_NUGGET = helper.register(ITEM, "copper_nugget",
            () -> new Item(new Item.Properties()));
        
        // Copper Armor
        COPPER_HELMET = helper.register(ITEM, "copper_helmet",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)));
        
        COPPER_CHESTPLATE = helper.register(ITEM, "copper_chestplate",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));
        
        COPPER_LEGGINGS = helper.register(ITEM, "copper_leggings",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)));
        
        COPPER_BOOTS = helper.register(ITEM, "copper_boots",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)));
    }
}
