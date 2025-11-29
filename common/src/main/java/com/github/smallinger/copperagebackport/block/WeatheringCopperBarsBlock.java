package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
     * Get the next oxidation stage block
     */
    public Optional<Block> getNextBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.of(ModBlocks.EXPOSED_COPPER_BARS.get());
            case EXPOSED -> Optional.of(ModBlocks.WEATHERED_COPPER_BARS.get());
            case WEATHERED -> Optional.of(ModBlocks.OXIDIZED_COPPER_BARS.get());
            case OXIDIZED -> Optional.empty();
        };
    }

    /**
     * Get the previous oxidation stage block (for axe scraping)
     */
    public Optional<Block> getPreviousBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.empty();
            case EXPOSED -> Optional.of(ModBlocks.COPPER_BARS.get());
            case WEATHERED -> Optional.of(ModBlocks.EXPOSED_COPPER_BARS.get());
            case OXIDIZED -> Optional.of(ModBlocks.WEATHERED_COPPER_BARS.get());
        };
    }

    /**
     * Get the waxed version of this block
     */
    public Optional<Block> getWaxedBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.of(ModBlocks.WAXED_COPPER_BARS.get());
            case EXPOSED -> Optional.of(ModBlocks.WAXED_EXPOSED_COPPER_BARS.get());
            case WEATHERED -> Optional.of(ModBlocks.WAXED_WEATHERED_COPPER_BARS.get());
            case OXIDIZED -> Optional.of(ModBlocks.WAXED_OXIDIZED_COPPER_BARS.get());
        };
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    public Optional<BlockState> getNext(BlockState state) {
        return getNextBlock().map(block -> copyBarsState(state, block.defaultBlockState()));
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Handle honeycomb waxing
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxed = getWaxedBlock();
            if (waxed.isPresent()) {
                BlockState newState = copyBarsState(state, waxed.get().defaultBlockState());
                
                level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3003, pos, 0); // WAX_ON particles
                
                if (!level.isClientSide) {
                    level.setBlock(pos, newState, 11);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        // Handle axe scraping (reduce oxidation level)
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> previous = getPreviousBlock();
            if (previous.isPresent()) {
                BlockState newState = copyBarsState(state, previous.get().defaultBlockState());
                
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0); // SCRAPE particles
                
                if (!level.isClientSide) {
                    level.setBlock(pos, newState, 11);
                    if (!player.isCreative()) {
                        stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                    }
                }
                
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
