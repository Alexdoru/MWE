package fr.alexdoru.mwe.asm.hooks.mc.gui;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.asm.interfaces.GuiChatAccessor;
import fr.alexdoru.mwe.features.MegaWallsEndGameStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

public class GuiScreenHook_CustomChatClickEvent {

    /**
     * Returns true if it handles a custom click event
     */
    public static boolean executeMWEClickEvent(String command) {
        if (MegaWallsEndGameStats.MW_GAME_END_STATS_COMMAND.equals(command)) {
            MWE.INSTANCE().getMwGameStats().printGameStatsMessage();
            return true;
        }
        final Minecraft mc = Minecraft.getMinecraft();
        if (command != null && mc.currentScreen instanceof GuiChat) {
            final String lowerCase = command.toLowerCase();
            if (lowerCase.startsWith("/report ") || lowerCase.startsWith("/wdr ")) {
                boolean flag = false;
                if (mc.currentScreen instanceof GuiChatAccessor) {
                    flag = ((GuiChatAccessor) mc.currentScreen).getSentHistoryCursor() == mc.ingameGUI.getChatGUI().getSentMessages().size();
                }
                mc.ingameGUI.getChatGUI().addToSentMessages(command);
                if (flag) {
                    ((GuiChatAccessor) mc.currentScreen).setSentHistoryCursor(mc.ingameGUI.getChatGUI().getSentMessages().size());
                }
            }
        }
        return false;
    }

}
