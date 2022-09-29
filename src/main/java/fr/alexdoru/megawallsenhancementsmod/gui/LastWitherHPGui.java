package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.MwGameEvent;
import fr.alexdoru.fkcountermod.events.ScoreboardEvent;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LastWitherHPGui extends MyCachedGui {

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "Wither dies in 148s";
    public static LastWitherHPGui instance;
    private static String color = "";
    private static long lastWitherHPUpdate = 0;
    private static long thirdWitherDeathTime = 0;
    private static int witherHp = 0;

    public LastWitherHPGui() {
        instance = this;
        guiPosition = ConfigHandler.lastWitherHUDPosition;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void updateWitherHP(int witherHPIn) {
        if (witherHp != witherHPIn) {
            lastWitherHPUpdate = System.currentTimeMillis();
        }
        witherHp = witherHPIn;
        if (ScoreboardEvent.getMwScoreboardParser().isOnlyOneWitherAlive()) {
            color = "\u00a7" + ScoreboardEvent.getMwScoreboardParser().getAliveWithers().get(0);
        }
        updateDisplayText();
    }

    @SubscribeEvent
    public void onMWEvent(MwGameEvent event) {
        if (event.getType() == MwGameEvent.EventType.THIRD_WITHER_DEATH) {
            thirdWitherDeathTime = System.currentTimeMillis();
            color = "\u00a7" + ScoreboardEvent.getMwScoreboardParser().getAliveWithers().get(0);
        }
    }

    @Override
    public void updateDisplayText() {
        long time = System.currentTimeMillis();
        int timeToDie = (witherHp / 8) * 5 + (thirdWitherDeathTime + 55000L - time > 0 ? (int) ((thirdWitherDeathTime + 55000L - time) / 1000L) - 4 : (int) ((lastWitherHPUpdate - time) / 1000L) + 3);
        displayText = color + "Wither dies in " + Math.max(0, timeToDie) + "s";
    }

    @Override
    public void render(ScaledResolution resolution) {
        if (ConfigHandler.witherHUDinSiderbar) {
            return;
        }
        int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);
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
