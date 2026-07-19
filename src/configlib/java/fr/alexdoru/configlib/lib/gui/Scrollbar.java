package fr.alexdoru.configlib.lib.gui;

import fr.alexdoru.configlib.api.ColorPalette;

public final class Scrollbar {

    public static final int SCROLL_STEP = 180;
    public static final int SCROLL_CONSUMED_PER_FRAME = 12;

    private int scroll;
    private int scrollDirection;
    private int amountToScroll;
    private long lastScrollTime;

    private boolean dragging;
    private int grabbedAtY;

    private final Box thumb = new Box();

    public void init(int lastScroll, int contentHeight, int boxHeight) {
        this.scroll = 0;
        this.scroll(-lastScroll, contentHeight, boxHeight);
    }

    public void updateScrollPos(int contentHeight, int boxHeight) {
        if (amountToScroll > 0) {
            final long time = System.currentTimeMillis();
            if (time - lastScrollTime > 1) {
                final int step = Math.min(amountToScroll, Math.max(3, amountToScroll / SCROLL_CONSUMED_PER_FRAME));
                this.scroll(scrollDirection * step, contentHeight, boxHeight);
                amountToScroll -= step;
                lastScrollTime = time;
            }
        }
    }

    public void drawScrollbar(ColorPalette colorPalette, Box box, int mouseY, int contentHeight) {
        final int categoryBoxHeight = box.getHeight() - 2;
        final boolean renderCategoryScrollbar = contentHeight > categoryBoxHeight;
        if (renderCategoryScrollbar) {
            final int scrollBarSize = categoryBoxHeight * categoryBoxHeight / contentHeight;
            final int minScrollBarY = box.TOP + 1 + 1;
            final int maxScrollBarY = box.BOTTOM - 1 - scrollBarSize - 1;
            if (dragging) {
                final int relativeMouseY = mouseY - minScrollBarY - grabbedAtY;
                if (maxScrollBarY != minScrollBarY) {
                    final int newScroll = relativeMouseY * (contentHeight - box.getHeight()) / (maxScrollBarY - minScrollBarY);
                    this.scroll(this.scroll - newScroll, contentHeight, box.getHeight());
                }
            }
            this.thumb.LEFT = box.RIGHT - 5;
            this.thumb.TOP = ((maxScrollBarY - minScrollBarY) * this.scroll) / (contentHeight - categoryBoxHeight) + minScrollBarY;
            this.thumb.RIGHT = this.thumb.LEFT + 3;
            this.thumb.BOTTOM = this.thumb.TOP + scrollBarSize;
            GuiUtil.drawVerticalLine(box.RIGHT - 4, box.TOP + 4, box.BOTTOM - 4, colorPalette.SCROLLBAR_TRACK);
            GuiUtil.drawRect(this.thumb, colorPalette.SCROLLBAR_THUMB);
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.thumb.isMouseInBox(mouseX, mouseY)) {
            dragging = true;
            grabbedAtY = mouseY - this.thumb.TOP;
            return true;
        }
        return false;
    }

    public void mouseReleased(int mouseButton) {
        if (mouseButton == 0 && dragging) {
            dragging = false;
        }
    }

    private void scroll(int amount, int contentHeight, int boxHeight) {
        this.scroll = this.scroll - amount;
        if (this.scroll > contentHeight + 2 * ConfigGuiScreen.PADDING - boxHeight) {
            this.scroll = contentHeight + 2 * ConfigGuiScreen.PADDING - boxHeight;
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
