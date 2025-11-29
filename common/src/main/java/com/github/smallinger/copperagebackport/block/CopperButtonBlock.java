package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
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
        super(BlockSetType.COPPER, 15, properties);
        this.weatherState = weatherState;
    }

    public void setWaxedButton(Supplier<WaxedCopperButtonBlock> waxedButton) {
        this.waxedButton = waxedButton;
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }
    
    /**
     * Get the next oxidation stage
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_BUTTON.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_BUTTON.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_BUTTON.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_BUTTON.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_BUTTON.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_BUTTON.get());
        }
        return WeatheringCopper.getNext(block);
    }
    
    /**
     * Get the previous oxidation stage for scraping
     */
    public static Optional<Block> getPreviousBlock(Block block) {
        if (block == ModBlocks.OXIDIZED_COPPER_BUTTON.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_BUTTON.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_BUTTON.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_BUTTON.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_BUTTON.get()) {
            return Optional.of(ModBlocks.COPPER_BUTTON.get());
        }
        return Optional.empty();
    }
    
    /**
     * Get the waxed version of this block
     */
    private Optional<Block> getWaxedBlock(Block block) {
        if (waxedButton == null) {
            return Optional.empty();
        }
        return Optional.of(waxedButton.get());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
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
                
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }
            
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        
        // Check if player is using an axe to scrape oxidation
        if (stack.is(ItemTags.AXES)) {
            Block previousBlock = null;
            
            // Determine the previous oxidation state
            if (this == ModBlocks.OXIDIZED_COPPER_BUTTON.get()) {
                previousBlock = ModBlocks.WEATHERED_COPPER_BUTTON.get();
            } else if (this == ModBlocks.WEATHERED_COPPER_BUTTON.get()) {
                previousBlock = ModBlocks.EXPOSED_COPPER_BUTTON.get();
            } else if (this == ModBlocks.EXPOSED_COPPER_BUTTON.get()) {
                previousBlock = ModBlocks.COPPER_BUTTON.get();
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
                    
                    if (!player.isCreative()) {
                        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                    }
                }
                
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }



    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Don't oxidize while button is pressed to prevent it getting stuck
        if (state.getValue(POWERED)) {
            return;
        }
        WeatheringHelper.tryWeather(state, level, pos, random, CopperButtonBlock::getNextBlock);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        // Don't tick while button is pressed
        if (state.getValue(POWERED)) {
            return false;
        }
        return WeatheringHelper.canWeather(state, CopperButtonBlock::getNextBlock);
    }
}
