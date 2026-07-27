package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.MouseButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public abstract class OverlayConfigGuiButton extends ConfigGuiButton {

    protected boolean isOverlayOpen;

    protected OverlayConfigGuiButton(Field field, Method event, ConfigProperty annotation) {
        super(field, event, annotation);
    }

    public abstract boolean isMouseOverOverlay(int mouseX, int mouseY);

    public abstract void drawOverlay(ColorPalette colorPalette, int mouseX, int mouseY);

    public List<String> getOverlayHoveringTextLines() { return null; }

    /** @return {@code true} if the overlay handles the mouse click */
    public abstract boolean mouseClickedOnOverlay(int mouseX, int mouseY, MouseButton button) throws IllegalAccessException;

    /** @return {@code true} if the overlay handles the mouse release */
    public boolean mouseReleasedOnOverlay(int mouseX, int mouseY, MouseButton button) { return false; }

    /** @return {@code true} if the overlay handles the mouse input */
    public boolean handleOverlayMouseInput() { return false; }

    /** @return {@code true} if the overlay handles the key typed */
    public boolean overlayKeyTyped(char typedChar, int keyCode) { return false; }

    public boolean isOverlayOpen() {
        return isOverlayOpen;
    }

    public void closeOverlay() {
        this.isOverlayOpen = false;
    }
}
