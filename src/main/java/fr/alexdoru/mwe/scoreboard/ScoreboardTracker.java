package fr.alexdoru.mwe.scoreboard;

import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.gui.MWERendererManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ScoreboardTracker {

    private static final ScoreboardParser PARSER = new ScoreboardParser();

    private String prevGameId = null;
    private boolean prevHasGameEnded = false;
    private int prevAmountWitherAlive = 4;

    @SubscribeEvent
    public void onGameStart(MegaWallsGameEvent event) {
        if (event.type == MegaWallsGameEvent.Type.GAME_START) {
            PARSER.onGameStart();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft.getMinecraft().mcProfiler.startSection("MWE Scoreboard");
            PARSER.reset();
            PARSER.update();
            if (PARSER.isMWReplay()) {
                MWERendererManager.baseLocationHUD.setCurrentMap(PARSER.getReplayMap());
            }
            this.fireScoreboardRelatedEvents();
            Minecraft.getMinecraft().mcProfiler.endSection();
        }
    }

    private void fireScoreboardRelatedEvents() {
        final String gameId = PARSER.getGameId();
        final boolean hasgameended = PARSER.hasGameEnded();
        final int amountWitherAlive = PARSER.getAliveWithers().size();

        if (gameId == null) { // not in MW game

            if (this.prevGameId != null) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.Type.DISCONNECT));
            }

        } else { // is in MW game

            if (amountWitherAlive == 1 && prevAmountWitherAlive > 1) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.Type.THIRD_WITHER_DEATH));
            } else if (amountWitherAlive == 0 && prevAmountWitherAlive > 0) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.Type.DEATHMATCH_START));
            }

            if (!gameId.equals(this.prevGameId)) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.Type.CONNECT));
            }

        }

        if (hasgameended && !this.prevHasGameEnded) {
            MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.Type.GAME_END));
        }

        this.prevGameId = gameId;
        this.prevHasGameEnded = hasgameended;
        this.prevAmountWitherAlive = amountWitherAlive;
    }

    public static ScoreboardParser getParser() {
        return PARSER;
    }

    public static boolean isInMwGame() {
        return PARSER.isInMwGame();
    }

    public static boolean isPreGameLobby() {
        return PARSER.isPreGameLobby();
    }

    public static boolean isPrepPhase() {
        return PARSER.isPrepPhase();
    }

    public static boolean isMWEnvironement() {
        return PARSER.isMWEnvironement();
    }

    public static boolean isReplayMode() {
        return PARSER.isReplayMode();
    }

    public static boolean isAtlasMode() {
        return PARSER.isAtlasMode();
    }

    public static boolean isMWReplay() {
        return PARSER.isMWReplay();
    }

    public static boolean isInSkyblock() {
        return PARSER.isInSkyblock();
    }

}