package com.github.smallinger.copperagebackport.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;

/**
 * Copper Shovel item.
 * Uses CopperTier which has stats between Stone and Iron.
 */
public class CopperShovelItem extends ShovelItem {
    
    public CopperShovelItem(Tier tier, float attackDamage, float attackSpeed, Item.Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
