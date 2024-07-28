package fr.alexdoru.mwe.config.lib;

import fr.alexdoru.mwe.gui.guiapi.GuiPosition;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConfigField {

    private final Map<String, Property> propertyMap;
    private final Field field;
    private final ConfigProperty annotation;

    public ConfigField(Field field, Map<String, Property> map, Configuration config) throws IllegalAccessException {
        this.propertyMap = map;
        this.field = field;
        this.annotation = field.getAnnotation(ConfigProperty.class);
        if (this.propertyMap.containsKey(annotation.name())) {
            throw new IllegalStateException("Duplicate key names in config properties : " + annotation.name());
        }
        this.createPropertyFromField(config);
        this.loadConfigValueToField();
    }

    private void createPropertyFromField(Configuration config) throws IllegalAccessException {
        if (field.getType() == GuiPosition.class) {
            final String xKey = "Xpos " + annotation.name();
            final String yKey = "Ypos " + annotation.name();
            final GuiPosition guiPosition = (GuiPosition) field.get(null);
            propertyMap.put(xKey, config.get(annotation.category(), xKey, guiPosition.getRelativeX(), "The x " + annotation.comment() + ", value ranges from 0 to 1"));
            propertyMap.put(yKey, config.get(annotation.category(), yKey, guiPosition.getRelativeY(), "The y " + annotation.comment() + ", value ranges from 0 to 1"));
        } else if (field.getType() == String.class) {
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), (String) field.get(null), annotation.comment()));
        } else if (field.getType() == boolean.class) {
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), (boolean) field.get(null), annotation.comment()));
        } else if (field.getType() == double.class) {
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), (double) field.get(null), annotation.comment()));
        } else if (field.getType() == int.class) {
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), (int) field.get(null), annotation.comment()));
        } else if (field.getGenericType().toString().equals("java.util.List<java.lang.String>")) {
            //noinspection unchecked
            final String[] defaultStrings = ((List<String>) field.get(null)).toArray(new String[0]);
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), defaultStrings, annotation.comment()));
        } else {
            throw new IllegalArgumentException("Type of field not handled by config");
        }
    }

    private void loadConfigValueToField() throws IllegalAccessException {
        if (field.getType() == GuiPosition.class) {
            final GuiPosition position = ((GuiPosition) field.get(null));
            position.setRelativePosition(
                    propertyMap.get("Xpos " + annotation.name()).getDouble(),
                    propertyMap.get("Ypos " + annotation.name()).getDouble());
        } else if (field.getType() == String.class) {
            field.set(null, propertyMap.get(annotation.name()).getString());
        } else if (field.getType() == boolean.class) {
            field.setBoolean(null, propertyMap.get(annotation.name()).getBoolean());
        } else if (field.getType() == double.class) {
            field.setDouble(null, propertyMap.get(annotation.name()).getDouble());
        } else if (field.getType() == int.class) {
            field.setInt(null, propertyMap.get(annotation.name()).getInt());
        } else if (field.getGenericType().toString().equals("java.util.List<java.lang.String>")) {
            final String[] strings = propertyMap.get(annotation.name()).getStringList();
            //noinspection unchecked
            final List<String> list = (List<String>) field.get(null);
            list.clear();
            list.addAll(Arrays.asList(strings));
        } else {
            throw new IllegalArgumentException("Type of field not handled by config");
        }
    }

    public void saveFieldValueToConfig() throws IllegalAccessException {
        if (field.getType() == GuiPosition.class) {
            final GuiPosition guiPosition = (GuiPosition) field.get(null);
            propertyMap.get("Xpos " + annotation.name()).set(guiPosition.getRelativeX());
            propertyMap.get("Ypos " + annotation.name()).set(guiPosition.getRelativeY());
        } else if (field.getType() == String.class) {
            propertyMap.get(annotation.name()).set((String) field.get(null));
        } else if (field.getType() == boolean.class) {
            propertyMap.get(annotation.name()).set((boolean) field.get(null));
        } else if (field.getType() == double.class) {
            propertyMap.get(annotation.name()).set((double) field.get(null));
        } else if (field.getType() == int.class) {
            propertyMap.get(annotation.name()).set((int) field.get(null));
        } else if (field.getGenericType().toString().equals("java.util.List<java.lang.String>")) {
            //noinspection unchecked
            propertyMap.get(annotation.name()).set(((List<String>) field.get(null)).toArray(new String[0]));
        } else {
            throw new IllegalArgumentException("Type of field not handled by config");
        }
    }

}
