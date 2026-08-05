package fr.alexdoru.configlib.lib;

import fr.alexdoru.configlib.api.ConfigProperty;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

public final class ConfigNameResolver {

    private final Map<String, Method> events = new HashMap<>();
    private final Set<String> allEventNames = new HashSet<>();
    private final Map<String, Method> hideOverrides = new HashMap<>();
    private final Set<String> allHideNames = new HashSet<>();
    private final Map<String, ConfigProperty> shortKeyUsages = new HashMap<>();

    ConfigNameResolver() {}

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

    public void checkUnused() {
        if (!allEventNames.isEmpty()) {
            throw new IllegalStateException("Some config events use config names that were not found : " + Arrays.toString(allEventNames.toArray()));
        }
        if (!allHideNames.isEmpty()) {
            throw new IllegalStateException("Some config hide overrides use config names that were not found : " + Arrays.toString(allHideNames.toArray()));
        }
    }

}
