package fr.alexdoru.mwe.config;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.gui.guiapi.GuiPosition;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractConfig {

    private static Configuration config;
    private static final HashMap<String, Property> propertyMap = new HashMap<>();

    public static void preInit(File file) {
        config = new Configuration(file);
        try {
            syncConfig(true, true, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ConfigHandler.onModUpdate();
    }

    /**
     * Saves the values of the config fields to the config file
     */
    public static void saveConfig() {
        try {
            syncConfig(false, false, true);
        } catch (IllegalAccessException e) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.DARK_RED + "Failed to save the config!");
            e.printStackTrace();
        }
    }

    /**
     * Reads the config file and loads the values into the fields of this class
     */
    public static void loadConfigFromFile() {
        try {
            syncConfig(true, true, false);
        } catch (IllegalAccessException e) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.DARK_RED + "Failed to load the config!");
            e.printStackTrace();
        }
    }

    private static void syncConfig(boolean loadFromConfigFile, boolean readFieldsFromConfig, boolean saveFieldsToConfig) throws IllegalAccessException {

        if (config == null) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.DARK_RED + "Config didn't load when the game started, this shouldn't happen !");
            return;
        }

        if (loadFromConfigFile) {
            config.load();
            propertyMap.clear();
            final HashMap<String, List<String>> categoryOrderMap = new HashMap<>();
            for (final Field field : ConfigHandler.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    final ConfigProperty configProperty = field.getAnnotation(ConfigProperty.class);
                    generatePropertyFromField(categoryOrderMap, field, configProperty);
                }
            }
            for (final Map.Entry<String, List<String>> entry : categoryOrderMap.entrySet()) {
                Collections.sort(entry.getValue());
                config.setCategoryPropertyOrder(entry.getKey(), entry.getValue());
            }
        }

        for (final Field field : ConfigHandler.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                final ConfigProperty configProperty = field.getAnnotation(ConfigProperty.class);
                if (readFieldsFromConfig) {
                    assignFieldToConfigValue(field, configProperty);
                }
                if (saveFieldsToConfig) {
                    saveFieldValueToConfig(field, configProperty);
                }
            }
        }

        if (config.hasChanged()) {
            config.save();
        }

    }

    private static void assignFieldToConfigValue(Field field, ConfigProperty configProperty) throws IllegalAccessException {
        if (field.getType() == GuiPosition.class) {
            final GuiPosition position = ((GuiPosition) field.get(null));
            position.setRelativePosition(
                    propertyMap.get("Xpos " + configProperty.name()).getDouble(),
                    propertyMap.get("Ypos " + configProperty.name()).getDouble()
            );
        } else if (field.getType() == String.class) {
            field.set(null, propertyMap.get(configProperty.name()).getString());
        } else if (field.getType() == boolean.class) {
            field.setBoolean(null, propertyMap.get(configProperty.name()).getBoolean());
        } else if (field.getType() == double.class) {
            field.setDouble(null, propertyMap.get(configProperty.name()).getDouble());
        } else if (field.getType() == int.class) {
            field.setInt(null, propertyMap.get(configProperty.name()).getInt());
        } else {
            throw new IllegalArgumentException("Type of field not handled by config");
        }
    }

    private static void saveFieldValueToConfig(Field field, ConfigProperty configProperty) throws IllegalAccessException {
        if (field.getType() == GuiPosition.class) {
            final GuiPosition guiPosition = (GuiPosition) field.get(null);
            propertyMap.get("Xpos " + configProperty.name()).set(guiPosition.getRelativeX());
            propertyMap.get("Ypos " + configProperty.name()).set(guiPosition.getRelativeY());
        } else if (field.getType() == String.class) {
            propertyMap.get(configProperty.name()).set((String) field.get(null));
        } else if (field.getType() == boolean.class) {
            propertyMap.get(configProperty.name()).set((boolean) field.get(null));
        } else if (field.getType() == double.class) {
            propertyMap.get(configProperty.name()).set((double) field.get(null));
        } else if (field.getType() == int.class) {
            propertyMap.get(configProperty.name()).set((int) field.get(null));
        } else {
            throw new IllegalArgumentException("Type of field not handled by config");
        }
    }

    private static void generatePropertyFromField(HashMap<String, List<String>> categoryOrderMap, Field field, ConfigProperty configProperty) throws IllegalAccessException {
        List<String> propsInCategory = categoryOrderMap.get(configProperty.category());
        if (propsInCategory == null) {
            propsInCategory = new ArrayList<>();
        }
        if (field.getType() == GuiPosition.class) {
            final String xKey = "Xpos " + configProperty.name();
            final String yKey = "Ypos " + configProperty.name();
            if (propertyMap.containsKey(xKey) || propertyMap.containsKey(yKey)) {
                throw new IllegalStateException("Duplicate key names in config properties");
            }
            propsInCategory.add(xKey);
            propsInCategory.add(yKey);
            final GuiPosition guiPosition = (GuiPosition) field.get(null);
            propertyMap.put(xKey, config.get(configProperty.category(), xKey, guiPosition.getRelativeX(), "The x " + configProperty.comment() + ", value ranges from 0 to 1"));
            propertyMap.put(yKey, config.get(configProperty.category(), yKey, guiPosition.getRelativeY(), "The y " + configProperty.comment() + ", value ranges from 0 to 1"));
            return;
        }
        propsInCategory.add(configProperty.name());
        if (propertyMap.containsKey(configProperty.name())) {
            throw new IllegalStateException("Duplicate key names in config properties");
        }
        if (field.getType() == String.class) {
            propertyMap.put(configProperty.name(), config.get(configProperty.category(), configProperty.name(), (String) field.get(null), configProperty.comment()));
        } else if (field.getType() == boolean.class) {
            propertyMap.put(configProperty.name(), config.get(configProperty.category(), configProperty.name(), (boolean) field.get(null), configProperty.comment()));
        } else if (field.getType() == double.class) {
            propertyMap.put(configProperty.name(), config.get(configProperty.category(), configProperty.name(), (double) field.get(null), configProperty.comment()));
        } else if (field.getType() == int.class) {
            propertyMap.put(configProperty.name(), config.get(configProperty.category(), configProperty.name(), (int) field.get(null), configProperty.comment()));
        } else {
            throw new IllegalArgumentException("Type of field not handled by config");
        }
    }

}
