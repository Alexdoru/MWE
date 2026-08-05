package fr.alexdoru.configlib.api;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ConfigSaveLoadTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private final AtomicInteger counter = new AtomicInteger();
    private File configFile;

    @Before
    public void setup() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method build = FMLInjectionData.class.getDeclaredMethod("build", File.class, LaunchClassLoader.class);
        build.setAccessible(true);
        build.invoke(null, tempFolder.getRoot(), null);
        this.configFile = tempFolder.newFile("test" + counter.getAndIncrement() + ".cfg");
    }

    private static class RendererWrite {

        @ConfigProperty(category = "foo", name = "bar")
        public static final RendererPosition configFieldWrite = new RendererPosition(false, 0d, 0d);

    }

    private static class RendererRead {

        @ConfigProperty(category = "foo", name = "bar")
        public static final RendererPosition configFieldRead = new RendererPosition(false, 0d, 0d);

    }

    @Test
    public void rendererTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(RendererWrite.class);
        assertFalse(RendererWrite.configFieldWrite.isEnabled());
        assertEquals(0d, RendererWrite.configFieldWrite.getRelativeX(), 0d);
        assertEquals(0d, RendererWrite.configFieldWrite.getRelativeY(), 0d);
        final double savedX = 0.5d;
        final double savedY = 0.75d;
        RendererWrite.configFieldWrite.setEnabled(true);
        RendererWrite.configFieldWrite.setRelativePosition(savedX, savedY);
        configHandler.saveConfig();
        assertTrue(RendererWrite.configFieldWrite.isEnabled());
        assertEquals(savedX, RendererWrite.configFieldWrite.getRelativeX(), 0d);
        assertEquals(savedY, RendererWrite.configFieldWrite.getRelativeY(), 0d);

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertFalse(RendererRead.configFieldRead.isEnabled());
        assertEquals(0d, RendererRead.configFieldRead.getRelativeX(), 0d);
        assertEquals(0d, RendererRead.configFieldRead.getRelativeY(), 0d);
        configHandler.registerConfig(RendererRead.class);
        assertTrue(RendererRead.configFieldRead.isEnabled());
        assertEquals(savedX, RendererRead.configFieldRead.getRelativeX(), 0d);
        assertEquals(savedY, RendererRead.configFieldRead.getRelativeY(), 0d);
    }

    private static class StringWrite {

        @ConfigProperty(category = "foo", name = "bar")
        public static String configFieldWrite = "Hello world";

    }

    private static class StringRead {

        @ConfigProperty(category = "foo", name = "bar")
        public static String configFieldRead = "";

    }

    @Test
    public void stringTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(StringWrite.class);
        assertEquals("Hello world", StringWrite.configFieldWrite);
        final String savedValue = "Saved Value";
        StringWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();
        assertEquals(savedValue, StringWrite.configFieldWrite);

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertEquals("", StringRead.configFieldRead);
        configHandler.registerConfig(StringRead.class);
        assertEquals(savedValue, StringRead.configFieldRead);
    }

    private static class BoolWrite {

        @ConfigProperty(category = "foo", name = "bar")
        public static boolean configFieldWrite;

    }

    private static class BoolRead {

        @ConfigProperty(category = "foo", name = "bar")
        public static boolean configFieldRead;

    }

    @Test
    public void booleanTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(BoolWrite.class);
        assertFalse(BoolWrite.configFieldWrite);
        BoolWrite.configFieldWrite = true;
        configHandler.saveConfig();
        assertTrue(BoolWrite.configFieldWrite);

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertFalse(BoolRead.configFieldRead);
        configHandler.registerConfig(BoolRead.class);
        assertTrue(BoolRead.configFieldRead);
    }

    private static class DoubleWrite {

        @ConfigProperty(category = "foo", name = "bar")
        public static double configFieldWrite = 1.0d;

    }

    private static class DoubleRead {

        @ConfigProperty(category = "foo", name = "bar")
        public static double configFieldRead = 2.0d;

    }

    @Test
    public void doubleTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(DoubleWrite.class);
        assertEquals(1.0d, DoubleWrite.configFieldWrite, 0d);
        final double savedValue = 25d;
        DoubleWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();
        assertEquals(savedValue, DoubleWrite.configFieldWrite, 0d);

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertEquals(2.0d, DoubleRead.configFieldRead, 0d);
        configHandler.registerConfig(DoubleRead.class);
        assertEquals(savedValue, DoubleRead.configFieldRead, 0d);
    }

    private static class IntWrite {

        @ConfigProperty(category = "foo", name = "bar")
        public static int configFieldWrite = 1;

    }

    private static class IntRead {

        @ConfigProperty(category = "foo", name = "bar")
        public static int configFieldRead = 2;

    }

    @Test
    public void intTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(IntWrite.class);
        assertEquals(1, IntWrite.configFieldWrite);
        final int savedValue = 25;
        IntWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();
        assertEquals(savedValue, IntWrite.configFieldWrite);

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertEquals(2, IntRead.configFieldRead);
        configHandler.registerConfig(IntRead.class);
        assertEquals(savedValue, IntRead.configFieldRead);
    }

    private static class EnumColorWrite {

        @ConfigProperty(category = "foo", name = "bar")
        public static EnumChatFormatting configFieldWrite = EnumChatFormatting.GREEN;

    }

    private static class EnumColorRead {

        @ConfigProperty(category = "foo", name = "bar")
        public static EnumChatFormatting configFieldRead = EnumChatFormatting.RED;

    }

    @Test
    public void enumChatColorTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(EnumColorWrite.class);
        assertEquals(EnumChatFormatting.GREEN, EnumColorWrite.configFieldWrite);
        final EnumChatFormatting savedValue = EnumChatFormatting.DARK_PURPLE;
        EnumColorWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();
        assertEquals(savedValue, EnumColorWrite.configFieldWrite);

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertEquals(EnumChatFormatting.RED, EnumColorRead.configFieldRead);
        configHandler.registerConfig(EnumColorRead.class);
        assertEquals(savedValue, EnumColorRead.configFieldRead);
    }

    private enum TestEnum {
        FIRST, SECOND, THIRD
    }

    private static class EnumWrite {

        @ConfigProperty(category = "foo", name = "bar")
        public static TestEnum configFieldWrite = TestEnum.FIRST;

    }

    private static class EnumRead {

        @ConfigProperty(category = "foo", name = "bar")
        public static TestEnum configFieldRead = TestEnum.SECOND;

    }

    @Test
    public void enumTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(EnumWrite.class);
        assertEquals(TestEnum.FIRST, EnumWrite.configFieldWrite);
        final TestEnum savedValue = TestEnum.THIRD;
        EnumWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();
        assertEquals(savedValue, EnumWrite.configFieldWrite);

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertEquals(TestEnum.SECOND, EnumRead.configFieldRead);
        configHandler.registerConfig(EnumRead.class);
        assertEquals(savedValue, EnumRead.configFieldRead);
    }

    private static class StringListWrite {

        @ConfigProperty(category = "foo", name = "bar")
        public static final List<String> configFieldWrite = new ArrayList<>(Arrays.asList("one", "two"));

    }

    private static class StringListRead {

        @ConfigProperty(category = "foo", name = "bar")
        public static final List<String> configFieldRead = new ArrayList<>(Collections.singletonList("apple"));

    }

    @Test
    public void stringListTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(StringListWrite.class);
        assertEquals(2, StringListWrite.configFieldWrite.size());
        assertEquals("one", StringListWrite.configFieldWrite.get(0));
        assertEquals("two", StringListWrite.configFieldWrite.get(1));
        StringListWrite.configFieldWrite.clear();
        StringListWrite.configFieldWrite.add("three");
        StringListWrite.configFieldWrite.add("four");
        StringListWrite.configFieldWrite.add("five");
        configHandler.saveConfig();
        assertEquals(3, StringListWrite.configFieldWrite.size());
        assertEquals("three", StringListWrite.configFieldWrite.get(0));
        assertEquals("four", StringListWrite.configFieldWrite.get(1));
        assertEquals("five", StringListWrite.configFieldWrite.get(2));

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertEquals(1, StringListRead.configFieldRead.size());
        assertEquals("apple", StringListRead.configFieldRead.get(0));
        configHandler.registerConfig(StringListRead.class);
        assertEquals(3, StringListRead.configFieldRead.size());
        assertEquals("three", StringListRead.configFieldRead.get(0));
        assertEquals("four", StringListRead.configFieldRead.get(1));
        assertEquals("five", StringListRead.configFieldRead.get(2));
    }

}
