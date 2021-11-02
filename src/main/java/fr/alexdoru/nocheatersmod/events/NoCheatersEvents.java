package fr.alexdoru.nocheatersmod.events;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.nocheatersmod.NoCheatersMod;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Date;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.getTagNoCheaters;

public class NoCheatersEvents {

    private static int ticks = 0;
    public static final IChatComponent iprefix = new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix = iprefix.getFormattedText();
    public static final IChatComponent iprefix_bhop = new ChatComponentText(EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix_bhop = iprefix_bhop.getFormattedText();
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onGui(GuiOpenEvent event) { //resets the ticks counter when you have a loading screen
        if (event.gui instanceof GuiDownloadTerrain)
            ticks = 0;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {  // scans your world 2 seconds avec joining // TODO ajouter un decompte pour pas que ca envoie toutes les commandes auto report en meme temps
        if (!NoCheatersMod.areWarningsToggled() || !mc.inGameHasFocus) {
            return;
        }
        if (ticks == 39) {
            scanCurrentWorld();
        } else if (ticks < 39) {
            ticks++;
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) { // check chaque nouveau joueur qui est rendu dans le jeu

        // TODO ca met le triangle scangame sur le mec en squad

        if (ticks < 40 || mc.thePlayer == null || !(event.entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entity;
        String uuid = player.getUniqueID().toString().replace("-", "");
        String playerName = player.getName();
        WDR wdr = WdredPlayers.getWdredMap().get(uuid);
        boolean printmsg = false;
        long datenow = (new Date()).getTime();

        if (wdr == null) {
            wdr = WdredPlayers.getWdredMap().get(playerName);
            if (wdr != null) {
                uuid = playerName;
            }
        }

        if (wdr != null) { // player was reported

            if (NoCheatersMod.isAutoreportToggled() && datenow - wdr.timestamp > NoCheatersMod.getTimebetweenreports() && datenow - wdr.timestamp < NoCheatersMod.getTimeautoreport()) {
                ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/sendreportagain " + uuid + " " + playerName);
            }

            if (wdr.hacks.contains("bhop")) { // player bhops
                if (NoCheatersMod.areIconsToggled()) {
                    player.addPrefix(iprefix_bhop);
                    player.refreshDisplayName();
                }
                printmsg = true;
            } else if (!(wdr.isOnlyStalking())) { // player is cheating
                if (NoCheatersMod.areIconsToggled()) {
                    player.addPrefix(iprefix);
                    player.refreshDisplayName();
                }
                printmsg = true;
            }

            if (NoCheatersMod.areWarningsToggled() && printmsg) {
                String chatmessage = createwarningmessage(datenow, uuid, playerName, wdr);
                mc.thePlayer.addChatComponentMessage(IChatComponent.Serializer.jsonToComponent(chatmessage));
            }

        } else if (NoCheatersMod.areIconsToggled()) { // check the scangame map

            IChatComponent imsg = CommandScanGame.getScanmap().get(uuid);
            if (imsg != null && !imsg.equals(CommandScanGame.nomatch)) {
                player.addPrefix(CommandScanGame.iprefix);
                player.refreshDisplayName();
            }

        }

    }

    public static void scanCurrentWorld() { // TODO ajouter le autoreport

        long timenow = (new Date()).getTime();

        try {

            for (NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {

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

                String chatmessage = createwarningmessage(timenow, uuid, playerName, wdr);
                (Minecraft.getMinecraft()).thePlayer.addChatComponentMessage(IChatComponent.Serializer.jsonToComponent(chatmessage));
            }

        } catch (Exception exception) {

            addChatMessage(new ChatComponentText(getTagNoCheaters() +
                    EnumChatFormatting.RED + "Error, scan incomplete"));
        }

        ticks++;
    }

    public static String createwarningmessage(long datenow, String uuid, String playername, WDR wdr) {
        // format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc
        if (wdr.hacks.get(0).charAt(0) == '-') { // is a timestamped report

            String[] formattedmessageArray = createPlayerTimestampedMsg(playername, wdr, "light_purple");
            String allCheats = formattedmessageArray[1];

            String message = "[\"\",{\"text\":\"Warning : \",\"color\":\"red\"}" + formattedmessageArray[0] + ",{\"text\":\" joined,\",\"color\":\"gray\"}";

            if (datenow - wdr.timestamp > NoCheatersMod.getTimebetweenreports()) { // montre le bouton pour re-report si l'ancien report est plus vieux que X heures

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

            if (datenow - wdr.timestamp > NoCheatersMod.getTimebetweenreports() && !(wdr.isOnlyStalking())) { // montre le bouton pour re-report si l'ancien report est plus vieux que X heures

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
