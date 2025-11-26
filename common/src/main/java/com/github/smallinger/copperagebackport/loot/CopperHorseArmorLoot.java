package com.github.smallinger.copperagebackport.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Set;

/**
 * Common loot table configuration for Copper Horse Armor.
 * This class provides shared data for all platform-specific loot modifier implementations.
 */
public class CopperHorseArmorLoot {
    
    /**
     * Loot tables that should contain Copper Horse Armor (same locations as Iron Horse Armor).
     * Based on Minecraft 1.20.1 loot tables.
     */
    public static final Set<ResourceLocation> TARGET_LOOT_TABLES = Set.of(
        new ResourceLocation("chests/simple_dungeon"),
        new ResourceLocation("chests/desert_pyramid"),
        new ResourceLocation("chests/jungle_temple"),
        new ResourceLocation("chests/nether_bridge"),
        new ResourceLocation("chests/stronghold_corridor"),
        new ResourceLocation("chests/end_city_treasure"),
        new ResourceLocation("chests/village/village_weaponsmith")
    );
    
    /**
     * Weight for Copper Horse Armor in loot tables.
     * Same as Iron Horse Armor weight in vanilla.
     */
    public static final int LOOT_WEIGHT = 15;
    
    /**
     * Checks if a loot table should have Copper Horse Armor added.
     * @param lootTableId The loot table identifier
     * @return true if this loot table should contain Copper Horse Armor
     */
    public static boolean shouldModifyLootTable(ResourceLocation lootTableId) {
        return TARGET_LOOT_TABLES.contains(lootTableId);
    }
    
    /**
     * Checks if an ItemStack is Iron Horse Armor.
     * Used to determine if Copper Horse Armor should be added alongside it.
     * @param stack The ItemStack to check
     * @return true if the stack is Iron Horse Armor
     */
    public static boolean isIronHorseArmor(ItemStack stack) {
        return stack.getItem() == Items.IRON_HORSE_ARMOR;
    }
}
