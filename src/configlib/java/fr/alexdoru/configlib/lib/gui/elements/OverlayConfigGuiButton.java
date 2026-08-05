package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.lib.ConfigFieldContainer;

public abstract class OverlayConfigGuiButton extends ConfigGuiButton {

    protected boolean isOverlayOpen;

    protected OverlayConfigGuiButton(ConfigFieldContainer container) {
        super(container);
    }

    public void closeOverlay() {
        this.isOverlayOpen = false;
    }

    public final boolean isOverlayOpen() {
        return isOverlayOpen;
    }

}
