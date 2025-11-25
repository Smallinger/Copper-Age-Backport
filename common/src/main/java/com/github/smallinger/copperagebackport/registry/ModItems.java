package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.item.tools.CopperTier;
import com.github.smallinger.copperagebackport.platform.Services;
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
    public static Supplier<BlockItem> BIRCH_SHELF_ITEM;
    public static Supplier<BlockItem> SPRUCE_SHELF_ITEM;
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
    
    // Copper Lantern Items (Weathering)
    public static Supplier<BlockItem> COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> EXPOSED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> WEATHERED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> OXIDIZED_COPPER_LANTERN_ITEM;
    
    // Waxed Copper Lantern Items
    public static Supplier<BlockItem> WAXED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> WAXED_EXPOSED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> WAXED_WEATHERED_COPPER_LANTERN_ITEM;
    public static Supplier<BlockItem> WAXED_OXIDIZED_COPPER_LANTERN_ITEM;

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
        
        BIRCH_SHELF_ITEM = helper.register(ITEM, "birch_shelf",
            () -> new BlockItem(ModBlocks.BIRCH_SHELF.get(), new Item.Properties()));
        
        SPRUCE_SHELF_ITEM = helper.register(ITEM, "spruce_shelf",
            () -> new BlockItem(ModBlocks.SPRUCE_SHELF.get(), new Item.Properties()));
        
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

        // Register Copper Lantern Items (Weathering)
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

        // Register Copper Tools
        // Copper Axe: 7.0 base attack damage + 1.0 material bonus = 8 total, -3.2 attack speed
        COPPER_AXE = helper.register(ITEM, "copper_axe",
            () -> new AxeItem(CopperTier.INSTANCE, new Item.Properties().attributes(AxeItem.createAttributes(CopperTier.INSTANCE, 7.0F, -3.2F))));
        
        // Copper Pickaxe: 1.0 base attack damage, -2.8 attack speed
        COPPER_PICKAXE = helper.register(ITEM, "copper_pickaxe",
            () -> new PickaxeItem(CopperTier.INSTANCE, new Item.Properties().attributes(PickaxeItem.createAttributes(CopperTier.INSTANCE, 1.0F, -2.8F))));
        
        // Copper Shovel: 1.5 base attack damage, -3.0 attack speed
        COPPER_SHOVEL = helper.register(ITEM, "copper_shovel",
            () -> new ShovelItem(CopperTier.INSTANCE, new Item.Properties().attributes(ShovelItem.createAttributes(CopperTier.INSTANCE, 1.5F, -3.0F))));
        
        // Copper Hoe: -2.0 base attack damage, -1.0 attack speed (fast)
        COPPER_HOE = helper.register(ITEM, "copper_hoe",
            () -> new HoeItem(CopperTier.INSTANCE, new Item.Properties().attributes(HoeItem.createAttributes(CopperTier.INSTANCE, -2.0F, -1.0F))));
        
        // Copper Sword: 3.0 base attack damage, -2.4 attack speed
        COPPER_SWORD = helper.register(ITEM, "copper_sword",
            () -> new SwordItem(CopperTier.INSTANCE, new Item.Properties().attributes(SwordItem.createAttributes(CopperTier.INSTANCE, 3, -2.4F))));
        
        // Copper Nugget
        COPPER_NUGGET = helper.register(ITEM, "copper_nugget",
            () -> new Item(new Item.Properties()));
        
        // Copper Armor
        COPPER_HELMET = helper.register(ITEM, "copper_helmet",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER.get(), ArmorItem.Type.HELMET, new Item.Properties()));
        
        COPPER_CHESTPLATE = helper.register(ITEM, "copper_chestplate",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER.get(), ArmorItem.Type.CHESTPLATE, new Item.Properties()));
        
        COPPER_LEGGINGS = helper.register(ITEM, "copper_leggings",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER.get(), ArmorItem.Type.LEGGINGS, new Item.Properties()));
        
        COPPER_BOOTS = helper.register(ITEM, "copper_boots",
            () -> new ArmorItem(com.github.smallinger.copperagebackport.item.armor.CopperArmorMaterial.COPPER.get(), ArmorItem.Type.BOOTS, new Item.Properties()));
    }
}
