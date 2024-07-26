package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.List;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook_HidePing {

    private static boolean drawPing = true;

    // called once per frame
    public static int getPingWidth(int original, List<NetworkPlayerInfo> list) {
        if (MWEConfig.hidePingTablist) {
            if (ScoreboardTracker.isInMwGame()) {
                drawPing = false;
                return 0;
            }
            boolean b = false;
            for (final NetworkPlayerInfo netInfo : list) {
                if (netInfo.getResponseTime() > 1) {
                    b = true;
                    break;
                }
            }
            drawPing = b;
            return drawPing ? original : 0;
        }
        drawPing = true;
        return original;
    }

    //called n times per frame
    public static boolean shouldDrawPing() {
        return drawPing;
    }

}
