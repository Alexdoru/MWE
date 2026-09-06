package fr.alexdoru.mwe.data;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.features.SquadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public final class NameFormatter {

    private NameFormatter() {}

    private static final Pattern obfPattern = Pattern.compile("§k[OX]*");

    static String deobfString(@NotNull String obfText) {
        return obfPattern.matcher(obfText).replaceAll("");
    }

    /**
     * Returns the player's name exactly as it is shown in the tablist
     */
    public static String getTablistName(@NotNull NetworkPlayerInfo netInfo) {
        try {
            return Minecraft.getMinecraft().ingameGUI.getTabList().getPlayerName(netInfo);
        } catch (Throwable ignored) {}
        if (netInfo.getDisplayName() == null) {
            return getVanillaName(netInfo);
        }
        return netInfo.getDisplayName().getFormattedText();
    }

    /**
     * Returns the player's name formatted according to the vanilla team
     */
    public static String getVanillaName(@NotNull NetworkPlayerInfo netInfo) {
        return ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName());
    }

    public static String getFormattedNameSimple(String playername) {
        final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
        if (netInfo == null) {
            return SquadHandler.getSquadname(playername);
        }
        return getFormattedNameSimple(netInfo);
    }

    public static String getFormattedNameSimple(@NotNull NetworkPlayerInfo netInfo) {
        final String playername = netInfo.getGameProfile().getName();
        final ScorePlayerTeam team = netInfo.getPlayerTeam();
        final String squadname = SquadHandler.getSquadnameUnsafe(playername);
        if (team == null) {
            if (squadname != null) {
                return squadname;
            } else {
                return playername;
            }
        } else {
            final StringBuilder sb = new StringBuilder();
            if (MWEConfig.deobfNamesInTab) {
                sb.append(deobfString(team.getColorPrefix()));
            } else {
                sb.append(team.getColorPrefix());
            }
            if (squadname != null) {
                if (MWEConfig.coloredSquadmates) {
                    sb.append(MWEConfig.squadmateColor);
                }
                sb.append(squadname);
            } else {
                sb.append(playername);
            }
            return sb.append(team.getColorSuffix()).toString();
        }
    }

    public static String getFormattedName(@NotNull NetworkPlayerInfo netInfo, boolean showPrefix, boolean showSuffix, boolean showAlias) {
        final PlayerDataManager.PlayerData data = PlayerDataManager.getData(netInfo.getGameProfile().getId());
        if (data == null) {
            return getVanillaName(netInfo);
        }
        final StringBuilder sb = new StringBuilder();
        if (showPrefix) {
            sb.append(data.prefix);
        }
        sb.append(data.middleName);
        if (showSuffix) {
            sb.append(data.suffix);
        }
        if (MWEConfig.showFakePlayersInTab && PlayerDataManager.isNickedPlayer(netInfo.getGameProfile().getId())) {
            sb.append(EnumChatFormatting.DARK_RED).append(EnumChatFormatting.BOLD).append(" *").append(EnumChatFormatting.RESET);
        }
        if (showAlias && data.alias != null) {
            sb.append(" (").append(EnumChatFormatting.GOLD).append(data.alias).append(EnumChatFormatting.RESET).append(")");
        }
        return sb.toString();
    }

}
