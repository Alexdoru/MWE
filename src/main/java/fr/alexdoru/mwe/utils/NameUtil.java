package fr.alexdoru.mwe.utils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import fr.alexdoru.mwe.asm.hooks.NetHandlerPlayClientHook_PlayerMapTracker;
import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.asm.interfaces.NetworkPlayerInfoAccessor;
import fr.alexdoru.mwe.chat.ChatHandler;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.AliasData;
import fr.alexdoru.mwe.data.MWPlayerData;
import fr.alexdoru.mwe.data.ScangameData;
import fr.alexdoru.mwe.enums.MWClass;
import fr.alexdoru.mwe.features.LeatherArmorManager;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.nocheaters.WDR;
import fr.alexdoru.mwe.nocheaters.WarningMessages;
import fr.alexdoru.mwe.nocheaters.WdrData;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Pattern;

/**
 * What this class does :
 * on new NetworkPlayerInfo instance creation :
 * - updates/creates MWPlayerData stored in the cache
 * - assigns custom displayName or null
 * - assigns the finals of the player
 * on Team packets :
 * - transforms the name in the tablist
 * - update the fields in entity player
 * on playerJoin :
 * - look the MWPlayerData and assigns the custom fields in EntityPlayer
 * - print warning message
 * <p>
 * When the world loads, for EntityPlayerSP it does :
 * - fire on playerjoin event twice
 * - then receive two new Networkplayerinfo packets
 * When swapping lobbys on hypixel for entityplayerSP :
 * - fires the playerjoin event 6 times
 * - then receive two new Networkplayerinfo packets
 * When swapping lobbys on hypixel for other players :
 * - receive two new Networkplayerinfo packets
 * - fires the playerjoin event once
 * - then receives one networkplayerinfo packet
 * When a player enters our render distance :
 * - receive once new Networkplayerinfo packet
 * - fire the playerjoin event once
 */
public class NameUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final String WARNING_ICON = EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET;
    public static final String RED_WARNING_ICON = EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET;
    public static final String PINK_WARNING_ICON = EnumChatFormatting.LIGHT_PURPLE.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET;
    public static final String SQUAD_ICON = EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GREEN + "S" + EnumChatFormatting.GOLD + "] " + EnumChatFormatting.RESET;
    private static final IChatComponent IWARNING_ICON = new ChatComponentText(WARNING_ICON);
    private static final IChatComponent IRED_WARNING_ICON = new ChatComponentText(RED_WARNING_ICON);
    private static final IChatComponent IPINK_WARNING_ICON = new ChatComponentText(PINK_WARNING_ICON);
    private static final IChatComponent ISQUAD_ICON = new ChatComponentText(SQUAD_ICON);
    private static final List<IChatComponent> ALL_ICONS_LIST = Arrays.asList(IWARNING_ICON, IRED_WARNING_ICON, IPINK_WARNING_ICON, ISQUAD_ICON);
    private static final Set<UUID> warningMsgPrinted = new HashSet<>();

    /**
     * This updates the infos storred in MWPlayerData.dataCache for the player : playername
     * and refreshes the name in the tablist and the nametag.
     * Set refreshDisplayName to true to fire the NameFormat Event and
     * update the name of the player as well, in case you changed it via a command
     * for example : /squad add player as aliasname
     */
    public static void updateMWPlayerDataAndEntityData(String playername, boolean refreshDisplayName) {
        if (isValidMinecraftName(playername)) {
            final NetworkPlayerInfo netInfo = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(playername);
            if (netInfo != null) {
                ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(getMWPlayerData(netInfo.getGameProfile(), true).displayName);
            }
            final EntityPlayer player = getPlayerEntityByName(playername);
            if (player != null) {
                updateEntityPlayerFields(player, false);
                if (refreshDisplayName) {
                    player.refreshDisplayName();
                }
            }
        }
    }

    public static void updateMWPlayerDataAndEntityData(EntityPlayer player, boolean refreshDisplayName) {
        final NetworkPlayerInfo netInfo = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(player.getName());
        if (netInfo != null) {
            ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(getMWPlayerData(netInfo.getGameProfile(), true).displayName);
        }
        updateEntityPlayerFields(player, false);
        if (refreshDisplayName) {
            player.refreshDisplayName();
        }
    }

    public static void updateMWPlayerDataAndEntityData(NetworkPlayerInfo netInfo, boolean refreshDisplayName) {
        ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(getMWPlayerData(netInfo.getGameProfile(), true).displayName);
        final EntityPlayer player = getPlayerEntityByName(netInfo.getGameProfile().getName());
        if (player != null) {
            updateEntityPlayerFields(player, false);
            if (refreshDisplayName) {
                player.refreshDisplayName();
            }
        }
    }

    public static void refreshAllNamesInWorld() {
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            updateMWPlayerDataAndEntityData(netInfo, true);
        }
    }

    private static final Pattern MINECRAFT_NAME_PATTERN = Pattern.compile("\\w{1,16}");

    public static boolean isValidMinecraftName(String name) {
        return !StringUtil.isNullOrEmpty(name) && (MINECRAFT_NAME_PATTERN.matcher(name).matches() || ScoreboardTracker.isReplayMode());
    }

    public static void onTeamPacket(String playername) {
        if (!isValidMinecraftName(playername)) return;
        final NetworkPlayerInfo netInfo = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(playername);
        if (netInfo == null) return;
        final MWPlayerData.PlayerData mwPlayerData = getMWPlayerData(netInfo.getGameProfile(), true);
        ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(mwPlayerData.displayName);
        if (mc.theWorld != null) {
            final EntityPlayer player;
            if (playername.equals(MWEConfig.hypixelNick)) {
                player = mc.thePlayer;
            } else {
                player = getPlayerEntityByName(netInfo.getGameProfile().getName());
            }
            if (player != null) {
                updateEntityPlayerFields(player, mwPlayerData);
            }
        }
    }

    private static void updateEntityPlayerFields(EntityPlayer player, MWPlayerData.PlayerData mwPlayerData) {
        final EntityPlayerAccessor playerAccessor = (EntityPlayerAccessor) player;
        final int oldColor = playerAccessor.getPlayerTeamColorInt();
        playerAccessor.setPlayerTeamColor(mwPlayerData.teamColor);
        if (MWEConfig.pinkSquadmates && mwPlayerData.squadname != null) {
            playerAccessor.setPlayerTeamColorInt(ColorUtil.getColorInt('d'));
        } else {
            playerAccessor.setPlayerTeamColorInt(ColorUtil.getColorInt(mwPlayerData.teamColor));
        }
        playerAccessor.setMWClass(mwPlayerData.mwClass);
        LeatherArmorManager.onColorChange(player, oldColor, playerAccessor.getPlayerTeamColorInt());
    }

    /**
     * Updates the custom fields in the entity player, the icon on nametags and also checks to print
     * the warning message if player was reported and is currently joining the world
     */
    public static void updateEntityPlayerFields(EntityPlayer player, boolean onPlayerJoin) {

        final MWPlayerData.PlayerData playerData = MWPlayerData.get(player.getUniqueID());
        if (playerData == null) {
            return;
        }

        updateEntityPlayerFields(player, playerData);

        player.getPrefixes().removeAll(ALL_ICONS_LIST);

        if (playerData.extraPrefix != null) {
            if (playerData.extraPrefix == ISQUAD_ICON) {
                if (!MWEConfig.squadIconTabOnly) {
                    player.addPrefix(playerData.extraPrefix);
                }
            } else {
                if (!MWEConfig.warningIconsTabOnly) {
                    player.addPrefix(playerData.extraPrefix);
                }
            }
        }

        if (onPlayerJoin && playerData.wdr != null) {
            if (MWEConfig.warningMessages) {
                if (!warningMsgPrinted.contains(player.getUniqueID())) {
                    warningMsgPrinted.add(player.getUniqueID());
                    ChatHandler.deleteWarningFromChat(player.getName());
                    WarningMessages.printWarningMessage(player.getUniqueID(), player.getTeam(), player.getName(), playerData.wdr);
                }
            }
        }

    }

    public static void clearWarningMessagesPrinted() {
        warningMsgPrinted.clear();
    }

    /**
     * Transforms the infos storred in MWPlayerData.dataCache and returns the MWplayerData for the player
     * For each new player spawned in the world it will create a new networkplayerinfo instance
     * a rerun all the code in the method to generate the MWPlayerData instance
     */
    @Nonnull
    public static MWPlayerData.PlayerData getMWPlayerData(GameProfile gameProfile, boolean forceRefresh) {

        final UUID id = gameProfile.getId();
        MWPlayerData.PlayerData playerData = MWPlayerData.get(id);

        if (!forceRefresh && playerData != null) {
            return playerData;
        }

        final String username = gameProfile.getName();
        final String uuid = id.toString().replace("-", "");
        final WDR wdr = WdrData.getWdr(id, username);
        String extraPrefix = "";
        IChatComponent iExtraPrefix = null;
        final String squadname = SquadHandler.getSquad().get(username);

        if (squadname != null) {
            if (MWEConfig.squadIconOnNames || MWEConfig.squadIconTabOnly) {
                extraPrefix = SQUAD_ICON;
                iExtraPrefix = ISQUAD_ICON;
            }
        } else {
            if (MWEConfig.warningIconsOnNames || MWEConfig.warningIconsTabOnly) {
                if (wdr != null) {
                    if (wdr.hasRedIcon()) {
                        extraPrefix = RED_WARNING_ICON;
                        iExtraPrefix = IRED_WARNING_ICON;
                    } else {
                        extraPrefix = WARNING_ICON;
                        iExtraPrefix = IWARNING_ICON;
                    }
                } else if (ScangameData.doesPlayerFlag(id)) {
                    extraPrefix = PINK_WARNING_ICON;
                    iExtraPrefix = IPINK_WARNING_ICON;
                }
            }
        }

        IChatComponent displayName = null;
        char teamColor = '\0';
        MWClass mwClass = null;
        if (mc.theWorld != null) {
            ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(username);
            if (team == null && mc.thePlayer != null && mc.thePlayer.getName().equals(username) && !MWEConfig.hypixelNick.isEmpty()) {
                team = mc.theWorld.getScoreboard().getPlayersTeam(MWEConfig.hypixelNick);
            }
            if (team != null) {
                final String teamprefix = team.getColorPrefix();
                final String colorSuffix = team.getColorSuffix();
                teamColor = StringUtil.getLastColorCharOf(teamprefix);
                mwClass = MWClass.fromTeamTag(ScoreboardTracker.isMWReplay() ? teamprefix : colorSuffix);
                final boolean isobf = teamprefix.contains("§k");
                final boolean isNicked = id.version() == 1;
                final String alias = AliasData.getAlias(isNicked ? username : uuid);
                if (iExtraPrefix != null || isobf || isNicked || squadname != null || alias != null) {
                    final StringBuilder sb = new StringBuilder();
                    if (iExtraPrefix != null) sb.append(extraPrefix);
                    if (isobf && MWEConfig.deobfNamesInTab) sb.append(deobfString(teamprefix));
                    else sb.append(teamprefix);
                    if (squadname != null) sb.append(squadname);
                    else sb.append(username);
                    sb.append(colorSuffix);
                    if (isNicked && MWEConfig.showFakePlayersInTab) {
                        sb.append(EnumChatFormatting.DARK_RED).append(EnumChatFormatting.BOLD).append(" *");
                    }
                    if (alias != null) {
                        sb.append(EnumChatFormatting.RESET).append(" (").append(EnumChatFormatting.GOLD).append(alias).append(EnumChatFormatting.RESET).append(")");
                    }
                    displayName = new ChatComponentText(sb.toString());
                }
            }
        }

        if (playerData == null) {
            playerData = new MWPlayerData.PlayerData(id, wdr, iExtraPrefix, squadname, displayName, teamColor, mwClass);
        } else {
            playerData.setData(wdr, iExtraPrefix, squadname, displayName, teamColor, mwClass);
        }

        return playerData;

    }

    private static final Pattern obfPattern = Pattern.compile("§k[OX]*");

    private static String deobfString(String obfText) {
        return obfPattern.matcher(obfText).replaceAll("");
    }

    public static EntityPlayer getPlayerEntityByName(String playername) {
        // we loop backwards because the player list contains duplicate entities
        // and the "active" ones are the latest inserted
        final List<EntityPlayer> playerList = mc.theWorld.playerEntities;
        for (int i = playerList.size() - 1; i >= 0; --i) {
            if (playername.equals(playerList.get(i).getName())) {
                return playerList.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the formatted name of the player, additionnal icons, squadname, alias and prestive V tag included
     * Same method that the one in {@link net.minecraft.client.gui.GuiPlayerTabOverlay#getPlayerName}
     */
    public static String getFormattedName(String playername) {
        final NetworkPlayerInfo netInfo = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(playername);
        if (netInfo == null) {
            return playername;
        }
        return getFormattedName(netInfo);
    }

    /**
     * Returns the formatted name of the player, additionnal icons, squadname, alias not included
     * Same method that the one in {@link net.minecraft.client.gui.GuiPlayerTabOverlay#getPlayerName}
     */
    public static String getFormattedName(NetworkPlayerInfo netInfo) {
        if (netInfo.getDisplayName() == null) {
            return ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName());
        }
        return netInfo.getDisplayName().getFormattedText();
    }

    /**
     * Returns the formatted team name with additionnaly a squadname
     * This doesn't return the icons in front that the player may have.
     */
    public static String getFormattedNameWithoutIcons(String playername) {
        final NetworkPlayerInfo netInfo = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(playername);
        if (netInfo == null) {
            return SquadHandler.getSquadname(playername);
        }
        return getFormattedNameWithoutIcons(netInfo);
    }

    /**
     * Returns the formatted team name with additionnaly a squadname
     * This doesn't return the icons in front that the player may have.
     */
    public static String getFormattedNameWithoutIcons(NetworkPlayerInfo netInfo) {
        return getFormattedNameWithoutIcons(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName());
    }

    /**
     * Equivalent of {@link net.minecraft.scoreboard.ScorePlayerTeam#formatPlayerName}
     * but with eventually a squadname
     */
    public static String getFormattedNameWithoutIcons(Team team, String playername) {
        if (team == null) {
            return SquadHandler.getSquadname(playername);
        } else if (team instanceof ScorePlayerTeam) {
            return deobfString(((ScorePlayerTeam) team).getColorPrefix()) + SquadHandler.getSquadname(playername) + ((ScorePlayerTeam) team).getColorSuffix();
        }
        return deobfString(team.formatString(playername));
    }

    /**
     * Returns true if it's the uuid of an NPC
     * from experimentation, on Hypixel :
     * <p>
     * - nicked players are v1
     * <p>
     * - NPCs are v2
     * <p>
     * - real players are v4
     */
    public static boolean isNPC(UUID uuid) {
        return uuid.version() == 2;
    }

    public static boolean isntRealPlayer(UUID uuid) {
        return uuid.version() != 4;
    }

    /**
     * Returns true if the player is using a random class
     */
    public static boolean isPlayerUsingRandom(NetworkPlayerInfo netInfo) {
        final String skinHash = "512a44f6c022dfaa6f61274c85aa1594cb304f0136fd5d1d3a27c1379e875692";
        if (!netInfo.hasLocationSkin()) {
            return false;
        }
        return skinHash.equals(netInfo.getLocationSkin().toString().substring(16));
    }

    private static final Ordering<NetworkPlayerInfo> netInfoOrdering = Ordering.from(new NameUtil.PlayerComparator());

    public static List<NetworkPlayerInfo> sortedCopyOf(Collection<NetworkPlayerInfo> list) {
        return netInfoOrdering.sortedCopy(list);
    }

    private static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        @Override
        public int compare(NetworkPlayerInfo netInfo1, NetworkPlayerInfo netInfo2) {
            final ScorePlayerTeam team1 = netInfo1.getPlayerTeam();
            final ScorePlayerTeam team2 = netInfo2.getPlayerTeam();
            return ComparisonChain.start()
                    .compareTrueFirst(netInfo1.getGameType() != WorldSettings.GameType.SPECTATOR, netInfo2.getGameType() != WorldSettings.GameType.SPECTATOR)
                    .compare(team1 != null ? team1.getRegisteredName() : "", team2 != null ? team2.getRegisteredName() : "")
                    .compare(netInfo1.getGameProfile().getName(), netInfo2.getGameProfile().getName())
                    .result();
        }
    }

}
