package com.github.smallinger.copperagebackport.block.shelf;

import com.github.smallinger.copperagebackport.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

/**
 * Block entity for the shelf block that stores up to 3 items.
 */
public class ShelfBlockEntity extends BlockEntity implements ListBackedContainer, WorldlyContainer {
    
    public static final int MAX_ITEMS = 3;
    private static final String ITEMS_TAG = "Items";
    private static final String ALIGN_ITEMS_TO_BOTTOM_TAG = "align_items_to_bottom";
    
    // Slots array for all faces - all 3 slots accessible
    private static final int[] SLOTS = new int[]{0, 1, 2};
    
    private final NonNullList<ItemStack> items = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);
    private boolean alignItemsToBottom = false;

    public ShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHELF_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    /**
     * Swaps an item in the given slot with the provided stack.
     * @return The item that was previously in the slot
     */
    public ItemStack swapItemNoUpdate(int slot, ItemStack stack) {
        ItemStack oldItem = this.removeItemNoUpdate(slot);
        this.setItemNoUpdate(slot, stack.copy());
        return oldItem;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items, true);
        tag.putBoolean(ALIGN_ITEMS_TO_BOTTOM_TAG, this.alignItemsToBottom);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items);
        this.alignItemsToBottom = tag.getBoolean(ALIGN_ITEMS_TO_BOTTOM_TAG);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, this.items, true);
        tag.putBoolean(ALIGN_ITEMS_TO_BOTTOM_TAG, this.alignItemsToBottom);
        return tag;
    }

    public boolean getAlignItemsToBottom() {
        return this.alignItemsToBottom;
    }

    public void setAlignItemsToBottom(boolean alignItemsToBottom) {
        this.alignItemsToBottom = alignItemsToBottom;
        this.setChanged();
    }

    public float getVisualRotationYInDegrees() {
        return this.getBlockState().getValue(ShelfBlock.FACING).getOpposite().toYRot();
    }

    // WorldlyContainer implementation
    @Override
    public int[] getSlotsForFace(Direction side) {
        // Allow interaction from all sides for hoppers/droppers
        // Items are added/removed left to right (slots 0, 1, 2)
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        // Check if we can place the item in this slot
        ItemStack existing = this.getItem(slot);
        
        if (existing.isEmpty()) {
            // Empty slot - only allow if all previous slots are filled or have the same item
            for (int i = 0; i < slot; i++) {
                ItemStack prev = this.getItem(i);
                if (prev.isEmpty()) {
                    return false; // Must fill from left to right
                }
                // If previous slot has same item and isn't full, should go there first
                if (ItemStack.isSameItemSameTags(prev, stack) && prev.getCount() < prev.getMaxStackSize()) {
                    return false;
                }
            }
            return true;
        } else {
            // Slot has items - can only add if same item type and not full
            return ItemStack.isSameItemSameTags(existing, stack) && existing.getCount() < existing.getMaxStackSize();
        }
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        // Can take from any slot that has items
        // But prefer taking from rightmost slot first (reverse order)
        for (int i = MAX_ITEMS - 1; i > slot; i--) {
            if (!this.getItem(i).isEmpty()) {
                return false; // There's a slot to the right with items, take from there first
            }
        }
        return true;
    }
    
    @Override
    public int getMaxStackSize() {
        return 64; // Allow full stacking
    }
}
