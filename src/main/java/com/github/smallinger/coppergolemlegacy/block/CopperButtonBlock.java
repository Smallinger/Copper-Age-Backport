package com.github.smallinger.coppergolemlegacy.block;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.util.WeatheringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;
import java.util.function.Supplier;

public class CopperButtonBlock extends ButtonBlock implements WeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;
    private Supplier<WaxedCopperButtonBlock> waxedButton;

    public CopperButtonBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
        super(properties, BlockSetType.IRON, 15, true); // Properties first in 1.20.1
        this.weatherState = weatherState;
    }

    public void setWaxedButton(Supplier<WaxedCopperButtonBlock> waxedButton) {
        this.waxedButton = waxedButton;
    }
    
    /**
     * Override to provide our own oxidation chain since we can't modify the static BiMap
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == CopperGolemLegacy.COPPER_BUTTON.get()) {
            return Optional.of(CopperGolemLegacy.EXPOSED_COPPER_BUTTON.get());
        } else if (block == CopperGolemLegacy.EXPOSED_COPPER_BUTTON.get()) {
            return Optional.of(CopperGolemLegacy.WEATHERED_COPPER_BUTTON.get());
        } else if (block == CopperGolemLegacy.WEATHERED_COPPER_BUTTON.get()) {
            return Optional.of(CopperGolemLegacy.OXIDIZED_COPPER_BUTTON.get());
        }
        return WeatheringCopper.getNext(block);
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        // Check if player is using honeycomb to wax the button
        if (stack.is(Items.HONEYCOMB) && waxedButton != null) {
            level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3003, pos, 0);
            
            if (!level.isClientSide) {
                // Replace with waxed version, preserving button state
                BlockState waxedState = waxedButton.get().defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(POWERED, state.getValue(POWERED))
                    .setValue(FACE, state.getValue(FACE));
                level.setBlock(pos, waxedState, 11);
                
                if (player != null && !player.isCreative()) {
                    stack.shrink(1);
                }
            }
            
            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        
        // Check if player is using an axe to scrape oxidation
        if (stack.is(ItemTags.AXES)) {
            Block previousBlock = null;
            
            // Determine the previous oxidation state
            if (this == CopperGolemLegacy.OXIDIZED_COPPER_BUTTON.get()) {
                previousBlock = CopperGolemLegacy.WEATHERED_COPPER_BUTTON.get();
            } else if (this == CopperGolemLegacy.WEATHERED_COPPER_BUTTON.get()) {
                previousBlock = CopperGolemLegacy.EXPOSED_COPPER_BUTTON.get();
            } else if (this == CopperGolemLegacy.EXPOSED_COPPER_BUTTON.get()) {
                previousBlock = CopperGolemLegacy.COPPER_BUTTON.get();
            }
            
            if (previousBlock != null) {
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0);
                
                if (!level.isClientSide) {
                    // Replace with previous oxidation state, preserving button state
                    BlockState newState = previousBlock.defaultBlockState()
                        .setValue(FACING, state.getValue(FACING))
                        .setValue(POWERED, state.getValue(POWERED))
                        .setValue(FACE, state.getValue(FACE));
                    level.setBlock(pos, newState, 11);
                    
                    if (player != null && !player.isCreative()) {
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }
                }
                
                return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }
        }
        
        return InteractionResult.PASS;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, CopperButtonBlock::getNextBlock);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, CopperButtonBlock::getNextBlock);
    }
}
