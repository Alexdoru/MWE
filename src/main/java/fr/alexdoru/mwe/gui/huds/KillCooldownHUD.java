package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.api.events.ChatMessageSentEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.api.events.WitherHealthDecayEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KillCooldownHUD extends AbstractRenderer {

    private final TimerUtil timerKillCooldown = new TimerUtil(60000L);
    private MWTeam ownTeam;
    private boolean resetKillCooldown;
    private long lastkilltime = 0;

    public KillCooldownHUD() {
        super(MWEConfig.killCooldownHUDPosition);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onSentMessage(ChatMessageSentEvent event) {
        if (this.getPosition().isEnabled() && ScoreboardTracker.isInMwGame()) {
            final String message = event.message.toLowerCase();
            if (message.equals("/kill") || message.startsWith("/kill ")) {
                if (timerKillCooldown.update()) {
                    lastkilltime = System.currentTimeMillis();
                }
            }
        }
    }

    @SubscribeEvent
    public void onGameEvent(MegaWallsGameEvent event) {
        if (event.type == Type.GAME_START) {
            resetKillCooldown = false;
        }
        if (event.type == Type.CONNECT) {
            ownTeam = MWTeam.fromColorChar(ScoreboardTracker.getParser().getOwnMWTeamColor());
        }
    }

    @SubscribeEvent
    public void onWitherHealthDecay(WitherHealthDecayEvent event) {
        if (!resetKillCooldown && event.health < 100 && event.team == ownTeam) {
            resetKillCooldown = true;
            this.hideHUD();
        }
    }

    public void hideHUD() {
        lastkilltime = 0;
    }

    @Override
    public void render(ScaledResolution resolution) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        final int timeleft = 60 - ((int) (System.currentTimeMillis() - lastkilltime)) / 1000;
        final String displayText = "/kill cooldown : " + timeleft + "s";
        this.rendererPosition.updateAdjustedAbsolutePosition(resolution, fr.getStringWidth(displayText), fr.FONT_HEIGHT);
        fr.drawStringWithShadow(displayText, this.rendererPosition.getAbsoluteRenderX(), this.rendererPosition.getAbsoluteRenderY(), MWEConfig.killCooldownHUDColor);
    }

    @Override
    public void renderDummy() {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("/kill cooldown : 60s", this.rendererPosition.getAbsoluteRenderX(), this.rendererPosition.getAbsoluteRenderY(), MWEConfig.killCooldownHUDColor);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return currentTimeMillis - lastkilltime < 60000L && ScoreboardTracker.isInMwGame();
    }

}
