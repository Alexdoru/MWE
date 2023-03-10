package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.ColorSelectionScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;

public class HitboxConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    private final int buttonsWidth = 170;

    public HitboxConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.maxWidth = (10 + buttonsWidth) * 2;
        this.maxHeight = (buttonsHeight + 4) * 13 + buttonsHeight;
        super.initGui();
        final int xLeftColumn = getxCenter() - buttonsWidth - 10;
        final int xRightColumn = getxCenter() + 10;

        buttonList.add(new GuiButton(1, xLeftColumn, getButtonYPos(1), buttonsWidth, buttonsHeight, getButtonDisplayString(1))); //show hitboxes on players
        buttonList.add(new GuiButton(2, xLeftColumn, getButtonYPos(2), buttonsWidth, buttonsHeight, getButtonDisplayString(2))); //show hitboxes of grounded arrows
        buttonList.add(new GuiButton(3, xLeftColumn, getButtonYPos(3), buttonsWidth, buttonsHeight, getButtonDisplayString(3))); //show hitboxes of arrows in players
        buttonList.add(new GuiButton(13, xLeftColumn, getButtonYPos(4), buttonsWidth, buttonsHeight, getButtonDisplayString(13))); //show hitboxes of arrows in air
        buttonList.add(new GuiButton(4, xLeftColumn, getButtonYPos(5), buttonsWidth, buttonsHeight, getButtonDisplayString(4))); //show hitboxes of dropped items
        buttonList.add(new GuiButton(5, xLeftColumn, getButtonYPos(6), buttonsWidth, buttonsHeight, getButtonDisplayString(5))); //show hitboxes of passive mobs
        buttonList.add(new GuiButton(6, xLeftColumn, getButtonYPos(7), buttonsWidth, buttonsHeight, getButtonDisplayString(6))); //show hitboxes of hostile mobs
        buttonList.add(new GuiButton(7, xLeftColumn, getButtonYPos(8), buttonsWidth, buttonsHeight, getButtonDisplayString(7))); //show hitboxes of item frames
        buttonList.add(new GuiButton(8, xLeftColumn, getButtonYPos(9), buttonsWidth, buttonsHeight, getButtonDisplayString(8))); //show hitboxes of "other entities"

        buttonList.add(new GuiButton(17, xRightColumn, getButtonYPos(1), buttonsWidth, buttonsHeight, getButtonDisplayString(17))); //team color on hitboxes
        buttonList.add(new GuiButton(18, xRightColumn, getButtonYPos(2), buttonsWidth - 25, buttonsHeight, getButtonDisplayString(18))); //select custom hitbox color
        buttonList.add(new GuiButton(14, xRightColumn, getButtonYPos(3), buttonsWidth, buttonsHeight, getButtonDisplayString(14))); //real size hitboxes
        buttonList.add(new GuiButton(9, xRightColumn, getButtonYPos(4), buttonsWidth, buttonsHeight, getButtonDisplayString(9))); //red eyeline
        buttonList.add(new GuiButton(15, xRightColumn, getButtonYPos(5), buttonsWidth, buttonsHeight, getButtonDisplayString(15))); //hide close hitboxes
        buttonList.add(new GuiSlider(16, xRightColumn, getButtonYPos(6), buttonsWidth, buttonsHeight, "Range : ", " m", 0d, 64d, ConfigHandler.hitboxDrawRange, false, true, this)); //hitbox draw range slider
        buttonList.add(new GuiButton(10, xRightColumn, getButtonYPos(8), buttonsWidth, buttonsHeight, getButtonDisplayString(10))); //draw blue vector
        buttonList.add(new GuiButton(11, xRightColumn, getButtonYPos(9), buttonsWidth, buttonsHeight, getButtonDisplayString(11))); //draw blue vector on players only
        buttonList.add(new GuiButton(12, xRightColumn, getButtonYPos(10), buttonsWidth, buttonsHeight, getButtonDisplayString(12))); //make red eyeline 3m

        buttonList.add(new GuiButton(0, getxCenter() - 150 / 2, getButtonYPos(12), 150, buttonsHeight, getButtonDisplayString(0))); //close button
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle(EnumChatFormatting.BLUE + "Hitboxes", 2, getxCenter(), getButtonYPos(-1));
        final String msg = EnumChatFormatting.GRAY + "You obviously need to press f3+b to enable hitboxes";
        drawCenteredString(fontRendererObj, msg, getxCenter(), getButtonYPos(-1) + 2 * fontRendererObj.FONT_HEIGHT, 0);
        drawCenteredTitle(EnumChatFormatting.WHITE + "Draw Hitbox for :", 1, getxCenter() - buttonsWidth / 2.0f - 10, getButtonYPos(1) - buttonsHeight / 2.0f);
        drawCenteredTitle(EnumChatFormatting.BLUE + "Blue vector :", 1, getxCenter() + buttonsWidth / 2.0f + 10, getButtonYPos(8) - buttonsHeight / 2.0f);
        final int top = getButtonYPos(2);
        final int bottom = top + 20;
        final int left = getxCenter() + 10 + buttonsWidth - 25 + 4;
        final int right = left + bottom - top;
        drawColoredRectWithOutline(top, bottom, left, right, ConfigHandler.hitboxColor);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 0:
                return parent == null ? "Close" : "Done";
            case 1:
                return "Players : " + getSuffix(ConfigHandler.drawHitboxForPlayers);
            case 2:
                return "Arrows on ground : " + getSuffix(ConfigHandler.drawHitboxForGroundedArrows);
            case 3:
                return "Arrows in players : " + getSuffix(ConfigHandler.drawHitboxForPinnedArrows);
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
                return "Draw blue vector : " + getSuffix(!ConfigHandler.hideBlueVect);
            case 11:
                return "For players only : " + getSuffix(ConfigHandler.drawBlueVectForPlayersOnly);
            case 12:
                return "Make vector 3 meters : " + getSuffix(ConfigHandler.makeBlueVect3Meters);
            case 14:
                return "Real size hitbox : " + getSuffix(ConfigHandler.realSizeHitbox);
            case 15:
                return "Hide close hitbox : " + getSuffix(ConfigHandler.drawRangedHitbox);
            case 17:
                return "Team colored hitbox : " + getSuffix(ConfigHandler.teamColoredHitbox);
            case 18:
                return "Select custom hitbox color";
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
                ConfigHandler.hideBlueVect = !ConfigHandler.hideBlueVect;
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
            case 17:
                ConfigHandler.teamColoredHitbox = !ConfigHandler.teamColoredHitbox;
                break;
            case 18:
                mc.displayGuiScreen(new ColorSelectionScreen(this, ConfigHandler.hitboxColor, color -> ConfigHandler.hitboxColor = color));
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

}
