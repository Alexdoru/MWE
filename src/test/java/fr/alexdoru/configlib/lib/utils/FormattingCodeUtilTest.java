package fr.alexdoru.configlib.lib.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormattingCodeUtilTest {

    @Test
    public void toSectionFormConvertsValidCodes() {
        assertEquals("§cHello §lWorld§r", FormattingCodeUtil.toFormattedText("&cHello &lWorld&r"));
    }

    @Test
    public void toSectionFormLowercasesUppercaseCodes() {
        assertEquals("§aGreen §kmagic", FormattingCodeUtil.toFormattedText("&AGreen &Kmagic"));
    }

    @Test
    public void toSectionFormKeepsInvalidCodes() {
        assertEquals("&zText & more&", FormattingCodeUtil.toFormattedText("&zText & more&"));
    }

    @Test
    public void toSectionFormHandlesDoubleAmpersand() {
        assertEquals("&§a", FormattingCodeUtil.toFormattedText("&&a"));
    }

    @Test
    public void plainStringsAreUnchanged() {
        assertEquals("Hello World", FormattingCodeUtil.toFormattedText("Hello World"));
        assertEquals("Hello World", FormattingCodeUtil.toAmpersandText("Hello World"));
        assertEquals("", FormattingCodeUtil.toFormattedText(""));
    }

    @Test
    public void roundTrip() {
        final String value = "§6[§lMWE§6] §fHi";
        assertEquals("&6[&lMWE&6] &fHi", FormattingCodeUtil.toAmpersandText(value));
        assertEquals(value, FormattingCodeUtil.toFormattedText(FormattingCodeUtil.toAmpersandText(value)));
    }

}
