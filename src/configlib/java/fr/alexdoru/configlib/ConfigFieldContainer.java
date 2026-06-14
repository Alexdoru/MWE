package fr.alexdoru.configlib;

import fr.alexdoru.configlib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.gui.elements.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConfigFieldContainer {

    private static final boolean FORCE_SHOW_HIDDEN = Boolean.getBoolean("config.lib.showHidden");

    private final Map<String, Property> propertyMap;
    private final Field field;
    private final ConfigProperty annotation;
    private final Method event;
    private final Method hideOverride;

    ConfigFieldContainer(
            Configuration config,
            Map<String, Property> propertyMap,
            Field field,
            Map<String, Method> configEvents,
            Map<String, Method> configHideOverrides) throws IllegalAccessException {
        this.propertyMap = propertyMap;
        this.field = field;
        this.annotation = field.getAnnotation(ConfigProperty.class);
        if (this.propertyMap.containsKey(annotation.name())) {
            throw new IllegalStateException("Duplicate key names in config properties : " + annotation.name());
        }
        this.event = configEvents.get(annotation.name());
        this.hideOverride = configHideOverrides.get(annotation.name());
        this.createPropertyFromField(config);
        this.loadConfigValueToField();
    }

    private void createPropertyFromField(Configuration config) throws IllegalAccessException {
        if (field.getType() == RendererPosition.class) {
            final String showKey = "Show " + annotation.name();
            final String xKey = "Xpos " + annotation.name();
            final String yKey = "Ypos " + annotation.name();
            final RendererPosition rendererPosition = (RendererPosition) field.get(null);
            propertyMap.put(showKey, config.get(annotation.category(), showKey, rendererPosition.isEnabled()));
            propertyMap.put(xKey, config.get(annotation.category(), xKey, rendererPosition.getRelativeX()));
            propertyMap.put(yKey, config.get(annotation.category(), yKey, rendererPosition.getRelativeY()));
        } else if (field.getType() == String.class) {
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), (String) field.get(null)));
        } else if (field.getType() == boolean.class) {
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), (boolean) field.get(null)));
        } else if (field.getType() == double.class) {
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), (double) field.get(null)));
        } else if (field.getType() == int.class) {
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), (int) field.get(null)));
        } else if (field.getGenericType().toString().equals("java.util.List<java.lang.String>")) {
            //noinspection unchecked
            final String[] defaultStrings = ((List<String>) field.get(null)).toArray(new String[0]);
            propertyMap.put(annotation.name(), config.get(annotation.category(), annotation.name(), defaultStrings));
        } else {
            throw new IllegalArgumentException("Type of field not handled by config lib " + field.getType());
        }
    }

    private void loadConfigValueToField() throws IllegalAccessException {
        if (field.getType() == RendererPosition.class) {
            final RendererPosition position = ((RendererPosition) field.get(null));
            position.setEnabled(propertyMap.get("Show " + annotation.name()).getBoolean());
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
            throw new IllegalArgumentException("Type of field not handled by config lib " + field.getType());
        }
    }

    public void saveFieldValueToConfig() throws IllegalAccessException {
        if (field.getType() == RendererPosition.class) {
            final RendererPosition rendererPosition = (RendererPosition) field.get(null);
            propertyMap.get("Show " + annotation.name()).set(rendererPosition.isEnabled());
            propertyMap.get("Xpos " + annotation.name()).set(rendererPosition.getRelativeX());
            propertyMap.get("Ypos " + annotation.name()).set(rendererPosition.getRelativeY());
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
            throw new IllegalArgumentException("Type of field not handled by config lib " + field.getType());
        }
    }

    public ConfigUIElement getConfigButton(ConfigGuiScreen configGuiScreen, @Nullable IRendererManager rendererManager) throws IllegalAccessException {
        if (annotation.hidden()) return null;
        if (!FORCE_SHOW_HIDDEN && hideOverride != null) {
            try {
                final boolean shouldHide = (boolean) hideOverride.invoke(null);
                if (shouldHide) return null;
            } catch (InvocationTargetException ignored) {}
        }
        if (field.getType() == RendererPosition.class) {
            if (rendererManager == null) {
                throw new IllegalStateException("Config must have an IRendererManager to handle renderers");
            }
            return new RendererGuiButton(configGuiScreen, rendererManager, field, event, annotation);
        } else if (field.getType() == boolean.class) {
            return new BooleanGuiButton(field, event, annotation);
        } else if (field.getType() == double.class) {
            if (annotation.sliderMin() == annotation.sliderMax()) {
                throw new IllegalArgumentException("Config slider cannot have same min and max values. Name : " + annotation.name());
            }
            return new SliderGuiButton(field, event, annotation);
        } else if (field.getType() == int.class) {
            if (annotation.isColor()) {
                final int defaultColor = Integer.parseInt(propertyMap.get(annotation.name()).getDefault());
                return new ColorGuiButton(configGuiScreen, field, annotation, defaultColor);
            } else {
                if (annotation.sliderMin() == annotation.sliderMax()) {
                    throw new IllegalArgumentException("Config slider cannot have same min and max values. Name : " + annotation.name());
                }
                return new SliderGuiButton(field, event, annotation);
            }
        }
        throw new IllegalArgumentException("Type of field not handled by config lib " + field.getType());
    }

    public ConfigProperty getAnnotation() {
        return annotation;
    }

}
