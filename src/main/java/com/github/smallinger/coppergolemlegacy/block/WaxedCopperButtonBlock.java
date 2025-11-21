package com.github.smallinger.coppergolemlegacy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class WaxedCopperButtonBlock extends ButtonBlock {
    private final WeatheringCopper.WeatherState weatherState;
    private final Supplier<CopperButtonBlock> unwaxedButton;

    public WaxedCopperButtonBlock(WeatheringCopper.WeatherState weatherState, Supplier<CopperButtonBlock> unwaxedButton, Properties properties) {
        super(BlockSetType.COPPER, 15, properties);
        this.weatherState = weatherState;
        this.unwaxedButton = unwaxedButton;
    }
    
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Check if player is using an axe to remove wax
        if (stack.is(ItemTags.AXES)) {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, pos, 0);
            
            if (!level.isClientSide) {
                // Replace with unwaxed version, preserving button state
                BlockState unwaxedState = unwaxedButton.get().defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(POWERED, state.getValue(POWERED))
                    .setValue(FACE, state.getValue(FACE));
                level.setBlock(pos, unwaxedState, 11);
                
                if (!player.isCreative()) {
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            }
            
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // Waxed buttons can still be pressed, even when oxidized
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
    
    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return false; // Waxed buttons don't oxidize
    }
}
