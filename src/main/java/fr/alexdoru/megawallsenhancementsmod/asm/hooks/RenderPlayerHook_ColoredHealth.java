package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.ColorUtil;
import net.minecraft.client.entity.AbstractClientPlayer;

@SuppressWarnings("unused")
public class RenderPlayerHook_ColoredHealth {

    public static StringBuilder getColoredScore(StringBuilder str, int score, AbstractClientPlayer entity) {
        if (ConfigHandler.coloredScoreAboveHead) {
            if (ScoreboardTracker.isInMwGame()) {
                return str.append(ColorUtil.getHPColor(44, score)).append(score);
            } else {
                return str.append(ColorUtil.getHPColor(entity.getMaxHealth(), score)).append(score);
            }
        }
        return str.append(score);
    }

}
