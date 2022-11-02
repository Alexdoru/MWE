package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.ButtonFancy;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.ButtonToggle;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.FKCounterHUD;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.common.Loader;

public class FKConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("fkcounter", "background.png");
    private static final int columns = 4;
    private static final int rows = 2;
    private static final int buttonSize = 50;
    private static final int widthBetweenButtons = 10;
    private static final int heightBetweenButtons = 30;

    private ButtonToggle buttoncompacthud;
    private ButtonToggle buttonsidebar;
    private ButtonToggle buttonshowplayers;

    public FKConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new ButtonFancy(100, getxCenter() + widthBetweenButtons / 2 + (widthBetweenButtons + buttonSize) + 10, getButtonYPos(4) - findMenuHeight() / 2 + heightBetweenButtons + buttonSize + 10, 30, 14, "Move HUD", 0.5));

        buttonList.add(addSettingButton(ConfigHandler.showfkcounterHUD, 0, 0, 0, "Show HUD"));
        buttonList.add(buttoncompacthud = addSettingButton(ConfigHandler.fkcounterHUDCompact, 1, 0, 1, "Compact HUD"));
        buttonList.add(buttonsidebar = addSettingButton(ConfigHandler.fkcounterHUDinSidebar, 7, 0, 2, "HUD in Sidebar"));
        buttonList.add(addSettingButton(ConfigHandler.fkcounterHUDTablist, 8, 0, 3, "FK in tablist"));
        buttonList.add(addSettingButton(ConfigHandler.fkcounterHUDDrawBackground, 3, 1, 0, "HUD Background"));
        buttonList.add(addSettingButton(ConfigHandler.fkcounterHUDTextShadow, 4, 1, 1, "Text Shadow"));
        buttonList.add(buttonshowplayers = addSettingButton(ConfigHandler.fkcounterHUDShowPlayers, 2, 1, 2, "Show Players"));

        buttonList.add(new GuiSlider(5, getxCenter() - 150 / 2, getButtonYPos(7), "HUD Size : ", 0.1d, 4d, ConfigHandler.fkcounterHUDSize, this));
        buttonList.add(new GuiSlider(6, getxCenter() - 150 / 2, getButtonYPos(8), 150, 20, "Player amount : ", "", 1d, 10d, ConfigHandler.fkcounterHUDPlayerAmount, false, true, this));
        buttonList.add(new GuiButton(200, getxCenter() - 150 / 2, getButtonYPos(9), 150, 20, "Done"));
    }

    private ButtonToggle addSettingButton(boolean setting, int buttonid, int row, int column, String buttonText) {
        final int x;
        final int i = (widthBetweenButtons + buttonSize) * (column - columns / 2);
        //if (columns % 2 == 0) { // even
        x = getxCenter() + widthBetweenButtons / 2 + i;
        //} else { // odd
        //    x = getxCenter() - buttonSize / 2 + i;
        //}
        final int y = getButtonYPos(4) - findMenuHeight() / 2 + heightBetweenButtons + row * buttonSize;
        return new ButtonToggle(setting, buttonid, x + 10, y + 10, buttonText);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final int rectWidth = findMenuWidth();
        final int rectHeight = findMenuHeight();
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 0.7F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawModalRectWithCustomSizedTexture(getxCenter() - rectWidth / 2, getButtonYPos(4) - rectHeight / 2, 0, 0, rectWidth, rectHeight, rectWidth, rectHeight);
        drawCenteredTitle(EnumChatFormatting.AQUA + "Final Kill Counter v" + FKCounterMod.VERSION, 2, getxCenter(), getButtonYPos(-1));
        final String msg = "for Mega Walls";
        drawCenteredString(fontRendererObj, EnumChatFormatting.GRAY + msg, getxCenter() + fontRendererObj.getStringWidth(msg), getButtonYPos(-1) + 2 * fontRendererObj.FONT_HEIGHT, 0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 100:
                mc.displayGuiScreen(new PositionEditGuiScreen(FKCounterHUD.instance, this));
                break;
            case 200:
                mc.displayGuiScreen(parent);
                break;
            case 0:
                ConfigHandler.showfkcounterHUD = !ConfigHandler.showfkcounterHUD;
                ((ButtonToggle) button).setting = ConfigHandler.showfkcounterHUD;
                break;
            case 1:
                ConfigHandler.fkcounterHUDCompact = !ConfigHandler.fkcounterHUDCompact;
                ((ButtonToggle) button).setting = ConfigHandler.fkcounterHUDCompact;
                if (ConfigHandler.fkcounterHUDCompact) {
                    ConfigHandler.fkcounterHUDShowPlayers = false;
                    buttonshowplayers.setting = false;
                } else {
                    ConfigHandler.fkcounterHUDinSidebar = false;
                    buttonsidebar.setting = false;
                }
                break;
            case 2:
                ConfigHandler.fkcounterHUDShowPlayers = !ConfigHandler.fkcounterHUDShowPlayers;
                ((ButtonToggle) button).setting = ConfigHandler.fkcounterHUDShowPlayers;
                if (ConfigHandler.fkcounterHUDShowPlayers) {
                    ConfigHandler.fkcounterHUDCompact = false;
                    buttoncompacthud.setting = false;
                    ConfigHandler.fkcounterHUDinSidebar = false;
                    buttonsidebar.setting = false;
                }
                break;
            case 3:
                ConfigHandler.fkcounterHUDDrawBackground = !ConfigHandler.fkcounterHUDDrawBackground;
                ((ButtonToggle) button).setting = ConfigHandler.fkcounterHUDDrawBackground;
                break;
            case 4:
                ConfigHandler.fkcounterHUDTextShadow = !ConfigHandler.fkcounterHUDTextShadow;
                ((ButtonToggle) button).setting = ConfigHandler.fkcounterHUDTextShadow;
                break;
            case 7:
                ConfigHandler.fkcounterHUDinSidebar = !ConfigHandler.fkcounterHUDinSidebar;
                ((ButtonToggle) button).setting = ConfigHandler.fkcounterHUDinSidebar;
                if (ConfigHandler.fkcounterHUDinSidebar) {
                    ConfigHandler.fkcounterHUDShowPlayers = false;
                    buttonshowplayers.setting = false;
                    ConfigHandler.fkcounterHUDCompact = true;
                    buttoncompacthud.setting = true;
                    if (Loader.isModLoaded("feather")) {
                        ChatUtil.addChatMessage(EnumChatFormatting.RED + "The sidebar integration for HUD doesn't work with Feather because the client is obfuscated and closed source >:(");
                    }
                }
                break;
            case 8:
                ConfigHandler.fkcounterHUDTablist = !ConfigHandler.fkcounterHUDTablist;
                ((ButtonToggle) button).setting = ConfigHandler.fkcounterHUDTablist;
                break;
        }
        if (button instanceof ButtonToggle) {
            FKCounterHUD.instance.updateDisplayText();
        }
    }

    private int findMenuWidth() {
        return buttonSize * columns + widthBetweenButtons * (columns - 1);
    }

    private int findMenuHeight() {
        return heightBetweenButtons + buttonSize * rows;
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
