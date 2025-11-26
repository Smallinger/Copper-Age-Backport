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
        
        // Use common class for loot table checks
        if (CopperHorseArmorLoot.shouldModifyLootTable(lootTableId)) {
            // Check if iron horse armor was generated, if so add copper with same probability
            boolean hasIronHorseArmor = generatedLoot.stream()
                .anyMatch(CopperHorseArmorLoot::isIronHorseArmor);
            
            if (hasIronHorseArmor) {
                // Add copper horse armor alongside iron horse armor
                generatedLoot.add(new ItemStack(ModItems.COPPER_HORSE_ARMOR.get()));
            }
        }
        
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
