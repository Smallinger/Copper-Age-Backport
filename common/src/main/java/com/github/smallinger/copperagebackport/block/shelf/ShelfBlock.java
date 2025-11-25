package com.github.smallinger.copperagebackport.block.shelf;

import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

/**
 * A shelf block that can hold up to 3 items.
 * When powered, multiple shelves can connect to swap items with the player's hotbar.
 */
public class ShelfBlock extends BaseEntityBlock implements SelectableSlotContainer, SideChainPartBlock, SimpleWaterloggedBlock {
    
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<SideChainPart> SIDE_CHAIN_PART = EnumProperty.create("side_chain_part", SideChainPart.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    private static final Map<Direction, VoxelShape> SHAPES = createShapes();
    
    private static Map<Direction, VoxelShape> createShapes() {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        VoxelShape baseShape = Shapes.or(
            Block.box(0.0, 12.0, 11.0, 16.0, 16.0, 13.0),  // Top shelf
            Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0),   // Back panel
            Block.box(0.0, 0.0, 11.0, 16.0, 4.0, 13.0)     // Bottom shelf
        );
        
        map.put(Direction.NORTH, baseShape);
        map.put(Direction.SOUTH, rotateShape(baseShape, Direction.SOUTH));
        map.put(Direction.WEST, rotateShape(baseShape, Direction.WEST));
        map.put(Direction.EAST, rotateShape(baseShape, Direction.EAST));
        
        return map;
    }
    
    private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
        // Rotate the shape based on direction
        switch (direction) {
            case SOUTH:
                return Shapes.or(
                    Block.box(0.0, 12.0, 3.0, 16.0, 16.0, 5.0),
                    Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0),
                    Block.box(0.0, 0.0, 3.0, 16.0, 4.0, 5.0)
                );
            case WEST:
                return Shapes.or(
                    Block.box(11.0, 12.0, 0.0, 13.0, 16.0, 16.0),
                    Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                    Block.box(11.0, 0.0, 0.0, 13.0, 4.0, 16.0)
                );
            case EAST:
                return Shapes.or(
                    Block.box(3.0, 12.0, 0.0, 5.0, 16.0, 16.0),
                    Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0),
                    Block.box(3.0, 0.0, 0.0, 5.0, 4.0, 16.0)
                );
            default:
                return shape;
        }
    }

    public ShelfBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, SIDE_CHAIN_PART, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShelfBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
            .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide()) {
            boolean hasSignal = level.hasNeighborSignal(pos);
            if (state.getValue(POWERED) != hasSignal) {
                BlockState newState = state.setValue(POWERED, hasSignal);
                if (!hasSignal) {
                    newState = newState.setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED);
                }

                level.setBlock(pos, newState, 3);
                this.playSound(level, pos, hasSignal ? ModSounds.SHELF_ACTIVATE.get() : ModSounds.SHELF_DEACTIVATE.get());
                level.gameEvent(hasSignal ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Context.of(newState));
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getValue(POWERED)) {
            this.updateSelfAndNeighborsOnPoweringUp(level, pos, state, oldState);
        } else {
            this.updateNeighborsAfterPoweringDown(level, pos, state);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof ShelfBlockEntity shelfEntity) {
                // Manually drop all items from the shelf
                for (int i = 0; i < shelfEntity.getContainerSize(); i++) {
                    ItemStack itemStack = shelfEntity.getItem(i);
                    if (!itemStack.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                    }
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
            this.updateNeighborsAfterPoweringDown(level, pos, state);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ShelfBlockEntity shelfEntity && hand == InteractionHand.MAIN_HAND) {
            OptionalInt slotOpt = this.getHitSlot(hit, state.getValue(FACING));
            if (slotOpt.isEmpty()) {
                return InteractionResult.PASS;
            }
            
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            
            Inventory inventory = player.getInventory();
            ItemStack heldItem = player.getItemInHand(hand);
            
            if (!state.getValue(POWERED)) {
                // Single item swap
                boolean swapped = swapSingleItem(heldItem, player, shelfEntity, slotOpt.getAsInt(), inventory);
                if (swapped) {
                    this.playSound(level, pos, heldItem.isEmpty() ? ModSounds.SHELF_TAKE_ITEM.get() : ModSounds.SHELF_SINGLE_SWAP.get());
                } else {
                    if (heldItem.isEmpty()) {
                        return InteractionResult.PASS;
                    }
                    this.playSound(level, pos, ModSounds.SHELF_PLACE_ITEM.get());
                }
                return InteractionResult.CONSUME;
            } else {
                // Hotbar swap when powered
                boolean swapped = this.swapHotbar(level, pos, inventory);
                if (!swapped) {
                    return InteractionResult.CONSUME;
                }
                this.playSound(level, pos, ModSounds.SHELF_MULTI_SWAP.get());
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    private static boolean swapSingleItem(ItemStack heldStack, Player player, ShelfBlockEntity shelf, int slot, Inventory inventory) {
        // Swap the held item with the shelf item directly
        ItemStack shelfItem = shelf.swapItemNoUpdate(slot, heldStack);
        // In creative mode with empty shelf slot, keep a copy of the original item
        ItemStack newHeldItem = player.getAbilities().instabuild && shelfItem.isEmpty() ? heldStack.copy() : shelfItem;
        // Set the player's selected slot to the item that was in the shelf
        inventory.setItem(inventory.selected, newHeldItem);
        inventory.setChanged();
        shelf.setChanged();
        return !shelfItem.isEmpty();
    }

    private boolean swapHotbar(Level level, BlockPos pos, Inventory inventory) {
        List<BlockPos> connectedBlocks = this.getAllBlocksConnectedTo(level, pos);
        if (connectedBlocks.isEmpty()) {
            return false;
        }

        boolean anySwapped = false;

        for (int i = 0; i < connectedBlocks.size(); i++) {
            ShelfBlockEntity shelfEntity = (ShelfBlockEntity) level.getBlockEntity(connectedBlocks.get(i));
            if (shelfEntity != null) {
                for (int j = 0; j < shelfEntity.getContainerSize(); j++) {
                    int hotbarSlot = 9 - (connectedBlocks.size() - i) * shelfEntity.getContainerSize() + j;
                    if (hotbarSlot >= 0 && hotbarSlot < 9) {
                        ItemStack hotbarItem = inventory.removeItemNoUpdate(hotbarSlot);
                        ItemStack shelfItem = shelfEntity.swapItemNoUpdate(j, hotbarItem);
                        if (!hotbarItem.isEmpty() || !shelfItem.isEmpty()) {
                            inventory.setItem(hotbarSlot, shelfItem);
                            anySwapped = true;
                        }
                    }
                }
                inventory.setChanged();
                shelfEntity.setChanged();
            }
        }

        return anySwapped;
    }

    private void playSound(LevelAccessor level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return 0;
        }
        if (level.getBlockEntity(pos) instanceof ShelfBlockEntity shelfEntity) {
            int slot0 = shelfEntity.getItem(0).isEmpty() ? 0 : 1;
            int slot1 = shelfEntity.getItem(1).isEmpty() ? 0 : 1;
            int slot2 = shelfEntity.getItem(2).isEmpty() ? 0 : 1;
            return slot0 | (slot1 << 1) | (slot2 << 2);
        }
        return 0;
    }

    // SelectableSlotContainer implementation
    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public int getColumns() {
        return 3;
    }

    // SideChainPartBlock implementation
    @Override
    public SideChainPart getSideChainPart(BlockState state) {
        return state.getValue(SIDE_CHAIN_PART);
    }

    @Override
    public BlockState setSideChainPart(BlockState state, SideChainPart part) {
        return state.setValue(SIDE_CHAIN_PART, part);
    }

    @Override
    public Direction getFacing(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public boolean isConnectable(BlockState state) {
        return state.is(ModTags.Blocks.WOODEN_SHELVES) && state.hasProperty(POWERED) && state.getValue(POWERED);
    }

    @Override
    public int getMaxChainLength() {
        return 3;
    }
}
