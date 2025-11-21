package com.github.smallinger.coppergolemlegacy.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class WaxedCopperGolemStatueBlock extends CopperGolemStatueBlock {
    public static final MapCodec<WaxedCopperGolemStatueBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(WaxedCopperGolemStatueBlock::getWeatheringState),
                propertiesCodec()
            )
            .apply(instance, WaxedCopperGolemStatueBlock::new)
    );

    public WaxedCopperGolemStatueBlock(WeatheringCopper.WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }

    @Override
    protected MapCodec<? extends WaxedCopperGolemStatueBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return false; // Waxed statues don't oxidize
    }
}