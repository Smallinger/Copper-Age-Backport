package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.ModBlockSetTypes;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A copper trapdoor that weathers over time.
 * Ported from Minecraft 1.21.10 to 1.20.1
 */
public class WeatheringCopperTrapDoorBlock extends TrapDoorBlock implements WeatheringCopper {
    
    private final WeatherState weatherState;
    private Supplier<WaxedCopperTrapDoorBlock> waxedTrapdoor;

    public WeatheringCopperTrapDoorBlock(WeatherState weatherState, Properties properties) {
        super(properties, ModBlockSetTypes.COPPER);
        this.weatherState = weatherState;
    }
    
    public void setWaxedTrapdoor(Supplier<WaxedCopperTrapDoorBlock> waxedTrapdoor) {
        this.waxedTrapdoor = waxedTrapdoor;
    }

    /**
     * Override to provide our own oxidation chain since we can't modify the static BiMap
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_TRAPDOOR.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_TRAPDOOR.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_TRAPDOOR.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_TRAPDOOR.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_TRAPDOOR.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_TRAPDOOR.get());
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
        
        // Check if player is using honeycomb to wax the trapdoor
        if (stack.is(Items.HONEYCOMB) && waxedTrapdoor != null) {
            level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3003, pos, 0);
            
            if (!level.isClientSide) {
                // Replace with waxed version, preserving trapdoor state
                BlockState waxedState = waxedTrapdoor.get().defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(OPEN, state.getValue(OPEN))
                    .setValue(HALF, state.getValue(HALF))
                    .setValue(POWERED, state.getValue(POWERED))
                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
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
            if (this == ModBlocks.OXIDIZED_COPPER_TRAPDOOR.get()) {
                previousBlock = ModBlocks.WEATHERED_COPPER_TRAPDOOR.get();
            } else if (this == ModBlocks.WEATHERED_COPPER_TRAPDOOR.get()) {
                previousBlock = ModBlocks.EXPOSED_COPPER_TRAPDOOR.get();
            } else if (this == ModBlocks.EXPOSED_COPPER_TRAPDOOR.get()) {
                previousBlock = ModBlocks.COPPER_TRAPDOOR.get();
            }
            
            if (previousBlock != null) {
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0);
                
                if (!level.isClientSide) {
                    // Replace with previous oxidation state, preserving trapdoor state
                    BlockState newState = previousBlock.defaultBlockState()
                        .setValue(FACING, state.getValue(FACING))
                        .setValue(OPEN, state.getValue(OPEN))
                        .setValue(HALF, state.getValue(HALF))
                        .setValue(POWERED, state.getValue(POWERED))
                        .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                    level.setBlock(pos, newState, 11);
                    
                    if (player != null && !player.isCreative()) {
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }
                }
                
                return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }
        }
        
        // Pass to default trapdoor behavior (open/close)
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringCopperTrapDoorBlock::getNextBlock);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperTrapDoorBlock::getNextBlock);
    }
}
