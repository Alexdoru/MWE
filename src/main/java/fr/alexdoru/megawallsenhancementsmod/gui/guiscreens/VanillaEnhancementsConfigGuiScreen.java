package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.*;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

public class VanillaEnhancementsConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    private final GuiScreen parent;

    public VanillaEnhancementsConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final boolean isPatcherNotLoaded = !ASMLoadingPlugin.isPatcherLoaded();
        final boolean isOrangeSimpleModLoaded = Loader.isModLoaded("orangesimplemod") || !ASMLoadingPlugin.isObf();
        final boolean hasOptifine = FMLClientHandler.instance().hasOptifine() || !ASMLoadingPlugin.isObf();
        final int buttonWidth = 210;
        this.maxWidth = (10 + buttonWidth) * 2;
        this.maxHeight = (buttonsHeight + 4) * 12 + buttonsHeight;
        super.initGui();
        final int xPosLeft = getxCenter() - buttonWidth - 10;
        final int xPosRight = getxCenter() + 10;
        this.elementList.add(new TextElement(GOLD + "Vanilla Enhancements", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.buttonList.add(new FancyGuiButton(
                xPosLeft, getButtonYPos(1),
                buttonWidth, buttonsHeight,
                () -> "Cancel night vision effect : " + getSuffix(!ConfigHandler.cancelNightVisionEffect),
                () -> ConfigHandler.cancelNightVisionEffect = !ConfigHandler.cancelNightVisionEffect,
                GREEN + "Cancel night vision effect",
                GRAY + "Removes the visual effect of night vision"));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(2),
                buttonWidth, buttonsHeight,
                "Clear view",
                (b) -> ConfigHandler.clearVision = b,
                () -> ConfigHandler.clearVision,
                GRAY + "Stops rendering particles that are too close (75cm) to the camera for a better visibility"));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(3),
                buttonWidth, buttonsHeight,
                "Fix actionbar text overlap",
                (b) -> ConfigHandler.fixActionbarTextOverlap = b,
                () -> ConfigHandler.fixActionbarTextOverlap,
                GRAY + "Prevents the actionbar text from overlapping with the armor bar if the player has more than 2 rows of health"));
        this.buttonList.add(new FancyGuiButton(
                xPosLeft, getButtonYPos(4),
                buttonWidth, buttonsHeight,
                () -> "Sound warning when low HP : " + getSuffix(ConfigHandler.playSoundLowHP),
                () -> {
                    ConfigHandler.playSoundLowHP = !ConfigHandler.playSoundLowHP;
                    if (ConfigHandler.playSoundLowHP) {
                        SoundUtil.playLowHPSound();
                    }
                }, GREEN + "Sound warning when low HP",
                GRAY + "Plays a sound when your health drops below the threshold defined below",
                GRAY + "The sound used is \"note.pling\" check your sound settings to see if it's enabled !"));
        this.buttonList.add(new GuiSlider(20, xPosLeft, getButtonYPos(5), buttonWidth, buttonsHeight, "Health threshold : ", " %", 0d, 100d, ConfigHandler.healthThreshold * 100d, false, true, this));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(6),
                buttonWidth, buttonsHeight,
                "Limit dropped item rendered",
                (b) -> ConfigHandler.limitDroppedEntityRendered = b,
                () -> ConfigHandler.limitDroppedEntityRendered,
                GRAY + "Dynamically modifies the render distance of dropped items entities to preserve performance. It starts reducing the render distance when exceeding the threshold set below.",
                GRAY + "There is a keybind (ESC -> options -> controls -> MegaWallsEnhancements) to toggle it on the fly"));
        this.buttonList.add(new GuiSlider(23, xPosLeft, getButtonYPos(7), buttonWidth, buttonsHeight, "Maximum dropped item entities : ", "", 40d, 400d, ConfigHandler.maxDroppedEntityRendered, false, true, this));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(8),
                buttonWidth, buttonsHeight,
                "Safe inventory",
                (b) -> ConfigHandler.safeInventory = b,
                () -> ConfigHandler.safeInventory,
                GRAY + "Prevents dropping the sword you are holding in your hotbar. Prevents hotkeying important kit items out of your inventory (in Mega Walls)."));
        if (isOrangeSimpleModLoaded) {
            this.buttonList.add(new OptionGuiButton(
                    xPosLeft, getButtonYPos(9),
                    buttonWidth, buttonsHeight,
                    "Hide Toggle Sprint HUD",
                    (b) -> ConfigHandler.hideToggleSprintText = b,
                    () -> ConfigHandler.hideToggleSprintText,
                    GRAY + "Hides the Toggle Sprint HUD from Orange's Marshall Simple Mod"));
        }
        if (hasOptifine) {
            this.buttonList.add(new OptionGuiButton(
                    xPosLeft, getButtonYPos(isOrangeSimpleModLoaded ? 10 : 9),
                    buttonWidth, buttonsHeight,
                    "Hide Optifine hats",
                    (b) -> ConfigHandler.hideOptifineHats = b,
                    () -> ConfigHandler.hideOptifineHats,
                    GRAY + "Hides the hats added by Optifine during Halloween and Christmas",
                    DARK_RED + "Requires game restart to be fully effective"));
        }
        this.buttonList.add(new FancyGuiButton(
                xPosRight, getButtonYPos(1),
                buttonWidth - 25, buttonsHeight,
                () -> "Select custom hurt color",
                () -> mc.displayGuiScreen(new ColorSelectionGuiScreen(this, ConfigHandler.hitColor, 0x4CFF0000, color -> ConfigHandler.hitColor = color)),
                GREEN + "Custom hurt color",
                GRAY + "Change the color entities take when they get hurt"));
        this.elementList.add(new ColoredSquareElement(xPosRight + buttonWidth - 25 + 4, getButtonYPos(1), 20, () -> ConfigHandler.hitColor));
        this.buttonList.add(new FancyGuiButton(
                xPosRight, getButtonYPos(2),
                buttonWidth / 2 - 2, buttonsHeight,
                () -> "Color armor : " + (ConfigHandler.colorArmorWhenHurt ? GREEN + "On" : RED + "Off"),
                () -> ConfigHandler.colorArmorWhenHurt = !ConfigHandler.colorArmorWhenHurt,
                GREEN + "Color armor",
                GRAY + "The armor will be colored as well when a player is hurt, like it does in 1.7.",
                GRAY + "If you have a 1.7 Old animation mod (such as " + YELLOW + "Sk1er's 1.7 Old Animation mod" + GRAY + "), you might need to turn off their \"Red Armor\" setting for this one to take effect."));
        this.buttonList.add(new FancyGuiButton(
                xPosRight + buttonWidth / 2 + 2, getButtonYPos(2),
                buttonWidth / 2 - 2, buttonsHeight,
                () -> "Use team color : " + (ConfigHandler.useTeamColorWhenHurt ? GREEN + "On" : RED + "Off"),
                () -> ConfigHandler.useTeamColorWhenHurt = !ConfigHandler.useTeamColorWhenHurt,
                GREEN + "Use team color",
                GRAY + "When hurt the players will take the color of their team, other entities will take the custom color defined above.",
                YELLOW + "When this is enabled, it still uses the alpha level defined in the custom color."));
        final List<String> shortCoinsList = new ArrayList<>();
        shortCoinsList.add(GREEN + "Short coin messages");
        shortCoinsList.add("");
        shortCoinsList.add(GRAY + "Makes the coins & tokens messages shorter by removing the network booster info. It also compacts the guild bonus message and coin message into one.");
        shortCoinsList.add("");
        shortCoinsList.add(GOLD + "+100 coins! (hypixel's Network booster)" + AQUA + " FINAL KILL");
        shortCoinsList.add(WHITE + "Will become : ");
        shortCoinsList.add(GOLD + "+100 coins!" + AQUA + " FINAL KILL");
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(3),
                buttonWidth, buttonsHeight,
                "Short coin messages",
                (b) -> ConfigHandler.shortCoinMessage = b,
                () -> ConfigHandler.shortCoinMessage,
                shortCoinsList));
        final List<String> coloredScoresHead = new ArrayList<>();
        coloredScoresHead.add(GREEN + "Colored scores above head");
        coloredScoresHead.add("");
        coloredScoresHead.add(GRAY + "Renders the scores/health in color, according to the score value and the player's maximum health points");
        coloredScoresHead.add("");
        coloredScoresHead.add(DARK_GREEN + "44" + RED + " ❤");
        coloredScoresHead.add(GREEN + "35" + RED + " ❤");
        coloredScoresHead.add(YELLOW + "25" + RED + " ❤");
        coloredScoresHead.add(RED + "15" + RED + " ❤");
        coloredScoresHead.add(DARK_RED + "5" + RED + " ❤");
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(4),
                buttonWidth, buttonsHeight,
                "Colored scores above head",
                (b) -> ConfigHandler.coloredScoreAboveHead = b,
                () -> ConfigHandler.coloredScoreAboveHead,
                coloredScoresHead));
        final List<String> coloredScoresTabList = new ArrayList<>();
        coloredScoresTabList.add(GREEN + "Colored scores in tab");
        coloredScoresTabList.add("");
        coloredScoresTabList.add(GRAY + "Renders the scores/health in color, according to the score value and the player's maximum health points");
        coloredScoresTabList.add("");
        coloredScoresTabList.add(RED + "OrangeMarshall " + GRAY + "[ZOM] " + DARK_GREEN + "44");
        coloredScoresTabList.add(RED + "OrangeMarshall " + GRAY + "[ZOM] " + GREEN + "35");
        coloredScoresTabList.add(RED + "OrangeMarshall " + GRAY + "[ZOM] " + YELLOW + "25");
        coloredScoresTabList.add(RED + "OrangeMarshall " + GRAY + "[ZOM] " + RED + "15");
        coloredScoresTabList.add(RED + "OrangeMarshall " + GRAY + "[ZOM]  " + DARK_RED + "5");
        coloredScoresTabList.add("");
        coloredScoresTabList.add(RED + "This will not work with certain mods such as Orange Marshall's Vanilla Enhancements");
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(5),
                buttonWidth, buttonsHeight,
                "Colored scores in tab",
                (b) -> ConfigHandler.useColoredScores = b,
                () -> ConfigHandler.useColoredScores,
                coloredScoresTabList));
        this.buttonList.add(new FancyGuiButton(
                xPosRight, getButtonYPos(6),
                buttonWidth, buttonsHeight,
                () -> {
                    if (ConfigHandler.hideTablistHeaderFooter && !ConfigHandler.showHeaderFooterOutsideMW) {
                        return "Hide header/footer in tab : " + GREEN + "Enabled";
                    } else if (ConfigHandler.hideTablistHeaderFooter /*&& ConfigHandler.showHeaderFooterOutsideMW*/) {
                        return "Hide header/footer in tab : " + GREEN + "Only in MW";
                    } else {
                        return "Hide header/footer in tab : " + RED + "Disabled";
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
                GREEN + "Hide header/footer in tab",
                DARK_GRAY + "▪ " + GREEN + "Enabled" + GRAY + " : will hide the text at top and bottom of the tablist",
                DARK_GRAY + "▪ " + GREEN + "Only in MW" + GRAY + " : will hide the text at top and bottom of the tablist only in Mega Walls since that text can contain usefull information in other games such as Bedwars or Skyblock",
                DARK_GRAY + "▪ " + RED + "Disabled" + GRAY + " : will always render the text at top and bottom of the tablist",
                RED + "This will not work with certain mods such as Orange Marshall's Vanilla Enhancements"));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(7),
                buttonWidth, buttonsHeight,
                "Show playercount in tab",
                (b) -> ConfigHandler.showPlayercountTablist = b,
                () -> ConfigHandler.showPlayercountTablist,
                GRAY + "Displays the amount of players in the lobby at the top of the tablist",
                RED + "This will not work with certain mods such as Orange Marshall's Vanilla Enhancements"));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(8),
                buttonWidth, buttonsHeight,
                "Hide ping in tab",
                (b) -> ConfigHandler.hidePingTablist = b,
                () -> ConfigHandler.hidePingTablist,
                GRAY + "Stops rendering the ping in the tablist when all values are equal to 1",
                RED + "This will not work with certain mods such as Orange Marshall's Vanilla Enhancements"));
        if (isPatcherNotLoaded) {
            buttonList.add(new GuiSlider(30, xPosRight, getButtonYPos(9), buttonWidth, buttonsHeight, "Tablist size : ", " players", 50d, 125d, ConfigHandler.tablistSize, false, true, this));
        }
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(11), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        switch (slider.id) {
            case 20:
                ConfigHandler.healthThreshold = slider.getValueInt() / 100d;
                break;
            case 23:
                ConfigHandler.maxDroppedEntityRendered = slider.getValueInt();
                break;
            case 30:
                ConfigHandler.tablistSize = slider.getValueInt();
                break;
        }
    }

}
