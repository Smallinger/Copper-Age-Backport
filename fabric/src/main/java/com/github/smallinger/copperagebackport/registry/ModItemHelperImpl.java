package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.item.Copper3DBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

/**
 * Fabric implementation of ModItemHelper.
 */
public class ModItemHelperImpl implements ModItemHelper {

    @Override
    public Item createSpawnEgg() {
        return new SpawnEggItem(
            ModEntities.COPPER_GOLEM.get(),
            0xB87333, // Primary color (copper)
            0x48D1CC, // Secondary color (oxidized copper/cyan)
            new Item.Properties()
        );
    }

    @Override
    public BlockItem create3DBlockItem(Block block, Item.Properties properties) {
        return new Copper3DBlockItem(block, properties);
    }
}

