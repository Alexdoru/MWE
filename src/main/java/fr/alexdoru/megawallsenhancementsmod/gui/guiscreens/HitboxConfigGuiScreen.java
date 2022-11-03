package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
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
        this.maxHeight = (buttonsHeight + 4) * 12 + buttonsHeight;
        super.initGui();
        final int XleftColumn = getxCenter() - buttonsWidth - 10;
        final int XrightColumn = getxCenter() + 10;

        buttonList.add(new GuiButton(1, XleftColumn, getButtonYPos(1), buttonsWidth, buttonsHeight, getButtonDisplayString(1)));
        buttonList.add(new GuiButton(2, XleftColumn, getButtonYPos(2), buttonsWidth, buttonsHeight, getButtonDisplayString(2)));
        buttonList.add(new GuiButton(3, XleftColumn, getButtonYPos(3), buttonsWidth, buttonsHeight, getButtonDisplayString(3)));
        buttonList.add(new GuiButton(13, XleftColumn, getButtonYPos(4), buttonsWidth, buttonsHeight, getButtonDisplayString(13)));
        buttonList.add(new GuiButton(4, XleftColumn, getButtonYPos(5), buttonsWidth, buttonsHeight, getButtonDisplayString(4)));
        buttonList.add(new GuiButton(5, XleftColumn, getButtonYPos(6), buttonsWidth, buttonsHeight, getButtonDisplayString(5)));
        buttonList.add(new GuiButton(6, XleftColumn, getButtonYPos(7), buttonsWidth, buttonsHeight, getButtonDisplayString(6)));
        buttonList.add(new GuiButton(7, XleftColumn, getButtonYPos(8), buttonsWidth, buttonsHeight, getButtonDisplayString(7)));
        buttonList.add(new GuiButton(8, XleftColumn, getButtonYPos(9), buttonsWidth, buttonsHeight, getButtonDisplayString(8)));

        buttonList.add(new GuiButton(14, XrightColumn, getButtonYPos(1), buttonsWidth, buttonsHeight, getButtonDisplayString(14)));
        buttonList.add(new GuiButton(9, XrightColumn, getButtonYPos(2), buttonsWidth, buttonsHeight, getButtonDisplayString(9)));
        buttonList.add(new GuiButton(15, XrightColumn, getButtonYPos(3), buttonsWidth, buttonsHeight, getButtonDisplayString(15)));
        buttonList.add(new GuiSlider(16, XrightColumn, getButtonYPos(4), buttonsWidth, buttonsHeight, "Range : ", " m", 0d, 64d, ConfigHandler.hitboxDrawRange, false, true, this));
        buttonList.add(new GuiButton(10, XrightColumn, getButtonYPos(7), buttonsWidth, buttonsHeight, getButtonDisplayString(10)));
        buttonList.add(new GuiButton(11, XrightColumn, getButtonYPos(8), buttonsWidth, buttonsHeight, getButtonDisplayString(11)));
        buttonList.add(new GuiButton(12, XrightColumn, getButtonYPos(7), buttonsWidth, buttonsHeight, getButtonDisplayString(12)));

        buttonList.add(new GuiButton(0, getxCenter() - 150 / 2, getButtonYPos(11), 150, buttonsHeight, getButtonDisplayString(0)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle(EnumChatFormatting.BLUE + "Hitboxes", 2, getxCenter(), getButtonYPos(-1));
        final String msg = EnumChatFormatting.GRAY + "You obviously need to press f3+b to enable hitboxes";
        drawCenteredString(fontRendererObj, msg, getxCenter(), getButtonYPos(-1) + 2 * fontRendererObj.FONT_HEIGHT, 0);
        drawCenteredTitle(EnumChatFormatting.WHITE + "Draw Hitbox for :", 1, getxCenter() - buttonsWidth / 2.0f - 10, getButtonYPos(1) - buttonsHeight / 2.0f);
        drawCenteredTitle(EnumChatFormatting.BLUE + "Blue vector :", 1, getxCenter() + buttonsWidth / 2.0f + 10, getButtonYPos(7) - buttonsHeight / 2.0f);
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
