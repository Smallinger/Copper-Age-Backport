package com.github.smallinger.copperagebackport.client.gui.options.control;

import com.github.smallinger.copperagebackport.client.gui.options.Control;
import com.github.smallinger.copperagebackport.client.gui.options.Option;
import com.github.smallinger.copperagebackport.client.gui.widget.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * A read-only label control that displays text without any interaction.
 * Used for informational text in config screens.
 */
public class LabelControl implements Control<String> {

    private final Option<String> option;

    public LabelControl(Option<String> option) {
        this.option = option;
    }

    @Override
    public ControlElement<String> createElement(Dim2i dim) {
        return new Element(option, dim);
    }

    @Override
    public Option<String> getOption() {
        return option;
    }

    @Override
    public int getMaxWidth() {
        return 200;
    }

    private static class Element extends ControlElement<String> {
        
        private static final int TEXT_COLOR = 0xFFAAAAAA;

        public Element(Option<String> option, Dim2i dim) {
            super(option, dim);
        }

        @Override
        protected Dim2i getControlDim() {
            // Label takes up the right portion of the row
            int controlWidth = 200;
            return new Dim2i(dim.x() + dim.width() - controlWidth - 5, dim.y(), controlWidth, dim.height());
        }

        @Override
        protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            Font font = Minecraft.getInstance().font;
            String value = option.getValue();
            if (value != null && !value.isEmpty()) {
                Component text = Component.translatable(value);
                int textWidth = font.width(text);
                int x = dim.x() + dim.width() - textWidth - 5;
                int y = dim.y() + (dim.height() - font.lineHeight) / 2;
                graphics.drawString(font, text, x, y, TEXT_COLOR, false);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // Labels are not interactive
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            // Labels are not interactive
            return false;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            // Labels are not interactive
            return false;
        }
    }
}
