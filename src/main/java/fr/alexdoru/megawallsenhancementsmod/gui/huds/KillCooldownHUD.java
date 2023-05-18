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
    public void render(ScaledResolution resolution) {
        if (timerUpdateText.update()) {
            final int timeleft = 60 - ((int) (System.currentTimeMillis() - lastkilltime)) / 1000;
            displayText = EnumChatFormatting.DARK_RED + "/kill cooldown : " + timeleft + "s";
        }
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, mc.fontRendererObj.getStringWidth(displayText), mc.fontRendererObj.FONT_HEIGHT);
        mc.fontRendererObj.drawStringWithShadow(displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public void renderDummy() {
        mc.fontRendererObj.drawStringWithShadow(DUMMY_TEXT, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return currentTimeMillis - lastkilltime < 60000L && FKCounterMod.isInMwGame;
    }

}
