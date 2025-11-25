package com.github.smallinger.copperagebackport.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class for handling custom weathering/oxidation logic.
 * Use this when blocks can't be registered in vanilla's immutable WeatheringCopper maps.
 */
public class WeatheringHelper {
    
    /**
     * Vanilla copper oxidation probability
     */
    public static final float OXIDATION_CHANCE = 0.05688889F;
    
    /**
     * Applies weathering/oxidation to a block with vanilla probability.
     * 
     * @param state Current block state
     * @param level Server level
     * @param pos Block position
     * @param random Random source
     * @param getNextBlock Function that returns the next oxidation stage
     * @return true if oxidation occurred, false otherwise
     */
    public static boolean tryWeather(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, 
                                      Function<Block, Optional<Block>> getNextBlock) {
        Optional<Block> nextBlock = getNextBlock.apply(state.getBlock());
        
        if (nextBlock.isPresent() && random.nextFloat() < OXIDATION_CHANCE) {
            BlockState newState = nextBlock.get().withPropertiesOf(state);
            level.setBlockAndUpdate(pos, newState);
            return true;
        }
        
        return false;
    }
    
    /**
     * Checks if a block can weather/oxidize.
     * 
     * @param state Current block state
     * @param getNextBlock Function that returns the next oxidation stage
     * @return true if the block has a next stage
     */
    public static boolean canWeather(BlockState state, Function<Block, Optional<Block>> getNextBlock) {
        return getNextBlock.apply(state.getBlock()).isPresent();
    }
}
