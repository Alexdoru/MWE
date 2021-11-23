package fr.alexdoru.nocheatersmod.events;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;

public class NoCheatersEvents {

    private static int ticks = 0;
    public static int nbReport = 0;
    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if (event.gui instanceof GuiDownloadTerrain) {
            ticks = 0;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!ConfigHandler.togglewarnings || !mc.inGameHasFocus) {
            return;
        }
        if (ticks == 39) {
            scanCurrentWorld();
            ticks++;
        } else if (ticks < 39) {
            ticks++;
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (ticks < 39 || mc.thePlayer == null || !(event.entity instanceof EntityPlayer)) {
            return;
        }
        NameUtil.handlePlayer((EntityPlayer) event.entity, ConfigHandler.toggleicons, ConfigHandler.togglewarnings, ConfigHandler.toggleautoreport);
    }

    public static void scanCurrentWorld() {

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

            if (wdr != null) {

                if (ConfigHandler.toggleicons) {
                    EntityPlayer player = mc.theWorld.getPlayerEntityByName(playerName);
                    if (wdr.hacks.contains("bhop")) { // player bhops
                        player.addPrefix(NameUtil.iprefix_bhop);
                    } else if (!(wdr.isOnlyStalking())) { // player is cheating
                        player.addPrefix(NameUtil.iprefix);
                    }
                    player.refreshDisplayName();
                }

                boolean gotautoreported = false;

                if (ConfigHandler.toggleautoreport && datenow - wdr.timestamp > ConfigHandler.timeBetweenReports && datenow - wdr.timestamp < ConfigHandler.timeAutoReport) {
                    String finalUuid = uuid;
                    new DelayedTask(() -> {
                        ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/sendreportagain " + finalUuid + " " + playerName);
                        nbReport--;
                    }, 20 * nbReport);
                    nbReport++;
                    gotautoreported = true;
                }

                if (ConfigHandler.togglewarnings) {
                    addChatMessage(IChatComponent.Serializer.jsonToComponent(createwarningmessage(datenow, uuid, playerName, wdr, gotautoreported)));
                }

            }

        }

    }

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

            if (wdr != null) {
                list.add(IChatComponent.Serializer.jsonToComponent(createwarningmessage(datenow, uuid, playerName, wdr, false)));
                if (ConfigHandler.toggleautoreport && datenow - wdr.timestamp > ConfigHandler.timeBetweenReports && datenow - wdr.timestamp < ConfigHandler.timeAutoReport) {
                    String finalUuid = uuid;
                    new DelayedTask(() -> {
                        ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/sendreportagain " + finalUuid + " " + playerName);
                        nbReport--;
                    }, 20 * nbReport);
                    nbReport++;
                }
            }

        }

        return list;

    }

    public static String createwarningmessage(long datenow, String uuid, String playername, WDR wdr, boolean forceNoReportAgain) {
        // format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc
        if (wdr.hacks.get(0).charAt(0) == '-') { // is a timestamped report

            String[] formattedmessageArray = createPlayerTimestampedMsg(playername, wdr, "light_purple");
            String allCheats = formattedmessageArray[1];

            String message = "[\"\",{\"text\":\"Warning : \",\"color\":\"red\"}" + formattedmessageArray[0] + ",{\"text\":\" joined,\",\"color\":\"gray\"}";

            if (!forceNoReportAgain && datenow - wdr.timestamp > ConfigHandler.timeBetweenReports) { // montre le bouton pour re-report si l'ancien report est plus vieux que X heures

                message = message + ",{\"text\":\" Report again\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/sendreportagain " + uuid + " " + playername + "\"}"

                        + ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click here to report this player again\",\"color\":\"yellow\"}]}}";

            }

            return message + ",{\"text\":\" Cheats : \",\"color\":\"gray\"},{\"text\":\"" + allCheats + "\",\"color\":\"dark_blue\"}]";

        } else { // report not timestamped

            StringBuilder cheats = new StringBuilder();

            for (String hack : wdr.hacks) {
                cheats.append(" ").append(hack);
            }

            StringBuilder message = new StringBuilder("[\"\",{\"text\":\"Warning : \",\"color\":\"red\"},{\"text\":\"" + playername + "\",\"color\":\"light_purple\""

                    + ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":"

                    + "[\"\",{\"text\":\"" + playername + "\",\"color\":\"light_purple\"},{\"text\":\"\\n\"},"

                    + "{\"text\":\"Reported at : \",\"color\":\"green\"},{\"text\":\"" + DateUtil.localformatTimestamp(wdr.timestamp) + "\",\"color\":\"yellow\"},{\"text\":\"\\n\"},"

                    + "{\"text\":\"Reported for :\",\"color\":\"green\"},{\"text\":\"" + cheats + "\",\"color\":\"gold\"},{\"text\":\"\\n\\n\"},"

                    + "{\"text\":\"Click this message to stop receiving warnings for this player\",\"color\":\"yellow\"},{\"text\":\" \"}]}"

                    + ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/unwdr " + uuid + " " + playername + "\"}}"

                    + ",{\"text\":\" joined,\",\"color\":\"gray\"}");

            if (datenow - wdr.timestamp > ConfigHandler.timeBetweenReports && !(wdr.isOnlyStalking())) { // montre le bouton pour re-report si l'ancien report est plus vieux que X heures

                message.append(",{\"text\":\" Report again\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/sendreportagain ").append(uuid).append(" ").append(playername).append("\"}").append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click here to report this player again\",\"color\":\"yellow\"}]}}");

            }

            message.append(",{\"text\":\" Cheats : \",\"color\":\"gray\"}");

            for (String hack : wdr.hacks) {
                if (hack.equalsIgnoreCase("bhop")) {
                    message.append(",{\"text\":\"").append(hack).append(" ").append("\",\"color\":\"dark_red\"}");
                } else if (hack.contains("stalk")) {
                    message.append(",{\"text\":\"").append(hack).append(" ").append("\",\"color\":\"dark_green\"}");
                } else if (hack.equalsIgnoreCase("nick")) {
                    message.append(",{\"text\":\"").append(hack).append(" ").append("\",\"color\":\"dark_purple\"}");
                } else {
                    message.append(",{\"text\":\"").append(hack).append(" ").append("\",\"color\":\"gold\"}");
                }
            }

            return message + "]";

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
        StringBuilder message = new StringBuilder(",{\"text\":\"" + playername + "\",\"color\":\"" + namecolor + "\""

                + ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":"

                + "[\"\",{\"text\":\"" + playername + "\",\"color\":\"light_purple\"}");

        int j = 0; // indice of timestamp
        for (int i = 0; i < wdr.hacks.size(); i++) {

            if ((wdr.hacks.get(i).charAt(0) == '-' && i != 0) || i == wdr.hacks.size() - 1) { // constructmessage

                if (i == wdr.hacks.size() - 1) {
                    cheats = cheats + " " + wdr.hacks.get(i);
                    allCheats.append(allCheats.toString().contains(wdr.hacks.get(i)) ? "" : " " + wdr.hacks.get(i));
                }

                if (serverID.equals(oldgameID) && Math.abs(timestamphackreport - oldtimestamp) < 3000000 && playernamewhenreported.equals(oldname)) { // if it is same server ID and reports

                    message.append(",{\"text\":\"\\n\"},{\"text\":\"Reported at (EST - server time) : \",\"color\":\"green\"},{\"text\":\"").append(DateUtil.ESTformatTimestamp(timestamphackreport)).append("\",\"color\":\"yellow\"},{\"text\":\"\\n\"},").append("{\"text\":\"Timer on replay (approx.) : \",\"color\":\"green\"},{\"text\":\"").append(timeronreplay).append("\",\"color\":\"gold\"},{\"text\":\"\\n\"},").append("{\"text\":\"Timestamp for : \",\"color\":\"green\"},{\"text\":\"").append(cheats).append("\",\"color\":\"gold\"}").append((i == wdr.hacks.size() - 1) ? "" : ",{\"text\":\"\\n\"}");

                } else {

                    message.append(",{\"text\":\"\\n\"},{\"text\":\"Reported at (EST - server time) : \",\"color\":\"green\"},{\"text\":\"").append(DateUtil.ESTformatTimestamp(timestamphackreport)).append("\",\"color\":\"yellow\"},{\"text\":\"\\n\"},").append("{\"text\":\"Playername at the moment of the report : \",\"color\":\"green\"},{\"text\":\"").append(playernamewhenreported).append("\",\"color\":\"red\"},{\"text\":\"\\n\"},").append("{\"text\":\"ServerID : \",\"color\":\"green\"},{\"text\":\"").append(serverID).append("\",\"color\":\"gold\"},").append("{\"text\":\" Timer on replay (approx.) : \",\"color\":\"green\"},{\"text\":\"").append(timeronreplay).append("\",\"color\":\"gold\"},{\"text\":\"\\n\"},").append("{\"text\":\"Timestamp for : \",\"color\":\"green\"},{\"text\":\"").append(cheats).append("\",\"color\":\"gold\"}").append((i == wdr.hacks.size() - 1) ? "" : ",{\"text\":\"\\n\"}");

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

        message.append("]}}");

        return new String[]{message.toString(), allCheats.toString()};
    }

}
