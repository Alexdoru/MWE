package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.MwGameEvent;
import fr.alexdoru.fkcountermod.events.ScoreboardEvent;
import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LastWitherHPGui extends MyCachedGui {

    public static LastWitherHPGui instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.BLUE + "Wither dies in 148s";
    private static long lastupdate = 0;
    private static long thirdWitherDeathTime = 0;
    private static String color;

    public LastWitherHPGui() {
        instance = this;
        guiPosition = MWEnConfigHandler.lastWitherHUDPosition;
    }

    @Override
    public void updateDisplayText() {
        int timeToDie = ScoreboardEvent.getMwScoreboardParser().getWitherHP()*5/8 + (int)Math.max(0, thirdWitherDeathTime + 60000L - System.currentTimeMillis());
        displayText = color + "Wither dies in " + timeToDie + "s";
    }

    @SubscribeEvent
    public void onMWEvent(MwGameEvent event) {
        if(event.getType() == MwGameEvent.EventType.THIRD_WITHER_DEATH) {
            thirdWitherDeathTime = System.currentTimeMillis();
            color = ScoreboardEvent.getMwScoreboardParser().getAliveWithers().get(0);
        }
    }

    @Override
    public void render() {
        super.render();
        final long time = System.currentTimeMillis();
        if (time - lastupdate >= 1000L) {
            updateDisplayText();
            lastupdate = time;
        }
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];
        frObj.drawStringWithShadow(displayText, x, y, 0);
    }

    @Override
    public void renderDummy() {
        super.renderDummy();
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];
        drawString(frObj, DUMMY_TEXT, x, y, 0);
    }

    @Override
    public boolean isEnabled() {
        return MWEnConfigHandler.show_lastWitherHUD && FKCounterMod.isInMwGame() && ScoreboardEvent.getMwScoreboardParser().isOnlyOneWitherAlive();
    }

    @Override
    public void save() {
        MWEnConfigHandler.saveConfig();
    }

}
