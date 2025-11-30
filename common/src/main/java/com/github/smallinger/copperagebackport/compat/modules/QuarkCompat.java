package com.github.smallinger.copperagebackport.compat.modules;

import com.github.smallinger.copperagebackport.compat.IContainerCompat;
import com.github.smallinger.copperagebackport.compat.IModCompatModule;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Compatibility module for Quark mod.
 * 
 * Quark adds variant chests made from different wood types and materials:
 * - Wood variant chests (Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry, Bamboo, Crimson, Warped)
 * - Special variant chests (Nether Brick, Purpur, Prismarine)
 * - All chests have regular and trapped variants
 * 
 * Quark chests extend vanilla ChestBlock and use their own BlockEntity types:
 * - VariantChestBlockEntity for regular chests
 * - VariantTrappedChestBlockEntity for trapped chests
 * 
 * This module enables the Copper Golem to deposit items into Quark chest containers.
 * Since Quark is an optional dependency, we use reflection to avoid
 * ClassNotFoundException when the mod is not installed.
 */
public class QuarkCompat implements IModCompatModule, IContainerCompat {
    
    public static final String MOD_ID = "quark";
    
    // Block classes (loaded via reflection)
    private Class<?> variantChestBlockClass;
    private Class<?> variantTrappedChestBlockClass;
    
    // BlockEntity classes
    private Class<?> variantChestBlockEntityClass;
    private Class<?> variantTrappedChestBlockEntityClass;
    
    @Override
    public String getModId() {
        return MOD_ID;
    }
    
    @Override
    public void init() {
        // Try to load VariantChestBlock (regular chests)
        try {
            variantChestBlockClass = Class.forName("org.violetmoon.quark.content.building.block.VariantChestBlock");
        } catch (ClassNotFoundException e) {
            // Not available
        }
        
        // Try to load VariantTrappedChestBlock (trapped chests)
        try {
            variantTrappedChestBlockClass = Class.forName("org.violetmoon.quark.content.building.block.VariantTrappedChestBlock");
        } catch (ClassNotFoundException e) {
            // Not available
        }
        
        // Try to load VariantChestBlockEntity
        try {
            variantChestBlockEntityClass = Class.forName("org.violetmoon.quark.content.building.block.be.VariantChestBlockEntity");
        } catch (ClassNotFoundException e) {
            // Not available
        }
        
        // Try to load VariantTrappedChestBlockEntity
        try {
            variantTrappedChestBlockEntityClass = Class.forName("org.violetmoon.quark.content.building.block.be.VariantTrappedChestBlockEntity");
        } catch (ClassNotFoundException e) {
            // Not available
        }
    }
    
    /**
     * Check if the block is a Quark variant chest.
     */
    @Override
    public boolean isValidContainer(BlockState state) {
        Block block = state.getBlock();
        
        // Check if it's a Quark regular chest
        if (variantChestBlockClass != null && variantChestBlockClass.isInstance(block)) {
            return true;
        }
        
        // Check if it's a Quark trapped chest
        if (variantTrappedChestBlockClass != null && variantTrappedChestBlockClass.isInstance(block)) {
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
     * Open or close the Quark chest.
     * Quark chests extend vanilla ChestBlock so they use the same triggerEvent mechanism.
     */
    @Override
    public void setOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return;
        
        // Check if it's a Quark Chest BlockEntity
        boolean isQuarkChest = (variantChestBlockEntityClass != null && variantChestBlockEntityClass.isInstance(blockEntity)) ||
                               (variantTrappedChestBlockEntityClass != null && variantTrappedChestBlockEntityClass.isInstance(blockEntity));
        
        if (isQuarkChest) {
            // Use triggerEvent to control the lid animation (same as vanilla chests)
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
     * Get a Container wrapper for the Quark chest block entity.
     * Quark BlockEntities extend ChestBlockEntity which extends RandomizableContainerBlockEntity which implements Container.
     */
    @Override
    @Nullable
    public Container getContainer(BlockEntity blockEntity, Level level, BlockPos pos) {
        boolean isQuarkChest = (variantChestBlockEntityClass != null && variantChestBlockEntityClass.isInstance(blockEntity)) ||
                               (variantTrappedChestBlockEntityClass != null && variantTrappedChestBlockEntityClass.isInstance(blockEntity));
        
        if (isQuarkChest) {
            // VariantChestBlockEntity and VariantTrappedChestBlockEntity extend ChestBlockEntity which implements Container
            if (blockEntity instanceof Container container) {
                return container;
            }
        }
        return null;
    }
}
