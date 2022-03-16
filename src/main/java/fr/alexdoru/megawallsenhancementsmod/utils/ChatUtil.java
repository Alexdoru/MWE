package fr.alexdoru.megawallsenhancementsmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final FontRenderer frObj = mc.fontRendererObj;

    public static String getTagMW() {
        return EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "MWEn" + EnumChatFormatting.GOLD + "] ";
    }

    public static String getTagNoCheaters() {
        return EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] ";
    }

    public static void addChatMessage(IChatComponent msg) {

        if (mc.theWorld != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(msg);
        }

    }

    public static IChatComponent makeiChatList(String listtitle, IChatComponent imessagebody, int displaypage, int nbpage, String command) {

        IChatComponent imsgstart = new ChatComponentText("");


        if (displaypage > 1) {

            imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + bar() + "\n" + "             "));

            imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + " <<")
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage - 1))))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage - 1)))));

            imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + " " + listtitle + " (Page " + displaypage + " of " + nbpage + ")"));

        } else {

            imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + bar() + "\n" + "                "));
            imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + " " + listtitle + " (Page " + displaypage + " of " + nbpage + ")"));

        }

        if (displaypage < nbpage) {

            imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + " >>" + "\n")
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage + 1))))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage + 1)))));

        } else {
            imsgstart.appendSibling(new ChatComponentText("\n"));
        }

        IChatComponent imsgend = new ChatComponentText(EnumChatFormatting.GOLD + bar());

        return imsgstart.appendSibling(imessagebody).appendSibling(imsgend);
    }

    public static String makeChatList(String listtitle, String messagebody, int displaypage, int nbpage, String command) {

        String messagestart;

        if (displaypage > 1) { // put previous page arrow

            messagestart = "[\"\",{\"text\":\"" + bar() + "\",\"color\":\"blue\"},{\"text\":\"\\n\"}"

                    + ",{\"text\":\"             \",\"color\":\"white\"}"

                    + ",{\"text\":\" <<\",\"bold\":true,\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + " " + (displaypage - 1)

                    + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to view page " + (displaypage - 1) + "\",\"color\":\"yellow\"}]}}"

                    + ",{\"text\":\" " + listtitle + " (Page " + displaypage + " of " + nbpage + ")\",\"color\":\"gold\"}"; // ajouter des boutons if c pas la premiere page etc,

        } else { // don't put previous page arrow

            messagestart = "[\"\",{\"text\":\"" + bar() + "\",\"color\":\"blue\"},{\"text\":\"\\n\"}"

                    + ",{\"text\":\"                " + listtitle + " (Page " + displaypage + " of " + nbpage + ")\",\"color\":\"gold\"}";

        }

        if (displaypage < nbpage) {

            messagestart = messagestart + ",{\"text\":\" >>\",\"bold\":true,\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + " " + (displaypage + 1)

                    + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to view page " + (displaypage + 1) + "\",\"color\":\"yellow\"}]}}"

                    + ",{\"text\":\"\\n\"}";

        } else {

            messagestart = messagestart + ",{\"text\":\"\\n\"}";

        }

        String messageend = ",{\"text\":\"" + bar() + "\",\"color\":\"blue\"}]";

        return messagestart + messagebody + messageend;

    }

    public static String apikeyMissingErrorMsg() {
        return EnumChatFormatting.DARK_RED + "You didn't set up your Api key. \n"
                + EnumChatFormatting.DARK_RED + "Connect to Hypixel and type \"/api new\" to get an Api key, the mod should automatically detect the key and save it. \n"
                + EnumChatFormatting.DARK_RED + "If you want to use a key that you already have, type \"/mwenhancements setapikey <key>\".";
    }

    public static String invalidplayernameMsg(String playername) {
        return EnumChatFormatting.RED + "The name " + EnumChatFormatting.YELLOW + playername + EnumChatFormatting.RED + " doesn't exist, it might be a nick.";
    }

    /**
     * Draws a bar that takes the width of the chat window
     */
    public static String bar() {

        char separator = '-';
        int chatWidth = mc.ingameGUI.getChatGUI().getChatWidth();
        int separatorWidth = frObj.getCharWidth(separator);

        return new String(new char[chatWidth / separatorWidth]).replace("\0", "-");
    }

    /**
     * Returns the message with spaces at the start to make the message centered in the chat box
     */
    public static String centerLine(String message) {

        char space = ' ';
        int chatWidth = mc.ingameGUI.getChatGUI().getChatWidth();
        int separatorWidth = frObj.getCharWidth(space);
        int messageWidth = frObj.getStringWidth(message);

        if (messageWidth >= chatWidth)
            return message;

        String separatorText = new String(new char[(chatWidth - messageWidth) / (2 * separatorWidth)]).replace("\0", " ");

        return separatorText + message;
    }

    /**
     * Returns the amounts of spaces needed to make a message centered
     */
    public static String getSeparatorToCenter(String message) {

        char space = ' ';
        int chatWidth = mc.ingameGUI.getChatGUI().getChatWidth();
        int separatorWidth = frObj.getCharWidth(space);
        int messageWidth = frObj.getStringWidth(message);

        if (messageWidth >= chatWidth)
            return "";

        return new String(new char[(chatWidth - messageWidth) / (2 * separatorWidth)]).replace("\0", " ");
    }

    /**
     * Returns a formatted message, the input matrix needs to be square.
     * If the message cannot be formatted (chat box too small for instance) the unformatted message is returned
     */
    public static String alignText(String[][] messagematrix) {

        char separator = ' ';
        int chatWidth = mc.ingameGUI.getChatGUI().getChatWidth();
        int separatorWidth = frObj.getCharWidth(separator);
        int columnWidth = 0;
        int maxLineWidth = 0;

        for (String[] line : messagematrix) {

            StringBuilder linemessage = new StringBuilder();

            for (String msg : line) {
                linemessage.append(msg);
                columnWidth = Math.max(columnWidth, frObj.getStringWidth(msg));
            }

            maxLineWidth = Math.max(maxLineWidth, frObj.getStringWidth(linemessage.toString()));

        }

        String leftSeparatorText = "";

        if (chatWidth > maxLineWidth)
            leftSeparatorText = new String(new char[(chatWidth - maxLineWidth) / (2 * separatorWidth)]).replace("\0", String.valueOf(separator));

        StringBuilder message = new StringBuilder();

        for (String[] strings : messagematrix) { // lines
            for (int j = 0; j < strings.length; j++) { // columns

                if (j == 0) { // first element on the left

                    int messageWidth = frObj.getStringWidth(strings[j]);
                    message.append(leftSeparatorText).append(strings[j]).append(new String(new char[(columnWidth - messageWidth) / (separatorWidth)]).replace("\0", String.valueOf(separator)));

                } else if (j == strings.length - 1) { // last element on the right

                    message.append(strings[j]).append("\n");

                } else { // element in the middle

                    int messageWidth = frObj.getStringWidth(strings[j]);
                    message.append(strings[j]).append(new String(new char[(columnWidth - messageWidth) / (separatorWidth)]).replace("\0", String.valueOf(separator)));

                }

            }

        }

        return message.toString();

    }

    public static String capitalizeFirstLetter(String string) {
        if (string == null) {
            return null;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Returns the integer as a String with a space for thousands delimiter
     */
    public static String formatInt(int number) {
        String str = String.valueOf(number);
        char separator = ' ';
        int iterator = 1;
        StringBuilder msg = new StringBuilder();

        for (int i = str.length() - 1; i >= 0; i--) {

            msg.insert(0, ((iterator == 3 && i != 0) ? String.valueOf(separator) : "") + str.charAt(i));

            if (iterator == 3) {
                iterator = 1;
            } else {
                iterator++;
            }

        }

        return msg.toString();
    }

    /**
     * Converts int from 1 to 5 to roman
     */
    public static String intToRoman(int number) {

        switch (number) {
            case (1):
                return "I";
            case (2):
                return "II";
            case (3):
                return "III";
            case (4):
                return "IV";
            case (5):
                return "V";
            default:
                return String.valueOf(number);

        }

    }

    public static IChatComponent PlanckeHeaderText(String formattedname, String playername, String titletext) {

        return new ChatComponentText(getSeparatorToCenter(formattedname + EnumChatFormatting.GOLD + titletext))
                .appendSibling(new ChatComponentText(formattedname)
                        .setChatStyle(new ChatStyle()
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/names " + playername))
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click for name history")))))

                .appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + titletext)
                        .setChatStyle(new ChatStyle()
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://plancke.io/hypixel/player/stats/" + playername))
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open Plancke in browser")))));
    }

    public static IChatComponent makeReportButtons(String playername, String cheatReport, String cheatWDR, ClickEvent.Action actionreport, ClickEvent.Action actionwdr) {

        return new ChatComponentText(EnumChatFormatting.DARK_GREEN + "Report")
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(actionreport, "/report " + playername + " " + cheatReport))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
                                        + EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/report " + playername + " " + cheatReport + "\n"
                                        + EnumChatFormatting.GRAY + "Using the report option won't save the cheater's name in the mod NoCheaters\n\n"
                                        + getReportingAdvice()))))

                .appendSibling(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + " WDR ")
                        .setChatStyle(new ChatStyle()
                                .setChatClickEvent(new ClickEvent(actionwdr, "/wdr " + playername + " " + cheatWDR))
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
                                                + EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/wdr " + playername + " " + cheatWDR + "\n"
                                                + EnumChatFormatting.GRAY + "Using the wdr option will give you warnings about this player ingame\n\n"
                                                + getReportingAdvice())))));
    }

    public static String getReportingAdvice() {
        return EnumChatFormatting.RED + "To make reporting efficient, be sure to report\n"
                + EnumChatFormatting.DARK_RED + EnumChatFormatting.BOLD + "when you are ingame with the cheater\n"
                + EnumChatFormatting.RED + "and not before the game starts or in the lobby.";
    }

    public static String getChatReportingAdvice() {
        return EnumChatFormatting.RED + "To make reporting efficient, be sure to report"
                + EnumChatFormatting.DARK_RED + EnumChatFormatting.BOLD + "when you are ingame with the cheater"
                + EnumChatFormatting.RED + "and not before the game starts or in the lobby.";
    }

    public static IChatComponent formattedNameWithReportButton(String playername, String formattedName) {
        return new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + playername + " cheating"))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
                                + EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/report " + playername + " cheating" + "\n"
                                + EnumChatFormatting.GRAY + "Using the report option won't save the cheater's name in the mod NoCheaters"))));
    }

}
