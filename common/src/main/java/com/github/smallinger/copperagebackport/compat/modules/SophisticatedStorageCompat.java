package com.github.smallinger.copperagebackport.compat.modules;

import com.github.smallinger.copperagebackport.compat.IContainerCompat;
import com.github.smallinger.copperagebackport.compat.IModCompatModule;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

/**
 * Compatibility module for SophisticatedStorage mod.
 * 
 * SophisticatedStorage adds enhanced storage blocks (Chests, Barrels, Shulker Boxes)
 * with upgrade slots and special features. This module enables the Copper Golem
 * to deposit items into SophisticatedStorage containers.
 * 
 * Supported containers:
 * - Chests (all tiers: Wood, Copper, Iron, Gold, Diamond, Netherite)
 * - Barrels (all tiers)
 * - Limited Barrels (all tiers)
 * 
 * Since SophisticatedStorage is an optional dependency, we use reflection
 * to avoid ClassNotFoundException when the mod is not installed.
 */
public class SophisticatedStorageCompat implements IModCompatModule, IContainerCompat {
    
    public static final String MOD_ID = "sophisticatedstorage";
    
    // Block classes (loaded via reflection)
    private Class<?> chestBlockClass;
    private Class<?> barrelBlockClass;
    private Class<?> limitedBarrelBlockClass;
    
    // Base BlockEntity class that has setShouldBeOpen
    private Class<?> storageBlockEntityClass;
    
    // Method for lid animation: StorageBlockEntity.setShouldBeOpen(boolean)
    private Method setShouldBeOpenMethod;
    
    // Network packet classes for syncing chest open state to clients (NeoForge 1.21.x)
    private Class<?> storageOpennessPayloadClass;
    private Constructor<?> storageOpennessPayloadConstructor;
    private Class<?> packetDistributorClass;
    private Method sendToPlayersTrackingChunkMethod;
    
    // For IItemHandler Container wrapper (NeoForge uses Capabilities class)
    private Class<?> capabilitiesClass;
    private Object itemHandlerCapability;
    private Class<?> iItemHandlerClass;
    
    private boolean chestInitialized = false;
    private boolean barrelInitialized = false;
    
    @Override
    public String getModId() {
        return MOD_ID;
    }
    
    @Override
    public void init() {
        // Try to load ChestBlock
        try {
            chestBlockClass = Class.forName("net.p3pp3rf1y.sophisticatedstorage.block.ChestBlock");
            chestInitialized = true;
        } catch (ClassNotFoundException e) {
            // ChestBlock not available
        }
        
        // Try to load BarrelBlock
        try {
            barrelBlockClass = Class.forName("net.p3pp3rf1y.sophisticatedstorage.block.BarrelBlock");
            barrelInitialized = true;
        } catch (ClassNotFoundException e) {
            // BarrelBlock not available
        }
        
        // Try to load LimitedBarrelBlock
        try {
            limitedBarrelBlockClass = Class.forName("net.p3pp3rf1y.sophisticatedstorage.block.LimitedBarrelBlock");
        } catch (ClassNotFoundException e) {
            // LimitedBarrelBlock not available (optional)
        }
        
        // Try to load StorageBlockEntity and its setShouldBeOpen method
        try {
            storageBlockEntityClass = Class.forName("net.p3pp3rf1y.sophisticatedstorage.block.StorageBlockEntity");
            setShouldBeOpenMethod = storageBlockEntityClass.getMethod("setShouldBeOpen", boolean.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // StorageBlockEntity.setShouldBeOpen not available
        }
        
        // Try to load network packet classes for syncing chest open state (NeoForge 1.21.x)
        try {
            storageOpennessPayloadClass = Class.forName("net.p3pp3rf1y.sophisticatedstorage.network.StorageOpennessPayload");
            storageOpennessPayloadConstructor = storageOpennessPayloadClass.getConstructor(BlockPos.class, boolean.class);
            
            packetDistributorClass = Class.forName("net.neoforged.neoforge.network.PacketDistributor");
            Class<?> customPacketPayloadClass = Class.forName("net.minecraft.network.protocol.common.custom.CustomPacketPayload");
            Class<?> payloadArrayClass = java.lang.reflect.Array.newInstance(customPacketPayloadClass, 0).getClass();
            // sendToPlayersTrackingChunk(ServerLevel, ChunkPos, CustomPacketPayload, CustomPacketPayload...)
            sendToPlayersTrackingChunkMethod = packetDistributorClass.getMethod("sendToPlayersTrackingChunk", 
                net.minecraft.server.level.ServerLevel.class, net.minecraft.world.level.ChunkPos.class, 
                customPacketPayloadClass, payloadArrayClass);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // Network packet classes not available
        }
        
        // Try to load NeoForge Capabilities for IItemHandler access
        try {
            capabilitiesClass = Class.forName("net.neoforged.neoforge.capabilities.Capabilities");
            Class<?> itemHandlerClass = Class.forName("net.neoforged.neoforge.capabilities.Capabilities$ItemHandler");
            Field blockField = itemHandlerClass.getField("BLOCK");
            itemHandlerCapability = blockField.get(null);
            iItemHandlerClass = Class.forName("net.neoforged.neoforge.items.IItemHandler");
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            // NeoForge Capabilities not available (expected on Fabric)
        }
        
        // Initialization complete - ModCompat will log the result
    }
    
    /**
     * Check if the block is a SophisticatedStorage container.
     */
    @Override
    public boolean isValidContainer(BlockState state) {
        Block block = state.getBlock();
        
        // Check if it's a SophisticatedStorage ChestBlock
        if (chestBlockClass != null && chestBlockClass.isInstance(block)) {
            return true;
        }
        
        // Check if it's a SophisticatedStorage BarrelBlock
        if (barrelBlockClass != null && barrelBlockClass.isInstance(block)) {
            return true;
        }
        
        // Check if it's a SophisticatedStorage LimitedBarrelBlock
        if (limitedBarrelBlockClass != null && limitedBarrelBlockClass.isInstance(block)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if this handler can handle the given block state.
     * Same as isValidContainer for this implementation.
     */
    @Override
    public boolean canHandle(BlockState state) {
        return isValidContainer(state);
    }
    
    /**
     * Open or close the SophisticatedStorage container.
     * Uses StorageBlockEntity.setShouldBeOpen(boolean) which handles both
     * the lid animation and sound playback internally.
     * Also sends network packet to sync the open state to all clients.
     */
    @Override
    public void setOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return;
        
        Block block = state.getBlock();
        
        try {
            // Use setShouldBeOpen if available (handles animation on server side)
            if (setShouldBeOpenMethod != null && storageBlockEntityClass != null 
                    && storageBlockEntityClass.isInstance(blockEntity)) {
                setShouldBeOpenMethod.invoke(blockEntity, open);
            }
            
            // Send network packet to sync chest open state to all clients
            // This is required for the lid animation to play on clients
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                sendOpennessPacket(serverLevel, pos, open);
            }
            
            // Play appropriate sound
            if (chestBlockClass != null && chestBlockClass.isInstance(block)) {
                // Chest sound
                level.playSound(null, pos, 
                    open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE, 
                    SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            } else if (barrelBlockClass != null && barrelBlockClass.isInstance(block)) {
                // Barrel sound
                level.playSound(null, pos, 
                    open ? SoundEvents.BARREL_OPEN : SoundEvents.BARREL_CLOSE, 
                    SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            } else if (limitedBarrelBlockClass != null && limitedBarrelBlockClass.isInstance(block)) {
                // Limited Barrel sound (same as barrel)
                level.playSound(null, pos, 
                    open ? SoundEvents.BARREL_OPEN : SoundEvents.BARREL_CLOSE, 
                    SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            }
        } catch (Exception e) {
            // Failed to set open state - ignore silently
        }
    }
    
    /**
     * Sends a StorageOpennessPayload packet to all clients tracking the chunk.
     * This syncs the chest open/close animation to clients.
     * NeoForge 1.21.x uses PacketDistributor.sendToPlayersTrackingChunk
     */
    private void sendOpennessPacket(net.minecraft.server.level.ServerLevel serverLevel, BlockPos pos, boolean open) {
        if (storageOpennessPayloadConstructor == null || sendToPlayersTrackingChunkMethod == null) {
            return;
        }
        
        try {
            // Create the StorageOpennessPayload(pos, shouldBeOpen)
            Object payload = storageOpennessPayloadConstructor.newInstance(pos, open);
            
            // Get the ChunkPos
            net.minecraft.world.level.ChunkPos chunkPos = serverLevel.getChunkAt(pos).getPos();
            
            // Create empty varargs array for the additional payloads parameter
            Class<?> customPacketPayloadClass = Class.forName("net.minecraft.network.protocol.common.custom.CustomPacketPayload");
            Object emptyPayloadsArray = java.lang.reflect.Array.newInstance(customPacketPayloadClass, 0);
            
            // Call PacketDistributor.sendToPlayersTrackingChunk(serverLevel, chunkPos, payload, emptyArray)
            sendToPlayersTrackingChunkMethod.invoke(null, serverLevel, chunkPos, payload, emptyPayloadsArray);
        } catch (Exception e) {
            // Failed to send packet - animation won't play on clients but items still work
        }
    }
    
    /**
     * Get a Container wrapper for SophisticatedStorage containers.
     * Uses reflection to access NeoForge's IItemHandler capability and wraps it as Container.
     */
    @Override
    @Nullable
    public Container getContainer(BlockEntity blockEntity, Level level, BlockPos pos) {
        if (!isValidContainer(level.getBlockState(pos))) {
            return null;
        }
        
        if (itemHandlerCapability == null || iItemHandlerClass == null) {
            return null;
        }
        
        try {
            // NeoForge 1.21 uses BlockCapability.getCapability(level, pos, state, blockEntity, context)
            // The capability object (Capabilities.ItemHandler.BLOCK) has the getCapability method
            Class<?> blockCapabilityClass = Class.forName("net.neoforged.neoforge.capabilities.BlockCapability");
            Method getCapabilityMethod = blockCapabilityClass.getMethod("getCapability", Level.class, BlockPos.class, BlockState.class, BlockEntity.class, Object.class);
            
            BlockState state = level.getBlockState(pos);
            Object itemHandler = getCapabilityMethod.invoke(itemHandlerCapability, level, pos, state, blockEntity, null);
            
            if (itemHandler == null) {
                return null;
            }
            
            // Create a wrapper that adapts IItemHandler to Container
            return new ItemHandlerContainerWrapper(itemHandler, iItemHandlerClass);
            
        } catch (Exception e) {
            // Failed to get container - return null silently
            return null;
        }
    }
    
    /**
     * Wrapper class that adapts NeoForge's IItemHandler to Minecraft's Container interface.
     * Uses reflection to call IItemHandler methods.
     */
    private static class ItemHandlerContainerWrapper implements Container {
        private final Object itemHandler;
        private final Method getSlotsMethod;
        private final Method getStackInSlotMethod;
        private final Method insertItemMethod;
        private final Method extractItemMethod;
        private final Method getSlotLimitMethod;
        
        public ItemHandlerContainerWrapper(Object itemHandler, Class<?> iItemHandlerClass) throws Exception {
            this.itemHandler = itemHandler;
            this.getSlotsMethod = iItemHandlerClass.getMethod("getSlots");
            this.getStackInSlotMethod = iItemHandlerClass.getMethod("getStackInSlot", int.class);
            this.insertItemMethod = iItemHandlerClass.getMethod("insertItem", int.class, ItemStack.class, boolean.class);
            this.extractItemMethod = iItemHandlerClass.getMethod("extractItem", int.class, int.class, boolean.class);
            this.getSlotLimitMethod = iItemHandlerClass.getMethod("getSlotLimit", int.class);
        }
        
        @Override
        public int getContainerSize() {
            try {
                return (Integer) getSlotsMethod.invoke(itemHandler);
            } catch (Exception e) {
                return 0;
            }
        }
        
        @Override
        public boolean isEmpty() {
            for (int i = 0; i < getContainerSize(); i++) {
                if (!getItem(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public ItemStack getItem(int slot) {
            try {
                return (ItemStack) getStackInSlotMethod.invoke(itemHandler, slot);
            } catch (Exception e) {
                return ItemStack.EMPTY;
            }
        }
        
        @Override
        public ItemStack removeItem(int slot, int amount) {
            try {
                return (ItemStack) extractItemMethod.invoke(itemHandler, slot, amount, false);
            } catch (Exception e) {
                return ItemStack.EMPTY;
            }
        }
        
        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            try {
                ItemStack current = getItem(slot);
                if (current.isEmpty()) return ItemStack.EMPTY;
                return (ItemStack) extractItemMethod.invoke(itemHandler, slot, current.getCount(), false);
            } catch (Exception e) {
                return ItemStack.EMPTY;
            }
        }
        
        @Override
        public void setItem(int slot, ItemStack stack) {
            try {
                // First extract everything from the slot
                extractItemMethod.invoke(itemHandler, slot, 64, false);
                // Then insert the new stack
                if (!stack.isEmpty()) {
                    insertItemMethod.invoke(itemHandler, slot, stack, false);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        
        @Override
        public int getMaxStackSize() {
            try {
                if (getContainerSize() > 0) {
                    return (Integer) getSlotLimitMethod.invoke(itemHandler, 0);
                }
            } catch (Exception e) {
                // Ignore
            }
            return 64;
        }
        
        @Override
        public void setChanged() {
            // IItemHandler doesn't have setChanged, but the storage should auto-save
        }
        
        @Override
        public boolean stillValid(Player player) {
            return true;
        }
        
        @Override
        public void clearContent() {
            for (int i = 0; i < getContainerSize(); i++) {
                removeItemNoUpdate(i);
            }
        }
    }
}
