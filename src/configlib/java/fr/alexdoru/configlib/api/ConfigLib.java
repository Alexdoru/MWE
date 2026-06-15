package fr.alexdoru.configlib.api;

import fr.alexdoru.configlib.lib.ConfigHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class ConfigLib {

    private ConfigLib() {}

    /**
     * Creates a new config handler
     *
     * @param configFile - the config file to load from
     * @param configName - the name of your config, this will show as the title in the config scren
     */
    public static IConfigHandler newConfigHandler(@NotNull File configFile, @NotNull String configName) {
        return new ConfigHandler(configFile, configName);
    }

    /**
     * Creates a new config handler
     *
     * @param configFile    - the config file to load from
     * @param configName    - the name of your config, this will show as the title in the config scren
     * @param configVersion - the current version of your mod
     */
    public static IConfigHandler newConfigHandler(@NotNull File configFile, @NotNull String configName, @NotNull String configVersion) {
        return new ConfigHandler(configFile, configName, configVersion);
    }

}
