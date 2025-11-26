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
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Weathering Copper Chain block that oxidizes over time.
 */
public class WeatheringCopperChainBlock extends CopperChainBlock implements WeatheringCopper {

    public WeatheringCopperChainBlock(WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }

    /**
     * Provides our own oxidation chain for copper chains.
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_CHAIN.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_CHAIN.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_CHAIN.get());
        }
        return Optional.empty();
    }
    
    /**
     * Gets the previous oxidation stage (for scraping with axe).
     */
    public static Optional<Block> getPreviousBlock(Block block) {
        if (block == ModBlocks.EXPOSED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.COPPER_CHAIN.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_CHAIN.get());
        } else if (block == ModBlocks.OXIDIZED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_CHAIN.get());
        }
        return Optional.empty();
    }
    
    /**
     * Gets the waxed variant of a copper chain.
     */
    public static Optional<Block> getWaxedBlock(Block block) {
        if (block == ModBlocks.COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.WAXED_COPPER_CHAIN.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.WAXED_EXPOSED_COPPER_CHAIN.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.WAXED_WEATHERED_COPPER_CHAIN.get());
        } else if (block == ModBlocks.OXIDIZED_COPPER_CHAIN.get()) {
            return Optional.of(ModBlocks.WAXED_OXIDIZED_COPPER_CHAIN.get());
        }
        return Optional.empty();
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is using honeycomb to wax the chain
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxedBlock = getWaxedBlock(state.getBlock());
            
            if (waxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3003, pos, 0); // WAX_ON particles
                
                if (!level.isClientSide) {
                    BlockState newState = waxedBlock.get().withPropertiesOf(state);
                    level.setBlockAndUpdate(pos, newState);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        // Check if player is using an axe to scrape oxidation
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> previousBlock = getPreviousBlock(state.getBlock());
            
            if (previousBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0); // SCRAPE particles
                
                if (!level.isClientSide) {
                    BlockState newState = previousBlock.get().withPropertiesOf(state);
                    level.setBlockAndUpdate(pos, newState);
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return InteractionResult.PASS;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringCopperChainBlock::getNextBlock);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperChainBlock::getNextBlock);
    }

    @Override
    public WeatherState getAge() {
        return getWeatheringState();
    }
}
