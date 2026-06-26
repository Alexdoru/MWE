package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

public final class FinalKillCounterManager {

    private FinalKillCounter fkCounter;

    @SubscribeEvent
    public void onMwGame(MegaWallsGameEvent event) {

        // to fix the bug where the FKCounter doesn't work properly if you play two games in a row on a server with the same serverID
        if (event.type == Type.GAME_START) {
            final String serverID = ScoreboardTracker.getServerID();
            if (serverID != null) {
                this.fkCounter = new FinalKillCounter(serverID);
            }
            if (this.fkCounter != null) {
                this.fkCounter.setTeamPrefixes();
            }
        } else if (event.type == Type.CONNECT) {
            final String serverID = ScoreboardTracker.getServerID();
            if (this.fkCounter == null || !this.fkCounter.getServerID().equals(serverID)) {
                this.fkCounter = new FinalKillCounter(serverID);
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
