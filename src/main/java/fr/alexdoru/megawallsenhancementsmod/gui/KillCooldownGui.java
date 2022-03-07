package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.util.EnumChatFormatting;

public class KillCooldownGui extends MyCachedGui {

    private static final String DUMMY_TEXT = EnumChatFormatting.DARK_RED + "/kill cooldown : 60s";
    public static KillCooldownGui instance;
    private static long lastkilltime = 0;
    private static long lastupdate = 0;

    public KillCooldownGui() {
        instance = this;
        guiPosition = ConfigHandler.killcooldownHUDPosition;
    }

    /**
     * Called to draw the gui, when you use /kill
     */
    public static void drawCooldownGui() {
        final long time = System.currentTimeMillis();
        if (!(time - lastkilltime < 60000L)) { // doesn't update the cooldown if you used /kill in the last 60 seconds
            lastkilltime = time;
        }
    }

    public static void hideGUI() {
        lastkilltime = 0;
    }

    @Override
    public void updateDisplayText() {
        int timeleft = 60 - ((int) (System.currentTimeMillis() - lastkilltime)) / 1000;
        displayText = EnumChatFormatting.DARK_RED + "/kill cooldown : " + timeleft + "s";
    }

    @Override
    public void render() {
        final long time = System.currentTimeMillis();
        if (time - lastupdate >= 1000L) {
            updateDisplayText();
            lastupdate = time;
        }
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        frObj.drawStringWithShadow(displayText, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public void renderDummy() {
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        frObj.drawStringWithShadow(DUMMY_TEXT, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public boolean isEnabled() {
        return System.currentTimeMillis() - lastkilltime < 60000L && FKCounterMod.isInMwGame;
    }

}
