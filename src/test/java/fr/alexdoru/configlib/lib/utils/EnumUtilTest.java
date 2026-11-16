package fr.alexdoru.configlib.lib.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnumUtilTest {

    private enum Sample {
        A,
        TOP,
        TOP_LEFT,
        BOTTOM_RIGHT_CORNER,
        alreadyLower
    }

    @Test
    public void formatEnumNameCapitalisesEveryWord() {
        assertEquals("A", EnumUtil.formatEnumName(Sample.A));
        assertEquals("Top", EnumUtil.formatEnumName(Sample.TOP));
        assertEquals("Top Left", EnumUtil.formatEnumName(Sample.TOP_LEFT));
        assertEquals("Bottom Right Corner", EnumUtil.formatEnumName(Sample.BOTTOM_RIGHT_CORNER));
        assertEquals("Alreadylower", EnumUtil.formatEnumName(Sample.alreadyLower));
    }

}
