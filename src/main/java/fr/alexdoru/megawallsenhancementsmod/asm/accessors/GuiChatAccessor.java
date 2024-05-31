package fr.alexdoru.megawallsenhancementsmod.asm.accessors;

import net.minecraft.client.gui.GuiTextField;

public interface GuiChatAccessor {

    int getSentHistoryCursor();

    void setSentHistoryCursor(int i);

    GuiTextField getInputField();

}
