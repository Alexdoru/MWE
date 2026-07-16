package fr.alexdoru.configlib.lib.gui;

public final class Box {

    public int LEFT, TOP, RIGHT, BOTTOM;

    public int getHeight() {
        return BOTTOM - TOP;
    }

    public int getWidth() {
        return RIGHT - LEFT;
    }

    public boolean isMouseInBox(int mouseX, int mouseY) {
        return mouseX >= LEFT && mouseX < RIGHT && mouseY >= TOP && mouseY < BOTTOM;
    }

}
