package fr.alexdoru.configlib;

import fr.alexdoru.configlib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.gui.elements.CategoryGuiButton;

import java.lang.reflect.Field;

public final class ConfigCategoryContainer {

    private final String categoryName;
    private final ConfigCategory annotation;

    ConfigCategoryContainer(Field field) throws IllegalAccessException {
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
