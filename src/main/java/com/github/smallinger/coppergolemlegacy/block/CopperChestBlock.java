package com.github.smallinger.coppergolemlegacy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.ModSounds;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperChestBlockEntity;

import java.util.Map;
import java.util.function.Supplier;

public class CopperChestBlock extends ChestBlock {
    private static final Map<Block, Supplier<Block>> COPPER_TO_COPPER_CHEST_MAPPING = Map.of(
        Blocks.COPPER_BLOCK, () -> CopperGolemLegacy.COPPER_CHEST.get(),
        Blocks.EXPOSED_COPPER, () -> CopperGolemLegacy.EXPOSED_COPPER_CHEST.get(),
        Blocks.WEATHERED_COPPER, () -> CopperGolemLegacy.WEATHERED_COPPER_CHEST.get(),
        Blocks.OXIDIZED_COPPER, () -> CopperGolemLegacy.OXIDIZED_COPPER_CHEST.get(),
        Blocks.WAXED_COPPER_BLOCK, () -> CopperGolemLegacy.COPPER_CHEST.get(),
        Blocks.WAXED_EXPOSED_COPPER, () -> CopperGolemLegacy.EXPOSED_COPPER_CHEST.get(),
        Blocks.WAXED_WEATHERED_COPPER, () -> CopperGolemLegacy.WEATHERED_COPPER_CHEST.get(),
        Blocks.WAXED_OXIDIZED_COPPER, () -> CopperGolemLegacy.OXIDIZED_COPPER_CHEST.get()
    );

    private final WeatheringCopper.WeatherState weatherState;
    private final SoundEvent openSound;
    private final SoundEvent closeSound;

    public CopperChestBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties, () -> CopperGolemLegacy.COPPER_CHEST_BLOCK_ENTITY.get());
        this.weatherState = weatherState;
        this.openSound = ModSounds.COPPER_CHEST_OPEN.get();
        this.closeSound = ModSounds.COPPER_CHEST_CLOSE.get();
    }

        public WeatheringCopper.WeatherState getState() {
        return this.weatherState;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Full block shape for collision - allows entities to walk on top
        return Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Full block collision shape - prevents entities from falling through
        return Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CopperChestBlockEntity(pos, state);
    }

    public static BlockState getFromCopperBlock(Block block, Direction direction, Level level, BlockPos pos) {
        Block chestBlock = COPPER_TO_COPPER_CHEST_MAPPING.getOrDefault(block, () -> CopperGolemLegacy.COPPER_CHEST.get()).get();
        return chestBlock.defaultBlockState().setValue(FACING, direction);
    }
}

