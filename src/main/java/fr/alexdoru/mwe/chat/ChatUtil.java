package fr.alexdoru.mwe.chat;

import fr.alexdoru.mwe.asm.hooks.NetHandlerPlayClientHook_PlayerMapTracker;
import fr.alexdoru.mwe.asm.interfaces.ChatComponentTextAccessor;
import fr.alexdoru.mwe.asm.interfaces.GuiChatAccessor;
import fr.alexdoru.mwe.asm.interfaces.NetworkPlayerInfoAccessor_ChatHeads;
import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

import java.util.Arrays;

import static net.minecraft.util.EnumChatFormatting.*;

public class ChatUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static String getTagMW() {
        return GOLD + "[" + DARK_GRAY + "MWE" + GOLD + "] " + RESET;
    }

    public static String getTagNoCheaters() {
        return GOLD + "[" + DARK_GRAY + "NoCheaters" + GOLD + "] " + RESET;
    }

    public static String getTagHackerDetector() {
        return ConfigHandler.flagMessagePrefix + " " + RESET;
    }

    public static String getTagHitboxes() {
        return BLUE + "[Hitbox] " + RESET;
    }

    public static void addChatMessage(String msg) {
        addChatMessage(new ChatComponentText(msg));
    }

    public static void addChatMessage(IChatComponent msg) {
        addChatMessage(msg, mc.isCallingFromMinecraftThread());
    }

    private static void addChatMessage(IChatComponent msg, boolean isCallingFromMinecraftThread) {
        if (isCallingFromMinecraftThread) {
            if (mc.theWorld != null && mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(msg);
            }
        } else {
            mc.addScheduledTask(() -> {
                if (mc.theWorld != null && mc.thePlayer != null) {
                    mc.thePlayer.addChatMessage(msg);
                }
            });
        }
    }

    public static void sendChatMessage(String msg, boolean addToHistory) {
        if (mc.thePlayer == null) return;
        mc.thePlayer.sendChatMessage(msg);
        if (!addToHistory) return;
        boolean flag = false;
        if (mc.currentScreen instanceof GuiChatAccessor) {
            flag = ((GuiChatAccessor) mc.currentScreen).getSentHistoryCursor() == mc.ingameGUI.getChatGUI().getSentMessages().size();
        }
        mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        if (flag) {
            ((GuiChatAccessor) mc.currentScreen).setSentHistoryCursor(mc.ingameGUI.getChatGUI().getSentMessages().size());
        }
    }

    public static void addSkinToComponent(IChatComponent msg, String playername) {
        if (msg instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) msg).getSkinChatHead() == null) {
            tryAddSkinToComponent(msg, playername);
        }
    }

    public static boolean tryAddSkinToComponent(IChatComponent msg, String playername) {
        final NetworkPlayerInfo netInfo = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(playername);
        if (netInfo instanceof NetworkPlayerInfoAccessor_ChatHeads) {
            final SkinChatHead skin = new SkinChatHead(netInfo.getLocationSkin());
            ((ChatComponentTextAccessor) msg).setSkinChatHead(skin);
            ((NetworkPlayerInfoAccessor_ChatHeads) netInfo).setSkinChatHead(skin);
            return true;
        } else {
            final ResourceLocation resourceLocation = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerSkin(playername);
            if (resourceLocation != null) {
                ((ChatComponentTextAccessor) msg).setSkinChatHead(new SkinChatHead(resourceLocation));
                return true;
            }
        }
        return false;
    }

    public static void printIChatList(String listtitle, IChatComponent imessagebody, int displaypage, int nbpage, String command, EnumChatFormatting barColor, IChatComponent titleHoverText, String titleURL) {
        final IChatComponent titleLine = getListTitleLine(listtitle, displaypage, nbpage, command, titleHoverText, titleURL);
        addChatMessage(new ChatComponentText(barColor + bar() + "\n")
                .appendSibling(titleLine)
                .appendText("\n")
                .appendSibling(imessagebody)
                .appendText(barColor + bar())
        );
    }

    public static IChatComponent getListTitleLine(String listtitle, int displaypage, int nbpage, String command, IChatComponent titleHoverText, String titleURL) {
        final IChatComponent titleLine = new ChatComponentText("             ");
        if (displaypage > 1) {
            titleLine.appendSibling(new ChatComponentText(YELLOW + "" + BOLD + " <<")
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(YELLOW + "Click to view page " + (displaypage - 1))))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage - 1)))));
        } else {
            titleLine.appendText("   ");
        }
        final IChatComponent titleComponent = new ChatComponentText(GOLD + " " + listtitle + " (Page " + displaypage + " of " + nbpage + ")");
        if (titleHoverText != null && titleURL != null) {
            titleComponent.setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, titleHoverText))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, titleURL)));
        }
        titleLine.appendSibling(titleComponent);
        if (displaypage < nbpage) {
            titleLine.appendSibling(new ChatComponentText(YELLOW + "" + BOLD + " >>")
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(YELLOW + "Click to view page " + (displaypage + 1))))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage + 1)))));
        }
        return titleLine;
    }

    public static void printApikeySetupInfo() {
        addChatMessage(getTagMW() + RED + "You didn't set up your Api key. If you have an Api Key, use "
                + YELLOW + "\"/mwe setapikey <key>\""
                + RED + " to use it with the mod.");
    }

    public static String inexistantMinecraftNameMsg(String playername) {
        return RED + "The name " + YELLOW + playername + RED + " doesn't exist, it might be a nick.";
    }

    public static String invalidMinecraftNameMsg(String playername) {
        return RED + "The name " + YELLOW + playername + RED + " isn't a valid Minecraft username.";
    }

    /**
     * Draws a bar that takes the width of the chat window
     */
    public static String bar() {
        final char separator = '-';
        final int chatWidth = mc.ingameGUI.getChatGUI().getChatWidth();
        final int separatorWidth = mc.fontRendererObj.getCharWidth(separator);
        return STRIKETHROUGH + new String(new char[chatWidth / separatorWidth]).replace("\0", "-");
    }

    /**
     * Returns the message with spaces at the start to make the message centered in the chat box
     */
    public static String centerLine(String message) {
        return getSeparatorToCenter(message) + message;
    }

    /**
     * Returns the amounts of spaces needed to make a message centered
     */
    public static String getSeparatorToCenter(String message) {
        final int chatWidth = mc.ingameGUI.getChatGUI().getChatWidth();
        final int messageWidth = mc.fontRendererObj.getStringWidth(message);
        if (messageWidth >= chatWidth) return "";
        return getSeparatorOfLength((chatWidth - messageWidth) / 2);
    }

    public static String getSeparatorOfLength(int length) {
        final char space = ' ';
        final int separatorWidth = mc.fontRendererObj.getCharWidth(space);
        final int amountChars = length / separatorWidth;
        if (amountChars < 1) return "";
        final char[] chars = new char[amountChars];
        Arrays.fill(chars, space);
        return new String(chars);
    }

    /**
     * Returns a formatted message, the input matrix needs to be square.
     * If the message cannot be formatted (chat box too small for instance) the unformatted message is returned
     */
    public static String alignText(String[][] messagematrix) {

        final char separator = ' ';
        final int chatWidth = mc.ingameGUI.getChatGUI().getChatWidth();
        final int separatorWidth = mc.fontRendererObj.getCharWidth(separator);
        int columnWidth = 0;
        int maxLineWidth = 0;

        for (final String[] line : messagematrix) {
            final StringBuilder linemessage = new StringBuilder();
            for (final String msg : line) {
                linemessage.append(msg);
                columnWidth = Math.max(columnWidth, mc.fontRendererObj.getStringWidth(msg));
            }
            maxLineWidth = Math.max(maxLineWidth, mc.fontRendererObj.getStringWidth(linemessage.toString()));
        }

        String leftSeparatorText = "";

        if (chatWidth > maxLineWidth) {
            leftSeparatorText = new String(new char[(chatWidth - maxLineWidth) / (2 * separatorWidth)]).replace("\0", String.valueOf(separator));
        }

        final StringBuilder message = new StringBuilder();

        for (final String[] strings : messagematrix) { // lines
            for (int j = 0; j < strings.length; j++) { // columns

                if (j == 0) { // first element on the left

                    final int messageWidth = mc.fontRendererObj.getStringWidth(strings[j]);
                    message.append(leftSeparatorText).append(strings[j]).append(new String(new char[(columnWidth - messageWidth) / (separatorWidth)]).replace("\0", String.valueOf(separator)));

                } else if (j == strings.length - 1) { // last element on the right

                    message.append(strings[j]).append("\n");

                } else { // element in the middle

                    final int messageWidth = mc.fontRendererObj.getStringWidth(strings[j]);
                    message.append(strings[j]).append(new String(new char[(columnWidth - messageWidth) / (separatorWidth)]).replace("\0", String.valueOf(separator)));

                }

            }
        }

        return message.toString();

    }

    /**
     * Returns the integer as a String with a space for thousands delimiter
     */
    public static String formatInt(int number) {
        return formatLong(number);
    }

    /**
     * Returns the integer as a String with a space for thousands delimiter
     */
    public static String formatLong(long number) {
        final String str = String.valueOf(number);
        final char separator = ' ';
        int iterator = 1;
        final StringBuilder msg = new StringBuilder();
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
        return new ChatComponentText(getSeparatorToCenter(formattedname + GOLD + titletext))
                .appendSibling(new ChatComponentText(formattedname)
                        .setChatStyle(new ChatStyle()
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/names " + playername))
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(YELLOW + "Click for name history")))))

                .appendSibling(new ChatComponentText(GOLD + titletext)
                        .setChatStyle(new ChatStyle()
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://plancke.io/hypixel/player/stats/" + playername))
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(YELLOW + "Click to open Plancke in browser")))));
    }

    public static IChatComponent getReportButtons(String playername, String cheatReport, String cheatWDR, ClickEvent.Action actionreport, ClickEvent.Action actionwdr) {
        return getReportButton(playername, cheatReport, actionreport).appendSibling(getWDRButton(playername, cheatWDR, actionwdr));
    }

    public static IChatComponent getReportButton(String playername, String cheatReport, ClickEvent.Action actionreport) {
        return new ChatComponentText(DARK_GREEN + " [Report]")
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(actionreport, "/report " + playername + " " + cheatReport))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText(GREEN + "Click this message to report this player" + "\n"
                                        + YELLOW + "Command : " + RED + "/report " + playername + " " + cheatReport + "\n"
                                        + GRAY + "Using the report option won't save the cheater's name in the mod NoCheaters"
                                        + getReportingAdvice()))));
    }

    public static IChatComponent getWDRButton(String playername, String cheatWDR, ClickEvent.Action actionwdr) {
        return new ChatComponentText(DARK_PURPLE + " [WDR]")
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(actionwdr, "/wdr " + playername + " " + cheatWDR))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText(GREEN + "Click this message to report this player" + "\n"
                                        + YELLOW + "Command : " + RED + "/wdr " + playername + " " + cheatWDR + "\n"
                                        + GRAY + "Using the wdr option will give you warnings about this player ingame\n"
                                        + GRAY + "You can use " + YELLOW + "/unwdr " + playername + GRAY + " to remove them from your report list"
                                        + getReportingAdvice()))));
    }

    public static String getReportingAdvice() {
        if (ScoreboardTracker.isPreGameLobby()) {
            return "\n\n" + RED + "To make reporting efficient, be sure to use /report"
                    + DARK_RED + " when you are ingame with\n" + DARK_RED + "the cheater and they are currently cheating\n"
                    + RED + ", not 2 minutes after, not after the game ended or before the game starts.";
        }
        return "";
    }

    public static void printReportingAdvice() {
        addChatMessage(getTagNoCheaters() + RED + "To make reporting efficient, be sure to use /report"
                + DARK_RED + " when you are ingame with the cheater and they are currently cheating "
                + RED + ", not 2 minutes after, not after the game ended or before the game starts." +
                " This way a replay with a timestamp can be attached to the report for review.");
    }

    public static IChatComponent formattedNameWithReportButton(String playername, String formattedName) {
        return new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + playername + " cheating"))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText(GREEN + "Click this message to report this player" + "\n"
                                + YELLOW + "Command : " + RED + "/report " + playername + " cheating" + "\n"
                                + GRAY + "Using the report option won't save the cheater's name in the mod NoCheaters"))));
    }

    public static void debug(String msg) {
        addChatMessage(AQUA + "[Debug]: " + RESET + msg);
    }

}
