package fr.alexdoru.mwe.config.lib;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.lib.gui.ConfigGuiScreen;
import fr.alexdoru.mwe.gui.guiapi.GuiPosition;
import fr.alexdoru.mwe.utils.MapUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class AbstractConfig {

    private final Class<?> configClass;
    private final Configuration config;
    private final LinkedHashMap<String, LinkedHashMap<String, List<String>>> configStructure = new LinkedHashMap<>();
    private final List<ConfigCategoryContainer> categories = new ArrayList<>();
    private final List<ConfigFieldContainer> configFields = new ArrayList<>();

    protected AbstractConfig(Class<?> clazz, File file) {
        configClass = clazz;
        config = new Configuration(file);
        config.load();
        try {
            final Map<String, Method> configEvents = new HashMap<>();
            final Set<String> eventUsages = new HashSet<>();
            final Map<String, Method> configHideOverrides = new HashMap<>();
            final Set<String> hideUsages = new HashSet<>();
            for (final Method method : configClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ConfigPropertyEvent.class)) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalStateException("Config event method " + method.getName() + " must be static");
                    }
                    final String desc = Type.getMethodDescriptor(method);
                    if (!"()V".equals(desc)) {
                        throw new IllegalStateException("Config event method " + method.getName() + " must be ()V");
                    }
                    final String[] configName = method.getAnnotation(ConfigPropertyEvent.class).name();
                    for (final String name : configName) {
                        configEvents.put(name, method);
                        eventUsages.add(name);
                    }
                } else if (method.isAnnotationPresent(ConfigPropertyHideOverride.class)) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalStateException("Config hide condition method " + method.getName() + " must be static");
                    }
                    final String desc = Type.getMethodDescriptor(method);
                    if (!"()Z".equals(desc)) {
                        throw new IllegalStateException("Config hide condition method " + method.getName() + " must be ()Z");
                    }
                    final String[] configName = method.getAnnotation(ConfigPropertyHideOverride.class).name();
                    for (final String name : configName) {
                        configHideOverrides.put(name, method);
                        hideUsages.add(name);
                    }
                }
            }
            final Map<String, Property> propertyMap = new HashMap<>();
            for (final Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    final ConfigFieldContainer fieldContainer = new ConfigFieldContainer(field, configEvents, configHideOverrides, propertyMap, config);
                    eventUsages.remove(fieldContainer.getAnnotation().name());
                    hideUsages.remove(fieldContainer.getAnnotation().name());
                    configFields.add(fieldContainer);
                } else if (field.isAnnotationPresent(ConfigCategory.class)) {
                    categories.add(new ConfigCategoryContainer(field));
                }
            }
            if (!eventUsages.isEmpty()) {
                throw new IllegalStateException("Some config events are not used anywhere : " + Arrays.toString(eventUsages.toArray()));
            }
            if (!hideUsages.isEmpty()) {
                throw new IllegalStateException("Some config hide conditions are not used anywhere : " + Arrays.toString(hideUsages.toArray()));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create the config!");
        }
        try {
            readConfigInDefinitionOrder();
        } catch (IOException e) {
            readConfigInAlphabeticalOrder();
            e.printStackTrace();
        }
        setConfigPropertyOrder();
        if (config.hasChanged()) {
            config.save();
        }
    }

    public void save() {
        try {
            for (final ConfigFieldContainer configField : configFields) {
                configField.saveFieldValueToConfig();
            }
        } catch (IllegalAccessException e) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.DARK_RED + "Failed to save the config!");
            e.printStackTrace();
        }
        if (config.hasChanged()) {
            config.save();
        }
    }

    public GuiScreen getConfigGuiScreen() {
        try {
            return new ConfigGuiScreen(this, categories, configFields, configStructure);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to generate the config menu!");
        }
    }

    private void readConfigInDefinitionOrder() throws IOException {
        // We need to parse the class bytes with ASM because
        // the reflection method class.getDeclaredFields()
        // returns the fields in no specific order.
        final InputStream is = configClass.getResourceAsStream('/' + configClass.getName().replace('.', '/') + ".class");
        if (is == null) return;
        final ClassReader cr = new ClassReader(is);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_CODE);
        final Set<String> configNames = new HashSet<>();
        final String annotationDesc = Type.getDescriptor(ConfigProperty.class);
        final String guiPositionDesc = Type.getDescriptor(GuiPosition.class);
        for (final FieldNode field : cn.fields) {
            if (field.visibleAnnotations == null) continue;
            for (final AnnotationNode annotation : field.visibleAnnotations) {
                if (!annotation.desc.equals(annotationDesc)) continue;
                String configCategory = null;
                String configSubCategory = "";
                String configName = null;
                for (int i = 0; i < annotation.values.size(); i += 2) {
                    final Object name = annotation.values.get(i);
                    final Object value = annotation.values.get(i + 1);
                    if ("category".equals(name) && value instanceof String) {
                        configCategory = ((String) value);
                    } else if ("subCategory".equals(name) && value instanceof String) {
                        configSubCategory = ((String) value);
                    } else if ("name".equals(name) && value instanceof String) {
                        configName = (String) value;
                    }
                }
                if (configCategory != null && configName != null) {
                    if (!configNames.add(configName)) {
                        throw new IllegalStateException("Duplicate key names in config properties");
                    }
                    final LinkedHashMap<String, List<String>> subCategory = configStructure.computeIfAbsent(configCategory, m -> new LinkedHashMap<>());
                    final List<String> subCatConfigs = subCategory.computeIfAbsent(configSubCategory, l -> new ArrayList<>());
                    final boolean isGuiPosition = field.desc.equals(guiPositionDesc);
                    if (isGuiPosition) {
                        subCatConfigs.add("Show " + configName);
                        subCatConfigs.add("Xpos " + configName);
                        subCatConfigs.add("Ypos " + configName);
                    }
                    subCatConfigs.add(configName);
                }
            }
        }
    }

    private void readConfigInAlphabeticalOrder() {
        final LinkedHashMap<String, LinkedHashMap<String, List<String>>> unsortedStructure = new LinkedHashMap<>();
        for (final ConfigFieldContainer configField : configFields) {
            final ConfigProperty annotation = configField.getAnnotation();
            final LinkedHashMap<String, List<String>> subCategory = unsortedStructure.computeIfAbsent(annotation.category(), m -> new LinkedHashMap<>());
            final List<String> subCatConfigs = subCategory.computeIfAbsent(annotation.subCategory(), l -> new ArrayList<>());
            final boolean isGuiPosition = configField.getType() == GuiPosition.class;
            if (isGuiPosition) {
                subCatConfigs.add("Show " + annotation.name());
                subCatConfigs.add("Xpos " + annotation.name());
                subCatConfigs.add("Ypos " + annotation.name());
            }
            subCatConfigs.add(annotation.name());
        }
        for (final Map.Entry<String, LinkedHashMap<String, List<String>>> entry : MapUtil.sortByKey(unsortedStructure).entrySet()) {
            configStructure.put(entry.getKey(), MapUtil.sortByKey(entry.getValue()));
        }
    }

    private void setConfigPropertyOrder() {
        for (final Map.Entry<String, LinkedHashMap<String, List<String>>> entry : configStructure.entrySet()) {
            final LinkedHashMap<String, List<String>> subCategory = entry.getValue();
            final List<String> categoryConfigs = new ArrayList<>();
            for (final List<String> configs : subCategory.values()) {
                categoryConfigs.addAll(configs);
            }
            config.setCategoryPropertyOrder(entry.getKey(), categoryConfigs);
        }
    }

}
