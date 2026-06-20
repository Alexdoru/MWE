package fr.alexdoru.configlib.lib;

import fr.alexdoru.configlib.api.*;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

public final class ConfigHandler implements IConfigHandler {

    private final Configuration config;
    private final String configName;
    private final String savedVersion;
    private final String version;
    private final boolean hasUpdated;
    private final Map<String, Property> propertyMap = new HashMap<>();
    private final List<ConfigFieldContainer> configFields = new ArrayList<>();
    private final Set<String> categoryNames = new HashSet<>();
    private final List<ConfigCategoryContainer> categories = new ArrayList<>();
    /** CategoryName -> SubCategoryName -> List of ConfigSetting */
    private final LinkedHashMap<String, LinkedHashMap<String, List<String>>> configStructure = new LinkedHashMap<>();
    private final RendererManager rendererManager;
    @Nullable
    private IConfigTitleRenderer titleRenderer;
    @NotNull
    private ColorPalette colorPalette = new ColorPalette();
    private boolean hasCommand;

    /**
     * Creates a new config handler
     *
     * @param configFile - the config file to load from
     * @param configName - the name of your config, this will show as the title in the config scren
     */
    public ConfigHandler(@NotNull File configFile, @NotNull String configName) {
        this(configFile, configName, "1.0");
    }

    /**
     * Creates a new config handler
     *
     * @param configFile    - the config file to load from
     * @param configName    - the name of your config, this will show as the title in the config scren
     * @param configVersion - the current version of your mod
     */
    public ConfigHandler(@NotNull File configFile, @NotNull String configName, @NotNull String configVersion) {
        Objects.requireNonNull(configFile);
        Objects.requireNonNull(configName);
        Objects.requireNonNull(configVersion);
        this.config = new Configuration(configFile);
        this.config.load();
        this.configName = configName;
        final Property modVersion = config.get("Config", "version", configVersion);
        this.savedVersion = modVersion.getString();
        this.version = configVersion;
        this.hasUpdated = !this.savedVersion.equals(configVersion);
        if (this.hasUpdated) {
            modVersion.set(configVersion);
            config.save();
        }
        this.rendererManager = new RendererManager(this.configName);
    }

    @Override
    public void registerConfig(@NotNull Class<?> clazz) {
        if (config == null) {
            throw new IllegalStateException("Config file is not loaded yet");
        }
        final List<Method> loadEvents = new ArrayList<>();
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
                    validateMethod(method, "hide condition", "()Z");
                    method.setAccessible(true);
                    final String[] configName = method.getAnnotation(ConfigPropertyHideOverride.class).name();
                    for (final String name : configName) {
                        configHideOverrides.put(name, method);
                        hideUsages.add(name);
                    }
                } else if (method.isAnnotationPresent(ConfigUpdatedEvent.class)) {
                    validateMethod(method, "update", "(Ljava/lang/String;Ljava/lang/String;)V");
                    method.setAccessible(true);
                    updateEvents.add(method);
                } else if (method.isAnnotationPresent(ConfigLoadedEvent.class)) {
                    validateMethod(method, "load", "()V");
                    method.setAccessible(true);
                    loadEvents.add(method);
                }
            }
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    final ConfigFieldContainer fieldContainer = new ConfigFieldContainer(config, propertyMap, field, configEvents, configHideOverrides);
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
            throw new RuntimeException("Caught exception while registering config class " + clazz.getName(), e);
        }
        try {
            readConfigInDefinitionOrder(clazz);
        } catch (IOException e) {
            readConfigInAlphabeticalOrder(clazz);
            e.printStackTrace();
        }
        setConfigPropertyOrder();
        for (final Method method : loadEvents) {
            try {
                method.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        if (hasUpdated) {
            for (final Method method : updateEvents) {
                try {
                    method.invoke(null, this.savedVersion, this.version);
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

    @Override
    public void saveConfig() {
        if (configFields.isEmpty()) {
            throw new IllegalStateException("Config is empty!");
        }
        try {
            for (final ConfigFieldContainer configField : configFields) {
                configField.saveFieldValueToConfig();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (config.hasChanged()) {
            config.save();
        }
    }

    @Override
    public @NotNull GuiScreen getConfigGuiScreen() {
        if (configFields.isEmpty()) {
            throw new IllegalStateException("Config is empty!");
        }
        try {
            return new ConfigGuiScreen(this, configName, categories, configFields, configStructure, colorPalette, titleRenderer, rendererManager);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to generate the config menu!", e);
        }
    }

    @Override
    public void registerConfigCommand(@NotNull String commandName) {
        Objects.requireNonNull(commandName);
        if (this.hasCommand) {
            throw new IllegalStateException("Config command already registered");
        }
        ClientCommandHandler.instance.registerCommand(new ConfigCommand(this, commandName));
        this.hasCommand = true;
    }

    @Override
    public void setConfigTitleRenderer(@NotNull IConfigTitleRenderer titleRenderer) {
        Objects.requireNonNull(titleRenderer);
        this.titleRenderer = titleRenderer;
    }

    @NotNull
    @Override
    public IRendererManager getRendererManager() {
        return this.rendererManager;
    }

    @Override
    public void setColorPalette(@NotNull ColorPalette colorPalette) {
        Objects.requireNonNull(colorPalette);
        this.colorPalette = colorPalette;
    }

    private void readConfigInDefinitionOrder(Class<?> clazz) throws IOException {
        // We need to parse the class bytes with ASM because
        // the reflection method class.getDeclaredFields()
        // returns the fields in no specific order.
        final InputStream is = clazz.getResourceAsStream('/' + clazz.getName().replace('.', '/') + ".class");
        if (is == null) return;
        final ClassReader cr = new ClassReader(is);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_CODE);
        final String annotationDesc = Type.getDescriptor(ConfigProperty.class);
        final String rendererPositionDesc = Type.getDescriptor(RendererPosition.class);
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
                    final boolean isRendererPosition = field.desc.equals(rendererPositionDesc);
                    if (isRendererPosition) {
                        subCatConfigs.add("Show " + configName);
                        subCatConfigs.add("Xpos " + configName);
                        subCatConfigs.add("Ypos " + configName);
                    }
                    subCatConfigs.add(configName);
                }
            }
        }
    }

    private void readConfigInAlphabeticalOrder(Class<?> clazz) {
        for (final Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                final ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                final LinkedHashMap<String, List<String>> subCategory = configStructure.computeIfAbsent(annotation.category(), m -> new LinkedHashMap<>());
                final List<String> subCatConfigs = subCategory.computeIfAbsent(annotation.subCategory(), l -> new ArrayList<>());
                if (field.getType() == RendererPosition.class) {
                    subCatConfigs.add("Show " + annotation.name());
                    subCatConfigs.add("Xpos " + annotation.name());
                    subCatConfigs.add("Ypos " + annotation.name());
                }
                subCatConfigs.add(annotation.name());
            }
        }
        sortCategoriesAndSubCategories();
    }

    private void sortCategoriesAndSubCategories() {
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
