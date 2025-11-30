package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Copper Torch block that uses the custom copper fire flame particle.
 * Extends TorchBlock for compatibility with mods like Amendments.
 */
public class CopperTorchBlock extends TorchBlock {

    public CopperTorchBlock(BlockBehaviour.Properties properties) {
        super(properties, ParticleTypes.FLAME); // Dummy particle, we override animateTick
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = (double)pos.getX() + 0.5D;
        double y = (double)pos.getY() + 0.7D;
        double z = (double)pos.getZ() + 0.5D;
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(ModParticles.COPPER_FIRE_FLAME.get(), x, y, z, 0.0D, 0.0D, 0.0D);
    }
}
