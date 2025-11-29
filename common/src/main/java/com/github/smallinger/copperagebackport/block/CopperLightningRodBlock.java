package com.github.smallinger.copperagebackport.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Base class for Copper Lightning Rod blocks.
 * Extends RodBlock like vanilla Lightning Rod but allows for weathering variants.
 */
public class CopperLightningRodBlock extends RodBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int ACTIVATION_TICKS = 8;
    public static final int RANGE = 128;
    private static final int SPARK_CYCLE = 200;

    public CopperLightningRodBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.UP)
            .setValue(WATERLOGGED, false)
            .setValue(POWERED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return this.defaultBlockState()
            .setValue(FACING, context.getClickedFace())
            .setValue(WATERLOGGED, flag);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, 
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) && state.getValue(FACING) == direction ? 15 : 0;
    }

    /**
     * Called when lightning strikes this rod
     */
    public void onLightningStrike(BlockState state, Level level, BlockPos pos) {
        level.setBlock(pos, state.setValue(POWERED, true), 3);
        this.updateNeighbours(state, level, pos);
        level.scheduleTick(pos, this, ACTIVATION_TICKS);
        level.levelEvent(3002, pos, state.getValue(FACING).getAxis().ordinal());
    }

    private void updateNeighbours(BlockState state, Level level, BlockPos pos) {
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, state.setValue(POWERED, false), 3);
        this.updateNeighbours(state, level, pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.isThundering() 
            && (long)level.random.nextInt(SPARK_CYCLE) <= level.getGameTime() % SPARK_CYCLE 
            && pos.getY() == level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) - 1) {
            ParticleUtils.spawnParticlesAlongAxis(
                state.getValue(FACING).getAxis(), 
                level, 
                pos, 
                0.125, 
                ParticleTypes.ELECTRIC_SPARK, 
                UniformInt.of(1, 2));
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (state.getValue(POWERED)) {
                this.updateNeighbours(state, level, pos);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!state.is(oldState.getBlock()) && state.getValue(POWERED) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.setBlock(pos, state.setValue(POWERED, false), 18);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, WATERLOGGED);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }
}
