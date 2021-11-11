package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.ChatEvents;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class MWEnConfigGuiScreen extends MyGuiScreen {

    private final int ButtonsHeight = 20;
    private final GuiScreen parent;

    public MWEnConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        /*
         * Defines the button list
         */
        final int buttonsWidth = 210;
        final int sideButtonsWidth = 100;
        this.buttonList.add(new GuiButton(16, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4) * 4, buttonsWidth, ButtonsHeight, getButtonDisplayString(16)));
        this.buttonList.add(new GuiButton(15, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4) * 3, buttonsWidth, ButtonsHeight, getButtonDisplayString(15)));
        this.buttonList.add(new GuiButton(0, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4) * 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(0)));
        this.buttonList.add(new GuiButton(9, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(9)));
        this.buttonList.add(new GuiButton(1, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, getButtonDisplayString(1)));
        this.buttonList.add(new GuiButton(2, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        this.buttonList.add(new GuiButton(3, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(3)));
        this.buttonList.add(new GuiButton(11, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 3, buttonsWidth, ButtonsHeight, getButtonDisplayString(11)));
        this.buttonList.add(new GuiButton(4, getxCenter() - 150 / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 5, 150, ButtonsHeight, getButtonDisplayString(4)));

        this.buttonList.add(new GuiButton(5, getxCenter() + buttonsWidth / 2 + 4, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(5)));
        this.buttonList.add(new GuiButton(6, getxCenter() + buttonsWidth / 2 + 4, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 2, sideButtonsWidth, ButtonsHeight, getButtonDisplayString(6)));
        this.buttonList.add(new GuiButton(10, getxCenter() + buttonsWidth / 2 + 4, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 3, sideButtonsWidth, ButtonsHeight, getButtonDisplayString(10)));
        this.buttonList.add(new GuiButton(13, getxCenter() + buttonsWidth / 2 + 4, getyCenter() - ButtonsHeight / 2, sideButtonsWidth, ButtonsHeight, getButtonDisplayString(13)));

        this.buttonList.add(new GuiButton(7, getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(7)));
        this.buttonList.add(new GuiButton(8, getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 2, sideButtonsWidth, ButtonsHeight, getButtonDisplayString(8)));
        this.buttonList.add(new GuiButton(12, getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 3, sideButtonsWidth, ButtonsHeight, getButtonDisplayString(12)));
        this.buttonList.add(new GuiButton(14, getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth, getyCenter() - ButtonsHeight / 2, sideButtonsWidth, ButtonsHeight, getButtonDisplayString(14)));
        super.initGui();
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 16:
                return "Strength particule HBR DRE : " + getSuffix(ConfigHandler.strengthParticules);
            case 15:
                return "Icons on names " + NameUtil.squadprefix + NameUtil.prefix + " : " + getSuffix(ConfigHandler.toggleicons);
            case 0:
                return "Shorten coin messages : " + getSuffix(ConfigHandler.shortencoinmessage);
            case 9:
                return "HUD before hunter strength : " + getSuffix(ConfigHandler.hunterStrengthHUD);
            case 1:
                return "Report suggestions in chat : " + getSuffix(ConfigHandler.reportsuggestions);
            case 2:
                return "Show /kill cooldown HUD : " + getSuffix(ConfigHandler.show_killcooldownHUD);
            case 3:
                return "Show Arrow Hit HUD : " + getSuffix(ConfigHandler.show_ArrowHitHUD);
            case 11:
                return "Show wither death time HUD : " + getSuffix(ConfigHandler.show_lastWitherHUD);
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
                return "Reset HUD position";
            default:
                return "no display text for this button id";
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 16:
                ConfigHandler.strengthParticules = !ConfigHandler.strengthParticules;
                break;
            case 15:
                ConfigHandler.toggleIcons();
                break;
            case 0:
                ConfigHandler.shortencoinmessage = !ConfigHandler.shortencoinmessage;
                break;
            case 9:
                if (!ConfigHandler.hunterStrengthHUD) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(ChatEvents.strengthSound, 0.0F));
                }
                ConfigHandler.hunterStrengthHUD = !ConfigHandler.hunterStrengthHUD;
                break;
            case 1:
                if (!ConfigHandler.reportsuggestions) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(ChatEvents.reportSuggestionSound, 1.0F));
                }
                ConfigHandler.reportsuggestions = !ConfigHandler.reportsuggestions;
                break;
            case 2:
                ConfigHandler.show_killcooldownHUD = !ConfigHandler.show_killcooldownHUD;
                break;
            case 3:
                ConfigHandler.show_ArrowHitHUD = !ConfigHandler.show_ArrowHitHUD;
                break;
            case 11:
                ConfigHandler.show_lastWitherHUD = !ConfigHandler.show_lastWitherHUD;
                break;
            case 4:
                mc.displayGuiScreen(parent);
                break;
            case 5:
                mc.displayGuiScreen(new PositionEditGuiScreen(KillCooldownGui.instance, this));
                break;
            case 6:
                mc.displayGuiScreen(new PositionEditGuiScreen(ArrowHitGui.instance, this));
                break;
            case 10:
                mc.displayGuiScreen(new PositionEditGuiScreen(LastWitherHPGui.instance, this));
                break;
            case 13:
                mc.displayGuiScreen(new PositionEditGuiScreen(HunterStrengthGui.instance, this));
                break;
            case 7:
                KillCooldownGui.instance.guiPosition.setRelative(0d, 0d);
                break;
            case 8:
                ArrowHitGui.instance.guiPosition.setRelative(0.5d, 9d / 20d);
                break;
            case 12:
                LastWitherHPGui.instance.guiPosition.setRelative(0.9d, 0.1d);
                break;
            case 14:
                HunterStrengthGui.instance.guiPosition.setRelative(0.5d, 8d / 20d);
                break;
            default:
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle("Mega Walls Enhancements v" + MegaWallsEnhancementsMod.version, 2, (width / 2.0f), getyCenter() - (ButtonsHeight + 4) * 6);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
