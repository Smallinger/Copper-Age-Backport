package com.github.smallinger.copperagebackport.fabric.loot;

import com.github.smallinger.copperagebackport.loot.CopperHorseArmorLoot;
import com.github.smallinger.copperagebackport.registry.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * Fabric implementation for adding Copper Horse Armor to loot tables.
 * Uses Fabric API's LootTableEvents to inject items into existing loot tables.
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
                // Add copper horse armor with the same weight as iron horse armor
                LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.COPPER_HORSE_ARMOR.get())
                        .setWeight(CopperHorseArmorLoot.LOOT_WEIGHT));
                
                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}
