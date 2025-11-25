package com.github.smallinger.copperagebackport.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.SharedConstants;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * A search field widget for filtering options.
 */
public class SearchFieldWidget implements Renderable, GuiEventListener, NarratableEntry {
    
    private final Dim2i dim;
    private final Font font;
    private final Component placeholder;
    
    private String text = "";
    private boolean focused;
    private boolean hovered;
    private int cursorPosition;
    private int selectionStart;
    private Consumer<String> responder;
    
    private long lastCursorBlink;
    private boolean cursorVisible = true;

    private static final int BACKGROUND_COLOR = 0x90000000;
    private static final int BACKGROUND_COLOR_FOCUSED = 0xE0000000;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int PLACEHOLDER_COLOR = 0xFFAAAAAA;

    public SearchFieldWidget(Dim2i dim, Component placeholder) {
        this.dim = dim;
        this.font = Minecraft.getInstance().font;
        this.placeholder = placeholder;
    }

    public void setResponder(Consumer<String> responder) {
        this.responder = responder;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.hovered = this.dim.containsCursor(mouseX, mouseY);
        
        // Background
        int bgColor = this.focused ? BACKGROUND_COLOR_FOCUSED : BACKGROUND_COLOR;
        graphics.fill(dim.x(), dim.y(), dim.getLimitX(), dim.getLimitY(), bgColor);
        
        int textX = dim.x() + 6;
        int textY = dim.y() + (dim.height() - 8) / 2;
        
        if (this.text.isEmpty() && !this.focused) {
            // Draw placeholder
            graphics.drawString(font, this.placeholder, textX, textY, PLACEHOLDER_COLOR);
        } else {
            // Draw text
            graphics.drawString(font, this.text, textX, textY, TEXT_COLOR);
            
            // Draw cursor
            if (this.focused) {
                long time = System.currentTimeMillis();
                if (time - lastCursorBlink > 500) {
                    cursorVisible = !cursorVisible;
                    lastCursorBlink = time;
                }
                
                if (cursorVisible) {
                    int cursorX = textX + font.width(text.substring(0, cursorPosition));
                    graphics.fill(cursorX, textY - 1, cursorX + 1, textY + 9, 0xFFFFFFFF);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.dim.containsCursor(mouseX, mouseY)) {
            this.setFocused(true);
            if (button == 0) {
                // Position cursor based on click
                int relX = (int) mouseX - dim.x() - 6;
                this.cursorPosition = 0;
                for (int i = 0; i <= text.length(); i++) {
                    if (font.width(text.substring(0, i)) > relX) {
                        break;
                    }
                    this.cursorPosition = i;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.focused) return false;
        
        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE:
                if (cursorPosition > 0) {
                    text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                    cursorPosition--;
                    onTextChanged();
                }
                return true;
            case GLFW.GLFW_KEY_DELETE:
                if (cursorPosition < text.length()) {
                    text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                    onTextChanged();
                }
                return true;
            case GLFW.GLFW_KEY_LEFT:
                if (cursorPosition > 0) cursorPosition--;
                return true;
            case GLFW.GLFW_KEY_RIGHT:
                if (cursorPosition < text.length()) cursorPosition++;
                return true;
            case GLFW.GLFW_KEY_HOME:
                cursorPosition = 0;
                return true;
            case GLFW.GLFW_KEY_END:
                cursorPosition = text.length();
                return true;
            case GLFW.GLFW_KEY_ESCAPE:
                this.setFocused(false);
                return true;
        }
        
        // Ctrl+A - Select all (clear)
        if (keyCode == GLFW.GLFW_KEY_A && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            this.text = "";
            this.cursorPosition = 0;
            onTextChanged();
            return true;
        }
        
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.focused) return false;
        
        if (SharedConstants.isAllowedChatCharacter(chr)) {
            text = text.substring(0, cursorPosition) + chr + text.substring(cursorPosition);
            cursorPosition++;
            onTextChanged();
            return true;
        }
        return false;
    }

    private void onTextChanged() {
        if (this.responder != null) {
            this.responder.accept(this.text);
        }
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        this.cursorPosition = text.length();
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            this.lastCursorBlink = System.currentTimeMillis();
            this.cursorVisible = true;
        }
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return this.focused ? NarrationPriority.FOCUSED : (this.hovered ? NarrationPriority.HOVERED : NarrationPriority.NONE);
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
    }
}
