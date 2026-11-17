package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.gui.Box;
import fr.alexdoru.configlib.lib.gui.GuiUtil;

public abstract class OverlayConfigGuiButton extends ConfigGuiButton {

    protected boolean isOverlayOpen;

    protected OverlayConfigGuiButton(ConfigFieldContainer container) {
        super(container);
    }

    public void drawOverlay(ColorPalette colorPalette, int mouseX, int mouseY) {}

    public void mouseScrolled(int direction) {}

    public boolean keyTyped(char typedChar, int keyCode) {
        return false;
    }

    public void updateScreen() {}

    public void closeOverlay() {
        this.isOverlayOpen = false;
    }

    protected final void drawOverlayTitle(ColorPalette colorPalette, Box panelBox, int titleY) {
        GuiUtil.drawCenteredString(annotation.name(), panelBox.LEFT + panelBox.getWidth() / 2, titleY, colorPalette.SETTING_NAME_TEXT);
    }

    protected final void drawOverlayTitle(String title, Box panelBox, int titleY) {
        GuiUtil.drawCenteredString(title, panelBox.LEFT + panelBox.getWidth() / 2, titleY, 0xFFFFFFFF);
    }

    public final boolean isOverlayOpen() {
        return isOverlayOpen;
    }

}
