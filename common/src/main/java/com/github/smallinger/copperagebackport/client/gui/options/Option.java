package com.github.smallinger.copperagebackport.client.gui.options;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a single configurable option.
 */
public interface Option<T> {
    
    Component getName();
    
    @Nullable
    Component getTooltip();
    
    Control<T> getControl();
    
    T getValue();
    
    void setValue(T value);
    
    T getDefaultValue();
    
    void reset();
    
    boolean hasChanged();
    
    boolean isAvailable();
    
    void applyChanges();
}
