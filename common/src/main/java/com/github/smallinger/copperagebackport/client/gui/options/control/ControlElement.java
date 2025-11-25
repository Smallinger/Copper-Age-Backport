package com.github.smallinger.copperagebackport.client.gui.options.control;

import com.github.smallinger.copperagebackport.client.gui.options.Option;
import com.github.smallinger.copperagebackport.client.gui.widget.AbstractWidget;
import com.github.smallinger.copperagebackport.client.gui.widget.Dim2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Base class for option control UI elements.
 */
public abstract class ControlElement<T> extends AbstractWidget {
    
    protected final Option<T> option;
    protected Dim2i dim;

    protected static final int LABEL_COLOR = 0xFFFFFFFF;
    protected static final int LABEL_COLOR_DISABLED = 0xFF808080;
    protected static final int BACKGROUND_COLOR = 0x90000000;
    protected static final int BACKGROUND_COLOR_HOVERED = 0xE0000000;

    public ControlElement(Option<T> option, Dim2i dim) {
        this.option = option;
        this.dim = dim;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.hovered = this.dim.containsCursor(mouseX, mouseY);
        
        // Draw background
        int bgColor = hovered ? BACKGROUND_COLOR_HOVERED : BACKGROUND_COLOR;
        this.drawRect(graphics, dim.x(), dim.y(), dim.getLimitX(), dim.getLimitY(), bgColor);
        
        // Draw label
        Component label = option.getName();
        int labelColor = option.isAvailable() ? LABEL_COLOR : LABEL_COLOR_DISABLED;
        this.drawString(graphics, label, dim.x() + 6, dim.y() + (dim.height() - 8) / 2, labelColor);
        
        // Draw control
        renderControl(graphics, mouseX, mouseY, partialTick);
        
        // Draw focus border
        if (this.focused) {
            this.drawBorder(graphics, dim.x(), dim.y(), dim.getLimitX(), dim.getLimitY(), 0xFFFFFFFF);
        }
    }

    /**
     * Render the control part (right side).
     */
    protected abstract void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /**
     * Gets the control area dimension.
     */
    protected abstract Dim2i getControlDim();

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.dim.containsCursor(mouseX, mouseY);
    }

    protected boolean isMouseOverControl(double mouseX, double mouseY) {
        return getControlDim().containsCursor(mouseX, mouseY);
    }

    public Option<T> getOption() {
        return option;
    }

    public Dim2i getDim() {
        return dim;
    }
    
    public void setDim(Dim2i dim) {
        this.dim = dim;
    }
}
