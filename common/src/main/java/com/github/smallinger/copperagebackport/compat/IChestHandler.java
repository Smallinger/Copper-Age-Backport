package com.github.smallinger.copperagebackport.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for handling chest open/close animations for mod chests.
 * Implement this alongside IModCompatModule to add custom chest handling.
 * 
 * This is needed because many mods don't use vanilla's blockEvent system
 * for chest animations, requiring mod-specific code to trigger them.
 */
public interface IChestHandler {
    
    /**
     * Check if this handler can handle the given block state.
     * 
     * @param state The block state to check
     * @return True if this handler should process this block
     */
    boolean canHandle(BlockState state);
    
    /**
     * Open or close the chest at the given position.
     * 
     * @param level The world
     * @param pos The block position
     * @param state The current block state
     * @param open True to open, false to close
     */
    void setOpen(Level level, BlockPos pos, BlockState state, boolean open);
    
    /**
     * Check if the chest at the given position is currently open.
     * Default implementation returns false (unknown state).
     * 
     * @param level The world
     * @param pos The block position
     * @param state The current block state
     * @return True if open, false if closed or unknown
     */
    default boolean isOpen(Level level, BlockPos pos, BlockState state) {
        return false;
    }
}
