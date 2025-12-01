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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Base Copper Grate block - for waxed variants that don't oxidize.
 * Extends Block with waterlogging support and adds axe scraping to remove wax.
 * Uses CUTOUT render type for transparency.
 */
public class CopperGrateBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final WeatheringCopper.WeatherState weatheringState;

    public CopperGrateBlock(WeatheringCopper.WeatherState weatheringState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatheringState = weatheringState;
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    public WeatheringCopper.WeatherState getWeatheringState() {
        return this.weatheringState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.is(Fluids.WATER));
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

    /**
     * Gets the unwaxed variant of a waxed copper grate block.
     */
    public static Optional<Block> getUnwaxedBlock(Block block) {
        if (block == ModBlocks.WAXED_COPPER_GRATE.get()) {
            return Optional.of(ModBlocks.COPPER_GRATE.get());
        } else if (block == ModBlocks.WAXED_EXPOSED_COPPER_GRATE.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_GRATE.get());
        } else if (block == ModBlocks.WAXED_WEATHERED_COPPER_GRATE.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_GRATE.get());
        } else if (block == ModBlocks.WAXED_OXIDIZED_COPPER_GRATE.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_GRATE.get());
        }
        return Optional.empty();
    }

    /**
     * Copy waterlogged state from one state to another
     */
    protected BlockState copyGrateState(BlockState from, BlockState to) {
        return to.setValue(WATERLOGGED, from.getValue(WATERLOGGED));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is using an axe on a waxed grate - dewax it
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> unwaxedBlock = getUnwaxedBlock(state.getBlock());
            
            if (unwaxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, pos, 0); // WAX_OFF particles
                
                if (!level.isClientSide) {
                    BlockState newState = copyGrateState(state, unwaxedBlock.get().defaultBlockState());
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
}
