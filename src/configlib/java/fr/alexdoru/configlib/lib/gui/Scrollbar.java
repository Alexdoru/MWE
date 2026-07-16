package fr.alexdoru.configlib.lib.gui;

import fr.alexdoru.configlib.api.ColorPalette;
import net.minecraft.client.gui.Gui;

public class Scrollbar {

    private int scroll;
    private int scrollDirection;
    private int amountToScroll;
    private long lastScrollTime;

    private boolean dragging;
    private int grabbedAtY;

    private int thumbLeft, thumbTop, thumbRight, thumbBottom;

    public void init(int lastScroll, int contentHeight, int boxHeight) {
        this.scroll = 0;
        this.scroll(-lastScroll, contentHeight, boxHeight);
    }

    public void updateScrollPos(int contentHeight, int boxHeight) {
        if (amountToScroll > 0) {
            final long time = System.currentTimeMillis();
            if (time - lastScrollTime > 1) {
                final int step = Math.min(amountToScroll, Math.max(3, amountToScroll / 8));
                this.scroll(scrollDirection * step, contentHeight, boxHeight);
                amountToScroll -= step;
                lastScrollTime = time;
            }
        }
    }

    public void drawScrollbar(ColorPalette colorPalette, int mouseY, int boxTop, int boxRight, int boxBottom, int contentHeight) {
        final int categoryBoxHeight = boxBottom - boxTop - 2;
        final boolean renderCategoryScrollbar = contentHeight > categoryBoxHeight;
        if (renderCategoryScrollbar) {
            final int scrollBarSize = categoryBoxHeight * categoryBoxHeight / contentHeight;
            final int minScrollBarY = boxTop + 1 + 1;
            final int maxScrollBarY = boxBottom - 1 - scrollBarSize - 1;
            if (dragging) {
                final int relativeMouseY = mouseY - minScrollBarY - grabbedAtY;
                if (maxScrollBarY != minScrollBarY) {
                    final int newScroll = relativeMouseY * (contentHeight - (boxBottom - boxTop)) / (maxScrollBarY - minScrollBarY);
                    this.scroll(this.scroll - newScroll, contentHeight, boxBottom - boxTop);
                }
            }
            this.thumbLeft = boxRight - 5;
            this.thumbTop = ((maxScrollBarY - minScrollBarY) * this.scroll) / (contentHeight - categoryBoxHeight) + minScrollBarY;
            this.thumbRight = this.thumbLeft + 3;
            this.thumbBottom = this.thumbTop + scrollBarSize;
            GuiUtil.drawVerticalLine(boxRight - 4, boxTop + 4, boxBottom - 4, colorPalette.SCROLLBAR_TRACK);
            Gui.drawRect(this.thumbLeft, this.thumbTop, this.thumbRight, this.thumbBottom, colorPalette.SCROLLBAR_THUMB);
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isMouseInBox(mouseX, mouseY, this.thumbLeft, this.thumbRight, this.thumbTop, this.thumbBottom)) {
            dragging = true;
            grabbedAtY = mouseY - this.thumbTop;
            return true;
        }
        return false;
    }

    private boolean isMouseInBox(int mouseX, int mouseY, int boxLeft, int boxRight, int boxTop, int boxBottom) {
        return mouseX >= boxLeft && mouseX < boxRight && mouseY >= boxTop && mouseY < boxBottom;
    }

    public void mouseReleased(int mouseButton) {
        if (mouseButton == 0 && dragging) {
            dragging = false;
        }
    }

    private void scroll(int amount, int contentHeight, int boxHeight) {
        this.scroll = this.scroll - amount;
        if (this.scroll > contentHeight + 2 * 6 - boxHeight) {
            this.scroll = contentHeight + 2 * 6 - boxHeight;
            this.amountToScroll = 0;
        }
        if (this.scroll <= 0) {
            this.scroll = 0;
            this.amountToScroll = 0;
        }
    }

    public void scheduleScroll(int direction, int amount) {
        this.scrollDirection = direction;
        this.amountToScroll = amount;
    }

    public void resetScroll() {
        this.scroll = 0;
        this.amountToScroll = 0;
    }

    public int getScroll() {
        return scroll;
    }

}
