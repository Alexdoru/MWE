package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardParser;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

public class GameInfoGrabber {

    private static final String TIME_WALLS_FALL = "Walls Fall"; // finish 6min after replay starts
    private static final String TIME_ENRAGE_OFF = "Enrage Off"; // lasts for 8mins
    private static final String TIME_DEATHMATCH = "Deathmatch"; // lasts for 31min
    private static final String TIME_GAME_END = "Game End"; // at the end it makes 45min total
    private static long gamestarttimestamp = 0;
    private static String gameID = "?";

    public static String getGameIDfromscoreboard() {
        final List<String> scoresRaw = ScoreboardUtils.getUnformattedSidebarText();
        if (scoresRaw.size() == 0) {
            return "?";
        }
        final Matcher matcher = ScoreboardParser.GAME_ID_PATTERN.matcher(scoresRaw.get(0));
        if (!matcher.find()) {
            return "?";
        }
        return matcher.group(1);
    }

    /**
     * Prints scoreboard in chat
     */
    public static void debugGetScoreboard() {
        final List<String> scoresColor = ScoreboardUtils.getFormattedSidebarText();
        if (scoresColor.size() == 0) {
            ChatUtil.addChatMessage("There are no active scoreboards in this world.");
            return;
        }
        for (final String sidebarScore : scoresColor) {
            ChatUtil.addChatMessage(sidebarScore);
            MegaWallsEnhancementsMod.logger.info(sidebarScore);
        }
    }

    public static void saveinfoOnGameStart() {
        gamestarttimestamp = (new Date()).getTime() + 1000L;
        gameID = getGameIDfromscoreboard();
    }

    /**
     * Returns the time since the start of the game as a string. example : "24min54sec"
     *
     * @param timestamp - date time in millisecond when you press the timestamp keybind
     * @param serverID  - current serverID when you press the timestamp keybind
     */
    public static String getTimeSinceGameStart(long timestamp, String serverID, int delay) {

        if (FKCounterMod.isInMwGame) {

            final List<String> scoresRaw = ScoreboardUtils.getUnformattedSidebarText();

            if (scoresRaw.size() < 2) {
                return "?";
            }

            final String time_line = scoresRaw.get(1);
            final String[] split = time_line.split(":");

            if (split.length < 3) {
                return "?";
            }

            final int score_sec = 60 * Integer.parseInt(split[1].replace(" ", "")) + Integer.parseInt(split[2].replace(" ", ""));
            int sec_since_start = 0;

            switch (split[0]) {
                case TIME_WALLS_FALL:
                    sec_since_start = (6 * 60 - score_sec);
                    break;
                case TIME_ENRAGE_OFF:
                    sec_since_start = 6 * 60 + (8 * 60 - score_sec);
                    break;
                case TIME_DEATHMATCH:
                    final String storedGameID = getstoredGameID();
                    if (!storedGameID.equals("?") && storedGameID.equals(serverID)) {
                        final long long_sec_since_start = (timestamp > getstoredTimestamp() ? timestamp - getstoredTimestamp() : 0L) / 1000; //en secondes
                        return long_sec_since_start / 60 + "min" + long_sec_since_start % 60 + "sec";
                    } else {
                        return "?";
                    }
                case TIME_GAME_END:
                    sec_since_start = (45 * 60 - score_sec);
                    break;
            }

            final int result = sec_since_start > delay ? sec_since_start - delay : 0;
            return result / 60 + "min" + result % 60 + "sec";

        } else if (!getstoredGameID().equals("?") && getstoredGameID().equals(serverID)) {

            final long sec_since_start = (timestamp > getstoredTimestamp() ? timestamp - getstoredTimestamp() : 0L) / 1000; //en secondes
            return sec_since_start / 60 + "min" + sec_since_start % 60 + "sec";

        }

        return "?";

    }

    public static String getstoredGameID() {
        return gameID;
    }

    public static long getstoredTimestamp() {
        return gamestarttimestamp;
    }

}
