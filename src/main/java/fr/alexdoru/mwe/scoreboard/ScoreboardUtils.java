package fr.alexdoru.mwe.scoreboard;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class ScoreboardUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isPlayingHypixelPit() {
        final String title = getUnformattedSidebarTitle();
        return title != null && title.contains("THE HYPIXEL PIT");
    }

    public static boolean isMegaWallsMythicGame() {
        if (!ScoreboardTracker.isMWEnvironement()) {
            return false;
        }
        final List<String> scoresRaw = getUnformattedSidebarText();
        if (scoresRaw.isEmpty()) {
            return false;
        }
        for (final String line : scoresRaw) {
            if (line.contains("MYTHIC GAME!")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of formatted strings containing each line of the scoreboard/sidebar
     * Item at index 0 is the first line etc
     */
    public static List<String> getFormattedSidebarText() {
        final List<String> lines = new ArrayList<>();
        if (mc.theWorld == null) {
            return lines;
        }
        final Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return lines;
        }
        return getFormattedSidebarText(scoreboard);
    }

    /**
     * Returns a list of formatted strings containing each line of the scoreboard/sidebar
     * Item at index 0 is the first line
     * See Vanilla code at {@link net.minecraft.client.gui.GuiIngame#renderScoreboard}
     */
    public static List<String> getFormattedSidebarText(Scoreboard scoreboard) {
        final ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) {
            return new ArrayList<>();
        }
        final Collection<Score> scores = scoreboard.getSortedScores(objective);
        List<Score> list = new ArrayList<>();
        for (final Score input : scores) {
            if (input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#")) {
                list.add(input);
            }
        }
        if (list.size() > 15) {
            list = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        }
        final List<String> lines = new ArrayList<>();
        for (final Score score : list) {
            lines.add(ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(score.getPlayerName()), ""));
        }
        Collections.reverse(lines);
        return lines;
    }

    /**
     * Returns the sidebar text as a list of unformatted strings, each element is a line of the scoreboard/sidebar
     * Item at index 0 is the first line etc
     */
    public static List<String> getUnformattedSidebarText() {
        return stripControlCodes(getFormattedSidebarText());
    }

    /**
     * Returns the sidebar text as a list of unformatted strings, each element is a line of the scoreboard/sidebar
     * Item at index 0 is the first line etc
     *
     * @param scoreboard - raw minecraft scoreboard - mc.theWorld.getScoreboard()
     */
    public static List<String> getUnformattedSidebarText(Scoreboard scoreboard) {
        final List<String> lines = getFormattedSidebarText(scoreboard);
        return stripControlCodes(lines);
    }

    /**
     * Returns unformatted top of the scoreboard/sidebar
     */
    public static String getUnformattedSidebarTitle() {
        return mc.theWorld == null ? null : getUnformattedSidebarTitle(mc.theWorld.getScoreboard());
    }

    /**
     * Returns unformatted top of the scoreboard/sidebar
     */
    public static String getUnformattedSidebarTitle(Scoreboard scoreboard) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(getSidebarTitle(scoreboard));
    }

    /**
     * Returns formatted top of the scoreboard/sidebar
     */
    public static String getSidebarTitle(Scoreboard scoreboard) {
        if (scoreboard == null) {
            return "";
        }
        final ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) {
            return "";
        }
        return objective.getDisplayName();
    }

    /**
     * Prints scoreboard in chat
     */
    public static void printScoreboard() {
        if (mc.theWorld == null) {
            return;
        }
        final Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        final ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) {
            return;
        }
        Collection<Score> scores = scoreboard.getSortedScores(objective);
        final List<Score> list = scores.stream().filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#")).collect(Collectors.toList());
        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }
        final List<String> printChat = new ArrayList<>();
        final List<String> printConsole = new ArrayList<>();
        for (final Score score : scores) {
            final ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            final String s1;
            if (scoreplayerteam == null) {
                s1 = score.getPlayerName() + EnumChatFormatting.RED + score.getScorePoints();
                printChat.add(s1);
                printConsole.add("playername : '" + StringUtil.getStringAsUnicode(score.getPlayerName()) + "' points : '" + score.getScorePoints());
            } else {
                s1 = scoreplayerteam.getColorPrefix() + score.getPlayerName() + scoreplayerteam.getColorSuffix() + EnumChatFormatting.RED + score.getScorePoints();
                printChat.add(s1);
                printConsole.add("prefix : '" + scoreplayerteam.getColorPrefix() + "' playername : '" + StringUtil.getStringAsUnicode(score.getPlayerName()) + "' suffix : " + scoreplayerteam.getColorSuffix() + "' points : '" + score.getScorePoints());
            }
        }
        printChat.forEach(ChatUtil::addChatMessage);
        printConsole.forEach(MWE.logger::info);
    }

    /**
     * Strips the color control codes of all the lines of the input list
     *
     * @param listIn - String list with chat color control codes
     */
    public static List<String> stripControlCodes(List<String> listIn) {
        final List<String> list = new ArrayList<>(listIn.size());
        for (final String line : listIn) {
            list.add(EnumChatFormatting.getTextWithoutFormattingCodes(line));
        }
        return list;
    }

    public static String getGameIdFromScoreboard() {
        final List<String> scoresRaw = getUnformattedSidebarText();
        if (scoresRaw.isEmpty()) {
            return null;
        }
        for (final String line : scoresRaw) {
            final Matcher matcher = ScoreboardParser.GAME_ID_PATTERN.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

}

