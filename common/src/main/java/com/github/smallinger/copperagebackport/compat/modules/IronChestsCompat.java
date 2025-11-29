package com.github.smallinger.copperagebackport.compat.modules;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.compat.IContainerCompat;
import com.github.smallinger.copperagebackport.compat.IModCompatModule;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * Compatibility module for Iron Chests mod.
 * 
 * Iron Chests adds metal chest variants with larger storage capacity:
 * - Copper Chest (45 slots)
 * - Iron Chest (54 slots)
 * - Gold Chest (81 slots)
 * - Diamond Chest (108 slots)
 * - Crystal Chest (108 slots, transparent)
 * - Obsidian Chest (108 slots, explosion resistant)
 * - Dirt Chest (1 slot, for fun)
 * 
 * Also includes trapped variants of all chest types.
 * 
 * This module enables the Copper Golem to deposit items into Iron Chests containers.
 * Since Iron Chests is an optional dependency, we use reflection to avoid
 * ClassNotFoundException when the mod is not installed.
 */
public class IronChestsCompat implements IModCompatModule, IContainerCompat {
    
    public static final String MOD_ID = "ironchest";
    
    // Block classes (loaded via reflection)
    private Class<?> abstractIronChestBlockClass;
    private Class<?> abstractTrappedIronChestBlockClass;
    
    // BlockEntity class
    private Class<?> abstractIronChestBlockEntityClass;
    
    @Override
    public String getModId() {
        return MOD_ID;
    }
    
    @Override
    public void init() {
        // Try to load AbstractIronChestBlock (regular chests)
        try {
            abstractIronChestBlockClass = Class.forName("com.progwml6.ironchest.common.block.regular.AbstractIronChestBlock");
        } catch (ClassNotFoundException e) {
            // Not available
        }
        
        // Try to load AbstractTrappedIronChestBlock (trapped chests)
        try {
            abstractTrappedIronChestBlockClass = Class.forName("com.progwml6.ironchest.common.block.trapped.AbstractTrappedIronChestBlock");
        } catch (ClassNotFoundException e) {
            // Not available
        }
        
        // Try to load AbstractIronChestBlockEntity
        try {
            abstractIronChestBlockEntityClass = Class.forName("com.progwml6.ironchest.common.block.regular.entity.AbstractIronChestBlockEntity");
        } catch (ClassNotFoundException e) {
            // Not available
        }
    }
    
    /**
     * Check if the block is an Iron Chests container.
     */
    @Override
    public boolean isValidContainer(BlockState state) {
        Block block = state.getBlock();
        
        // Check if it's an Iron Chests regular chest
        if (abstractIronChestBlockClass != null && abstractIronChestBlockClass.isInstance(block)) {
            return true;
        }
        
        // Check if it's an Iron Chests trapped chest
        if (abstractTrappedIronChestBlockClass != null && abstractTrappedIronChestBlockClass.isInstance(block)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if this handler can handle the given block state.
     */
    @Override
    public boolean canHandle(BlockState state) {
        return isValidContainer(state);
    }
    
    /**
     * Open or close the Iron Chests container.
     * Iron Chests uses triggerEvent(1, count) for lid animation,
     * where count > 0 means open and count == 0 means closed.
     */
    @Override
    public void setOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return;
        
        // Check if it's an Iron Chest BlockEntity
        if (abstractIronChestBlockEntityClass != null && abstractIronChestBlockEntityClass.isInstance(blockEntity)) {
            // Use triggerEvent to control the lid animation
            // Event ID 1 is used for opener count, count > 0 = open, count == 0 = closed
            level.blockEvent(pos, state.getBlock(), 1, open ? 1 : 0);
            
            // Play appropriate sound
            if (open) {
                level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            } else {
                level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }
    
    /**
     * Get a Container wrapper for the Iron Chests block entity.
     * Iron Chests BlockEntities extend RandomizableContainerBlockEntity which implements Container.
     */
    @Override
    @Nullable
    public Container getContainer(BlockEntity blockEntity, Level level, BlockPos pos) {
        if (abstractIronChestBlockEntityClass != null && abstractIronChestBlockEntityClass.isInstance(blockEntity)) {
            // AbstractIronChestBlockEntity extends RandomizableContainerBlockEntity which implements Container
            if (blockEntity instanceof Container container) {
                return container;
            }
        }
        return null;
    }
}
