package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import net.minecraft.util.EnumChatFormatting;

public class GeneralConfigGuiScreen extends MyGuiScreen {

    @Override
    public void initGui() {
        final int buttonsWidth = 150;
        this.maxWidth = buttonsWidth;
        this.maxHeight = (buttonsHeight + 4) * 9 + buttonsHeight;
        super.initGui();
        final int xPos = this.getxCenter() - buttonsWidth / 2;
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(1), buttonsWidth, buttonsHeight, EnumChatFormatting.AQUA + "Final Kill Counter", () -> mc.displayGuiScreen(new FKConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(2), buttonsWidth, buttonsHeight, EnumChatFormatting.GREEN + "Mega Walls Enhancements", () -> mc.displayGuiScreen(new MWEnConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(3), buttonsWidth, buttonsHeight, EnumChatFormatting.DARK_PURPLE + "HUDs", () -> mc.displayGuiScreen(new HUDsConfigGuiScreen(this))));
        // TODO add button for auto-updates
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(4), buttonsWidth, buttonsHeight, EnumChatFormatting.RED + "NoCheaters", () -> mc.displayGuiScreen(new NoCheatersConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(5), buttonsWidth, buttonsHeight, EnumChatFormatting.DARK_RED + "Hacker Detector", () -> mc.displayGuiScreen(new HackerDetectorConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(6), buttonsWidth, buttonsHeight, EnumChatFormatting.BLUE + "Hitboxes, better F3+b", () -> mc.displayGuiScreen(new HitboxConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(8), buttonsWidth, buttonsHeight, "Close", () -> mc.displayGuiScreen(null)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle(EnumChatFormatting.GREEN + "Config", 2, getxCenter(), getButtonYPos(-1));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
