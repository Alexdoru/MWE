package fr.alexdoru.configlib.lib;

import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.api.RendererAlignment;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.api.RendererPositionV2;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.elements.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
        } else if (field.getType() == RendererPositionV2.class) {
            return FieldType.RENDERER_V2;
        } else if (field.getType() == String.class) {
            return FieldType.STRING;
        } else if (field.getType() == boolean.class) {
            return FieldType.BOOLEAN;
        } else if (field.getType() == double.class) {
            return FieldType.DOUBLE;
        } else if (field.getType() == int.class) {
            return FieldType.INT;
        } else if (field.getType() == KeyBinding.class) {
            return FieldType.KEYBIND;
        } else if (field.getType() == EnumChatFormatting.class) {
            return FieldType.ENUM_COLOR;
        } else if (Enum.class.isAssignableFrom(field.getType())) {
            return FieldType.ENUM;
        } else if (field.getType() == String[].class) {
            return FieldType.STRING_ARRAY;
        } else if (field.getType() == boolean[].class) {
            return FieldType.BOOLEAN_ARRAY;
        } else if (field.getType() == double[].class) {
            return FieldType.DOUBLE_ARRAY;
        } else if (field.getType() == int[].class) {
            return FieldType.INT_ARRAY;
        } else if (List.class.isAssignableFrom(field.getType())) {
            final Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                final ParameterizedType parameterized = (ParameterizedType) genericType;
                final Type[] typeArgs = parameterized.getActualTypeArguments();
                if (typeArgs.length == 1) {
                    if (typeArgs[0] == String.class) {
                        return FieldType.STRING_LIST;
                    } else if (typeArgs[0] == Boolean.class) {
                        return FieldType.BOOLEAN_LIST;
                    } else if (typeArgs[0] == Double.class) {
                        return FieldType.DOUBLE_LIST;
                    } else if (typeArgs[0] == Integer.class) {
                        return FieldType.INT_LIST;
                    }
                }
            }
        }
        throw new IllegalStateException("Type of field not handled by config lib " + field.getType());
    }

    private void createPropertyFromField(Configuration config) throws IllegalAccessException {
        if (annotation.isColor() && this.fieldType != FieldType.INT) {
            throw new IllegalStateException("isColor can only be used on int fields : " + annotation.category() + " " + annotation.name());
        }
        if (annotation.isFormattedString() && this.fieldType != FieldType.STRING) {
            throw new IllegalStateException("isFormattedString can only be used on String fields : " + annotation.category() + " " + annotation.name());
        }
        switch (this.fieldType) {
            case RENDERER: {
                final RendererPosition rendererPosition = (RendererPosition) field.get(null);
                Objects.requireNonNull(rendererPosition);
                propertyMap.put(this.getKey("Show "), config.get(annotation.category(), "Show " + annotation.name(), rendererPosition.isEnabled()));
                propertyMap.put(this.getKey("Xpos "), config.get(annotation.category(), "Xpos " + annotation.name(), rendererPosition.getRelativeX()));
                propertyMap.put(this.getKey("Ypos "), config.get(annotation.category(), "Ypos " + annotation.name(), rendererPosition.getRelativeY()));
                break;
            }
            case RENDERER_V2: {
                final RendererPositionV2 rendererPosition = (RendererPositionV2) field.get(null);
                Objects.requireNonNull(rendererPosition);
                propertyMap.put(this.getKey("Show "), config.get(annotation.category(), "Show " + annotation.name(), rendererPosition.isEnabled()));
                propertyMap.put(this.getKey("Xpos "), config.get(annotation.category(), "Xpos " + annotation.name(), rendererPosition.getRelativeX()));
                propertyMap.put(this.getKey("Ypos "), config.get(annotation.category(), "Ypos " + annotation.name(), rendererPosition.getRelativeY()));
                propertyMap.put(this.getKey("Scale "), config.get(annotation.category(), "Scale " + annotation.name(), rendererPosition.getScale()));
                propertyMap.put(this.getKey("Align "), config.get(annotation.category(), "Align " + annotation.name(), rendererPosition.getAlignment().name()));
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
                this.validateSliderBounds();
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), (double) field.get(null)));
                break;
            }
            case INT: {
                if (!annotation.isColor()) {
                    this.validateSliderBounds();
                }
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), (int) field.get(null)));
                break;
            }
            case KEYBIND: {
                final KeyBinding keyBinding = (KeyBinding) field.get(null);
                Objects.requireNonNull(keyBinding, "Keybinding fields must not be null");
                ClientRegistry.registerKeyBinding(keyBinding);
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
                final String[] defaultStrings = ((List<String>) this.validateListField()).toArray(new String[0]);
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), defaultStrings));
                break;
            }
            case BOOLEAN_LIST: {
                //noinspection unchecked
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), toBooleanArray((List<Boolean>) this.validateListField())));
                break;
            }
            case DOUBLE_LIST: {
                //noinspection unchecked
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), toDoubleArray((List<Double>) this.validateListField())));
                break;
            }
            case INT_LIST: {
                //noinspection unchecked
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), toIntArray((List<Integer>) this.validateListField())));
                break;
            }
            case STRING_ARRAY: {
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), (String[]) this.validateArrayField()));
                break;
            }
            case BOOLEAN_ARRAY: {
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), (boolean[]) this.validateArrayField()));
                break;
            }
            case DOUBLE_ARRAY: {
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), (double[]) this.validateArrayField()));
                break;
            }
            case INT_ARRAY: {
                propertyMap.put(this.getKey(), config.get(annotation.category(), annotation.name(), (int[]) this.validateArrayField()));
                break;
            }
            default: {
                throw new IllegalStateException("Type of field not handled by config lib " + field.getType());
            }
        }
    }

    private void validateSliderBounds() {
        if (!annotation.hidden() && annotation.sliderMin() >= annotation.sliderMax()) {
            throw new IllegalStateException("Config slider min must be lower than max : " + annotation.category() + " " + annotation.name());
        }
    }

    private Object validateArrayField() throws IllegalAccessException {
        final String fieldName = annotation.category() + " " + annotation.name();
        if (Modifier.isFinal(field.getModifiers())) {
            throw new IllegalStateException("Array config fields must not be final : " + fieldName);
        }
        final Object array = Objects.requireNonNull(field.get(null), "Array config fields must not be null : " + fieldName);
        if (array instanceof Object[] && Arrays.asList((Object[]) array).contains(null)) {
            throw new IllegalStateException("Array config fields must not contain null elements : " + fieldName);
        }
        return array;
    }

    private List<?> validateListField() throws IllegalAccessException {
        final String fieldName = annotation.category() + " " + annotation.name();
        //noinspection unchecked
        final List<Object> list = (List<Object>) field.get(null);
        Objects.requireNonNull(list, "List config fields must not be null : " + fieldName);
        try {
            list.add(null);
            list.remove(list.size() - 1);
        } catch (UnsupportedOperationException e) {
            throw new IllegalStateException("List config fields must be mutable : " + fieldName, e);
        }
        if (list.contains(null)) {
            throw new IllegalStateException("List config fields must not contain null elements : " + fieldName);
        }
        return list;
    }

    /** Hidden fields may leave sliderMin == sliderMax, meaning the value is unbounded */
    private boolean hasSliderBounds() {
        return annotation.sliderMin() < annotation.sliderMax();
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
            case RENDERER_V2: {
                final RendererPositionV2 position = (RendererPositionV2) field.get(null);
                position.setEnabled(this.getProp("Show ").getBoolean());
                position.setRelativePosition(
                        this.getProp("Xpos ").getDouble(),
                        this.getProp("Ypos ").getDouble()
                );
                position.setScale(this.getProp("Scale ").getDouble());
                final RendererAlignment alignment = getEnumValue(RendererAlignment.class, this.getProp("Align ").getString());
                position.setAlignment(alignment == null ? position.getDefaultAlignment() : alignment);
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
                double value = this.getProp().getDouble();
                if (this.hasSliderBounds()) {
                    value = MathHelper.clamp_double(value, annotation.sliderMin(), annotation.sliderMax());
                }
                field.setDouble(null, value);
                break;
            }
            case INT: {
                int value = this.getProp().getInt();
                if (!annotation.isColor() && this.hasSliderBounds()) {
                    value = MathHelper.clamp_int(value, annotation.sliderMin(), annotation.sliderMax());
                }
                field.setInt(null, value);
                break;
            }
            case KEYBIND: {
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
            case INT_LIST: {
                //noinspection unchecked
                final List<Integer> list = (List<Integer>) field.get(null);
                list.clear();
                Arrays.stream(this.getProp().getIntList()).forEach(list::add);
                break;
            }
            case DOUBLE_LIST: {
                //noinspection unchecked
                final List<Double> list = (List<Double>) field.get(null);
                list.clear();
                Arrays.stream(this.getProp().getDoubleList()).forEach(list::add);
                break;
            }
            case BOOLEAN_LIST: {
                //noinspection unchecked
                final List<Boolean> list = (List<Boolean>) field.get(null);
                list.clear();
                for (final boolean value : this.getProp().getBooleanList()) {
                    list.add(value);
                }
                break;
            }
            case INT_ARRAY: {
                field.set(null, this.getProp().getIntList());
                break;
            }
            case DOUBLE_ARRAY: {
                field.set(null, this.getProp().getDoubleList());
                break;
            }
            case BOOLEAN_ARRAY: {
                field.set(null, this.getProp().getBooleanList());
                break;
            }
            case STRING_ARRAY: {
                field.set(null, this.getProp().getStringList().clone());
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
            case RENDERER_V2: {
                final RendererPositionV2 rendererPosition = (RendererPositionV2) field.get(null);
                this.getProp("Show ").set(rendererPosition.isEnabled());
                this.getProp("Xpos ").set(rendererPosition.getRelativeX());
                this.getProp("Ypos ").set(rendererPosition.getRelativeY());
                this.getProp("Scale ").set(rendererPosition.getScale());
                this.getProp("Align ").set(rendererPosition.getAlignment().name());
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
            case KEYBIND: {
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
            case INT_ARRAY: {
                this.getProp().set((int[]) field.get(null));
                break;
            }
            case DOUBLE_ARRAY: {
                this.getProp().set((double[]) field.get(null));
                break;
            }
            case BOOLEAN_ARRAY: {
                this.getProp().set((boolean[]) field.get(null));
                break;
            }
            case STRING_ARRAY: {
                this.getProp().set((String[]) field.get(null));
                break;
            }
            case INT_LIST: {
                //noinspection unchecked
                this.getProp().set(toIntArray((List<Integer>) field.get(null)));
                break;
            }
            case DOUBLE_LIST: {
                //noinspection unchecked
                this.getProp().set(toDoubleArray((List<Double>) field.get(null)));
                break;
            }
            case BOOLEAN_LIST: {
                //noinspection unchecked
                this.getProp().set(toBooleanArray((List<Boolean>) field.get(null)));
                break;
            }
            default: {
                throw new IllegalStateException("Type of field not handled by config lib " + field.getType());
            }
        }
    }

    private static boolean[] toBooleanArray(List<Boolean> list) {
        final boolean[] array = new boolean[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static double[] toDoubleArray(List<Double> list) {
        final double[] array = new double[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static int[] toIntArray(List<Integer> list) {
        final int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
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
            case RENDERER:
            case RENDERER_V2: {
                return new RendererGuiButton(this, configGuiScreen, rendererManager);
            }
            case STRING: {
                return new StringGuiButton(this, configGuiScreen, this.getProp().getDefault(), annotation.isFormattedString());
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
            case KEYBIND: {
                return new KeybindGuiButton(this);
            }
            case ENUM_COLOR: {
                return new ColorEnumGuiButton(this, configGuiScreen);
            }
            case ENUM: {
                return new EnumGuiButton(this, configGuiScreen);
            }
        }
        throw new IllegalStateException("Type of field not handled by config lib gui screen " + field.getType() + " you can mark the field as hidden to prevent crashing");
    }

    void setDependency(ConfigFieldContainer dependency) {
        if (dependency != null) {
            if (dependency.fieldType != FieldType.BOOLEAN && dependency.fieldType != FieldType.RENDERER && dependency.fieldType != FieldType.RENDERER_V2) {
                throw new IllegalStateException("Cannot depend on a config field other than boolean or RendererPosition");
            }
            for (ConfigFieldContainer parent = dependency; parent != null; parent = parent.dependency) {
                if (parent == this) {
                    throw new IllegalStateException("Circular dependency detected for config " + annotation.category() + " " + annotation.name());
                }
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
        } else if (this.fieldType == FieldType.RENDERER || this.fieldType == FieldType.RENDERER_V2) {
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
        //noinspection unchecked
        return getEnumValue((Class<T>) field.getType(), valueName);
    }

    @Nullable
    private static <T extends Enum<T>> T getEnumValue(@NotNull Class<T> enumClass, @NotNull String valueName) {
        try {
            return Enum.valueOf(enumClass, valueName);
        } catch (IllegalArgumentException ignored) {}
        return null;
    }

    enum FieldType {
        RENDERER,
        RENDERER_V2,
        STRING,
        BOOLEAN,
        DOUBLE,
        INT,
        KEYBIND,
        ENUM_COLOR,
        ENUM,
        STRING_ARRAY,
        BOOLEAN_ARRAY,
        DOUBLE_ARRAY,
        INT_ARRAY,
        STRING_LIST,
        BOOLEAN_LIST,
        DOUBLE_LIST,
        INT_LIST
    }

}
