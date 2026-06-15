package fr.alexdoru.configlib;

import fr.alexdoru.configlib.gui.ColorPalette;
import net.minecraft.client.gui.GuiScreen;
import org.jetbrains.annotations.NotNull;

public interface IConfigHandler {

    /**
     * Registers a config class to your config handler.
     * Fields and methods of the class should be static and annotated
     * with annotations from the {@link fr.alexdoru.configlib} package
     */
    void registerConfig(@NotNull Class<?> clazz);

    /**
     * Saves config values to the config file
     */
    void saveConfig();

    /**
     * Get the config gui screen
     */
    @NotNull
    GuiScreen getConfigGuiScreen();

    /**
     * Automatically creates and registers a command that will
     * open your config gui screen
     */
    void registerConfigCommand(@NotNull String commandName);

    /**
     * Adds a custom title renderer for your config gui screen
     */
    void setConfigTitleRenderer(@NotNull IConfigTitleRenderer titleRenderer);

    /**
     * Sets the renderer manager, needed if your config has renderers
     */
    void setRendererManager(@NotNull IRendererManager rendererManager);

    /**
     * Sets the color palette to use for the config gui screen
     */
    void setColorPalette(@NotNull ColorPalette colorPalette);

}
