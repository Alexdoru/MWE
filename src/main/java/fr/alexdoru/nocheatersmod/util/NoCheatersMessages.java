package fr.alexdoru.nocheatersmod.util;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.ReportQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoCheatersMessages {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Called when you type /nocheaters
     */
    public static List<IChatComponent> getReportMessagesforWorld() {
        List<IChatComponent> list = new ArrayList<>();
        long datenow = (new Date()).getTime();
        for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
            String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
            String playerName = networkPlayerInfo.getGameProfile().getName();
            WDR wdr = WdredPlayers.getWdredMap().get(uuid);
            if (wdr == null) {
                wdr = WdredPlayers.getWdredMap().get(playerName);
                if (wdr != null) {
                    uuid = playerName;
                }
            }
            if (wdr == null) {
                continue;
            }
            boolean gotautoreported = !wdr.isCheating() || ReportQueue.INSTANCE.sendAutoReport(datenow, playerName, wdr);// TODO mettre le check is cheating dans le send auto report
            list.add(createwarningmessage(
                    datenow,
                    uuid,
                    (!FKCounterMod.isInMwGame || FKCounterMod.isitPrepPhase) ? null : ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), playerName),
                    playerName,
                    wdr,
                    gotautoreported
            ));
        }
        return list;
    }

    public static IChatComponent createwarningmessage(long datenow, String uuid, String formattedName, String playername, WDR wdr, boolean disableReportButton) {

        String wdrmapKey = wdr.isNicked() ? playername : uuid;
        IChatComponent[] imsgArray = createPlayerNameWithHoverText(formattedName, playername, wdrmapKey, wdr, EnumChatFormatting.LIGHT_PURPLE);
        IChatComponent imsg = new ChatComponentText(EnumChatFormatting.RED + "Warning : ").appendSibling(imsgArray[0]);
        IChatComponent allCheats = imsgArray[1];
        imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GRAY + " joined,"));
        boolean olderThanMaxAutoreport = wdr.isOlderThanMaxAutoreport(datenow);

        if (olderThanMaxAutoreport) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + " [Report Player]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click here to report this player again")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sendreportagain " + wdrmapKey + " " + playername))));
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " [Remove Player]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click here to remove this player from your report list")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + wdrmapKey + " " + playername))));
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.RED + " It's been " + EnumChatFormatting.GRAY + DateUtil.timeSince(wdr.timeLastManualReport) + EnumChatFormatting.RED + " since you last manually reported that player for : "))
                    .appendSibling(allCheats)
                    .appendSibling(new ChatComponentText(EnumChatFormatting.RED + ", either remove them from your report list or report them again."));
        } else if (!disableReportButton && FKCounterMod.isInMwGame && !FKCounterMod.isitPrepPhase && wdr.canBeReported(datenow)) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + " Report again").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click here to report this player again")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sendreportagain " + wdrmapKey + " " + playername))));
        }

        if (!FKCounterMod.preGameLobby) {
            allCheats.setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
                            + EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/report " + playername + " cheating" + "\n\n"
                            + ChatUtil.getReportingAdvice())))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + playername + " cheating")));
        }

        if (!olderThanMaxAutoreport) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GRAY + " Cheats : ")).appendSibling(allCheats);
        }

        return imsg;

    }

    /**
     * Returns an array with new IChatComponent[]{playernameWithHoverText,allCheats};
     * "playernameWithHoverText" is a message with the player name and a hover event on top with the report info
     * "allCheats" is a list of all the hacks for this player
     */
    public static IChatComponent[] createPlayerNameWithHoverText(String formattedNameIn, String playername, String wdrmapKey, WDR wdr, EnumChatFormatting namecolor) {

        String formattedName = formattedNameIn == null ? namecolor.toString() + playername : formattedNameIn;
        StringBuilder cheats = new StringBuilder();
        if (wdr.hacks.get(0).charAt(0) == '-') {

            long timestamphackreport = 0L;
            StringBuilder allCheats = new StringBuilder();
            String serverID = "";
            String timeronreplay = "";
            String playernamewhenreported = "";
            String oldname = "";
            long oldtimestamp = 0L;
            String oldgameID = "";
            IChatComponent hoverText = new ChatComponentText(formattedName);

            int j = 0;
            for (int i = 0; i < wdr.hacks.size(); i++) {

                if ((wdr.hacks.get(i).charAt(0) == '-' && i != 0) || i == wdr.hacks.size() - 1) {

                    if (i == wdr.hacks.size() - 1) {
                        cheats.append(" ").append(wdr.hacks.get(i));
                        allCheats.append(allCheats.toString().contains(wdr.hacks.get(i)) ? "" : " " + wdr.hacks.get(i));
                    }

                    if (serverID.equals(oldgameID) && Math.abs(timestamphackreport - oldtimestamp) < 3000000 && playernamewhenreported.equals(oldname)) { // if it is same server ID and reports

                        hoverText.appendSibling(new ChatComponentText("\n"
                                + EnumChatFormatting.GREEN + "Reported at (EST - server time) : " + EnumChatFormatting.YELLOW + DateUtil.ESTformatTimestamp(timestamphackreport) + "\n"
                                + EnumChatFormatting.GREEN + "Timer on replay (approx.) : " + EnumChatFormatting.GOLD + timeronreplay + "\n"
                                + EnumChatFormatting.GREEN + "Timestamp for : " + EnumChatFormatting.GOLD + cheats + ((i == wdr.hacks.size() - 1) ? "" : "\n")));

                    } else {

                        hoverText.appendSibling(new ChatComponentText("\n"
                                + EnumChatFormatting.GREEN + "Reported at (EST - server time) : " + EnumChatFormatting.YELLOW + DateUtil.ESTformatTimestamp(timestamphackreport) + "\n"
                                + EnumChatFormatting.GREEN + "Playername at the moment of the report : " + EnumChatFormatting.RED + playernamewhenreported + "\n"
                                + EnumChatFormatting.GREEN + "ServerID : " + EnumChatFormatting.GOLD + serverID + EnumChatFormatting.GREEN + " Timer on replay (approx.) : " + EnumChatFormatting.GOLD + timeronreplay + "\n"
                                + EnumChatFormatting.GREEN + "Timestamp for : " + EnumChatFormatting.GOLD + cheats + ((i == wdr.hacks.size() - 1) ? "" : "\n")));

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

            IChatComponent imsg = new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));

            return new IChatComponent[]{imsg, new ChatComponentText(EnumChatFormatting.DARK_BLUE + allCheats.toString())};

        } else {

            for (String hack : wdr.hacks) {
                cheats.append(" ").append(hack);
            }

            IChatComponent imsg = new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + wdrmapKey + " " + playername))
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                            formattedName + "\n"
                                    + EnumChatFormatting.GREEN + "Last auto report : " + EnumChatFormatting.YELLOW + DateUtil.localformatTimestamp(wdr.timestamp) + "\n"
                                    + EnumChatFormatting.GREEN + "Last manual report : " + EnumChatFormatting.YELLOW + DateUtil.localformatTimestamp(wdr.timeLastManualReport) + "\n"
                                    + EnumChatFormatting.GREEN + "Reported for :" + EnumChatFormatting.GOLD + cheats + "\n\n"
                                    + EnumChatFormatting.YELLOW + "Click here to remove this player from your report list"))));

            IChatComponent allCheats = new ChatComponentText("");

            for (String hack : wdr.hacks) {
                if (hack.equalsIgnoreCase("bhop")) {
                    allCheats.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_RED + hack + " "));
                } else if (hack.contains(WDR.IGNORED)) {
                    allCheats.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GRAY + hack + " "));
                } else if (hack.equalsIgnoreCase(WDR.NICK)) {
                    allCheats.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + hack + " "));
                } else {
                    allCheats.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + hack + " "));
                }
            }

            return new IChatComponent[]{imsg, allCheats};

        }

    }

}
