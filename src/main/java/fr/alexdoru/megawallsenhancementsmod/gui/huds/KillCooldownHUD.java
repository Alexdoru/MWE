package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.utils.TimerUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class KillCooldownHUD extends MyCachedHUD {

    public static KillCooldownHUD instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.DARK_RED + "/kill cooldown : 60s";
    private long lastkilltime = 0;
    private final TimerUtil timerKillCooldown = new TimerUtil(60000L);
    private final TimerUtil timerUpdateText = new TimerUtil(1000L);

    public KillCooldownHUD() {
        super(ConfigHandler.killCooldownHUDPosition);
        instance = this;
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
    public int getWidth() {
        return frObj.getStringWidth(DUMMY_TEXT);
    }

    @Override
    public void updateDisplayText() {
        final int timeleft = 60 - ((int) (System.currentTimeMillis() - lastkilltime)) / 1000;
        displayText = EnumChatFormatting.DARK_RED + "/kill cooldown : " + timeleft + "s";
    }

    @Override
    public void render(ScaledResolution resolution) {
        if (timerUpdateText.update()) {
            updateDisplayText();
        }
        final int[] absolutePos = this.guiPosition.getAbsolutePositionForRender(resolution, frObj.getStringWidth(displayText), frObj.FONT_HEIGHT);
        frObj.drawStringWithShadow(displayText, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public void renderDummy() {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition();
        frObj.drawStringWithShadow(DUMMY_TEXT, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return currentTimeMillis - lastkilltime < 60000L && FKCounterMod.isInMwGame;
    }

}
