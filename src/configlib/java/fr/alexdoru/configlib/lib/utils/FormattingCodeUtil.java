package fr.alexdoru.configlib.lib.utils;

import net.minecraft.util.EnumChatFormatting;

public final class FormattingCodeUtil {

    private static final char FORMAT = '§';
    private static final char AMPERSAND = '&';
    private static final String CODES;

    static {
        final StringBuilder sb = new StringBuilder();
        for (final EnumChatFormatting formatting : EnumChatFormatting.values()) {
            sb.append(formatting.toString().charAt(1));
        }
        CODES = sb.toString();
    }

    private FormattingCodeUtil() {}

    public static String toAmpersandText(String s) {
        return s.replace(FORMAT, AMPERSAND);
    }

    public static String toFormattedText(String s) {
        final char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == AMPERSAND) {
                final char code = Character.toLowerCase(chars[i + 1]);
                if (CODES.indexOf(code) != -1) {
                    chars[i] = FORMAT;
                    chars[i + 1] = code;
                    i++;
                }
            }
        }
        return new String(chars);
    }

    public static char getCode(EnumChatFormatting formatting) {
        return formatting.toString().charAt(1);
    }

}
