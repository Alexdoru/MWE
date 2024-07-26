package fr.alexdoru.mwe.gui.guiscreens;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.gui.elements.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import static net.minecraft.util.EnumChatFormatting.*;

public class HitboxConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    public HitboxConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final int buttonsWidth = 170;
        this.maxWidth = (10 + buttonsWidth) * 2;
        this.maxHeight = (buttonsHeight + 4) * 13 + buttonsHeight;
        super.initGui();
        final int xLeftCol = getxCenter() - buttonsWidth - 10;
        final int xRightCol = getxCenter() + 10;
        this.elementList.add(new TextElement(BLUE + "Hitboxes", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.elementList.add(new TextElement(GRAY + "You obviously need to press f3+b to enable hitboxes", getxCenter(), getButtonYPos(-1) + 2 * fontRendererObj.FONT_HEIGHT).makeCentered());
        this.elementList.add(new TextElement(WHITE + "Draw Hitbox for :", getxCenter() - buttonsWidth / 2 - 10, getButtonYPos(1) - buttonsHeight / 2).makeCentered());
        this.elementList.add(new ColoredSquareElement(xRightCol + buttonsWidth - 25 + 4, getButtonYPos(3), 20, () -> MWEConfig.hitboxColor));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(1),
                buttonsWidth, 20,
                "Players",
                (b) -> MWEConfig.drawHitboxForPlayers = b,
                () -> MWEConfig.drawHitboxForPlayers));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(2),
                buttonsWidth, 20,
                "Arrows on ground",
                (b) -> MWEConfig.drawHitboxForGroundedArrows = b,
                () -> MWEConfig.drawHitboxForGroundedArrows));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(3),
                buttonsWidth, 20,
                "Arrows in players",
                (b) -> MWEConfig.drawHitboxForPinnedArrows = b,
                () -> MWEConfig.drawHitboxForPinnedArrows));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(4),
                buttonsWidth, 20,
                "Flying arrows",
                (b) -> MWEConfig.drawHitboxForFlyingArrows = b,
                () -> MWEConfig.drawHitboxForFlyingArrows));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(5),
                buttonsWidth, 20,
                "Dropped items",
                (b) -> MWEConfig.drawHitboxForDroppedItems = b,
                () -> MWEConfig.drawHitboxForDroppedItems));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(6),
                buttonsWidth, 20,
                "Passive mobs",
                (b) -> MWEConfig.drawHitboxForPassiveMobs = b,
                () -> MWEConfig.drawHitboxForPassiveMobs));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(7),
                buttonsWidth, 20,
                "Aggressive mobs",
                (b) -> MWEConfig.drawHitboxForAggressiveMobs = b,
                () -> MWEConfig.drawHitboxForAggressiveMobs));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(8),
                buttonsWidth, 20,
                "Withers",
                (b) -> MWEConfig.drawHitboxForWithers = b,
                () -> MWEConfig.drawHitboxForWithers));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(9),
                buttonsWidth, 20,
                "Item frame",
                (b) -> MWEConfig.drawHitboxItemFrame = b,
                () -> MWEConfig.drawHitboxItemFrame));
        this.buttonList.add(new OptionGuiButton(
                xLeftCol, getButtonYPos(10),
                buttonsWidth, 20,
                "Other entities",
                (b) -> MWEConfig.drawHitboxForOtherEntity = b,
                () -> MWEConfig.drawHitboxForOtherEntity));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(1),
                buttonsWidth, 20,
                "Team colored",
                (b) -> MWEConfig.teamColoredPlayerHitbox = b,
                () -> MWEConfig.teamColoredPlayerHitbox,
                GRAY + "For players, the hitbox will take the color of the team associated team"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(2),
                buttonsWidth, 20,
                "Team colored arrows",
                (b) -> MWEConfig.teamColoredArrowHitbox = b,
                () -> MWEConfig.teamColoredArrowHitbox,
                GRAY + "For arrows, the hitbox will take the color of the shooter's team"));
        this.buttonList.add(new FancyGuiButton(
                xRightCol, getButtonYPos(3),
                buttonsWidth - 25, buttonsHeight,
                () -> "Select custom hitbox color",
                () -> mc.displayGuiScreen(new ColorSelectionGuiScreen(this, MWEConfig.hitboxColor, 0xFFFFFF, color -> MWEConfig.hitboxColor = color)),
                GREEN + "Custom hitbox color",
                GRAY + "Change the color of the entity hitboxes"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(4),
                buttonsWidth, 20,
                "Real size hitbox",
                (b) -> MWEConfig.realSizeHitbox = b,
                () -> MWEConfig.realSizeHitbox,
                GRAY + "The hitbox will be larger and represent the area where you can attack entities"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(5),
                buttonsWidth, 20,
                "Red eyeline",
                (b) -> MWEConfig.drawRedBox = b,
                () -> MWEConfig.drawRedBox,
                GRAY + "Draw a red square at the eye level of entities"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(6),
                buttonsWidth, 20,
                "Draw blue vector",
                (b) -> MWEConfig.drawBlueVect = b,
                () -> MWEConfig.drawBlueVect,
                GRAY + "Draw a blue line comming out of the eyes of entities that represent where they look at"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(7),
                buttonsWidth, 20,
                "For players only",
                (b) -> MWEConfig.drawBlueVectForPlayersOnly = b,
                () -> MWEConfig.drawBlueVectForPlayersOnly,
                GRAY + "Draw blue vector for players only"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(8),
                buttonsWidth, 20,
                "Make vector 3 meters",
                (b) -> MWEConfig.makeBlueVect3Meters = b,
                () -> MWEConfig.makeBlueVect3Meters,
                GRAY + "Make the blue vector 3 meters long, just like the player's attack reach"));
        this.buttonList.add(new OptionGuiButton(
                xRightCol, getButtonYPos(9),
                buttonsWidth, 20,
                "Hide close hitbox",
                (b) -> MWEConfig.hideCloseHitbox = b,
                () -> MWEConfig.hideCloseHitbox,
                GRAY + "Stops rendering the hitboxes that are closer than the range set below"));
        this.buttonList.add(new GuiSlider(16, xRightCol, getButtonYPos(10), buttonsWidth, buttonsHeight, "Range : ", " m", 0d, 64d, MWEConfig.hitboxDrawRange, false, true, this)); //hitbox draw range slider
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(12), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 16) {
            MWEConfig.hitboxDrawRange = slider.getValue();
        }
    }

}
