package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.client.gui.ScaledResolution;

public class KillCooldownHUD extends AbstractRenderer {

    private long lastkilltime = 0;
    private final TimerUtil timerKillCooldown = new TimerUtil(60000L);

    public KillCooldownHUD() {
        super(MWEConfig.killCooldownHUDPosition);
    }

    /**
     * Called to draw the HUD, when you use /kill
     */
    public void drawCooldownHUD() {
        if (timerKillCooldown.update()) {
            lastkilltime = System.currentTimeMillis();
        }
    }

    public void hideHUD() {
        lastkilltime = 0;
    }

    @Override
    public void render(ScaledResolution resolution) {
        final int timeleft = 60 - ((int) (System.currentTimeMillis() - lastkilltime)) / 1000;
        final String displayText = "/kill cooldown : " + timeleft + "s";
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, mc.fontRendererObj.getStringWidth(displayText), mc.fontRendererObj.FONT_HEIGHT);
        mc.fontRendererObj.drawStringWithShadow(displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), MWEConfig.killCooldownHUDColor);
    }

    @Override
    public void renderDummy() {
        mc.fontRendererObj.drawStringWithShadow("/kill cooldown : 60s", this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), MWEConfig.killCooldownHUDColor);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return currentTimeMillis - lastkilltime < 60000L && ScoreboardTracker.isInMwGame();
    }

}
