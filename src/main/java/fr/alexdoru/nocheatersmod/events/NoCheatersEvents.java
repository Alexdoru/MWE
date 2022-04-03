package fr.alexdoru.nocheatersmod.events;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GameProfileAccessor;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoCheatersEvents {

    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            // TODO tester l'impact sur les performances du try/catch block
            //  Placing code inside a try-catch block inhibits certain optimizations that modern JVM implementations might otherwise perform.
            try {
                if (event.entity instanceof EntityPlayerSP) {
                    /*Delaying the transformation for self because certain fields such as mc.theWorld.getScoreboard().getPlayersTeam(username) are null when you just joined the world*/
                    new DelayedTask(() -> NameUtil.transformNametag((EntityPlayer) event.entity, true), 1);
                } else {
                    NameUtil.transformNametag((EntityPlayer) event.entity, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the auto report feature
     *
     * @return true if it sends a report
     */
    public static boolean sendAutoReport(long datenow, String playerName, WDR wdr) {
        if (wdr.canBeAutoreported(datenow)) {
            wdr.timestamp = datenow;
            ReportQueue.INSTANCE.addPlayerToQueue(playerName);
            return true;
        }
        return false;
    }

    /**
     * Called when you type /nocheaters
     */
    public static List<IChatComponent> getReportMessagesforWorld() {

        List<IChatComponent> list = new ArrayList<>();
        long datenow = (new Date()).getTime();

        for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {

            MWPlayerData mwPlayerData = ((GameProfileAccessor) networkPlayerInfo.getGameProfile()).getMWPlayerData();
            if (mwPlayerData != null) {
                WDR wdr = mwPlayerData.wdr;
                if (wdr == null) {
                    continue;
                }
                String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
                String playerName = networkPlayerInfo.getGameProfile().getName();
                boolean gotautoreported = !wdr.isCheating() || sendAutoReport(datenow, playerName, wdr);
                list.add(createwarningmessage(datenow, uuid, playerName, wdr, gotautoreported));
            }

        }

        return list;

    }

    public static IChatComponent createwarningmessage(long datenow, String uuid, String playername, WDR wdr, boolean disableReportButton) {

        IChatComponent imsg;
        IChatComponent allCheats;

        if (wdr.hacks.get(0).charAt(0) == '-') {
            // format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc
            IChatComponent[] formattedmessageArray = createPlayerTimestampedMsg(playername, wdr, EnumChatFormatting.LIGHT_PURPLE);
            allCheats = formattedmessageArray[1];

            imsg = new ChatComponentText(EnumChatFormatting.RED + "Warning : ").appendSibling(formattedmessageArray[0]).appendSibling(new ChatComponentText(EnumChatFormatting.GRAY + " joined,"));

        } else {

            StringBuilder cheats = new StringBuilder();

            for (String hack : wdr.hacks) {
                cheats.append(" ").append(hack);
            }

            imsg = new ChatComponentText(EnumChatFormatting.RED + "Warning : ")
                    .appendSibling(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + playername).setChatStyle(new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + uuid + " " + playername))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                    EnumChatFormatting.LIGHT_PURPLE + playername + "\n"
                                            + EnumChatFormatting.GREEN + "Last report : " + EnumChatFormatting.YELLOW + DateUtil.localformatTimestamp(wdr.timestamp) + "\n"
                                            + EnumChatFormatting.GREEN + "Last manual report : " + EnumChatFormatting.YELLOW + DateUtil.localformatTimestamp(wdr.timeLastManualReport) + "\n"
                                            + EnumChatFormatting.GREEN + "Reported for :" + EnumChatFormatting.GOLD + cheats + "\n\n"
                                            + EnumChatFormatting.YELLOW + "Click here to remove this player from your report list")))));

            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GRAY + " joined,"));

            allCheats = new ChatComponentText("");

            for (String hack : wdr.hacks) {
                if (hack.equalsIgnoreCase("bhop")) {
                    allCheats.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_RED + hack + " "));
                } else if (hack.contains(WDR.IGNORED)) {
                    allCheats.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GRAY + hack + " "));
                }else if (hack.equalsIgnoreCase(WDR.NICK)) {
                    allCheats.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + hack + " "));
                } else {
                    allCheats.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + hack + " "));
                }
            }

        }

        if (!disableReportButton && FKCounterMod.isInMwGame && !FKCounterMod.isitPrepPhase && wdr.canBeReported(datenow)) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + " Report again").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click here to report this player again")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sendreportagain " + uuid + " " + playername))));
        } else if ((FKCounterMod.isInMwGame || FKCounterMod.preGameLobby) && wdr.isOlderThanMaxAutoreport(datenow)) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + " [Report Player]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click here to report this player again")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sendreportagain " + uuid + " " + playername))));
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " [Remove Player]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click here to remove this player from your report list")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + uuid + " " + playername))));
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.RED + " It's been more than a week since you last manually reported that player, are you sure they are still cheating ? Report them again or Remove them from your report list."));
        }

        if (!FKCounterMod.preGameLobby) {
            allCheats.setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
                            + EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/report " + playername + " cheating" + "\n\n"
                            + ChatUtil.getReportingAdvice())))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + playername + " cheating")));
        }

        return imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GRAY + " Cheats : ")).appendSibling(allCheats);

    }

    /**
     * Returns an array with new IChatComponent[]{message,allCheats};
     * "message" is a message with the player name and a hover event on top with the timestamped report info
     * allCheats is a list of all the hacks for this player
     */
    public static IChatComponent[] createPlayerTimestampedMsg(String playername, WDR wdr, EnumChatFormatting namecolor) {

        StringBuilder cheats = new StringBuilder();
        long timestamphackreport = 0L;
        StringBuilder allCheats = new StringBuilder();
        String serverID = "";
        String timeronreplay = "";
        String playernamewhenreported = "";
        String oldname = "";
        long oldtimestamp = 0L;
        String oldgameID = "";
        IChatComponent hoverText = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + playername);

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

        IChatComponent imsg = new ChatComponentText(namecolor + playername).setChatStyle(new ChatStyle()
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));

        return new IChatComponent[]{imsg, new ChatComponentText(EnumChatFormatting.DARK_BLUE + allCheats.toString())};

    }

}
