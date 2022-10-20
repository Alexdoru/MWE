package fr.alexdoru.megawallsenhancementsmod.fkcounter.utils;

import fr.alexdoru.megawallsenhancementsmod.gui.huds.LastWitherHPHUD;
import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;
import net.minecraft.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreboardParser {

    public static final Pattern GAME_ID_PATTERN = Pattern.compile("\\d+/\\d+/\\d+\\s+([\\d\\w]+)");
    private static final Pattern GAME_END_PATTERN = Pattern.compile("Game End: (\\d+):(\\d+)");
    private static final Pattern GATES_OPEN_PATTERN = Pattern.compile("Gates Open: \\d+:\\d+");
    private static final Pattern WALLS_FALL_PATTERN = Pattern.compile("Walls Fall: (\\d+):(\\d+)");
    private static final Pattern MW_TITLE_PATTERN = Pattern.compile("MEGA\\sWALLS");
    private static final Pattern MW_INGAME_PATTERN = Pattern.compile("[0-9]+\\sFinals?\\s[0-9]+\\sF\\.\\sAssists?");
    private static final Pattern PREGAME_LOBBY_PATTERN = Pattern.compile("Players:\\s*[0-9]+/[0-9]+");
    private static final Pattern WITHER_ALIVE_PATTERN = Pattern.compile("\\[[BGRY]\\] Wither HP: ?(\\d+)");
    private static final Pattern WITHER_ALIVE_HEART_PATTERN = Pattern.compile("\\[[BGRY]\\] Wither [\u2764\u2665]: ?(\\d+)");

    private final ArrayList<String> aliveWithers = new ArrayList<>();
    private String gameId = null;
    private boolean isInMwGame = false;
    private boolean isMWEnvironement = false;
    private boolean preGameLobby = false;
    private boolean isitPrepPhase = false;
    private boolean hasGameEnded = false;

    /* This runs on every tick to parse the scoreboard data */
    public ScoreboardParser(Scoreboard scoreboard) {

        if (scoreboard == null) {
            return;
        }

        final String title = ScoreboardUtils.getUnformattedSidebarTitle(scoreboard);
        if (MW_TITLE_PATTERN.matcher(title).find()) {
            isMWEnvironement = true;
        } else {
            return;
        }

        final List<String> formattedSidebarLines = ScoreboardUtils.getFormattedSidebarText(scoreboard);
        final List<String> unformattedSidebarLines = ScoreboardUtils.stripControlCodes(formattedSidebarLines);

        if (unformattedSidebarLines.isEmpty()) {
            return;
        }

        final Matcher matcher = GAME_ID_PATTERN.matcher(unformattedSidebarLines.get(0));
        if (matcher.find()) {
            gameId = matcher.group(1);
        } else {
            checkPreGameLobby(unformattedSidebarLines);
            return;
        }

        if (unformattedSidebarLines.size() < 10) {
            return;
        }

        if (MW_INGAME_PATTERN.matcher(unformattedSidebarLines.get(9)).find()) {
            isInMwGame = true;
        } else {
            if (checkPreGameLobby(unformattedSidebarLines)) {
                return;
            }
        }

        if (WALLS_FALL_PATTERN.matcher(unformattedSidebarLines.get(1)).find()
                || GATES_OPEN_PATTERN.matcher(unformattedSidebarLines.get(1)).find()) {
            isitPrepPhase = true;
        }

        int eliminated_teams = 0;
        int witherHP = 1000;

        for (int i = 3; i < Math.min(unformattedSidebarLines.size(), 7); i++) {

            final String line = unformattedSidebarLines.get(i);
            /*Wither alive detection*/
            final Matcher matcher1 = WITHER_ALIVE_PATTERN.matcher(line);
            final String colorCode;
            if (matcher1.find()) {
                final String formattedLine = formattedSidebarLines.get(i);
                colorCode = StringUtil.getLastColorCodeBefore(formattedLine, "\\[");
                aliveWithers.add(colorCode);
                witherHP = Integer.parseInt(matcher1.group(1));
            } else {
                final Matcher matcher2 = WITHER_ALIVE_HEART_PATTERN.matcher(line);
                if (matcher2.find()) {
                    final String formattedLine = formattedSidebarLines.get(i);
                    colorCode = StringUtil.getLastColorCodeBefore(formattedLine, "\\[");
                    aliveWithers.add(colorCode);
                    witherHP = 2 * Integer.parseInt(matcher2.group(1));
                }
            }

            if (line.contains("eliminated!")) {
                eliminated_teams++;
            }

        }

        if (eliminated_teams == 3 || unformattedSidebarLines.get(1).contains("None!:")) {
            hasGameEnded = true;
        }

        if (isOnlyOneWitherAlive()) {
            LastWitherHPHUD.instance.updateWitherHP(witherHP);
        }

    }

    private boolean checkPreGameLobby(List<String> unformattedSidebarLines) {
        for (final String line : unformattedSidebarLines) {
            if (PREGAME_LOBBY_PATTERN.matcher(line).find()) {
                gameId = null;
                preGameLobby = true;
                isInMwGame = false;
                return true;
            }
        }
        return false;
    }

    public boolean isWitherAlive(String colorCode) {
        return aliveWithers.contains(colorCode);
    }

    public boolean areAllWithersAlive() {
        return aliveWithers.size() == 4;
    }

    public String getGameId() {
        return gameId;
    }

    public List<String> getAliveWithers() {
        return aliveWithers;
    }

    public boolean hasGameEnded() {
        return hasGameEnded;
    }

    public boolean isitPrepPhase() {
        return isitPrepPhase;
    }

    public boolean isOnlyOneWitherAlive() {
        return aliveWithers.size() == 1;
    }

    public boolean isMWEnvironement() {
        return isMWEnvironement;
    }

    public boolean isInMwGame() {
        return isInMwGame;
    }

    public boolean isPreGameLobby() {
        return preGameLobby;
    }

}
