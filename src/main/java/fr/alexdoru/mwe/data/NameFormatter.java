package fr.alexdoru.mwe.data;

import fr.alexdoru.mwe.features.SquadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

import java.util.regex.Pattern;

public final class NameFormatter {

    private NameFormatter() {}

    private static final Pattern obfPattern = Pattern.compile("§k[OX]*");

    static String deobfString(String obfText) {
        return obfPattern.matcher(obfText).replaceAll("");
    }

    /**
     * Returns the player's name exactly as it is shown in the tablist
     */
    public static String getTablistName(NetworkPlayerInfo netInfo) {
        try {
            return Minecraft.getMinecraft().ingameGUI.getTabList().getPlayerName(netInfo);
        } catch (Throwable ignored) {}
        if (netInfo.getDisplayName() == null) {
            return ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName());
        }
        return netInfo.getDisplayName().getFormattedText();
    }

    /**
     * Returns the player's name formatted according to the vanilla team
     */
    public static String getVanillaName(NetworkPlayerInfo netInfo) {
        return ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName());
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

}
