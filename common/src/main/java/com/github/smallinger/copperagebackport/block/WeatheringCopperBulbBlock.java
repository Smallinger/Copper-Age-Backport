package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Copper Bulb that can naturally oxidize over time.
 * Uses WeatheringCopper interface for oxidation mechanics.
 */
public class WeatheringCopperBulbBlock extends CopperBulbBlock implements WeatheringCopper {

    public WeatheringCopperBulbBlock(WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(weatherState, properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringCopperBulbBlock::getNextBlockOptional);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperBulbBlock::getNextBlockOptional);
    }

    @Override
    public Optional<BlockState> getNext(BlockState state) {
        return Optional.ofNullable(getNextBlock(state.getBlock()))
                .map(block -> block.withPropertiesOf(state));
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    /**
     * Gets the next oxidation stage block as Optional (for WeatheringHelper).
     */
    public static Optional<Block> getNextBlockOptional(Block block) {
        return Optional.ofNullable(getNextBlock(block));
    }

    /**
     * Gets the next oxidation stage block for the given copper bulb block.
     */
    public static Block getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_BULB.get()) {
            return ModBlocks.EXPOSED_COPPER_BULB.get();
        } else if (block == ModBlocks.EXPOSED_COPPER_BULB.get()) {
            return ModBlocks.WEATHERED_COPPER_BULB.get();
        } else if (block == ModBlocks.WEATHERED_COPPER_BULB.get()) {
            return ModBlocks.OXIDIZED_COPPER_BULB.get();
        }
        return null;
    }

    /**
     * Gets the previous oxidation stage block for the given copper bulb block (for scraping with axe).
     */
    public static Block getPreviousBlock(Block block) {
        if (block == ModBlocks.OXIDIZED_COPPER_BULB.get()) {
            return ModBlocks.WEATHERED_COPPER_BULB.get();
        } else if (block == ModBlocks.WEATHERED_COPPER_BULB.get()) {
            return ModBlocks.EXPOSED_COPPER_BULB.get();
        } else if (block == ModBlocks.EXPOSED_COPPER_BULB.get()) {
            return ModBlocks.COPPER_BULB.get();
        }
        return null;
    }

    /**
     * Gets the waxed version of the given copper bulb block.
     */
    public static Block getWaxedBlock(Block block) {
        if (block == ModBlocks.COPPER_BULB.get()) {
            return ModBlocks.WAXED_COPPER_BULB.get();
        } else if (block == ModBlocks.EXPOSED_COPPER_BULB.get()) {
            return ModBlocks.WAXED_EXPOSED_COPPER_BULB.get();
        } else if (block == ModBlocks.WEATHERED_COPPER_BULB.get()) {
            return ModBlocks.WAXED_WEATHERED_COPPER_BULB.get();
        } else if (block == ModBlocks.OXIDIZED_COPPER_BULB.get()) {
            return ModBlocks.WAXED_OXIDIZED_COPPER_BULB.get();
        }
        return null;
    }

    /**
     * Gets the unwaxed version of the given waxed copper bulb block.
     */
    public static Block getUnwaxedBlock(Block block) {
        if (block == ModBlocks.WAXED_COPPER_BULB.get()) {
            return ModBlocks.COPPER_BULB.get();
        } else if (block == ModBlocks.WAXED_EXPOSED_COPPER_BULB.get()) {
            return ModBlocks.EXPOSED_COPPER_BULB.get();
        } else if (block == ModBlocks.WAXED_WEATHERED_COPPER_BULB.get()) {
            return ModBlocks.WEATHERED_COPPER_BULB.get();
        } else if (block == ModBlocks.WAXED_OXIDIZED_COPPER_BULB.get()) {
            return ModBlocks.OXIDIZED_COPPER_BULB.get();
        }
        return null;
    }
}
