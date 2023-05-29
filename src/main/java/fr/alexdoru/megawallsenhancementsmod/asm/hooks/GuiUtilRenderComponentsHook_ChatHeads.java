package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.ChatComponentTextAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

@SuppressWarnings("unused")
public class GuiUtilRenderComponentsHook_ChatHeads {

    private static boolean offsetLine = false;

    public static ChatComponentText getStarterChatComponent(ChatComponentText newMsg, IChatComponent fullMessage) {
        if (fullMessage instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) fullMessage).getSkin() != null) {
            offsetLine = ConfigHandler.chatHeads;
            ((ChatComponentTextAccessor) newMsg).setSkin(((ChatComponentTextAccessor) fullMessage).getSkin());
            return newMsg;
        }
        offsetLine = false;
        return newMsg;
    }

    public static int modifyLineWidth(int original) {
        if (offsetLine && original > 9) {
            return original - 9;
        }
        return original;
    }

    public static void removeOffset() {
        offsetLine = false;
    }

}
