package fr.alexdoru.mwe.api.config;

import net.minecraft.client.gui.GuiScreen;

public interface IConfigHandler {

    /**
     * Registers a config class to your config handler.
     * Fields and methods of the class should be static and annotated
     * with annotations from the fr.alexdoru.mwe.api.config package
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
     * Display the config gui screen
     */
    void displayConfigGuiScreen();

}
