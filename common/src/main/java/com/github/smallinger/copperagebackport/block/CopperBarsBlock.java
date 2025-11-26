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
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Base Copper Bars block - for waxed variants that don't oxidize.
 * Extends vanilla IronBarsBlock and adds axe scraping to remove wax.
 */
public class CopperBarsBlock extends IronBarsBlock {
    private final WeatheringCopper.WeatherState weatheringState;

    public CopperBarsBlock(WeatheringCopper.WeatherState weatheringState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatheringState = weatheringState;
    }

    public WeatheringCopper.WeatherState getWeatheringState() {
        return this.weatheringState;
    }

    /**
     * Gets the unwaxed variant of a waxed copper bars block.
     */
    public static Optional<Block> getUnwaxedBlock(Block block) {
        if (block == ModBlocks.WAXED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.COPPER_BARS.get());
        } else if (block == ModBlocks.WAXED_EXPOSED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_BARS.get());
        } else if (block == ModBlocks.WAXED_WEATHERED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_BARS.get());
        } else if (block == ModBlocks.WAXED_OXIDIZED_COPPER_BARS.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_BARS.get());
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
        
        // Check if player is using an axe on a waxed bars - dewax it
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> unwaxedBlock = getUnwaxedBlock(state.getBlock());
            
            if (unwaxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, pos, 0); // WAX_OFF particles
                
                if (!level.isClientSide) {
                    BlockState newState = copyBarsState(state, unwaxedBlock.get().defaultBlockState());
                    level.setBlockAndUpdate(pos, newState);
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return InteractionResult.PASS;
    }
}
