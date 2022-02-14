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

        int XposLeftButton = getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth;
        int XposCenterButton = getxCenter() - buttonsWidth / 2;
        int XposRightButton = getxCenter() + buttonsWidth / 2 + 4;

        this.buttonList.add(new GuiButton(16, XposCenterButton, getYposForButton(-5), buttonsWidth, ButtonsHeight, getButtonDisplayString(16)));
        this.buttonList.add(new GuiButton(15, XposCenterButton, getYposForButton(-4), buttonsWidth, ButtonsHeight, getButtonDisplayString(15)));
        this.buttonList.add(new GuiButton(0, XposCenterButton, getYposForButton(-3), buttonsWidth, ButtonsHeight, getButtonDisplayString(0)));
        this.buttonList.add(new GuiButton(1, XposCenterButton, getYposForButton(-2), buttonsWidth, ButtonsHeight, getButtonDisplayString(1)));
        this.buttonList.add(new GuiButton(18, XposCenterButton, getYposForButton(-1), buttonsWidth, ButtonsHeight, getButtonDisplayString(18)));
        this.buttonList.add(new GuiButton(9, XposCenterButton, getYposForButton(0), buttonsWidth, ButtonsHeight, getButtonDisplayString(9)));
        this.buttonList.add(new GuiButton(2, XposCenterButton, getYposForButton(1), buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        this.buttonList.add(new GuiButton(3, XposCenterButton, getYposForButton(2), buttonsWidth, ButtonsHeight, getButtonDisplayString(3)));
        this.buttonList.add(new GuiButton(11, XposCenterButton, getYposForButton(3), buttonsWidth, ButtonsHeight, getButtonDisplayString(11)));
        this.buttonList.add(new GuiButton(19, XposCenterButton, getYposForButton(4), buttonsWidth, ButtonsHeight, getButtonDisplayString(19)));
        this.buttonList.add(new GuiButton(17, XposCenterButton, getYposForButton(5), buttonsWidth, ButtonsHeight, getButtonDisplayString(17)));
        this.buttonList.add(new GuiSlider(20, XposCenterButton, getYposForButton(6), buttonsWidth, ButtonsHeight, "Health threshold : ", " %", 0d, 100d, ConfigHandler.healthThreshold * 100d, false, true, this));
        this.buttonList.add(new GuiButton(4, getxCenter() - 150 / 2, getYposForButton(8), 150, ButtonsHeight, getButtonDisplayString(4)));

        this.buttonList.add(new GuiButton(5, XposRightButton, getYposForButton(1), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(5)));
        this.buttonList.add(new GuiButton(6, XposRightButton, getYposForButton(2), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(6)));
        this.buttonList.add(new GuiButton(10, XposRightButton, getYposForButton(3), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(10)));
        this.buttonList.add(new GuiButton(13, XposRightButton, getYposForButton(0), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(13)));

        this.buttonList.add(new GuiButton(7, XposLeftButton, getYposForButton(1), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(7)));
        this.buttonList.add(new GuiButton(8, XposLeftButton, getYposForButton(2), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(8)));
        this.buttonList.add(new GuiButton(12, XposLeftButton, getYposForButton(3), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(12)));
        this.buttonList.add(new GuiButton(14, XposLeftButton, getYposForButton(0), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(14)));
        super.initGui();
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 16:
                return "Strength particule HBR DRE : " + getSuffix(ConfigHandler.strengthParticules);
            case 15:
                return "Icons on names : " + getSuffix(ConfigHandler.toggleicons);
            case 0:
                return "Shorten coin messages : " + getSuffix(ConfigHandler.shortencoinmessage);
            case 9:
                return "HUD before hunter strength : " + getSuffix(ConfigHandler.hunterStrengthHUD);
            case 1:
                return "Report suggestions in chat : " + getSuffix(ConfigHandler.reportsuggestions);
            case 18:
                return "Cancel night vision effect : " + getSuffix(!ConfigHandler.keepNightVisionEffect);
            case 2:
                return "Show /kill cooldown HUD : " + getSuffix(ConfigHandler.show_killcooldownHUD);
            case 3:
                return "Show Arrow Hit HUD : " + getSuffix(ConfigHandler.show_ArrowHitHUD);
            case 11:
                return "Show wither death time HUD : " + (ConfigHandler.witherHUDinSiderbar ? EnumChatFormatting.YELLOW + "in Sidebar" : getSuffix(ConfigHandler.show_lastWitherHUD));
            case 19:
                return "Colored Tablist Scores : " + getSuffix(ConfigHandler.useColoredScores);
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
                textLines.add("");
                textLines.add(NameUtil.squadprefix + EnumChatFormatting.YELLOW + "Players in your squad");
                textLines.add(NameUtil.prefix_bhop + EnumChatFormatting.YELLOW + "Players reported for bhop");
                textLines.add(NameUtil.prefix + EnumChatFormatting.YELLOW + "Players reported for other cheats");
                textLines.add(NameUtil.prefix_scan + EnumChatFormatting.YELLOW + "Players flagged by the /scangame command");
                break;
            case 0:
                textLines.add(EnumChatFormatting.GREEN + "Makes the coin messages shorter by removing the network booster info");
                textLines.add(EnumChatFormatting.GREEN + "It makes the assists messages in mega walls fit on one line instead of two");
                textLines.add("");
                textLines.add(EnumChatFormatting.GOLD + "+100 coins! (hypixel's Network booster)" + EnumChatFormatting.AQUA + " FINAL KILL");
                textLines.add("Will become : ");
                textLines.add(EnumChatFormatting.GOLD + "+100 coins!" + EnumChatFormatting.AQUA + " FINAL KILL");
                break;
            case 9:
                textLines.add(EnumChatFormatting.GREEN + "When you play the Hunter class it prints a HUD and plays a sound before getting strength");
                break;
            case 1:
                textLines.add(EnumChatFormatting.GREEN + "When there is a message that respects the following patterns it will print a report suggestion in chat");
                textLines.add(EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "playername is bhoping");
                textLines.add(EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "wdr playername cheat");
                textLines.add(EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "report playername cheat");
                break;
            case 18:
                textLines.add(EnumChatFormatting.GREEN + "Removes the visual effect of night vision");
                break;
            case 2:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD with the cooldown of the /kill command in Mega Walls");
                break;
            case 3:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD with the health of your opponent on arrow hits");
                break;
            case 11:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD with the time it takes for the last wither to die");
                textLines.add(EnumChatFormatting.GREEN + "The HUD can be configured to appear in the Sidebar");
                break;
            case 19:
                textLines.add(EnumChatFormatting.GREEN + "Adds colors to the scores/health in the tablist depending on the value");
                textLines.add("");
                textLines.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM] " + EnumChatFormatting.DARK_GREEN + "44");
                textLines.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM] " + EnumChatFormatting.GREEN + "35");
                textLines.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM] " + EnumChatFormatting.YELLOW + "25");
                textLines.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM] " + EnumChatFormatting.RED + "15");
                textLines.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM]  " + EnumChatFormatting.DARK_RED + "5");
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
            case 18:
                ConfigHandler.keepNightVisionEffect = !ConfigHandler.keepNightVisionEffect;
                break;
            case 2:
                ConfigHandler.show_killcooldownHUD = !ConfigHandler.show_killcooldownHUD;
                break;
            case 3:
                ConfigHandler.show_ArrowHitHUD = !ConfigHandler.show_ArrowHitHUD;
                break;
            case 11:
                if (ConfigHandler.show_lastWitherHUD && !ConfigHandler.witherHUDinSiderbar) {
                    ConfigHandler.witherHUDinSiderbar = true;
                    break;
                }
                if (ConfigHandler.witherHUDinSiderbar && ConfigHandler.show_lastWitherHUD) {
                    ConfigHandler.witherHUDinSiderbar = false;
                    ConfigHandler.show_lastWitherHUD = false;
                    break;
                }
                if (!ConfigHandler.show_lastWitherHUD && !ConfigHandler.witherHUDinSiderbar) {
                    ConfigHandler.show_lastWitherHUD = true;
                }
                break;
            case 19:
                ConfigHandler.useColoredScores = !ConfigHandler.useColoredScores;
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
                LastWitherHPGui.instance.guiPosition.setRelative(0.75d, 0.05d);
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
        drawCenteredTitle("Mega Walls Enhancements v" + MegaWallsEnhancementsMod.version, 2, (width / 2.0f), getyCenter() - (ButtonsHeight + 4) * 7, Integer.parseInt("55FF55", 16));
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawTooltips(mouseX, mouseY);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 20) {
            ConfigHandler.healthThreshold = Math.floor(slider.getValue()) / 100d;
        }
    }

}
