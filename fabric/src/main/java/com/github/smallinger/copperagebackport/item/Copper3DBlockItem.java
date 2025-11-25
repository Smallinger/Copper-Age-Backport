package com.github.smallinger.copperagebackport.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * Custom BlockItem for 3D rendered items (copper chests and statues).
 * Rendering is handled by BuiltinItemRendererRegistry registration in CopperAgeBackportFabricClient.
 */
public class Copper3DBlockItem extends BlockItem {

    public Copper3DBlockItem(Block block, Properties properties) {
        super(block, properties);
    }
}

