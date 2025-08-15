package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.ChatComponentTextAccessor;
import net.minecraft.util.IChatComponent;

public class ChatComponentStyleHook_ChatHeads {

    public static void transferHeadToComponent(IChatComponent thisComponent, IChatComponent otherComponent) {
        if (thisComponent instanceof ChatComponentTextAccessor && otherComponent instanceof ChatComponentTextAccessor) {
            if (((ChatComponentTextAccessor) otherComponent).getSkinChatHead() != null) {
                ((ChatComponentTextAccessor) thisComponent).setSkinChatHead(((ChatComponentTextAccessor) otherComponent).getSkinChatHead());
                ((ChatComponentTextAccessor) otherComponent).setSkinChatHead(null);
            }
        }
    }

}
