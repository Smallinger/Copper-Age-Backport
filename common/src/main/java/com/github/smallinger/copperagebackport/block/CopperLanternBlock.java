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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
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
 * Base Copper Lantern block - similar to vanilla LanternBlock but for copper.
 * This is used for waxed variants that don't oxidize.
 * Extends LanternBlock for automatic Amendments mod compatibility (wall placement, falling behavior).
 */
public class CopperLanternBlock extends LanternBlock implements WeatheringCopper {
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    // Same shape as vanilla lantern
    protected static final VoxelShape AABB = Shapes.or(
        Block.box(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D),
        Block.box(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D)
    );
    protected static final VoxelShape HANGING_AABB = Shapes.or(
        Block.box(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D),
        Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D)
    );
    
    private final WeatheringCopper.WeatherState weatheringState;

    public CopperLanternBlock(WeatheringCopper.WeatherState weatheringState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatheringState = weatheringState;
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(HANGING, false)
            .setValue(WATERLOGGED, false));
    }

    public WeatheringCopper.WeatherState getWeatheringState() {
        return this.weatheringState;
    }

    @Override
    public WeatherState getAge() {
        return this.weatheringState;
    }
    
    /**
     * Gets the unwaxed variant of a waxed copper lantern block.
     */
    public static Optional<Block> getUnwaxedBlock(Block block) {
        if (block == ModBlocks.WAXED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.COPPER_LANTERN.get());
        } else if (block == ModBlocks.WAXED_EXPOSED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_LANTERN.get());
        } else if (block == ModBlocks.WAXED_WEATHERED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_LANTERN.get());
        } else if (block == ModBlocks.WAXED_OXIDIZED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_LANTERN.get());
        }
        return Optional.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is using an axe on a waxed lantern - dewax it
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState blockstate = this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
                }
            }
        }

        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? HANGING_AABB : AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING, WATERLOGGED);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite();
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }

    protected static Direction getConnectedDirection(BlockState state) {
        return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return getConnectedDirection(state).getOpposite() == direction && !state.canSurvive(level, pos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
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
