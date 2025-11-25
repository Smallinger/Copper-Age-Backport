package com.github.smallinger.copperagebackport.platform.services;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Platform-specific helper for creating BlockEntityTypes.
 * This is needed because BlockEntityType.BlockEntitySupplier is package-private in Minecraft 1.20.1.
 */
public interface IBlockEntityHelper {
    
    /**
     * Creates a BlockEntityType for the given blocks.
     * 
     * @param factory The factory function that creates block entities
     * @param blocks The blocks this entity type is valid for
     * @return A new BlockEntityType
     */
    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Block... blocks);
    
    /**
     * Factory interface for creating block entities.
     */
    @FunctionalInterface
    interface BlockEntityFactory<T extends BlockEntity> {
        T create(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state);
    }
}
