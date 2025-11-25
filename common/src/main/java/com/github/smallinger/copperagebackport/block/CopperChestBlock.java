package com.github.smallinger.copperagebackport.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.block.entity.CopperChestBlockEntity;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.registry.ModBlockEntities;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class CopperChestBlock extends ChestBlock {
    private static final Map<Block, Supplier<Block>> COPPER_TO_COPPER_CHEST_MAPPING = Map.of(
        Blocks.COPPER_BLOCK, () -> ModBlocks.COPPER_CHEST.get(),
        Blocks.EXPOSED_COPPER, () -> ModBlocks.EXPOSED_COPPER_CHEST.get(),
        Blocks.WEATHERED_COPPER, () -> ModBlocks.WEATHERED_COPPER_CHEST.get(),
        Blocks.OXIDIZED_COPPER, () -> ModBlocks.OXIDIZED_COPPER_CHEST.get(),
        Blocks.WAXED_COPPER_BLOCK, () -> ModBlocks.COPPER_CHEST.get(),
        Blocks.WAXED_EXPOSED_COPPER, () -> ModBlocks.EXPOSED_COPPER_CHEST.get(),
        Blocks.WAXED_WEATHERED_COPPER, () -> ModBlocks.WEATHERED_COPPER_CHEST.get(),
        Blocks.WAXED_OXIDIZED_COPPER, () -> ModBlocks.OXIDIZED_COPPER_CHEST.get()
    );

    protected final WeatheringCopper.WeatherState weatherState;
    private final SoundEvent openSound;
    private final SoundEvent closeSound;

    public CopperChestBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties, () -> ModBlockEntities.COPPER_CHEST_BLOCK_ENTITY.get());
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
        Block chestBlock = COPPER_TO_COPPER_CHEST_MAPPING.getOrDefault(block, () -> ModBlocks.COPPER_CHEST.get()).get();
        return chestBlock.defaultBlockState().setValue(FACING, direction);
    }

    /**
     * Get the unwaxed version of a waxed chest block
     */
    public static Optional<Block> getUnwaxedBlock(Block block) {
        if (block == ModBlocks.WAXED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.COPPER_CHEST.get());
        } else if (block == ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_CHEST.get());
        } else if (block == ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_CHEST.get());
        } else if (block == ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_CHEST.get());
        }
        return Optional.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is using an axe on a waxed chest - dewax it
        if (stack.is(ItemTags.AXES)) {
            // Don't allow dewaxing if chest is open
            if (level.getBlockEntity(pos) instanceof CopperChestBlockEntity chestEntity && chestEntity.isChestOpen()) {
                return InteractionResult.PASS;
            }
            
            Optional<Block> unwaxedBlock = getUnwaxedBlock(state.getBlock());
            
            if (unwaxedBlock.isPresent()) {
                ChestType chestType = state.getValue(TYPE);
                Block newBlock = unwaxedBlock.get();
                
                // Play sounds and particles for both chests BEFORE server check
                if (chestType != ChestType.SINGLE) {
                    Direction connectedDir = ChestBlock.getConnectedDirection(state);
                    BlockPos connectedPos = pos.relative(connectedDir);
                    BlockState connectedState = level.getBlockState(connectedPos);
                    
                    if (connectedState.getBlock() == state.getBlock()) {
                        // Play sounds and particles for both chests
                        level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3004, pos, 0);
                        level.playSound(player, connectedPos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3004, connectedPos, 0);
                    } else {
                        level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3004, pos, 0);
                    }
                } else {
                    // Single chest
                    level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(player, 3004, pos, 0);
                }
                
                if (!level.isClientSide) {
                    NonNullList<ItemStack> currentItems = copyInventoryAndClear(level, pos);
                    NonNullList<ItemStack> connectedItems = NonNullList.create();
                    BlockState newState = newBlock.withPropertiesOf(state);
                    
                    // If this is a double chest, update both halves atomically
                    if (chestType != ChestType.SINGLE) {
                        Direction connectedDir = ChestBlock.getConnectedDirection(state);
                        BlockPos connectedPos = pos.relative(connectedDir);
                        BlockState connectedState = level.getBlockState(connectedPos);
                        
                        if (connectedState.getBlock() == state.getBlock()) {
                            BlockState connectedNewState = newBlock.withPropertiesOf(connectedState);
                            connectedItems = copyInventoryAndClear(level, connectedPos);
                            
                            // Update both chests with flag 2 (no block updates to neighbors yet)
                            level.setBlock(pos, newState, 2);
                            level.setBlock(connectedPos, connectedNewState, 2);
                            
                            // Now send block updates to both positions
                            level.blockUpdated(pos, newBlock);
                            level.blockUpdated(connectedPos, newBlock);
                            restoreInventory(level, pos, currentItems);
                            restoreInventory(level, connectedPos, connectedItems);
                        } else {
                            // Other half is different, update only this one
                            level.setBlockAndUpdate(pos, newState);
                            restoreInventory(level, pos, currentItems);
                        }
                    } else {
                        // Single chest - normal update
                        level.setBlockAndUpdate(pos, newState);
                        restoreInventory(level, pos, currentItems);
                    }
                    
                    if (!player.isCreative()) {
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }
                }
                
                return InteractionResult.SUCCESS;
            }
            // Block chest opening even if not waxed
            return InteractionResult.SUCCESS;
        }
        
        // Default chest behavior (open inventory)
        return super.use(state, level, pos, player, hand, hit);
    }

    /**
     * Copy all items from the chest at the given position and clear the container to avoid drop logic.
     */
    protected static NonNullList<ItemStack> copyInventoryAndClear(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof Container container) {
            NonNullList<ItemStack> items = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < container.getContainerSize(); i++) {
                items.set(i, container.getItem(i).copy());
            }
            container.clearContent();
            if (blockEntity instanceof CopperChestBlockEntity chestEntity) {
                chestEntity.setChanged();
            }
            return items;
        }
        return NonNullList.create();
    }

    /**
     * Restore previously copied items into the chest at the given position.
     */
    protected static void restoreInventory(Level level, BlockPos pos, NonNullList<ItemStack> items) {
        if (items.isEmpty()) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof Container container) {
            for (int i = 0; i < Math.min(container.getContainerSize(), items.size()); i++) {
                container.setItem(i, items.get(i));
            }
            if (blockEntity instanceof CopperChestBlockEntity chestEntity) {
                chestEntity.setChanged();
            }
        }
    }
}

