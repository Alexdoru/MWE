package fr.alexdoru.mwe.utils;

import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class StringUtil {

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
            if (c == '§' && i + 1 < len && "0123456789abcdefklmnorABCDEFKLMNOR".indexOf(chars[i + 1]) != -1) {
                i++;
                continue;
            }
            chars[count++] = c;
        }
        return new String(chars, 0, count);
    }

    /**
     * Returns the last formatting code of a String
     * <p>
     * Returns '\0' if it can't find any formatting code
     * <p>
     * Returns a single character
     */
    public static char getLastFormattingCharOf(String text) {
        for (int i = text.length() - 1; i >= 0; --i) {
            if (text.charAt(i) == '§' && i + 1 < text.length()) {
                final char format = text.charAt(i + 1);
                final int index = "0123456789abcdefklmnorABCDEFKLMNOR".indexOf(format);
                if (index != -1) {
                    return index < 22 ? format : "0123456789abcdefklmnorABCDEFKLMNOR".charAt(index - 12);
                }
            }
        }
        return '\0';
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
        return c == '\0' ? "" : String.valueOf(c);
    }

    /**
     * Returns the last formatting char before the first occurence of a certain target in a String
     * <p>
     * Returns '\0' if it can't find any formatting code
     */
    public static char getLastFormattingCharBefore(String message, String target) {
        final int index = message.indexOf(target);
        return index == -1 ? '\0' : getLastFormattingCharOf(message.substring(0, index));
    }

    /**
     * Returns the last formatting code before the first occurence of a certain target in a String
     * <p>
     * Returns "" if it can't find any formatting code
     * <p>
     * Returns a single character as a String
     */
    public static String getLastFormattingCodeBefore(String message, String target) {
        final int index = message.indexOf(target);
        return index == -1 ? "" : getLastFormattingCodeOf(message.substring(0, index));
    }

    /**
     * Returns the last color code character
     * <p>
     * Returns '\0' if it can't find any color code
     * <p>
     * Returns a single character
     */
    public static char getLastColorCharOf(String text) {
        for (int i = text.length() - 1; i >= 0; --i) {
            if (text.charAt(i) == '§' && i + 1 < text.length()) {
                final char format = text.charAt(i + 1);
                final int index = "0123456789abcdefABCDEF".indexOf(format);
                if (index != -1) {
                    return index < 16 ? format : "0123456789abcdefABCDEF".charAt(index - 6);
                }
            }
        }
        return '\0';
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
        return c == '\0' ? "" : String.valueOf(c);
    }

    /**
     * Returns the last color char before the first occurence of a certain target in a String
     * <p>
     * Returns '\0' if it can't find any color code
     */
    public static char getLastColorCharBefore(String message, String target) {
        final int index = message.indexOf(target);
        return index == -1 ? '\0' : getLastColorCharOf(message.substring(0, index));
    }

    /**
     * Returns the last color code before the first occurence of a certain target in a String
     * <p>
     * Returns "" if it can't find any color code
     * <p>
     * Returns a single character as a String
     */
    public static String getLastColorCodeBefore(String message, String target) {
        final int index = message.indexOf(target);
        return index == -1 ? "" : getLastColorCodeOf(message.substring(0, index));
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String getStringAsUnicode(String s) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            stringBuilder.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
        }
        return stringBuilder.toString();
    }

    public static String uppercaseFirstLetter(String string) {
        if (string == null) {
            return null;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String getRepetitionOf(char c, int length) {
        final char[] chars = new char[length];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    /**
     * On hypixel the chat messages sent by players follow the pattern:
     * <p>
     * Alexdoru: the message sent by the player
     * <p>
     * With a bunch of color codes everywhere
     * This method adds injectedText after the name of the sender and before the ": ".
     * In addition, it can add text after the ": " which is the start of the players message.
     * In addition, it can clean all the formatting codes in the rest of the message.
     * <p>
     * Example:
     * sample text : "§6[MVP§8++§6] Kyotone§f§r§f: §r§eAlexdoru§r§f is bhopping§r"
     * after first split : "§6[MVP§8++§6] " + "Kyotone" + "§f§r§f: §r§eAlexdoru§r§f is bhopping§r"
     * after second split :"§6[MVP§8++§6] " + "Kyotone" + "§f§r§f" + ": " + "§r§eAlexdoru§r§f is bhopping§r"
     */
    public static String insertAfterName(String message, String messageSender, @Nonnull String injectedText, String injectAtMsgStart, boolean cleanEnd) {
        final String[] split = message.split(messageSender, 2);
        if (split.length != 2) {
            return message;
        }
        final String[] secondSplit = split[1].split(": ", 2);
        if (secondSplit.length != 2) {
            return split[0] + messageSender + injectedText + (cleanEnd ? removeFormattingCodes(split[1]) : split[1]);
        }
        return split[0] + messageSender + injectedText + secondSplit[0] + ": " + injectAtMsgStart + (cleanEnd ? removeFormattingCodes(secondSplit[1]) : secondSplit[1]);
    }

    /**
     * Changes the color of the target inside a message while keeping the original color after that
     */
    public static String changeColorOf(String message, String target, EnumChatFormatting color) {
        final String[] split = message.split(target, 2);
        if (split.length != 2) {
            return message;
        }
        return split[0] + color + target + '§' + getLastFormattingCodeOf(split[0]) + split[1];
    }

    /**
     * Replaces the target inside the String while preserving the color after the replacement
     */
    public static String replaceTargetWith(String message, String target, String replacement) {
        final String[] split = message.split(target, 2);
        if (split.length != 2) {
            return message;
        }
        return split[0] + replacement + '§' + getLastFormattingCodeOf(split[0]) + split[1];
    }

    public static IChatComponent censorChatMessage(String message, String messageSender) {
        final String[] split = message.split(messageSender, 2);
        if (split.length != 2) {
            return new ChatComponentText(message);
        }
        final String[] secondSplit = split[1].split(": ", 2);
        if (secondSplit.length != 2) {
            return new ChatComponentText(message);
        }
        return (new ChatComponentText(split[0] + messageSender + secondSplit[0] + ": "))
                .appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GRAY + "Censored")
                        .setChatStyle(new ChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(secondSplit[1])))));
    }

}
