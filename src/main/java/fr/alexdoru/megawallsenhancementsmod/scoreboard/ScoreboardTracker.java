package fr.alexdoru.megawallsenhancementsmod.scoreboard;

import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ScoreboardTracker {

    /**
     * Turns true from the moment the player gets tp to the cage to the end of the game
     * False in the pre game lobby
     */
    public static boolean isInMwGame = false;
    /** True in the pre game lobby in mega walls */
    public static boolean isPreGameLobby = false;
    /** True during the preparation phase of a mega walls game */
    public static boolean isPrepPhase = false;
    /** True in mega walls lobbys, games etc */
    public static boolean isMWEnvironement = false;
    /** True when in the Replay Mode */
    public static boolean isReplayMode = false;
    /** True when is Skyblock */
    public static boolean isInSkyblock = false;

    private static ScoreboardParser parser = new ScoreboardParser();

    private String prevGameId = null;
    private boolean prevHasGameEnded = false;
    private int prevAmountWitherAlive = 4;

    public static ScoreboardParser getParser() {
        return parser;
    }

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

        parser = new ScoreboardParser();

        ScoreboardTracker.isInMwGame = parser.isInMwGame();
        ScoreboardTracker.isMWEnvironement = parser.isMWEnvironement();
        ScoreboardTracker.isPreGameLobby = parser.isPreGameLobby();
        ScoreboardTracker.isPrepPhase = parser.isPrepPhase();
        ScoreboardTracker.isReplayMode = parser.isReplayMode();
        ScoreboardTracker.isInSkyblock = parser.isInSkyblock();

        this.fireScoreboardRelatedEvents();

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

}