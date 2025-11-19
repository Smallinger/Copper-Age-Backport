package com.github.smallinger.coppergolemlegacy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperGolemStatueBlock extends CopperGolemStatueBlock implements WeatheringCopper {
    public WeatheringCopperGolemStatueBlock(WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.onRandomTick(state, level, pos, random);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    public WeatherState getAge() {
        return this.getWeatheringState();
    }
}

