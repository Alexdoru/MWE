package fr.alexdoru.mwe.gui.guiscreens;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.gui.elements.*;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
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
                (b) -> MWEConfig.showfkcounterHUD = b,
                () -> MWEConfig.showfkcounterHUD,
                GuiManager.fkCounterHUD,
                this,
                GRAY + "Displays the amount of final kills per team in Mega Walls",
                RED + "This will only work if you have your Hypixel language set to English")
                .accept(this.buttonList);
        this.buttonList.add(new FancyGuiButton(
                getxCenter() - 100, getButtonYPos(2),
                () -> {
                    // Normal mode
                    if (!MWEConfig.fkcounterHUDShowPlayers && !MWEConfig.fkcounterHUDCompact && !MWEConfig.fkcounterHUDinSidebar) {
                        return "Display mode : " + GREEN + "Classic";
                        // Player mode
                    } else if (MWEConfig.fkcounterHUDShowPlayers && !MWEConfig.fkcounterHUDCompact && !MWEConfig.fkcounterHUDinSidebar) {
                        return "Display mode : " + GREEN + "Player";
                        // Compact mode
                    } else if (!MWEConfig.fkcounterHUDShowPlayers && MWEConfig.fkcounterHUDCompact && !MWEConfig.fkcounterHUDinSidebar) {
                        return "Display mode : " + GREEN + "Compact";
                        // Compact mode in sidebar
                    } else if (!MWEConfig.fkcounterHUDShowPlayers && MWEConfig.fkcounterHUDCompact /*&& MWEConfig.fkcounterHUDinSidebar*/) {
                        return "Display mode : " + GREEN + "Compact in sidebar";
                    } else {
                        // force normal mode if you have weird boolean states
                        return "Display mode : " + RED + "ERROR, click the button";
                    }
                },
                () -> {

                    // Normal mode
                    if (!MWEConfig.fkcounterHUDShowPlayers && !MWEConfig.fkcounterHUDCompact && !MWEConfig.fkcounterHUDinSidebar) {
                        MWEConfig.fkcounterHUDShowPlayers = true;
                        // Player mode
                    } else if (MWEConfig.fkcounterHUDShowPlayers && !MWEConfig.fkcounterHUDCompact && !MWEConfig.fkcounterHUDinSidebar) {
                        MWEConfig.fkcounterHUDShowPlayers = false;
                        MWEConfig.fkcounterHUDCompact = true;
                        // Compact mode
                    } else if (!MWEConfig.fkcounterHUDShowPlayers && MWEConfig.fkcounterHUDCompact && !MWEConfig.fkcounterHUDinSidebar) {
                        MWEConfig.fkcounterHUDinSidebar = true;
                        // Compact mode in sidebar
                    } else if (!MWEConfig.fkcounterHUDShowPlayers && MWEConfig.fkcounterHUDCompact /*&& MWEConfig.fkcounterHUDinSidebar*/) {
                        MWEConfig.fkcounterHUDCompact = false;
                        MWEConfig.fkcounterHUDinSidebar = false;
                    } else {
                        // force normal mode if you have weird boolean states
                        MWEConfig.fkcounterHUDShowPlayers = false;
                        MWEConfig.fkcounterHUDCompact = false;
                        MWEConfig.fkcounterHUDinSidebar = false;
                    }
                    GuiManager.fkCounterHUD.updateDisplayText();

                },
                YELLOW + "Change the look of the HUD :",
                DARK_GRAY + "▪ " + GREEN + "Classic mode" + GRAY + " : draws the names of each teams with the amount of finals",
                DARK_GRAY + "▪ " + GREEN + "Player mode" + GRAY + " : draws the names of each teams, along with the names and amounts of finals of each player that has finals, you can change the amount of playernames it draws per team with the slider below",
                DARK_GRAY + "▪ " + GREEN + "Compact mode" + GRAY + " : only draws 4 colored numbers that represent the finals of each teams",
                DARK_GRAY + "▪ " + GREEN + "Compact mode in sidebar" + GRAY + " : subtly places the compact HUD in the sidebar"
        ));
        this.buttonList.add(new GuiSlider(6, xPos, getButtonYPos(3), 200, buttonsHeight, "Player amount : ", "", 1d, 10d, MWEConfig.fkcounterHUDPlayerAmount, false, true, this));
        this.buttonList.add(new GuiSlider(5, xPos, getButtonYPos(4), 200, buttonsHeight, "HUD Size : ", "%", 1d, 400d, MWEConfig.fkcounterHUDSize * 100d, false, true, this));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(5),
                "Draw background",
                (b) -> MWEConfig.fkcounterHUDDrawBackground = b,
                () -> MWEConfig.fkcounterHUDDrawBackground,
                GRAY + "Renders a background behind the final kill counter HUD"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(6),
                "Finals in tablist",
                (b) -> MWEConfig.fkcounterHUDTablist = b,
                () -> MWEConfig.fkcounterHUDTablist,
                GRAY + "Renders in the tablist next to their names the amount of final kills that each player has",
                RED + "This will not work with certain mods such as Orange Marshall's Vanilla Enhancements"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(7),
                "Show kill diff in chat",
                (b) -> MWEConfig.showKillDiffInChat = b,
                () -> MWEConfig.showKillDiffInChat,
                GRAY + "Adds at the end of kill messages the final kill difference for every kill"));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(9), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        switch (slider.id) {
            case 5:
                MWEConfig.fkcounterHUDSize = slider.getValueInt() / 100d;
                break;
            case 6:
                MWEConfig.fkcounterHUDPlayerAmount = slider.getValueInt();
                GuiManager.fkCounterHUD.updateDisplayText();
                break;
        }
    }

}
