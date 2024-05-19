package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.*;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.FKCounterHUD;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import static net.minecraft.util.EnumChatFormatting.*;

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
        this.elementList.add(new TextElement(AQUA + msg0, getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.elementList.add(new TextElement(GRAY + msg1, getxCenter() + fontRendererObj.getStringWidth(msg0) - fontRendererObj.getStringWidth(msg1), getButtonYPos(-1) + 2 * fontRendererObj.FONT_HEIGHT));
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(1),
                "Show Final Kill Counter",
                (b) -> ConfigHandler.showfkcounterHUD = b,
                () -> ConfigHandler.showfkcounterHUD,
                FKCounterHUD.instance,
                this,
                GRAY + "Displays the amount of final kills per team in Mega Walls",
                RED + "This will only work if you have your Hypixel language set to English")
                .accept(this.buttonList);
        this.buttonList.add(new FancyGuiButton(
                getxCenter() - 100, getButtonYPos(2),
                () -> {
                    // Normal mode
                    if (!ConfigHandler.fkcounterHUDShowPlayers && !ConfigHandler.fkcounterHUDCompact && !ConfigHandler.fkcounterHUDinSidebar) {
                        return "Display mode : " + GREEN + "Classic";
                        // Player mode
                    } else if (ConfigHandler.fkcounterHUDShowPlayers && !ConfigHandler.fkcounterHUDCompact && !ConfigHandler.fkcounterHUDinSidebar) {
                        return "Display mode : " + GREEN + "Player";
                        // Compact mode
                    } else if (!ConfigHandler.fkcounterHUDShowPlayers && ConfigHandler.fkcounterHUDCompact && !ConfigHandler.fkcounterHUDinSidebar) {
                        return "Display mode : " + GREEN + "Compact";
                        // Compact mode in sidebar
                    } else if (!ConfigHandler.fkcounterHUDShowPlayers && ConfigHandler.fkcounterHUDCompact /*&& ConfigHandler.fkcounterHUDinSidebar*/) {
                        return "Display mode : " + GREEN + "Compact in sidebar";
                    } else {
                        // force normal mode if you have weird boolean states
                        return "Display mode : " + RED + "ERROR, click the button";
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
                YELLOW + "Change the look of the HUD :",
                DARK_GRAY + "▪ " + GREEN + "Classic mode" + GRAY + " : draws the names of each teams with the amount of finals",
                DARK_GRAY + "▪ " + GREEN + "Player mode" + GRAY + " : draws the names of each teams, along with the names and amounts of finals of each player that has finals, you can change the amount of playernames it draws per team with the slider below",
                DARK_GRAY + "▪ " + GREEN + "Compact mode" + GRAY + " : only draws 4 colored numbers that represent the finals of each teams",
                DARK_GRAY + "▪ " + GREEN + "Compact mode in sidebar" + GRAY + " : subtly places the compact HUD in the sidebar"
        ));
        this.buttonList.add(new GuiSlider(6, xPos, getButtonYPos(3), 200, buttonsHeight, "Player amount : ", "", 1d, 10d, ConfigHandler.fkcounterHUDPlayerAmount, false, true, this));
        this.buttonList.add(new GuiSlider(5, xPos, getButtonYPos(4), 200, buttonsHeight, "HUD Size : ", "%", 1d, 400d, ConfigHandler.fkcounterHUDSize * 100d, false, true, this));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(5),
                "Draw background",
                (b) -> ConfigHandler.fkcounterHUDDrawBackground = b,
                () -> ConfigHandler.fkcounterHUDDrawBackground,
                GRAY + "Renders a background behind the final kill counter HUD"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(6),
                "Finals in tablist",
                (b) -> ConfigHandler.fkcounterHUDTablist = b,
                () -> ConfigHandler.fkcounterHUDTablist,
                GRAY + "Renders in the tablist next to their names the amount of final kills that each player has",
                RED + "This will not work with certain mods such as Orange Marshall's Vanilla Enhancements"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(7),
                "Show kill diff in chat",
                (b) -> ConfigHandler.showKillDiffInChat = b,
                () -> ConfigHandler.showKillDiffInChat,
                GRAY + "Adds at the end of kill messages the final kill difference for every kill"));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(9), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        switch (slider.id) {
            case 5:
                ConfigHandler.fkcounterHUDSize = slider.getValueInt() / 100d;
                break;
            case 6:
                ConfigHandler.fkcounterHUDPlayerAmount = slider.getValueInt();
                FKCounterHUD.instance.updateDisplayText();
                break;
        }
    }

}
