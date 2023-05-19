package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

public class HitboxConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    public HitboxConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final int buttonsWidth = 170;
        this.maxWidth = (10 + buttonsWidth) * 2;
        this.maxHeight = (buttonsHeight + 4) * 12 + buttonsHeight;
        super.initGui();
        final int xLeftCol = getxCenter() - buttonsWidth - 10;
        final int xRightCol = getxCenter() + 10;
        this.elementList.add(new TextElement(EnumChatFormatting.BLUE + "Hitboxes", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.elementList.add(new TextElement(EnumChatFormatting.GRAY + "You obviously need to press f3+b to enable hitboxes", getxCenter(), getButtonYPos(-1) + 2 * fontRendererObj.FONT_HEIGHT).makeCentered());
        this.elementList.add(new TextElement(EnumChatFormatting.WHITE + "Draw Hitbox for :", getxCenter() - buttonsWidth / 2 - 10, getButtonYPos(1) - buttonsHeight / 2).makeCentered());
        this.elementList.add(new ColoredSquareElement(xRightCol + buttonsWidth - 25 + 4, getButtonYPos(2), 20, () -> ConfigHandler.hitboxColor));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(1),
                buttonsWidth, 20,
                "Players",
                (b) -> ConfigHandler.drawHitboxForPlayers = b,
                () -> ConfigHandler.drawHitboxForPlayers));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(2),
                buttonsWidth, 20,
                "Arrows on ground",
                (b) -> ConfigHandler.drawHitboxForGroundedArrows = b,
                () -> ConfigHandler.drawHitboxForGroundedArrows));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(3),
                buttonsWidth, 20,
                "Arrows in players",
                (b) -> ConfigHandler.drawHitboxForPinnedArrows = b,
                () -> ConfigHandler.drawHitboxForPinnedArrows));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(4),
                buttonsWidth, 20,
                "Flying arrows",
                (b) -> ConfigHandler.drawHitboxForFlyingArrows = b,
                () -> ConfigHandler.drawHitboxForFlyingArrows));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(5),
                buttonsWidth, 20,
                "Dropped items",
                (b) -> ConfigHandler.drawHitboxForDroppedItems = b,
                () -> ConfigHandler.drawHitboxForDroppedItems));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(6),
                buttonsWidth, 20,
                "Passive mobs",
                (b) -> ConfigHandler.drawHitboxForPassiveMobs = b,
                () -> ConfigHandler.drawHitboxForPassiveMobs));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(7),
                buttonsWidth, 20,
                "Aggressive mobs",
                (b) -> ConfigHandler.drawHitboxForAggressiveMobs = b,
                () -> ConfigHandler.drawHitboxForAggressiveMobs));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(8),
                buttonsWidth, 20,
                "Item frame",
                (b) -> ConfigHandler.drawHitboxItemFrame = b,
                () -> ConfigHandler.drawHitboxItemFrame));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(9),
                buttonsWidth, 20,
                "Other entities",
                (b) -> ConfigHandler.drawHitboxForOtherEntity = b,
                () -> ConfigHandler.drawHitboxForOtherEntity));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(1),
                buttonsWidth, 20,
                "Team colored hitbox",
                (b) -> ConfigHandler.teamColoredHitbox = b,
                () -> ConfigHandler.teamColoredHitbox,
                EnumChatFormatting.GRAY + "For players, the hitbox will take the color of the player's team"));
        this.buttonList.add(new FancyGuiButton(
                xRightCol, getButtonYPos(2),
                buttonsWidth - 25, buttonsHeight,
                () -> "Select custom hitbox color",
                () -> mc.displayGuiScreen(new ColorSelectionGuiScreen(this, ConfigHandler.hitboxColor, 0xFFFFFF, color -> ConfigHandler.hitboxColor = color)),
                EnumChatFormatting.GREEN + "Custom hitbox color",
                EnumChatFormatting.GRAY + "Change the color of the entity hitboxes"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(3),
                buttonsWidth, 20,
                "Real size hitbox",
                (b) -> ConfigHandler.realSizeHitbox = b,
                () -> ConfigHandler.realSizeHitbox,
                EnumChatFormatting.GRAY + "The hitbox will be larger and represent the area where you can attack entities"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(4),
                buttonsWidth, 20,
                "Red eyeline",
                (b) -> ConfigHandler.drawRedBox = b,
                () -> ConfigHandler.drawRedBox,
                EnumChatFormatting.GRAY + "Draw a red square at the eye level of entities"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(5),
                buttonsWidth, 20,
                "Draw blue vector",
                (b) -> ConfigHandler.drawBlueVect = b,
                () -> ConfigHandler.drawBlueVect,
                EnumChatFormatting.GRAY + "Draw a blue line comming out of the eyes of entities that represent where they look at"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(6),
                buttonsWidth, 20,
                "For players only",
                (b) -> ConfigHandler.drawBlueVectForPlayersOnly = b,
                () -> ConfigHandler.drawBlueVectForPlayersOnly,
                EnumChatFormatting.GRAY + "Draw blue vector for players only"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(7),
                buttonsWidth, 20,
                "Make vector 3 meters",
                (b) -> ConfigHandler.makeBlueVect3Meters = b,
                () -> ConfigHandler.makeBlueVect3Meters,
                EnumChatFormatting.GRAY + "Make the blue vector 3 meters long, just like the player's attack reach"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(8),
                buttonsWidth, 20,
                "Hide close hitbox",
                (b) -> ConfigHandler.hideCloseHitbox = b,
                () -> ConfigHandler.hideCloseHitbox,
                EnumChatFormatting.GRAY + "Stops rendering the hitboxes that are closer than the range set below"));
        this.buttonList.add(new GuiSlider(16, xRightCol, getButtonYPos(9), buttonsWidth, buttonsHeight, "Range : ", " m", 0d, 64d, ConfigHandler.hitboxDrawRange, false, true, this)); //hitbox draw range slider
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(11), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 16) {
            ConfigHandler.hitboxDrawRange = slider.getValue();
        }
    }

}
