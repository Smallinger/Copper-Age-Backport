package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class WeatheringCopperGolemStatueBlock extends CopperGolemStatueBlock implements WeatheringCopper {
    
    public WeatheringCopperGolemStatueBlock(WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }
    
    /**
     * Override to provide our own oxidation chain
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE.get());
        }
        return WeatheringCopper.getNext(block);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringCopperGolemStatueBlock::getNextBlock);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperGolemStatueBlock::getNextBlock);
    }

    @Override
    public WeatherState getAge() {
        return this.getWeatheringState();
    }
}

