package fr.alexdoru.mwe.scoreboard;

import com.google.common.collect.ImmutableMap;
import fr.alexdoru.mwe.api.IScoreboardParser;
import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.features.AFKSoundWarning;
import fr.alexdoru.mwe.gui.MWERenderers;
import fr.alexdoru.mwe.utils.SoundUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private static final Map<String, MWTeam> TEAM_PREFIXES = new ImmutableMap.Builder<String, MWTeam>()
            .put("[B]", MWTeam.BLUE)
            .put("[G]", MWTeam.GREEN)
            .put("[R]", MWTeam.RED)
            .put("[Y]", MWTeam.YELLOW)
            .build();

    private boolean triggeredBlueWitherAlert = false;
    private boolean triggeredGreenWitherAlert = false;
    private boolean triggeredRedWitherAlert = false;
    private boolean triggeredYellowWitherAlert = false;
    private boolean triggeredWallsFallAlert = false;
    private boolean triggeredGameEndAlert = false;
    private boolean triggeredKillCooldownReset = false;

    private int prevGameEndTime;

    private final List<MWTeam> aliveWithers = new ArrayList<>(4);
    private final List<MWTeam> aliveWithersView = Collections.unmodifiableList(aliveWithers);
    private String serverID = null;
    private boolean isInMwGame = false;
    private boolean isMWEnvironement = false;
    private boolean isReplayMode = false;
    private boolean isAtlasMode = false;
    private boolean isMWReplay = false;
    private String replayMap = null;
    private boolean isInSkyblock = false;
    private boolean isPreGameLobby = false;
    private boolean isPrepPhase = false;
    private boolean hasGameEnded = false;

    void onGameStart() {
        triggeredBlueWitherAlert = false;
        triggeredGreenWitherAlert = false;
        triggeredRedWitherAlert = false;
        triggeredYellowWitherAlert = false;
        triggeredWallsFallAlert = false;
        triggeredGameEndAlert = false;
        triggeredKillCooldownReset = false;
    }

    void reset() {
        aliveWithers.clear();
        serverID = null;
        isInMwGame = false;
        isMWEnvironement = false;
        isReplayMode = false;
        isAtlasMode = false;
        isMWReplay = false;
        replayMap = null;
        isInSkyblock = false;
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
        final String cleanTitle = StringUtil.removeFormattingCodes(title);
        final List<String> formattedLines = ScoreboardUtils.getFormattedSidebarText(objective);
        final List<String> cleanLines = ScoreboardUtils.stripControlCodes(formattedLines);

        if (!cleanLines.isEmpty()) {
            final Matcher gameIDMatcher = GAME_ID_PATTERN.matcher(cleanLines.get(0));
            if (gameIDMatcher.find()) {
                serverID = gameIDMatcher.group(1);
            }
        }

        if (cleanTitle.contains("MEGA WALLS")) {
            isMWEnvironement = true;
            final String teamColor = StringUtil.getLastColorCodeBefore(title, "MEGA WALLS");
            this.parseMegaWallsScoreboard(formattedLines, cleanLines, teamColor);
        } else if (cleanTitle.contains("REPLAY")) {
            isReplayMode = true;
            this.parseReplayScoreboard(cleanLines);
        } else if (cleanTitle.contains("ATLAS")) {
            isReplayMode = true;
            isAtlasMode = true;
            this.parseReplayScoreboard(cleanLines);
        } else if (cleanTitle.contains("SKYBLOCK")) {
            isInSkyblock = true;
        }

    }

    private void parseMegaWallsScoreboard(List<String> formattedLines, List<String> cleanLines, String teamColor) {

        if (cleanLines.size() < 10) {
            return;
        }

        if (MW_INGAME_PATTERN.matcher(cleanLines.get(9)).find()) {
            isInMwGame = true;
        } else {
            for (final String line : cleanLines) {
                if (PREGAME_LOBBY_PATTERN.matcher(line).find()) {
                    isPreGameLobby = true;
                    return;
                }
            }
        }

        this.parseMWTimeLine(cleanLines.get(1));
        this.parseWitherAndTeamsLines(teamColor, formattedLines, cleanLines);
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

    private void parseWitherAndTeamsLines(String teamColor, List<String> formattedLines, List<String> cleanLines) {
        int eliminatedTeams = 0;
        int witherHP = 1000;

        for (int i = 3; i < 7; i++) {

            final String line = cleanLines.get(i);
            /*Wither alive detection*/
            final String colorCode;
            final MWTeam witherTeam;
            final Matcher matcher = WITHER_ALIVE_PATTERN.matcher(line);
            if (matcher.find()) {
                colorCode = StringUtil.getLastColorCodeBefore(formattedLines.get(i), "[");
                witherTeam = TEAM_PREFIXES.get(matcher.group(1));
                aliveWithers.add(witherTeam);
                witherHP = Integer.parseInt(matcher.group(2).replace(",", ""));
            } else {
                if (line.contains("eliminated!")) {
                    eliminatedTeams++;
                }
                continue;
            }

            if (!triggeredKillCooldownReset && witherHP < 100 && !colorCode.isEmpty() && colorCode.equals(teamColor)) {
                MWERenderers.killCooldownHUD.hideHUD();
                triggeredKillCooldownReset = true;
            }

            if (MWEConfig.witherAlerts && witherHP < MWEConfig.witherAlertsThreshold) {
                boolean playNotif = false;
                switch (witherTeam) {
                    case BLUE: {
                        if (!triggeredBlueWitherAlert) {
                            triggeredBlueWitherAlert = true;
                            playNotif = true;
                        }
                        break;
                    }
                    case GREEN: {
                        if (!triggeredGreenWitherAlert) {
                            triggeredGreenWitherAlert = true;
                            playNotif = true;
                        }
                        break;
                    }
                    case RED: {
                        if (!triggeredRedWitherAlert) {
                            triggeredRedWitherAlert = true;
                            playNotif = true;
                        }
                        break;
                    }
                    case YELLOW: {
                        if (!triggeredYellowWitherAlert) {
                            triggeredYellowWitherAlert = true;
                            playNotif = true;
                        }
                        break;
                    }
                }
                if (playNotif) {
                    ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "The " + witherTeam.getColorPrefix() + witherTeam.getName() + " Wither " + EnumChatFormatting.GREEN + "is below " + EnumChatFormatting.YELLOW + MWEConfig.witherAlertsThreshold + "HP!");
                    SoundUtil.playNotePling();
                }
            }

        }

        if (eliminatedTeams == 3) {
            hasGameEnded = true;
        }

        if (aliveWithers.size() == 1) {
            MWERenderers.lastWitherHPHUD.updateWitherHP(witherHP);
        }
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
        return isInSkyblock;
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

}
