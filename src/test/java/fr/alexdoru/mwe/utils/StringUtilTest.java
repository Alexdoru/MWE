package fr.alexdoru.mwe.utils;

import net.minecraft.util.EnumChatFormatting;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilTest {

    @Test
    public void removeFormattingCodesTest() {

        assertNull(StringUtil.removeFormattingCodes(null));
        assertEquals("", StringUtil.removeFormattingCodes(""));
        assertEquals("a", StringUtil.removeFormattingCodes("a"));

        // length 1, hits the "< 2" early return
        assertEquals("§", StringUtil.removeFormattingCodes("§"));

        // --- No formatting codes present ---
        assertEquals("Hello world", StringUtil.removeFormattingCodes("Hello world"));

        // '§' followed by a char not in the valid code set should be left alone
        assertEquals("§zHello", StringUtil.removeFormattingCodes("§zHello"));

        // '§' as the very last character, i + 1 >= len
        assertEquals("Hello§", StringUtil.removeFormattingCodes("Hello§"));

        // --- Basic stripping ---
        assertEquals("Hello", StringUtil.removeFormattingCodes("§aHello"));
        assertEquals("Hello", StringUtil.removeFormattingCodes("Hello§r"));
        assertEquals("HelloWorld", StringUtil.removeFormattingCodes("Hello§bWorld"));
        assertEquals("Hello World", StringUtil.removeFormattingCodes("§a§lHello §r§9World"));
        assertEquals("", StringUtil.removeFormattingCodes("§a§b§c§d"));
        assertEquals("Text", StringUtil.removeFormattingCodes("§k§l§m§nText"));

        // --- Case sensitivity ---
        final String[] array = new String[]{"§0Test", "§9Test", "§aTest", "§fTest", "§ATest", "§FTest", "§kTest", "§KTest", "§oTest", "§OTest", "§rTest",};
        for (final String input : array) {
            assertEquals("Test", StringUtil.removeFormattingCodes(input));
        }

        // 'g', 'h', 'p', 'q', etc. are not valid formatting codes
        assertEquals("§gHello", StringUtil.removeFormattingCodes("§gHello"));
        assertEquals("§!Hello", StringUtil.removeFormattingCodes("§!Hello"));

        String input = "§6[§eServer§6] §fWelcome, §aPlayer123§f!";
        String expected = "[Server] Welcome, Player123!";
        assertEquals(expected, StringUtil.removeFormattingCodes(input));

        input = "§l§nStats§r\n§7Kills: §c10\n§7Deaths: §c2";
        expected = "Stats\nKills: 10\nDeaths: 2";
        assertEquals(expected, StringUtil.removeFormattingCodes(input));

        assertEquals("plain text", StringUtil.removeFormattingCodes("§r§r§rplain text§r§r"));

    }

    @Test
    public void removeFormattingCodesTest_MatchesVanilla() {

        final String[] strings = new String[]{
                "Hello world",
                "§aHello",
                "Hello§r",
                "Hello§bWorld",
                "§a§lHello §r§9World",
                "§a§b§c§d",
                "§k§l§m§nText",
                "§gHello",
                "§!Hello",
                "§éHello",
                " §ewww.hypixel.ne\ud83c\udf82 §et1",
                " §f\ud83c\udf89 2",
                " §aSkeleton\ud83c\udf81 3",
                " Selected Class:\ud83d\udc79 4",
                " §2\ud83c\udfc0 5",
                " additional playe\u26bd rs6",
                " allow time for\ud83c\udf6d 7",
                " Starting in §a04\ud83c\udf20 §a:35§f to8",
                " §1\ud83d\udc7e 9",
                " Players: §a37/10\ud83d\udc0d §a010",
                " Map: §aEbonveil\ud83d\udd2e 11",
                " §0\ud83d\udc7d 12",
                " §707/27/24  §8M2\ud83d\udca3 §82B13",
                "§ewww.hypixel.ne\ud83c\udf82§et1",
                "             \ud83c\udf892",
                "§60 §fClass Poin\ud83c\udf81§fts3",
                "§60 §fCoins\ud83d\udc794",
                "§a0 §fF. Kills \ud83c\udfc0§a0 §fF. Assists5",
                "§a0 §fKills §a0 \u26bd§fAssists6",
                "        \ud83c\udf6d7",
                "§6[Y] §fWither§6\ud83c\udf20§6 HP§7: §61,0008",
                "§c[R] §fWither§c\ud83d\udc7e§c HP§7: §c1,0009",
                "§2[G] §fWither§2\ud83d\udc0d§2 HP§7: §21,00010",
                "§1[B] Wither§1 H\ud83d\udd2e§1P§7: §11,00011",
                "   \ud83d\udc7d12",
                "§fWalls Fall: §a\ud83d\udca3§a06:1313",
                "§707/27/24  §8M2\ud83c\udf6b§82B14"
        };

        for (final String input : strings) {
            assertEquals(EnumChatFormatting.getTextWithoutFormattingCodes(input), StringUtil.removeFormattingCodes(input));

        }

    }

    @Test
    public void testGetLastFormattingCharOf() {
        // Empty / no section sign
        assertEquals('\0', StringUtil.getLastFormattingCharOf(""));
        assertEquals('\0', StringUtil.getLastFormattingCharOf("plain text, no codes"));

        // Section sign at the very end (no char after it)
        assertEquals('\0', StringUtil.getLastFormattingCharOf("hello§"));

        // Section sign followed by an invalid char
        assertEquals('\0', StringUtil.getLastFormattingCharOf("§z"));
        assertEquals('\0', StringUtil.getLastFormattingCharOf("§§")); // '§' isn't a valid code itself

        // Single codes, upper/lowercase
        assertEquals('a', StringUtil.getLastFormattingCharOf("§a"));
        assertEquals('a', StringUtil.getLastFormattingCharOf("§A"));
        assertEquals('k', StringUtil.getLastFormattingCharOf("§k"));
        assertEquals('k', StringUtil.getLastFormattingCharOf("§K"));
        assertEquals('r', StringUtil.getLastFormattingCharOf("§R"));

        // Multiple codes -> returns the last one
        assertEquals('b', StringUtil.getLastFormattingCharOf("§ahello §bworld"));

        // Trailing invalid code -> falls back to the previous valid code
        assertEquals('a', StringUtil.getLastFormattingCharOf("§a§z"));

        // Consecutive section signs -> still finds the valid code
        assertEquals('a', StringUtil.getLastFormattingCharOf("§§a"));

        // Code in the middle of a string
        assertEquals('c', StringUtil.getLastFormattingCharOf("start §c end of string"));

        // All valid codes map correctly
        final String validCodes = "0123456789abcdefklmnorABCDEFKLMNOR";
        for (final char c : validCodes.toCharArray()) {
            final char expected = Character.toLowerCase(c);
            assertEquals(expected, StringUtil.getLastFormattingCharOf("§" + c));
        }

        // All invalid codes return '\0'
        final char[] invalidChars = {'g', 'h', 'z', '!', ' ', 'p'};
        for (final char c : invalidChars) {
            assertEquals('\0', StringUtil.getLastFormattingCharOf("§" + c));
        }
    }

    @Test
    public void testGetLastColorCharOf() {
        // Empty / no section sign
        assertEquals('\0', StringUtil.getLastColorCharOf(""));
        assertEquals('\0', StringUtil.getLastColorCharOf("plain text, no codes"));

        // Section sign at the very end (no char after it)
        assertEquals('\0', StringUtil.getLastColorCharOf("hello§"));

        // Format codes (k/l/m/n/o/r) are not color codes
        assertEquals('\0', StringUtil.getLastColorCharOf("§k"));
        assertEquals('\0', StringUtil.getLastColorCharOf("§R"));

        // Single codes, upper/lowercase
        assertEquals('a', StringUtil.getLastColorCharOf("§a"));
        assertEquals('a', StringUtil.getLastColorCharOf("§A"));
        assertEquals('f', StringUtil.getLastColorCharOf("§F"));
        assertEquals('4', StringUtil.getLastColorCharOf("§4"));

        // Multiple codes -> returns the last color code, skipping format codes
        assertEquals('b', StringUtil.getLastColorCharOf("§khello §bworld"));

        // Trailing format code -> falls back to the previous color code
        assertEquals('a', StringUtil.getLastColorCharOf("§a§k"));

        // Consecutive section signs -> still finds the valid code
        assertEquals('a', StringUtil.getLastColorCharOf("§§a"));

        // All valid color codes map correctly (loop instead of @ParameterizedTest)
        final String validCodes = "0123456789abcdefABCDEF";
        for (final char c : validCodes.toCharArray()) {
            final char expected = Character.toLowerCase(c);
            assertEquals(expected, StringUtil.getLastColorCharOf("§" + c));
        }

        // Invalid or format-only codes return '\0'
        final char[] invalidChars = {'k', 'l', 'm', 'n', 'o', 'r', 'K', 'L', 'M', 'N', 'O', 'R', 'g', 'z', '!'};
        for (final char c : invalidChars) {
            assertEquals('\0', StringUtil.getLastColorCharOf("§" + c));
        }
    }

}
