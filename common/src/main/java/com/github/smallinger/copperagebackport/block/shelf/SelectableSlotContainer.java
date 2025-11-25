package com.github.smallinger.copperagebackport.block.shelf;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Interface for blocks that have selectable slots based on hit position.
 * Used by ShelfBlock to determine which of the 3 slots was clicked.
 */
public interface SelectableSlotContainer {
    int getRows();

    int getColumns();

    default OptionalInt getHitSlot(BlockHitResult hitResult, Direction direction) {
        return getRelativeHitCoordinatesForBlockFace(hitResult, direction).map(coords -> {
            int row = getSection(1.0F - coords.y, this.getRows());
            int col = getSection(coords.x, this.getColumns());
            return OptionalInt.of(col + row * this.getColumns());
        }).orElseGet(OptionalInt::empty);
    }

    private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult hitResult, Direction direction) {
        Direction hitDirection = hitResult.getDirection();
        if (direction != hitDirection) {
            return Optional.empty();
        } else {
            Vec3 location = hitResult.getLocation();
            double x = location.x() - hitResult.getBlockPos().getX();
            double y = location.y() - hitResult.getBlockPos().getY();
            double z = location.z() - hitResult.getBlockPos().getZ();

            switch (direction) {
                case NORTH:
                    return Optional.of(new Vec2((float)(1.0 - x), (float)y));
                case SOUTH:
                    return Optional.of(new Vec2((float)x, (float)y));
                case WEST:
                    return Optional.of(new Vec2((float)z, (float)y));
                case EAST:
                    return Optional.of(new Vec2((float)(1.0 - z), (float)y));
                default:
                    return Optional.empty();
            }
        }
    }

    private static int getSection(float pos, int count) {
        float scaledPos = pos * 16.0F;
        float sectionSize = 16.0F / count;
        return Mth.clamp(Mth.floor(scaledPos / sectionSize), 0, count - 1);
    }
}
