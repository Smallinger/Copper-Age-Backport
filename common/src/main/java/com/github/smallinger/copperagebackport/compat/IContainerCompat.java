package com.github.smallinger.copperagebackport.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

/**
 * Extended interface for mod container compatibility.
 * Combines container validation with chest handling for mods that add
 * containers the Copper Golem should be able to use as destinations.
 * 
 * Implement this interface (along with IModCompatModule) to:
 * 1. Declare which blocks from your mod are valid destinations for item transport
 * 2. Handle chest open/close animations for those containers
 * 3. Provide a Container wrapper for the Golem to interact with
 */
public interface IContainerCompat extends IChestHandler {
    
    /**
     * Check if the given block state is a valid destination container from this mod.
     * The Copper Golem will be able to deposit items into blocks that return true.
     * 
     * @param state The block state to check
     * @return True if this is a valid destination container
     */
    boolean isValidContainer(BlockState state);
    
    /**
     * Get a Container wrapper for the given block entity.
     * This is called when the Golem needs to insert/extract items.
     * 
     * For mods that don't use vanilla Container interface (like NeoForge's IItemHandler),
     * this method should return a wrapper that adapts the mod's inventory to Container.
     * 
     * @param blockEntity The block entity to get a container for
     * @param level The level
     * @param pos The block position
     * @return A Container wrapper, or null if not applicable
     */
    @Nullable
    default Container getContainer(BlockEntity blockEntity, Level level, BlockPos pos) {
        // Default implementation for mods that use vanilla Container
        if (blockEntity instanceof Container container) {
            return container;
        }
        return null;
    }
}
