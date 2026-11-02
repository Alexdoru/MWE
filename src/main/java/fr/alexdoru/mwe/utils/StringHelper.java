package fr.alexdoru.mwe.utils;

import fr.alexdoru.mwe.api.utils.StringUtil;
import net.minecraft.util.EnumChatFormatting;

public final class StringHelper {

    private StringHelper() {}

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
    public static String insertAfterName(String message, String messageSender, String injectedText, String injectAtMsgStart, boolean cleanEnd) {
        final String[] split = message.split(messageSender, 2);
        if (split.length != 2) {
            return message;
        }
        final String[] secondSplit = split[1].split(": ", 2);
        if (secondSplit.length != 2) {
            return split[0] + messageSender + injectedText + (cleanEnd ? StringUtil.removeFormattingCodes(split[1]) : split[1]);
        }
        return split[0] + messageSender + injectedText + secondSplit[0] + ": " + injectAtMsgStart + (cleanEnd ? StringUtil.removeFormattingCodes(secondSplit[1]) : secondSplit[1]);
    }

    /**
     * Changes the color of the target inside a message while keeping the original color after that
     */
    public static String changeColorOf(String message, String target, EnumChatFormatting color) {
        final String[] split = message.split(target, 2);
        if (split.length != 2) {
            return message;
        }
        return split[0] + color + target + '§' + StringUtil.getLastFormattingCodeOf(split[0]) + split[1];
    }

    /**
     * Replaces the target inside the String while preserving the color after the replacement
     */
    public static String replaceFirstKeepFormat(String message, String target, String replacement) {
        final String[] split = message.split(target, 2);
        if (split.length != 2) {
            return message;
        }
        return split[0] + replacement + '§' + StringUtil.getLastFormattingCodeOf(split[0]) + split[1];
    }

}
