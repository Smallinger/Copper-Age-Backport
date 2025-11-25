package com.github.smallinger.copperagebackport.block.shelf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for blocks that can form horizontal chains when powered.
 * Used by ShelfBlock to connect up to 3 shelves together for hotbar swapping.
 */
public interface SideChainPartBlock {
    SideChainPart getSideChainPart(BlockState state);

    BlockState setSideChainPart(BlockState state, SideChainPart sideChainPart);

    Direction getFacing(BlockState state);

    boolean isConnectable(BlockState state);

    int getMaxChainLength();

    default List<BlockPos> getAllBlocksConnectedTo(LevelAccessor level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        if (!this.isConnectable(blockstate)) {
            return new ArrayList<>();
        } else {
            Neighbors neighbors = this.getNeighbors(level, pos, this.getFacing(blockstate));
            List<BlockPos> list = new LinkedList<>();
            list.add(pos);
            this.addBlocksConnectingTowards(neighbors::left, SideChainPart.LEFT, pos1 -> list.add(0, pos1));
            this.addBlocksConnectingTowards(neighbors::right, SideChainPart.RIGHT, list::add);
            return list;
        }
    }

    private void addBlocksConnectingTowards(IntFunction<Neighbor> neighborGetter, SideChainPart part, Consumer<BlockPos> output) {
        for (int i = 1; i < this.getMaxChainLength(); i++) {
            Neighbor neighbor = neighborGetter.apply(i);
            if (neighbor.connectsTowards(part)) {
                output.accept(neighbor.pos());
            }

            if (neighbor.isUnconnectableOrChainEnd()) {
                break;
            }
        }
    }

    default void updateNeighborsAfterPoweringDown(LevelAccessor level, BlockPos pos, BlockState state) {
        Neighbors neighbors = this.getNeighbors(level, pos, this.getFacing(state));
        neighbors.left().disconnectFromRight();
        neighbors.right().disconnectFromLeft();
    }

    default void updateSelfAndNeighborsOnPoweringUp(LevelAccessor level, BlockPos pos, BlockState state, BlockState oldState) {
        if (this.isConnectable(state)) {
            if (!this.isBeingUpdatedByNeighbor(state, oldState)) {
                Neighbors neighbors = this.getNeighbors(level, pos, this.getFacing(state));
                SideChainPart sidechainpart = SideChainPart.UNCONNECTED;
                int leftCount = neighbors.left().isConnectable()
                    ? this.getAllBlocksConnectedTo(level, neighbors.left().pos()).size()
                    : 0;
                int rightCount = neighbors.right().isConnectable()
                    ? this.getAllBlocksConnectedTo(level, neighbors.right().pos()).size()
                    : 0;
                int currentChainLength = 1;
                if (this.canConnect(leftCount, currentChainLength)) {
                    sidechainpart = sidechainpart.whenConnectedToTheLeft();
                    neighbors.left().connectToTheRight();
                    currentChainLength += leftCount;
                }

                if (this.canConnect(rightCount, currentChainLength)) {
                    sidechainpart = sidechainpart.whenConnectedToTheRight();
                    neighbors.right().connectToTheLeft();
                }

                this.setPart(level, pos, sidechainpart);
            }
        }
    }

    private boolean canConnect(int segmentLength, int currentChainLength) {
        return segmentLength > 0 && currentChainLength + segmentLength <= this.getMaxChainLength();
    }

    private boolean isBeingUpdatedByNeighbor(BlockState state, BlockState oldState) {
        boolean flag = this.getSideChainPart(state).isConnected();
        boolean flag1 = this.isConnectable(oldState) && this.getSideChainPart(oldState).isConnected();
        return flag || flag1;
    }

    private Neighbors getNeighbors(LevelAccessor level, BlockPos pos, Direction facing) {
        return new Neighbors(this, level, facing, pos, new HashMap<>());
    }

    default void setPart(LevelAccessor level, BlockPos pos, SideChainPart part) {
        BlockState blockstate = level.getBlockState(pos);
        if (this.getSideChainPart(blockstate) != part) {
            level.setBlock(pos, this.setSideChainPart(blockstate, part), 3);
        }
    }

    // Neighbor interface
    interface Neighbor {
        BlockPos pos();

        boolean isConnectable();

        boolean isUnconnectableOrChainEnd();

        boolean connectsTowards(SideChainPart part);

        default void connectToTheRight() {}

        default void connectToTheLeft() {}

        default void disconnectFromRight() {}

        default void disconnectFromLeft() {}
    }

    // Empty neighbor (non-connectable)
    class EmptyNeighbor implements Neighbor {
        private final BlockPos pos;

        public EmptyNeighbor(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public BlockPos pos() {
            return this.pos;
        }

        @Override
        public boolean isConnectable() {
            return false;
        }

        @Override
        public boolean isUnconnectableOrChainEnd() {
            return true;
        }

        @Override
        public boolean connectsTowards(SideChainPart part) {
            return false;
        }
    }

    // Chain neighbor (connectable shelf)
    class SideChainNeighbor implements Neighbor {
        private final LevelAccessor level;
        private final SideChainPartBlock block;
        private final BlockPos pos;
        private final SideChainPart part;

        public SideChainNeighbor(LevelAccessor level, SideChainPartBlock block, BlockPos pos, SideChainPart part) {
            this.level = level;
            this.block = block;
            this.pos = pos;
            this.part = part;
        }

        @Override
        public BlockPos pos() {
            return this.pos;
        }

        @Override
        public boolean isConnectable() {
            return true;
        }

        @Override
        public boolean isUnconnectableOrChainEnd() {
            return this.part.isChainEnd();
        }

        @Override
        public boolean connectsTowards(SideChainPart targetPart) {
            return this.part.isConnectionTowards(targetPart);
        }

        @Override
        public void connectToTheRight() {
            this.block.setPart(this.level, this.pos, this.part.whenConnectedToTheRight());
        }

        @Override
        public void connectToTheLeft() {
            this.block.setPart(this.level, this.pos, this.part.whenConnectedToTheLeft());
        }

        @Override
        public void disconnectFromRight() {
            this.block.setPart(this.level, this.pos, this.part.whenDisconnectedFromTheRight());
        }

        @Override
        public void disconnectFromLeft() {
            this.block.setPart(this.level, this.pos, this.part.whenDisconnectedFromTheLeft());
        }
    }

    // Neighbors helper class
    class Neighbors {
        private final SideChainPartBlock block;
        private final LevelAccessor level;
        private final Direction facing;
        private final BlockPos center;
        private final Map<BlockPos, Neighbor> cache;

        public Neighbors(SideChainPartBlock block, LevelAccessor level, Direction facing, BlockPos center, Map<BlockPos, Neighbor> cache) {
            this.block = block;
            this.level = level;
            this.facing = facing;
            this.center = center;
            this.cache = cache;
        }

        private boolean isConnectableToThisBlock(BlockState state) {
            return this.block.isConnectable(state) && this.block.getFacing(state) == this.facing;
        }

        private Neighbor createNewNeighbor(BlockPos pos) {
            BlockState blockstate = this.level.getBlockState(pos);
            SideChainPart sidechainpart = this.isConnectableToThisBlock(blockstate) ? this.block.getSideChainPart(blockstate) : null;
            return sidechainpart == null
                ? new EmptyNeighbor(pos)
                : new SideChainNeighbor(this.level, this.block, pos, sidechainpart);
        }

        private Neighbor getOrCreateNeighbor(Direction direction, Integer distance) {
            BlockPos neighborPos = this.center.relative(direction, distance);
            return this.cache.computeIfAbsent(neighborPos, this::createNewNeighbor);
        }

        public Neighbor left(int distance) {
            return this.getOrCreateNeighbor(this.facing.getClockWise(), distance);
        }

        public Neighbor right(int distance) {
            return this.getOrCreateNeighbor(this.facing.getCounterClockWise(), distance);
        }

        public Neighbor left() {
            return this.left(1);
        }

        public Neighbor right() {
            return this.right(1);
        }
    }
}
