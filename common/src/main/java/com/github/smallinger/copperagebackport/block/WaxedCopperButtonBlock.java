package com.github.smallinger.copperagebackport.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.Supplier;

public class WaxedCopperButtonBlock extends ButtonBlock {
    private final WeatheringCopper.WeatherState weatherState;
    private final Supplier<CopperButtonBlock> unwaxedButton;

    public WaxedCopperButtonBlock(WeatheringCopper.WeatherState weatherState, Supplier<CopperButtonBlock> unwaxedButton, Properties properties) {
        super(properties, BlockSetType.IRON, 15, true); // Properties first in 1.20.1
        this.weatherState = weatherState;
        this.unwaxedButton = unwaxedButton;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
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
                
                if (player != null && !player.isCreative()) {
                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                }
            }
            
            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        
        // Pass to default button behavior (press the button)
        return super.use(state, level, pos, player, hand, hitResult);
    }

}
