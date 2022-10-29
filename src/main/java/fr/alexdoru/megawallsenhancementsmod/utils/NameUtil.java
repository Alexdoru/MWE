package fr.alexdoru.megawallsenhancementsmod.utils;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GameProfileAccessor;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.WarningMessagesHandler;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.*;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameUtil {

    private static final IChatComponent iprefix_old_report = new ChatComponentText(EnumChatFormatting.GRAY + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix_old_report = iprefix_old_report.getFormattedText();
    private static final IChatComponent iprefix = new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix = iprefix.getFormattedText();
    private static final IChatComponent iprefix_bhop = new ChatComponentText(EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix_bhop = iprefix_bhop.getFormattedText();
    private static final IChatComponent iprefix_scan = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "\u26a0 ");
    public static final String prefix_scan = iprefix_scan.getFormattedText();
    private static final IChatComponent isquadprefix = new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GREEN + "S" + EnumChatFormatting.GOLD + "] ");
    public static final String squadprefix = isquadprefix.getFormattedText();
    private static final List<IChatComponent> allPrefix = Arrays.asList(iprefix_old_report, iprefix, iprefix_bhop, iprefix_scan, isquadprefix);
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern PATTERN_CLASS_TAG = Pattern.compile("\\[([A-Z]{3})\\]");

    /**
     * This updates the infos storred in GameProfile.MWPlayerData for the player : playername
     * and refreshes the name in the tablist and the nametag
     * set refreshDisplayName to true to fire the NameFormat Event and
     * update the name of the player in case you change it via a command
     * for example : /squad add player as aliasname
     */
    public static void updateGameProfileAndName(String playername, boolean refreshDisplayName) {
        if (isValidMinecraftName(playername)) {
            final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(playername);
            if (networkPlayerInfo != null) {
                transformGameProfile(networkPlayerInfo.getGameProfile(), true);
                networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
            }
            final EntityPlayer player = mc.theWorld.getPlayerEntityByName(playername);
            if (player != null) {
                transformGameProfile(player.getGameProfile(), true);
                NameUtil.transformNametag(player, false);
                if (refreshDisplayName) {
                    player.refreshDisplayName();
                }
            }
        }
    }

    /**
     * This updates the infos storred in GameProfile.MWPlayerData and refreshes the name in the tablist and the nametag
     */
    public static void updateGameProfileAndName(NetworkPlayerInfo networkPlayerInfo) {
        transformGameProfile(networkPlayerInfo.getGameProfile(), true);
        networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
        final EntityPlayer player = mc.theWorld.getPlayerEntityByName(networkPlayerInfo.getGameProfile().getName());
        if (player != null) {
            NameUtil.transformNametag(player, false);
        }
    }

    private static final Pattern MINECRAFT_NAME_PATTERN = Pattern.compile("\\w{1,16}");
    public static boolean isValidMinecraftName(String playername) {
        return !StringUtil.isNullOrEmpty(playername) && MINECRAFT_NAME_PATTERN.matcher(playername).matches();
    }

    public static void onScoreboardPacket(String playername) {
        if (isValidMinecraftName(playername)) {
            final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(playername);
            if (networkPlayerInfo != null) {
                transformGameProfile(networkPlayerInfo.getGameProfile(), true);
                networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
            }
        }
    }

    /**
     * Transforms the nametag of the player based on the infos stored in getGameProfile.MWPlayerData
     * to save performance instead of redoing the hashmap access
     */
    public static void transformNametag(EntityPlayer player, boolean onPlayerJoin) {

        if (!onPlayerJoin) {
            player.getPrefixes().removeAll(allPrefix);
        }

        if (player instanceof EntityPlayerSP) {
            transformGameProfile(player.getGameProfile(), true);
        }

        final MWPlayerData mwPlayerData = ((GameProfileAccessor) player.getGameProfile()).getMWPlayerData();

        if (mwPlayerData == null) {
            return;
        }

        if (mwPlayerData.extraPrefix != null) {
            player.addPrefix(mwPlayerData.extraPrefix);
        }

        if (onPlayerJoin && mwPlayerData.wdr != null && mwPlayerData.wdr.transformName()) { // player was reported
            final long datenow = (new Date()).getTime();
            final String playerName = player.getName();
            final boolean gotautoreported = ReportQueue.INSTANCE.addAutoReportToQueue(datenow, playerName, mwPlayerData.wdr);
            if (ConfigHandler.warningMessages || mwPlayerData.wdr.shouldPrintBigText(datenow)) {
                final String uuid = player.getUniqueID().toString().replace("-", "");
                ChatHandler.deleteWarningMessagesFor(playerName);
                WarningMessagesHandler.printWarningMessage(
                        datenow,
                        uuid,
                        (!FKCounterMod.isInMwGame || FKCounterMod.isitPrepPhase) ? null : ScorePlayerTeam.formatPlayerName(player.getTeam(), playerName),
                        playerName,
                        mwPlayerData.wdr,
                        gotautoreported
                );
            }
        }

    }

    /**
     * Transforms the infos storred in GameProfile.MWPlayerData
     * For each new player spawned in the world it will create a new networkplayerinfo instance a rerun all the code in the method
     * to generate the field MWPlayerData, however it will reuse the field to display the nametag
     */
    public static void transformGameProfile(GameProfile gameProfileIn, boolean forceRefresh) {

        final GameProfileAccessor gameProfileAccessor = (GameProfileAccessor) gameProfileIn;
        final MWPlayerData mwPlayerData = gameProfileAccessor.getMWPlayerData();
        final UUID id = gameProfileIn.getId();

        if (!forceRefresh) {
            if (mwPlayerData == null) {
                final MWPlayerData cachedMWPlayerData = MWPlayerData.dataCache.get(id);
                if (cachedMWPlayerData != null) {
                    gameProfileAccessor.setMWPlayerData(cachedMWPlayerData);
                    return;
                }
            } else {
                return;
            }
        }

        final String username = gameProfileIn.getName();
        final String uuid = id.toString().replace("-", "");
        final WDR wdr = WdrData.getWdr(uuid, username);
        String extraPrefix = "";
        IChatComponent iExtraPrefix = null;
        final String squadname = SquadHandler.getSquad().get(username);
        final boolean isSquadMate = squadname != null;

        if (ConfigHandler.iconsOnNames) {
            if (isSquadMate) {
                extraPrefix = squadprefix;
                iExtraPrefix = isquadprefix;
            } else {
                if (wdr != null) {
                    if (wdr.transformName()) {
                        if (wdr.shouldPutGrayIcon()) {
                            extraPrefix = prefix_old_report;
                            iExtraPrefix = iprefix_old_report;
                        } else {
                            if (wdr.hacks.contains("bhop")) {
                                extraPrefix = prefix_bhop;
                                iExtraPrefix = iprefix_bhop;
                            } else {
                                extraPrefix = prefix;
                                iExtraPrefix = iprefix;
                            }
                        }
                    }
                } else {
                    if (ScangameData.doesPlayerFlag(id)) {
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
            final ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(username);
            if (team != null) {
                final String teamprefix = team.getColorPrefix();
                colorSuffix = team.getColorSuffix();
                if (ConfigHandler.prestigeV && colorSuffix != null && colorSuffix.contains(EnumChatFormatting.GOLD.toString())) {
                    final Matcher matcher = PATTERN_CLASS_TAG.matcher(colorSuffix);
                    if (matcher.find()) {
                        final String tag = matcher.group(1);
                        final EnumChatFormatting prestigeVcolor = PrestigeVCache.checkCacheAndUpdate(uuid, gameProfileIn.getName(), tag);
                        if (prestigeVcolor != null) {
                            formattedPrestigeVstring = " " + prestigeVcolor + "[" + tag + "]";
                        }
                    }
                }

                final boolean isobf = teamprefix.contains("\u00a7k");
                final String alias = AliasData.getAlias(username);
                if (iExtraPrefix != null || formattedPrestigeVstring != null || alias != null) {
                    displayName = new ChatComponentText(
                            (isobf ? "" : extraPrefix)
                                    + teamprefix
                                    + (isSquadMate ? squadname : username)
                                    + (formattedPrestigeVstring != null ? formattedPrestigeVstring : colorSuffix)
                                    + (alias == null ? "" : EnumChatFormatting.RESET + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.RESET + ")")
                    );
                }
            }
        }

        if (mwPlayerData == null) {
            gameProfileAccessor.setMWPlayerData(new MWPlayerData(id, wdr, iExtraPrefix, squadname, displayName, colorSuffix, formattedPrestigeVstring));
        } else {
            mwPlayerData.setData(id, wdr, iExtraPrefix, squadname, displayName, colorSuffix, formattedPrestigeVstring);
        }

    }

    /**
     * Returns the formatted team name with additionnaly a custom prestige V tag
     * This doesn't return the icons in front that the player may have.
     */
    public static String getFormattedNameWithoutIcons(String playername) {
        final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(playername);
        if (networkPlayerInfo == null) {
            return playername;
        }
        final MWPlayerData mwPlayerData = MWPlayerData.dataCache.get(networkPlayerInfo.getGameProfile().getId());
        if (mwPlayerData != null && mwPlayerData.P5Tag != null && mwPlayerData.originalP4Tag != null) {
            return ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), playername).replace(mwPlayerData.originalP4Tag, mwPlayerData.P5Tag);
        }
        return ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), playername);
    }

    /**
     * Returns the formatted name of the player, additionnal icons and prestive V tag included
     * Same method that the one in {@link net.minecraft.client.gui.GuiPlayerTabOverlay}
     */
    public static String getFormattedName(String playername) {
        final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(playername);
        if (networkPlayerInfo == null) {
            return playername;
        }
        return getFormattedName(networkPlayerInfo);
    }

    public static String getFormattedName(NetworkPlayerInfo networkPlayerInfo) {
        if (networkPlayerInfo.getDisplayName() == null) {
            return ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), networkPlayerInfo.getGameProfile().getName());
        }
        return networkPlayerInfo.getDisplayName().getFormattedText();
    }

    /**
     * Used for /scangame
     */
    public static IChatComponent getFormattedNameWithPlanckeClickEvent(NetworkPlayerInfo networkPlayerInfoIn) {
        final String formattedName;
        if (networkPlayerInfoIn.getPlayerTeam() == null) {
            formattedName = networkPlayerInfoIn.getGameProfile().getName();
        } else {
            final ScorePlayerTeam team = networkPlayerInfoIn.getPlayerTeam();
            formattedName = team.getColorPrefix().replace("\u00a7k", "").replace("O", "") + networkPlayerInfoIn.getGameProfile().getName() + team.getColorSuffix();
        }
        return new ChatComponentText(formattedName)
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to see the mega walls stats of that player")))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plancke " + networkPlayerInfoIn.getGameProfile().getName() + " mw")));
    }

    public static IChatComponent getTransformedDisplayName(GameProfile gameProfileIn) {
        final MWPlayerData mwPlayerData = ((GameProfileAccessor) gameProfileIn).getMWPlayerData();
        if (mwPlayerData != null) {
            return mwPlayerData.displayName;
        }
        return null;
    }

    public static void refreshAllNamesInWorld() {
        mc.getNetHandler().getPlayerInfoMap().forEach(p -> {
            if (p != null) {
                NameUtil.updateGameProfileAndName(p.getGameProfile().getName(), true);
            }
        });
    }

    /**
     * Returns true if it's the uuid of an NPC
     * from experimentation, nicks are v1 and real players v4
     */
    public static boolean filterNPC(UUID uuid) {
        return uuid.version() == 2;
    }

    public static boolean isntRealPlayer(UUID uuid) {
        return uuid.version() != 4;
    }

}
