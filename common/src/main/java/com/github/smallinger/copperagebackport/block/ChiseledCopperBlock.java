package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Chiseled Copper block (waxed/non-oxidizing version).
 * Used for waxed chiseled copper variants that can be scraped to unwaxed version.
 */
public class ChiseledCopperBlock extends Block {
    private final WeatheringCopper.WeatherState weatherState;

    public ChiseledCopperBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }

    /**
     * Gets the unwaxed version of this block (for scraping with axe).
     */
    public static Optional<Block> getUnwaxedBlock(Block block) {
        if (block == ModBlocks.WAXED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.CHISELED_COPPER.get());
        } else if (block == ModBlocks.WAXED_EXPOSED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.EXPOSED_CHISELED_COPPER.get());
        } else if (block == ModBlocks.WAXED_WEATHERED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.WEATHERED_CHISELED_COPPER.get());
        } else if (block == ModBlocks.WAXED_OXIDIZED_CHISELED_COPPER.get()) {
            return Optional.of(ModBlocks.OXIDIZED_CHISELED_COPPER.get());
        }
        return Optional.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Handle scraping with axe (removes wax)
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> unwaxedBlock = getUnwaxedBlock(state.getBlock());
            if (unwaxedBlock.isPresent()) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, unwaxedBlock.get().withPropertiesOf(state));
                    level.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return InteractionResult.PASS;
    }
}
