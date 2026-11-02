package fr.alexdoru.mwe.api.utils;

import java.util.Arrays;

public final class StringUtil {

    private StringUtil() {}

    public static boolean isFormatCharacter(char c) {
        return isFormatColor(c) || isFormatSpecial(c);
    }

    public static boolean isFormatColor(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    public static boolean isFormatSpecial(char c) {
        return c >= 'k' && c <= 'o' || c >= 'K' && c <= 'O' || c == 'r' || c == 'R';
    }

    /**
     * A faster version of {@link net.minecraft.util.EnumChatFormatting#getTextWithoutFormattingCodes(String)}
     */
    public static String removeFormattingCodes(String text) {
        if (text == null || text.length() < 2) return text;
        final int len = text.length();
        final char[] chars = text.toCharArray();
        int count = 0;
        for (int i = 0; i < len; i++) {
            final char c = chars[i];
            if (c == '§' && i + 1 < len && isFormatCharacter(chars[i + 1])) {
                i++;
                continue;
            }
            chars[count++] = c;
        }
        if (count == len) return text;
        return new String(chars, 0, count);
    }

    private static char getLastFormattingCharOf(String text, int maxIndex) {
        for (int i = maxIndex - 1; i >= 0; --i) {
            if (text.charAt(i) == '§' && i + 1 < maxIndex) {
                final char format = text.charAt(i + 1);
                if (isFormatCharacter(format)) {
                    if (format >= 'A' && format <= 'R') {
                        return Character.toLowerCase(format);
                    } else {
                        return format;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Returns the last formatting code of a String
     * <p>
     * Returns 0 if it can't find any formatting code
     * <p>
     * Returns a single character
     */
    public static char getLastFormattingCharOf(String text) {
        return getLastFormattingCharOf(text, text.length());
    }

    /**
     * Returns the last formatting code of a String
     * <p>
     * Returns "" if it can't find any formatting code
     * <p>
     * Returns a single character as a String
     */
    public static String getLastFormattingCodeOf(String text) {
        final char c = getLastFormattingCharOf(text);
        return c == 0 ? "" : String.valueOf(c);
    }

    /**
     * Returns the last formatting char before the first occurence of a certain target in a String
     * <p>
     * Returns 0 if it can't find any formatting code
     */
    public static char getLastFormattingCharBefore(String message, String target) {
        final int index = message.indexOf(target);
        return index == -1 ? 0 : getLastFormattingCharOf(message, index);
    }

    /**
     * Returns the last formatting code before the first occurence of a certain target in a String
     * <p>
     * Returns "" if it can't find any formatting code
     * <p>
     * Returns a single character as a String
     */
    public static String getLastFormattingCodeBefore(String message, String target) {
        final char c = getLastFormattingCharBefore(message, target);
        return c == 0 ? "" : String.valueOf(c);
    }

    private static char getLastColorCharOf(String text, int maxIndex) {
        for (int i = maxIndex - 1; i >= 0; --i) {
            if (text.charAt(i) == '§' && i + 1 < maxIndex) {
                final char format = text.charAt(i + 1);
                if (isFormatColor(format)) {
                    if (format >= 'A' && format <= 'R') {
                        return Character.toLowerCase(format);
                    } else {
                        return format;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Returns the last color code character
     * <p>
     * Returns 0 if it can't find any color code
     * <p>
     * Returns a single character
     */
    public static char getLastColorCharOf(String text) {
        return getLastColorCharOf(text, text.length());
    }

    /**
     * Returns the last color code of a String
     * <p>
     * Returns "" if it can't find any formatting code
     * <p>
     * Returns a single character as a String
     */
    public static String getLastColorCodeOf(String text) {
        final char c = getLastColorCharOf(text);
        return c == 0 ? "" : String.valueOf(c);
    }

    /**
     * Returns the last color char before the first occurence of a certain target in a String
     * <p>
     * Returns 0 if it can't find any color code
     */
    public static char getLastColorCharBefore(String message, String target) {
        final int index = message.indexOf(target);
        return index == -1 ? 0 : getLastColorCharOf(message, index);
    }

    /**
     * Returns the last color code before the first occurence of a certain target in a String
     * <p>
     * Returns "" if it can't find any color code
     * <p>
     * Returns a single character as a String
     */
    public static String getLastColorCodeBefore(String message, String target) {
        final char c = getLastColorCharBefore(message, target);
        return c == 0 ? "" : String.valueOf(c);
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

//    public static String getStringAsUnicode(String s) {
//        final StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < s.length(); i++) {
//            final char c = s.charAt(i);
//            stringBuilder.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
//        }
//        return stringBuilder.toString();
//    }

    public static String uppercaseFirstLetter(String string) {
        if (isNullOrEmpty(string)) {
            return string;
        }
        if (string.length() == 1) {
            return String.valueOf(Character.toUpperCase(string.charAt(0)));
        }
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String getRepetitionOf(char c, int length) {
        final char[] chars = new char[length];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    /**
     * Removes the target char from the String
     */
    public static String remove(String s, char target) {
        final int index = s.indexOf(target);
        if (index == -1) return s;
        final int len = s.length();
        final char[] chars = s.toCharArray();
        int write = index;
        for (int read = index + 1; read < len; read++) {
            if (chars[read] != target) {
                chars[write++] = chars[read];
            }
        }
        return new String(chars, 0, write);
    }

    /**
     * Removes the target substring from the String
     */
    public static String remove(String s, String target) {
        return replace(s, target, "");
    }

    /**
     * Replaces every occurrence of target with replacement from the String
     */
    public static String replace(String s, String target, String replacement) {

        final int len = s.length();
        final int targetLen = target.length();
        final int replLen = replacement.length();

        if (targetLen == 0) {
            final StringBuilder sb = new StringBuilder(len + (len + 1) * replLen);
            sb.append(replacement);
            for (int i = 0; i < len; i++) {
                sb.append(s.charAt(i)).append(replacement);
            }
            return sb.toString();
        }

        int index = s.indexOf(target);
        if (index == -1) {
            return s;
        }

        final int PREDICTED_MATCHES = 2;
        final StringBuilder sb = new StringBuilder(len + Math.max(0, replLen - targetLen) * PREDICTED_MATCHES);
        int start = 0;
        do {
            sb.append(s, start, index).append(replacement);
            start = index + targetLen;
            index = s.indexOf(target, start);
        } while (index >= 0);

        return sb.append(s, start, len).toString();
    }

    public static String removeFirst(String s, String target) {
        return replaceFirst(s, target, "");
    }

    @SuppressWarnings("StringBufferReplaceableByString") // allocated to exact length
    public static String replaceFirst(String s, String target, String replacement) {
        final int index = s.indexOf(target);
        if (index == -1) {
            return s;
        }
        return new StringBuilder(s.length() - target.length() + replacement.length())
                .append(s, 0, index)
                .append(replacement)
                .append(s, index + target.length(), s.length())
                .toString();
    }

}
