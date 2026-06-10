package fr.alexdoru.mwe.config.lib;

import fr.alexdoru.mwe.api.config.ConfigCategory;
import fr.alexdoru.mwe.config.lib.gui.ConfigGuiScreen;
import fr.alexdoru.mwe.config.lib.gui.elements.CategoryGuiButton;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ConfigCategoryContainer {

    private static final Set<String> categoryNames = new HashSet<>();

    private final String categoryName;
    private final ConfigCategory annotation;

    ConfigCategoryContainer(Field field) throws IllegalAccessException {
        this.categoryName = ((String) field.get(null));
        this.annotation = field.getAnnotation(ConfigCategory.class);
        if (!categoryNames.add(categoryName)) {
            throw new IllegalStateException("Duplicate category names : " + categoryName);
        }
    }

    public CategoryGuiButton getCategoryButton(ConfigGuiScreen configGuiScreen) {
        return new CategoryGuiButton(configGuiScreen, categoryName, annotation.displayname());
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ConfigCategory getAnnotation() {
        return annotation;
    }

}
