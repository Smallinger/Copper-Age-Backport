package com.github.smallinger.copperagebackport.block.shelf;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * Interface for containers backed by a NonNullList.
 * Provides default implementations for common Container methods.
 */
public interface ListBackedContainer extends Container {
    NonNullList<ItemStack> getItems();

    default int count() {
        return (int) this.getItems().stream().filter(Predicate.not(ItemStack::isEmpty)).count();
    }

    @Override
    default int getContainerSize() {
        return this.getItems().size();
    }

    @Override
    default void clearContent() {
        this.getItems().clear();
    }

    @Override
    default boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    default ItemStack getItem(int slot) {
        return this.getItems().get(slot);
    }

    @Override
    default ItemStack removeItem(int slot, int amount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), slot, amount);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    default ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.getItems(), slot);
    }

    @Override
    default boolean canPlaceItem(int slot, ItemStack stack) {
        if (!this.acceptsItemType(stack)) {
            return false;
        }
        ItemStack existing = this.getItem(slot);
        if (existing.isEmpty()) {
            return true;
        }
        // Can stack if same item and not full (use isSameItemSameTags for 1.20.1)
        return ItemStack.isSameItemSameTags(existing, stack) && existing.getCount() < existing.getMaxStackSize();
    }

    default boolean acceptsItemType(ItemStack stack) {
        return true;
    }

    @Override
    default void setItem(int slot, ItemStack stack) {
        this.setItemNoUpdate(slot, stack);
        this.setChanged();
    }

    default void setItemNoUpdate(int slot, ItemStack stack) {
        this.getItems().set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }
}
