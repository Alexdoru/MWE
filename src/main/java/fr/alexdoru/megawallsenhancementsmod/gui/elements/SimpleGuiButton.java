package fr.alexdoru.megawallsenhancementsmod.gui.elements;

import net.minecraft.client.gui.GuiButton;

public class SimpleGuiButton extends GuiButton {

    private final Runnable action;

    public SimpleGuiButton(int x, int y, String buttonText, Runnable action) {
        this(x, y, 200, 20, buttonText, action);
    }

    public SimpleGuiButton(int x, int y, int widthIn, int heightIn, String buttonText, Runnable action) {
        super(0, x, y, widthIn, heightIn, buttonText);
        this.action = action;
    }

    public void onButtonPressed() {
        this.action.run();
    }

}
