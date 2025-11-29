package com.github.smallinger.copperagebackport.compat;

import net.minecraft.world.level.block.Block;

import java.util.Set;

/**
 * Interface for mods that modify block rendering (like FastChest).
 * Implement this alongside IModCompatModule to handle renderer compatibility.
 * 
 * This is needed for mods like FastChest that replace chest renderers
 * with static block models - we need to ensure our copper chests are
 * included in their renderer modifications.
 */
public interface IRendererCompat {
    
    /**
     * Get all blocks that should use static/fast rendering.
     * These blocks will be registered with the mod's fast renderer system.
     * 
     * @return Set of blocks to register for fast rendering
     */
    Set<Block> getFastRenderBlocks();
    
    /**
     * Check if fast rendering is currently enabled for the given block.
     * 
     * @param block The block to check
     * @return True if fast rendering is active for this block
     */
    boolean isFastRenderEnabled(Block block);
}
