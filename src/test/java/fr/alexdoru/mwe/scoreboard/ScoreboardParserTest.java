package fr.alexdoru.mwe.scoreboard;

import fr.alexdoru.mwe.api.enums.MWTeam;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ScoreboardParserTest {

    @Test
    public void mainLobbyTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§e§lHYPIXEL";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8L2§84B",
                "",
                "Rank: §bMVP§0+§b",
                "Achievements: §e§e10,060",
                "Hypixel Level: §3404",
                "",
                "Lobby: §a1",
                "Players: §a26,83§a7",
                "",
                "§eSummer 2026",
                "Event Level: §61§600",
                "",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(0, parser.getAliveWithers().size());
        assertEquals("L24B", parser.getServerID());
        assertFalse(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertFalse(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void mwLobbyTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§e§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8L1§81C",
                "Class: §aPhoenix",
                "Class Points: §a§a4,503",
                "§1",
                "Kills: §a93,857",
                "Final Kills: §a2§a6,096",
                "Wins: §a4,214",
                "§2",
                "Coins: §625,588,§6668",
                "Mythic Favor: §e§e351",
                "§3",
                "§e§lHAPPY HOUR",
                "§62x coins for §a§l14:36",
                "§4",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(0, parser.getAliveWithers().size());
        assertEquals("L11C", parser.getServerID());
        assertTrue(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertFalse(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void mwPregameLobbyTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§f§lMEGA W§6§lA§e§lLLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "§0",
                "Map: §aStonehold",
                "Players: §a28/10§a0",
                "§1",
                "Starting in §a05§a:06§f if",
                "§a2§f more players join",
                "§2",
                "Selected Class:",
                "§aPhoenix",
                "§8",
                "§e§lHAPPY HOUR!",
                "§62x coins",
                "",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(0, parser.getAliveWithers().size());
        assertEquals("M22C", parser.getServerID());
        assertTrue(parser.isMWEnvironement());
        assertTrue(parser.isPreGameLobby());
        assertFalse(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void mwGateOpenTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Gates Open: §a§a00:06",
                "",
                "§6[Y] Wither§6 H§6P§7: §61,000",
                "§1[B] §fWither§1§1 HP§7: §11,000",
                "§2[G] §fWither§2§2 HP§7: §21,000",
                "§c[R] §fWither§c§c HP§7: §c1,000",
                "",
                "§a0 §fKills §a0 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§60 §fCoins",
                "§60 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(4, parser.getAliveWithers().size());
        assertEquals("M22C", parser.getServerID());
        assertTrue(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertTrue(parser.isInMwGame());
        assertTrue(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void mwPrepPhaseTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Walls Fall: §a§a06:14",
                "",
                "§6[Y] Wither§6 H§6P§7: §61,000",
                "§1[B] §fWither§1§1 HP§7: §11,000",
                "§2[G] §fWither§2§2 HP§7: §21,000",
                "§c[R] §fWither§c§c HP§7: §c1,000",
                "",
                "§a0 §fKills §a0 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§60 §fCoins",
                "§60 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(4, parser.getAliveWithers().size());
        assertEquals("M22C", parser.getServerID());
        assertTrue(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertTrue(parser.isInMwGame());
        assertTrue(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void mwEnrageOnTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Enrage Off: §a§a03:43",
                "",
                "§6[Y] Wither§6 H§6P§7: §6870",
                "§1[B] §fWither§1§1 HP§7: §1892",
                "§2[G] §fWither§2§2 HP§7: §2892",
                "§c[R] §fWither§c§c HP§7: §c892",
                "",
                "§a0 §fKills §a2 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§61,056 §fCoins",
                "§61 §fClass Point",
                "",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(4, parser.getAliveWithers().size());
        assertEquals("M22C", parser.getServerID());
        assertTrue(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertTrue(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void mwWitherAliveTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Game End: §a31§a:12",
                "",
                "§6[Y] Wither§6 H§6P§7: §6870",
                "§1[B] §fWither§1§1 HP§7: §1892",
                "§2[G] §fWither§2§2 HP§7: §2892",
                "§c[R] §fPlayers§7: §c11",
                "",
                "§a0 §fKills §a2 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§61,056 §fCoins",
                "§61 §fClass Point",
                "",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(3, parser.getAliveWithers().size());
        assertEquals("M22C", parser.getServerID());
        assertTrue(parser.isWitherAlive(MWTeam.BLUE));
        assertTrue(parser.isWitherAlive(MWTeam.GREEN));
        assertFalse(parser.isWitherAlive(MWTeam.RED));
        assertTrue(parser.isWitherAlive(MWTeam.YELLOW));
        assertTrue(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertTrue(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void mwDeathmatchTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Game End: §a31§a:12",
                "",
                "§6[Y] Players§7:§7 §65",
                "§1[B] §fPlayers§7: §19",
                "§2[G] §fPlayers§7: §27",
                "§c[R] §fPlayers§7: §c11",
                "",
                "§a6 §fKills §a8 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§614,769 §fCoins",
                "§63 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(0, parser.getAliveWithers().size());
        assertEquals("M22C", parser.getServerID());
        assertTrue(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertTrue(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertTrue(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void mwGameEnd3TeamsDeadTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Game End: §a03§a:38",
                "",
                "§6[Y] §fPlayers§7: §63",
                "§7Blue eliminate§7d!",
                "§7Green eliminat§7ed!",
                "§7Red eliminated§7!",
                "",
                "§a6 §fKills §a8 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§615,509 §fCoins",
                "§616 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(0, parser.getAliveWithers().size());
        assertEquals("M22C", parser.getServerID());
        assertTrue(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertTrue(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertTrue(parser.isDeathmatch());
        assertTrue(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void mwGameEndDrawTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Game End: §a00§a:00",
                "",
                "§6[Y] §fPlayers§7: §63",
                "§1[B] §fPlayers§7: §19",
                "§7Green eliminat§7ed!",
                "§7Red eliminated§7!",
                "",
                "§a6 §fKills §a8 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§615,509 §fCoins",
                "§616 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(0, parser.getAliveWithers().size());
        assertEquals("M22C", parser.getServerID());
        assertTrue(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertTrue(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertTrue(parser.isDeathmatch());
        assertTrue(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertFalse(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertNull(parser.getReplayMap());
    }

    @Test
    public void AtlasTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§e§lATLAS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8m1§820R",
                "§7Replay §7from §7mini78CR",
                "",
                "Date: §a08/30/20§a26",
                "Time: §a21:46 (E§aST)",
                "",
                "Game: §aSkyWars",
                "Mode: §aMini Nor§amal",
                "",
                "Map: §aCopper Cl§aiff",
                "",
                "Longest Tick: §a§a53ms",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(0, parser.getAliveWithers().size());
        assertEquals("m120R", parser.getServerID());
        assertFalse(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertFalse(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertTrue(parser.isAtlasMode());
        assertTrue(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertEquals("Copper Cliff", parser.getReplayMap());
    }

    @Test
    public void ReplayTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§e§lREPLAY";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8m3§81CJ",
                "§7Replay §7from §7mini76CU",
                "",
                "Date: §a08/31/20§a26",
                "Time: §a09:30 (E§aST)",
                "",
                "Game: §aSkyWars",
                "Mode: §aSolo Nor§amal",
                "",
                "Map: §aSandwars",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(0, parser.getAliveWithers().size());
        assertEquals("m31CJ", parser.getServerID());
        assertFalse(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertFalse(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertTrue(parser.isReplayMode());
        assertFalse(parser.isMWReplay());
        assertEquals("Sandwars", parser.getReplayMap());
    }

    @Test
    public void mwReplayTest() {
        final ScoreboardParser parser = new ScoreboardParser();
        final String title = "§e§lREPLAY";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M4§8C",
                "§7Replay §7from §7mega34B",
                "",
                "Date: §a08/30/20§a26",
                "Time: §a14:45 (E§aST)",
                "",
                "Game: §aMega Wal§als",
                "Mode: §aStandard",
                "",
                "Map: §aShadowsto§ane",
                "§ewww.hypixel.ne§et"
        ));
        parser.update(title, formattedLines);
        assertEquals(0, parser.getAliveWithers().size());
        assertEquals("M4C", parser.getServerID());
        assertFalse(parser.isMWEnvironement());
        assertFalse(parser.isPreGameLobby());
        assertFalse(parser.isInMwGame());
        assertFalse(parser.isPrepPhase());
        assertFalse(parser.isDeathmatch());
        assertFalse(parser.hasGameEnded());
        assertFalse(parser.isAtlasMode());
        assertTrue(parser.isReplayMode());
        assertTrue(parser.isMWReplay());
        assertEquals("Shadowstone", parser.getReplayMap());
    }

}