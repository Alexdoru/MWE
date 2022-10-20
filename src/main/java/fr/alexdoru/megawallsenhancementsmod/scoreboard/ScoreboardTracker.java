package fr.alexdoru.megawallsenhancementsmod.scoreboard;

import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ScoreboardTracker {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static ScoreboardParser mwScoreboardParser = new ScoreboardParser(null);
    private static String prevGameId = null;
    private static boolean prevHasGameEnded = false;
    private static int prevAmountWitherAlive = 4;

    public static ScoreboardParser getMwScoreboardParser() {
        return mwScoreboardParser;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        if (mc.theWorld == null) {
            FKCounterMod.isInMwGame = false;
            FKCounterMod.isMWEnvironement = false;
            FKCounterMod.preGameLobby = false;
            FKCounterMod.isitPrepPhase = false;
            return;
        }

        final Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            FKCounterMod.isInMwGame = false;
            FKCounterMod.isMWEnvironement = false;
            FKCounterMod.preGameLobby = false;
            FKCounterMod.isitPrepPhase = false;
            return;
        }

        mwScoreboardParser = new ScoreboardParser(scoreboard);

        final String gameId = mwScoreboardParser.getGameId();
        final boolean hasgameended = mwScoreboardParser.hasGameEnded();
        final int amountWitherAlive = mwScoreboardParser.getAliveWithers().size();
        FKCounterMod.isInMwGame = mwScoreboardParser.isInMwGame();
        FKCounterMod.isMWEnvironement = mwScoreboardParser.isMWEnvironement();
        FKCounterMod.preGameLobby = mwScoreboardParser.isPreGameLobby();
        FKCounterMod.isitPrepPhase = mwScoreboardParser.isitPrepPhase();

        if (gameId == null) { // not in MW game

            if (prevGameId != null) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.DISCONNECT));
            }

        } else { // is in MW game

            if (amountWitherAlive == 1 && prevAmountWitherAlive > 1) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.THIRD_WITHER_DEATH));
            } else if (amountWitherAlive == 0 && prevAmountWitherAlive > 0) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.DEATHMATCH_START));
            }

            if (!gameId.equals(prevGameId)) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.CONNECT));
            }

        }

        if (hasgameended && !prevHasGameEnded) {
            MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.GAME_END));
        }

        prevGameId = gameId;
        prevHasGameEnded = hasgameended;
        prevAmountWitherAlive = amountWitherAlive;

    }

}
