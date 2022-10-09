package fr.alexdoru.megawallsenhancementsmod.asm.accessor;

import net.minecraft.client.gui.ChatLine;

import java.util.List;

public interface GuiNewChatAccessor {
    void deleteWarningMessagesFor(String playername);
    void deleteAllWarningMessages();
    List<ChatLine> getChatLines();
    List<ChatLine> getDrawnChatLines();
}
