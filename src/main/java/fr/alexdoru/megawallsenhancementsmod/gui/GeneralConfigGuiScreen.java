package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.fkcountermod.gui.FKConfigGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class GeneralConfigGuiScreen extends MyGuiScreen {

    private final int ButtonsHeight = 20;

    @Override
    public void initGui() {
        /*
         * Defines the button list
         */
        int buttonsWidth = 150;
        this.buttonList.add(new GuiButton(0, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, EnumChatFormatting.GREEN + "Final Kill Counter"));
        this.buttonList.add(new GuiButton(1, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2, buttonsWidth, ButtonsHeight, EnumChatFormatting.GREEN + "Mega Walls Enhancements"));
        this.buttonList.add(new GuiButton(2, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, EnumChatFormatting.GREEN + "No Cheaters"));
        this.buttonList.add(new GuiButton(3, getxCenter() - 150 / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 4, 150, ButtonsHeight, "Close"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle("Config", 2, (width / 2.0f), getyCenter() - (ButtonsHeight + 4) * 6);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new FKConfigGuiScreen(this));
                break;
            case 1:
                mc.displayGuiScreen(new MWEnConfigGuiScreen(this));
                break;
            case 2:
                mc.displayGuiScreen(new NoCheatersConfigGuiScreen(this));
                break;
            case 3:
                mc.displayGuiScreen(null);
                break;
        }
        super.actionPerformed(button);

    }

}
