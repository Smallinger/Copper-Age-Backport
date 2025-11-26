package com.github.smallinger.copperagebackport.forge.loot;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.loot.CopperHorseArmorLoot;
import com.github.smallinger.copperagebackport.registry.ModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Forge implementation for adding Copper Horse Armor to loot tables.
 * Uses Global Loot Modifiers to inject items into existing loot tables.
 * 
 * Since Global Loot Modifiers run after loot generation, we use a random
 * chance to add the item based on the target spawn probability from vanilla 1.21.10.
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
public class ForgeLootTableModifier extends LootModifier {
    
    // Registry for loot modifier serializers
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = 
        DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Constants.MOD_ID);
    
    public static final Codec<ForgeLootTableModifier> CODEC = RecordCodecBuilder.create(instance ->
        codecStart(instance).apply(instance, ForgeLootTableModifier::new)
    );
    
    public static final RegistryObject<Codec<ForgeLootTableModifier>> ADD_COPPER_HORSE_ARMOR = 
        LOOT_MODIFIERS.register("add_copper_horse_armor", () -> CODEC);

    public ForgeLootTableModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }
    
    /**
     * Register the loot modifier with the mod event bus.
     */
    public static void register(IEventBus modEventBus) {
        LOOT_MODIFIERS.register(modEventBus);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ResourceLocation lootTableId = context.getQueriedLootTableId();
        
        // Check if this loot table should have copper horse armor
        if (CopperHorseArmorLoot.shouldModifyLootTable(lootTableId)) {
            // Get the spawn chance for this loot table
            double spawnChance = getSpawnChance(lootTableId);
            
            // Use the loot context's random source for consistency
            if (context.getRandom().nextDouble() < spawnChance) {
                generatedLoot.add(new ItemStack(ModItems.COPPER_HORSE_ARMOR.get()));
            }
        }
        
        return generatedLoot;
    }
    
    /**
     * Get the spawn chance for copper horse armor in the given loot table.
     * Values are based on Minecraft Wiki spawn chances for vanilla 1.21.10.
     */
    private double getSpawnChance(ResourceLocation lootTableId) {
        String path = lootTableId.getPath();
        
        if (path.equals("chests/simple_dungeon")) return 0.194;  // 19.4%
        if (path.equals("chests/desert_pyramid")) return 0.170;  // 17%
        if (path.equals("chests/nether_bridge")) return 0.179;   // 17.9%
        if (path.equals("chests/jungle_temple")) return 0.044;   // 4.4%
        if (path.equals("chests/end_city_treasure")) return 0.046; // 4.6%
        if (path.equals("chests/stronghold_corridor")) return 0.025; // 2.5%
        if (path.equals("chests/village/village_weaponsmith")) return 0.056; // 5.6%
        
        return 0.05; // Default fallback (shouldn't happen)
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
