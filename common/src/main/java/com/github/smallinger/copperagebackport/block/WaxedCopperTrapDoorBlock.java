package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.ModBlockSetTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import java.util.function.Supplier;

/**
 * A waxed copper trapdoor that does not weather.
 * Ported from Minecraft 1.21.10 to 1.20.1
 */
public class WaxedCopperTrapDoorBlock extends TrapDoorBlock {
    
    private final WeatheringCopper.WeatherState weatherState;
    private final Supplier<WeatheringCopperTrapDoorBlock> unwaxedTrapdoor;

    public WaxedCopperTrapDoorBlock(WeatheringCopper.WeatherState weatherState, Supplier<WeatheringCopperTrapDoorBlock> unwaxedTrapdoor, Properties properties) {
        super(properties, ModBlockSetTypes.COPPER);
        this.weatherState = weatherState;
        this.unwaxedTrapdoor = unwaxedTrapdoor;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        // Check if player is using an axe to remove wax
        if (stack.is(ItemTags.AXES)) {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, pos, 0);
            
            if (!level.isClientSide) {
                // Replace with unwaxed version, preserving trapdoor state
                BlockState unwaxedState = unwaxedTrapdoor.get().defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(OPEN, state.getValue(OPEN))
                    .setValue(HALF, state.getValue(HALF))
                    .setValue(POWERED, state.getValue(POWERED))
                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                level.setBlock(pos, unwaxedState, 11);
                
                if (player != null && !player.isCreative()) {
                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                }
            }
            
            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        
        // Pass to default trapdoor behavior (open/close)
        return super.use(state, level, pos, player, hand, hitResult);
    }
}
