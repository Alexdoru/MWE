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
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ConfigEventTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private File configFile;

    @Before
    public void setup() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method build = FMLInjectionData.class.getDeclaredMethod("build", File.class, LaunchClassLoader.class);
        build.setAccessible(true);
        build.invoke(null, tempFolder.getRoot(), null);
        this.configFile = tempFolder.newFile("config.cfg");
    }

    private static class TestConfig1 {

        static AtomicBoolean testBool = new AtomicBoolean(false);

        @ConfigUpdatedEvent
        public static void onUpdate(String savedVersion, String version) {
            testBool.set(true);
            assertEquals("2.0", savedVersion);
            assertEquals("3.0", version);
        }

    }

    @Test
    public void updateEventTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig", "2.0");
        TestConfig1.testBool.set(false);
        configHandler.registerConfig(TestConfig1.class);
        assertFalse(TestConfig1.testBool.get());

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig", "3.0");
        TestConfig1.testBool.set(false);
        configHandler.registerConfig(TestConfig1.class);
        assertTrue(TestConfig1.testBool.get());
    }

    private static class TestConfig2 {

        static AtomicBoolean testBool = new AtomicBoolean(false);

        @ConfigLoadedEvent
        public static void onLoad() {
            testBool.set(true);
        }

    }

    @Test
    public void loadEventTest() {
        final IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        TestConfig2.testBool.set(false);
        configHandler.registerConfig(TestConfig2.class);
        assertTrue(TestConfig2.testBool.get());
    }

}
