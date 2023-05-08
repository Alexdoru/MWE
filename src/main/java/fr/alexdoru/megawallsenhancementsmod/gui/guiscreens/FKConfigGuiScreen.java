package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.*;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.FKCounterHUD;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

public class FKConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    public FKConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.maxWidth = 90 * 2 + BUTTON_WIDTH + 4 * 2;
        this.maxHeight = (buttonsHeight + 4) * 10 + buttonsHeight;
        super.initGui();
        final int xPos = getxCenter() - 200 / 2;
        final String msg0 = "Final Kill Counter";
        final String msg1 = "for Mega Walls";
        this.elementList.add(new TextElement(EnumChatFormatting.AQUA + msg0, getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.elementList.add(new TextElement(EnumChatFormatting.GRAY + msg1, getxCenter() + fontRendererObj.getStringWidth(msg0) - fontRendererObj.getStringWidth(msg1), getButtonYPos(-1) + 2 * fontRendererObj.FONT_HEIGHT));
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(1),
                "Show Final Kill Counter",
                (b) -> ConfigHandler.showfkcounterHUD = b,
                () -> ConfigHandler.showfkcounterHUD,
                ConfigHandler.fkcounterHUDPosition,
                FKCounterHUD.instance,
                this,
                EnumChatFormatting.GRAY + "Displays the amount of final kills per team in Mega Walls",
                EnumChatFormatting.RED + "This will only work if you have your Hypixel language set to English")
                .accept(this.buttonList);
        this.buttonList.add(new FancyGuiButton(
                getxCenter() - 100, getButtonYPos(2),
                () -> {
                    // Normal mode
                    if (!ConfigHandler.fkcounterHUDShowPlayers && !ConfigHandler.fkcounterHUDCompact && !ConfigHandler.fkcounterHUDinSidebar) {
                        return "Display mode : " + EnumChatFormatting.GREEN + "Classic";
                        // Player mode
                    } else if (ConfigHandler.fkcounterHUDShowPlayers && !ConfigHandler.fkcounterHUDCompact && !ConfigHandler.fkcounterHUDinSidebar) {
                        return "Display mode : " + EnumChatFormatting.GREEN + "Player";
                        // Compact mode
                    } else if (!ConfigHandler.fkcounterHUDShowPlayers && ConfigHandler.fkcounterHUDCompact && !ConfigHandler.fkcounterHUDinSidebar) {
                        return "Display mode : " + EnumChatFormatting.GREEN + "Compact";
                        // Compact mode in sidebar
                    } else if (!ConfigHandler.fkcounterHUDShowPlayers && ConfigHandler.fkcounterHUDCompact /*&& ConfigHandler.fkcounterHUDinSidebar*/) {
                        return "Display mode : " + EnumChatFormatting.GREEN + "Compact in sidebar";
                    } else {
                        // force normal mode if you have weird boolean states
                        return "Display mode : " + EnumChatFormatting.RED + "ERROR, click the button";
                    }
                },
                () -> {

                    // Normal mode
                    if (!ConfigHandler.fkcounterHUDShowPlayers && !ConfigHandler.fkcounterHUDCompact && !ConfigHandler.fkcounterHUDinSidebar) {
                        ConfigHandler.fkcounterHUDShowPlayers = true;
                        // Player mode
                    } else if (ConfigHandler.fkcounterHUDShowPlayers && !ConfigHandler.fkcounterHUDCompact && !ConfigHandler.fkcounterHUDinSidebar) {
                        ConfigHandler.fkcounterHUDShowPlayers = false;
                        ConfigHandler.fkcounterHUDCompact = true;
                        // Compact mode
                    } else if (!ConfigHandler.fkcounterHUDShowPlayers && ConfigHandler.fkcounterHUDCompact && !ConfigHandler.fkcounterHUDinSidebar) {
                        ConfigHandler.fkcounterHUDinSidebar = true;
                        // Compact mode in sidebar
                    } else if (!ConfigHandler.fkcounterHUDShowPlayers && ConfigHandler.fkcounterHUDCompact /*&& ConfigHandler.fkcounterHUDinSidebar*/) {
                        ConfigHandler.fkcounterHUDCompact = false;
                        ConfigHandler.fkcounterHUDinSidebar = false;
                    } else {
                        // force normal mode if you have weird boolean states
                        ConfigHandler.fkcounterHUDShowPlayers = false;
                        ConfigHandler.fkcounterHUDCompact = false;
                        ConfigHandler.fkcounterHUDinSidebar = false;
                    }
                    FKCounterHUD.instance.updateDisplayText();

                },
                EnumChatFormatting.YELLOW + "Change the look of the HUD :",
                EnumChatFormatting.DARK_GRAY + "\u25AA " + EnumChatFormatting.GREEN + "Classic mode" + EnumChatFormatting.GRAY + " : draws the names of each teams with the amount of finals",
                EnumChatFormatting.DARK_GRAY + "\u25AA " + EnumChatFormatting.GREEN + "Player mode" + EnumChatFormatting.GRAY + " : draws the names of each teams, along with the names and amounts of finals of each player that has finals, you can change the amount of playernames it draws per team with the slider below",
                EnumChatFormatting.DARK_GRAY + "\u25AA " + EnumChatFormatting.GREEN + "Compact mode" + EnumChatFormatting.GRAY + " : only draws 4 colored numbers that represent the finals of each teams",
                EnumChatFormatting.DARK_GRAY + "\u25AA " + EnumChatFormatting.GREEN + "Compact mode in sidebar" + EnumChatFormatting.GRAY + " : subtly places the compact HUD in the sidebar"
        ));
        this.buttonList.add(new GuiSlider(6, xPos, getButtonYPos(3), 200, buttonsHeight, "Player amount : ", "", 1d, 10d, ConfigHandler.fkcounterHUDPlayerAmount, false, true, this));
        this.buttonList.add(new GuiSlider(5, xPos, getButtonYPos(4), 200, buttonsHeight, "HUD Size : ", "", 0.1d, 4d, ConfigHandler.fkcounterHUDSize, true, true, this));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(5),
                "Draw background",
                (b) -> ConfigHandler.fkcounterHUDDrawBackground = b,
                () -> ConfigHandler.fkcounterHUDDrawBackground,
                EnumChatFormatting.GRAY + "Renders a background behind the final kill counter HUD"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(6),
                "Finals in tablist",
                (b) -> ConfigHandler.fkcounterHUDTablist = b,
                () -> ConfigHandler.fkcounterHUDTablist,
                EnumChatFormatting.GRAY + "Renders in the tablist next to their names the amount of final kills that each player has",
                EnumChatFormatting.RED + "This will not work with certain mods such as Orange Marshall's Vanilla Enhancements"));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(8), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        switch (slider.id) {
            case 5:
                final double newvalue = Math.floor(slider.getValue() * 20d) / 20d;
                ConfigHandler.fkcounterHUDSize = newvalue;
                slider.setValue(newvalue);
                break;
            case 6:
                ConfigHandler.fkcounterHUDPlayerAmount = (int) slider.getValue();
                FKCounterHUD.instance.updateDisplayText();
                break;
        }
    }

}
