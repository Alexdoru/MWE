package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;

public class GuiChatHook_CopyMessages {

    private static boolean copyChatLine = false;

    @SuppressWarnings("unused")
    public static void onChatRightClick(int mouseButton) {
        if (ConfigHandler.rightClickChatCopy && mouseButton == 1) {
            copyChatLine = true;
            Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
            copyChatLine = false;
        }
    }

    @SuppressWarnings("unused")
    public static boolean onChatLeftClick(boolean ranClickEvent, IChatComponent component) {
        if (ranClickEvent) {
            return true;
        } else {
            if (ConfigHandler.leftClickChatCopy && component != null) {
                copyChatLine = true;
                Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
                copyChatLine = false;
            }
        }
        return false;
    }

    public static boolean copyChatLine() {
        return copyChatLine;
    }

}
