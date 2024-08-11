package fr.alexdoru.mwe.config.lib;

import fr.alexdoru.mwe.config.lib.gui.ConfigGuiScreen;
import fr.alexdoru.mwe.config.lib.gui.elements.CategoryGuiButton;

import java.lang.reflect.Field;

public class ConfigCategoryContainer {

    private final String categoryName;
    private final ConfigCategory annotation;

    public ConfigCategoryContainer(Field field) throws IllegalAccessException {
        this.categoryName = ((String) field.get(null));
        this.annotation = field.getAnnotation(ConfigCategory.class);
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
