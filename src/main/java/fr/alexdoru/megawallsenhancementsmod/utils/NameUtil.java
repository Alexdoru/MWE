package fr.alexdoru.megawallsenhancementsmod.utils;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GameProfileAccessor;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
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

import java.util.*;

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

    private static final HashMap<String, NetworkPlayerInfo> playerInfoMap = new HashMap<>();

    /**
     * Method call is inject by NetHandlerPlayClientTransformer
     */
    public static void putPlayerInMap(String playerName, NetworkPlayerInfo networkplayerinfo) {
        playerInfoMap.put(playerName, networkplayerinfo);
    }

    /**
     * Method call is inject by NetHandlerPlayClientTransformer
     */
    public static NetworkPlayerInfo removePlayerFromMap(String playerName) {
        return playerInfoMap.remove(playerName);
    }

    public static NetworkPlayerInfo getPlayerInfo(String playerName) {
        return playerInfoMap.get(playerName);
    }

    /**
     * This updates the infos storred in GameProfile.MWPlayerData and refreshes the name in the tablist and the nametags
     */
    public static void updateGameProfileAndName(String playername) {
        NetworkPlayerInfo networkPlayerInfo = playerInfoMap.get(playername);
        if (networkPlayerInfo != null) {
            transformGameProfile(networkPlayerInfo.getGameProfile());
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
            EntityPlayer player = mc.theWorld.getPlayerEntityByName(playername);
            if (player != null) {
                NameUtil.removeNametagIcons(player);
                NameUtil.transformNametag(player, ConfigHandler.toggleicons, false, false);
            }
        }
    }

    /**
     * This updates the infos storred in GameProfile.MWPlayerData and refreshes the name in the tablist and the nametags
     */
    public static void updateGameProfileAndName(GameProfile gameProfile, boolean areIconsToggled) {
        transformGameProfile(gameProfile);
        NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(gameProfile.getId());
        if (networkPlayerInfo != null) {
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(gameProfile));
        }
        EntityPlayer player = mc.theWorld.getPlayerEntityByName(gameProfile.getName());
        if (player != null) {
            NameUtil.removeNametagIcons(player);
            NameUtil.transformNametag(player, areIconsToggled, false, false);
        }
    }

    public static void transformNametag(EntityPlayer player, boolean areIconsToggled, boolean areWarningsToggled, boolean checkAutoreport) {

        if (!(player.getGameProfile() instanceof GameProfileAccessor)) {
            return;
        }

        MWPlayerData mwPlayerData = ((GameProfileAccessor) player.getGameProfile()).getMWPlayerData();

        // For self it is null
        if (mwPlayerData == null) {
            transformGameProfile(player.getGameProfile());
            mwPlayerData = ((GameProfileAccessor) player.getGameProfile()).getMWPlayerData();
            if (mwPlayerData == null) {
                return;
            }
        }

        if (areIconsToggled && mwPlayerData.squadname != null) {
            player.addPrefix(isquadprefix);
            player.refreshDisplayName();
            return;
        }

        String uuid = player.getUniqueID().toString().replace("-", "");
        String playerName = player.getName();
        long datenow = (new Date()).getTime();

        if (mwPlayerData.wdr != null) { // player was reported

            boolean gotautoreported = checkAutoreport && NoCheatersEvents.sendAutoReport(datenow, playerName, mwPlayerData.wdr);

            if (mwPlayerData.wdr.hacks.contains("bhop")) { // player bhops
                if (areIconsToggled) {
                    player.addPrefix(iprefix_bhop);
                    player.refreshDisplayName();
                }
            } else {
                if (areIconsToggled) {
                    player.addPrefix(iprefix);
                    player.refreshDisplayName();
                }
            }

            if (areWarningsToggled) {
                mc.thePlayer.addChatComponentMessage(IChatComponent.Serializer.jsonToComponent(NoCheatersEvents.createwarningmessage(datenow, uuid, playerName, mwPlayerData.wdr, gotautoreported)));
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

    /**
     * Method call is inject by Scoreboard
     */
    public static void transformNameTablist(String playername) {
        NetworkPlayerInfo networkPlayerInfo = playerInfoMap.get(playername);
        if (networkPlayerInfo != null) {
            transformGameProfile(networkPlayerInfo.getGameProfile());
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
        }
    }

    public static void transformNameTablist(UUID uuid) {
        NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(uuid);
        if (networkPlayerInfo != null) {
            transformGameProfile(networkPlayerInfo.getGameProfile());
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
        }
    }

    /**
     * Called in constructor of NetworkPlayerInfo
     */
    public static void transformGameProfile(GameProfile gameProfileIn) {

        if (!(gameProfileIn instanceof GameProfileAccessor)) {
            return;
        }

        String username = gameProfileIn.getName();
        String uuid = gameProfileIn.getId().toString().replace("-", "");
        String extraprefix = "";
        WDR wdr = null;
        String squadname = SquadEvent.getSquad().get(username);
        boolean isSquadMate = squadname != null;

        if (ConfigHandler.toggleicons) {

            if (isSquadMate) {

                extraprefix = squadprefix;

            } else {

                wdr = WdredPlayers.getWdredMap().get(uuid);

                if (wdr == null) {
                    wdr = WdredPlayers.getWdredMap().get(username);
                    if (wdr != null) {
                        uuid = username;
                    }
                }

                if (wdr != null) {

                    if (wdr.hacks.contains("bhop")) {
                        extraprefix = prefix_bhop;
                    } else {
                        extraprefix = prefix;
                    }

                } else { //scangame

                    IChatComponent imsg = CommandScanGame.getScanmap().get(uuid);

                    if (imsg != null && !imsg.equals(CommandScanGame.nomatch)) {
                        extraprefix = prefix_scan;
                    }

                }

            }

        }

        ((GameProfileAccessor) gameProfileIn).setMWPlayerData(new MWPlayerData(wdr, extraprefix, squadname, KillCounter.getPlayersFinals(username)));

    }

    public static IChatComponent getTransformedDisplayName(GameProfile gameProfileIn) {
        if (gameProfileIn instanceof GameProfileAccessor) {
            ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(gameProfileIn.getName());
            if (team != null) {
                String teamprefix = team.getColorPrefix();
                MWPlayerData mwPlayerData = ((GameProfileAccessor) gameProfileIn).getMWPlayerData();
                if (mwPlayerData != null && !(teamprefix.contains("\u00a7k") && mwPlayerData.extraPrefix != null)) {
                    return new ChatComponentText(mwPlayerData.extraPrefix + teamprefix
                            + (mwPlayerData.squadname != null ? mwPlayerData.squadname : gameProfileIn.getName())
                            + team.getColorSuffix());
                }
            }
        }
        return null;
    }

    public static void toggleIcons() {
        ConfigHandler.toggleicons = !ConfigHandler.toggleicons;
        if (ConfigHandler.toggleicons) {
            mc.theWorld.playerEntities.forEach(playerEntity -> updateGameProfileAndName(playerEntity.getGameProfile(), true));
        } else {
            mc.theWorld.playerEntities.forEach(playerEntity -> {
                NameUtil.removeNametagIcons(playerEntity);
                NameUtil.transformNameTablist(playerEntity.getUniqueID());
            });
        }
    }

}
