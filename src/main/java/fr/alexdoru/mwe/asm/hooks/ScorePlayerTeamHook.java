package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;

@SuppressWarnings("unused")
public class ScorePlayerTeamHook {

    public static String spoofSidebarLine(String playername) {
        if (MWEConfig.witherHUDinSidebar && MWEConfig.lastWitherHUDPosition.isEnabled() && ScoreboardTracker.isInMwGame() && "\ud83d\udca3".equals(playername) && ScoreboardTracker.getParser().isOnlyOneWitherAlive()) {
            return GuiManager.lastWitherHPHUD.displayText;
        }
        if (MWEConfig.fkcounterHUDinSidebar && MWEConfig.fkcounterHUDPosition.isEnabled() && ScoreboardTracker.isInMwGame() && "\ud83d\udc7d".equals(playername) && FinalKillCounter.getGameId() != null) {
            return GuiManager.fkCounterHUD.displayText;
        }
        return null;
    }

// Fake Playername | Red number at end of line
//\ud83c\udf82 points : '1
//\ud83c\udf89 points : '2
//\ud83c\udf81 points : '3
//\ud83d\udc79 points : '4
//\ud83c\udfc0 points : '5
//\u26bd       points : '6
//\ud83c\udf6d points : '7
//\ud83c\udf20 points : '8
//\ud83d\udc7e points : '9
//\ud83d\udc0d points : '10
//\ud83d\udd2e points : '11
//\ud83d\udc7d points : '12
//\ud83d\udca3 points : '13
//\ud83c\udf6b points : '14
//\ud83d\udd2b points : '15

}
