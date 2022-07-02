package fr.alexdoru.fkcountermod.utils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Returns a list of formatted strings containing each line of the scoreboard/sidebar
     * Item at index 0 is the first line etc
     */
    public static List<String> getFormattedSidebarText() {
        List<String> lines = new ArrayList<>();
        if (mc.theWorld == null) {
            return lines;
        }
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return lines;
        }
        return getFormattedSidebarText(scoreboard);
    }

    /**
     * Returns a list of formatted strings containing each line of the scoreboard/sidebar
     * Item at index 0 is the first line etc
     */
    public static List<String> getFormattedSidebarText(Scoreboard scoreboard) {
        List<String> lines = new ArrayList<>();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) {
            return lines;
        }
        Collection<Score> scores = scoreboard.getSortedScores(objective);
        List<Score> list = scores.stream().filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#")).collect(Collectors.toList());
        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }
        for (Score score : scores) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            lines.add(ScorePlayerTeam.formatPlayerName(team, ""));
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
        List<String> lines = getFormattedSidebarText(scoreboard);
        return stripControlCodes(lines);
    }

    /**
     * Strips the color control codes of all the lines of the input list
     *
     * @param ListIn - String list with chat color control codes
     */
    public static List<String> stripControlCodes(List<String> ListIn) {
        List<String> unformattedList = new ArrayList<>();
        for (int i = 0; i < ListIn.size(); i++) {
            unformattedList.add(i, StringUtils.stripControlCodes(ListIn.get(i)));
        }
        return unformattedList;
    }

    /**
     * Returns formatted top of the scoreboard/sidebar
     */
    public static String getSidebarTitle(Scoreboard scoreboard) {
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) {
            return "";
        }
        return objective.getDisplayName();
    }

    /**
     * Returns unformatted top of the scoreboard/sidebar
     */
    public static String getUnformattedSidebarTitle(Scoreboard scoreboard) {
        return StringUtils.stripControlCodes(getSidebarTitle(scoreboard));
    }

    public static String getUnformattedSidebarTitle() {
        return mc.theWorld == null ? null : getUnformattedSidebarTitle(mc.theWorld.getScoreboard());
    }

}

