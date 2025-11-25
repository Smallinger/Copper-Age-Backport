package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.block.entity.CopperChestBlockEntity;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class WeatheringCopperChestBlock extends CopperChestBlock implements WeatheringCopper {
    
    public WeatheringCopperChestBlock(WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }
    
    /**
     * Override to provide our own oxidation chain since we can't modify the static BiMap
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_CHEST.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_CHEST.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_CHEST.get());
        }
        return WeatheringCopper.getNext(block);
    }
    
    /**
     * Get the previous oxidation stage for scraping with axe
     */
    public static Optional<Block> getPreviousBlock(Block block) {
        if (block == ModBlocks.OXIDIZED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_CHEST.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_CHEST.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.COPPER_CHEST.get());
        }
        return Optional.empty();
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is using an axe - ALWAYS block chest opening with axe
        if (stack.is(ItemTags.AXES)) {
            // Don't allow scraping if chest is open
            if (level.getBlockEntity(pos) instanceof CopperChestBlockEntity chestEntity && chestEntity.isChestOpen()) {
                return InteractionResult.PASS;
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
                        // Play sounds and particles for both chests
                        level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3005, pos, 0);
                        level.playSound(player, connectedPos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3005, connectedPos, 0);
                    } else {
                        level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3005, pos, 0);
                    }
                } else {
                    // Single chest
                    level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(player, 3005, pos, 0);
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
                            
                            // Double durability damage for double chest
                            if (!player.isCreative()) {
                                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                            }
                        } else {
                            // Other half is different, update only this one
                            level.setBlockAndUpdate(pos, newState);
                            restoreInventory(level, pos, currentItems);
                            
                            if (!player.isCreative()) {
                                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                            }
                        }
                    } else {
                        // Single chest - normal update
                        level.setBlockAndUpdate(pos, newState);
                        restoreInventory(level, pos, currentItems);
                        
                        if (!player.isCreative()) {
                            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                        }
                    }
                }
                
                return InteractionResult.SUCCESS;
            }
            // No oxidation to remove - allow chest to open
            return InteractionResult.PASS;
        }
        
        // Check if player is using honeycomb to wax the chest - ALWAYS block chest opening with honeycomb
        if (stack.is(Items.HONEYCOMB)) {
            // Don't allow waxing if chest is open
            if (level.getBlockEntity(pos) instanceof CopperChestBlockEntity chestEntity && chestEntity.isChestOpen()) {
                return InteractionResult.PASS;
            }
            
            Block waxedBlock = null;
            
            if (this == ModBlocks.COPPER_CHEST.get()) {
                waxedBlock = ModBlocks.WAXED_COPPER_CHEST.get();
            } else if (this == ModBlocks.EXPOSED_COPPER_CHEST.get()) {
                waxedBlock = ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get();
            } else if (this == ModBlocks.WEATHERED_COPPER_CHEST.get()) {
                waxedBlock = ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get();
            } else if (this == ModBlocks.OXIDIZED_COPPER_CHEST.get()) {
                waxedBlock = ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get();
            }
            
            if (waxedBlock != null) {
                ChestType chestType = state.getValue(TYPE);
                
                // Play sounds and particles for both chests BEFORE server check
                if (chestType != ChestType.SINGLE) {
                    Direction connectedDir = ChestBlock.getConnectedDirection(state);
                    BlockPos connectedPos = pos.relative(connectedDir);
                    BlockState connectedState = level.getBlockState(connectedPos);
                    
                    if (connectedState.getBlock() == state.getBlock()) {
                        // Play sounds and particles for both chests
                        level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3003, pos, 0);
                        level.playSound(player, connectedPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3003, connectedPos, 0);
                    } else {
                        level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(player, 3003, pos, 0);
                    }
                } else {
                    // Single chest
                    level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(player, 3003, pos, 0);
                }
                
                if (!level.isClientSide) {
                    NonNullList<ItemStack> currentItems = copyInventoryAndClear(level, pos);
                    NonNullList<ItemStack> connectedItems = NonNullList.create();
                    BlockState waxedState = waxedBlock.withPropertiesOf(state);
                    
                    // If this is a double chest, update both halves atomically
                    if (chestType != ChestType.SINGLE) {
                        Direction connectedDir = ChestBlock.getConnectedDirection(state);
                        BlockPos connectedPos = pos.relative(connectedDir);
                        BlockState connectedState = level.getBlockState(connectedPos);
                        
                        if (connectedState.getBlock() == state.getBlock()) {
                            BlockState connectedWaxedState = waxedBlock.withPropertiesOf(connectedState);
                            connectedItems = copyInventoryAndClear(level, connectedPos);
                            
                            // Update both chests with flag 2 (no block updates to neighbors yet)
                            level.setBlock(pos, waxedState, 2);
                            level.setBlock(connectedPos, connectedWaxedState, 2);
                            
                            // Now send block updates to both positions
                            level.blockUpdated(pos, waxedBlock);
                            level.blockUpdated(connectedPos, waxedBlock);
                            restoreInventory(level, pos, currentItems);
                            restoreInventory(level, connectedPos, connectedItems);
                        } else {
                            // Other half is different, update only this one
                            level.setBlockAndUpdate(pos, waxedState);
                            restoreInventory(level, pos, currentItems);
                        }
                    } else {
                        // Single chest - normal update
                        level.setBlockAndUpdate(pos, waxedState);
                        restoreInventory(level, pos, currentItems);
                    }
                    
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                
                return InteractionResult.SUCCESS;
            }
            // Already waxed or cannot wax - allow chest to open
            return InteractionResult.PASS;
        }
        
        // Default chest behavior (open inventory)
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        ChestType chestType = state.getValue(TYPE);
        
        // Only left or single chests trigger oxidation
        if (!chestType.equals(ChestType.RIGHT)) {
            // Don't oxidize if chest is open
            if (level.getBlockEntity(pos) instanceof CopperChestBlockEntity chestEntity && chestEntity.isChestOpen()) {
                return;
            }
            
            Optional<Block> nextBlock = getNextBlock(state.getBlock());
            
            if (nextBlock.isPresent() && random.nextFloat() < WeatheringHelper.OXIDATION_CHANCE) {
                Block newBlock = nextBlock.get();
                
                // If this is part of a double chest, update both halves atomically
                if (chestType != ChestType.SINGLE && newBlock instanceof WeatheringCopperChestBlock) {
                    Direction connectedDir = ChestBlock.getConnectedDirection(state);
                    BlockPos otherPos = pos.relative(connectedDir);
                    BlockState otherState = level.getBlockState(otherPos);
                    
                    // Ensure the other half is also a copper chest of the same type
                    if (otherState.getBlock() == state.getBlock()) {
                        // Don't oxidize if other chest is open
                        if (level.getBlockEntity(otherPos) instanceof CopperChestBlockEntity otherChestEntity && otherChestEntity.isChestOpen()) {
                            return;
                        }
                        
                        NonNullList<ItemStack> thisItems = copyInventoryAndClear(level, pos);
                        NonNullList<ItemStack> otherItems = copyInventoryAndClear(level, otherPos);

                        // Prepare both new states with preserved properties
                        BlockState newState = newBlock.withPropertiesOf(state);
                        BlockState newOtherState = newBlock.withPropertiesOf(otherState);
                        
                        // Update both chests with flag 2 (no block updates to neighbors yet)
                        // Use flag 2 to keep block entities (including items)
                        level.setBlock(pos, newState, 2);
                        level.setBlock(otherPos, newOtherState, 2);
                        restoreInventory(level, pos, thisItems);
                        restoreInventory(level, otherPos, otherItems);
                        
                        // Now send block updates to both positions
                        level.blockUpdated(pos, newBlock);
                        level.blockUpdated(otherPos, newBlock);
                        
                        return;
                    }
                }
                
                // Single chest or no valid pair - normal update
                NonNullList<ItemStack> currentItems = copyInventoryAndClear(level, pos);
                BlockState newState = newBlock.withPropertiesOf(state);
                // Use flag 2 to keep block entity (including items)
                level.setBlock(pos, newState, 2);
                restoreInventory(level, pos, currentItems);
                level.blockUpdated(pos, newBlock);
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperChestBlock::getNextBlock);
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }
}
