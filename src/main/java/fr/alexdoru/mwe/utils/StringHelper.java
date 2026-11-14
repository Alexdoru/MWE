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
        final int senderIndex = message.indexOf(messageSender);
        if (senderIndex == -1) {
            return message;
        }
        final String beforeSender = message.substring(0, senderIndex);
        final String afterSender = message.substring(senderIndex + messageSender.length());
        final int colonIndex = afterSender.indexOf(": ");
        if (colonIndex == -1) {
            return beforeSender + messageSender + injectedText + (cleanEnd ? StringUtil.removeFormattingCodes(afterSender) : afterSender);
        }
        final String afterColon = afterSender.substring(colonIndex + 2);
        return beforeSender + messageSender + injectedText + afterSender.substring(0, colonIndex) + ": " + injectAtMsgStart + (cleanEnd ? StringUtil.removeFormattingCodes(afterColon) : afterColon);
    }

    /**
     * Changes the color of the target inside a message while keeping the original color after that
     */
    public static String changeColorOf(String message, String target, EnumChatFormatting color) {
        final int index = message.indexOf(target);
        if (index == -1) {
            return message;
        }
        final String before = message.substring(0, index);
        return before + color + target + getFormattingToRestore(before) + message.substring(index + target.length());
    }

    /**
     * Replaces the target inside the String while preserving the color after the replacement
     */
    public static String replaceFirstKeepFormat(String message, String target, String replacement) {
        final int index = message.indexOf(target);
        if (index == -1) {
            return message;
        }
        final String before = message.substring(0, index);
        return before + replacement + getFormattingToRestore(before) + message.substring(index + target.length());
    }

    private static String getFormattingToRestore(String textBefore) {
        final String code = StringUtil.getLastFormattingCodeOf(textBefore);
        return code.isEmpty() ? EnumChatFormatting.RESET.toString() : '§' + code;
    }

}
