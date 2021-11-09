package fr.alexdoru.fkcountermod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.gui.elements.ButtonFancy;
import fr.alexdoru.fkcountermod.gui.elements.ButtonToggle;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.MyGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class FKConfigGuiScreen extends MyGuiScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("fkcounter", "background.png");

    @SuppressWarnings("FieldMayBeFinal")
    private int columns = 3;
    @SuppressWarnings("FieldMayBeFinal")
    private int rows = 2;

    private final int buttonSize = 50;
    private final int widthBetweenButtons = 10;
    private final int heightBetweenButtons = 30;
    private final GuiScreen parent;

    private ButtonToggle buttoncompacthud;
    private ButtonToggle buttonshowplayers;

    public FKConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.add(new ButtonFancy(100, width / 2 + 45, height / 2 - findMenuHeight() / 2 + 8, 30, 14, "Move HUD", 0.5));
        buttonList.add(addSettingButton(ConfigHandler.show_fkcHUD, 0, 0, 0, "Show HUD"));
        buttonList.add(buttoncompacthud = addSettingButton(ConfigHandler.compact_hud, 1, 0, 1, "Compact HUD"));
        buttonList.add(buttonshowplayers = addSettingButton(ConfigHandler.show_players, 2, 0, 2, "Show Players"));
        buttonList.add(addSettingButton(ConfigHandler.draw_background, 3, 1, 0, "HUD Background"));
        buttonList.add(addSettingButton(ConfigHandler.text_shadow, 4, 1, 1, "Text Shadow"));
        this.buttonList.add(new GuiButton(200, getxCenter() - 150 / 2, getyCenter() + 86, 150, 20, parent == null ? "Close" : "Done"));
        super.initGui();
    }

    @Override
    public void actionPerformed(GuiButton button) {

        switch (button.id) {
            case 100:
                mc.displayGuiScreen(new PositionEditGuiScreen(FKCounterGui.instance, this));
                break;
            case 200:
                mc.displayGuiScreen(parent);
                break;
            case 0:
                ConfigHandler.show_fkcHUD = !ConfigHandler.show_fkcHUD;
                ((ButtonToggle) button).setting = ConfigHandler.show_fkcHUD;
                break;
            case 1:
                ConfigHandler.compact_hud = !ConfigHandler.compact_hud;
                ((ButtonToggle) button).setting = ConfigHandler.compact_hud;
                if (ConfigHandler.compact_hud && ConfigHandler.show_players) {
                    ConfigHandler.show_players = false;
                    buttonshowplayers.setting = false;
                }
                break;
            case 2:
                ConfigHandler.show_players = !ConfigHandler.show_players;
                ((ButtonToggle) button).setting = ConfigHandler.show_players;
                if (ConfigHandler.show_players && ConfigHandler.compact_hud) {
                    ConfigHandler.compact_hud = false;
                    buttoncompacthud.setting = false;
                }
                break;
            case 3:
                ConfigHandler.draw_background = !ConfigHandler.draw_background;
                ((ButtonToggle) button).setting = ConfigHandler.draw_background;
                break;
            case 4:
                ConfigHandler.text_shadow = !ConfigHandler.text_shadow;
                ((ButtonToggle) button).setting = ConfigHandler.text_shadow;
                break;
        }

        if (button instanceof ButtonToggle) {
            FKCounterGui.instance.updateDisplayText();
        }

    }

    private ButtonToggle addSettingButton(boolean setting, int buttonid, int row, int column, String buttonText) {

        int x;
        final int i = (widthBetweenButtons + buttonSize) * (column - columns / 2);

        if (columns % 2 == 0) { // even
            x = getxCenter() + widthBetweenButtons / 2 + i;
        } else { // odd
            x = getxCenter() - buttonSize / 2 + i;
        }

        int y = getyCenter() - findMenuHeight() / 2 + heightBetweenButtons + row * buttonSize;
        return new ButtonToggle(setting, buttonid, x + 13, y + 20, buttonText);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int rectWidth = findMenuWidth();
        int rectHeight = findMenuHeight();
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 0.7F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawModalRectWithCustomSizedTexture(width / 2 - rectWidth / 2, height / 2 - rectHeight / 2, 0, 0, rectWidth, rectHeight, rectWidth, rectHeight);
        drawCenteredString(fontRendererObj, "FKCounter v" + FKCounterMod.VERSION, width / 2, height / 2 - rectHeight / 2 + 10, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private int findMenuWidth() {
        return buttonSize * columns + widthBetweenButtons * (columns - 1);
    }

    private int findMenuHeight() {
        return heightBetweenButtons + buttonSize * rows;
    }

}
