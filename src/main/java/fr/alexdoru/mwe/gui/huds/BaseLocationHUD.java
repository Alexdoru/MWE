package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.enums.MWMap;
import fr.alexdoru.mwe.api.events.MapEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.chat.LocrawListener;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BaseLocationHUD extends AbstractRenderer {

    private MWMap currentMap;

    public BaseLocationHUD() {
        super(MWEConfig.baseLocationHUDPosition);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void render(ScaledResolution resolution) {
        final Minecraft mc = Minecraft.getMinecraft();
        final String text = currentMap.getPlayerBaseLocation(mc.thePlayer);
        this.rendererPosition.updateAdjustedAbsolutePosition(resolution, mc.fontRendererObj.getStringWidth(text), mc.fontRendererObj.FONT_HEIGHT);
        drawCenteredString(mc.fontRendererObj, text, this.rendererPosition.getAbsoluteRenderX(), this.rendererPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public void renderDummy() {
        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, EnumChatFormatting.GREEN + "GREEN", this.rendererPosition.getAbsoluteRenderX(), this.rendererPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.rendererPosition.isEnabled() && (ScoreboardTracker.isInMwGame() || ScoreboardTracker.isMWReplay()) && this.currentMap != null;
    }

    @SubscribeEvent
    public void onMWEvent(MegaWallsGameEvent event) {
        if (!this.rendererPosition.isEnabled()) return;
        if (event.type == MegaWallsGameEvent.Type.CONNECT) {
            LocrawListener.setMegaWallsMap();
        }
    }

    @SubscribeEvent
    public void onMapEvent(MapEvent event) {
        currentMap = MWMap.fromName(event.mapName);
    }

}
