package fr.alexdoru.mwe.data;

import fr.alexdoru.mwe.features.SquadHandler;
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

    // TODO hold a fake guiplayertab instance and use the method to get tablist name
    // TODO redirect all usages of ScorePlayerTeam.formatPlayerName to this class

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
