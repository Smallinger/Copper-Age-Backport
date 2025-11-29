package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Base Copper Chain block - similar to vanilla ChainBlock but for copper.
 * This is used for waxed variants that don't oxidize.
 */
public class CopperChainBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    // Chain shape: 3x3 pixels in the center, full height
    protected static final VoxelShape Y_AXIS_AABB = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.5D, 6.5D, 0.0D, 9.5D, 9.5D, 16.0D);
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 6.5D, 6.5D, 16.0D, 9.5D, 9.5D);
    
    private final WeatheringCopper.WeatherState weatheringState;

    public CopperChainBlock(WeatheringCopper.WeatherState weatheringState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatheringState = weatheringState;
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(WATERLOGGED, false)
            .setValue(AXIS, Direction.Axis.Y));
    }

    public WeatheringCopper.WeatherState getWeatheringState() {
        return this.weatheringState;
    }
    
    /**
     * Gets the unwaxed variant of a waxed copper chain block.
     */
    public static Optional<Block> getUnwaxedBlock(Block block) {
        if (block == ModBlocks.WAXED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.COPPER_CHAIN.get());
        } else if (block == ModBlocks.WAXED_EXPOSED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_CHAIN.get());
        } else if (block == ModBlocks.WAXED_WEATHERED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_CHAIN.get());
        } else if (block == ModBlocks.WAXED_OXIDIZED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_CHAIN.get());
        }
        return Optional.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is using an axe on a waxed chain - dewax it
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> unwaxedBlock = getUnwaxedBlock(state.getBlock());
            
            if (unwaxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, pos, 0); // WAX_OFF particles
                
                if (!level.isClientSide) {
                    BlockState newState = unwaxedBlock.get().withPropertiesOf(state);
                    level.setBlockAndUpdate(pos, newState);
                    if (!player.isCreative()) {
                        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    }
                }
                
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(AXIS)) {
            case X:
                return X_AXIS_AABB;
            case Z:
                return Z_AXIS_AABB;
            case Y:
            default:
                return Y_AXIS_AABB;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean waterlogged = fluidstate.getType() == Fluids.WATER;
        return super.getStateForPlacement(context).setValue(WATERLOGGED, waterlogged);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, AXIS);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
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
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }
}
