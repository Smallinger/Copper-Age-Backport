package com.github.smallinger.copperagebackport.item.tools;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

/**
 * Copper Hoe item.
 * Uses CopperTier which has stats between Stone and Iron.
 */
public class CopperHoeItem extends HoeItem {
    
    public CopperHoeItem(Tier tier, int attackDamage, float attackSpeed, Item.Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
