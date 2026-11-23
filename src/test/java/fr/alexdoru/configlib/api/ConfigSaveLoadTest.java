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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    private static class RendererV2Write {

        @ConfigProperty(category = "foo", name = "bar")
        public static final RendererPositionV2 configFieldWrite = new RendererPositionV2(false, 0d, 0d, 1d, RendererAlignment.TOP_LEFT);

    }

    private static class RendererV2Read {

        @ConfigProperty(category = "foo", name = "bar")
        public static final RendererPositionV2 configFieldRead = new RendererPositionV2(false, 0d, 0d, 1d, RendererAlignment.TOP_LEFT);

    }

    @Test
    public void rendererV2Test() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(RendererV2Write.class);
        assertFalse(RendererV2Write.configFieldWrite.isEnabled());
        assertEquals(1d, RendererV2Write.configFieldWrite.getScale(), 0d);
        assertEquals(RendererAlignment.TOP_LEFT, RendererV2Write.configFieldWrite.getAlignment());
        final double savedX = 0.5d;
        final double savedY = 0.75d;
        final double savedScale = 1.7d;
        RendererV2Write.configFieldWrite.setEnabled(true);
        RendererV2Write.configFieldWrite.setRelativePosition(savedX, savedY);
        RendererV2Write.configFieldWrite.setScale(savedScale);
        RendererV2Write.configFieldWrite.setAlignment(RendererAlignment.BOTTOM_RIGHT);
        configHandler.saveConfig();

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertFalse(RendererV2Read.configFieldRead.isEnabled());
        assertEquals(1d, RendererV2Read.configFieldRead.getScale(), 0d);
        assertEquals(RendererAlignment.TOP_LEFT, RendererV2Read.configFieldRead.getAlignment());
        configHandler.registerConfig(RendererV2Read.class);
        assertTrue(RendererV2Read.configFieldRead.isEnabled());
        assertEquals(savedX, RendererV2Read.configFieldRead.getRelativeX(), 0d);
        assertEquals(savedY, RendererV2Read.configFieldRead.getRelativeY(), 0d);
        assertEquals(savedScale, RendererV2Read.configFieldRead.getScale(), 0d);
        assertEquals(RendererAlignment.BOTTOM_RIGHT, RendererV2Read.configFieldRead.getAlignment());
    }

    private static class RendererV2InvalidAlignment {

        @ConfigProperty(category = "foo", name = "bar")
        public static final RendererPositionV2 configField = new RendererPositionV2(true, 0d, 0d, 1d, RendererAlignment.CENTER);

    }

    @Test
    public void rendererV2InvalidAlignmentFallsBackToDefault() throws IOException {
        Files.write(configFile.toPath(), Arrays.asList(
                "foo {",
                "    S:\"Align bar\"=NOT_AN_ALIGNMENT",
                "}"
        ), StandardCharsets.UTF_8);
        RendererV2InvalidAlignment.configField.setAlignment(RendererAlignment.TOP_LEFT);
        final IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(RendererV2InvalidAlignment.class);
        assertEquals(RendererAlignment.CENTER, RendererV2InvalidAlignment.configField.getAlignment());
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

    private static class FormattedStringWrite {

        @ConfigProperty(category = "foo", name = "bar", isFormattedString = true)
        public static String configFieldWrite = EnumChatFormatting.RED + "Hello";

    }

    private static class FormattedStringRead {

        @ConfigProperty(category = "foo", name = "bar", isFormattedString = true)
        public static String configFieldRead = "";

    }

    @Test
    public void formattedStringTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(FormattedStringWrite.class);
        assertEquals(EnumChatFormatting.RED + "Hello", FormattedStringWrite.configFieldWrite);
        final String savedValue = EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "Saved";
        FormattedStringWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(FormattedStringRead.class);
        assertEquals(savedValue, FormattedStringRead.configFieldRead);
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

        @ConfigProperty(category = "foo", name = "bar", sliderMax = 10)
        public static double configFieldWrite = 1.0d;

    }

    private static class DoubleRead {

        @ConfigProperty(category = "foo", name = "bar", sliderMax = 10)
        public static double configFieldRead = 2.0d;

    }

    @Test
    public void doubleTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(DoubleWrite.class);
        assertEquals(1.0d, DoubleWrite.configFieldWrite, 0d);
        final double savedValue = 7.5d;
        DoubleWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();
        assertEquals(savedValue, DoubleWrite.configFieldWrite, 0d);

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertEquals(2.0d, DoubleRead.configFieldRead, 0d);
        configHandler.registerConfig(DoubleRead.class);
        assertEquals(savedValue, DoubleRead.configFieldRead, 0d);
    }

    private static class IntWrite {

        @ConfigProperty(category = "foo", name = "bar", sliderMax = 10)
        public static int configFieldWrite = 1;

    }

    private static class IntRead {

        @ConfigProperty(category = "foo", name = "bar", sliderMax = 10)
        public static int configFieldRead = 2;

    }

    @Test
    public void intTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(IntWrite.class);
        assertEquals(1, IntWrite.configFieldWrite);
        final int savedValue = 7;
        IntWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();
        assertEquals(savedValue, IntWrite.configFieldWrite);

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        assertEquals(2, IntRead.configFieldRead);
        configHandler.registerConfig(IntRead.class);
        assertEquals(savedValue, IntRead.configFieldRead);
    }

    private static class SliderClamp {

        @ConfigProperty(category = "foo", name = "intAbove", sliderMin = 2, sliderMax = 10)
        public static int intAbove = 5;

        @ConfigProperty(category = "foo", name = "intBelow", sliderMin = 2, sliderMax = 10)
        public static int intBelow = 5;

        @ConfigProperty(category = "foo", name = "doubleAbove", sliderMin = 2, sliderMax = 10)
        public static double doubleAbove = 5d;

        @ConfigProperty(category = "foo", name = "doubleBelow", sliderMin = 2, sliderMax = 10)
        public static double doubleBelow = 5d;

        @ConfigProperty(category = "foo", name = "hiddenInt", hidden = true)
        public static int hiddenInt = 5;

        @ConfigProperty(category = "foo", name = "hiddenDouble", hidden = true)
        public static double hiddenDouble = 5d;

        @ConfigProperty(category = "foo", name = "color", isColor = true)
        public static int color = 0xFFFFFF;

    }

    @Test
    public void sliderValuesAreClampedOnLoad() throws IOException {
        Files.write(configFile.toPath(), Arrays.asList(
                "foo {",
                "    I:intAbove=25",
                "    I:intBelow=-3",
                "    D:doubleAbove=25.0",
                "    D:doubleBelow=-3.5",
                "    I:hiddenInt=25",
                "    D:hiddenDouble=-3.5",
                "    I:color=16711680",
                "}"
        ), StandardCharsets.UTF_8);
        final IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(SliderClamp.class);
        assertEquals(10, SliderClamp.intAbove);
        assertEquals(2, SliderClamp.intBelow);
        assertEquals(10d, SliderClamp.doubleAbove, 0d);
        assertEquals(2d, SliderClamp.doubleBelow, 0d);
        assertEquals(25, SliderClamp.hiddenInt);
        assertEquals(-3.5d, SliderClamp.hiddenDouble, 0d);
        assertEquals(0xFF0000, SliderClamp.color);
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

    private static class IntArrayWrite {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static int[] configFieldWrite = {1, 2};

    }

    private static class IntArrayRead {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static int[] configFieldRead = {9};

    }

    @Test
    public void intArrayTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(IntArrayWrite.class);
        assertArrayEquals(new int[]{1, 2}, IntArrayWrite.configFieldWrite);
        final int[] savedValue = {3, -4, 5};
        IntArrayWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(IntArrayRead.class);
        assertArrayEquals(savedValue, IntArrayRead.configFieldRead);
    }

    private static class DoubleArrayWrite {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static double[] configFieldWrite = {1.5d, 2d};

    }

    private static class DoubleArrayRead {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static double[] configFieldRead = {9d};

    }

    @Test
    public void doubleArrayTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(DoubleArrayWrite.class);
        assertArrayEquals(new double[]{1.5d, 2d}, DoubleArrayWrite.configFieldWrite, 0d);
        final double[] savedValue = {3.25d, -4d, 5.5d};
        DoubleArrayWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(DoubleArrayRead.class);
        assertArrayEquals(savedValue, DoubleArrayRead.configFieldRead, 0d);
    }

    private static class BooleanArrayWrite {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static boolean[] configFieldWrite = {true, false};

    }

    private static class BooleanArrayRead {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static boolean[] configFieldRead = {false};

    }

    @Test
    public void booleanArrayTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(BooleanArrayWrite.class);
        assertArrayEquals(new boolean[]{true, false}, BooleanArrayWrite.configFieldWrite);
        final boolean[] savedValue = {false, true, true};
        BooleanArrayWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(BooleanArrayRead.class);
        assertArrayEquals(savedValue, BooleanArrayRead.configFieldRead);
    }

    private static class StringArrayWrite {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static String[] configFieldWrite = {"one", "two"};

    }

    private static class StringArrayRead {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static String[] configFieldRead = {"apple"};

    }

    @Test
    public void stringArrayTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(StringArrayWrite.class);
        assertArrayEquals(new String[]{"one", "two"}, StringArrayWrite.configFieldWrite);
        final String[] savedValue = {"three", "four", "five"};
        StringArrayWrite.configFieldWrite = savedValue;
        configHandler.saveConfig();

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(StringArrayRead.class);
        assertArrayEquals(savedValue, StringArrayRead.configFieldRead);
    }

    private static class IntListWrite {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static final List<Integer> configFieldWrite = new ArrayList<>(Arrays.asList(1, 2));

    }

    private static class IntListRead {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static final List<Integer> configFieldRead = new ArrayList<>(Collections.singletonList(9));

    }

    @Test
    public void intListTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(IntListWrite.class);
        assertEquals(Arrays.asList(1, 2), IntListWrite.configFieldWrite);
        final List<Integer> savedValue = Arrays.asList(3, -4, 5);
        IntListWrite.configFieldWrite.clear();
        IntListWrite.configFieldWrite.addAll(savedValue);
        configHandler.saveConfig();

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(IntListRead.class);
        assertEquals(savedValue, IntListRead.configFieldRead);
    }

    private static class DoubleListWrite {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static final List<Double> configFieldWrite = new ArrayList<>(Arrays.asList(1.5d, 2d));

    }

    private static class DoubleListRead {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static final List<Double> configFieldRead = new ArrayList<>(Collections.singletonList(9d));

    }

    @Test
    public void doubleListTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(DoubleListWrite.class);
        assertEquals(Arrays.asList(1.5d, 2d), DoubleListWrite.configFieldWrite);
        final List<Double> savedValue = Arrays.asList(3.25d, -4d, 5.5d);
        DoubleListWrite.configFieldWrite.clear();
        DoubleListWrite.configFieldWrite.addAll(savedValue);
        configHandler.saveConfig();

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(DoubleListRead.class);
        assertEquals(savedValue, DoubleListRead.configFieldRead);
    }

    private static class BooleanListWrite {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static final List<Boolean> configFieldWrite = new ArrayList<>(Arrays.asList(true, false));

    }

    private static class BooleanListRead {

        @ConfigProperty(category = "foo", name = "bar", hidden = true)
        public static final List<Boolean> configFieldRead = new ArrayList<>(Collections.singletonList(false));

    }

    @Test
    public void booleanListTest() {
        IConfigHandler configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(BooleanListWrite.class);
        assertEquals(Arrays.asList(true, false), BooleanListWrite.configFieldWrite);
        final List<Boolean> savedValue = Arrays.asList(false, true, true);
        BooleanListWrite.configFieldWrite.clear();
        BooleanListWrite.configFieldWrite.addAll(savedValue);
        configHandler.saveConfig();

        configHandler = ConfigLib.newConfigHandler(configFile, "TestConfig");
        configHandler.registerConfig(BooleanListRead.class);
        assertEquals(savedValue, BooleanListRead.configFieldRead);
    }

}
