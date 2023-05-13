package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.FancyGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.OptionGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.ArrayList;
import java.util.List;

public class VanillaEnhancementsConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    private final GuiScreen parent;

    public VanillaEnhancementsConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final boolean isPatcherNotLoaded = !ASMLoadingPlugin.isPatcherLoaded();
        this.maxWidth = (10 + BUTTON_WIDTH) * 2;
        this.maxHeight = (buttonsHeight + 4) * 10 + buttonsHeight;
        super.initGui();
        final int xPosLeft = getxCenter() - BUTTON_WIDTH - 10;
        final int xPosRight = getxCenter() + 10;
        this.elementList.add(new TextElement(EnumChatFormatting.GOLD + "Vanilla Enhancements", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.buttonList.add(new FancyGuiButton(
                xPosLeft, getButtonYPos(1),
                () -> "Cancel night vision effect : " + getSuffix(!ConfigHandler.cancelNightVisionEffect),
                () -> ConfigHandler.cancelNightVisionEffect = !ConfigHandler.cancelNightVisionEffect,
                EnumChatFormatting.GREEN + "Cancel night vision effect",
                EnumChatFormatting.GRAY + "Removes the visual effect of night vision"));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(2),
                "Clear view",
                (b) -> ConfigHandler.clearVision = b,
                () -> ConfigHandler.clearVision,
                EnumChatFormatting.GRAY + "Stops rendering particles that are too close (75cm) to the camera for a better visibility"));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(3),
                "Fix actionbar text overlap",
                (b) -> ConfigHandler.fixActionbarTextOverlap = b,
                () -> ConfigHandler.fixActionbarTextOverlap,
                EnumChatFormatting.GRAY + "Prevents the actionbar text from overlapping with the armor bar if the player has more that 2 rows of health"));
        this.buttonList.add(new FancyGuiButton(
                xPosLeft, getButtonYPos(4),
                () -> "Sound warning when low HP : " + getSuffix(ConfigHandler.playSoundLowHP),
                () -> {
                    ConfigHandler.playSoundLowHP = !ConfigHandler.playSoundLowHP;
                    if (ConfigHandler.playSoundLowHP) {
                        SoundUtil.playLowHPSound();
                    }
                }, EnumChatFormatting.GREEN + "Sound warning when low HP",
                EnumChatFormatting.GRAY + "Plays a sound when your health drops below the threshold defined below",
                EnumChatFormatting.GRAY + "The sound used is \"note.pling\" check your sound settings to see if it's enabled !"));
        this.buttonList.add(new GuiSlider(20, xPosLeft, getButtonYPos(5), BUTTON_WIDTH, buttonsHeight, "Health threshold : ", " %", 0d, 100d, ConfigHandler.healthThreshold * 100d, false, true, this));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(6),
                "Limit dropped item rendered",
                (b) -> ConfigHandler.limitDroppedEntityRendered = b,
                () -> ConfigHandler.limitDroppedEntityRendered,
                EnumChatFormatting.GRAY + "Dynamically modifies the render distance for dropped items entities to preserve performance. It starts reducing the render distance when exceeding the threshold set below.",
                EnumChatFormatting.GRAY + "There is a keybind (ESC -> options -> controls -> MegaWallsEnhancements) to toggle it on the fly"));
        this.buttonList.add(new GuiSlider(23, xPosLeft, getButtonYPos(7), BUTTON_WIDTH, buttonsHeight, "Maximum dropped item entities : ", "", 40d, 400d, ConfigHandler.maxDroppedEntityRendered, false, true, this));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(1),
                "Safe inventory",
                (b) -> ConfigHandler.safeInventory = b,
                () -> ConfigHandler.safeInventory,
                EnumChatFormatting.GRAY + "Prevents dropping the sword you are holding in your hotbar. Prevents hotkeying important kit items out of your inventory (only in Mega Walls)."));
        final List<String> shortCoinsList = new ArrayList<>();
        shortCoinsList.add(EnumChatFormatting.GREEN + "Short coin messages");
        shortCoinsList.add("");
        shortCoinsList.add(EnumChatFormatting.GRAY + "Makes the coin messages shorter by removing the network booster info.");
        shortCoinsList.add(EnumChatFormatting.GRAY + "And also compacts the guild bonus message and coin message into one.");
        shortCoinsList.add(EnumChatFormatting.GRAY + "It makes the assists messages in mega walls fit on one line instead of two.");
        shortCoinsList.add("");
        shortCoinsList.add(EnumChatFormatting.GOLD + "+100 coins! (hypixel's Network booster)" + EnumChatFormatting.AQUA + " FINAL KILL");
        shortCoinsList.add("Will become : ");
        shortCoinsList.add(EnumChatFormatting.GOLD + "+100 coins!" + EnumChatFormatting.AQUA + " FINAL KILL");
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(2),
                "Short coin messages",
                (b) -> ConfigHandler.shortCoinMessage = b,
                () -> ConfigHandler.shortCoinMessage,
                shortCoinsList));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(3),
                "Show playercount in tab",
                (b) -> ConfigHandler.showPlayercountTablist = b,
                () -> ConfigHandler.showPlayercountTablist,
                EnumChatFormatting.GRAY + "Displays the amount of players in the lobby at the top of the tablist"));
        this.buttonList.add(new FancyGuiButton(
                xPosRight, getButtonYPos(4),
                () -> {
                    if (ConfigHandler.hideTablistHeaderFooter && !ConfigHandler.showHeaderFooterOutsideMW) {
                        return "Hide header/footer in tab : " + EnumChatFormatting.GREEN + "Enabled";
                    } else if (ConfigHandler.hideTablistHeaderFooter /*&& ConfigHandler.showHeaderFooterOutsideMW*/) {
                        return "Hide header/footer in tab : " + EnumChatFormatting.GREEN + "Only in MW";
                    } else {
                        return "Hide header/footer in tab : " + EnumChatFormatting.RED + "Disabled";
                    }
                },
                () -> {
                    if (ConfigHandler.hideTablistHeaderFooter && !ConfigHandler.showHeaderFooterOutsideMW) {
                        ConfigHandler.showHeaderFooterOutsideMW = true;
                    } else if (ConfigHandler.hideTablistHeaderFooter /*&& ConfigHandler.showHeaderFooterOutsideMW*/) {
                        ConfigHandler.hideTablistHeaderFooter = false;
                        ConfigHandler.showHeaderFooterOutsideMW = false;
                    } else {
                        ConfigHandler.hideTablistHeaderFooter = true;
                        ConfigHandler.showHeaderFooterOutsideMW = false;
                    }
                },
                EnumChatFormatting.GREEN + "Hide header/footer in tab",
                EnumChatFormatting.DARK_GRAY + "\u25AA " + EnumChatFormatting.GREEN + "Enabled" + EnumChatFormatting.GRAY + " : will hide the text at top and bottom of the tablist",
                EnumChatFormatting.DARK_GRAY + "\u25AA " + EnumChatFormatting.GREEN + "Only in MW" + EnumChatFormatting.GRAY + " : will hide the text at top and bottom of the tablist only in Mega Walls since that text can contain usefull information in other games such as Bedwars or Skyblock",
                EnumChatFormatting.DARK_GRAY + "\u25AA " + EnumChatFormatting.RED + "Disabled" + EnumChatFormatting.GRAY + " : will always render the text at top and bottom of the tablist"));
        final List<String> coloredScoresList = new ArrayList<>();
        coloredScoresList.add(EnumChatFormatting.GREEN + "Colored scores in tab");
        coloredScoresList.add("");
        coloredScoresList.add(EnumChatFormatting.GRAY + "Adds colors to the scores/health in the tablist depending on the value");
        coloredScoresList.add("");
        coloredScoresList.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM] " + EnumChatFormatting.DARK_GREEN + "44");
        coloredScoresList.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM] " + EnumChatFormatting.GREEN + "35");
        coloredScoresList.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM] " + EnumChatFormatting.YELLOW + "25");
        coloredScoresList.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM] " + EnumChatFormatting.RED + "15");
        coloredScoresList.add(EnumChatFormatting.RED + "OrangeMarshall " + EnumChatFormatting.GRAY + "[ZOM]  " + EnumChatFormatting.DARK_RED + "5");
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(5),
                "Colored scores in tab",
                (b) -> ConfigHandler.useColoredScores = b,
                () -> ConfigHandler.useColoredScores,
                coloredScoresList));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(6),
                "Hide ping in tab",
                (b) -> ConfigHandler.hidePingTablist = b,
                () -> ConfigHandler.hidePingTablist,
                EnumChatFormatting.GRAY + "Stops rendering the ping in the tablist when all values are equal to 1"));
        if (isPatcherNotLoaded) {
            buttonList.add(new GuiSlider(30, xPosRight, getButtonYPos(7), BUTTON_WIDTH, buttonsHeight, "Tablist size : ", " players", 50d, 125d, ConfigHandler.tablistSize, false, true, this));
        }
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(9), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        switch (slider.id) {
            case 20:
                ConfigHandler.healthThreshold = Math.floor(slider.getValue()) / 100d;
                break;
            case 23:
                ConfigHandler.maxDroppedEntityRendered = (int) slider.getValue();
                break;
            case 30:
                ConfigHandler.tablistSize = (int) slider.getValue();
                break;
        }
    }

}
