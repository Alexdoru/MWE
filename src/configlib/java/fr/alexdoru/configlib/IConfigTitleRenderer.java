package fr.alexdoru.configlib;

import fr.alexdoru.configlib.gui.ColorPalette;
import net.minecraft.client.gui.FontRenderer;

public interface IConfigTitleRenderer {

    /**
     * Render the config title at position X and Y
     */
    void renderTitle(FontRenderer fontRenderer, ColorPalette colorPalette, int x, int y);

}
