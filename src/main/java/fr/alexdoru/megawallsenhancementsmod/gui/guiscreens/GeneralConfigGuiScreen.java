package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.OptionGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import net.minecraft.util.EnumChatFormatting;

public class GeneralConfigGuiScreen extends MyGuiScreen {

    @Override
    public void initGui() {
        final int buttonsWidth = 150;
        this.maxWidth = buttonsWidth + (buttonsWidth + 10) * 2;
        this.maxHeight = (buttonsHeight + 4) * 9 + buttonsHeight;
        super.initGui();
        final int xPos = this.getxCenter() - buttonsWidth / 2;
        this.elementList.add(new TextElement(EnumChatFormatting.GREEN + "Config", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(1), buttonsWidth, buttonsHeight, EnumChatFormatting.AQUA + "Final Kill Counter", () -> mc.displayGuiScreen(new FKConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(2), buttonsWidth, buttonsHeight, EnumChatFormatting.GREEN + "Mega Walls Enhancements", () -> mc.displayGuiScreen(new MegaWallsEnhancementsConfigGuiScreen(this))));
        this.buttonList.add(new OptionGuiButton(
                getxCenter() + buttonsWidth / 2 + 10, getButtonYPos(2),
                buttonsWidth, buttonsHeight,
                "Automatic Updates",
                (b) -> ConfigHandler.automaticUpdate = b,
                () -> ConfigHandler.automaticUpdate,
                EnumChatFormatting.GRAY + "When your game starts, it will check and notify you if an update is available. If this setting is turned " +
                        EnumChatFormatting.GREEN + "ON" + EnumChatFormatting.GRAY + " it will also download the updated mod and install it in your mods folder when closing your game."));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(3), buttonsWidth, buttonsHeight, EnumChatFormatting.DARK_PURPLE + "HUDs", () -> mc.displayGuiScreen(new HUDsConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(4), buttonsWidth, buttonsHeight, EnumChatFormatting.RED + "NoCheaters", () -> mc.displayGuiScreen(new NoCheatersConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(5), buttonsWidth, buttonsHeight, EnumChatFormatting.DARK_RED + "Hacker Detector", () -> mc.displayGuiScreen(new HackerDetectorConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(6), buttonsWidth, buttonsHeight, EnumChatFormatting.BLUE + "Hitboxes, better F3+b", () -> mc.displayGuiScreen(new HitboxConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(8), buttonsWidth, buttonsHeight, "Done", () -> mc.displayGuiScreen(null)));
    }

}
