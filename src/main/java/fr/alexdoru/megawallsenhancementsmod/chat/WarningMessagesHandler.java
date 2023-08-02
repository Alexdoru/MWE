package fr.alexdoru.megawallsenhancementsmod.chat;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.GuiScreenHook;
import fr.alexdoru.megawallsenhancementsmod.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.data.WdrData;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Date;

public class WarningMessagesHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Called when you type /nocheaters
     */
    public static void printReportMessagesForWorld(boolean callFromCommand) {
        ChatHandler.deleteAllWarningMessages();
        boolean foundReport = false;
        final long datenow = (new Date()).getTime();
        for (final NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
            final String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
            final String playerName = networkPlayerInfo.getGameProfile().getName();
            final WDR wdr = WdrData.getWdr(uuid, playerName);
            if (wdr == null) {
                continue;
            }
            foundReport = true;
            final boolean gotautoreported = ReportQueue.INSTANCE.addAutoReportToQueue(datenow, playerName, wdr);
            if (wdr.transformName()) {
                printWarningMessage(
                        datenow,
                        uuid,
                        (!ScoreboardTracker.isInMwGame || ScoreboardTracker.isPrepPhase) ? null : ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), playerName),
                        playerName,
                        wdr,
                        gotautoreported
                );
            }
        }
        if (callFromCommand && !foundReport) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "No reported player here !");
        }
    }

    public static void printWarningMessage(long datenow, String uuid, String formattedName, String playername, WDR wdr, boolean disableReportButton) {

        final String wdrmapKey = wdr.isNicked() ? playername : uuid;
        final IChatComponent[] imsgArray = createPlayerNameWithHoverText(formattedName, playername, wdrmapKey, wdr, EnumChatFormatting.LIGHT_PURPLE);
        final IChatComponent imsg = new ChatComponentText(EnumChatFormatting.RED + "Warning : ").appendSibling(imsgArray[0]);
        final IChatComponent allCheats = imsgArray[1];
        imsg.appendText(EnumChatFormatting.GRAY + " joined,");
        final boolean olderThanMaxAutoreport = wdr.shouldPrintBigText(datenow);

        if (olderThanMaxAutoreport) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + " [Report Player]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click here to continue auto-reporting that player every game")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook.SEND_REPORT_AGAIN + wdrmapKey + " " + playername))));
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " [Remove Player]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click here to remove this player from your report list\n"
                            + EnumChatFormatting.YELLOW + "and stop auto-reporting them every game")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + wdrmapKey + " " + playername))));
            imsg.appendText(EnumChatFormatting.RED + " It's been " + EnumChatFormatting.GRAY + DateUtil.timeSince(wdr.timeLastManualReport) + EnumChatFormatting.RED + " since you last manually reported that player for :")
                    .appendSibling(allCheats)
                    .appendText(EnumChatFormatting.RED + ", either remove them from your report list or report them again.");
        } else if (!disableReportButton && ScoreboardTracker.isInMwGame && !ScoreboardTracker.isPrepPhase && wdr.canBeReported(datenow)) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + " [Report again]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click here to report this player again")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook.SEND_REPORT_AGAIN + wdrmapKey + " " + playername))));
        }

        if (!ScoreboardTracker.isPreGameLobby) {
            allCheats.setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
                            + EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/report " + playername + " cheating" + ChatUtil.getReportingAdvice())))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + playername + " cheating")));
        }

        if (!olderThanMaxAutoreport) {
            imsg.appendText(EnumChatFormatting.GRAY + " Cheats :");
            imsg.appendSibling(allCheats);
        }

        ChatUtil.addChatMessage(imsg);

    }

    /**
     * Returns an array with new IChatComponent[]{playernameWithHoverText,allCheats};
     * "playernameWithHoverText" is a message with the player name and a hover event on top with the report info
     * "allCheats" is a list of all the hacks for this player
     */
    public static IChatComponent[] createPlayerNameWithHoverText(String formattedNameIn, String playername, String wdrmapKey, WDR wdr, EnumChatFormatting namecolor) {

        final String formattedName = formattedNameIn == null ? namecolor.toString() + playername : formattedNameIn;

        if (wdr.hacks.get(0).charAt(0) == '-') {
            StringBuilder cheats = new StringBuilder();
            long timestamphackreport = 0L;
            final StringBuilder allCheats = new StringBuilder();
            String serverID = "";
            String timeronreplay = "";
            String playernamewhenreported = "";
            String oldname = "";
            long oldtimestamp = 0L;
            String oldgameID = "";
            final IChatComponent hoverText = new ChatComponentText(formattedName);

            int j = 0;
            for (int i = 0; i < wdr.hacks.size(); i++) {

                if ((wdr.hacks.get(i).charAt(0) == '-' && i != 0) || i == wdr.hacks.size() - 1) {

                    if (i == wdr.hacks.size() - 1) {
                        cheats.append(" ").append(wdr.hacks.get(i));
                        allCheats.append(allCheats.toString().contains(wdr.hacks.get(i)) ? "" : " " + wdr.hacks.get(i));
                    }

                    if (serverID.equals(oldgameID) && Math.abs(timestamphackreport - oldtimestamp) < 3000000 && playernamewhenreported.equals(oldname)) { // if it is same server ID and reports

                        hoverText.appendText("\n"
                                + EnumChatFormatting.GREEN + "Reported at (EST - server time) : " + EnumChatFormatting.YELLOW + DateUtil.ESTformatTimestamp(timestamphackreport) + "\n"
                                + EnumChatFormatting.GREEN + "Timer on replay (approx.) : " + EnumChatFormatting.GOLD + timeronreplay + "\n"
                                + EnumChatFormatting.GREEN + "Timestamp for : " + EnumChatFormatting.GOLD + cheats + ((i == wdr.hacks.size() - 1) ? "" : "\n"));

                    } else {

                        hoverText.appendText("\n"
                                + EnumChatFormatting.GREEN + "Reported at (EST - server time) : " + EnumChatFormatting.YELLOW + DateUtil.ESTformatTimestamp(timestamphackreport) + "\n"
                                + EnumChatFormatting.GREEN + "Playername at the moment of the report : " + EnumChatFormatting.RED + playernamewhenreported + "\n"
                                + EnumChatFormatting.GREEN + "ServerID : " + EnumChatFormatting.GOLD + serverID + EnumChatFormatting.GREEN + " Timer on replay (approx.) : " + EnumChatFormatting.GOLD + timeronreplay + "\n"
                                + EnumChatFormatting.GREEN + "Timestamp for : " + EnumChatFormatting.GOLD + cheats + ((i == wdr.hacks.size() - 1) ? "" : "\n"));

                    }

                }

                if (wdr.hacks.get(i).charAt(0) == '-') { // serverID

                    j = i;
                    oldgameID = serverID;
                    serverID = wdr.hacks.get(i).substring(1);
                    cheats = new StringBuilder();

                } else if (i == j + 1) { // timer on replay

                    timeronreplay = wdr.hacks.get(i);

                } else if (i == j + 2) { // playernameduringgame

                    oldname = playernamewhenreported;
                    playernamewhenreported = wdr.hacks.get(i);

                } else if (i == j + 3) { // timestampforcheat

                    oldtimestamp = timestamphackreport;
                    timestamphackreport = Long.parseLong(wdr.hacks.get(i));

                } else if (i > j + 3 && i != wdr.hacks.size() - 1) { // cheats

                    cheats.append(" ").append(wdr.hacks.get(i));
                    allCheats.append(allCheats.toString().contains(wdr.hacks.get(i)) ? "" : " " + wdr.hacks.get(i));

                }

            }

            final IChatComponent imsg = new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));

            return new IChatComponent[]{imsg, new ChatComponentText(EnumChatFormatting.DARK_BLUE + allCheats.toString())};

        } else {

            final IChatComponent imsg = new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + wdrmapKey + " " + playername))
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                            formattedName + "\n"
                                    //+ EnumChatFormatting.GREEN + "Last auto report : " + EnumChatFormatting.YELLOW + DateUtil.localformatTimestamp(wdr.timestamp) + "\n"
                                    + EnumChatFormatting.GREEN + "Last reported : " + EnumChatFormatting.YELLOW + DateUtil.timeSince(wdr.timeLastManualReport) + " ago, on " + DateUtil.localformatTimestamp(wdr.timeLastManualReport) + "\n"
                                    + EnumChatFormatting.GREEN + "Reported for :" + EnumChatFormatting.GOLD + wdr.hacksToString() + "\n\n"
                                    + EnumChatFormatting.YELLOW + "Click here to remove this player from your report list"))));

            final IChatComponent allCheats = new ChatComponentText("");

            for (final String hack : wdr.hacks) {
                if (hack.startsWith("bhop")
                        || hack.startsWith("autoblock")
                        || hack.startsWith("fastbreak")
                        || hack.startsWith("noslowdown")) {
                    allCheats.appendText(" " + EnumChatFormatting.DARK_RED + hack);
                } else if (hack.equalsIgnoreCase(WDR.NICK)) {
                    allCheats.appendText(" " + EnumChatFormatting.DARK_PURPLE + hack);
                } else if (!hack.equals(WDR.IGNORED)) {
                    allCheats.appendText(" " + EnumChatFormatting.GOLD + hack);
                }
            }

            return new IChatComponent[]{imsg, allCheats};

        }

    }

}
