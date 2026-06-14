package fr.alexdoru.configlib;

import net.minecraft.client.gui.GuiScreen;
import org.jetbrains.annotations.NotNull;

public interface IConfigHandler {

    /**
     * Registers a config class to your config handler.
     * Fields and methods of the class should be static and annotated
     * with annotations from the {@link fr.alexdoru.configlib} package
     */
    void registerConfig(Class<?> clazz);

    /**
     * Saves config values to the config file
     */
    void saveConfig();

    /**
     * Get the config gui screen
     */
    GuiScreen getConfigGuiScreen();

    /**
     * Adds a custom title renderer for your config gui screen
     */
    void setConfigTitleRenderer(@NotNull IConfigTitleRenderer titleRenderer);

    /**
     * If your config has renderers, you need to set a renderer manager
     */
    void setRendererManager(@NotNull IRendererManager rendererManager);

}
