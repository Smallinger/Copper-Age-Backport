package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.ModBlockSetTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import java.util.function.Supplier;

/**
 * A waxed copper door that does not weather.
 * Can be unwaxed with an axe.
 * Ported from Minecraft 1.21.10 to 1.20.1
 */
public class CopperDoorBlock extends DoorBlock {
    
    private final WeatheringCopper.WeatherState weatherState;
    private final Supplier<WeatheringCopperDoorBlock> unwaxedDoor;

    public CopperDoorBlock(WeatheringCopper.WeatherState weatherState, Supplier<WeatheringCopperDoorBlock> unwaxedDoor, Properties properties) {
        super(properties, ModBlockSetTypes.COPPER);
        this.weatherState = weatherState;
        this.unwaxedDoor = unwaxedDoor;
    }
    
    /**
     * Override updateShape to accept any copper door as a valid partner half.
     * This prevents the door from breaking when waxing/unwaxing changes the block type.
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        
        // Check vertical connections (upper/lower half relationship)
        if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            // Check if the neighbor is ANY copper door with the opposite half
            if (WeatheringCopperDoorBlock.isAnyCopperDoor(neighborState.getBlock()) && neighborState.getValue(HALF) != half) {
                // Copy properties from neighbor to stay synchronized
                return state.setValue(FACING, neighborState.getValue(FACING))
                    .setValue(OPEN, neighborState.getValue(OPEN))
                    .setValue(HINGE, neighborState.getValue(HINGE))
                    .setValue(POWERED, neighborState.getValue(POWERED));
            } else {
                // Neighbor is not a copper door, break this half
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            // Check if lower half can survive (needs solid block below)
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, pos) 
                ? Blocks.AIR.defaultBlockState() 
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        // Check if player is using an axe to remove wax
        if (stack.is(ItemTags.AXES)) {
            // Get both halves' positions for sounds and particles
            BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockPos upperPos = lowerPos.above();
            
            // Play sounds and particles for both halves
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, lowerPos, 0);
            level.levelEvent(player, 3004, upperPos, 0);
            
            if (!level.isClientSide) {
                
                BlockState lowerState = level.getBlockState(lowerPos);
                BlockState upperState = level.getBlockState(upperPos);
                
                // Replace both halves with unwaxed versions
                BlockState unwaxedLowerState = unwaxedDoor.get().defaultBlockState()
                    .setValue(FACING, lowerState.getValue(FACING))
                    .setValue(OPEN, lowerState.getValue(OPEN))
                    .setValue(HINGE, lowerState.getValue(HINGE))
                    .setValue(POWERED, lowerState.getValue(POWERED))
                    .setValue(HALF, DoubleBlockHalf.LOWER);
                
                BlockState unwaxedUpperState = unwaxedDoor.get().defaultBlockState()
                    .setValue(FACING, upperState.getValue(FACING))
                    .setValue(OPEN, upperState.getValue(OPEN))
                    .setValue(HINGE, upperState.getValue(HINGE))
                    .setValue(POWERED, upperState.getValue(POWERED))
                    .setValue(HALF, DoubleBlockHalf.UPPER);
                
                // Update both halves atomically with flag 2 (no block updates to neighbors yet)
                level.setBlock(lowerPos, unwaxedLowerState, 2);
                level.setBlock(upperPos, unwaxedUpperState, 2);
                
                // Now send block updates to both positions after both are updated
                level.blockUpdated(lowerPos, unwaxedDoor.get());
                level.blockUpdated(upperPos, unwaxedDoor.get());
                
                if (player != null && !player.isCreative()) {
                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                }
            }
            
            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        
        // Pass to default door behavior (open/close)
        return super.use(state, level, pos, player, hand, hitResult);
    }
}
