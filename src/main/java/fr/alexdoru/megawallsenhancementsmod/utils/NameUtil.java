package fr.alexdoru.megawallsenhancementsmod.utils;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static fr.alexdoru.nocheatersmod.events.NoCheatersEvents.nbReport;

public class NameUtil {

    private static final IChatComponent iprefix = new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix = iprefix.getFormattedText();
    private static final IChatComponent iprefix_bhop = new ChatComponentText(EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    private static final String prefix_bhop = iprefix_bhop.getFormattedText();
    private static final IChatComponent iprefix_scan = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    private static final String prefix_scan = iprefix_scan.getFormattedText();
    private static final IChatComponent isquadprefix = new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GREEN + "S" + EnumChatFormatting.GOLD + "] ");
    public static final String squadprefix = isquadprefix.getFormattedText();
    private static final List<IChatComponent> allPrefix = Arrays.asList(iprefix, iprefix_bhop, iprefix_scan, isquadprefix);
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void handlePlayer(String playername) {
        EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(playername);
        if (player != null) {
            NameUtil.removeNametagIcons(player);
            NameUtil.handlePlayer(player, true, false, false);
        }
    }

    public static void handlePlayer(EntityPlayer player, boolean areIconsToggled, boolean areWarningsToggled, boolean isAutoreportToggled) {

        String playerName = player.getName();
        String squadname = SquadEvent.getSquad().get(playerName);

        if (areIconsToggled && squadname != null) {
            player.addPrefix(isquadprefix);
            player.refreshDisplayName();
            return;
        }

        String uuid = player.getUniqueID().toString().replace("-", "");
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

            if (isAutoreportToggled && datenow - wdr.timestamp > ConfigHandler.timeBetweenReports && datenow - wdr.timestamp < ConfigHandler.timeAutoReport) {
                String finalUuid = uuid;
                new DelayedTask(() -> {
                    ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/sendreportagain " + finalUuid + " " + playerName);
                    nbReport--;
                }, 20 * nbReport);
                nbReport++;
            }

            if (wdr.hacks.contains("bhop")) { // player bhops
                if (areIconsToggled) {
                    player.addPrefix(iprefix_bhop);
                    player.refreshDisplayName();
                }
                printmsg = true;
            } else if (!(wdr.isOnlyStalking())) { // player is cheating
                if (areIconsToggled) {
                    player.addPrefix(iprefix);
                    player.refreshDisplayName();
                }
                printmsg = true;
            }

            if (areWarningsToggled && printmsg) {
                String chatmessage = NoCheatersEvents.createwarningmessage(datenow, uuid, playerName, wdr);
                mc.thePlayer.addChatComponentMessage(IChatComponent.Serializer.jsonToComponent(chatmessage));
            }

        } else if (areIconsToggled) { // check the scangame map

            IChatComponent imsg = CommandScanGame.getScanmap().get(uuid);
            if (imsg != null && !imsg.equals(CommandScanGame.nomatch)) {
                player.addPrefix(iprefix_scan);
                player.refreshDisplayName();
            }

        }

    }

    public static void removeNametagIcons(EntityPlayer player) {
        player.getPrefixes().removeAll(allPrefix);
        player.refreshDisplayName();
    }

    public static void transformNameTablist(String playername) {
        NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(playername);
        if (networkPlayerInfo != null) {
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo));
        }
    }

    public static IChatComponent getTransformedDisplayName(NetworkPlayerInfo networkPlayerInfo) {
        return getTransformedDisplayName(networkPlayerInfo.getGameProfile(), networkPlayerInfo.getDisplayName());
    }

    /*
     * replaces the names of squadmates
     * adds a tag to squadmates
     * adds a tag to reported players
     * unscrambles the tablist
     */
    public static IChatComponent getTransformedDisplayName(GameProfile gameProfile, IChatComponent displayName) {

        // public void handleTeams(S3EPacketTeams packetIn) in NetHandlerPlayClient
        // add a hook to transform the names
        // 0 : create a team
        // 1 : remove a team
        // 2 : Set team displayname/prefix/suffix and/or whether friendly fire is enabled
        // 3 : add players to team
        // 4 : remove players from team

        //***************************

        // TODO transform les player name sur les paquets de team
        // ca laisse des croix devant les pseudo quand qqu deco reco en mw
        // ca conserve pas les couleurs des teams en miniwalls
        // en miniwalls the display name est null pendant la game

        String username = gameProfile.getName();
        String uuid = gameProfile.getId().toString().replace("-", "");
        String extraprefix = "";
        boolean needtochange = false;

        String squadname = SquadEvent.getSquad().get(username);
        boolean isSquadMate = squadname != null;

        if (ConfigHandler.toggleicons) {

            if (isSquadMate) {

                extraprefix = squadprefix;
                needtochange = true;

            } else {

                WDR wdr = WdredPlayers.getWdredMap().get(uuid);

                if (wdr == null) {
                    wdr = WdredPlayers.getWdredMap().get(username);
                    if (wdr != null) {
                        uuid = username;
                    }
                }

                if (wdr != null) {

                    if (!wdr.isOnlyStalking()) {

                        if (wdr.hacks.contains("bhop")) {
                            extraprefix = prefix_bhop;
                        } else {
                            extraprefix = prefix;
                        }

                    }

                    needtochange = true;

                } else { //scangame

                    IChatComponent imsg = CommandScanGame.getScanmap().get(uuid);

                    if (imsg != null && !imsg.equals(CommandScanGame.nomatch)) {
                        extraprefix = prefix_scan;
                        needtochange = true;
                    }

                }

            }

        }

        if (displayName != null) {

            String formattedname = displayName.getFormattedText().replace(squadprefix, "").replace(prefix_bhop, "").replace(prefix, "").replace(prefix_scan, "");

            if (needtochange) {
                return new ChatComponentText(extraprefix).appendSibling(new ChatComponentText((isSquadMate ? formattedname.replace(username, squadname).replace("\u00a7k", "") : formattedname.replace("\u00a7k", ""))));
            } else {
                return new ChatComponentText(formattedname.replace("\u00a7k", ""));
            }

        }

        ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(username);

        if (team != null) {

            String teamprefix = team.getColorPrefix();

            if (teamprefix.contains("\u00a7k") || needtochange) {
                return new ChatComponentText(extraprefix + teamprefix.replace("\u00a7k", "").replace("O", "") + (isSquadMate ? squadname : username) + team.getColorSuffix());
            }

        }

        return null;

    }

}
