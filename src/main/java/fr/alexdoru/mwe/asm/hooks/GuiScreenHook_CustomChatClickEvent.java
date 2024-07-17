package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.GuiChatAccessor;
import fr.alexdoru.mwe.features.MegaWallsEndGameStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("unused")
public class GuiScreenHook_CustomChatClickEvent {

    public static final String COPY_TO_CLIPBOARD_COMMAND = "/copyToClipBoard ";
    public static final String MW_GAME_END_STATS = "/gamestatsmw";

    /**
     * Returns true if it handles a custom click event
     */
    public static boolean executeMWEClickEvent(String command) {
        if (command != null && command.startsWith(COPY_TO_CLIPBOARD_COMMAND)) {
            GuiScreen.setClipboardString(command.replaceFirst(COPY_TO_CLIPBOARD_COMMAND, ""));
            return true;
        }
        if (MW_GAME_END_STATS.equals(command)) {
            MegaWallsEndGameStats.printGameStatsMessage();
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
