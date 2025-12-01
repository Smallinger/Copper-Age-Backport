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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Weathering Chiseled Copper block - oxidizes over time and can be waxed or scraped.
 * A decorative full copper block that follows vanilla oxidation mechanics.
 */
public class WeatheringChiseledCopperBlock extends Block implements WeatheringCopper {
    private final WeatherState weatherState;

    public WeatheringChiseledCopperBlock(WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    /**
     * Provides our own oxidation chain for chiseled copper.
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.EXPOSED_CHISELED_COPPER.get());
        } else if (block == ModBlocks.EXPOSED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.WEATHERED_CHISELED_COPPER.get());
        } else if (block == ModBlocks.WEATHERED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.OXIDIZED_CHISELED_COPPER.get());
        }
        return Optional.empty();
    }

    /**
     * Gets the previous oxidation stage (for scraping with axe).
     */
    public static Optional<Block> getPreviousBlock(Block block) {
        if (block == ModBlocks.EXPOSED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.CHISELED_COPPER.get());
        } else if (block == ModBlocks.WEATHERED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.EXPOSED_CHISELED_COPPER.get());
        } else if (block == ModBlocks.OXIDIZED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.WEATHERED_CHISELED_COPPER.get());
        }
        return Optional.empty();
    }

    /**
     * Gets the waxed version of this block.
     */
    public static Optional<Block> getWaxedBlock(Block block) {
        if (block == ModBlocks.CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.WAXED_CHISELED_COPPER.get());
        } else if (block == ModBlocks.EXPOSED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.WAXED_EXPOSED_CHISELED_COPPER.get());
        } else if (block == ModBlocks.WEATHERED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.WAXED_WEATHERED_CHISELED_COPPER.get());
        } else if (block == ModBlocks.OXIDIZED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.WAXED_OXIDIZED_CHISELED_COPPER.get());
        }
        return Optional.empty();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringChiseledCopperBlock::getNextBlock);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringChiseledCopperBlock::getNextBlock);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Handle waxing with honeycomb
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxedBlock = getWaxedBlock(state.getBlock());
            if (waxedBlock.isPresent()) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, waxedBlock.get().withPropertiesOf(state));
                    level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        // Handle scraping with axe
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> previousBlock = getPreviousBlock(state.getBlock());
            if (previousBlock.isPresent()) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, previousBlock.get().withPropertiesOf(state));
                    level.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return InteractionResult.PASS;
    }
}
