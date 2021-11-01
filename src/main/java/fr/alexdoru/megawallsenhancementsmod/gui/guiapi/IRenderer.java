package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

public interface IRenderer {

    /**
     * Returns the height of the HUD.
     *
     * @return The height in pixel.
     */
    int getHeight();

    /**
     * Returns the width of the HUD.
     *
     * @return The width in pixel.
     */
    int getWidth();

    /**
     * Render the HUD.
     */
    void render();

    /**
     * Render the dummy HUD, which is
     * used in the configuration screen
     * where you can move it around.
     */
    void renderDummy();

    /**
     * Can be used to disable the renderer
     * more conveniently than unregistering
     * it from the API.
     */
    boolean isEnabled();

    /**
     * Is called for each HUD when the configuration screen is closed
     * in order to save the now Gui position to the settings.
     */
    void save();

    /**
     * Creates a new HUDPosition object based on relative coordinates.
     * From 0 to 1. Example: 0.3 being 30% of the screen size.
     *
     * @return The initial ScreenPosition position.
     * This is where the HUD will be rendered when opening a screen.
     * Preferably load the values from a configuration file.
     * Can be null.
     */
    GuiPosition getHUDPosition();

}
