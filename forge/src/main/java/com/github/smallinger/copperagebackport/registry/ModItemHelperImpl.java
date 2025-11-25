package com.github.smallinger.copperagebackport.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;

/**
 * Forge implementation of ModItemHelper.
 */
public class ModItemHelperImpl implements ModItemHelper {
    
    @Override
    public Item createSpawnEgg() {
        return new ForgeSpawnEggItem(
            () -> ModEntities.COPPER_GOLEM.get(),
            0xB87333, // Primary color (copper)
            0x48D1CC, // Secondary color (oxidized copper/cyan)
            new Item.Properties()
        );
    }
    
    @Override
    public BlockItem create3DBlockItem(Block block, Item.Properties properties) {
        return new com.github.smallinger.copperagebackport.forge.item.Copper3DBlockItem(block, properties);
    }
}
