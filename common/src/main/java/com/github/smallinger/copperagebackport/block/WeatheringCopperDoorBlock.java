package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.ModBlockSetTypes;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A copper door that weathers over time.
 * Ported from Minecraft 1.21.10 to 1.20.1
 * 
 * Important: Only the lower half triggers weathering to ensure both halves oxidize together.
 */
public class WeatheringCopperDoorBlock extends DoorBlock implements WeatheringCopper {
    
    private final WeatherState weatherState;
    private Supplier<CopperDoorBlock> waxedDoor;

    public WeatheringCopperDoorBlock(WeatherState weatherState, Properties properties) {
        super(properties, ModBlockSetTypes.COPPER);
        this.weatherState = weatherState;
    }
    
    public void setWaxedDoor(Supplier<CopperDoorBlock> waxedDoor) {
        this.waxedDoor = waxedDoor;
    }

    /**
     * Override to provide our own oxidation chain since we can't modify the static BiMap
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_DOOR.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_DOOR.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_DOOR.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_DOOR.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_DOOR.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_DOOR.get());
        }
        return WeatheringCopper.getNext(block);
    }
    
    /**
     * Check if a block is any copper door (weathering or waxed, any oxidation level)
     */
    public static boolean isAnyCopperDoor(Block block) {
        return block == ModBlocks.COPPER_DOOR.get()
            || block == ModBlocks.EXPOSED_COPPER_DOOR.get()
            || block == ModBlocks.WEATHERED_COPPER_DOOR.get()
            || block == ModBlocks.OXIDIZED_COPPER_DOOR.get()
            || block == ModBlocks.WAXED_COPPER_DOOR.get()
            || block == ModBlocks.WAXED_EXPOSED_COPPER_DOOR.get()
            || block == ModBlocks.WAXED_WEATHERED_COPPER_DOOR.get()
            || block == ModBlocks.WAXED_OXIDIZED_COPPER_DOOR.get();
    }
    
    /**
     * Override updateShape to accept any copper door as a valid partner half.
     * This prevents the door from breaking when oxidation changes the block type.
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        
        // Check vertical connections (upper/lower half relationship)
        if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            // Check if the neighbor is ANY copper door with the opposite half
            if (isAnyCopperDoor(neighborState.getBlock()) && neighborState.getValue(HALF) != half) {
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
    public WeatherState getAge() {
        return this.weatherState;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is using honeycomb to wax the door
        if (stack.is(Items.HONEYCOMB) && waxedDoor != null) {
            // Get both halves' positions for sounds and particles
            BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockPos upperPos = lowerPos.above();
            
            // Play sounds and particles for both halves
            level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3003, lowerPos, 0);
            level.levelEvent(player, 3003, upperPos, 0);
            
            if (!level.isClientSide) {
                
                BlockState lowerState = level.getBlockState(lowerPos);
                BlockState upperState = level.getBlockState(upperPos);
                
                // Replace both halves with waxed versions
                BlockState waxedLowerState = waxedDoor.get().defaultBlockState()
                    .setValue(FACING, lowerState.getValue(FACING))
                    .setValue(OPEN, lowerState.getValue(OPEN))
                    .setValue(HINGE, lowerState.getValue(HINGE))
                    .setValue(POWERED, lowerState.getValue(POWERED))
                    .setValue(HALF, DoubleBlockHalf.LOWER);
                
                BlockState waxedUpperState = waxedDoor.get().defaultBlockState()
                    .setValue(FACING, upperState.getValue(FACING))
                    .setValue(OPEN, upperState.getValue(OPEN))
                    .setValue(HINGE, upperState.getValue(HINGE))
                    .setValue(POWERED, upperState.getValue(POWERED))
                    .setValue(HALF, DoubleBlockHalf.UPPER);
                
                // Update both halves atomically with flag 2 (no block updates to neighbors yet)
                level.setBlock(lowerPos, waxedLowerState, 2);
                level.setBlock(upperPos, waxedUpperState, 2);
                
                // Now send block updates to both positions after both are updated
                level.blockUpdated(lowerPos, waxedDoor.get());
                level.blockUpdated(upperPos, waxedDoor.get());
                
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
            if (this == ModBlocks.OXIDIZED_COPPER_DOOR.get()) {
                previousBlock = ModBlocks.WEATHERED_COPPER_DOOR.get();
            } else if (this == ModBlocks.WEATHERED_COPPER_DOOR.get()) {
                previousBlock = ModBlocks.EXPOSED_COPPER_DOOR.get();
            } else if (this == ModBlocks.EXPOSED_COPPER_DOOR.get()) {
                previousBlock = ModBlocks.COPPER_DOOR.get();
            }
            
            if (previousBlock != null) {
                // Get both halves' positions for sounds and particles
                BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
                BlockPos upperPos = lowerPos.above();
                
                // Play sounds and particles for both halves
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, lowerPos, 0);
                level.levelEvent(player, 3005, upperPos, 0);
                
                if (!level.isClientSide) {
                    
                    BlockState lowerState = level.getBlockState(lowerPos);
                    BlockState upperState = level.getBlockState(upperPos);
                    
                    // Replace both halves with previous oxidation state
                    BlockState newLowerState = previousBlock.defaultBlockState()
                        .setValue(FACING, lowerState.getValue(FACING))
                        .setValue(OPEN, lowerState.getValue(OPEN))
                        .setValue(HINGE, lowerState.getValue(HINGE))
                        .setValue(POWERED, lowerState.getValue(POWERED))
                        .setValue(HALF, DoubleBlockHalf.LOWER);
                    
                    BlockState newUpperState = previousBlock.defaultBlockState()
                        .setValue(FACING, upperState.getValue(FACING))
                        .setValue(OPEN, upperState.getValue(OPEN))
                        .setValue(HINGE, upperState.getValue(HINGE))
                        .setValue(POWERED, upperState.getValue(POWERED))
                        .setValue(HALF, DoubleBlockHalf.UPPER);
                    
                    // Update both halves atomically with flag 2 (no block updates to neighbors yet)
                    level.setBlock(lowerPos, newLowerState, 2);
                    level.setBlock(upperPos, newUpperState, 2);
                    
                    // Now send block updates to both positions after both are updated
                    level.blockUpdated(lowerPos, previousBlock);
                    level.blockUpdated(upperPos, previousBlock);
                    
                    if (player != null && !player.isCreative()) {
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }
                }
                
                return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }
        }
        
        // Pass to default door behavior (open/close)
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Only process weathering for the lower half to ensure both halves oxidize together
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos upperPos = pos.above();
            BlockState upperState = level.getBlockState(upperPos);
            
            // Ensure upper half is also the same door block
            if (upperState.getBlock() != state.getBlock()) {
                return;
            }
            
            Optional<Block> nextBlock = getNextBlock(state.getBlock());
            if (nextBlock.isPresent() && WeatheringHelper.shouldWeather(state, level, pos, random)) {
                Block next = nextBlock.get();
                
                // Replace lower half
                BlockState newLowerState = next.defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(OPEN, state.getValue(OPEN))
                    .setValue(HINGE, state.getValue(HINGE))
                    .setValue(POWERED, state.getValue(POWERED))
                    .setValue(HALF, DoubleBlockHalf.LOWER);
                
                // Replace upper half
                BlockState newUpperState = next.defaultBlockState()
                    .setValue(FACING, upperState.getValue(FACING))
                    .setValue(OPEN, upperState.getValue(OPEN))
                    .setValue(HINGE, upperState.getValue(HINGE))
                    .setValue(POWERED, upperState.getValue(POWERED))
                    .setValue(HALF, DoubleBlockHalf.UPPER);
                
                // Update both halves atomically with flag 2 (no block updates to neighbors yet)
                level.setBlock(pos, newLowerState, 2);
                level.setBlock(upperPos, newUpperState, 2);
                
                // Now send block updates to both positions after both are updated
                level.blockUpdated(pos, next);
                level.blockUpdated(upperPos, next);
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperDoorBlock::getNextBlock);
    }
}
