package com.github.smallinger.coppergolemlegacy.block;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WaxedCopperGolemStatueBlock extends CopperGolemStatueBlock {

    public WaxedCopperGolemStatueBlock(WeatheringCopper.WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }

    /**
     * Get the unwaxed version of this waxed statue
     */
    public static java.util.Optional<Block> getUnwaxedBlock(Block block) {
        if (block == CopperGolemLegacy.WAXED_COPPER_GOLEM_STATUE.get()) {
            return java.util.Optional.of(CopperGolemLegacy.COPPER_GOLEM_STATUE.get());
        } else if (block == CopperGolemLegacy.WAXED_EXPOSED_COPPER_GOLEM_STATUE.get()) {
            return java.util.Optional.of(CopperGolemLegacy.EXPOSED_COPPER_GOLEM_STATUE.get());
        } else if (block == CopperGolemLegacy.WAXED_WEATHERED_COPPER_GOLEM_STATUE.get()) {
            return java.util.Optional.of(CopperGolemLegacy.WEATHERED_COPPER_GOLEM_STATUE.get());
        } else if (block == CopperGolemLegacy.WAXED_OXIDIZED_COPPER_GOLEM_STATUE.get()) {
            return java.util.Optional.of(CopperGolemLegacy.OXIDIZED_COPPER_GOLEM_STATUE.get());
        }
        return java.util.Optional.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Axe interaction - dewax if waxed, otherwise restore golem
        if (stack.is(ItemTags.AXES)) {
            // Try dewaxing first
            java.util.Optional<Block> unwaxedBlock = getUnwaxedBlock(state.getBlock());
            
            if (unwaxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, pos, 0);
                
                if (!level.isClientSide) {
                    BlockState newState = unwaxedBlock.get().defaultBlockState()
                        .setValue(FACING, state.getValue(FACING))
                        .setValue(POSE, state.getValue(POSE))
                        .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                    level.setBlock(pos, newState, Block.UPDATE_ALL);
                    
                    if (!player.isCreative()) {
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }
                }
                
                return InteractionResult.SUCCESS;
            }
            
            // Not waxed - restore golem (use parent behavior)
            return super.use(state, level, pos, player, hand, hitResult);
        }
        
        // For other interactions, use parent behavior
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return false; // Waxed statues don't oxidize
    }
}
