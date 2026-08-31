package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DateUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LastWitherHPHUD extends AbstractRenderer {

    private String displayText = "";
    private long lastWitherHPUpdate = 0;
    private long thirdWitherDeathTime = 0;
    private int witherHp = 0;

    public LastWitherHPHUD() {
        super(MWEConfig.lastWitherHUDPosition);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && ScoreboardTracker.getParser().isOnlyOneWitherAlive()) {
            this.updateDisplayText(ScoreboardTracker.getParser().getLastWitherHealth());
        }
    }

    private void updateDisplayText(int health) {
        if (witherHp != health) {
            lastWitherHPUpdate = System.currentTimeMillis();
        }
        witherHp = health;
        final String color = ScoreboardTracker.getParser().getAliveWithers().get(0).getColorPrefix();
        final long time = System.currentTimeMillis();
        final int timeToDie = (witherHp / 8) * 5 + (thirdWitherDeathTime + 55000L - time > 0 ? (int) ((thirdWitherDeathTime + 55000L - time) / 1000L) - 4 : (int) ((lastWitherHPUpdate - time) / 1000L) + 3);
        displayText = color + "Wither dies in " + DateUtil.formatTime(Math.max(0, timeToDie));
    }

    @SubscribeEvent
    public void onMWEvent(MegaWallsGameEvent event) {
        if (event.type == Type.THIRD_WITHER_DEATH) {
            thirdWitherDeathTime = System.currentTimeMillis();
        }
    }

    @Override
    public void render(ScaledResolution resolution) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        this.rendererPosition.updateAdjustedAbsolutePosition(resolution, fr.getStringWidth(displayText), fr.FONT_HEIGHT);
        fr.drawStringWithShadow(displayText, this.rendererPosition.getAbsoluteRenderX(), this.rendererPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public void renderDummy() {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(EnumChatFormatting.GREEN + "Wither dies in 148s", this.rendererPosition.getAbsoluteRenderX(), this.rendererPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.rendererPosition.isEnabled() && !MWEConfig.witherHUDinSidebar && ScoreboardTracker.isInMwGame() && ScoreboardTracker.getParser().isOnlyOneWitherAlive();
    }

    public String getDisplayText() {
        return displayText;
    }

}
