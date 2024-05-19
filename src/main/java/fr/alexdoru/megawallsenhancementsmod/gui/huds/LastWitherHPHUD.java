package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LastWitherHPHUD extends AbstractRenderer {

    public static LastWitherHPHUD instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "Wither dies in 148s";
    public String displayText = "";
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
        if (ScoreboardTracker.getParser().isOnlyOneWitherAlive()) {
            color = "§" + ScoreboardTracker.getParser().getAliveWithers().get(0);
        }
        final long time = System.currentTimeMillis();
        final int timeToDie = (witherHp / 8) * 5 + (thirdWitherDeathTime + 55000L - time > 0 ? (int) ((thirdWitherDeathTime + 55000L - time) / 1000L) - 4 : (int) ((lastWitherHPUpdate - time) / 1000L) + 3);
        displayText = color + "Wither dies in " + Math.max(0, timeToDie) + "s";
    }

    @SubscribeEvent
    public void onMWEvent(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.THIRD_WITHER_DEATH) {
            thirdWitherDeathTime = System.currentTimeMillis();
            color = "§" + ScoreboardTracker.getParser().getAliveWithers().get(0);
        }
    }

    @Override
    public void render(ScaledResolution resolution) {
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, mc.fontRendererObj.getStringWidth(displayText), mc.fontRendererObj.FONT_HEIGHT);
        mc.fontRendererObj.drawStringWithShadow(displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public void renderDummy() {
        mc.fontRendererObj.drawStringWithShadow(DUMMY_TEXT, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return !ConfigHandler.witherHUDinSidebar && ConfigHandler.showLastWitherHUD && ScoreboardTracker.isInMwGame && ScoreboardTracker.getParser().isOnlyOneWitherAlive();
    }

}
