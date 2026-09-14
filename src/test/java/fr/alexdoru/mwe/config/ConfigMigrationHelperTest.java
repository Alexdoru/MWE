package fr.alexdoru.mwe.config;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigMigrationHelperTest {

    @Test
    public void comparableVersiontest() {
        assertTrue(ConfigMigrationHelper.isVersionLowerThan("4.0", "4.5"));
        assertFalse(ConfigMigrationHelper.isVersionLowerThan("4.0", "4.0"));
        assertFalse(ConfigMigrationHelper.isVersionLowerThan("4.0", "3.0"));
    }

}
