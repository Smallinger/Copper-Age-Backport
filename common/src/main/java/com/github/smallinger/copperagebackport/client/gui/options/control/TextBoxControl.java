package com.github.smallinger.copperagebackport.client.gui.options.control;

import com.github.smallinger.copperagebackport.client.gui.options.Control;
import com.github.smallinger.copperagebackport.client.gui.options.Option;
import com.github.smallinger.copperagebackport.client.gui.widget.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A read-only text control that displays multi-line descriptive text with paragraph support.
 * The text spans the full width and replaces the normal label/control layout.
 * Use \n in translation strings to create paragraph breaks.
 */
public class TextBoxControl implements Control<String> {

    private static final int PADDING = 6;
    private static final int PARAGRAPH_SPACING = 6;
    
    private final Option<String> option;

    public TextBoxControl(Option<String> option) {
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
        return 0; // No control on the right side
    }
    
    @Override
    public int getHeight(int availableWidth) {
        return calculateHeight(option.getValue(), availableWidth);
    }
    
    private static int calculateHeight(String translationKey, int availableWidth) {
        if (translationKey == null || translationKey.isEmpty()) {
            return 18;
        }
        
        Font font = Minecraft.getInstance().font;
        String text = Component.translatable(translationKey).getString();
        int maxWidth = availableWidth - (PADDING * 2);
        
        // Split by paragraph breaks
        String[] paragraphs = text.split("\n");
        int totalHeight = PADDING;
        
        for (int i = 0; i < paragraphs.length; i++) {
            String paragraph = paragraphs[i].trim();
            if (paragraph.isEmpty()) {
                totalHeight += PARAGRAPH_SPACING;
            } else {
                List<net.minecraft.util.FormattedCharSequence> lines = font.split(Component.literal(paragraph), maxWidth);
                totalHeight += lines.size() * (font.lineHeight + 2);
                
                // Add paragraph spacing between paragraphs (not after last)
                if (i < paragraphs.length - 1) {
                    totalHeight += PARAGRAPH_SPACING;
                }
            }
        }
        
        totalHeight += PADDING;
        return Math.max(18, totalHeight);
    }

    private static class Element extends ControlElement<String> {
        
        private static final int TEXT_COLOR = 0xFFAAAAAA;

        public Element(Option<String> option, Dim2i dim) {
            super(option, dim);
        }

        @Override
        protected Dim2i getControlDim() {
            // No separate control area - text spans full width
            return new Dim2i(dim.x(), dim.y(), 0, 0);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            this.hovered = this.dim.containsCursor(mouseX, mouseY);
            
            // Draw background covering the full area
            int bgColor = hovered ? BACKGROUND_COLOR_HOVERED : BACKGROUND_COLOR;
            this.drawRect(graphics, dim.x(), dim.y(), dim.getLimitX(), dim.getLimitY(), bgColor);
            
            // Draw the descriptive text with paragraph support
            Font font = Minecraft.getInstance().font;
            String value = option.getValue();
            if (value != null && !value.isEmpty()) {
                String text = Component.translatable(value).getString();
                int maxWidth = dim.width() - (PADDING * 2);
                
                // Split by paragraph breaks
                String[] paragraphs = text.split("\n");
                int y = dim.y() + PADDING;
                
                for (int i = 0; i < paragraphs.length; i++) {
                    String paragraph = paragraphs[i].trim();
                    if (paragraph.isEmpty()) {
                        y += PARAGRAPH_SPACING;
                    } else {
                        List<net.minecraft.util.FormattedCharSequence> lines = font.split(Component.literal(paragraph), maxWidth);
                        for (net.minecraft.util.FormattedCharSequence line : lines) {
                            graphics.drawString(font, line, dim.x() + PADDING, y, TEXT_COLOR, false);
                            y += font.lineHeight + 2;
                        }
                        
                        // Add paragraph spacing between paragraphs (not after last)
                        if (i < paragraphs.length - 1) {
                            y += PARAGRAPH_SPACING;
                        }
                    }
                }
            }
            
            // Draw focus border
            if (this.focused) {
                this.drawBorder(graphics, dim.x(), dim.y(), dim.getLimitX(), dim.getLimitY(), 0xFFFFFFFF);
            }
        }

        @Override
        protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // Nothing - we override render() completely
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return false;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return false;
        }
    }
}
