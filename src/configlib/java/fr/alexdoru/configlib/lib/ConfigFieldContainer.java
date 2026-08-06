package fr.alexdoru.configlib.lib;

import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.elements.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.jetbrains.annotations.NotNull;
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
    private final ConfigProperty annotation;
    private final Method event;
    private final Method hideOverride;
    private ConfigFieldContainer dependency;
    private boolean hasDependent;
    private final FieldType fieldType;

    ConfigFieldContainer(Configuration config, Map<String, Property> propertyMap, Field field, ConfigFieldRegistry fieldRegistry) throws IllegalAccessException {
        this.propertyMap = propertyMap;
        this.field = field;
        this.annotation = field.getAnnotation(ConfigProperty.class);
        ConfigHandler.validateString(this.annotation.category(), "Config category name");
        ConfigHandler.validateString(this.annotation.name(), "Config name");
        this.event = fieldRegistry.getEvent(annotation);
        this.hideOverride = fieldRegistry.getHideOverride(annotation);
        this.fieldType = getFieldType(field);
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
        } else if (Enum.class.isAssignableFrom(field.getType())) {
            return FieldType.ENUM;
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
        throw new IllegalStateException("Type of field not handled by config lib " + field.getType());
    }

    private void createPropertyFromField(Configuration config) throws IllegalAccessException {
        switch (this.fieldType) {
            case RENDERER: {
                final RendererPosition rendererPosition = (RendererPosition) field.get(null);
                Objects.requireNonNull(rendererPosition);
                propertyMap.put(this.getKey("Show "), config.get(annotation.category(), "Show " + annotation.name(), rendererPosition.isEnabled()));
                propertyMap.put(this.getKey("Xpos "), config.get(annotation.category(), "Xpos " + annotation.name(), rendererPosition.getRelativeX()));
                propertyMap.put(this.getKey("Ypos "), config.get(annotation.category(), "Ypos " + annotation.name(), rendererPosition.getRelativeY()));
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
                    throw new IllegalStateException("EnumChatFormatting fields must be colors!");
                }
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), color.name()));
                break;
            }
            case ENUM: {
                final Enum<?> value = (Enum<?>) field.get(null);
                Objects.requireNonNull(value);
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), value.name()));
                break;
            }
            case STRING_LIST: {
                //noinspection unchecked
                final String[] defaultStrings = ((List<String>) field.get(null)).toArray(new String[0]);
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), defaultStrings));
                break;
            }
            default: {
                throw new IllegalStateException("Type of field not handled by config lib " + field.getType());
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
                EnumChatFormatting value = this.getEnumValue(this.getProp().getString());
                if (value == null || !value.isColor()) {
                    value = this.getEnumValue(this.getProp().getDefault());
                }
                field.set(null, value);
                break;
            }
            case ENUM: {
                Enum<?> value = this.getEnumValue(this.getProp().getString());
                if (value == null) {
                    value = this.getEnumValue(this.getProp().getDefault());
                }
                field.set(null, value);
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
                throw new IllegalStateException("Type of field not handled by config lib " + field.getType());
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
            case ENUM: {
                this.getProp().set(((Enum<?>) field.get(null)).name());
                break;
            }
            case STRING_LIST: {
                //noinspection unchecked
                this.getProp().set(((List<String>) field.get(null)).toArray(new String[0]));
                break;
            }
            default: {
                throw new IllegalStateException("Type of field not handled by config lib " + field.getType());
            }
        }
    }

    public String getKey() {
        return annotation.category() + "$" + annotation.name();
    }

    private String getKey(String key) {
        return annotation.category() + "$" + key + annotation.name();
    }

    private Property getProp() {
        return propertyMap.get(this.getKey());
    }

    private Property getProp(String key) {
        return propertyMap.get(this.getKey(key));
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
                return new RendererGuiButton(this, configGuiScreen, rendererManager);
            }
            case BOOLEAN: {
                return new BooleanGuiButton(this, configGuiScreen);
            }
            case DOUBLE: {
                return new SliderGuiButton(this);
            }
            case INT: {
                if (annotation.isColor()) {
                    final int defaultColor = Integer.parseInt(this.getProp().getDefault());
                    return new ColorGuiButton(this, configGuiScreen, defaultColor);
                } else {
                    return new SliderGuiButton(this);
                }
            }
            case ENUM_COLOR: {
                return new ColorEnumGuiButton(this, configGuiScreen);
            }
            case ENUM: {
                return new EnumGuiButton(this);
            }
        }
        throw new IllegalStateException("Type of field not handled by config lib gui screen " + field.getType() + " you can mark the field as hidden to prevent crashing");
    }

    void setDependency(ConfigFieldContainer dependency) {
        if (dependency != null) {
            if (dependency.fieldType != FieldType.BOOLEAN && dependency.fieldType != FieldType.RENDERER) {
                throw new IllegalStateException("Cannot depend on a config field other than boolean or RendererPosition");
            }
            this.dependency = dependency;
            this.dependency.hasDependent = true;
        }
    }

    public boolean isVisible() {
        if (this.dependency == null) {
            return true;
        }
        try {
            return this.dependency.isActive();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isActive() throws IllegalAccessException {
        if (this.dependency != null && !this.dependency.isActive()) {
            return false;
        }
        if (this.fieldType == FieldType.BOOLEAN) {
            return (boolean) this.field.get(null);
        } else if (this.fieldType == FieldType.RENDERER) {
            return ((RendererPosition) this.field.get(null)).isEnabled();
        }
        throw new UnsupportedOperationException();
    }

    public Field getField() {
        return field;
    }

    public Method getEvent() {
        return event;
    }

    public ConfigProperty getAnnotation() {
        return annotation;
    }

    public boolean hasDependent() {
        return hasDependent;
    }

    @Nullable
    private <T extends Enum<T>> T getEnumValue(@NotNull String valueName) {
        if (!Enum.class.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException();
        }
        try {
            //noinspection unchecked
            return Enum.valueOf((Class<T>) field.getType(), valueName);
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
        ENUM,
        STRING_LIST
    }

}
