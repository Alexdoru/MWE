package fr.alexdoru.mwe.scoreboard;

import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.gui.MWERenderers;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardTracker {

    private static final ScoreboardParser PARSER = new ScoreboardParser();

    private boolean prevIsInMW = false;
    private boolean prevHasGameEnded = false;
    private int prevAmountWitherAlive = 4;

    @SubscribeEvent
    public void onGameStart(MegaWallsGameEvent event) {
        if (event.type == Type.GAME_START) {
            PARSER.onGameStart();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft.getMinecraft().mcProfiler.startSection("MWE Scoreboard");
            PARSER.reset();
            PARSER.update();
            if (PARSER.isMWReplay()) {
                MWERenderers.baseLocationHUD.setCurrentMap(PARSER.getReplayMap());
            }
            this.fireScoreboardRelatedEvents();
            Minecraft.getMinecraft().mcProfiler.endSection();
        }
    }

    private void fireScoreboardRelatedEvents() {
        final boolean isInMW = PARSER.isInMwGame();
        final boolean hasgameended = PARSER.hasGameEnded();
        final int amountWitherAlive = PARSER.getAliveWithers().size();

        if (isInMW) {

            if (amountWitherAlive == 1 && prevAmountWitherAlive > 1) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(Type.THIRD_WITHER_DEATH));
            } else if (amountWitherAlive == 0 && prevAmountWitherAlive > 0) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(Type.DEATHMATCH_START));
            }

            if (!this.prevIsInMW) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(Type.CONNECT));
            }

        } else {

            if (this.prevIsInMW) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(Type.DISCONNECT));
            }

        }

        if (hasgameended && !this.prevHasGameEnded) {
            MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(Type.GAME_END));
        }

        this.prevIsInMW = isInMW;
        this.prevHasGameEnded = hasgameended;
        this.prevAmountWitherAlive = amountWitherAlive;
    }

    @NotNull
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

    public static String getServerID() {
        return PARSER.getServerID();
    }

}