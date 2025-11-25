package com.github.smallinger.copperagebackport.client.gui.options.control;

import com.github.smallinger.copperagebackport.client.gui.options.Control;
import com.github.smallinger.copperagebackport.client.gui.options.Option;
import com.github.smallinger.copperagebackport.client.gui.widget.Dim2i;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * A toggle/checkbox control for boolean options.
 */
public class TickBoxControl implements Control<Boolean> {

    private final Option<Boolean> option;

    public TickBoxControl(Option<Boolean> option) {
        this.option = option;
    }

    @Override
    public ControlElement<Boolean> createElement(Dim2i dim) {
        return new Element(option, dim);
    }

    @Override
    public Option<Boolean> getOption() {
        return option;
    }

    @Override
    public int getMaxWidth() {
        return 36; // Width of toggle button
    }

    private static class Element extends ControlElement<Boolean> {
        
        private static final int TOGGLE_WIDTH = 36;
        private static final int TOGGLE_HEIGHT = 16;
        private static final int KNOB_SIZE = 12;
        
        private static final int COLOR_ON = 0xFF4CAF50;
        private static final int COLOR_OFF = 0xFF9E9E9E;
        private static final int COLOR_DISABLED = 0xFF616161;
        private static final int COLOR_KNOB = 0xFFFFFFFF;

        public Element(Option<Boolean> option, Dim2i dim) {
            super(option, dim);
        }

        @Override
        protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            Dim2i controlDim = getControlDim();
            int toggleX = controlDim.x();
            int toggleY = dim.y() + (dim.height() - TOGGLE_HEIGHT) / 2;
            
            boolean enabled = option.isAvailable();
            boolean value = option.getValue();
            
            // Draw toggle background
            int bgColor = !enabled ? COLOR_DISABLED : (value ? COLOR_ON : COLOR_OFF);
            this.drawRect(graphics, toggleX, toggleY, toggleX + TOGGLE_WIDTH, toggleY + TOGGLE_HEIGHT, bgColor);
            
            // Draw toggle border
            int borderColor = focused ? 0xFFFFFFFF : 0xFF404040;
            this.drawBorder(graphics, toggleX, toggleY, toggleX + TOGGLE_WIDTH, toggleY + TOGGLE_HEIGHT, borderColor);
            
            // Draw knob
            int knobPadding = 2;
            int knobX = value ? toggleX + TOGGLE_WIDTH - KNOB_SIZE - knobPadding : toggleX + knobPadding;
            int knobY = toggleY + knobPadding;
            this.drawRect(graphics, knobX, knobY, knobX + KNOB_SIZE, knobY + KNOB_SIZE, COLOR_KNOB);
            
            // Draw value text
            Component valueText = value ? 
                Component.translatable("options.on").withStyle(ChatFormatting.GREEN) :
                Component.translatable("options.off").withStyle(ChatFormatting.RED);
            
            int textX = toggleX - 6 - getStringWidth(valueText);
            int textY = dim.y() + (dim.height() - 8) / 2;
            this.drawString(graphics, valueText, textX, textY, 0xFFFFFFFF);
        }

        @Override
        protected Dim2i getControlDim() {
            int controlX = dim.x() + dim.width() - TOGGLE_WIDTH - 6;
            int controlY = dim.y() + (dim.height() - TOGGLE_HEIGHT) / 2;
            return new Dim2i(controlX, controlY, TOGGLE_WIDTH, TOGGLE_HEIGHT);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!option.isAvailable()) {
                return false;
            }
            
            if (button == 0 && isMouseOverControl(mouseX, mouseY)) {
                option.setValue(!option.getValue());
                playClickSound();
                return true;
            }
            
            return false;
        }
    }
}
