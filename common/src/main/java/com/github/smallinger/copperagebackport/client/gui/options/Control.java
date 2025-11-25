package com.github.smallinger.copperagebackport.client.gui.options;

import com.github.smallinger.copperagebackport.client.gui.options.control.ControlElement;
import com.github.smallinger.copperagebackport.client.gui.widget.Dim2i;

/**
 * Interface for option controls (UI widgets for editing options).
 */
public interface Control<T> {
    
    /**
     * Creates the control element for rendering.
     * @param dim The position and dimensions
     * @return The control element
     */
    ControlElement<T> createElement(Dim2i dim);
    
    /**
     * Gets the option this control is for.
     * @return The option
     */
    Option<T> getOption();
    
    /**
     * Gets the maximum width needed for the control's value display.
     * @return The width in pixels
     */
    int getMaxWidth();
}
