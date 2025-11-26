package com.github.smallinger.copperagebackport.client.gui.options.control;

import com.github.smallinger.copperagebackport.client.gui.options.Control;
import com.github.smallinger.copperagebackport.client.gui.options.Option;
import com.github.smallinger.copperagebackport.client.gui.widget.Dim2i;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

/**
 * A text field control for integer values with optional validation.
 * Shows value in a secondary format (e.g., Minecraft days) for better readability.
 */
public class IntegerFieldControl implements Control<Integer> {

    private final Option<Integer> option;
    private final int minValue;
    private final int maxValue;
    private final IntSupplier dynamicMin;
    private final ValueFormatter formatter;

    /**
     * Creates an integer field with static min/max bounds.
     */
    public IntegerFieldControl(Option<Integer> option, int min, int max, ValueFormatter formatter) {
        this.option = option;
        this.minValue = min;
        this.maxValue = max;
        this.dynamicMin = null;
        this.formatter = formatter;
    }

    /**
     * Creates an integer field with a dynamic minimum (useful for "to" fields that must be >= "from").
     */
    public IntegerFieldControl(Option<Integer> option, IntSupplier dynamicMin, int max, ValueFormatter formatter) {
        this.option = option;
        this.minValue = 0;
        this.maxValue = max;
        this.dynamicMin = dynamicMin;
        this.formatter = formatter;
    }

    @Override
    public ControlElement<Integer> createElement(Dim2i dim) {
        return new Element(option, dim, minValue, maxValue, dynamicMin, formatter);
    }

    @Override
    public Option<Integer> getOption() {
        return option;
    }

    @Override
    public int getMaxWidth() {
        return 100;
    }

    /**
     * Interface for formatting the display value.
     */
    @FunctionalInterface
    public interface ValueFormatter {
        Component format(int value);

        /**
         * Formats ticks as Minecraft days.
         */
        static ValueFormatter minecraftDays() {
            return value -> {
                float days = value / 24000f;
                if (days == (int) days) {
                    return Component.translatable("config.copperagebackport.days", (int) days);
                }
                return Component.translatable("config.copperagebackport.days_decimal", String.format("%.1f", days));
            };
        }

        /**
         * Formats as percentage.
         */
        static ValueFormatter percentage() {
            return value -> Component.literal(value + "%");
        }

        /**
         * Formats with a simple suffix.
         */
        static ValueFormatter withSuffix(String suffix) {
            return value -> Component.literal(value + " " + suffix);
        }

        /**
         * Plain number display.
         */
        static ValueFormatter number() {
            return value -> Component.literal(String.valueOf(value));
        }
    }

    private static class Element extends ControlElement<Integer> {

        private static final int FIELD_WIDTH = 70;
        private static final int FIELD_HEIGHT = 16;

        private static final int COLOR_BACKGROUND = 0xFF1E1E1E;
        private static final int COLOR_BORDER = 0xFF4A4A4A;
        private static final int COLOR_BORDER_FOCUSED = 0xFFB07830;
        private static final int COLOR_BORDER_ERROR = 0xFFFF4444;
        private static final int COLOR_TEXT = 0xFFFFFFFF;
        private static final int COLOR_TEXT_DISABLED = 0xFF808080;
        private static final int COLOR_CURSOR = 0xFFFFFFFF;
        private static final int COLOR_SELECTION = 0xFF3366CC;
        private static final int COLOR_HINT = 0xFF888888;

        private final int minValue;
        private final int maxValue;
        private final IntSupplier dynamicMin;
        private final ValueFormatter formatter;

        private String text;
        private int cursorPosition;
        private int selectionStart;
        private int selectionEnd;
        private boolean editing;
        private boolean hasError;
        private boolean justStartedEditing; // Flag to prevent setFocused(false) from closing immediately

        public Element(Option<Integer> option, Dim2i dim, int min, int max, IntSupplier dynamicMin, ValueFormatter formatter) {
            super(option, dim);
            this.minValue = min;
            this.maxValue = max;
            this.dynamicMin = dynamicMin;
            this.formatter = formatter;
            this.text = String.valueOf(option.getValue());
            this.cursorPosition = text.length();
            this.selectionStart = 0;
            this.selectionEnd = 0;
        }

        private int getEffectiveMin() {
            return dynamicMin != null ? Math.max(minValue, dynamicMin.getAsInt()) : minValue;
        }

        @Override
        protected void renderControl(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            Dim2i controlDim = getControlDim();
            int fieldX = controlDim.x();
            int fieldY = dim.y() + (dim.height() - FIELD_HEIGHT) / 2;

            boolean enabled = option.isAvailable();

            // Validate current value (check both text during editing AND actual option value)
            int effectiveMin = getEffectiveMin();
            hasError = false;
            
            if (editing) {
                // During editing, validate the text being typed
                try {
                    int value = Integer.parseInt(text);
                    hasError = value < effectiveMin || value > maxValue;
                } catch (NumberFormatException e) {
                    hasError = !text.isEmpty();
                }
            } else {
                // When not editing, check if the actual option value is valid
                // This catches cases where min changed dynamically (e.g., weatheringTickFrom increased)
                int currentValue = option.getValue();
                hasError = currentValue < effectiveMin || currentValue > maxValue;
            }

            // Draw field background
            drawRect(graphics, fieldX, fieldY, fieldX + FIELD_WIDTH, fieldY + FIELD_HEIGHT, COLOR_BACKGROUND);

            // Draw border - red if value is invalid
            int borderColor = hasError ? COLOR_BORDER_ERROR : (editing ? COLOR_BORDER_FOCUSED : COLOR_BORDER);
            drawBorder(graphics, fieldX, fieldY, fieldX + FIELD_WIDTH, fieldY + FIELD_HEIGHT, borderColor);

            // Draw text
            String displayText = editing ? text : String.valueOf(option.getValue());
            int textColor = enabled ? COLOR_TEXT : COLOR_TEXT_DISABLED;
            int textX = fieldX + 4;
            int textY = fieldY + (FIELD_HEIGHT - 8) / 2;

            // Selection highlight
            if (editing && selectionStart != selectionEnd) {
                int selStart = Math.min(selectionStart, selectionEnd);
                int selEnd = Math.max(selectionStart, selectionEnd);
                String beforeSel = displayText.substring(0, Math.min(selStart, displayText.length()));
                String selText = displayText.substring(Math.min(selStart, displayText.length()), Math.min(selEnd, displayText.length()));
                int selX1 = textX + font.width(beforeSel);
                int selX2 = selX1 + font.width(selText);
                drawRect(graphics, selX1, textY - 1, selX2, textY + 9, COLOR_SELECTION);
            }

            drawString(graphics, displayText, textX, textY, textColor);

            // Draw cursor (no blinking - always visible when editing)
            if (editing) {
                String beforeCursor = displayText.substring(0, Math.min(cursorPosition, displayText.length()));
                int cursorX = textX + font.width(beforeCursor);
                drawRect(graphics, cursorX, textY - 1, cursorX + 1, textY + 9, COLOR_CURSOR);
            }

            // Draw formatted hint (e.g., "21.0 Tage") to the left of the field
            if (formatter != null && !editing && !hasError) {
                Component hint = formatter.format(option.getValue());
                int hintX = fieldX - 8 - getStringWidth(hint);
                drawString(graphics, hint, hintX, textY, COLOR_HINT);
            }

            // Draw error hint if value is invalid
            if (hasError) {
                Component errorHint = Component.literal("Min: " + effectiveMin);
                int hintX = fieldX - 8 - getStringWidth(errorHint);
                drawString(graphics, errorHint, hintX, textY, COLOR_BORDER_ERROR);
            }
        }

        @Override
        protected Dim2i getControlDim() {
            int controlX = dim.x() + dim.width() - FIELD_WIDTH - 6;
            int controlY = dim.y() + (dim.height() - FIELD_HEIGHT) / 2;
            return new Dim2i(controlX, controlY, FIELD_WIDTH, FIELD_HEIGHT);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!option.isAvailable()) {
                return false;
            }

            // Check if click is within the entire element area
            boolean clickedOnElement = dim.containsCursor(mouseX, mouseY);

            if (button == 0 && clickedOnElement) {
                if (!editing) {
                    // Start editing when clicking anywhere on the element
                    startEditing();
                    playClickSound();
                } else {
                    // If already editing and clicked on control, position cursor
                    if (isMouseOverControl(mouseX, mouseY)) {
                        Dim2i controlDim = getControlDim();
                        int relX = (int) mouseX - controlDim.x() - 4;
                        cursorPosition = getCharIndexAtX(relX);
                        selectionStart = cursorPosition;
                        selectionEnd = cursorPosition;
                    } else {
                        // Clicked on label while editing - just finish editing
                        finishEditing();
                    }
                }
                return true;
            } else if (editing) {
                // Clicked outside the element - finish editing but don't consume the click
                finishEditing();
            }

            return false;
        }

        private int getCharIndexAtX(int x) {
            int idx = 0;
            int width = 0;
            while (idx < text.length()) {
                int charWidth = font.width(String.valueOf(text.charAt(idx)));
                if (width + charWidth / 2 > x) {
                    break;
                }
                width += charWidth;
                idx++;
            }
            return idx;
        }

        private void startEditing() {
            justStartedEditing = true; // Prevent setFocused(false) from immediately closing
            editing = true;
            text = String.valueOf(option.getValue());
            cursorPosition = text.length();
            selectionStart = 0;
            selectionEnd = text.length();
        }

        private void finishEditing() {
            editing = false;
            try {
                int value = Integer.parseInt(text);
                int effectiveMin = getEffectiveMin();
                value = Mth.clamp(value, effectiveMin, maxValue);
                option.setValue(value);
            } catch (NumberFormatException e) {
                // Keep old value
            }
            text = String.valueOf(option.getValue());
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!editing) {
                if (focused && (keyCode == 257 || keyCode == 335)) { // Enter or numpad enter
                    startEditing();
                    return true;
                }
                return false;
            }

            boolean ctrl = (modifiers & 2) != 0; // GLFW_MOD_CONTROL

            switch (keyCode) {
                case 259: // Backspace
                    if (hasSelection()) {
                        deleteSelection();
                    } else if (cursorPosition > 0) {
                        text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                        cursorPosition--;
                    }
                    clearSelection();
                    return true;

                case 261: // Delete
                    if (hasSelection()) {
                        deleteSelection();
                    } else if (cursorPosition < text.length()) {
                        text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                    }
                    clearSelection();
                    return true;

                case 263: // Left arrow
                    if (cursorPosition > 0) {
                        cursorPosition--;
                    }
                    if ((modifiers & 1) == 0) { // Not shift
                        clearSelection();
                    } else {
                        selectionEnd = cursorPosition;
                    }
                    return true;

                case 262: // Right arrow
                    if (cursorPosition < text.length()) {
                        cursorPosition++;
                    }
                    if ((modifiers & 1) == 0) {
                        clearSelection();
                    } else {
                        selectionEnd = cursorPosition;
                    }
                    return true;

                case 268: // Home
                    cursorPosition = 0;
                    if ((modifiers & 1) == 0) {
                        clearSelection();
                    } else {
                        selectionEnd = cursorPosition;
                    }
                    return true;

                case 269: // End
                    cursorPosition = text.length();
                    if ((modifiers & 1) == 0) {
                        clearSelection();
                    } else {
                        selectionEnd = cursorPosition;
                    }
                    return true;

                case 257: // Enter
                case 335: // Numpad Enter
                    finishEditing();
                    return true;

                case 256: // Escape
                    text = String.valueOf(option.getValue());
                    editing = false;
                    return true;

                case 65: // A (select all)
                    if (ctrl) {
                        selectionStart = 0;
                        selectionEnd = text.length();
                        cursorPosition = text.length();
                        return true;
                    }
                    break;
            }

            return false;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            if (!editing) {
                return false;
            }

            // Only allow digits and minus sign
            if (Character.isDigit(chr) || (chr == '-' && cursorPosition == 0 && !text.contains("-"))) {
                if (hasSelection()) {
                    deleteSelection();
                }
                text = text.substring(0, cursorPosition) + chr + text.substring(cursorPosition);
                cursorPosition++;
                clearSelection();
                return true;
            }

            return false;
        }

        private boolean hasSelection() {
            return selectionStart != selectionEnd;
        }

        private void clearSelection() {
            selectionStart = cursorPosition;
            selectionEnd = cursorPosition;
        }

        private void deleteSelection() {
            int start = Math.min(selectionStart, selectionEnd);
            int end = Math.max(selectionStart, selectionEnd);
            text = text.substring(0, start) + text.substring(end);
            cursorPosition = start;
            clearSelection();
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
            if (!option.isAvailable()) {
                return false;
            }

            if (isMouseOverControl(mouseX, mouseY) || editing) {
                int effectiveMin = getEffectiveMin();
                int current = option.getValue();
                int step = 24000; // 1 Minecraft day

                if (delta > 0) {
                    option.setValue(Math.min(maxValue, current + step));
                } else if (delta < 0) {
                    option.setValue(Math.max(effectiveMin, current - step));
                }

                if (!editing) {
                    text = String.valueOf(option.getValue());
                }
                return true;
            }
            return false;
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!focused && editing) {
                // Don't close if we just started editing (click caused both unfocus and refocus)
                if (justStartedEditing) {
                    justStartedEditing = false;
                    return;
                }
                finishEditing();
            }
            if (focused) {
                justStartedEditing = false;
            }
        }
    }
}
