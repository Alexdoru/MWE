package fr.alexdoru.mwe.api.config;

import net.minecraft.client.gui.FontRenderer;

public interface IConfigTitleRenderer {

    /**
     * Render the config title at position X and Y
     */
    void renderTitle(FontRenderer fontRenderer, int x, int y);

}
