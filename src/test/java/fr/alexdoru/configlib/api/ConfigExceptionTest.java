package fr.alexdoru.configlib.api;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ConfigExceptionTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private IConfigHandler configHandler;

    @Before
    public void setup() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method build = FMLInjectionData.class.getDeclaredMethod("build", File.class, LaunchClassLoader.class);
        build.setAccessible(true);
        build.invoke(null, tempFolder.getRoot(), null);
        final File configFile = tempFolder.newFile("test.cfg");
        this.configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
    }

    private static class TestConfig1 {

        @ConfigPropertyEvent(name = "bla")
        private static void on() {}

    }

    @Test
    public void unusedConfigPropertyEventThrows() {
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                this.configHandler.registerConfig(TestConfig1.class)
        );
        assertEquals("Some config events use config names that were not found : [bla]", exception.getMessage());
    }

    private static class TestConfig2 {

        @ConfigPropertyHideOverride(name = "bla")
        private static boolean on() {return false;}

    }

    @Test
    public void unusedHideOverrideThrows() {
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                this.configHandler.registerConfig(TestConfig2.class)
        );
        assertEquals("Some config hide overrides use config names that were not found : [bla]", exception.getMessage());
    }

    private static class TestConfig3 {

        @ConfigProperty(category = "foo", name = "bla")
        public static boolean testBool1;

        @ConfigProperty(category = "bar", name = "bla")
        public static boolean testBool2;

    }

    @Test
    public void sameConfigNameWorks() {
        this.configHandler.registerConfig(TestConfig3.class);
    }

    private static class TestConfig4 {

        @ConfigProperty(category = "foo", name = "bla")
        public static boolean testBool1;

        @ConfigProperty(category = "foo", name = "bla")
        public static boolean testBool2;

    }

    @Test
    public void collidingConfigNameThrows() {
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                this.configHandler.registerConfig(TestConfig4.class)
        );
        assertEquals("Config properties with duplicate key names : foo bla", exception.getMessage());
    }

    private static class TestConfig5 {

        @ConfigProperty(category = "foo", name = "bla")
        public static boolean testBool1;

        @ConfigProperty(category = "bar", name = "bla")
        public static boolean testBool2;

        @ConfigPropertyEvent(name = "foo$bla")
        private static void event() {}

    }

    @Test
    public void fullyQualifiedConfigEventNameWorks() {
        this.configHandler.registerConfig(TestConfig5.class);
    }

    private static class TestConfig6 {

        @ConfigProperty(category = "foo", name = "bla")
        public static boolean testBool1;

        @ConfigProperty(category = "bar", name = "bla")
        public static boolean testBool2;

        @ConfigPropertyEvent(name = "bla")
        private static void event() {}

    }

    @Test
    public void ambiguousConfigEventNameThrows() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                this.configHandler.registerConfig(TestConfig6.class)
        );
        assertEquals("Ambiguous config name used for event, could be bar$bla or foo$bla", exception.getMessage());
    }

    private static class TestConfig7 {

        @ConfigProperty(category = "foo", name = "bla")
        public static boolean testBool1;

        @ConfigProperty(category = "bar", name = "bla")
        public static boolean testBool2;

        @ConfigPropertyHideOverride(name = "foo$bla")
        private static boolean event() {return false;}

    }

    @Test
    public void fullyQualifiedConfigHideNameWorks() {
        this.configHandler.registerConfig(TestConfig7.class);
    }

    private static class TestConfig8 {

        @ConfigProperty(category = "foo", name = "bla")
        public static boolean testBool1;

        @ConfigProperty(category = "bar", name = "bla")
        public static boolean testBool2;

        @ConfigPropertyHideOverride(name = "bla")
        private static boolean event() {return false;}

    }

    @Test
    public void ambiguousConfigHideNameThrows() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                this.configHandler.registerConfig(TestConfig8.class)
        );
        assertEquals("Ambiguous config name used for hide override, could be bar$bla or foo$bla", exception.getMessage());
    }

}
