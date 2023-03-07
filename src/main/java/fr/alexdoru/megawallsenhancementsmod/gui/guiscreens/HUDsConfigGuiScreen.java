package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.*;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HUDsConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    public HUDsConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final int buttonsWidth = 200;
        final int sideButtonsWidth = 90;
        this.maxWidth = sideButtonsWidth * 2 + buttonsWidth + 4 * 2;
        this.maxHeight = (buttonsHeight + 4) * 12 + buttonsHeight;
        super.initGui();
        final int XposCenterButton = getxCenter() - buttonsWidth / 2;
        final int XposCenterLeftButton = getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth;
        final int XposCenterRightButton = getxCenter() + buttonsWidth / 2 + 4;
        /* HUD Buttons */
        buttonList.add(new GuiButton(3, XposCenterButton, getButtonYPos(1), buttonsWidth, buttonsHeight, getButtonDisplayString(3))); //arrow hit hud
        buttonList.add(new GuiButton(2, XposCenterButton, getButtonYPos(2), buttonsWidth, buttonsHeight, getButtonDisplayString(2))); // /kill cooldown hud
        buttonList.add(new GuiButton(22, XposCenterButton, getButtonYPos(3), buttonsWidth, buttonsHeight, getButtonDisplayString(22))); //squad hud
        buttonList.add(new GuiButton(9, XposCenterButton, getButtonYPos(4), buttonsWidth, buttonsHeight, getButtonDisplayString(9))); //strength hud
        buttonList.add(new GuiButton(11, XposCenterButton, getButtonYPos(5), buttonsWidth, buttonsHeight, getButtonDisplayString(11))); //wither death time hud
        buttonList.add(new GuiButton(15, XposCenterButton, getButtonYPos(6), buttonsWidth, buttonsHeight, getButtonDisplayString(15))); //creeper primed tnt hud
        buttonList.add(new GuiButton(18, XposCenterButton, getButtonYPos(7), buttonsWidth, buttonsHeight, getButtonDisplayString(18))); //energy display hud
        buttonList.add(new GuiSlider(21, XposCenterButton, getButtonYPos(8), buttonsWidth, buttonsHeight, "Energy threshold : ", "", 1d, 160d, ConfigHandler.aquaEnergyDisplayThreshold, false, true, this)); //energy display threshold slider
        buttonList.add(new GuiButton(28, XposCenterButton, getButtonYPos(9), buttonsWidth, buttonsHeight, getButtonDisplayString(28))); //speed hud
        buttonList.add(new GuiButton(31, XposCenterButton, getButtonYPos(10), buttonsWidth, buttonsHeight, getButtonDisplayString(31))); //phx bond hud

        /* Buttons : Reset HUD position */
        buttonList.add(new GuiButton(8, XposCenterLeftButton, getButtonYPos(1), sideButtonsWidth, buttonsHeight, getButtonDisplayString(8))); //arrow hit hud
        buttonList.add(new GuiButton(7, XposCenterLeftButton, getButtonYPos(2), sideButtonsWidth, buttonsHeight, getButtonDisplayString(7))); // /kill cooldown hud
        buttonList.add(new GuiButton(23, XposCenterLeftButton, getButtonYPos(3), sideButtonsWidth, buttonsHeight, getButtonDisplayString(23))); //squad hud
        buttonList.add(new GuiButton(14, XposCenterLeftButton, getButtonYPos(4), sideButtonsWidth, buttonsHeight, getButtonDisplayString(14))); //strength hud
        buttonList.add(new GuiButton(12, XposCenterLeftButton, getButtonYPos(5), sideButtonsWidth, buttonsHeight, getButtonDisplayString(12))); //wither death time hud
        buttonList.add(new GuiButton(19, XposCenterLeftButton, getButtonYPos(6), sideButtonsWidth, buttonsHeight, getButtonDisplayString(19))); //creeper primed tnt hud
        buttonList.add(new GuiButton(16, XposCenterLeftButton, getButtonYPos(7), sideButtonsWidth, buttonsHeight, getButtonDisplayString(16))); //energy display hud
        buttonList.add(new GuiButton(29, XposCenterLeftButton, getButtonYPos(9), sideButtonsWidth, buttonsHeight, getButtonDisplayString(29))); //speed hud
        buttonList.add(new GuiButton(32, XposCenterLeftButton, getButtonYPos(10), sideButtonsWidth, buttonsHeight, getButtonDisplayString(32))); //phx bond hud
        /* Buttons : Move HUD */
        buttonList.add(new GuiButton(6, XposCenterRightButton, getButtonYPos(1), sideButtonsWidth, buttonsHeight, getButtonDisplayString(6))); //arrow hit hud
        buttonList.add(new GuiButton(5, XposCenterRightButton, getButtonYPos(2), sideButtonsWidth, buttonsHeight, getButtonDisplayString(5))); // /kill cooldown hud
        buttonList.add(new GuiButton(24, XposCenterRightButton, getButtonYPos(3), sideButtonsWidth, buttonsHeight, getButtonDisplayString(24))); //squad hud
        buttonList.add(new GuiButton(13, XposCenterRightButton, getButtonYPos(4), sideButtonsWidth, buttonsHeight, getButtonDisplayString(13))); //strength hud
        buttonList.add(new GuiButton(10, XposCenterRightButton, getButtonYPos(5), sideButtonsWidth, buttonsHeight, getButtonDisplayString(10))); //wither death time hud
        buttonList.add(new GuiButton(17, XposCenterRightButton, getButtonYPos(6), sideButtonsWidth, buttonsHeight, getButtonDisplayString(17))); //creeper primed tnt hud
        buttonList.add(new GuiButton(20, XposCenterRightButton, getButtonYPos(7), sideButtonsWidth, buttonsHeight, getButtonDisplayString(20))); //energy display hud
        buttonList.add(new GuiButton(30, XposCenterRightButton, getButtonYPos(9), sideButtonsWidth, buttonsHeight, getButtonDisplayString(30))); //speed hud
        buttonList.add(new GuiButton(33, XposCenterRightButton, getButtonYPos(10), sideButtonsWidth, buttonsHeight, getButtonDisplayString(33))); //phx bond hud

        /* Exit button */
        buttonList.add(new GuiButton(4, getxCenter() - 150 / 2, getButtonYPos(11), 150, buttonsHeight, getButtonDisplayString(4))); //exit
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle(EnumChatFormatting.DARK_PURPLE + "HUDs", 2, getxCenter(), getButtonYPos(-1));
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawTooltips(mouseX, mouseY);
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 3:
                return "Arrow Hit HUD : " + getSuffix(ConfigHandler.showArrowHitHUD);
            case 2:
                return "/kill cooldown HUD : " + getSuffix(ConfigHandler.showKillCooldownHUD);
            case 22:
                return "Squad HUD : " + getSuffix(ConfigHandler.showSquadHUD);
            case 9:
                return "Strength HUD : " + getSuffix(ConfigHandler.showStrengthHUD);
            case 11:
                return "Wither death time HUD : " + (ConfigHandler.witherHUDinSidebar ? EnumChatFormatting.YELLOW + "in Sidebar" : getSuffix(ConfigHandler.showLastWitherHUD));
            case 15:
                return "Creeper primed TNT HUD : " + getSuffix(ConfigHandler.showPrimedTNTHUD);
            case 18:
                return "Energy display HUD : " + getSuffix(ConfigHandler.showEnergyDisplayHUD);
            case 28:
                return "Speed HUD : " + getSuffix(ConfigHandler.showSpeedHUD);
            case 31:
                return "Phoenix bond HUD : " + getSuffix(ConfigHandler.showPhxBondHUD);
            case 4:
                return "Done";
            case 5:
            case 6:
            case 10:
            case 13:
            case 24:
            case 17:
            case 20:
            case 30:
            case 33:
                return "Move HUD";
            case 7:
            case 8:
            case 12:
            case 14:
            case 23:
            case 16:
            case 19:
            case 29:
            case 32:
                return "Reset position";
            default:
                return "no display text for this button id";
        }
    }

    @Override
    protected List<String> getTooltipText(int id) {
        final List<String> textLines = new ArrayList<>();
        switch (id) {
            case 2:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD with the cooldown of the /kill command in Mega Walls");
                break;
            case 3:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD with the health of your opponent on arrow hits");
                break;
            case 9:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD when you get strenght with Dreadlord, Herobrine, Hunter and Zombie");
                break;
            case 11:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD with the time it takes for the last wither to die");
                textLines.add(EnumChatFormatting.GREEN + "The HUD can be configured to appear in the Sidebar");
                break;
            case 22:
                textLines.add(EnumChatFormatting.GREEN + "Displays a mini tablist with just your squadmates");
                break;
            case 15:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD with the cooldown of the primed TNT when playing Creeper");
                break;
            case 18:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD with the amount of energy you have");
                textLines.add(EnumChatFormatting.GREEN + "Turns " + EnumChatFormatting.AQUA + EnumChatFormatting.BOLD + "aqua" + EnumChatFormatting.GREEN + " when your energy level exceeds the amount set below");
                break;
            case 28:
                textLines.add(EnumChatFormatting.GREEN + "Displays you own speed");
                break;
            case 31:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD with the hearts healed from a Phoenix bond");
                break;
        }
        return textLines;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 9:
                ConfigHandler.showStrengthHUD = !ConfigHandler.showStrengthHUD;
                if (ConfigHandler.showStrengthHUD) {
                    SoundUtil.playStrengthSound();
                }
                break;
            case 2:
                ConfigHandler.showKillCooldownHUD = !ConfigHandler.showKillCooldownHUD;
                break;
            case 3:
                ConfigHandler.showArrowHitHUD = !ConfigHandler.showArrowHitHUD;
                break;
            case 22:
                ConfigHandler.showSquadHUD = !ConfigHandler.showSquadHUD;
                break;
            case 11:
                if (ConfigHandler.showLastWitherHUD && !ConfigHandler.witherHUDinSidebar) {
                    ConfigHandler.witherHUDinSidebar = true;
                    break;
                }
                if (!ConfigHandler.showLastWitherHUD && !ConfigHandler.witherHUDinSidebar) {
                    ConfigHandler.showLastWitherHUD = true;
                    break;
                }
                ConfigHandler.witherHUDinSidebar = false;
                ConfigHandler.showLastWitherHUD = false;
                break;
            case 15:
                ConfigHandler.showPrimedTNTHUD = !ConfigHandler.showPrimedTNTHUD;
                break;
            case 18:
                ConfigHandler.showEnergyDisplayHUD = !ConfigHandler.showEnergyDisplayHUD;
                break;
            case 28:
                ConfigHandler.showSpeedHUD = !ConfigHandler.showSpeedHUD;
                break;
            case 31:
                ConfigHandler.showPhxBondHUD = !ConfigHandler.showPhxBondHUD;
                break;
            case 4:
                mc.displayGuiScreen(parent);
                break;
            case 5:
                mc.displayGuiScreen(new PositionEditGuiScreen(KillCooldownHUD.instance, this));
                break;
            case 6:
                mc.displayGuiScreen(new PositionEditGuiScreen(ArrowHitHUD.instance, this));
                break;
            case 10:
                mc.displayGuiScreen(new PositionEditGuiScreen(LastWitherHPHUD.instance, this));
                break;
            case 13:
                mc.displayGuiScreen(new PositionEditGuiScreen(HunterStrengthHUD.instance, this));
                break;
            case 24:
                mc.displayGuiScreen(new PositionEditGuiScreen(SquadHealthHUD.instance, this));
                break;
            case 17:
                mc.displayGuiScreen(new PositionEditGuiScreen(CreeperPrimedTNTHUD.instance, this));
                break;
            case 20:
                mc.displayGuiScreen(new PositionEditGuiScreen(EnergyDisplayHUD.instance, this));
                break;
            case 30:
                mc.displayGuiScreen(new PositionEditGuiScreen(SpeeedHUD.instance, this));
                break;
            case 33:
                mc.displayGuiScreen(new PositionEditGuiScreen(PhxBondHud.instance, this));
                break;
            case 7:
                KillCooldownHUD.instance.guiPosition.resetToDefault();
                break;
            case 8:
                ArrowHitHUD.instance.guiPosition.resetToDefault();
                break;
            case 12:
                LastWitherHPHUD.instance.guiPosition.resetToDefault();
                break;
            case 14:
                HunterStrengthHUD.instance.guiPosition.resetToDefault();
                break;
            case 23:
                SquadHealthHUD.instance.guiPosition.resetToDefault();
                break;
            case 16:
                CreeperPrimedTNTHUD.instance.guiPosition.resetToDefault();
                break;
            case 19:
                EnergyDisplayHUD.instance.guiPosition.resetToDefault();
                break;
            case 29:
                SpeeedHUD.instance.guiPosition.resetToDefault();
                break;
            case 32:
                PhxBondHud.instance.guiPosition.resetToDefault();
                break;
            default:
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 21) {
            ConfigHandler.aquaEnergyDisplayThreshold = (int) slider.getValue();
        }
    }

}
