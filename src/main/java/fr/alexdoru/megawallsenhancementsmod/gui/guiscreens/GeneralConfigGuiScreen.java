package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class GeneralConfigGuiScreen extends MyGuiScreen {

    @Override
    public void initGui() {
        final int buttonsWidth = 150;
        this.maxWidth = buttonsWidth + (buttonsWidth + 10) * 2;
        this.maxHeight = (buttonsHeight + 4) * 9 + buttonsHeight;
        super.initGui();
        /*
         * Defines the button list
         */
        final int xPos = getxCenter() - buttonsWidth / 2;
        this.buttonList.add(new GuiButton(0, xPos, getButtonYPos(1), buttonsWidth, buttonsHeight, EnumChatFormatting.AQUA + "Final Kill Counter"));
        this.buttonList.add(new GuiButton(1, xPos, getButtonYPos(2), buttonsWidth, buttonsHeight, EnumChatFormatting.GREEN + "Mega Walls Enhancements"));
        this.buttonList.add(new GuiButton(5, xPos, getButtonYPos(3), buttonsWidth, buttonsHeight, EnumChatFormatting.DARK_PURPLE + "HUDs"));
        this.buttonList.add(new GuiButton(2, xPos, getButtonYPos(4), buttonsWidth, buttonsHeight, EnumChatFormatting.RED + "No Cheaters"));
        this.buttonList.add(new GuiButton(7, getxCenter() + buttonsWidth / 2 + 10, getButtonYPos(2), buttonsWidth, buttonsHeight, EnumChatFormatting.WHITE + "Automatic Updates : " + getSuffix(ConfigHandler.automaticUpdate)));
        this.buttonList.add(new GuiButton(6, xPos, getButtonYPos(5), buttonsWidth, buttonsHeight, EnumChatFormatting.DARK_RED + "Hacker Detector"));
        this.buttonList.add(new GuiButton(4, xPos, getButtonYPos(6), buttonsWidth, buttonsHeight, EnumChatFormatting.BLUE + "Hitboxes, better F3+b"));
        this.buttonList.add(new GuiButton(3, xPos, getButtonYPos(8), buttonsWidth, buttonsHeight, "Close"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle(EnumChatFormatting.GREEN + "Config", 2, getxCenter(), getButtonYPos(-1));
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
            case 4:
                mc.displayGuiScreen(new HitboxConfigGuiScreen(this));
                break;
            case 5:
                mc.displayGuiScreen(new HUDsConfigGuiScreen(this));
                break;
            case 6:
                mc.displayGuiScreen(new HackerDetectorConfigGuiScreen(this));
                break;
            case 7:
                ConfigHandler.automaticUpdate = !ConfigHandler.automaticUpdate;
                button.displayString = EnumChatFormatting.WHITE + "Automatic Updates : " + getSuffix(ConfigHandler.automaticUpdate);
                break;
        }
        super.actionPerformed(button);
    }

}
