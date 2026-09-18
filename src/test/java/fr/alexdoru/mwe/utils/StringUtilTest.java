package fr.alexdoru.mwe.utils;

import net.minecraft.util.EnumChatFormatting;
import org.junit.Test;

import static org.junit.Assert.*;

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
        assertEquals(0, StringUtil.getLastFormattingCharOf(""));
        assertEquals(0, StringUtil.getLastFormattingCharOf("a"));
        assertEquals(0, StringUtil.getLastFormattingCharOf("plain text, no codes"));

        // Section sign at the very end (no char after it)
        assertEquals(0, StringUtil.getLastFormattingCharOf("hello§"));

        // Section sign followed by an invalid char
        assertEquals(0, StringUtil.getLastFormattingCharOf("§z"));
        assertEquals(0, StringUtil.getLastFormattingCharOf("§§")); // '§' isn't a valid code itself

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

        // All invalid codes return 0
        final char[] invalidChars = {'g', 'h', 'z', '!', ' ', 'p'};
        for (final char c : invalidChars) {
            assertEquals(0, StringUtil.getLastFormattingCharOf("§" + c));
        }
    }

    @Test
    public void testGetLastColorCharOf() {
        // Empty / no section sign
        assertEquals(0, StringUtil.getLastColorCharOf(""));
        assertEquals(0, StringUtil.getLastColorCharOf("plain text, no codes"));

        // Section sign at the very end (no char after it)
        assertEquals(0, StringUtil.getLastColorCharOf("hello§"));

        // Format codes (k/l/m/n/o/r) are not color codes
        assertEquals(0, StringUtil.getLastColorCharOf("§k"));
        assertEquals(0, StringUtil.getLastColorCharOf("§R"));

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

        // Invalid or format-only codes return 0
        final char[] invalidChars = {'k', 'l', 'm', 'n', 'o', 'r', 'K', 'L', 'M', 'N', 'O', 'R', 'g', 'z', '!'};
        for (final char c : invalidChars) {
            assertEquals(0, StringUtil.getLastColorCharOf("§" + c));
        }
    }

    @Test
    public void getLastColorCharBeforeTest() {
        assertEquals('b', StringUtil.getLastColorCharBefore("bonjour §b§alex", "alex"));
        assertEquals('a', StringUtil.getLastColorCharBefore("§ahello world", "world"));
        assertEquals('b', StringUtil.getLastColorCharBefore("§ared §bblue world", "world"));
        assertEquals(0, StringUtil.getLastColorCharBefore("§ahello world", "xyz"));
        assertEquals(0, StringUtil.getLastColorCharBefore("hello world", "world"));
        // the § code is after "world", so it must not count
        assertEquals(0, StringUtil.getLastColorCharBefore("hello world §a", "world"));
        // 'k' is a special/format code, not a color code, so it should be skipped
        assertEquals(0, StringUtil.getLastColorCharBefore("§khello world", "world"));
        assertEquals('a', StringUtil.getLastColorCharBefore("§Ahello world", "world"));
        // § with nothing after it (or not enough chars before target) shouldn't crash or match
        assertEquals(0, StringUtil.getLastColorCharBefore("hello§", "world"));
        assertEquals(0, StringUtil.getLastColorCharBefore("world hello", "world"));
    }

    @Test
    public void getLastFormattingCharBeforeTest() {
        assertEquals('b', StringUtil.getLastColorCharBefore("bonjour §b§alex", "alex"));
        assertEquals('a', StringUtil.getLastFormattingCharBefore("§ahello world", "world"));
        // 'k' is a valid special formatting code, unlike in the color-only method
        assertEquals('k', StringUtil.getLastFormattingCharBefore("§khello world", "world"));
        assertEquals('l', StringUtil.getLastFormattingCharBefore("§ared §lbold world", "world"));
        assertEquals(0, StringUtil.getLastFormattingCharBefore("§ahello world", "xyz"));
        assertEquals(0, StringUtil.getLastFormattingCharBefore("hello world", "world"));
        assertEquals(0, StringUtil.getLastFormattingCharBefore("hello world §k", "world"));
        assertEquals('k', StringUtil.getLastFormattingCharBefore("§Khello world", "world"));
        // 'z' isn't a valid color or special code, so it should be skipped
        assertEquals(0, StringUtil.getLastFormattingCharBefore("§zhello world", "world"));
        assertEquals(0, StringUtil.getLastFormattingCharBefore("hello§", "world"));
    }

    @Test
    public void uppercaseFirstTest() {
        assertNull(StringUtil.uppercaseFirstLetter(null));
        assertEquals("", StringUtil.uppercaseFirstLetter(""));
        assertEquals("A", StringUtil.uppercaseFirstLetter("a"));
        assertEquals("Hello", StringUtil.uppercaseFirstLetter("hello"));
        assertEquals("Hello", StringUtil.uppercaseFirstLetter("Hello"));
        assertEquals("1abc", StringUtil.uppercaseFirstLetter("1abc"));
        assertEquals("HeLLO", StringUtil.uppercaseFirstLetter("heLLO"));
        assertEquals("Étoile", StringUtil.uppercaseFirstLetter("étoile"));
        assertEquals(" hello", StringUtil.uppercaseFirstLetter(" hello"));
    }

    @Test
    public void removeCharTest() {
        final String[] strings = {
                "",
                "aab",
                "a.b.c.b",
                "ad$fca",
                "a d fc a",
                "a -avzaa",
                "anc4\nac"
        };
        for (final String s : strings) {
            assertEquals(s.replace("a", ""), StringUtil.remove(s, 'a'));
            assertEquals(s.replace("-", ""), StringUtil.remove(s, '-'));
            assertEquals(s.replace(" ", ""), StringUtil.remove(s, ' '));
            assertEquals(s.replace("8", ""), StringUtil.remove(s, '8'));
            assertEquals(s.replace(".", ""), StringUtil.remove(s, '.'));
            assertEquals(s.replace("$", ""), StringUtil.remove(s, '$'));
            assertEquals(s.replace("\n", ""), StringUtil.remove(s, '\n'));
        }
    }

    @Test
    public void testRemoveString() {

        final String[] strings = {
                "",
                "",
                "abcabc",
                "aabaa",
                "aaa",
                "hello",
                "hello",
                "hello",
                "ab",
        };

        final String[] toRemove = {
                "",
                "a",
                "bc",
                "a",
                "aa",
                "hello",
                "",
                "xyz",
                "abc"
        };

        for (int i = 0; i < strings.length; i++) {
            final String s = strings[i];
            final String rm = toRemove[i];
            assertEquals(
                    s.replace(rm, ""),
                    StringUtil.remove(s, rm)
            );
        }

        // regex metacharacters are treated literally
        assertEquals("ab", StringUtil.remove("a.b", "."));
        assertEquals("ab", StringUtil.remove("a$b", "$"));
        assertEquals("", StringUtil.remove("(.*)", "(.*)"));

    }

    @Test
    public void testReplace() {
        // basic replacement
        assertEquals("hell0 w0rld", StringUtil.replace("hello world", "o", "0"));
        assertEquals("a--b--c", StringUtil.replace("aXbXc", "X", "--"));

        // replacement longer / shorter / empty
        assertEquals("abb", StringUtil.replace("ab", "b", "bb"));
        assertEquals("ab", StringUtil.replace("xxabxx", "xx", ""));

        // first / last position
        assertEquals("bc", StringUtil.replace("abc", "a", ""));
        assertEquals("ab", StringUtil.replace("abc", "c", ""));

        // non-overlapping, left to right
        assertEquals("bb", StringUtil.replace("aaaa", "aa", "b"));
        assertEquals("ba", StringUtil.replace("aaa", "aa", "b"));

        // replacement text is not re-scanned
        assertEquals("abcabc", StringUtil.replace("abc", "abc", "abcabc"));

        // no match: same instance returned
        String unchanged = "abc";
        assertSame(unchanged, StringUtil.replace(unchanged, "d", "x"));

        // empty target
        assertEquals("", StringUtil.replace("", "a", "b"));

        // empty search inserts the replacement everywhere, like String.replace
        assertEquals("-a-b-c-", StringUtil.replace("abc", "", "-"));
        assertEquals("x", StringUtil.replace("", "", "x"));

        // regex metacharacters in search AND replacement are literal
        assertEquals("a$1\\b$1\\c", StringUtil.replace("a.b.c", ".", "$1\\"));
        assertEquals("aaXbb", StringUtil.replace("aa(.*)bb", "(.*)", "X"));

        // agrees with the JDK implementation on a mix of inputs
        final String[][] strings = {
                {"hello world", "o", "0"},
                {"aaaa", "aa", "b"},
                {"abc", "", "-"},
                {"abcabcabc", "bc", ""},
                {"mississippi", "ss", "SS"},
                {"mississippi", "i", "ii"},
                {"", "", ""},
        };
        for (final String[] s : strings) {
            assertEquals(
                    s[0].replace(s[1], s[2]),
                    StringUtil.replace(s[0], s[1], s[2])
            );
        }

    }

    @Test
    public void testRemoveFirst() {

        final String[] strings = {
                "",
                "",
                "abcabc",
                "aabaa",
                "aaa",
                "hello",
                "hello",
                "hello",
                "ab",
        };

        final String[] toRemove = {
                "",
                "a",
                "bc",
                "a",
                "aa",
                "hello",
                "",
                "xyz",
                "abc"
        };

        for (int i = 0; i < strings.length; i++) {
            final String s = strings[i];
            final String rm = toRemove[i];
            assertEquals(
                    s.replaceFirst(rm, ""),
                    StringUtil.removeFirst(s, rm)
            );
        }

        // regex metacharacters are treated literally
        assertEquals("ab", StringUtil.removeFirst("a.b", "."));
        assertEquals("ab", StringUtil.removeFirst("a$b", "$"));
        assertEquals("", StringUtil.removeFirst("(.*)", "(.*)"));
    }

    @Test
    public void testReplaceFirst() {
        assertEquals(
                "a-b-c".replaceFirst("-", "+"),
                StringUtil.replaceFirst("a-b-c", "-", "+")
        );
        assertEquals(
                "hello world".replaceFirst("hello", "bye"),
                StringUtil.replaceFirst("hello world", "hello", "bye")
        );
        assertEquals(
                "hello world".replaceFirst("world", "there"),
                StringUtil.replaceFirst("hello world", "world", "there")
        );
        assertEquals(
                "abc".replaceFirst("abc", "xyz"),
                StringUtil.replaceFirst("abc", "abc", "xyz")
        );
        assertEquals(
                "foo-bar-baz-bar".replaceFirst("bar", "BAR"),
                StringUtil.replaceFirst("foo-bar-baz-bar", "bar", "BAR")
        );
        assertEquals(
                "hello".replaceFirst("xyz", "abc"),
                StringUtil.replaceFirst("hello", "xyz", "abc")
        );
        assertEquals(
                "ab".replaceFirst("abc", "x"),
                StringUtil.replaceFirst("ab", "abc", "x")
        );
        assertEquals(
                "Hello".replaceFirst("hello", "x"),
                StringUtil.replaceFirst("Hello", "hello", "x")
        );
        assertEquals(
                "a-b-c".replaceFirst("-", ""),
                StringUtil.replaceFirst("a-b-c", "-", "")
        );
        assertEquals(
                "abc".replaceFirst("", "x"),
                StringUtil.replaceFirst("abc", "", "x")
        );
        assertEquals(
                "".replaceFirst("", "x"),
                StringUtil.replaceFirst("", "", "x")
        );
        assertEquals(
                "".replaceFirst("a", "x"),
                StringUtil.replaceFirst("", "a", "x")
        );
        assertEquals(
                "cost: X".replaceFirst("X", "5.00"),
                StringUtil.replaceFirst("cost: X", "X", "5.00")
        );
        assertEquals(
                "price".replaceFirst("price", "1"),
                StringUtil.replaceFirst("price", "price", "1")
        );
        assertEquals(
                "aaa".replaceFirst("aa", "b"),
                StringUtil.replaceFirst("aaa", "aa", "b")
        );
        assertEquals(
                "abab".replaceFirst("ab", "abab"),
                StringUtil.replaceFirst("abab", "ab", "abab")
        );
    }

}
