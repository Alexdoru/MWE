package fr.alexdoru.mwe.scoreboard;

import fr.alexdoru.mwe.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ScoreboardTracker {

    /**
     * Turns true from the moment the player gets tp to the cage to the end of the game
     * False in the pre game lobby
     */
    private static boolean isInMwGame = false;
    /** True in the pre game lobby in mega walls */
    private static boolean isPreGameLobby = false;
    /** True during the preparation phase of a mega walls game */
    private static boolean isPrepPhase = false;
    /** True in mega walls lobbys, games etc */
    private static boolean isMWEnvironement = false;
    /** True when in the Replay Mode, including Atlas */
    private static boolean isReplayMode = false;
    /** True when in the Atlas Mode */
    private static boolean isAtlasMode = false;
    /** True when in a mega walls replay **/
    private static boolean isMWReplay = false;
    /** True when is Skyblock */
    private static boolean isInSkyblock = false;

    private static ScoreboardParser parser = new ScoreboardParser();

    private String prevGameId = null;
    private boolean prevHasGameEnded = false;
    private int prevAmountWitherAlive = 4;

    @SubscribeEvent
    public void onGameStart(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_START) {
            ScoreboardParser.onGameStart();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        Minecraft.getMinecraft().mcProfiler.startSection("MWE Scoreboard");
        parser = new ScoreboardParser();
        isInMwGame = parser.isInMwGame();
        isMWEnvironement = parser.isMWEnvironement();
        isPreGameLobby = parser.isPreGameLobby();
        isPrepPhase = parser.isPrepPhase();
        isReplayMode = parser.isReplayMode();
        isAtlasMode = parser.isAtlasMode();
        isMWReplay = parser.isMWReplay();
        isInSkyblock = parser.isInSkyblock();
        if (isMWReplay) {
            GuiManager.baseLocationHUD.setCurrentMap(parser.getReplayMap());
        }
        this.fireScoreboardRelatedEvents();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    private void fireScoreboardRelatedEvents() {
        final String gameId = parser.getGameId();
        final boolean hasgameended = parser.hasGameEnded();
        final int amountWitherAlive = parser.getAliveWithers().size();

        if (gameId == null) { // not in MW game

            if (this.prevGameId != null) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.DISCONNECT));
            }

        } else { // is in MW game

            if (amountWitherAlive == 1 && prevAmountWitherAlive > 1) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.THIRD_WITHER_DEATH));
            } else if (amountWitherAlive == 0 && prevAmountWitherAlive > 0) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.DEATHMATCH_START));
            }

            if (!gameId.equals(this.prevGameId)) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.CONNECT));
            }

        }

        if (hasgameended && !this.prevHasGameEnded) {
            MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.GAME_END));
        }

        this.prevGameId = gameId;
        this.prevHasGameEnded = hasgameended;
        this.prevAmountWitherAlive = amountWitherAlive;
    }

    public static ScoreboardParser getParser() {
        return parser;
    }

    public static boolean isInMwGame() {
        return isInMwGame;
    }

    public static boolean isPreGameLobby() {
        return isPreGameLobby;
    }

    public static boolean isPrepPhase() {
        return isPrepPhase;
    }

    public static boolean isMWEnvironement() {
        return isMWEnvironement;
    }

    public static boolean isReplayMode() {
        return isReplayMode;
    }

    public static boolean isAtlasMode() {
        return isAtlasMode;
    }

    public static boolean isMWReplay() {
        return isMWReplay;
    }

    public static boolean isInSkyblock() {
        return isInSkyblock;
    }

}