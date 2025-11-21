package com.github.smallinger.coppergolemlegacy.block;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperChestBlockEntity;
import com.github.smallinger.coppergolemlegacy.util.WeatheringHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class WeatheringCopperChestBlock extends CopperChestBlock implements WeatheringCopper {
    
    public WeatheringCopperChestBlock(WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }

    @Override
    public MapCodec<? extends WeatheringCopperChestBlock> codec() {
        return null; // Simplified for 1.21.1
    }

    /**
     * Get the next oxidation stage
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == CopperGolemLegacy.COPPER_CHEST.get()) {
            return Optional.of(CopperGolemLegacy.EXPOSED_COPPER_CHEST.get());
        } else if (block == CopperGolemLegacy.EXPOSED_COPPER_CHEST.get()) {
            return Optional.of(CopperGolemLegacy.WEATHERED_COPPER_CHEST.get());
        } else if (block == CopperGolemLegacy.WEATHERED_COPPER_CHEST.get()) {
            return Optional.of(CopperGolemLegacy.OXIDIZED_COPPER_CHEST.get());
        }
        return WeatheringCopper.getNext(block);
    }
    
    /**
     * Get the previous oxidation stage for scraping
     */
    public static Optional<Block> getPreviousBlock(Block block) {
        if (block == CopperGolemLegacy.OXIDIZED_COPPER_CHEST.get()) {
            return Optional.of(CopperGolemLegacy.WEATHERED_COPPER_CHEST.get());
        } else if (block == CopperGolemLegacy.WEATHERED_COPPER_CHEST.get()) {
            return Optional.of(CopperGolemLegacy.EXPOSED_COPPER_CHEST.get());
        } else if (block == CopperGolemLegacy.EXPOSED_COPPER_CHEST.get()) {
            return Optional.of(CopperGolemLegacy.COPPER_CHEST.get());
        }
        return Optional.empty();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, 
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Check if player is using an axe - ALWAYS block chest opening with axe
        if (stack.is(ItemTags.AXES)) {
            // Don't allow scraping if chest is open
            if (level.getBlockEntity(pos) instanceof CopperChestBlockEntity chestEntity && chestEntity.isChestOpen()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            
            Optional<Block> previousBlock = getPreviousBlock(state.getBlock());
            
            if (previousBlock.isPresent()) {
                ChestType chestType = state.getValue(TYPE);
                Block newBlock = previousBlock.get();
                
                // Play sounds and particles for both chests BEFORE server check
                if (chestType != ChestType.SINGLE) {
                    Direction connectedDir = ChestBlock.getConnectedDirection(state);
                    BlockPos connectedPos = pos.relative(connectedDir);
                    BlockState connectedState = level.getBlockState(connectedPos);
                    
                    if (connectedState.getBlock() == state.getBlock()) {
                        level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3005, pos, 0);
                        level.playSound(player, connectedPos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3005, connectedPos, 0);
                    } else {
                        level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3005, pos, 0);
                    }
                } else {
                    level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(player, 3005, pos, 0);
                }
                
                if (!level.isClientSide) {
                    NonNullList<ItemStack> currentItems = copyInventoryAndClear(level, pos);
                    NonNullList<ItemStack> connectedItems = NonNullList.create();
                    BlockState newState = newBlock.withPropertiesOf(state);
                    
                    if (chestType != ChestType.SINGLE) {
                        Direction connectedDir = ChestBlock.getConnectedDirection(state);
                        BlockPos connectedPos = pos.relative(connectedDir);
                        BlockState connectedState = level.getBlockState(connectedPos);
                        
                        if (connectedState.getBlock() == state.getBlock()) {
                            BlockState connectedNewState = newBlock.withPropertiesOf(connectedState);
                            connectedItems = copyInventoryAndClear(level, connectedPos);
                            
                            level.setBlock(pos, newState, 2);
                            level.setBlock(connectedPos, connectedNewState, 2);
                            level.blockUpdated(pos, newBlock);
                            level.blockUpdated(connectedPos, newBlock);
                            
                            restoreInventory(level, pos, currentItems);
                            restoreInventory(level, connectedPos, connectedItems);
                            
                            stack.hurtAndBreak(2, player, LivingEntity.getSlotForHand(hand));
                        } else {
                            level.setBlockAndUpdate(pos, newState);
                            restoreInventory(level, pos, currentItems);
                            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                        }
                    } else {
                        level.setBlockAndUpdate(pos, newState);
                        restoreInventory(level, pos, currentItems);
                        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                    }
                }
                
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        
        // Check if player is using honeycomb to wax the chest
        if (stack.is(Items.HONEYCOMB)) {
            if (level.getBlockEntity(pos) instanceof CopperChestBlockEntity chestEntity && chestEntity.isChestOpen()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            
            Block waxedBlock = null;
            
            if (this == CopperGolemLegacy.COPPER_CHEST.get()) {
                waxedBlock = CopperGolemLegacy.WAXED_COPPER_CHEST.get();
            } else if (this == CopperGolemLegacy.EXPOSED_COPPER_CHEST.get()) {
                waxedBlock = CopperGolemLegacy.WAXED_EXPOSED_COPPER_CHEST.get();
            } else if (this == CopperGolemLegacy.WEATHERED_COPPER_CHEST.get()) {
                waxedBlock = CopperGolemLegacy.WAXED_WEATHERED_COPPER_CHEST.get();
            } else if (this == CopperGolemLegacy.OXIDIZED_COPPER_CHEST.get()) {
                waxedBlock = CopperGolemLegacy.WAXED_OXIDIZED_COPPER_CHEST.get();
            }
            
            if (waxedBlock != null) {
                ChestType chestType = state.getValue(TYPE);
                
                if (chestType != ChestType.SINGLE) {
                    Direction connectedDir = ChestBlock.getConnectedDirection(state);
                    BlockPos connectedPos = pos.relative(connectedDir);
                    BlockState connectedState = level.getBlockState(connectedPos);
                    
                    if (connectedState.getBlock() == state.getBlock()) {
                        level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3003, pos, 0);
                        level.playSound(player, connectedPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3003, connectedPos, 0);
                    } else {
                        level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3003, pos, 0);
                    }
                } else {
                    level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(player, 3003, pos, 0);
                }
                
                if (!level.isClientSide) {
                    NonNullList<ItemStack> currentItems = copyInventoryAndClear(level, pos);
                    NonNullList<ItemStack> connectedItems = NonNullList.create();
                    BlockState waxedState = waxedBlock.withPropertiesOf(state);
                    
                    if (chestType != ChestType.SINGLE) {
                        Direction connectedDir = ChestBlock.getConnectedDirection(state);
                        BlockPos connectedPos = pos.relative(connectedDir);
                        BlockState connectedState = level.getBlockState(connectedPos);
                        
                        if (connectedState.getBlock() == state.getBlock()) {
                            BlockState connectedWaxedState = waxedBlock.withPropertiesOf(connectedState);
                            connectedItems = copyInventoryAndClear(level, connectedPos);
                            
                            level.setBlock(pos, waxedState, 2);
                            level.setBlock(connectedPos, connectedWaxedState, 2);
                            level.blockUpdated(pos, waxedBlock);
                            level.blockUpdated(connectedPos, waxedBlock);
                            
                            restoreInventory(level, pos, currentItems);
                            restoreInventory(level, connectedPos, connectedItems);
                        } else {
                            level.setBlockAndUpdate(pos, waxedState);
                            restoreInventory(level, pos, currentItems);
                        }
                    } else {
                        level.setBlockAndUpdate(pos, waxedState);
                        restoreInventory(level, pos, currentItems);
                    }
                    
                    stack.consume(1, player);
                }
                
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        ChestType chestType = state.getValue(TYPE);
        
        if (chestType.equals(ChestType.RIGHT)) {
            return;
        }
        
        if (level.getBlockEntity(pos) instanceof CopperChestBlockEntity chestEntity && chestEntity.isChestOpen()) {
            return;
        }
        
        Optional<Block> nextBlock = getNextBlock(state.getBlock());
        if (!nextBlock.isPresent() || random.nextFloat() >= WeatheringHelper.OXIDATION_CHANCE) {
            return;
        }
        
        Block newBlock = nextBlock.get();
        
        if (chestType != ChestType.SINGLE) {
            Direction connectedDir = ChestBlock.getConnectedDirection(state);
            BlockPos otherPos = pos.relative(connectedDir);
            BlockState otherState = level.getBlockState(otherPos);
            
            if (otherState.getBlock() == state.getBlock()) {
                if (level.getBlockEntity(otherPos) instanceof CopperChestBlockEntity otherChest && otherChest.isChestOpen()) {
                    return;
                }
                
                NonNullList<ItemStack> thisItems = copyInventoryAndClear(level, pos);
                NonNullList<ItemStack> otherItems = copyInventoryAndClear(level, otherPos);
                BlockState newState = newBlock.withPropertiesOf(state);
                BlockState newOtherState = newBlock.withPropertiesOf(otherState);
                
                level.setBlock(pos, newState, 2);
                level.setBlock(otherPos, newOtherState, 2);
                level.blockUpdated(pos, newBlock);
                level.blockUpdated(otherPos, newBlock);
                restoreInventory(level, pos, thisItems);
                restoreInventory(level, otherPos, otherItems);
                return;
            }
        }
        
        NonNullList<ItemStack> currentItems = copyInventoryAndClear(level, pos);
        BlockState newState = newBlock.withPropertiesOf(state);
        level.setBlock(pos, newState, 2);
        restoreInventory(level, pos, currentItems);
        level.blockUpdated(pos, newBlock);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperChestBlock::getNextBlock);
    }

    @Override
    public WeatherState getAge() {
        return this.getState();
    }
    
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
