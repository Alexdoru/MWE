package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.ChatEvents;
import fr.alexdoru.megawallsenhancementsmod.events.LowHPIndicator;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MWEnConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

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
        this.buttonList.add(new GuiButton(17, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 4, buttonsWidth, ButtonsHeight, getButtonDisplayString(17)));
        this.buttonList.add(new GuiSlider(18, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 5, buttonsWidth, ButtonsHeight, "Health threshold : ", " %", 0d, 100d, ConfigHandler.healthThreshold * 100d, false, true, this));
        this.buttonList.add(new GuiButton(4, getxCenter() - 150 / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 7, 150, ButtonsHeight, getButtonDisplayString(4)));

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
            case 17:
                return "Sound warning when low HP : " + getSuffix(ConfigHandler.playSoundLowHP);
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
    public List<String> getTooltipText(int id) {
        List<String> textLines = new ArrayList<>();
        switch (id) {
            case 16:
                textLines.add(EnumChatFormatting.GREEN + "Spawns angry villager particles when the player gets a final kill");
                break;
            case 15:
                textLines.add(EnumChatFormatting.GREEN + "Toggles all icons on nametags and in the tablist");
                break;
            case 0:
                textLines.add(EnumChatFormatting.GREEN + "Makes the coin messages shorter by removing the network booster info");
                textLines.add("");
                textLines.add(EnumChatFormatting.GOLD + "+100 coins! (Alexdoru's Network booster)" + EnumChatFormatting.AQUA + " FINAL KILL");
                textLines.add("Will become : ");
                textLines.add(EnumChatFormatting.GOLD + "+100 coins!" + EnumChatFormatting.AQUA + " FINAL KILL");
                break;
            case 9:
                textLines.add(EnumChatFormatting.GREEN + "When you play the Hunter class it prints a HUD and plays a sound before getting strength");
                break;
            case 1:
                textLines.add(EnumChatFormatting.GREEN + "When there is a shout that respects the following patterns it will print a report suggestion in chat");
                textLines.add(EnumChatFormatting.GOLD + "[SHOUT] " + EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "Alexdoru is bhoping");
                textLines.add(EnumChatFormatting.GOLD + "[SHOUT] " + EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "wdr playername cheat");
                textLines.add(EnumChatFormatting.GOLD + "[SHOUT] " + EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "report playername cheat");
                break;
            case 11:
                textLines.add(EnumChatFormatting.GREEN + "When there is one wither alive it draws a HUD with the time it takes for the last wither to die");
                break;
        }
        return textLines;
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
            case 17:
                ConfigHandler.playSoundLowHP = !ConfigHandler.playSoundLowHP;
                if (ConfigHandler.playSoundLowHP) {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(LowHPIndicator.lowHPSound, 1.0F));
                }
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
        drawTooltips(mouseX, mouseY);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 18) {
            ConfigHandler.healthThreshold = Math.floor(slider.getValue()) / 100d;
        }
    }

}
