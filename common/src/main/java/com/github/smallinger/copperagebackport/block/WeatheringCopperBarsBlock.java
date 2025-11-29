package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Weathering Copper Bars block - oxidizes over time and can be waxed or scraped.
 * Based on vanilla IronBarsBlock but with copper weathering mechanics.
 */
public class WeatheringCopperBarsBlock extends IronBarsBlock implements WeatheringCopper {
    private final WeatherState weatherState;

    public WeatheringCopperBarsBlock(WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    /**
     * Provides our own oxidation chain for copper bars.
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_BARS.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_BARS.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_BARS.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_BARS.get());
        }
        return Optional.empty();
    }

    /**
     * Gets the previous oxidation stage (for scraping with axe).
     */
    public static Optional<Block> getPreviousBlock(Block block) {
        if (block == ModBlocks.EXPOSED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.COPPER_BARS.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_BARS.get());
        } else if (block == ModBlocks.OXIDIZED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_BARS.get());
        }
        return Optional.empty();
    }

    /**
     * Gets the waxed variant of a copper bars.
     */
    public static Optional<Block> getWaxedBlock(Block block) {
        if (block == ModBlocks.COPPER_BARS.get()) {
            return Optional.of(ModBlocks.WAXED_COPPER_BARS.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.WAXED_EXPOSED_COPPER_BARS.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.WAXED_WEATHERED_COPPER_BARS.get());
        } else if (block == ModBlocks.OXIDIZED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.WAXED_OXIDIZED_COPPER_BARS.get());
        }
        return Optional.empty();
    }

    /**
     * Copy all bar-related properties from one state to another
     */
    private BlockState copyBarsState(BlockState from, BlockState to) {
        return to.setValue(NORTH, from.getValue(NORTH))
                 .setValue(SOUTH, from.getValue(SOUTH))
                 .setValue(EAST, from.getValue(EAST))
                 .setValue(WEST, from.getValue(WEST))
                 .setValue(WATERLOGGED, from.getValue(WATERLOGGED));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Handle honeycomb waxing
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxedBlock = getWaxedBlock(state.getBlock());
            
            if (waxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3003, pos, 0); // WAX_ON particles
                
                if (!level.isClientSide) {
                    BlockState newState = copyBarsState(state, waxedBlock.get().defaultBlockState());
                    level.setBlockAndUpdate(pos, newState);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        // Handle axe scraping (reduce oxidation level)
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> previousBlock = getPreviousBlock(state.getBlock());
            
            if (previousBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0); // SCRAPE particles
                
                if (!level.isClientSide) {
                    BlockState newState = copyBarsState(state, previousBlock.get().defaultBlockState());
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
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringCopperBarsBlock::getNextBlock);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return getNextBlock(state.getBlock()).isPresent();
    }

    @Override
    public Optional<BlockState> getNext(BlockState state) {
        return getNextBlock(state.getBlock()).map(block -> copyBarsState(state, block.defaultBlockState()));
    }
}
