package fr.alexdoru.nocheatersmod.events;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GameProfileAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoCheatersEvents {

    private static int reportQueue = 1;
    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        /* mc.thePlayer is passing in here and is instance of EntityPlayerSp
         * other players are instance of EntityOtherPlayerMP */
        if (event.entity instanceof EntityPlayer) {
            try {
                NameUtil.transformNametag((EntityPlayer) event.entity, false, ConfigHandler.togglewarnings, ConfigHandler.toggleautoreport);
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
        if (ConfigHandler.toggleautoreport
                && FKCounterMod.isInMwGame
                && !FKCounterMod.isitPrepPhase
                && wdr.canBeReported(datenow)
                && 0 < ConfigHandler.timeAutoReport - datenow + wdr.timestamp) {
            wdr.timestamp = (new Date()).getTime();
            new DelayedTask(() -> {
                if (mc.thePlayer != null) {
                    mc.thePlayer.sendChatMessage("/wdr " + playerName + " cheating");
                }
                reportQueue--;
            }, 30 * reportQueue);
            reportQueue++;
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
                boolean gotautoreported = sendAutoReport(datenow, playerName, wdr);
                list.add(IChatComponent.Serializer.jsonToComponent(createwarningmessage(datenow, uuid, playerName, wdr, gotautoreported)));
            }

        }

        return list;

    }

    public static String createwarningmessage(long datenow, String uuid, String playername, WDR wdr, boolean disableReportButton) {
        // format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc
        if (wdr.hacks.get(0).charAt(0) == '-') { // is a timestamped report

            String[] formattedmessageArray = createPlayerTimestampedMsg(playername, wdr, "light_purple");
            String allCheats = formattedmessageArray[1];

            StringBuilder stringBuilder = new StringBuilder().append("[\"\",{\"text\":\"Warning : \",\"color\":\"red\"}").append(formattedmessageArray[0]).append(",{\"text\":\" joined,\",\"color\":\"gray\"}");

            if (!disableReportButton && wdr.canBeReported(datenow - 900000)) { // montre le bouton pour re-report si l'ancien report est plus vieux que X heures
                stringBuilder.append(",{\"text\":\" Report again\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/sendreportagain ")
                        .append(uuid).append(" ").append(playername).append("\"}")
                        .append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click here to report this player again\",\"color\":\"yellow\"}]}}");
            }

            return stringBuilder.append(",{\"text\":\" Cheats : \",\"color\":\"gray\"},{\"text\":\"").append(allCheats).append("\",\"color\":\"dark_blue\"}]").toString();

        } else { // report not timestamped

            StringBuilder cheats = new StringBuilder();

            for (String hack : wdr.hacks) {
                cheats.append(" ").append(hack);
            }

            StringBuilder stringBuilder = new StringBuilder("[\"\",{\"text\":\"Warning : \",\"color\":\"red\"},{\"text\":\"").append(playername).append("\",\"color\":\"light_purple\"")
                    .append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":").append("[\"\",{\"text\":\"").append(playername).append("\",\"color\":\"light_purple\"},{\"text\":\"\\n\"},")
                    .append("{\"text\":\"Reported at : \",\"color\":\"green\"},{\"text\":\"").append(DateUtil.localformatTimestamp(wdr.timestamp)).append("\",\"color\":\"yellow\"},{\"text\":\"\\n\"},")
                    .append("{\"text\":\"Reported for :\",\"color\":\"green\"},{\"text\":\"").append(cheats).append("\",\"color\":\"gold\"},{\"text\":\"\\n\\n\"},")
                    .append("{\"text\":\"Click this message to stop receiving warnings for this player\",\"color\":\"yellow\"},{\"text\":\" \"}]}")
                    .append(",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/unwdr ").append(uuid).append(" ").append(playername).append("\"}}")
                    .append(",{\"text\":\" joined,\",\"color\":\"gray\"}");

            if (!disableReportButton && wdr.canBeReported(datenow - 900000)) { // montre le bouton pour re-report si l'ancien report est plus vieux que X heures
                stringBuilder.append(",{\"text\":\" Report again\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/sendreportagain ")
                        .append(uuid).append(" ").append(playername).append("\"}")
                        .append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click here to report this player again\",\"color\":\"yellow\"}]}}");
            }

            stringBuilder.append(",{\"text\":\" Cheats : \",\"color\":\"gray\"}");

            for (String hack : wdr.hacks) {
                if (hack.equalsIgnoreCase("bhop")) {
                    stringBuilder.append(",{\"text\":\"").append(hack).append(" ").append("\",\"color\":\"dark_red\"}");
                } else if (hack.contains("stalk")) {
                    stringBuilder.append(",{\"text\":\"").append(hack).append(" ").append("\",\"color\":\"dark_green\"}");
                } else if (hack.equalsIgnoreCase("nick")) {
                    stringBuilder.append(",{\"text\":\"").append(hack).append(" ").append("\",\"color\":\"dark_purple\"}");
                } else {
                    stringBuilder.append(",{\"text\":\"").append(hack).append(" ").append("\",\"color\":\"gold\"}");
                }
            }

            return stringBuilder.append("]").toString();

        }

    }

    /**
     * Return an array with new String[]{message,allCheats};
     * "message" is a message with the player name and a hover event on top with the timestamped report info
     * allCheats is a list of all the hacks for this player
     */
    public static String[] createPlayerTimestampedMsg(String playername, WDR wdr, String namecolor) {

        String cheats = "";
        long timestamphackreport = 0L;
        StringBuilder allCheats = new StringBuilder();
        String serverID = "";
        String timeronreplay = "";
        String playernamewhenreported = "";
        String oldname = "";
        long oldtimestamp = 0L;
        String oldgameID = "";
        StringBuilder stringBuilder = new StringBuilder(",{\"text\":\"").append(playername).append("\",\"color\":\"").append(namecolor)
                .append("\"").append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":")
                .append("[\"\",{\"text\":\"").append(playername).append("\",\"color\":\"light_purple\"}");

        int j = 0; // indice of timestamp
        for (int i = 0; i < wdr.hacks.size(); i++) {

            if ((wdr.hacks.get(i).charAt(0) == '-' && i != 0) || i == wdr.hacks.size() - 1) { // constructmessage

                if (i == wdr.hacks.size() - 1) {
                    cheats = cheats + " " + wdr.hacks.get(i);
                    allCheats.append(allCheats.toString().contains(wdr.hacks.get(i)) ? "" : " " + wdr.hacks.get(i));
                }

                if (serverID.equals(oldgameID) && Math.abs(timestamphackreport - oldtimestamp) < 3000000 && playernamewhenreported.equals(oldname)) { // if it is same server ID and reports

                    stringBuilder.append(",{\"text\":\"\\n\"},{\"text\":\"Reported at (EST - server time) : \",\"color\":\"green\"},{\"text\":\"").append(DateUtil.ESTformatTimestamp(timestamphackreport)).append("\",\"color\":\"yellow\"},{\"text\":\"\\n\"},").append("{\"text\":\"Timer on replay (approx.) : \",\"color\":\"green\"},{\"text\":\"").append(timeronreplay).append("\",\"color\":\"gold\"},{\"text\":\"\\n\"},").append("{\"text\":\"Timestamp for : \",\"color\":\"green\"},{\"text\":\"").append(cheats).append("\",\"color\":\"gold\"}").append((i == wdr.hacks.size() - 1) ? "" : ",{\"text\":\"\\n\"}");

                } else {

                    stringBuilder.append(",{\"text\":\"\\n\"},{\"text\":\"Reported at (EST - server time) : \",\"color\":\"green\"},{\"text\":\"").append(DateUtil.ESTformatTimestamp(timestamphackreport)).append("\",\"color\":\"yellow\"},{\"text\":\"\\n\"},").append("{\"text\":\"Playername at the moment of the report : \",\"color\":\"green\"},{\"text\":\"").append(playernamewhenreported).append("\",\"color\":\"red\"},{\"text\":\"\\n\"},").append("{\"text\":\"ServerID : \",\"color\":\"green\"},{\"text\":\"").append(serverID).append("\",\"color\":\"gold\"},").append("{\"text\":\" Timer on replay (approx.) : \",\"color\":\"green\"},{\"text\":\"").append(timeronreplay).append("\",\"color\":\"gold\"},{\"text\":\"\\n\"},").append("{\"text\":\"Timestamp for : \",\"color\":\"green\"},{\"text\":\"").append(cheats).append("\",\"color\":\"gold\"}").append((i == wdr.hacks.size() - 1) ? "" : ",{\"text\":\"\\n\"}");

                }

            }

            if (wdr.hacks.get(i).charAt(0) == '-') { // serverID

                j = i;
                oldgameID = serverID;
                serverID = wdr.hacks.get(i).substring(1);
                cheats = "";

            } else if (i == j + 1) { // timer on replay

                timeronreplay = wdr.hacks.get(i);

            } else if (i == j + 2) { // playernameduringgame

                oldname = playernamewhenreported;
                playernamewhenreported = wdr.hacks.get(i);

            } else if (i == j + 3) { // timestampforcheat

                oldtimestamp = timestamphackreport;
                timestamphackreport = Long.parseLong(wdr.hacks.get(i));

            } else if (i > j + 3 && i != wdr.hacks.size() - 1) { // cheats

                cheats = cheats + " " + wdr.hacks.get(i);
                allCheats.append(allCheats.toString().contains(wdr.hacks.get(i)) ? "" : " " + wdr.hacks.get(i));

            }

        }

        stringBuilder.append("]}}");

        return new String[]{stringBuilder.toString(), allCheats.toString()};

    }

}
