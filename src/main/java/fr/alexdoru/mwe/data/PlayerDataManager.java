package fr.alexdoru.mwe.data;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.mwe.api.ITabNameModifier;
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

    private static final ChatComponentText IWARNING_ICON = new ChatComponentText(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET);
    private static final ChatComponentText IRED_WARNING_ICON = new ChatComponentText(EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET);
    private static final ChatComponentText IPINK_WARNING_ICON = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET);
    private static final ChatComponentText ISQUAD_ICON = new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GREEN + "S" + EnumChatFormatting.GOLD + "] " + EnumChatFormatting.RESET);
    private static final List<IChatComponent> ALL_ICONS_LIST = Arrays.asList(IWARNING_ICON, IRED_WARNING_ICON, IPINK_WARNING_ICON, ISQUAD_ICON);
    private static final Map<UUID, PlayerData> PLAYER_DATA_CACHE = new HashMap<>();
    private static final List<ITabNameModifier> REGISTERED_MODIFIERS = new ArrayList<>();

    public static void registerTabNameModifier(ITabNameModifier tabNameModifier) {
        REGISTERED_MODIFIERS.add(tabNameModifier);
        REGISTERED_MODIFIERS.sort(Comparator.comparingInt(ITabNameModifier::getPriority));
    }

    static void clearPlayerDataCache() {
        PLAYER_DATA_CACHE.clear();
    }

    static void removeFromDataCache(UUID uuid) {
        PLAYER_DATA_CACHE.remove(uuid);
    }

    static PlayerData getData(UUID uuid) {
        return PLAYER_DATA_CACHE.get(uuid);
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
        if (playerData.prefixIcon != null) {
            player.addPrefix(playerData.prefixIcon);
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
        ChatComponentText prefixIcon = null;

        if (squadname != null) {
            if (MWEConfig.squadIconOnNames) {
                prefixIcon = ISQUAD_ICON;
            }
        } else {
            if (MWEConfig.warningIconsOnNames) {
                final WDR wdr = WdrDataManager.getWdr(id, username);
                if (wdr != null) {
                    if (wdr.hasRedIcon()) {
                        prefixIcon = IRED_WARNING_ICON;
                    } else if (wdr.hasYellowIcon()) {
                        prefixIcon = IWARNING_ICON;
                    }
                } else if (ScangameData.doesPlayerFlag(id)) {
                    prefixIcon = IPINK_WARNING_ICON;
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

        final boolean modifyTabName = prefixIcon != null || isobf && MWEConfig.deobfNamesInTab || squadname != null || isNicked && MWEConfig.showFakePlayersInTab || alias != null;
        boolean externalModifer = false;
        if (!modifyTabName && !REGISTERED_MODIFIERS.isEmpty()) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < REGISTERED_MODIFIERS.size(); i++) {
                final ITabNameModifier modifier = REGISTERED_MODIFIERS.get(i);
                externalModifer = modifier.shouldModifyName(gameProfile);
                if (externalModifer) break;
            }
        }

        final PlayerData playerData;

        if (modifyTabName || externalModifer) {

            final StringBuilder prefixBuilder = new StringBuilder();
            if (prefixIcon != null) {
                prefixBuilder.append(prefixIcon.getUnformattedTextForChat());
            }

            final StringBuilder nameBuilder = new StringBuilder();
            if (isobf && MWEConfig.deobfNamesInTab) {
                nameBuilder.append(NameFormatter.deobfString(teamPrefix));
            } else {
                nameBuilder.append(teamPrefix);
            }
            if (squadname != null) {
                if (MWEConfig.coloredSquadmates && MWEConfig.coloredSquadTabname) {
                    nameBuilder.append(MWEConfig.squadmateColor);
                }
                nameBuilder.append(squadname);
            } else {
                nameBuilder.append(username);
            }
            nameBuilder.append(teamSuffix);

            final StringBuilder suffixBuilder = new StringBuilder();
            if (externalModifer) {
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < REGISTERED_MODIFIERS.size(); i++) {
                    REGISTERED_MODIFIERS.get(i).modifyTabname(gameProfile, prefixBuilder, suffixBuilder);
                }
            }

            final String prefix = prefixBuilder.toString();
            final String middleName = nameBuilder.toString();
            final String suffix = suffixBuilder.toString();
            final StringBuilder sb = new StringBuilder().append(prefix).append(middleName).append(suffix);
            if (isNicked && MWEConfig.showFakePlayersInTab) {
                sb.append(EnumChatFormatting.DARK_RED).append(EnumChatFormatting.BOLD).append(" *");
            }
            if (alias != null) {
                sb.append(EnumChatFormatting.RESET).append(" (").append(EnumChatFormatting.GOLD).append(alias).append(EnumChatFormatting.RESET).append(")");
            }
            final IChatComponent displayName = new ChatComponentText(sb.toString());
            playerData = new PlayerData(displayName, prefixIcon, prefix, middleName, suffix, alias, teamColor, mwClass, squadname != null);

        } else {

            playerData = new PlayerData(teamColor, mwClass);

        }

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

    static class PlayerData {

        public final IChatComponent displayName;
        public final IChatComponent prefixIcon;
        public final String prefix;
        public final String middleName;
        public final String suffix;
        public final String alias;
        public final char teamColor;
        public final MWClass mwClass;
        public final boolean isSquadmate;

        public PlayerData(char teamColor, MWClass mwClass) {
            this(null, null, null, null, null, null, teamColor, mwClass, false);
        }

        public PlayerData(IChatComponent displayNameIn, IChatComponent prefixIcon, String prefix, String middleName, String suffix, String alias, char teamColor, MWClass mwClass, boolean isSquadmate) {
            this.displayName = displayNameIn;
            this.prefixIcon = prefixIcon;
            this.prefix = prefix;
            this.middleName = middleName;
            this.suffix = suffix;
            this.alias = alias;
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
