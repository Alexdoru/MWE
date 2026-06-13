package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

public final class FinalKillCounterManager {

    private FinalKillCounter fkCounter;

    @SubscribeEvent
    public void onMwGame(MegaWallsGameEvent event) {

        // to fix the bug where the FKCounter doesn't work properly if you play two games in a row on a server with the same serverID
        if (event.type == MegaWallsGameEvent.Type.GAME_START) {
            final String gameId = ScoreboardTracker.getParser().getGameId();
            if (gameId != null) {
                this.fkCounter = new FinalKillCounter(gameId);
            }
            if (this.fkCounter != null) {
                this.fkCounter.setTeamPrefixes();
            }
        } else if (event.type == MegaWallsGameEvent.Type.CONNECT) {
            final String gameId = ScoreboardTracker.getParser().getGameId();
            if (this.fkCounter == null || !this.fkCounter.getGameId().equals(gameId)) {
                this.fkCounter = new FinalKillCounter(gameId);
            }
            // this is here to fix the bug where the killcounter doesn't work if you re-start your minecraft during a game of MW
            // or if you changed your colors for the teams in your MW settings and rejoined the game
            this.fkCounter.setTeamPrefixes();
        }

    }

    @Nullable
    public FinalKillCounter getFkCounter() {
        return fkCounter;
    }

}
