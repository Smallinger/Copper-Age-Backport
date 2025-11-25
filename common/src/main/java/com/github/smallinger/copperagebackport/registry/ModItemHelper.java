package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.platform.Services;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Platform-specific item helper.
 * The actual implementation is provided via Service Loader pattern.
 */
public interface ModItemHelper {
    
    /**
     * Creates a spawn egg for the Copper Golem.
     * Implementation varies between Forge and Fabric.
     */
    Item createSpawnEgg();
    
    /**
     * Creates a 3D rendered block item (for chests and statues).
     * Implementation varies between Forge (IClientItemExtensions) and Fabric (BuiltinItemRendererRegistry).
     */
    BlockItem create3DBlockItem(Block block, Item.Properties properties);
    
    /**
     * Gets the platform-specific implementation.
     */
    static Item createSpawnEggItem() {
        return Services.ITEM_HELPER.createSpawnEgg();
    }
    
    /**
     * Gets the platform-specific 3D block item.
     */
    static BlockItem create3DBlockItemForPlatform(Block block, Item.Properties properties) {
        return Services.ITEM_HELPER.create3DBlockItem(block, properties);
    }
}
