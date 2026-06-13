package fr.alexdoru.configlib;

import net.minecraft.client.gui.GuiScreen;

public interface IConfigHandler {

    /**
     * Registers a config class to your config handler.
     * Fields and methods of the class should be static and annotated
     * with annotations from the import fr.alexdoru.configlib package
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

}
