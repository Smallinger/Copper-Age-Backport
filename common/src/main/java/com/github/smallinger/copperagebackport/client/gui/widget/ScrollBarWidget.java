package com.github.smallinger.copperagebackport.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

/**
 * A scrollbar widget for scrolling content.
 */
public class ScrollBarWidget extends AbstractWidget {

    private static final int SCROLL_STEP = 18;

    private final Dim2i dim;
    private final int contentHeight;
    private final int visibleHeight;
    private final int maxOffset;
    private final Consumer<Integer> onScroll;
    private final Dim2i scrollArea;

    private int offset = 0;
    private boolean dragging = false;
    private int dragOffset = 0;

    private Dim2i thumbDim;

    public ScrollBarWidget(Dim2i dim, int contentHeight, int visibleHeight, Consumer<Integer> onScroll, Dim2i scrollArea) {
        this.dim = dim;
        this.contentHeight = contentHeight;
        this.visibleHeight = visibleHeight;
        this.maxOffset = Math.max(0, contentHeight - visibleHeight);
        this.onScroll = onScroll;
        this.scrollArea = scrollArea;
        this.updateThumb();
    }

    private void updateThumb() {
        if (this.maxOffset <= 0) {
            this.thumbDim = new Dim2i(dim.x() + 2, dim.y() + 2, dim.width() - 4, dim.height() - 4);
            return;
        }

        int trackHeight = dim.height() - 4;
        int thumbHeight = Math.max(20, (visibleHeight * trackHeight) / contentHeight);
        int maxThumbOffset = trackHeight - thumbHeight;
        int thumbOffset = (offset * maxThumbOffset) / maxOffset;

        this.thumbDim = new Dim2i(
            dim.x() + 2,
            dim.y() + 2 + thumbOffset,
            dim.width() - 4,
            thumbHeight
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.maxOffset <= 0) return;

        // Draw track
        this.drawRect(graphics, dim.x(), dim.y(), dim.getLimitX(), dim.getLimitY(), 0x40000000);
        this.drawBorder(graphics, dim.x(), dim.y(), dim.getLimitX(), dim.getLimitY(), 0xFF404040);

        // Draw thumb
        int thumbColor = (this.hovered || this.dragging) ? 0xFFCCCCCC : 0xFFAAAAAA;
        this.drawRect(graphics, thumbDim.x(), thumbDim.y(), thumbDim.getLimitX(), thumbDim.getLimitY(), thumbColor);

        // Focus border
        if (this.focused) {
            this.drawBorder(graphics, dim.x(), dim.y(), dim.getLimitX(), dim.getLimitY(), 0xFFFFFFFF);
        }

        this.hovered = dim.containsCursor(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || this.maxOffset <= 0) return false;

        if (dim.containsCursor(mouseX, mouseY)) {
            if (thumbDim.containsCursor(mouseX, mouseY)) {
                this.dragging = true;
                this.dragOffset = (int) (mouseY - thumbDim.getCenterY());
            } else {
                // Click on track - jump to position
                int trackHeight = dim.height() - 4;
                int thumbHeight = thumbDim.height();
                int clickPos = (int) (mouseY - dim.y() - 2 - thumbHeight / 2);
                int newOffset = (clickPos * maxOffset) / (trackHeight - thumbHeight);
                setOffset(newOffset);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.dragging = false;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.dragging && this.maxOffset > 0) {
            int trackHeight = dim.height() - 4;
            int thumbHeight = thumbDim.height();
            int dragPos = (int) (mouseY - this.dragOffset - dim.y() - 2 - thumbHeight / 2);
            int newOffset = (dragPos * maxOffset) / (trackHeight - thumbHeight);
            setOffset(newOffset);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        if (this.maxOffset <= 0) return false;

        // Check if mouse is over scrollbar or scroll area
        if (dim.containsCursor(mouseX, mouseY) || (scrollArea != null && scrollArea.containsCursor(mouseX, mouseY))) {
            setOffset(this.offset - (int) (scrollDelta * SCROLL_STEP));
            return true;
        }
        return false;
    }

    public void setOffset(int offset) {
        this.offset = Mth.clamp(offset, 0, this.maxOffset);
        this.updateThumb();
        if (this.onScroll != null) {
            this.onScroll.accept(this.offset);
        }
    }

    public int getOffset() {
        return this.offset;
    }

    public boolean isScrollable() {
        return this.maxOffset > 0;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return dim.containsCursor(mouseX, mouseY);
    }
}
