package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.ScoreboardEvent;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.util.EnumChatFormatting;

public class LastWitherHPGui extends MyCachedGui {

    public static LastWitherHPGui instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "Wither dies in 148s";
    private static String color;
    private static long lastTextUpdate = 0;
    private static long lastWitherHPUpdate = 0;
    private static int prevWitherHp = 0;

    public LastWitherHPGui() {
        instance = this;
        guiPosition = ConfigHandler.lastWitherHUDPosition;
    }

    public void updateWitherHP(int witherHP) {
        if (prevWitherHp != witherHP) {
            prevWitherHp = witherHP;
            lastWitherHPUpdate = System.currentTimeMillis();
            color = "\u00a7" + ScoreboardEvent.getMwScoreboardParser().getAliveWithers().get(0);
            updateDisplayText();
        }
    }

    @Override
    public void updateDisplayText() {
        long time = System.currentTimeMillis();
        int timeToDie = prevWitherHp * 5 / 8 - (int) ((time - lastWitherHPUpdate) / 1000L);
        displayText = color + "Wither dies in " + timeToDie + "s";
        lastTextUpdate = time;
    }

    @Override
    public void render() {
        if (System.currentTimeMillis() - lastTextUpdate >= 1000L) {
            updateDisplayText();
        }
        if (ConfigHandler.witherHUDinSiderbar) {
            return;
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
        return ConfigHandler.show_lastWitherHUD && FKCounterMod.isInMwGame && ScoreboardEvent.getMwScoreboardParser().isOnlyOneWitherAlive();
    }

}
