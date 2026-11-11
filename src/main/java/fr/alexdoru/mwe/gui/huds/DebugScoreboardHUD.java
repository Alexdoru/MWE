package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.IScoreboardParser;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DebugScoreboardHUD {

    private final IScoreboardParser parser = ScoreboardTracker.getParser();

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            final Minecraft mc = Minecraft.getMinecraft();
            final int x = 20;
            int y = 20;
            mc.fontRendererObj.drawStringWithShadow("isMWEnvironement " + formatBool(this.parser.isMWEnvironement()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isPreGameLobby " + formatBool(this.parser.isPreGameLobby()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isInMwGame " + formatBool(this.parser.isInMwGame()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("Game ID " + this.parser.getServerID(), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isPrepPhase " + formatBool(this.parser.isPrepPhase()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("withersAlive " + this.parser.getAliveWithers(), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isReplayMode " + formatBool(this.parser.isReplayMode()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isAtlasMode " + formatBool(this.parser.isAtlasMode()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isMWReplay " + formatBool(this.parser.isMWReplay()), x, y, 0xFFFFFF);
        }
    }

    private static String formatBool(boolean b) {
        return b ? EnumChatFormatting.GREEN + "True" : EnumChatFormatting.RED + "False";
    }

}
