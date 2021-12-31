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
import java.util.UUID;

import static fr.alexdoru.nocheatersmod.events.NoCheatersEvents.nbReport;

public class NameUtil {

    public static final IChatComponent iprefix = new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix = iprefix.getFormattedText();
    public static final IChatComponent iprefix_bhop = new ChatComponentText(EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix_bhop = iprefix_bhop.getFormattedText();
    private static final IChatComponent iprefix_scan = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix_scan = iprefix_scan.getFormattedText();
    private static final IChatComponent isquadprefix = new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GREEN + "S" + EnumChatFormatting.GOLD + "] ");
    public static final String squadprefix = isquadprefix.getFormattedText();
    private static final List<IChatComponent> allPrefix = Arrays.asList(iprefix, iprefix_bhop, iprefix_scan, isquadprefix);
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void handlePlayer(String playername) {
        EntityPlayer player = mc.theWorld.getPlayerEntityByName(playername);
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
        long datenow = (new Date()).getTime();

        if (wdr != null) { // player was reported

            boolean gotautoreported = false;

            if (isAutoreportToggled && datenow - wdr.timestamp > ConfigHandler.timeBetweenReports && datenow - wdr.timestamp < ConfigHandler.timeAutoReport) {
                new DelayedTask(() -> {
                    ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/sendreportagain " + uuid + " " + playerName);
                    nbReport--;
                }, 20 * nbReport);
                nbReport++;
                gotautoreported = true;
            }

            if (wdr.hacks.contains("bhop")) { // player bhops
                if (areIconsToggled) {
                    player.addPrefix(iprefix_bhop);
                    player.refreshDisplayName();
                }
            } else { // player is cheating
                if (areIconsToggled) {
                    player.addPrefix(iprefix);
                    player.refreshDisplayName();
                }
            }

            if (areWarningsToggled) {
                mc.thePlayer.addChatComponentMessage(IChatComponent.Serializer.jsonToComponent(NoCheatersEvents.createwarningmessage(datenow, uuid, playerName, wdr, gotautoreported)));
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
        // TODO O(n) mais il y a une map <UUID, Networkplayerinfo>
        // TODO faire une version miroir de la playerinfo map String name , networkplayerninfo ?
        // TODO injecter le config check toggleIcons avant de call les fonctions

        NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(playername);
        if (networkPlayerInfo != null) {
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo));
        }
    }

    public static void transformNameTablist(UUID uuid) {
        NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(uuid);
        if (networkPlayerInfo != null) {
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo));
        }
    }

    public static IChatComponent getTransformedDisplayName(NetworkPlayerInfo networkPlayerInfo) {
        return getTransformedDisplayName(networkPlayerInfo.getGameProfile());
    }

    /*
     * replaces the names of squadmates
     * adds a tag to squadmates
     * adds a tag to reported players
     * unscrambles the tablist
     */
    public static IChatComponent getTransformedDisplayName(GameProfile gameProfile) {

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

                if (wdr != null) {

                    if (wdr.hacks.contains("bhop")) {
                        extraprefix = prefix_bhop;
                    } else {
                        extraprefix = prefix;
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

        ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(username);

        if (team != null) {

            String teamprefix = team.getColorPrefix();

            if (!teamprefix.contains("\u00a7k") && needtochange) {
                return new ChatComponentText(extraprefix + teamprefix + (isSquadMate ? squadname : username) + team.getColorSuffix());
            }

        }

        return null;

    }

}
