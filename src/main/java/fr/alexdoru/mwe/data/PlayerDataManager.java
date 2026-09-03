package fr.alexdoru.mwe.data;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.mwe.api.enums.MWClass;
import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.asm.interfaces.NetworkPlayerInfoAccessor;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.features.LeatherArmorManager;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.nocheaters.WDR;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

/**
 * What this class does :
 * on new NetworkPlayerInfo instance creation :
 * - updates/creates PlayerData stored in the cache
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
public final class PlayerDataManager {

    private PlayerDataManager() {}

    public static final String WARNING_ICON = EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET;
    public static final String RED_WARNING_ICON = EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET;
    public static final String PINK_WARNING_ICON = EnumChatFormatting.LIGHT_PURPLE.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET;
    public static final String SQUAD_ICON = EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GREEN + "S" + EnumChatFormatting.GOLD + "] " + EnumChatFormatting.RESET;
    private static final ChatComponentText IWARNING_ICON = new ChatComponentText(WARNING_ICON);
    private static final ChatComponentText IRED_WARNING_ICON = new ChatComponentText(RED_WARNING_ICON);
    private static final ChatComponentText IPINK_WARNING_ICON = new ChatComponentText(PINK_WARNING_ICON);
    private static final ChatComponentText ISQUAD_ICON = new ChatComponentText(SQUAD_ICON);
    private static final List<IChatComponent> ALL_ICONS_LIST = Arrays.asList(IWARNING_ICON, IRED_WARNING_ICON, IPINK_WARNING_ICON, ISQUAD_ICON);
    private static final Map<UUID, PlayerData> PLAYER_DATA_CACHE = new HashMap<>();

    static void clearPlayerDataCache() {
        PLAYER_DATA_CACHE.clear();
    }

    static void removeFromDataCache(UUID uuid) {
        PLAYER_DATA_CACHE.remove(uuid);
    }

    private static final Pattern MINECRAFT_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{1,16}");

    private static boolean isValidMinecraftName(String name) {
        return !StringUtil.isNullOrEmpty(name) && (MINECRAFT_NAME_PATTERN.matcher(name).matches() || ScoreboardTracker.isReplayMode());
    }

    public static void refreshAllPlayerData() {
        Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().forEach(netInfo -> {
                    final PlayerData playerData = createPlayerData(netInfo.getGameProfile());
                    ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(playerData.displayName);
                }
        );
        Minecraft.getMinecraft().theWorld.playerEntities.forEach(PlayerDataManager::updateEntityPlayerFields);
    }

    public static void refreshPlayerData(String playername) {
        if (isValidMinecraftName(playername)) {
            final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
            refreshPlayerData(netInfo);
        }
    }

    public static void refreshPlayerData(@NotNull UUID id) {
        refreshPlayerData(Minecraft.getMinecraft().getNetHandler().getPlayerInfo(id));
    }

    public static void refreshPlayerData(NetworkPlayerInfo netInfo) {
        if (netInfo != null) {
            final PlayerData playerData = createPlayerData(netInfo.getGameProfile());
            ((NetworkPlayerInfoAccessor) netInfo).setCustomDisplayname(playerData.displayName);
            final EntityPlayer player = getPlayerEntityByUUID(netInfo.getGameProfile().getId());
            if (player != null) {
                updateEntityPlayerFields(player, playerData);
            }
        }
    }

    private static void updateEntityPlayerFields(EntityPlayer player) {
        final PlayerData playerData = PLAYER_DATA_CACHE.get(player.getUniqueID());
        if (playerData == null) {
            return;
        }
        updateEntityPlayerFields(player, playerData);
    }

    private static void updateEntityPlayerFields(EntityPlayer player, PlayerData playerData) {

        final EntityPlayerAccessor playerAccessor = (EntityPlayerAccessor) player;
        final int oldColor = playerAccessor.getPlayerSpecialRenderColor();
        playerAccessor.setPlayerTeamColor(playerData.teamColor);
        if (MWEConfig.coloredSquadmates && playerData.isSquadmate) {
            playerAccessor.setPlayerSpecialRenderColor(ColorUtil.getColorInt(MWEConfig.squadmateColor));
        } else {
            playerAccessor.setPlayerSpecialRenderColor(ColorUtil.getColorInt(playerData.teamColor));
        }
        playerAccessor.setMWClass(playerData.mwClass);
        LeatherArmorManager.onColorChange(player, oldColor, playerAccessor.getPlayerSpecialRenderColor());

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
        player.refreshDisplayName();

    }

    /**
     * Called on NetworkPlayerinfo instantiation
     */
    public static IChatComponent initDisplaynameForTablist(GameProfile gameProfile) {
        return PlayerDataManager.getOrCreatePlayerData(gameProfile).displayName;
    }

    private static @NotNull PlayerData getOrCreatePlayerData(GameProfile gameProfile) {
        final PlayerData playerData = PLAYER_DATA_CACHE.get(gameProfile.getId());
        if (playerData != null) {
            return playerData;
        }
        return createPlayerData(gameProfile);
    }

    @NotNull
    private static PlayerData createPlayerData(GameProfile gameProfile) {
        final UUID id = gameProfile.getId();
        final String username = gameProfile.getName();
        final String squadname = SquadHandler.getSquadnameUnsafe(username);
        ChatComponentText extraPrefix = null;

        if (squadname != null) {
            if (MWEConfig.squadIconOnNames) {
                extraPrefix = ISQUAD_ICON;
            }
        } else {
            if (MWEConfig.warningIconsOnNames) {
                final WDR wdr = WdrDataManager.getWdr(id, username);
                if (wdr != null) {
                    if (wdr.hasRedIcon()) {
                        extraPrefix = IRED_WARNING_ICON;
                    } else if (wdr.hasYellowIcon()) {
                        extraPrefix = IWARNING_ICON;
                    }
                } else if (ScangameData.doesPlayerFlag(id)) {
                    extraPrefix = IPINK_WARNING_ICON;
                }
            }
        }

        String teamPrefix = "";
        String teamSuffix = "";
        char teamColor = '\0';
        MWClass mwClass = null;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null) {
            ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(username);
            if (team == null && mc.thePlayer != null && mc.thePlayer.getName().equals(username) && !MWEConfig.hypixelNick.isEmpty()) {
                team = mc.theWorld.getScoreboard().getPlayersTeam(MWEConfig.hypixelNick);
            }
            if (team != null) {
                teamPrefix = team.getColorPrefix();
                teamSuffix = team.getColorSuffix();
                teamColor = StringUtil.getLastColorCharOf(teamPrefix);
                mwClass = MWClass.fromTeamTag(ScoreboardTracker.isMWReplay() ? teamPrefix : teamSuffix);
            }
        }

        final boolean isobf = teamPrefix.contains("§k");
        final boolean isNicked = PlayerDataManager.isNickedPlayer(id);
        final String alias = AliasDataManager.getAlias(id, username);

        IChatComponent displayName = null;

        if (extraPrefix != null || isobf && MWEConfig.deobfNamesInTab || squadname != null || isNicked && MWEConfig.showFakePlayersInTab || alias != null) {
            final StringBuilder sb = new StringBuilder();
            if (extraPrefix != null) {
                sb.append(extraPrefix.getUnformattedTextForChat());
            }
            if (isobf && MWEConfig.deobfNamesInTab) {
                sb.append(NameFormatter.deobfString(teamPrefix));
            } else {
                sb.append(teamPrefix);
            }
            if (squadname != null) {
                if (MWEConfig.coloredSquadmates && MWEConfig.coloredSquadTabname) {
                    sb.append(MWEConfig.squadmateColor);
                }
                sb.append(squadname);
            } else {
                sb.append(username);
            }
            sb.append(teamSuffix);
            if (isNicked && MWEConfig.showFakePlayersInTab) {
                sb.append(EnumChatFormatting.DARK_RED).append(EnumChatFormatting.BOLD).append(" *");
            }
            if (alias != null) {
                sb.append(EnumChatFormatting.RESET).append(" (").append(EnumChatFormatting.GOLD).append(alias).append(EnumChatFormatting.RESET).append(")");
            }
            displayName = new ChatComponentText(sb.toString());
        }

        final PlayerData playerData = new PlayerData(extraPrefix, displayName, teamColor, mwClass, squadname != null);
        PLAYER_DATA_CACHE.put(id, playerData);
        return playerData;
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

    public static EntityPlayer getPlayerEntityByUUID(UUID uuid) {
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

    public static boolean isNickedPlayer(UUID uuid) {
        return uuid.version() == 1;
    }

    public static boolean isNPCPlayer(UUID uuid) {
        return uuid.version() == 2;
    }

    public static boolean isRealPlayer(UUID uuid) {
        return uuid.version() == 4;
    }

    private static class PlayerData {

        public final IChatComponent extraPrefix;
        public final IChatComponent displayName;
        public final char teamColor;
        public final MWClass mwClass;
        public final boolean isSquadmate;

        public PlayerData(IChatComponent extraPrefix, IChatComponent displayNameIn, char teamColor, MWClass mwClass, boolean isSquadmate) {
            this.extraPrefix = extraPrefix;
            this.displayName = displayNameIn;
            this.teamColor = teamColor;
            this.mwClass = mwClass;
            this.isSquadmate = isSquadmate;
        }

    }

    public static class EventHandler {

        @SubscribeEvent(priority = EventPriority.HIGH)
        public void onPlayerJoin(EntityJoinWorldEvent event) {
            if (event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
                try {
                    final EntityPlayer player = (EntityPlayer) event.entity;
                    if (event.entity instanceof EntityPlayerSP) {
                        // Delaying the transformation for self because :
                        // - certain fields such as mc.theWorld.getScoreboard().getPlayersTeam(username) are null when you just joined the world
                        // - for self the player spawn before receiving a networkplayerinfo packet
                        new DelayedTask(() -> {
                            try {
                                final PlayerData playerData = createPlayerData(player.getGameProfile());
                                updateEntityPlayerFields(player, playerData);
                            } catch (Exception e) {
                                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Caught an exception when spawning " + event.entity.getName());
                                e.printStackTrace();
                            }
                        }, 1);
                    } else {
                        updateEntityPlayerFields(player);
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
