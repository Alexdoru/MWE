package fr.alexdoru.mwe.features;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.mwe.api.enums.MWClass;
import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.asm.interfaces.NetworkPlayerInfoAccessor;
import fr.alexdoru.mwe.chat.ChatHandler;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.AliasData;
import fr.alexdoru.mwe.data.NetPlayerInfoTracker;
import fr.alexdoru.mwe.data.ScangameData;
import fr.alexdoru.mwe.nocheaters.WDR;
import fr.alexdoru.mwe.nocheaters.WarningMessages;
import fr.alexdoru.mwe.nocheaters.WdrData;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

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
public final class NameFormatter {

    private NameFormatter() {}

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
    private static final Map<UUID, PlayerData> PLAYER_DATA_CACHE = new HashMap<>();

    public static void clearPlayerDataCache() {
        PLAYER_DATA_CACHE.clear();
    }

    public static void removeFromDataCache(UUID uuid) {
        PLAYER_DATA_CACHE.remove(uuid);
    }

    /**
     * This updates the infos storred in PlayerDataCache for the player : playername
     * and refreshes the name in the tablist and the nametag.
     */
    public static void updatePlayerDataAndEntityData(String playername) {
        if (isValidMinecraftName(playername)) {
            final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
            if (netInfo instanceof NetworkPlayerInfoAccessor) {
                ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(updatePlayerData(netInfo.getGameProfile()).displayName);
            }
            final EntityPlayer player = getPlayerEntityByName(playername);
            if (player != null) {
                updateEntityPlayerFields(player);
                player.refreshDisplayName();
            }
        }
    }

    public static void updatePlayerDataAndEntityData(@NotNull UUID id) {
        updatePlayerDataAndEntityData(Minecraft.getMinecraft().getNetHandler().getPlayerInfo(id));
    }

    public static void updatePlayerDataAndEntityData(NetworkPlayerInfo netInfo) {
        ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(updatePlayerData(netInfo.getGameProfile()).displayName);
        final EntityPlayer player = getPlayerEntityByUUID(netInfo.getGameProfile().getId());
        if (player != null) {
            updateEntityPlayerFields(player);
            player.refreshDisplayName();
        }
    }

    public static void refreshAllNamesInWorld() {
        Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().forEach(netInfo ->
                ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(updatePlayerData(netInfo.getGameProfile()).displayName)
        );
        Minecraft.getMinecraft().theWorld.playerEntities.forEach(player -> {
            updateEntityPlayerFields(player);
            player.refreshDisplayName();
        });
    }

    private static final Pattern MINECRAFT_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{1,16}");

    private static boolean isValidMinecraftName(String name) {
        return !StringUtil.isNullOrEmpty(name) && (MINECRAFT_NAME_PATTERN.matcher(name).matches() || ScoreboardTracker.isReplayMode());
    }

    public static void onTeamPacket(String playername) {
        if (!isValidMinecraftName(playername)) return;
        final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
        if (!(netInfo instanceof NetworkPlayerInfoAccessor)) return;
        final PlayerData playerData = updatePlayerData(netInfo.getGameProfile());
        ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(playerData.displayName);
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null) {
            final EntityPlayer player;
            if (playername.equals(MWEConfig.hypixelNick)) {
                player = mc.thePlayer;
            } else {
                player = getPlayerEntityByUUID(netInfo.getGameProfile().getId());
            }
            if (player != null) {
                updateEntityPlayerColor(player, playerData);
            }
        }
    }

    private static void updateEntityPlayerColor(EntityPlayer player, PlayerData playerData) {
        final EntityPlayerAccessor playerAccessor = (EntityPlayerAccessor) player;
        final int oldColor = playerAccessor.getPlayerSpecialRenderColor();
        playerAccessor.setPlayerTeamColor(playerData.teamColor);
        if (MWEConfig.coloredSquadmates && SquadHandler.isSquadmate(player.getName())) {
            playerAccessor.setPlayerSpecialRenderColor(ColorUtil.getColorInt(MWEConfig.squadmateColor));
        } else {
            playerAccessor.setPlayerSpecialRenderColor(ColorUtil.getColorInt(playerData.teamColor));
        }
        playerAccessor.setMWClass(playerData.mwClass);
        LeatherArmorManager.onColorChange(player, oldColor, playerAccessor.getPlayerSpecialRenderColor());
    }

    /**
     * Updates the custom fields in the entity player, the icon on nametags and also checks to print
     * the warning message if player was reported and is currently joining the world
     */
    private static void updateEntityPlayerFields(EntityPlayer player) {

        final PlayerData playerData = PLAYER_DATA_CACHE.get(player.getUniqueID());
        if (playerData == null) {
            return;
        }

        updateEntityPlayerColor(player, playerData);

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

    }

    private static void tryPrintWarningMessage(EntityPlayer player) {
        if (MWEConfig.warningMessages && !warningMsgPrinted.contains(player.getUniqueID())) {
            final WDR wdr = WdrData.getWdr(player.getUniqueID(), player.getName());
            if (wdr != null) {
                warningMsgPrinted.add(player.getUniqueID());
                ChatHandler.deleteWarningFromChat(player.getName());
                WarningMessages.printWarningMessage(player.getUniqueID(), player.getTeam(), player.getName(), wdr);
            }
        }
    }

    /**
     * Called on NetworkPlayerinfo instantiation
     */
    public static IChatComponent getDisplayName(GameProfile gameProfile) {
        return NameFormatter.getPlayerData(gameProfile).displayName;
    }

    private static @NotNull PlayerData getPlayerData(GameProfile gameProfile) {
        final PlayerData playerData = PLAYER_DATA_CACHE.get(gameProfile.getId());
        if (playerData != null) {
            return playerData;
        }
        return updatePlayerData(gameProfile);
    }

    @NotNull
    private static PlayerData updatePlayerData(GameProfile gameProfile) {
        final UUID id = gameProfile.getId();
        final String username = gameProfile.getName();
        final WDR wdr = WdrData.getWdr(id, username);
        String extraPrefix = "";
        IChatComponent iExtraPrefix = null;
        final String squadname = SquadHandler.getSquadnameUnsafe(username);

        if (squadname != null) {
            if (MWEConfig.squadIconOnNames) {
                extraPrefix = SQUAD_ICON;
                iExtraPrefix = ISQUAD_ICON;
            }
        } else {
            if (MWEConfig.warningIconsOnNames) {
                if (wdr != null) {
                    if (wdr.hasRedIcon()) {
                        extraPrefix = RED_WARNING_ICON;
                        iExtraPrefix = IRED_WARNING_ICON;
                    } else if (wdr.hasYellowIcon()) {
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
        final Minecraft mc = Minecraft.getMinecraft();
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
                final boolean isNicked = NameFormatter.isNickedPlayer(id);
                final String alias = AliasData.getAlias(id, username);
                if (iExtraPrefix != null || isobf || isNicked && MWEConfig.showFakePlayersInTab || squadname != null || alias != null) {
                    final StringBuilder sb = new StringBuilder();
                    if (iExtraPrefix != null) sb.append(extraPrefix);
                    if (isobf && MWEConfig.deobfNamesInTab) {
                        sb.append(deobfString(teamprefix));
                    } else {
                        sb.append(teamprefix);
                    }
                    if (squadname != null) {
                        if (MWEConfig.coloredSquadmates && MWEConfig.coloredSquadTabname) {
                            sb.append(MWEConfig.squadmateColor);
                        }
                        sb.append(squadname);
                    } else {
                        sb.append(username);
                    }
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

        final PlayerData playerData = new PlayerData(iExtraPrefix, displayName, teamColor, mwClass);
        PLAYER_DATA_CACHE.put(id, playerData);

        return playerData;
    }

    private static final Pattern obfPattern = Pattern.compile("§k[OX]*");

    private static String deobfString(String obfText) {
        return obfPattern.matcher(obfText).replaceAll("");
    }

    public static EntityPlayer getPlayerEntityByName(String playername) {
        // we loop backwards because the player list contains duplicate entities
        // and the "active" ones are the latest inserted
        final List<EntityPlayer> playerList = Minecraft.getMinecraft().theWorld.playerEntities;
        for (int i = playerList.size() - 1; i >= 0; --i) {
            if (playername.equals(playerList.get(i).getName())) {
                return playerList.get(i);
            }
        }
        return null;
    }

    private static EntityPlayer getPlayerEntityByUUID(UUID uuid) {
        // we loop backwards because the player list contains duplicate entities
        // and the "active" ones are the latest inserted
        final List<EntityPlayer> playerList = Minecraft.getMinecraft().theWorld.playerEntities;
        for (int i = playerList.size() - 1; i >= 0; --i) {
            if (uuid.equals(playerList.get(i).getUniqueID())) {
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
        final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
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
        final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
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
            final ScorePlayerTeam scorePlayerTeam = (ScorePlayerTeam) team;
            return deobfString(scorePlayerTeam.getColorPrefix()) + SquadHandler.getSquadname(playername) + scorePlayerTeam.getColorSuffix();
        }
        return deobfString(team.formatString(playername));
    }

    public static boolean isNickedPlayer(UUID uuid) {
        return uuid.version() == 1;
    }

    public static boolean isNPCPlayer(UUID uuid) {
        return uuid.version() == 2;
    }

    public static boolean isRealPlayer(UUID uuid) {
        return uuid.version() == 4;
    }

    /**
     * Returns true if the player is using a random class
     */
    public static boolean isPlayerUsingRandom(NetworkPlayerInfo netInfo) {
        if (!netInfo.hasLocationSkin()) {
            return false;
        }
        final String RANDOM_SKIN_HASH = "512a44f6c022dfaa6f61274c85aa1594cb304f0136fd5d1d3a27c1379e875692";
        return RANDOM_SKIN_HASH.equals(netInfo.getLocationSkin().toString().substring(16));
    }

    private static class PlayerData {

        public final IChatComponent extraPrefix;
        public final IChatComponent displayName;
        public final char teamColor;
        public final MWClass mwClass;

        public PlayerData(IChatComponent extraPrefix, IChatComponent displayNameIn, char teamColor, MWClass mwClass) {
            this.extraPrefix = extraPrefix;
            this.displayName = displayNameIn;
            this.teamColor = teamColor;
            this.mwClass = mwClass;
        }

    }

    public static class EventHandler {

        private long lastDeathTime;

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event) {
            if (event.world.isRemote && (System.currentTimeMillis() - lastDeathTime > 5000L)) {
                warningMsgPrinted.clear();
            }
        }

        @SubscribeEvent
        public void onGuiScreen(GuiScreenEvent.InitGuiEvent.Pre event) {
            if (event.gui instanceof GuiGameOver) {
                lastDeathTime = System.currentTimeMillis();
            }
        }

        @SubscribeEvent
        public void onPlayerJoin(EntityJoinWorldEvent event) {
            if (event.entity instanceof EntityPlayer && event.entity.worldObj.isRemote) {
                try {
                    final EntityPlayer player = (EntityPlayer) event.entity;
                    if (event.entity instanceof EntityPlayerSP) {
                        // Delaying the transformation for self because :
                        // - certain fields such as mc.theWorld.getScoreboard().getPlayersTeam(username) are null when you just joined the world
                        // - for self the player spawn before receiving a networkplayerinfo packet
                        new DelayedTask(() -> {
                            try {
                                updatePlayerData(player.getGameProfile());
                                updateEntityPlayerFields(player);
                            } catch (Exception e) {
                                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Caught an exception when spawning " + event.entity.getName());
                                e.printStackTrace();
                            }
                        }, 1);
                    } else {
                        updateEntityPlayerFields(player);
                        tryPrintWarningMessage(player);
                    }
                } catch (Exception e) {
                    ChatUtil.addChatMessage(EnumChatFormatting.RED + "Caught an exception when spawning " + event.entity.getName());
                    e.printStackTrace();
                }
            }
        }

        @SubscribeEvent
        public void onNameFormat(PlayerEvent.NameFormat event) {
            final String squadname = SquadHandler.getSquadnameUnsafe(event.username);
            if (squadname != null) {
                event.displayname = MWEConfig.coloredSquadmates ? MWEConfig.squadmateColor + squadname : squadname;
            }
        }

    }

}
