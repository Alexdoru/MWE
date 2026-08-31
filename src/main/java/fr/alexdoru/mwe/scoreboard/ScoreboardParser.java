package fr.alexdoru.mwe.scoreboard;

import fr.alexdoru.mwe.api.IScoreboardParser;
import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.features.AFKSoundWarning;
import fr.alexdoru.mwe.utils.SoundUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScoreboardParser implements IScoreboardParser {

    private static final Pattern GAME_ID_PATTERN = Pattern.compile("\\d+/\\d+/\\d+\\s+(\\w+)");
    private static final Pattern GATES_OPEN_PATTERN = Pattern.compile("Gates Open: \\d+:\\d+");
    private static final Pattern WALLS_FALL_PATTERN = Pattern.compile("Walls Fall: (\\d+):(\\d+)");
    private static final Pattern GAME_END_PATTERN = Pattern.compile("Game End: (\\d+):(\\d+)");
    private static final Pattern MW_INGAME_PATTERN = Pattern.compile("[0-9]+\\sF\\.\\sKills?\\s[0-9]+\\sF\\.\\sAssists?");
    private static final Pattern PREGAME_LOBBY_PATTERN = Pattern.compile("Players:\\s*[0-9]+/[0-9]+");
    private static final Pattern WITHER_ALIVE_PATTERN = Pattern.compile("(\\[[BGRY]]) Wither HP: ([,\\d]+)");
    private static final Pattern REPLAY_MAP_PATTERN = Pattern.compile("Map: ([a-zA-Z0-9_ ]+)");

    private boolean triggeredWallsFallAlert = false;
    private boolean triggeredGameEndAlert = false;

    private int prevGameEndTime;

    private final List<MWTeam> aliveWithers = new ArrayList<>(4);
    private final List<MWTeam> aliveWithersView = Collections.unmodifiableList(aliveWithers);
    private int blueWitherHp;
    private int greenWitherHp;
    private int redWitherHp;
    private int yellowWitherHp;
    private String serverID = null;
    private boolean isInMwGame = false;
    private boolean isMWEnvironement = false;
    private boolean isReplayMode = false;
    private boolean isAtlasMode = false;
    private boolean isMWReplay = false;
    private String replayMap = null;
    private boolean isPreGameLobby = false;
    private boolean isPrepPhase = false;
    private boolean hasGameEnded = false;

    ScoreboardParser() {}

    void onGameStart() {
        triggeredWallsFallAlert = false;
        triggeredGameEndAlert = false;
    }

    void reset() {
        aliveWithers.clear();
        blueWitherHp = 0;
        greenWitherHp = 0;
        redWitherHp = 0;
        yellowWitherHp = 0;
        serverID = null;
        isInMwGame = false;
        isMWEnvironement = false;
        isReplayMode = false;
        isAtlasMode = false;
        isMWReplay = false;
        replayMap = null;
        isPreGameLobby = false;
        isPrepPhase = false;
        hasGameEnded = false;
    }

    void update() {
        final ScoreObjective objective = ScoreboardUtils.getActiveObjective();
        if (objective == null) {
            return;
        }
        final String title = objective.getDisplayName();
        final List<String> formattedLines = ScoreboardUtils.getFormattedSidebarText(objective);
        this.update(title, formattedLines);
    }

    void update(String title, List<String> formattedLines) {
        final String cleanTitle = StringUtil.removeFormattingCodes(title);
        final List<String> cleanLines = ScoreboardUtils.stripControlCodes(formattedLines);
        if (!cleanLines.isEmpty()) {
            final Matcher gameIDMatcher = GAME_ID_PATTERN.matcher(cleanLines.get(0));
            if (gameIDMatcher.find()) {
                serverID = gameIDMatcher.group(1);
            }
        }
        if (cleanTitle.contains("MEGA WALLS")) {
            isMWEnvironement = true;
            this.parseMegaWallsScoreboard(cleanLines);
        } else if (cleanTitle.contains("REPLAY")) {
            isReplayMode = true;
            this.parseReplayScoreboard(cleanLines);
        } else if (cleanTitle.contains("ATLAS")) {
            isReplayMode = true;
            isAtlasMode = true;
            this.parseReplayScoreboard(cleanLines);
        }
    }

    private void parseMegaWallsScoreboard(List<String> cleanLines) {
        if (cleanLines.size() >= 10) {
            if (MW_INGAME_PATTERN.matcher(cleanLines.get(9)).find()) {
                isInMwGame = true;
                this.parseMWTimeLine(cleanLines.get(1));
                this.parseWitherAndTeamsLines(cleanLines);
            } else {
                for (final String line : cleanLines) {
                    if (PREGAME_LOBBY_PATTERN.matcher(line).find()) {
                        isPreGameLobby = true;
                        return;
                    }
                }
            }
        }
    }

    private void parseMWTimeLine(String gameTimeLine) {
        final Matcher gameEndMatcher = GAME_END_PATTERN.matcher(gameTimeLine);
        if (gameEndMatcher.find()) {
            final int secLeft = Integer.parseInt(gameEndMatcher.group(1)) * 60 + Integer.parseInt(gameEndMatcher.group(2));
            final boolean skip = Math.abs(prevGameEndTime - secLeft) > 10;
            prevGameEndTime = secLeft;
            if (skip) {
                // this is here to fix the bug that fires events
                // at 06:00 and 01:00 instead of 05:00 and 00:00
                // It is caused by the client processing a new tick
                // and parsing the scoreboard in between scoreboard
                // packets
                return;
            }
            if (!triggeredGameEndAlert && secLeft == 5 * 60) {
                SoundUtil.playNotePling();
                ChatUtil.addChatMessage(EnumChatFormatting.YELLOW + "Game ends in 5 minutes!");
                triggeredGameEndAlert = true;
            } else if (secLeft == 0) {
                hasGameEnded = true;
            }
            return;
        }
        final Matcher wallsFallMatcher = WALLS_FALL_PATTERN.matcher(gameTimeLine);
        if (wallsFallMatcher.find()) {
            isPrepPhase = true;
            if (!triggeredWallsFallAlert && wallsFallMatcher.group(1).equals("00") && wallsFallMatcher.group(2).equals("10") && !Display.isActive()) {
                AFKSoundWarning.playWallsFallSound();
                triggeredWallsFallAlert = true;
            }
            return;
        }
        if (GATES_OPEN_PATTERN.matcher(gameTimeLine).find()) {
            isPrepPhase = true;
        }
    }

    private void parseWitherAndTeamsLines(List<String> cleanLines) {

        int eliminatedTeams = 0;
        for (int i = 3; i < 7; i++) {
            final String line = cleanLines.get(i);
            final Matcher matcher = WITHER_ALIVE_PATTERN.matcher(line);
            if (matcher.find()) {
                final String witherPrefix = matcher.group(1);
                final int witherHp = Integer.parseInt(matcher.group(2).replace(",", ""));
                switch (witherPrefix) {
                    case "[B]":
                        aliveWithers.add(MWTeam.BLUE);
                        blueWitherHp = witherHp;
                        break;
                    case "[G]":
                        aliveWithers.add(MWTeam.GREEN);
                        greenWitherHp = witherHp;
                        break;
                    case "[R]":
                        aliveWithers.add(MWTeam.RED);
                        redWitherHp = witherHp;
                        break;
                    case "[Y]":
                        aliveWithers.add(MWTeam.YELLOW);
                        yellowWitherHp = witherHp;
                        break;
                }
            } else {
                if (line.contains("eliminated!")) {
                    eliminatedTeams++;
                }
            }
        }

        if (eliminatedTeams == 3) {
            hasGameEnded = true;
        }

//            if (!triggeredKillCooldownReset && witherHP < 100 && !colorCode.isEmpty() && colorCode.equals(teamColor)) {
//                MWE.INSTANCE().getMweRenderers().killCooldownHUD.hideHUD();
//                triggeredKillCooldownReset = true;
//            }
//
//            if (MWEConfig.witherAlerts && witherHP < MWEConfig.witherAlertsThreshold) {
//                boolean playNotif = false;
//                switch (witherTeam) {
//                    case BLUE: {
//                        if (!triggeredBlueWitherAlert) {
//                            triggeredBlueWitherAlert = true;
//                            playNotif = true;
//                        }
//                        break;
//                    }
//                    case GREEN: {
//                        if (!triggeredGreenWitherAlert) {
//                            triggeredGreenWitherAlert = true;
//                            playNotif = true;
//                        }
//                        break;
//                    }
//                    case RED: {
//                        if (!triggeredRedWitherAlert) {
//                            triggeredRedWitherAlert = true;
//                            playNotif = true;
//                        }
//                        break;
//                    }
//                    case YELLOW: {
//                        if (!triggeredYellowWitherAlert) {
//                            triggeredYellowWitherAlert = true;
//                            playNotif = true;
//                        }
//                        break;
//                    }
//                }
//                if (playNotif) {
//                    ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "The " + witherTeam.getColorPrefix() + witherTeam.getName() + " Wither " + EnumChatFormatting.GREEN + "is below " + EnumChatFormatting.YELLOW + MWEConfig.witherAlertsThreshold + "HP!");
//                    SoundUtil.playNotePling();
//                }
//            }
//
//        if (aliveWithers.size() == 1) {
//            MWE.INSTANCE().getMweRenderers().lastWitherHPHUD.updateWitherHP(witherHP);
//        }

    }

    private void parseReplayScoreboard(List<String> cleanLines) {
        for (final String line : cleanLines) {
            final Matcher mapMatcher = REPLAY_MAP_PATTERN.matcher(line);
            if (mapMatcher.find()) {
                replayMap = mapMatcher.group(1);
            } else if (line.contains("Game: Mega Walls")) {
                isMWReplay = true;
            }
        }
    }

    public boolean isWitherAlive(@NotNull MWTeam team) {
        return aliveWithers.contains(team);
    }

    public boolean isOnlyOneWitherAlive() {
        return this.getWitherCount() == 1;
    }

    @Override
    public boolean isDeathmatch() {
        return isInMwGame && this.getWitherCount() == 0;
    }

    public boolean hasGameEnded() {
        return hasGameEnded;
    }

    @Override
    public boolean isPrepPhase() {
        return isPrepPhase;
    }

    @Override
    public boolean isMWEnvironement() {
        return isMWEnvironement;
    }

    @Override
    public boolean isReplayMode() {
        return isReplayMode;
    }

    @Override
    public boolean isAtlasMode() {
        return isAtlasMode;
    }

    @Override
    public boolean isMWReplay() {
        return isMWReplay;
    }

    public String getReplayMap() {
        return replayMap;
    }

    @Override
    public boolean isInSkyblock() {
        return false;
    }

    @Override
    public boolean isInMwGame() {
        return isInMwGame;
    }

    @Override
    public boolean isPreGameLobby() {
        return isPreGameLobby;
    }

    @Override
    public String getServerID() {
        return serverID;
    }

    @Override
    public @NotNull List<MWTeam> getAliveWithers() {
        return aliveWithersView;
    }

    @Override
    public int getWitherCount() {
        return aliveWithers.size();
    }

    int getBlueWitherHp() {
        return blueWitherHp;
    }

    int getGreenWitherHp() {
        return greenWitherHp;
    }

    int getRedWitherHp() {
        return redWitherHp;
    }

    int getYellowWitherHp() {
        return yellowWitherHp;
    }

}
