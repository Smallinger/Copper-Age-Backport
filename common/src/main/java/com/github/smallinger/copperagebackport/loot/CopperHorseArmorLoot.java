package com.github.smallinger.copperagebackport.loot;

import net.minecraft.resources.ResourceLocation;

import java.util.Set;

/**
 * Common loot table configuration for Copper Horse Armor.
 * Defines which loot tables should contain Copper Horse Armor.
 * 
 * Spawn chances (implemented in platform-specific modifiers) based on Minecraft Wiki:
 * - Monster Room (simple_dungeon): 19.4%
 * - Desert Pyramid: 17%
 * - End City: 4.6%
 * - Jungle Pyramid: 4.4%
 * - Nether Fortress: 17.9%
 * - Stronghold (corridor): 2.5%
 * - Village Weaponsmith: 5.6%
 */
public class CopperHorseArmorLoot {
    
    /**
     * Loot tables that should contain Copper Horse Armor.
     */
    private static final Set<ResourceLocation> TARGET_LOOT_TABLES = Set.of(
        new ResourceLocation("chests/simple_dungeon"),
        new ResourceLocation("chests/desert_pyramid"),
        new ResourceLocation("chests/nether_bridge"),
        new ResourceLocation("chests/jungle_temple"),
        new ResourceLocation("chests/stronghold_corridor"),
        new ResourceLocation("chests/end_city_treasure"),
        new ResourceLocation("chests/village/village_weaponsmith")
    );
    
    /**
     * Checks if a loot table should have Copper Horse Armor added.
     * @param lootTableId The loot table identifier
     * @return true if this loot table should contain Copper Horse Armor
     */
    public static boolean shouldModifyLootTable(ResourceLocation lootTableId) {
        return TARGET_LOOT_TABLES.contains(lootTableId);
    }
}
