package fr.alexdoru.mwe.scoreboard;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ScoreboardUtils {

    private ScoreboardUtils() {}

    /**
     * Returns the current ScoreObjective displayed in the sidebar
     */
    @Nullable
    public static ScoreObjective getActiveObjective() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.theWorld.getScoreboard() == null) {
            return null;
        }
        final Scoreboard scoreboard = mc.theWorld.getScoreboard();
        final ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.thePlayer.getName());
        if (scoreplayerteam != null) {
            final int slot = scoreplayerteam.getChatFormat().getColorIndex();
            if (slot >= 0) {
                final ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
                if (objective != null) return objective;
            }
        }
        return scoreboard.getObjectiveInDisplaySlot(1);
    }

    /**
     * Returns the contents of the sidebar as a list of formatted strings
     */
    @NotNull
    public static List<String> getFormattedSidebarText() {
        final ScoreObjective objective = getActiveObjective();
        if (objective == null) return Collections.emptyList();
        return getFormattedSidebarText(objective);
    }

    /**
     * Returns the contents of the sidebar as a list of formatted strings
     * See Vanilla code at {@link net.minecraft.client.gui.GuiIngame#renderScoreboard}
     */
    @NotNull
    public static List<String> getFormattedSidebarText(@NotNull ScoreObjective objective) {
        final Scoreboard scoreboard = objective.getScoreboard();
        final Collection<Score> sortedScores = scoreboard.getSortedScores(objective);
        List<Score> list = new ArrayList<>(sortedScores.size());
        for (final Score input : sortedScores) {
            if (input.getPlayerName() != null && !input.getPlayerName().startsWith("#")) {
                list.add(input);
            }
        }
        if (list.size() > 15) {
            list = Lists.newArrayList(Iterables.skip(list, list.size() - 15));
        }
        final List<String> lines = new ArrayList<>(list.size());
        for (int i = list.size() - 1; i >= 0; i--) {
            final Score score = list.get(i);
            lines.add(ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(score.getPlayerName()), ""));
        }
        return lines;
    }

    /**
     * Returns the sidebar text as a list of unformatted strings, each element is a line of the scoreboard/sidebar
     * Item at index 0 is the first line etc
     */
    @NotNull
    public static List<String> getUnformattedSidebarText() {
        return stripControlCodes(getFormattedSidebarText());
    }

    /**
     * Strips the color control codes of all the lines of the input list
     *
     * @param listIn - String list with chat color control codes
     */
    @NotNull
    public static List<String> stripControlCodes(@NotNull List<String> listIn) {
        if (listIn.isEmpty()) return Collections.emptyList();
        final List<String> list = new ArrayList<>(listIn.size());
        for (final String line : listIn) {
            list.add(StringUtil.removeFormattingCodes(line));
        }
        return list;
    }

    public static boolean isPlayingHypixelPit() {
        final ScoreObjective objective = getActiveObjective();
        return objective != null && StringUtil.removeFormattingCodes(objective.getDisplayName()).contains("THE HYPIXEL PIT");
    }

    public static boolean isMegaWallsMythicGame() {
        if (ScoreboardTracker.isMWEnvironement()) {
            for (final String line : getUnformattedSidebarText()) {
                if (line.contains("MYTHIC GAME!")) {
                    return true;
                }
            }
        }
        return false;
    }

}

