package fr.alexdoru.configlib.lib;

import fr.alexdoru.configlib.api.ConfigProperty;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

public final class ConfigFieldRegistry {

    private final Map<String, Method> events = new HashMap<>();
    private final Set<String> allEventNames = new HashSet<>();
    private final Map<String, Method> hideOverrides = new HashMap<>();
    private final Set<String> allHideNames = new HashSet<>();
    private final List<ConfigFieldContainer> configFields = new ArrayList<>();
    private final Map<String, ConfigFieldContainer> namesToConfigFields = new HashMap<>();
    private final Map<String, ConfigFieldContainer> collidingConfigNames = new HashMap<>();
    private final Map<String, ConfigProperty> shortKeyUsages = new HashMap<>();

    ConfigFieldRegistry() {}

    public void addEvent(String[] names, Method method) {
        for (final String name : names) {
            ConfigHandler.validateString(name, "Event config name");
            events.put(name, method);
            allEventNames.add(name);
        }
    }

    public void addHideOverride(String[] names, Method method) {
        for (final String name : names) {
            ConfigHandler.validateString(name, "Hide override config name");
            hideOverrides.put(name, method);
            allHideNames.add(name);
        }
    }

    public void add(ConfigFieldContainer field) {
        if (namesToConfigFields.containsKey(field.getKey())) {
            throw new IllegalStateException("Config properties with duplicate key names : " + field.getAnnotation().category() + " " + field.getAnnotation().name());
        }
        namesToConfigFields.put(field.getKey(), field);
        final ConfigFieldContainer oldValue = namesToConfigFields.put(field.getAnnotation().name(), field);
        if (oldValue != null) {
            collidingConfigNames.put(field.getAnnotation().name(), oldValue);
        }
        configFields.add(field);
    }

    @Nullable
    public Method getEvent(ConfigProperty annotation) {
        final String shortKey = shortKey(annotation);
        Method event = events.get(shortKey);
        if (event != null) {
            checkKeyCollison(shortKey, annotation, "event");
            allEventNames.remove(shortKey);
            return event;
        }
        final String longKey = longKey(annotation);
        event = events.get(longKey);
        if (event != null) {
            allEventNames.remove(longKey);
        }
        return event;
    }

    @Nullable
    public Method getHideOverride(ConfigProperty annotation) {
        final String shortKey = shortKey(annotation);
        Method hide = hideOverrides.get(shortKey);
        if (hide != null) {
            checkKeyCollison(shortKey, annotation, "hide override");
            allHideNames.remove(shortKey);
            return hide;
        }
        final String longKey = longKey(annotation);
        hide = hideOverrides.get(longKey);
        if (hide != null) {
            allHideNames.remove(longKey);
        }
        return hide;
    }

    @Nullable
    private ConfigFieldContainer getDependency(ConfigProperty annotation) {
        if (annotation.dependsOn().isEmpty()) {
            return null;
        }
        final ConfigFieldContainer dependency = namesToConfigFields.get(annotation.dependsOn());
        if (dependency == null) {
            throw new IllegalStateException("Dependency " + annotation.dependsOn() + " for config " + annotation.category() + " " + annotation.name() + " not found");
        }
        final boolean usedShortKey = annotation.dependsOn().equals(dependency.getAnnotation().name());
        if (usedShortKey && collidingConfigNames.containsKey(annotation.dependsOn())) {
            throw new IllegalArgumentException("Ambiguous config name used for dependency, could be "
                    + longKey(namesToConfigFields.get(annotation.dependsOn()).getAnnotation())
                    + " or "
                    + longKey(collidingConfigNames.get(annotation.dependsOn()).getAnnotation())
            );
        }
        return dependency;
    }

    private void checkKeyCollison(String shortKey, ConfigProperty annotation, String type) {
        final ConfigProperty otherProp = shortKeyUsages.get(shortKey);
        if (otherProp != null && otherProp != annotation) {
            throw new IllegalArgumentException("Ambiguous config name used for " + type + ", could be " + longKey(annotation) + " or " + longKey(otherProp));
        }
        shortKeyUsages.put(shortKey, annotation);
    }

    private static String shortKey(ConfigProperty annotation) {
        return annotation.name();
    }

    private static String longKey(ConfigProperty annotation) {
        return annotation.category() + "$" + annotation.name();
    }

    public void assignDependencies() {
        for (final ConfigFieldContainer configField : this.configFields) {
            configField.setDependency(this.getDependency(configField.getAnnotation()));
        }
    }

    public void checkUnused() {
        if (!allEventNames.isEmpty()) {
            throw new IllegalStateException("Some config events use config names that were not found : " + Arrays.toString(allEventNames.toArray()));
        }
        if (!allHideNames.isEmpty()) {
            throw new IllegalStateException("Some config hide overrides use config names that were not found : " + Arrays.toString(allHideNames.toArray()));
        }
    }

    public List<ConfigFieldContainer> getFields() {
        return this.configFields;
    }

    public boolean isEmpty() {
        return this.configFields.isEmpty();
    }

}
