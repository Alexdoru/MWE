package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.MyGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;

public class HitboxConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    private final int buttonWidth = 170;

    public HitboxConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {

        int XleftColumn = getxCenter() - buttonWidth - 10;
        int XrightColumn = getxCenter() + 10;

        buttonList.add(new GuiButton(1, XleftColumn, getYposForButton(-4), buttonWidth, ButtonsHeight, getButtonDisplayString(1)));
        buttonList.add(new GuiButton(2, XleftColumn, getYposForButton(-3), buttonWidth, ButtonsHeight, getButtonDisplayString(2)));
        buttonList.add(new GuiButton(3, XleftColumn, getYposForButton(-2), buttonWidth, ButtonsHeight, getButtonDisplayString(3)));
        buttonList.add(new GuiButton(13, XleftColumn, getYposForButton(-1), buttonWidth, ButtonsHeight, getButtonDisplayString(13)));
        buttonList.add(new GuiButton(4, XleftColumn, getYposForButton(0), buttonWidth, ButtonsHeight, getButtonDisplayString(4)));
        buttonList.add(new GuiButton(5, XleftColumn, getYposForButton(1), buttonWidth, ButtonsHeight, getButtonDisplayString(5)));
        buttonList.add(new GuiButton(6, XleftColumn, getYposForButton(2), buttonWidth, ButtonsHeight, getButtonDisplayString(6)));
        buttonList.add(new GuiButton(7, XleftColumn, getYposForButton(3), buttonWidth, ButtonsHeight, getButtonDisplayString(7)));
        buttonList.add(new GuiButton(8, XleftColumn, getYposForButton(4), buttonWidth, ButtonsHeight, getButtonDisplayString(8)));

        buttonList.add(new GuiButton(14, XrightColumn, getYposForButton(-4), buttonWidth, ButtonsHeight, getButtonDisplayString(14)));
        buttonList.add(new GuiButton(9, XrightColumn, getYposForButton(-3), buttonWidth, ButtonsHeight, getButtonDisplayString(9)));
        buttonList.add(new GuiButton(15, XrightColumn, getYposForButton(-2), buttonWidth, ButtonsHeight, getButtonDisplayString(15)));
        buttonList.add(new GuiSlider(16, XrightColumn, getYposForButton(-1), buttonWidth, ButtonsHeight, "Range : ", " m", 0d, 64d, ConfigHandler.hitboxDrawRange, false, true, this));
        buttonList.add(new GuiButton(10, XrightColumn, getYposForButton(2), buttonWidth, ButtonsHeight, getButtonDisplayString(10)));
        buttonList.add(new GuiButton(11, XrightColumn, getYposForButton(3), buttonWidth, ButtonsHeight, getButtonDisplayString(11)));
        buttonList.add(new GuiButton(12, XrightColumn, getYposForButton(4), buttonWidth, ButtonsHeight, getButtonDisplayString(12)));

        buttonList.add(new GuiButton(0, getxCenter() - 150 / 2, getYposForButton(6), 150, ButtonsHeight, getButtonDisplayString(0)));
        super.initGui();
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 0:
                return parent == null ? "Close" : "Done";
            case 1:
                return "Players : " + getSuffix(ConfigHandler.drawHitboxForPlayers);
            case 2:
                return "Grounded arrows : " + getSuffix(ConfigHandler.drawHitboxForGroundedArrows);
            case 3:
                return "Pinned arrows : " + getSuffix(ConfigHandler.drawHitboxForPinnedArrows);
            case 13:
                return "Flying arrows : " + getSuffix(ConfigHandler.drawHitboxForFlyingArrows);
            case 4:
                return "Dropped items : " + getSuffix(ConfigHandler.drawHitboxForDroppedItems);
            case 5:
                return "Passive mobs : " + getSuffix(ConfigHandler.drawHitboxForPassiveMobs);
            case 6:
                return "Aggressive mobs : " + getSuffix(ConfigHandler.drawHitboxForAggressiveMobs);
            case 7:
                return "Item frame : " + getSuffix(ConfigHandler.drawHitboxItemFrame);
            case 8:
                return "Other entities : " + getSuffix(ConfigHandler.drawHitboxForOtherEntity);
            case 9:
                return "Red eyeline : " + getSuffix(ConfigHandler.drawRedBox);
            case 10:
                return "Draw blue vector : " + getSuffix(!ConfigHandler.HideBlueVect);
            case 11:
                return "For players only : " + getSuffix(ConfigHandler.drawBlueVectForPlayersOnly);
            case 12:
                return "Make vector 3 meters : " + getSuffix(ConfigHandler.makeBlueVect3Meters);
            case 14:
                return "Real size hitbox : " + getSuffix(ConfigHandler.realSizeHitbox);
            case 15:
                return "Hide close hitbox : " + getSuffix(ConfigHandler.drawRangedHitbox);
            default:
                return "";
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(parent);
                break;
            case 1:
                ConfigHandler.drawHitboxForPlayers = !ConfigHandler.drawHitboxForPlayers;
                break;
            case 2:
                ConfigHandler.drawHitboxForGroundedArrows = !ConfigHandler.drawHitboxForGroundedArrows;
                break;
            case 3:
                ConfigHandler.drawHitboxForPinnedArrows = !ConfigHandler.drawHitboxForPinnedArrows;
                break;
            case 13:
                ConfigHandler.drawHitboxForFlyingArrows = !ConfigHandler.drawHitboxForFlyingArrows;
                break;
            case 4:
                ConfigHandler.drawHitboxForDroppedItems = !ConfigHandler.drawHitboxForDroppedItems;
                break;
            case 5:
                ConfigHandler.drawHitboxForPassiveMobs = !ConfigHandler.drawHitboxForPassiveMobs;
                break;
            case 6:
                ConfigHandler.drawHitboxForAggressiveMobs = !ConfigHandler.drawHitboxForAggressiveMobs;
                break;
            case 7:
                ConfigHandler.drawHitboxItemFrame = !ConfigHandler.drawHitboxItemFrame;
                break;
            case 8:
                ConfigHandler.drawHitboxForOtherEntity = !ConfigHandler.drawHitboxForOtherEntity;
                break;
            case 9:
                ConfigHandler.drawRedBox = !ConfigHandler.drawRedBox;
                break;
            case 10:
                ConfigHandler.HideBlueVect = !ConfigHandler.HideBlueVect;
                break;
            case 11:
                ConfigHandler.drawBlueVectForPlayersOnly = !ConfigHandler.drawBlueVectForPlayersOnly;
                break;
            case 12:
                ConfigHandler.makeBlueVect3Meters = !ConfigHandler.makeBlueVect3Meters;
                break;
            case 14:
                ConfigHandler.realSizeHitbox = !ConfigHandler.realSizeHitbox;
                break;
            case 15:
                ConfigHandler.drawRangedHitbox = !ConfigHandler.drawRangedHitbox;
                break;
            default:
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 16) {
            ConfigHandler.hitboxDrawRange = (float) slider.getValue();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle("Hitboxes", 2, getxCenter(), getYposForButton(-8) + ButtonsHeight / 2.0f, Integer.parseInt("5555FF", 16));
        drawCenteredTitle("Draw Hitbox for :", 1, getxCenter() - buttonWidth / 2.0f - 10, getYposForButton(-4) - ButtonsHeight / 2.0f, Integer.parseInt("FFFFFF", 16));
        drawCenteredTitle("Blue vector :", 1, getxCenter() + buttonWidth / 2.0f + 10, getYposForButton(2) - ButtonsHeight / 2.0f, Integer.parseInt("0000FF", 16));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
