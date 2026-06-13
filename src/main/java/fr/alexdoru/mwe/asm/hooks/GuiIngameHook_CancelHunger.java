package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.StringUtil;

public class GuiIngameHook_CancelHunger {

    public static String cancelHungerTitle(String subtitle) {
        if (MWEConfig.hideHungerTitleInMW && ScoreboardTracker.isInMwGame() && subtitle.contains("Get to the center to stop the hunger")) {
            return "";
        }
        if (MWEConfig.hideAutomatonTitleInMW && ScoreboardTracker.isInMwGame() && StringUtil.removeFormattingCodes(subtitle).contains("|||||")) {
            return "";
        }
        return subtitle;
    }

}
