package com.github.smallinger.copperagebackport.platform;

import com.github.smallinger.copperagebackport.platform.services.IBlockEntityHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Forge implementation of IBlockEntityHelper.
 */
public class ForgeBlockEntityHelper implements IBlockEntityHelper {
    
    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Block... blocks) {
        // Forge can use method references directly because it has access to BlockEntitySupplier
        return BlockEntityType.Builder.of(factory::create, blocks).build(null);
    }
}
