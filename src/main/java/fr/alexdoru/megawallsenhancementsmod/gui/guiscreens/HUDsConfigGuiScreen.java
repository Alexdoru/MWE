package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.ArrowHitHUD;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.HunterStrengthHUD;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.KillCooldownHUD;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.LastWitherHPHUD;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.Loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HUDsConfigGuiScreen extends MyGuiScreen {

    public HUDsConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        final int buttonsWidth = 200;
        final int XposCenterButton = getxCenter() - buttonsWidth / 2;
        /* HUD Buttons */
        buttonList.add(new GuiButton(3, XposCenterButton, getButtonYPos(1), buttonsWidth, ButtonsHeight, getButtonDisplayString(3)));
        buttonList.add(new GuiButton(2, XposCenterButton, getButtonYPos(2), buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        buttonList.add(new GuiButton(9, XposCenterButton, getButtonYPos(3), buttonsWidth, ButtonsHeight, getButtonDisplayString(9)));
        buttonList.add(new GuiButton(11, XposCenterButton, getButtonYPos(4), buttonsWidth, ButtonsHeight, getButtonDisplayString(11)));
        final int sideButtonsWidth = 90;
        final int XposCenterLeftButton = getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth;
        final int XposCenterRightButton = getxCenter() + buttonsWidth / 2 + 4;
        /* Buttons : Reset HUD position */
        buttonList.add(new GuiButton(8, XposCenterLeftButton, getButtonYPos(1), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(8)));
        buttonList.add(new GuiButton(7, XposCenterLeftButton, getButtonYPos(2), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(7)));
        buttonList.add(new GuiButton(14, XposCenterLeftButton, getButtonYPos(3), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(14)));
        buttonList.add(new GuiButton(12, XposCenterLeftButton, getButtonYPos(4), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(12)));
        /* Buttons : Move HUD */
        buttonList.add(new GuiButton(6, XposCenterRightButton, getButtonYPos(1), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(6)));
        buttonList.add(new GuiButton(5, XposCenterRightButton, getButtonYPos(2), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(5)));
        buttonList.add(new GuiButton(13, XposCenterRightButton, getButtonYPos(3), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(13)));
        buttonList.add(new GuiButton(10, XposCenterRightButton, getButtonYPos(4), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(10)));
        /* Exit button */
        buttonList.add(new GuiButton(4, getxCenter() - 150 / 2, getButtonYPos(6), 150, ButtonsHeight, getButtonDisplayString(4)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle(EnumChatFormatting.DARK_PURPLE + "HUDs", 2, getxCenter(), getButtonYPos(-1));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 9:
                return "Strength HUD : " + getSuffix(ConfigHandler.showStrengthHUD);
            case 2:
                return "/kill cooldown HUD : " + getSuffix(ConfigHandler.showKillCooldownHUD);
            case 3:
                return "Arrow Hit HUD : " + getSuffix(ConfigHandler.showArrowHitHUD);
            case 11:
                return "Wither death time HUD : " + (ConfigHandler.witherHUDinSidebar ? EnumChatFormatting.YELLOW + "in Sidebar" : getSuffix(ConfigHandler.showLastWitherHUD));
            case 4:
                return "Done";
            case 5:
            case 6:
            case 10:
            case 13:
                return "Move HUD";
            case 7:
            case 8:
            case 12:
            case 14:
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
            case 11:
                if (ConfigHandler.showLastWitherHUD && !ConfigHandler.witherHUDinSidebar) {
                    ConfigHandler.witherHUDinSidebar = true;
                    if (Loader.isModLoaded("feather")) {
                        ChatUtil.addChatMessage(EnumChatFormatting.RED + "The sidebar integration for HUD doesn't work with Feather because the client is obfuscated and closed source >:(");
                    }
                    break;
                }
                if (!ConfigHandler.showLastWitherHUD && !ConfigHandler.witherHUDinSidebar) {
                    ConfigHandler.showLastWitherHUD = true;
                    break;
                }
                ConfigHandler.witherHUDinSidebar = false;
                ConfigHandler.showLastWitherHUD = false;
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
            case 7:
                KillCooldownHUD.instance.guiPosition.setRelative(0d, 0d);
                break;
            case 8:
                ArrowHitHUD.instance.guiPosition.setRelative(0.5d, 9d / 20d);
                break;
            case 12:
                LastWitherHPHUD.instance.guiPosition.setRelative(0.75d, 0.05d);
                break;
            case 14:
                HunterStrengthHUD.instance.guiPosition.setRelative(0.5d, 8d / 20d);
                break;
            default:
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);
    }

}
