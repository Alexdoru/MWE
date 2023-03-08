package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LastWitherHPHUD extends MyCachedHUD {

    public static LastWitherHPHUD instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "Wither dies in 148s";
    private String color = "";
    private long lastWitherHPUpdate = 0;
    private long thirdWitherDeathTime = 0;
    private int witherHp = 0;

    public LastWitherHPHUD() {
        super(ConfigHandler.lastWitherHUDPosition);
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void updateWitherHP(int witherHPIn) {
        if (witherHp != witherHPIn) {
            lastWitherHPUpdate = System.currentTimeMillis();
        }
        witherHp = witherHPIn;
        if (ScoreboardTracker.getMwScoreboardParser().isOnlyOneWitherAlive()) {
            color = "\u00a7" + ScoreboardTracker.getMwScoreboardParser().getAliveWithers().get(0);
        }
        updateDisplayText();
    }

    @SubscribeEvent
    public void onMWEvent(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.THIRD_WITHER_DEATH) {
            thirdWitherDeathTime = System.currentTimeMillis();
            color = "\u00a7" + ScoreboardTracker.getMwScoreboardParser().getAliveWithers().get(0);
        }
    }

    @Override
    public void updateDisplayText() {
        final long time = System.currentTimeMillis();
        final int timeToDie = (witherHp / 8) * 5 + (thirdWitherDeathTime + 55000L - time > 0 ? (int) ((thirdWitherDeathTime + 55000L - time) / 1000L) - 4 : (int) ((lastWitherHPUpdate - time) / 1000L) + 3);
        displayText = color + "Wither dies in " + Math.max(0, timeToDie) + "s";
    }

    @Override
    public void render(ScaledResolution resolution) {
        final int[] absolutePos = this.guiPosition.getAdjustedAbsolutePosition(resolution, frObj.getStringWidth(displayText), frObj.FONT_HEIGHT);
        frObj.drawStringWithShadow(displayText, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public void renderDummy() {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition();
        frObj.drawStringWithShadow(DUMMY_TEXT, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return !ConfigHandler.witherHUDinSidebar && ConfigHandler.showLastWitherHUD && FKCounterMod.isInMwGame && ScoreboardTracker.getMwScoreboardParser().isOnlyOneWitherAlive();
    }

}
