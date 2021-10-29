package fr.alexdoru.fkcountermod.hudproperty;

public interface IRenderer extends IConfigExchange {

    /**
     * Returns the height of the currently rendered HUD.
     *
     * @return The height in pixel.
     */
    int getHeight();

    /**
     * Returns the width of the currently rendered HUD.
     *
     * @return The width in pixel.
     */
    int getWidth();

    /**
     * Render the HUD at the given position.
     */
    void render(ScreenPosition position);

    /**
     * Render the HUD at the given position,
     * used in the configuration screen
     * where you can move it around.
     */
    void renderDummy(ScreenPosition position);

    /**
     * Can be used to disable the renderer
     * more conveniently than unregistering
     * it from the API.
     */
    default boolean isEnabled() {
        return true;
    }

}
