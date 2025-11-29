package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
     * Get the unwaxed (weathering) version of this block for axe scraping
     */
    public Optional<Block> getUnwaxedBlock() {
        return switch (this.weatheringState) {
            case UNAFFECTED -> Optional.of(ModBlocks.COPPER_BARS.get());
            case EXPOSED -> Optional.of(ModBlocks.EXPOSED_COPPER_BARS.get());
            case WEATHERED -> Optional.of(ModBlocks.WEATHERED_COPPER_BARS.get());
            case OXIDIZED -> Optional.of(ModBlocks.OXIDIZED_COPPER_BARS.get());
        };
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
        // Handle axe scraping to remove wax
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> unwaxed = getUnwaxedBlock();
            if (unwaxed.isPresent()) {
                BlockState newState = copyBarsState(state, unwaxed.get().defaultBlockState());
                
                level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, pos, 0); // WAX_OFF particles
                
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
