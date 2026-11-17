package fr.alexdoru.mwe.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

import java.util.Locale;

public final class ConfigMigrationHelper {

    private ConfigMigrationHelper() {}

    public static boolean isVersionLowerThan(String versionToTest, String referenceVersion) {
        return new ComparableVersion(versionToTest).compareTo(new ComparableVersion(referenceVersion)) < 0;
    }

    public static boolean hasKey(Configuration config, String category, String key) {
        return config.hasKey(category.toLowerCase(Locale.ENGLISH), key);
    }

    public static void remove(Configuration config, String category, String key) {
        config.getCategory(category.toLowerCase(Locale.ENGLISH)).remove(key);
    }

    public static void migrateConfig(Configuration config, String oldCategory, String newCategory, String key) {
        migrateConfig(config, oldCategory, key, newCategory, key);
    }

    public static void migrateConfig(Configuration config, String oldCategory, String oldKey, String newCategory, String newKey) {
        if (hasKey(config, oldCategory, oldKey) && !hasKey(config, newCategory, newKey)) {
            final String oldCat = oldCategory.toLowerCase(Locale.ENGLISH);
            final String newCat = newCategory.toLowerCase(Locale.ENGLISH);
            final Property prop = config.getCategory(oldCat).remove(oldKey);
            // Forge writes the property under Property#getName, not under its map key
            prop.setName(newKey);
            config.getCategory(newCat).put(newKey, prop);
        }
    }

}
