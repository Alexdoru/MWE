package fr.alexdoru.mwe.config.lib;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.GuiPosition;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.lib.gui.ConfigGuiScreen;
import fr.alexdoru.mwe.utils.DelayedTask;
import net.minecraft.client.Minecraft;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ConfigHandler {

    private static Configuration config;
    private static boolean hasUpdated;
    private static final List<Class<?>> registeredConfigs = new ArrayList<>();
    private static final Map<String, Property> propertyMap = new HashMap<>();
    private static final List<ConfigFieldContainer> configFields = new ArrayList<>();
    private static final Set<String> categoryNames = new HashSet<>();
    private static final List<ConfigCategoryContainer> categories = new ArrayList<>();
    /** CategoryName -> SubCategoryName -> List of ConfigSetting */
    private static final LinkedHashMap<String, LinkedHashMap<String, List<String>>> configStructure = new LinkedHashMap<>();

    public static void loadConfigFile(File file) {
        if (config != null) {
            throw new IllegalStateException("Config already created!");
        }
        config = new Configuration(file);
        config.load();
        final Property modVersion = config.get("General", "Mod Version", MWE.version);
        final String savedVersion = modVersion.getString();
        hasUpdated = !savedVersion.equals(MWE.version);
        if (hasUpdated) {
            modVersion.set(MWE.version);
            config.save();
        }
    }

    public static void registerConfig(Class<?> clazz) {
        if (config == null) {
            throw new IllegalStateException("Config file is not loaded yet");
        }
        registeredConfigs.add(clazz);
        final List<Method> updateEvents = new ArrayList<>();
        try {
            final Map<String, Method> configEvents = new HashMap<>();
            final Set<String> eventUsages = new HashSet<>();
            final Map<String, Method> configHideOverrides = new HashMap<>();
            final Set<String> hideUsages = new HashSet<>();
            for (final Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ConfigPropertyEvent.class)) {
                    validateMethod(method, "event", "()V");
                    method.setAccessible(true);
                    final String[] configName = method.getAnnotation(ConfigPropertyEvent.class).name();
                    for (final String name : configName) {
                        configEvents.put(name, method);
                        eventUsages.add(name);
                    }
                } else if (method.isAnnotationPresent(ConfigPropertyHideOverride.class)) {
                    validateMethod(method, "hide condition ", "()Z");
                    method.setAccessible(true);
                    final String[] configName = method.getAnnotation(ConfigPropertyHideOverride.class).name();
                    for (final String name : configName) {
                        configHideOverrides.put(name, method);
                        hideUsages.add(name);
                    }
                } else if (method.isAnnotationPresent(ConfigUpdate.class)) {
                    validateMethod(method, "update", "()V");
                    method.setAccessible(true);
                    updateEvents.add(method);
                }
            }
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    final ConfigFieldContainer fieldContainer = new ConfigFieldContainer(field, configEvents, configHideOverrides, propertyMap, config);
                    eventUsages.remove(fieldContainer.getAnnotation().name());
                    hideUsages.remove(fieldContainer.getAnnotation().name());
                    configFields.add(fieldContainer);
                } else if (field.isAnnotationPresent(ConfigCategory.class)) {
                    field.setAccessible(true);
                    final ConfigCategoryContainer categoryContainer = new ConfigCategoryContainer(field);
                    final String categoryName = categoryContainer.getCategoryName();
                    if (!categoryNames.add(categoryName)) {
                        throw new IllegalStateException("Duplicate category names : " + categoryName);
                    }
                    categories.add(categoryContainer);
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
            readConfigInDefinitionOrder(clazz);
        } catch (IOException e) {
            readConfigInAlphabeticalOrder(clazz);
            e.printStackTrace();
        }
        setConfigPropertyOrder();
        if (hasUpdated) {
            for (final Method method : updateEvents) {
                try {
                    method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (config.hasChanged()) {
            config.save();
        }
    }

    private static void validateMethod(Method method, String methodType, String desc) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("Config " + methodType + " method " + method.getName() + " must be static");
        }
        if (!desc.equals(Type.getMethodDescriptor(method))) {
            throw new IllegalStateException("Config " + methodType + " method " + method.getName() + " must be " + desc);
        }
    }

    public static void saveConfig() {
        if (registeredConfigs.isEmpty()) {
            throw new IllegalStateException("Config is not loaded");
        }
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

    public static GuiScreen getConfigGuiScreen() {
        if (registeredConfigs.isEmpty()) {
            throw new IllegalStateException("Config is not loaded");
        }
        try {
            return new ConfigGuiScreen(categories, configFields, configStructure);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to generate the config menu!");
        }
    }

    public static void displayConfigGuiScreen() {
        new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(getConfigGuiScreen()));
    }

    private static void readConfigInDefinitionOrder(Class<?> clazz) throws IOException {
        // We need to parse the class bytes with ASM because
        // the reflection method class.getDeclaredFields()
        // returns the fields in no specific order.
        final InputStream is = clazz.getResourceAsStream('/' + clazz.getName().replace('.', '/') + ".class");
        if (is == null) return;
        final ClassReader cr = new ClassReader(is);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_CODE);
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

    private static void readConfigInAlphabeticalOrder(Class<?> clazz) {
        for (final Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                final ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                final LinkedHashMap<String, List<String>> subCategory = configStructure.computeIfAbsent(annotation.category(), m -> new LinkedHashMap<>());
                final List<String> subCatConfigs = subCategory.computeIfAbsent(annotation.subCategory(), l -> new ArrayList<>());
                if (field.getType() == GuiPosition.class) {
                    subCatConfigs.add("Show " + annotation.name());
                    subCatConfigs.add("Xpos " + annotation.name());
                    subCatConfigs.add("Ypos " + annotation.name());
                }
                subCatConfigs.add(annotation.name());
            }
        }
        sortCategoriesAndSubCategories();
    }

    private static void sortCategoriesAndSubCategories() {
        final List<Map.Entry<String, LinkedHashMap<String, List<String>>>> categories = new ArrayList<>(configStructure.entrySet());
        categories.sort(Map.Entry.comparingByKey());
        configStructure.clear();
        for (final Map.Entry<String, LinkedHashMap<String, List<String>>> entry : categories) {
            final String categoryName = entry.getKey();
            final LinkedHashMap<String, List<String>> subCategory = entry.getValue();
            final List<Map.Entry<String, List<String>>> sortedSubCategory = new ArrayList<>(subCategory.entrySet());
            sortedSubCategory.sort(Map.Entry.comparingByKey());
            subCategory.clear();
            for (final Map.Entry<String, List<String>> subEntry : sortedSubCategory) {
                final String subCategoryName = subEntry.getKey();
                final List<String> configs = subEntry.getValue();
                subCategory.put(subCategoryName, configs);
            }
            configStructure.put(categoryName, subCategory);
        }
    }

    private static void setConfigPropertyOrder() {
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
