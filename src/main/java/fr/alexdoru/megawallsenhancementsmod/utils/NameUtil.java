package fr.alexdoru.megawallsenhancementsmod.utils;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.api.cache.PrestigeVCache;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GameProfileAccessor;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameUtil {

    private static final IChatComponent iprefix = new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix = iprefix.getFormattedText();
    private static final IChatComponent iprefix_bhop = new ChatComponentText(EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix_bhop = iprefix_bhop.getFormattedText();
    private static final IChatComponent iprefix_scan = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix_scan = iprefix_scan.getFormattedText();
    private static final IChatComponent isquadprefix = new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GREEN + "S" + EnumChatFormatting.GOLD + "] ");
    public static final String squadprefix = isquadprefix.getFormattedText();
    private static final List<IChatComponent> allPrefix = Arrays.asList(iprefix, iprefix_bhop, iprefix_scan, isquadprefix);
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern PATTERN_CLASS_TAG = Pattern.compile("\\[([A-Z]{3})\\]");

    /**
     * This updates the infos storred in GameProfile.MWPlayerData and refreshes the name in the tablist and the nametag
     */
    public static void updateGameProfileAndName(String playername) {
        NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(playername);
        if (networkPlayerInfo != null) {
            transformGameProfile(networkPlayerInfo.getGameProfile(), true);
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
            EntityPlayer player = mc.theWorld.getPlayerEntityByName(playername);
            if (player != null) {
                transformGameProfile(player.getGameProfile(), true);
                NameUtil.transformNametag(player, false);
            }
        } else {
            EntityPlayer player = mc.theWorld.getPlayerEntityByName(playername);
            if (player != null) {
                transformGameProfile(player.getGameProfile(), true);
                NameUtil.transformNametag(player, false);
            }
        }
    }

    /**
     * This updates the infos storred in GameProfile.MWPlayerData and refreshes the name in the tablist and the nametag
     */
    public static void updateGameProfileAndName(GameProfile gameProfile) {
        transformGameProfile(gameProfile, true);
        NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(gameProfile.getId());
        if (networkPlayerInfo != null) {
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(gameProfile));
        }
        EntityPlayer player = mc.theWorld.getPlayerEntityByName(gameProfile.getName());
        if (player != null) {
            NameUtil.transformNametag(player, false);
        }
    }

    /**
     * This updates the infos storred in GameProfile.MWPlayerData and refreshes the name in the tablist and the nametag
     */
    public static void updateGameProfileAndName(NetworkPlayerInfo networkPlayerInfo) {
        transformGameProfile(networkPlayerInfo.getGameProfile(), true);
        networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
        EntityPlayer player = mc.theWorld.getPlayerEntityByName(networkPlayerInfo.getGameProfile().getName());
        if (player != null) {
            NameUtil.transformNametag(player, false);
        }
    }

    /**
     * Transforms the nametag of the player based on the infos stored in getGameProfile.MWPlayerData
     * to save performance instead of redoing the hashmap access
     */
    public static void transformNametag(EntityPlayer player, boolean onPlayerJoin) {

        if (!onPlayerJoin) {
            player.getPrefixes().removeAll(allPrefix);
            player.refreshDisplayName();
        }

        if (player instanceof EntityPlayerSP) {
            transformGameProfile(player.getGameProfile(), true);
        }

        MWPlayerData mwPlayerData = ((GameProfileAccessor) player.getGameProfile()).getMWPlayerData();

        if (mwPlayerData == null) {
            return;
        }

        if (mwPlayerData.extraPrefix != null) {
            player.addPrefix(mwPlayerData.extraPrefix);
            player.refreshDisplayName();
        }

        if (mwPlayerData.squadname != null) {
            return;
        }

        if (onPlayerJoin && mwPlayerData.wdr != null && mwPlayerData.wdr.isCheating()) { // player was reported
            String playerName = player.getName();
            long datenow = (new Date()).getTime();
            boolean gotautoreported = NoCheatersEvents.sendAutoReport(datenow, playerName, mwPlayerData.wdr);
            if (ConfigHandler.togglewarnings || (ConfigHandler.toggleautoreport && mwPlayerData.wdr.isOlderThanMaxAutoreport(datenow))) {
                String uuid = player.getUniqueID().toString().replace("-", "");
                ChatUtil.addChatMessage(NoCheatersEvents.createwarningmessage(datenow, uuid, playerName, mwPlayerData.wdr, gotautoreported));
            }
        }

    }

    public static void transformNameTablist(UUID uuid) {
        NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(uuid);
        if (networkPlayerInfo != null) {
            transformGameProfile(networkPlayerInfo.getGameProfile(), true);
            networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
        }
    }

    /**
     * Transforms the infos storred in GameProfile.MWPlayerData
     * For each new player spawned in the world it will create a new networkplayerinfo instance a rerun all the code in the method
     * to generate the field MWPlayerData, however it will reuse the field to display the nametag
     */
    public static void transformGameProfile(GameProfile gameProfileIn, boolean forceRefresh) {

        GameProfileAccessor gameProfileAccessor = (GameProfileAccessor) gameProfileIn;
        MWPlayerData mwPlayerData = gameProfileAccessor.getMWPlayerData();
        UUID id = gameProfileIn.getId();

        if (mwPlayerData == null && !forceRefresh) {
            MWPlayerData cachedMWPlayerData = MWPlayerData.dataCache.get(id);
            if (cachedMWPlayerData != null) {
                gameProfileAccessor.setMWPlayerData(cachedMWPlayerData);
                return;
            }
        }

        if (mwPlayerData == null || forceRefresh) {

            String username = gameProfileIn.getName();
            String uuid = id.toString().replace("-", "");
            WDR wdr = WdredPlayers.getWdredMap().get(uuid);
            String extraPrefix = "";
            IChatComponent iExtraPrefix = null;
            String squadname = SquadEvent.getSquad().get(username);
            boolean isSquadMate = squadname != null;

            if (wdr == null) {
                wdr = WdredPlayers.getWdredMap().get(username);
                if (wdr != null) {
                    uuid = username;
                }
            }

            if (ConfigHandler.toggleicons) {

                if (isSquadMate) {

                    extraPrefix = squadprefix;
                    iExtraPrefix = isquadprefix;

                } else {

                    if (wdr != null) {

                        if (wdr.isCheating()) {

                            if (wdr.hacks.contains("bhop")) {
                                extraPrefix = prefix_bhop;
                                iExtraPrefix = iprefix_bhop;
                            } else {
                                extraPrefix = prefix;
                                iExtraPrefix = iprefix;
                            }

                        }

                    } else { //scangame

                        if (CommandScanGame.doesPlayerFlag(uuid)) {
                            extraPrefix = prefix_scan;
                            iExtraPrefix = iprefix_scan;
                        }

                    }

                }

            }

            IChatComponent displayName = null;
            String formattedPrestigeVstring = null;
            String colorSuffix = null;
            if (mc.theWorld != null) {
                ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(username);
                if (team != null) {
                    String teamprefix = team.getColorPrefix();
                    colorSuffix = team.getColorSuffix();
                    if (ConfigHandler.prestigeV && colorSuffix != null && colorSuffix.contains(EnumChatFormatting.GOLD.toString())) {
                        Matcher matcher = PATTERN_CLASS_TAG.matcher(colorSuffix);
                        if (matcher.find()) {
                            String tag = matcher.group(1);
                            EnumChatFormatting prestigeVcolor = PrestigeVCache.checkCacheAndUpdate(uuid, gameProfileIn.getName(), tag);
                            if (prestigeVcolor != null) {
                                formattedPrestigeVstring = " " + prestigeVcolor + "[" + tag + "]";
                            }
                        }
                    }

                    boolean isobf = teamprefix.contains("\u00a7k");
                    if (iExtraPrefix != null || formattedPrestigeVstring != null) {
                        displayName = new ChatComponentText(
                                (isobf ? "" : extraPrefix)
                                        + teamprefix
                                        + (isSquadMate ? squadname : username)
                                        + (formattedPrestigeVstring != null ? formattedPrestigeVstring : colorSuffix
                                ));
                    }
                }
            }

            if (mwPlayerData == null) {
                gameProfileAccessor.setMWPlayerData(new MWPlayerData(id, wdr, iExtraPrefix, squadname, displayName, colorSuffix, formattedPrestigeVstring));
            } else {
                mwPlayerData.setData(id, wdr, iExtraPrefix, squadname, displayName, colorSuffix, formattedPrestigeVstring);
            }

        }

    }

    public static IChatComponent getTransformedDisplayName(GameProfile gameProfileIn) {
        MWPlayerData mwPlayerData = ((GameProfileAccessor) gameProfileIn).getMWPlayerData();
        if (mwPlayerData != null) {
            return mwPlayerData.displayName;
        }
        return null;
    }

    public static void refreshAllNamesInWorld() {
        mc.getNetHandler().getPlayerInfoMap().forEach(NameUtil::updateGameProfileAndName);
    }

    /**
     * Returns true if it's the uuid of an NPC
     * from experimentation, nicks are v1 and real players v4
     */
    public static boolean filterNPC(UUID uuid) {
        return uuid.version() == 2;
    }

    public static boolean isRealPlayer(UUID uuid) {
        return uuid.version() == 4;
    }

}
