package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DebugScoreboardHUD {

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            final Minecraft mc = Minecraft.getMinecraft();
            final int x = 20;
            int y = 20;
            mc.fontRendererObj.drawStringWithShadow("isMWEnvironement " + formatBool(ScoreboardTracker.isMWEnvironement()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isPreGameLobby " + formatBool(ScoreboardTracker.isPreGameLobby()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isInMwGame " + formatBool(ScoreboardTracker.isInMwGame()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("Game ID " + ScoreboardTracker.getParser().getGameId(), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isPrepPhase " + formatBool(ScoreboardTracker.isPrepPhase()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("withersAlive " + ScoreboardTracker.getParser().getAliveWithers().size(), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isReplayMode " + formatBool(ScoreboardTracker.isReplayMode()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isMWReplay " + formatBool(ScoreboardTracker.isMWReplay()), x, y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("isInSkyblock " + formatBool(ScoreboardTracker.isInSkyblock()), x, y, 0xFFFFFF);
        }
    }

    private static String formatBool(boolean b) {
        return b ? EnumChatFormatting.GREEN + "True" : EnumChatFormatting.RED + "False";
    }

}
