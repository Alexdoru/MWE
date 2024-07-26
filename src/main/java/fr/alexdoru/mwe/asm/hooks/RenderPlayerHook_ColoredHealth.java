package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.ColorUtil;
import net.minecraft.client.entity.AbstractClientPlayer;

@SuppressWarnings("unused")
public class RenderPlayerHook_ColoredHealth {

    public static StringBuilder getColoredScore(StringBuilder str, int score, AbstractClientPlayer entity) {
        if (MWEConfig.coloredScoreAboveHead) {
            if (ScoreboardTracker.isInMwGame()) {
                return str.append(ColorUtil.getHPColor(44, score)).append(score);
            } else {
                return str.append(ColorUtil.getHPColor(entity.getMaxHealth(), score)).append(score);
            }
        }
        return str.append(score);
    }

}
