package fr.alexdoru.configlib;

import net.minecraft.client.gui.ScaledResolution;

public interface IRenderer {

    /**
     * Renders the HUD.
     *
     * @param resolution - Screen's resolution
     */
    void render(ScaledResolution resolution);

    /**
     * Renders the dummy HUD, which is
     * used in the configuration screen
     * where you can move it around.
     */
    void renderDummy();

    /**
     * Can be used to disable the renderer
     */
    boolean isEnabled(long currentTimeMillis);

    /**
     * Returns the RendererPosition of the Renderer
     */
    RendererPosition getPosition();

}
