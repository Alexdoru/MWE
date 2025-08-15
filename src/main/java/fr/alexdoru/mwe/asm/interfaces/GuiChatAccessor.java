package fr.alexdoru.mwe.asm.interfaces;

import net.minecraft.client.gui.GuiTextField;

public interface GuiChatAccessor {

    int getSentHistoryCursor();

    void setSentHistoryCursor(int i);

    GuiTextField getInputField();
}
