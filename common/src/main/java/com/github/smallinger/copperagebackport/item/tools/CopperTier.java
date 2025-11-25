package com.github.smallinger.copperagebackport.item.tools;

import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * Copper tool tier with stats between Stone and Iron.
 * Based on Minecraft 1.21.10 ToolMaterial.COPPER:
 * - Level: 1 (same as Stone, less than Iron's 2)
 * - Durability: 190 (Stone: 131, Iron: 250)
 * - Speed: 5.0 (Stone: 4.0, Iron: 6.0)
 * - Attack Damage Bonus: 1.0 (Stone: 1.0, Iron: 2.0)
 * - Enchantment Value: 13 (Stone: 5, Iron: 14)
 * - Repair: Copper Ingot
 */
public enum CopperTier implements Tier {
    INSTANCE(1, 190, 5.0F, 1.0F, 13, () -> Ingredient.of(Items.COPPER_INGOT));

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    CopperTier(int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
