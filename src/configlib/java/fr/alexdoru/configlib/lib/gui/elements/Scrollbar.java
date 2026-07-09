package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import net.minecraft.client.gui.Gui;

public final class Scrollbar {

    private final ColorPalette colorPalette;
    private final int allElementsHeight;
    private final int trackHeight;
    private final int thumbHeight;
    private final int trackTop, trackBottom;

    private int LEFT, RIGHT, TOP, BOTTOM;

    private int scroll;
    private boolean isDragging;
    private int grabbedAtY;
    private int scrollDirection;
    private int amountToScroll;
    private long lastScrolledAt;

    private Scrollbar(ColorPalette colorPalette, int allElementsHeight, int trackTop, int trackBottom) {
        this.colorPalette = colorPalette;
        this.allElementsHeight = allElementsHeight;
        this.trackTop = trackTop;
        this.trackBottom = trackBottom;
        this.trackHeight = trackBottom - trackTop;
        this.thumbHeight = (int) (this.trackHeight * ((float) this.trackHeight / allElementsHeight));
    }

    public static Scrollbar create(ColorPalette colorPalette, int allElementsHeight, int top, int bottom) {
        return create(colorPalette, allElementsHeight, top, bottom, null);
    }

    public static Scrollbar create(ColorPalette colorPalette, int allElementsHeight, int top, int bottom, Scrollbar previous) {
        if (allElementsHeight <= (bottom - top)) return null;
        final Scrollbar created = new Scrollbar(colorPalette, allElementsHeight, top, bottom);
        if (previous != null) {
            created.isDragging = previous.isDragging;
            created.grabbedAtY = previous.grabbedAtY;
            created.lastScrolledAt = previous.lastScrolledAt;
            created.updateScroll(previous.scroll);
        }
        return created;
    }

    public void draw(int left, int right, int mouseY, boolean drawTrack) {
        final int maxY = this.trackBottom - this.thumbHeight;
        if (this.isDragging) {
            final int relativeMouseY = mouseY - this.trackTop - this.grabbedAtY;
            final int newScroll = relativeMouseY * (this.allElementsHeight - this.trackHeight) / (maxY - this.trackTop);
            updateScroll(newScroll);
        }
        this.LEFT = left;
        this.RIGHT = right;
        this.TOP = this.trackTop + (int) ((float)this.scroll / (this.allElementsHeight - this.trackHeight) * (maxY - this.trackTop));
        this.BOTTOM = this.TOP + this.thumbHeight;
        if (drawTrack) {
            final int x = LEFT + (RIGHT - LEFT) / 2;
            GuiUtil.drawVerticalLine(x, trackTop + 4, trackBottom - 4, colorPalette.SCROLLBAR_TRACK);
        }
        Gui.drawRect(LEFT, TOP, RIGHT, BOTTOM, colorPalette.SCROLLBAR_THUMB);
    }

    public void draw(int left, int right, int mouseY) {
        draw(left, right, mouseY, true);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && (mouseX >= LEFT && mouseX < RIGHT && mouseY >= TOP && mouseY < BOTTOM)) {
            this.isDragging = true;
            this.grabbedAtY = mouseY - TOP;
            return true;
        }
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isDragging)
            this.isDragging = false;
    }

    public void updateFromMouseScroll(int eventDWheel) {
        if (eventDWheel != 0) {
            this.scrollDirection = eventDWheel > 0 ? -1 : 1;
            this.amountToScroll = Math.min(Math.abs(eventDWheel) * 2, 240);
        }
    }

    public void smoothScroll() {
        if (this.amountToScroll > 0) {
            final long time = System.currentTimeMillis();
            if (time - this.lastScrolledAt > 1) {
                final int step = Math.min(amountToScroll, Math.max(3, amountToScroll / 8));
                this.amountToScroll -= step;
                updateScroll(scroll + (scrollDirection * step));
                this.lastScrolledAt = time;
            }
        }
    }

    public int getScroll() { return this.scroll; }

    public int getTrackBottom() { return this.trackBottom; }

    private void updateScroll(int scroll) {
        this.scroll = scroll;
        if (this.scroll > (this.allElementsHeight - this.trackHeight)) {
            this.scroll = this.allElementsHeight - this.trackHeight;
            this.amountToScroll = 0;
        }
        if (this.scroll <= 0) {
            this.scroll = 0;
            this.amountToScroll = 0;
        }
    }
}
