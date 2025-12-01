package com.github.smallinger.copperagebackport.block;

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

import java.util.function.Supplier;

/**
 * Waxed Copper Bulb that does not oxidize over time.
 * Can be unwaxed with an axe.
 */
public class WaxedCopperBulbBlock extends CopperBulbBlock {
    private final Supplier<? extends CopperBulbBlock> unwaxedBulb;

    public WaxedCopperBulbBlock(WeatheringCopper.WeatherState weatherState, Supplier<? extends CopperBulbBlock> unwaxedBulb, BlockBehaviour.Properties properties) {
        super(weatherState, properties);
        this.unwaxedBulb = unwaxedBulb;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is using an axe to remove wax
        if (stack.is(ItemTags.AXES)) {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, pos, 0);
            
            if (!level.isClientSide) {
                // Replace with unwaxed version, preserving bulb state
                BlockState unwaxedState = unwaxedBulb.get().defaultBlockState()
                    .setValue(LIT, state.getValue(LIT))
                    .setValue(POWERED, state.getValue(POWERED));
                level.setBlock(pos, unwaxedState, 11);
                
                if (player != null && !player.isCreative()) {
                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                }
            }
            
            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        
        return InteractionResult.PASS;
    }
}
