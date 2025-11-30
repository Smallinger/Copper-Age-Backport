package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Copper Wall Torch block that uses the custom copper fire flame particle.
 * Extends WallTorchBlock for compatibility with mods like Amendments.
 */
public class CopperWallTorchBlock extends WallTorchBlock {

    public CopperWallTorchBlock(BlockBehaviour.Properties properties) {
        super(ParticleTypes.FLAME, properties); // Dummy particle, we override animateTick
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        Direction direction = state.getValue(FACING);
        double x = (double)pos.getX() + 0.5D;
        double y = (double)pos.getY() + 0.7D;
        double z = (double)pos.getZ() + 0.5D;
        double offsetX = 0.27D;
        double offsetY = 0.22D;
        Direction direction1 = direction.getOpposite();
        level.addParticle(ParticleTypes.SMOKE, x + offsetX * (double)direction1.getStepX(), y + offsetY, z + offsetX * (double)direction1.getStepZ(), 0.0D, 0.0D, 0.0D);
        level.addParticle(ModParticles.COPPER_FIRE_FLAME.get(), x + offsetX * (double)direction1.getStepX(), y + offsetY, z + offsetX * (double)direction1.getStepZ(), 0.0D, 0.0D, 0.0D);
    }
}
