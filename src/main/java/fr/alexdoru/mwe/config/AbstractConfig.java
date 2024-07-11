package fr.alexdoru.mwe.config;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.gui.guiapi.GuiPosition;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractConfig {

    private static Configuration config;
    private static final Map<String, Property> propertyMap = new HashMap<>();

    public static void loadConfig(File file) {
        if (config != null) throw new IllegalStateException("Config already exists!");
        config = new Configuration(file);
        config.load();
        try {
            for (final Field field : ConfigHandler.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    createPropertyFromField(field);
                    assignPropertyValueToField(field);
                }
            }
            setConfigPropertyOrder();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (config.hasChanged()) {
            config.save();
        }
        ConfigHandler.onModUpdate();
    }

    public static void saveConfig() {
        if (config == null) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.DARK_RED + "Config didn't load when the game started, this shouldn't happen !");
            throw new IllegalStateException("Config didn't load when the game started, this shouldn't happen !");
        }
        try {
            for (final Field field : ConfigHandler.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    assignFieldValueToProperty(field);
                }
            }
        } catch (IllegalAccessException e) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.DARK_RED + "Failed to save the config!");
            e.printStackTrace();
        }
        if (config.hasChanged()) {
            config.save();
        }
    }

    private static void setConfigPropertyOrder() {
        // We need to parse the class bytes with ASM because
        // the reflection method class.getDeclaredFields()
        // returns the fields in no specific order.
        final String className = "fr.alexdoru.mwe.config.ConfigHandler";
        try {
            final ClassReader cr = new ClassReader(className);
            final ClassNode cn = new ClassNode();
            cr.accept(cn, ClassReader.SKIP_CODE);
            final Set<String> configNames = new HashSet<>();
            final Map<String, List<String>> categoryOrderMap = new LinkedHashMap<>();
            final String annotationDesc = Type.getDescriptor(ConfigProperty.class);
            final String guiPositionDesc = Type.getDescriptor(GuiPosition.class);
            for (final FieldNode field : cn.fields) {
                if (field.visibleAnnotations == null) continue;
                for (final AnnotationNode annotation : field.visibleAnnotations) {
                    if (!annotation.desc.equals(annotationDesc)) continue;
                    String configCategory = null;
                    String configName = null;
                    for (int i = 0; i < annotation.values.size(); i += 2) {
                        final Object name = annotation.values.get(i);
                        final Object value = annotation.values.get(i + 1);
                        if ("category".equals(name) && value instanceof String) {
                            configCategory = ((String) value);
                        } else if ("name".equals(name) && value instanceof String) {
                            configName = (String) value;
                        }
                    }
                    if (configCategory != null && configName != null) {
                        if (!configNames.add(configName)) {
                            throw new IllegalStateException("Duplicate key names in config properties");
                        }
                        final List<String> list = categoryOrderMap.computeIfAbsent(configCategory, k -> new ArrayList<>());
                        final boolean isGuiPosition = field.desc.equals(guiPositionDesc);
                        if (isGuiPosition) {
                            list.add("Xpos " + configName);
                            list.add("Ypos " + configName);
                        } else {
                            list.add(configName);
                        }
                    }
                }
            }
            for (final Map.Entry<String, List<String>> entry : categoryOrderMap.entrySet()) {
                config.setCategoryPropertyOrder(entry.getKey(), entry.getValue());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void createPropertyFromField(Field field) throws IllegalAccessException {
        final ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
        if (field.getType() == GuiPosition.class) {
            final String xKey = "Xpos " + annotation.name();
            final String yKey = "Ypos " + annotation.name();
            if (propertyMap.containsKey(xKey) || propertyMap.containsKey(yKey)) {
                throw new IllegalStateException("Duplicate key names in config properties");
            }
            final GuiPosition guiPosition = (GuiPosition) field.get(null);
            propertyMap.put(xKey, config.get(annotation.category(), xKey, guiPosition.getRelativeX(), "The x " + annotation.comment() + ", value ranges from 0 to 1"));
            propertyMap.put(yKey, config.get(annotation.category(), yKey, guiPosition.getRelativeY(), "The y " + annotation.comment() + ", value ranges from 0 to 1"));
            return;
        }
        if (propertyMap.containsKey(annotation.name())) {
            throw new IllegalStateException("Duplicate key names in config properties");
        }
        if (field.getType() == String.class) {
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

    private static void assignPropertyValueToField(Field field) throws IllegalAccessException {
        final ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
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

    private static void assignFieldValueToProperty(Field field) throws IllegalAccessException {
        final ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
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
