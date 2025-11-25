package com.github.smallinger.copperagebackport.client.gui.options.control;

import com.github.smallinger.copperagebackport.client.gui.options.Control;
import com.github.smallinger.copperagebackport.client.gui.options.Option;
import com.github.smallinger.copperagebackport.client.gui.widget.Dim2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * A slider control for integer options with min/max range.
 */
public class SliderControl implements Control<Integer> {

    private final Option<Integer> option;
    private final int min;
    private final int max;
    private final int step;
    private final String suffix;

    public SliderControl(Option<Integer> option, int min, int max, int step, String suffix) {
        this.option = option;
        this.min = min;
        this.max = max;
        this.step = step;
        this.suffix = suffix;
    }

    public SliderControl(Option<Integer> option, int min, int max, int step) {
        this(option, min, max, step, "");
    }

    @Override
    public ControlElement<Integer> createElement(Dim2i dim) {
        return new Element(option, dim, min, max, step, suffix);
    }

    @Override
    public Option<Integer> getOption() {
        return option;
    }

    @Override
    public int getMaxWidth() {
        return 100; // Slider width
    }

    private static class Element extends ControlElement<Integer> {
        
        private static final int SLIDER_WIDTH = 100;
        private static final int SLIDER_HEIGHT = 6;
        private static final int HANDLE_WIDTH = 8;
        private static final int HANDLE_HEIGHT = 14;
        
        private static final int COLOR_TRACK = 0xFF3A3A3A;
        private static final int COLOR_TRACK_FILLED = 0xFFB07830;
        private static final int COLOR_HANDLE = 0xFFFFFFFF;
        private static final int COLOR_HANDLE_HOVERED = 0xFFFFDD88;
        
        private final int min;
        private final int max;
        private final int step;
        private final String suffix;
        private boolean dragging;

        public Element(Option<Integer> option, Dim2i dim, int min, int max, int step, String suffix) {
            super(option, dim);
            this.min = min;
            this.max = max;
            this.step = step;
            this.suffix = suffix;
        }

        @Override
        protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            Dim2i controlDim = getControlDim();
            int sliderX = controlDim.x();
            int sliderY = dim.y() + (dim.height() - SLIDER_HEIGHT) / 2;
            
            boolean enabled = option.isAvailable();
            int value = option.getValue();
            float progress = (float)(value - min) / (max - min);
            
            // Draw track background
            this.drawRect(graphics, sliderX, sliderY, sliderX + SLIDER_WIDTH, sliderY + SLIDER_HEIGHT, COLOR_TRACK);
            
            // Draw filled portion
            int filledWidth = (int)(SLIDER_WIDTH * progress);
            if (filledWidth > 0) {
                this.drawRect(graphics, sliderX, sliderY, sliderX + filledWidth, sliderY + SLIDER_HEIGHT, 
                    enabled ? COLOR_TRACK_FILLED : 0xFF806040);
            }
            
            // Draw handle
            int handleX = sliderX + filledWidth - HANDLE_WIDTH / 2;
            int handleY = dim.y() + (dim.height() - HANDLE_HEIGHT) / 2;
            
            boolean handleHovered = isMouseOverHandle(mouseX, mouseY, handleX);
            int handleColor = (handleHovered || dragging) ? COLOR_HANDLE_HOVERED : COLOR_HANDLE;
            
            this.drawRect(graphics, handleX, handleY, handleX + HANDLE_WIDTH, handleY + HANDLE_HEIGHT, handleColor);
            this.drawBorder(graphics, handleX, handleY, handleX + HANDLE_WIDTH, handleY + HANDLE_HEIGHT, 
                focused ? 0xFFFFFFFF : 0xFF404040);
            
            // Draw value text
            Component valueText = Component.literal(value + suffix);
            int textX = sliderX - 8 - getStringWidth(valueText);
            int textY = dim.y() + (dim.height() - 8) / 2;
            this.drawString(graphics, valueText, textX, textY, enabled ? LABEL_COLOR : LABEL_COLOR_DISABLED);
        }

        private boolean isMouseOverHandle(double mouseX, double mouseY, int handleX) {
            int handleY = dim.y() + (dim.height() - HANDLE_HEIGHT) / 2;
            return mouseX >= handleX && mouseX < handleX + HANDLE_WIDTH &&
                   mouseY >= handleY && mouseY < handleY + HANDLE_HEIGHT;
        }

        @Override
        protected Dim2i getControlDim() {
            int controlX = dim.x() + dim.width() - SLIDER_WIDTH - 6;
            int controlY = dim.y() + (dim.height() - SLIDER_HEIGHT) / 2;
            return new Dim2i(controlX, controlY, SLIDER_WIDTH, SLIDER_HEIGHT);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!option.isAvailable()) {
                return false;
            }
            
            if (button == 0 && isMouseOverControl(mouseX, mouseY)) {
                dragging = true;
                updateValue(mouseX);
                playClickSound();
                return true;
            }
            
            return false;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (dragging) {
                updateValue(mouseX);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (dragging) {
                dragging = false;
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
            if (!option.isAvailable()) {
                return false;
            }
            
            if (isMouseOverControl(mouseX, mouseY)) {
                int value = option.getValue();
                if (scrollDelta > 0) {
                    value = Math.min(max, value + step);
                } else if (scrollDelta < 0) {
                    value = Math.max(min, value - step);
                }
                option.setValue(value);
                return true;
            }
            return false;
        }

        private void updateValue(double mouseX) {
            Dim2i controlDim = getControlDim();
            float progress = Mth.clamp((float)(mouseX - controlDim.x()) / SLIDER_WIDTH, 0, 1);
            int rawValue = min + (int)(progress * (max - min));
            int steppedValue = ((rawValue - min + step / 2) / step) * step + min;
            option.setValue(Mth.clamp(steppedValue, min, max));
        }
    }
}
