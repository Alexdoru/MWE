package fr.alexdoru.mwe.config;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Locale;

import static org.junit.Assert.*;

public class ConfigMigrationHelperTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private File configFile;
    private Configuration config;

    @Before
    public void setup() throws Exception {
        final Method build = FMLInjectionData.class.getDeclaredMethod("build", File.class, LaunchClassLoader.class);
        build.setAccessible(true);
        build.invoke(null, tempFolder.getRoot(), null);
        this.configFile = tempFolder.newFile("migration.cfg");
        this.config = new Configuration(configFile);
    }

    @Test
    public void comparableVersiontest() {
        assertTrue(ConfigMigrationHelper.isVersionLowerThan("4.0", "4.5"));
        assertFalse(ConfigMigrationHelper.isVersionLowerThan("4.0", "4.0"));
        assertFalse(ConfigMigrationHelper.isVersionLowerThan("4.0", "3.0"));
    }

    @Test
    public void versionsAreComparedNumerically() {
        assertTrue(ConfigMigrationHelper.isVersionLowerThan("4.9", "4.10"));
        assertFalse(ConfigMigrationHelper.isVersionLowerThan("4.10", "4.9"));
        assertTrue(ConfigMigrationHelper.isVersionLowerThan("4.10", "4.11"));
        assertTrue(ConfigMigrationHelper.isVersionLowerThan("4.10.1", "4.11"));
        assertFalse(ConfigMigrationHelper.isVersionLowerThan("4.11", "4.10.1"));
        assertFalse(ConfigMigrationHelper.isVersionLowerThan("4.10", "4.10.0"));
    }

    @Test
    public void hasKeyIgnoresCategoryCase() {
        config.get("Icons", "Red Warning Icon", "red");
        assertTrue(ConfigMigrationHelper.hasKey(config, "Icons", "Red Warning Icon"));
        assertTrue(ConfigMigrationHelper.hasKey(config, "icons", "Red Warning Icon"));
        assertFalse(ConfigMigrationHelper.hasKey(config, "Icons", "Yellow Warning Icon"));
        assertFalse(ConfigMigrationHelper.hasKey(config, "NoCheaters", "Red Warning Icon"));
    }

    @Test
    public void removeDeletesOnlyTheGivenKey() {
        config.get("Vanilla", "Hide ping tablist", true);
        config.get("Vanilla", "Other", false);
        ConfigMigrationHelper.remove(config, "Vanilla", "Hide ping tablist");
        assertFalse(ConfigMigrationHelper.hasKey(config, "Vanilla", "Hide ping tablist"));
        assertTrue(ConfigMigrationHelper.hasKey(config, "Vanilla", "Other"));
    }

    @Test
    public void migrateMovesValueToNewCategory() {
        config.get("Icons", "Squad Warning Icon", "default").set("custom");
        ConfigMigrationHelper.migrateConfig(config, "Icons", "Squad", "Squad Warning Icon");
        assertFalse(ConfigMigrationHelper.hasKey(config, "Icons", "Squad Warning Icon"));
        assertEquals("custom", getString(config, "Squad", "Squad Warning Icon"));
    }

    @Test
    public void migrateDoesNothingWhenOldKeyIsMissing() {
        ConfigMigrationHelper.migrateConfig(config, "Icons", "Squad", "Squad Warning Icon");
        assertFalse(ConfigMigrationHelper.hasKey(config, "Squad", "Squad Warning Icon"));
    }

    @Test
    public void migrateDoesNotOverwriteExistingNewKey() {
        config.get("Icons", "Squad Warning Icon", "default").set("old");
        config.get("Squad", "Squad Warning Icon", "default").set("new");
        ConfigMigrationHelper.migrateConfig(config, "Icons", "Squad", "Squad Warning Icon");
        assertEquals("old", getString(config, "Icons", "Squad Warning Icon"));
        assertEquals("new", getString(config, "Squad", "Squad Warning Icon"));
    }

    @Test
    public void migrateWithRenameMovesValueToNewKey() {
        config.get("Icons", "Red Warning Icon", "default").set("custom");
        ConfigMigrationHelper.migrateConfig(config, "Icons", "Red Warning Icon", "NoCheaters", "Blatant Warning Icon");
        assertFalse(ConfigMigrationHelper.hasKey(config, "Icons", "Red Warning Icon"));
        assertFalse(ConfigMigrationHelper.hasKey(config, "NoCheaters", "Red Warning Icon"));
        assertEquals("custom", getString(config, "NoCheaters", "Blatant Warning Icon"));
        assertEquals("Blatant Warning Icon", config.getCategory("nocheaters").get("Blatant Warning Icon").getName());
    }

    @Test
    public void renameWithinSameCategory() {
        config.get("NoCheaters", "Yellow Warning Icon", "default").set("custom");
        ConfigMigrationHelper.migrateConfig(config, "NoCheaters", "Yellow Warning Icon", "NoCheaters", "Normal Warning Icon");
        assertFalse(ConfigMigrationHelper.hasKey(config, "NoCheaters", "Yellow Warning Icon"));
        assertEquals("custom", getString(config, "NoCheaters", "Normal Warning Icon"));
    }

    @Test
    public void migrateWithRenameDoesNotOverwriteExistingNewKey() {
        config.get("Icons", "Red Warning Icon", "default").set("old");
        config.get("NoCheaters", "Blatant Warning Icon", "default").set("new");
        ConfigMigrationHelper.migrateConfig(config, "Icons", "Red Warning Icon", "NoCheaters", "Blatant Warning Icon");
        assertEquals("old", getString(config, "Icons", "Red Warning Icon"));
        assertEquals("new", getString(config, "NoCheaters", "Blatant Warning Icon"));
    }

    @Test
    public void migrateIsIdempotent() {
        config.get("Icons", "Red Warning Icon", "default").set("custom");
        ConfigMigrationHelper.migrateConfig(config, "Icons", "Red Warning Icon", "NoCheaters", "Blatant Warning Icon");
        ConfigMigrationHelper.migrateConfig(config, "Icons", "Red Warning Icon", "NoCheaters", "Blatant Warning Icon");
        assertFalse(ConfigMigrationHelper.hasKey(config, "Icons", "Red Warning Icon"));
        assertEquals("custom", getString(config, "NoCheaters", "Blatant Warning Icon"));
    }

    @Test
    public void migratedValuesSurviveSaveAndReload() {
        config.get("Icons", "Red Warning Icon", "default").set("blatant");
        config.get("Icons", "Squad Warning Icon", "default").set("squad");
        ConfigMigrationHelper.migrateConfig(config, "Icons", "Red Warning Icon", "NoCheaters", "Blatant Warning Icon");
        ConfigMigrationHelper.migrateConfig(config, "Icons", "Squad", "Squad Warning Icon");
        config.save();

        final Configuration reloaded = new Configuration(configFile);
        assertFalse(ConfigMigrationHelper.hasKey(reloaded, "Icons", "Red Warning Icon"));
        assertFalse(ConfigMigrationHelper.hasKey(reloaded, "Icons", "Squad Warning Icon"));
        assertFalse(ConfigMigrationHelper.hasKey(reloaded, "NoCheaters", "Red Warning Icon"));
        assertEquals("blatant", getString(reloaded, "NoCheaters", "Blatant Warning Icon"));
        assertEquals("squad", getString(reloaded, "Squad", "Squad Warning Icon"));
    }

    private static String getString(Configuration config, String category, String key) {
        return config.getCategory(category.toLowerCase(Locale.ENGLISH)).get(key).getString();
    }

}
