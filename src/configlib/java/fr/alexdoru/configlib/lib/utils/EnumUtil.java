package fr.alexdoru.configlib.lib.utils;

public final class EnumUtil {

    private EnumUtil() {}

    public static String formatEnumName(Enum<?> e) {
        final char[] chars = e.name().replace('_', ' ').toLowerCase().toCharArray();
        boolean wordStart = true;
        for (int i = 0; i < chars.length; i++) {
            if (wordStart) {
                chars[i] = Character.toUpperCase(chars[i]);
            }
            wordStart = chars[i] == ' ';
        }
        return new String(chars);
    }

}
