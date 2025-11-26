package com.github.smallinger.copperagebackport.fabric.loot;

import com.github.smallinger.copperagebackport.loot.CopperHorseArmorLoot;
import com.github.smallinger.copperagebackport.registry.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * Fabric implementation for adding Copper Horse Armor to loot tables.
 * Uses Fabric API's LootTableEvents to inject items into existing loot tables.
 * 
 * Adds a new pool with Copper Horse Armor and an empty item weighted to achieve
 * the correct spawn probability matching vanilla 1.21.10 values.
 * 
 * Target spawn chances from Minecraft Wiki:
 * - Monster Room (simple_dungeon): 19.4%
 * - Desert Pyramid: 17%
 * - End City: 4.6%
 * - Jungle Pyramid: 4.4%
 * - Nether Fortress: 17.9%
 * - Stronghold (corridor): 2.5%
 * - Village Weaponsmith: 5.6%
 */
public class FabricLootTableModifier {
    
    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Only modify built-in loot tables (not data packs)
            if (!source.isBuiltin()) {
                return;
            }
            
            // Check if this is a loot table we want to modify
            if (CopperHorseArmorLoot.shouldModifyLootTable(id)) {
                // Get spawn chance weights for this loot table (out of 1000)
                int[] weights = getSpawnChanceWeights(id);
                int itemWeight = weights[0];
                int emptyWeight = weights[1];
                
                // Add a new pool with the copper horse armor and an empty entry
                // The ratio determines the spawn probability
                LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.COPPER_HORSE_ARMOR.get())
                        .setWeight(itemWeight))
                    .add(EmptyLootItem.emptyItem()
                        .setWeight(emptyWeight));
                
                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
    
    /**
     * Get the weights for copper horse armor spawn chance.
     * Returns [itemWeight, emptyWeight] where itemWeight/(itemWeight+emptyWeight) = spawn chance
     * Uses a scale of 1000 for precision.
     */
    private static int[] getSpawnChanceWeights(ResourceLocation lootTableId) {
        String path = lootTableId.getPath();
        
        // Values based on Minecraft Wiki spawn chances
        // Using scale of 1000 for precision
        if (path.equals("chests/simple_dungeon")) {
            // 19.4% chance
            return new int[] { 194, 806 };
        } else if (path.equals("chests/desert_pyramid")) {
            // 17% chance
            return new int[] { 170, 830 };
        } else if (path.equals("chests/nether_bridge")) {
            // 17.9% chance
            return new int[] { 179, 821 };
        } else if (path.equals("chests/jungle_temple")) {
            // 4.4% chance
            return new int[] { 44, 956 };
        } else if (path.equals("chests/end_city_treasure")) {
            // 4.6% chance
            return new int[] { 46, 954 };
        } else if (path.equals("chests/stronghold_corridor")) {
            // 2.5% chance
            return new int[] { 25, 975 };
        } else if (path.equals("chests/village/village_weaponsmith")) {
            // 5.6% chance
            return new int[] { 56, 944 };
        }
        
        // Default fallback (shouldn't happen)
        return new int[] { 50, 950 };
    }
}
