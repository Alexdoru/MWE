package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.ChatEvents;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
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
        this.buttonList.add(new GuiButton(0, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4) * 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(0)));
        this.buttonList.add(new GuiButton(9, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, getButtonDisplayString(9)));
        this.buttonList.add(new GuiButton(1, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(1)));
        this.buttonList.add(new GuiButton(2, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        this.buttonList.add(new GuiButton(3, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(3)));
        this.buttonList.add(new GuiButton(4, getxCenter() - 150 / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 4, 150, ButtonsHeight, getButtonDisplayString(4)));
        this.buttonList.add(new GuiButton(5, getxCenter() + buttonsWidth / 2 + 4, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(5)));
        this.buttonList.add(new GuiButton(6, getxCenter() + buttonsWidth / 2 + 4, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 2, sideButtonsWidth, ButtonsHeight, getButtonDisplayString(6)));
        this.buttonList.add(new GuiButton(7, getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(7)));
        this.buttonList.add(new GuiButton(8, getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 2, sideButtonsWidth, ButtonsHeight, getButtonDisplayString(8)));
        super.initGui();
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 0:
                return "Shorten coin messages : " + getSuffix(MWEnConfigHandler.shortencoinmessage);
            case 9:
                return "Sound before hunter strength : " + getSuffix(MWEnConfigHandler.hunterStrengthSound);
            case 1:
                return "Report suggestions in chat : " + getSuffix(MWEnConfigHandler.reportsuggestions);
            case 2:
                return "Show /kill cooldown HUD : " + getSuffix(MWEnConfigHandler.show_killcooldownGUI);
            case 3:
                return "Show Arrow Hit HUD : " + getSuffix(MWEnConfigHandler.show_ArrowHitGui);
            case 4:
                return "Done";
            case 5:
            case 6:
                return "Move HUD";
            case 7:
            case 8:
                return "Reset HUD position";
            default:
                return "no display text for this button id";
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                MWEnConfigHandler.shortencoinmessage = !MWEnConfigHandler.shortencoinmessage;
                break;
            case 9:
                if (!MWEnConfigHandler.hunterStrengthSound) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(ChatEvents.strengthSound, 0.0F));
                }
                MWEnConfigHandler.hunterStrengthSound = !MWEnConfigHandler.hunterStrengthSound;
                break;
            case 1:
                if (!MWEnConfigHandler.reportsuggestions) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(ChatEvents.reportSuggestionSound, 0.0F));
                }
                MWEnConfigHandler.reportsuggestions = !MWEnConfigHandler.reportsuggestions;
                break;
            case 2:
                MWEnConfigHandler.show_killcooldownGUI = !MWEnConfigHandler.show_killcooldownGUI;
                break;
            case 3:
                MWEnConfigHandler.show_ArrowHitGui = !MWEnConfigHandler.show_ArrowHitGui;
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
            case 7:
                KillCooldownGui.instance.guiPosition.setRelative(0d, 0d);
                break;
            case 8:
                ArrowHitGui.instance.guiPosition.setRelative(0.5d, 9d / 20d);
                break;
            default:
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle("Mega Walls Enhancements v" + MegaWallsEnhancementsMod.version, 2, (width / 2.0f), getyCenter() - (ButtonsHeight + 4) * 5);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        MWEnConfigHandler.saveConfig();
        super.onGuiClosed();
    }

}
