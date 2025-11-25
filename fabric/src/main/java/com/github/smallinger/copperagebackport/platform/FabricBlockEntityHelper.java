package com.github.smallinger.copperagebackport.platform;

import com.github.smallinger.copperagebackport.platform.services.IBlockEntityHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Fabric implementation of IBlockEntityHelper.
 */
public class FabricBlockEntityHelper implements IBlockEntityHelper {

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Block... blocks) {
        // Fabric also uses method references
        return BlockEntityType.Builder.of(factory::create, blocks).build(null);
    }
}

