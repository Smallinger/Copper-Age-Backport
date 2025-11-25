package com.github.smallinger.copperagebackport.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/**
 * Copper Sword item.
 * Uses CopperTier which has stats between Stone and Iron.
 */
public class CopperSwordItem extends SwordItem {
    
    public CopperSwordItem(Tier tier, int attackDamage, float attackSpeed, Item.Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
