package fr.alexdoru.configlib.lib;

import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.elements.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConfigFieldContainer {

    private static final boolean FORCE_SHOW_HIDDEN = Boolean.getBoolean("config.lib.showHidden");

    private final Map<String, Property> propertyMap;
    private final Field field;
    private final FieldType fieldType;
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
        this.fieldType = getFieldType(field);
        this.annotation = field.getAnnotation(ConfigProperty.class);
        this.event = configEvents.get(annotation.name());
        this.hideOverride = configHideOverrides.get(annotation.name());
        this.createPropertyFromField(config);
        this.loadConfigValueToField();
    }

    private static FieldType getFieldType(Field field) {
        if (field.getType() == RendererPosition.class) {
            return FieldType.RENDERER;
        } else if (field.getType() == String.class) {
            return FieldType.STRING;
        } else if (field.getType() == boolean.class) {
            return FieldType.BOOLEAN;
        } else if (field.getType() == double.class) {
            return FieldType.DOUBLE;
        } else if (field.getType() == int.class) {
            return FieldType.INT;
        } else if (field.getType() == EnumChatFormatting.class) {
            return FieldType.ENUM_COLOR;
        } else if (List.class.isAssignableFrom(field.getType())) {
            final Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                final ParameterizedType parameterized = (ParameterizedType) genericType;
                final Type[] typeArgs = parameterized.getActualTypeArguments();
                if (typeArgs.length == 1 && typeArgs[0] == String.class) {
                    return FieldType.STRING_LIST;
                }
            }
        }
        throw new IllegalArgumentException("Type of field not handled by config lib " + field.getType());
    }

    private void createPropertyFromField(Configuration config) throws IllegalAccessException {
        if (propertyMap.containsKey(this.fieldType == FieldType.RENDERER ? this.getKey("Show ") : this.getKey())) {
            throw new IllegalStateException("Config properties with duplicate key names : " + annotation.category() + " " + annotation.name());
        }
        switch (this.fieldType) {
            case RENDERER: {
                final String showKey = this.getKey("Show ");
                final String xKey = this.getKey("Xpos ");
                final String yKey = this.getKey("Ypos ");
                final RendererPosition rendererPosition = (RendererPosition) field.get(null);
                Objects.requireNonNull(rendererPosition);
                propertyMap.put(showKey, config.get(annotation.category(), showKey, rendererPosition.isEnabled()));
                propertyMap.put(xKey, config.get(annotation.category(), xKey, rendererPosition.getRelativeX()));
                propertyMap.put(yKey, config.get(annotation.category(), yKey, rendererPosition.getRelativeY()));
                break;
            }
            case STRING: {
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), Objects.requireNonNull((String) field.get(null))));
                break;
            }
            case BOOLEAN: {
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), (boolean) field.get(null)));
                break;
            }
            case DOUBLE: {
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), (double) field.get(null)));
                break;
            }
            case INT: {
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), (int) field.get(null)));
                break;
            }
            case ENUM_COLOR: {
                final EnumChatFormatting color = (EnumChatFormatting) field.get(null);
                Objects.requireNonNull(color);
                if (!color.isColor()) {
                    throw new IllegalArgumentException("EnumChatFormatting fields must be colors!");
                }
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), color.name()));
                break;
            }
            case STRING_LIST: {
                //noinspection unchecked
                final String[] defaultStrings = ((List<String>) field.get(null)).toArray(new String[0]);
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), defaultStrings));
                break;
            }
            default: {
                throw new IllegalArgumentException("Type of field not handled by config lib " + field.getType());
            }
        }
    }

    private void loadConfigValueToField() throws IllegalAccessException {
        switch (this.fieldType) {
            case RENDERER: {
                final RendererPosition position = ((RendererPosition) field.get(null));
                position.setEnabled(this.getProp("Show ").getBoolean());
                position.setRelativePosition(
                        this.getProp("Xpos ").getDouble(),
                        this.getProp("Ypos ").getDouble()
                );
                break;
            }
            case STRING: {
                field.set(null, this.getProp().getString());
                break;
            }
            case BOOLEAN: {
                field.setBoolean(null, this.getProp().getBoolean());
                break;
            }
            case DOUBLE: {
                field.setDouble(null, this.getProp().getDouble());
                break;
            }
            case INT: {
                field.setInt(null, this.getProp().getInt());
                break;
            }
            case ENUM_COLOR: {
                EnumChatFormatting color = getColorFromString(this.getProp().toString());
                if (color == null) {
                    color = getColorFromString(this.getProp().getDefault());
                }
                field.set(null, color);
                break;
            }
            case STRING_LIST: {
                final String[] strings = this.getProp().getStringList();
                //noinspection unchecked
                final List<String> list = (List<String>) field.get(null);
                list.clear();
                list.addAll(Arrays.asList(strings));
                break;
            }
            default: {
                throw new IllegalArgumentException("Type of field not handled by config lib " + field.getType());
            }
        }
    }

    void saveFieldValueToConfig() throws IllegalAccessException {
        switch (this.fieldType) {
            case RENDERER: {
                final RendererPosition rendererPosition = (RendererPosition) field.get(null);
                this.getProp("Show ").set(rendererPosition.isEnabled());
                this.getProp("Xpos ").set(rendererPosition.getRelativeX());
                this.getProp("Ypos ").set(rendererPosition.getRelativeY());
                break;
            }
            case STRING: {
                this.getProp().set((String) field.get(null));
                break;
            }
            case BOOLEAN: {
                this.getProp().set((boolean) field.get(null));
                break;
            }
            case DOUBLE: {
                this.getProp().set((double) field.get(null));
                break;
            }
            case INT: {
                this.getProp().set((int) field.get(null));
                break;
            }
            case ENUM_COLOR: {
                this.getProp().set(((EnumChatFormatting) field.get(null)).name());
                break;
            }
            case STRING_LIST: {
                //noinspection unchecked
                this.getProp().set(((List<String>) field.get(null)).toArray(new String[0]));
                break;
            }
            default: {
                throw new IllegalArgumentException("Type of field not handled by config lib " + field.getType());
            }
        }
    }

    private String getKey(String key) {
        return key + annotation.name();
    }

    private Property getProp(String key) {
        return propertyMap.get(this.getKey(key));
    }

    private String getKey() {
        return annotation.name();
    }

    private Property getProp() {
        return propertyMap.get(this.getKey());
    }

    public ConfigUIElement getConfigButton(ConfigGuiScreen configGuiScreen, RendererManager rendererManager) throws IllegalAccessException {
        if (annotation.hidden()) return null;
        if (!FORCE_SHOW_HIDDEN && hideOverride != null) {
            try {
                final boolean shouldHide = (boolean) hideOverride.invoke(null);
                if (shouldHide) return null;
            } catch (InvocationTargetException ignored) {}
        }
        switch (this.fieldType) {
            case RENDERER: {
                return new RendererGuiButton(configGuiScreen, rendererManager, field, event, annotation);
            }
            case BOOLEAN: {
                return new BooleanGuiButton(field, event, annotation);
            }
            case DOUBLE: {
                if (annotation.sliderMin() == annotation.sliderMax()) {
                    throw new IllegalArgumentException("Config slider cannot have same min and max values. Name : " + annotation.name());
                }
                return new SliderGuiButton(field, event, annotation);
            }
            case INT: {
                if (annotation.isColor()) {
                    final int defaultColor = Integer.parseInt(this.getProp().getDefault());
                    return new ColorGuiButton(configGuiScreen, field, annotation, defaultColor);
                } else {
                    if (annotation.sliderMin() == annotation.sliderMax()) {
                        throw new IllegalArgumentException("Config slider cannot have same min and max values. Name : " + annotation.name());
                    }
                    return new SliderGuiButton(field, event, annotation);
                }
            }
            case ENUM_COLOR: {
                return new ColorEnumGuiButton(field, event, annotation);
            }
        }
        throw new IllegalArgumentException("Type of field not handled by config lib gui screen " + field.getType() + " you can mark the field as hidden to prevent crashing");
    }

    public ConfigProperty getAnnotation() {
        return annotation;
    }

    @Nullable
    private static EnumChatFormatting getColorFromString(String s) {
        try {
            final EnumChatFormatting chatColor = EnumChatFormatting.valueOf(s);
            if (chatColor.isColor()) return chatColor;
        } catch (IllegalArgumentException ignored) {}
        return null;
    }

    enum FieldType {
        RENDERER,
        STRING,
        BOOLEAN,
        DOUBLE,
        INT,
        ENUM_COLOR,
        STRING_LIST
    }

}
