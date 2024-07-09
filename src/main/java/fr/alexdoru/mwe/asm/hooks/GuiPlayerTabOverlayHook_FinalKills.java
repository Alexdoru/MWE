package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook_FinalKills {

    private static int finalsScoreWidth = 0;

    public static void resetFinalsScoreWidth() {
        finalsScoreWidth = 0;
    }

    public static void computeFKScoreWidth(int playerFinalkills) {
        if (ScoreboardTracker.isInMwGame() && ConfigHandler.fkcounterHUDTablist) {
            if (playerFinalkills != 0) {
                finalsScoreWidth = Math.max(finalsScoreWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(" " + playerFinalkills));
            }
        }
    }

    public static int getRenderScoreWidth() {
        return finalsScoreWidth;
    }

    public static void renderFinals(int playerFinalkills, int j2, int i, int k2) {
        if (!ConfigHandler.fkcounterHUDTablist || playerFinalkills == 0 || !ScoreboardTracker.isInMwGame()) {
            return;
        }
        final String s1 = EnumChatFormatting.GOLD + " " + playerFinalkills;
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s1, j2 + i + 1, k2, 0xFFFFFF);
    }

}
