package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.ArrowHitHUD;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.HunterStrengthHUD;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.KillCooldownHUD;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.LastWitherHPHUD;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.common.Loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MWEnConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    public MWEnConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        /*
         * Defines the button list
         */
        final int buttonsWidth = 210;
        final int XposLeftButton = getxCenter() - buttonsWidth - 10;
        final int XposRightButton = getxCenter() + 10;

        buttonList.add(new GuiButton(18, XposLeftButton, getYposForButton(-5), buttonsWidth, ButtonsHeight, getButtonDisplayString(18)));
        buttonList.add(new GuiButton(26, XposLeftButton, getYposForButton(-4), buttonsWidth, ButtonsHeight, getButtonDisplayString(26)));
        buttonList.add(new GuiButton(19, XposLeftButton, getYposForButton(-3), buttonsWidth, ButtonsHeight, getButtonDisplayString(19)));
        buttonList.add(new GuiButton(25, XposLeftButton, getYposForButton(-2), buttonsWidth, ButtonsHeight, getButtonDisplayString(25)));
        buttonList.add(new GuiButton(15, XposLeftButton, getYposForButton(-1), buttonsWidth, ButtonsHeight, getButtonDisplayString(15)));
        buttonList.add(new GuiButton(27, XposLeftButton, getYposForButton(0), buttonsWidth, ButtonsHeight, getButtonDisplayString(27)));
        buttonList.add(new GuiButton(24, XposLeftButton, getYposForButton(1), buttonsWidth, ButtonsHeight, getButtonDisplayString(24)));

        buttonList.add(new GuiButton(21, XposRightButton, getYposForButton(-5), buttonsWidth, ButtonsHeight, getButtonDisplayString(21)));
        buttonList.add(new GuiButton(0, XposRightButton, getYposForButton(-4), buttonsWidth, ButtonsHeight, getButtonDisplayString(0)));
        buttonList.add(new GuiButton(16, XposRightButton, getYposForButton(-3), buttonsWidth, ButtonsHeight, getButtonDisplayString(16)));
        buttonList.add(new GuiButton(17, XposRightButton, getYposForButton(-2), buttonsWidth, ButtonsHeight, getButtonDisplayString(17)));
        buttonList.add(new GuiSlider(20, XposRightButton, getYposForButton(-1), buttonsWidth, ButtonsHeight, "Health threshold : ", " %", 0d, 100d, ConfigHandler.healthThreshold * 100d, false, true, this));
        buttonList.add(new GuiButton(22, XposRightButton, getYposForButton(0), buttonsWidth, ButtonsHeight, getButtonDisplayString(22)));
        buttonList.add(new GuiSlider(23, XposRightButton, getYposForButton(1), buttonsWidth, ButtonsHeight, "Maximum dropped item entities : ", "", 40d, 1000d, ConfigHandler.maxDroppedEntityRendered, false, true, this));

        final int XposCenterButton = getxCenter() - buttonsWidth / 2;

        /* HUD Buttons */
        buttonList.add(new GuiButton(9, XposCenterButton, getYposForButton(5), buttonsWidth, ButtonsHeight, getButtonDisplayString(9)));
        buttonList.add(new GuiButton(2, XposCenterButton, getYposForButton(4), buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        buttonList.add(new GuiButton(3, XposCenterButton, getYposForButton(3), buttonsWidth, ButtonsHeight, getButtonDisplayString(3)));
        buttonList.add(new GuiButton(11, XposCenterButton, getYposForButton(6), buttonsWidth, ButtonsHeight, getButtonDisplayString(11)));

        final int sideButtonsWidth = 100;
        final int XposCenterLeftButton = getxCenter() - buttonsWidth / 2 - 4 - sideButtonsWidth;
        final int XposCenterRightButton = getxCenter() + buttonsWidth / 2 + 4;

        /* Buttons : Reset HUD position */
        buttonList.add(new GuiButton(14, XposCenterLeftButton, getYposForButton(5), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(14)));
        buttonList.add(new GuiButton(7, XposCenterLeftButton, getYposForButton(4), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(7)));
        buttonList.add(new GuiButton(8, XposCenterLeftButton, getYposForButton(3), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(8)));
        buttonList.add(new GuiButton(12, XposCenterLeftButton, getYposForButton(6), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(12)));

        /* Buttons : Move HUD */
        buttonList.add(new GuiButton(13, XposCenterRightButton, getYposForButton(5), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(13)));
        buttonList.add(new GuiButton(5, XposCenterRightButton, getYposForButton(4), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(5)));
        buttonList.add(new GuiButton(6, XposCenterRightButton, getYposForButton(3), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(6)));
        buttonList.add(new GuiButton(10, XposCenterRightButton, getYposForButton(6), sideButtonsWidth, ButtonsHeight, getButtonDisplayString(10)));

        /* Exit button */
        buttonList.add(new GuiButton(4, getxCenter() - 150 / 2, getYposForButton(8), 150, ButtonsHeight, getButtonDisplayString(4)));
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 21:
                return "Safe inventory : " + getSuffix(ConfigHandler.safeInventory);
            case 16:
                return "Strength particule HBR DRE : " + getSuffix(ConfigHandler.strengthParticules);
            case 15:
                return "Icons on names : " + getSuffix(ConfigHandler.iconsOnNames);
            case 0:
                return "Short coin messages : " + getSuffix(ConfigHandler.shortCoinMessage);
            case 24:
                return "Prestige V tags : " + getSuffix(ConfigHandler.prestigeV);
            case 25:
                return "Hide repetitive MW msg : " + getSuffix(ConfigHandler.hideRepetitiveMWChatMsg);
            case 26:
                return "Clear view : " + getSuffix(ConfigHandler.clearVision);
            case 27:
                return "Nick Hider : " + getSuffix(ConfigHandler.nickHider);
            case 9:
                return "Show strength HUD : " + getSuffix(ConfigHandler.showStrengthHUD);
            case 18:
                return "Cancel night vision effect : " + getSuffix(!ConfigHandler.keepNightVisionEffect);
            case 2:
                return "Show /kill cooldown HUD : " + getSuffix(ConfigHandler.showKillCooldownHUD);
            case 3:
                return "Show arrow Hit HUD : " + getSuffix(ConfigHandler.showArrowHitHUD);
            case 11:
                return "Show wither death time HUD : " + (ConfigHandler.witherHUDinSidebar ? EnumChatFormatting.YELLOW + "in Sidebar" : getSuffix(ConfigHandler.showLastWitherHUD));
            case 19:
                return "Colored tablist health : " + getSuffix(ConfigHandler.useColoredScores);
            case 17:
                return "Sound warning when low HP : " + getSuffix(ConfigHandler.playSoundLowHP);
            case 22:
                return "Limit dropped item rendered : " + getSuffix(ConfigHandler.limitDroppedEntityRendered);
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
        final List<String> textLines = new ArrayList<>();
        switch (id) {
            case 21:
                textLines.add(EnumChatFormatting.GREEN + "Prevents dropping the sword you are holding in your hotbar");
                textLines.add(EnumChatFormatting.GREEN + "Prevents hotkeying important kit items out of your inventory");
                textLines.add(EnumChatFormatting.GRAY + "The later only works in Mega Walls");
                break;
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
                textLines.add(NameUtil.prefix_old_report + EnumChatFormatting.YELLOW + "Players no longer getting auto-reported");
                break;
            case 0:
                textLines.add(EnumChatFormatting.GREEN + "Makes the coin messages shorter by removing the network booster info");
                textLines.add(EnumChatFormatting.GREEN + "It makes the assists messages in mega walls fit on one line instead of two");
                textLines.add("");
                textLines.add(EnumChatFormatting.GOLD + "+100 coins! (hypixel's Network booster)" + EnumChatFormatting.AQUA + " FINAL KILL");
                textLines.add("Will become : ");
                textLines.add(EnumChatFormatting.GOLD + "+100 coins!" + EnumChatFormatting.AQUA + " FINAL KILL");
                break;
            case 24:
                textLines.add(EnumChatFormatting.GREEN + "Adds the prestige V colored tags in mega walls");
                textLines.add(EnumChatFormatting.GREEN + "You need at least" + EnumChatFormatting.GOLD + " 1 000 000 coins" + EnumChatFormatting.GREEN + ", " + EnumChatFormatting.GOLD + " 10 000 classpoints" + EnumChatFormatting.GREEN + ",");
                textLines.add(EnumChatFormatting.GREEN + "and a " + EnumChatFormatting.DARK_RED + "working API Key" + EnumChatFormatting.GREEN + ". This will send api requests and store the data");
                textLines.add(EnumChatFormatting.GREEN + "in a cache until you close your game.");
                textLines.add(EnumChatFormatting.GREEN + "Type " + EnumChatFormatting.YELLOW + "/mwenhancements clearcache" + EnumChatFormatting.GREEN + " to force update the data");
                textLines.add("");
                textLines.add(EnumChatFormatting.GOLD + "Prestige Colors :");
                textLines.add(EnumChatFormatting.GOLD + "10000 classpoints : " + EnumChatFormatting.DARK_PURPLE + "[TAG]");
                textLines.add(EnumChatFormatting.GOLD + "13000 classpoints : " + EnumChatFormatting.DARK_BLUE + "[TAG]");
                textLines.add(EnumChatFormatting.GOLD + "19000 classpoints : " + EnumChatFormatting.DARK_AQUA + "[TAG]");
                textLines.add(EnumChatFormatting.GOLD + "28000 classpoints : " + EnumChatFormatting.DARK_GREEN + "[TAG]");
                textLines.add(EnumChatFormatting.GOLD + "40000 classpoints : " + EnumChatFormatting.DARK_RED + "[TAG]");
                break;
            case 25:
                textLines.add(EnumChatFormatting.GREEN + "Hides the following messages in mega walls");
                textLines.add("");
                textLines.add(EnumChatFormatting.RED + "Get to the middle to stop the hunger!");
                textLines.add(EnumChatFormatting.GREEN + "You broke your protected chest");
                textLines.add(EnumChatFormatting.GREEN + "You broke your protected trapped chest");
                textLines.add(EnumChatFormatting.YELLOW + "Your Salvaging skill returned your arrow to you!");
                textLines.add(EnumChatFormatting.YELLOW + "Your Efficiency skill got you an extra drop!");
                textLines.add(EnumChatFormatting.GREEN + "Your " + EnumChatFormatting.AQUA + "Ability name " + EnumChatFormatting.GREEN + "skill is ready!");
                textLines.add(EnumChatFormatting.GREEN + "Click your sword or bow to activate your skill!");
                break;
            case 26:
                textLines.add(EnumChatFormatting.GREEN + "Stops rendering particles that are too close (75cm)");
                textLines.add(EnumChatFormatting.GREEN + "to the camera for a better visibility");
                break;
            case 9:
                textLines.add(EnumChatFormatting.GREEN + "Displays a HUD when you get strenght with Dreadlord, Herobrine, Hunter and Zombie");
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
            case 17:
                textLines.add(EnumChatFormatting.GREEN + "Plays a sound when your health drops below the threshold defined below");
                textLines.add(EnumChatFormatting.GRAY + "The sound used is \"note.pling\" check your sound settings to see if it's enabled !");
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
            case 22:
                textLines.add(EnumChatFormatting.GREEN + "Dynamically modifies the render distance for dropped items entities to preserve performance");
                textLines.add(EnumChatFormatting.GREEN + "It starts reducing the render distance when exceeding the threshold set below");
                textLines.add(EnumChatFormatting.GRAY + "There is a keybind (ESC -> options -> controls -> MegaWallsEnhancements) to toggle it on the fly");
                break;
            case 27:
                textLines.add(EnumChatFormatting.GREEN + "Shows your real name instead of your nick when forming the squad in Mega Walls");
                break;
        }
        return textLines;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 21:
                ConfigHandler.safeInventory = !ConfigHandler.safeInventory;
                break;
            case 16:
                ConfigHandler.strengthParticules = !ConfigHandler.strengthParticules;
                break;
            case 15:
                ConfigHandler.iconsOnNames = !ConfigHandler.iconsOnNames;
                NameUtil.refreshAllNamesInWorld();
                break;
            case 24:
                if (ConfigHandler.prestigeV) {
                    ConfigHandler.prestigeV = false;
                    NameUtil.refreshAllNamesInWorld();
                } else {
                    if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                        ChatUtil.printApikeySetupInfo();
                    } else {
                        ConfigHandler.prestigeV = true;
                        NameUtil.refreshAllNamesInWorld();
                    }
                }
                break;
            case 25:
                ConfigHandler.hideRepetitiveMWChatMsg = !ConfigHandler.hideRepetitiveMWChatMsg;
                break;
            case 26:
                ConfigHandler.clearVision = !ConfigHandler.clearVision;
                break;
            case 27:
                ConfigHandler.nickHider = !ConfigHandler.nickHider;
                break;
            case 0:
                ConfigHandler.shortCoinMessage = !ConfigHandler.shortCoinMessage;
                break;
            case 9:
                ConfigHandler.showStrengthHUD = !ConfigHandler.showStrengthHUD;
                if (ConfigHandler.showStrengthHUD) {
                    SoundUtil.playStrengthSound();
                }
                break;
            case 18:
                ConfigHandler.keepNightVisionEffect = !ConfigHandler.keepNightVisionEffect;
                break;
            case 2:
                ConfigHandler.showKillCooldownHUD = !ConfigHandler.showKillCooldownHUD;
                break;
            case 3:
                ConfigHandler.showArrowHitHUD = !ConfigHandler.showArrowHitHUD;
                break;
            case 11:
                if (ConfigHandler.showLastWitherHUD && !ConfigHandler.witherHUDinSidebar) {
                    ConfigHandler.witherHUDinSidebar = true;
                    if (Loader.isModLoaded("feather")) {
                        ChatUtil.addChatMessage(EnumChatFormatting.RED + "The sidebar integration for HUD doesn't work with Feather because the client is obfuscated and closed source >:(");
                    }
                    break;
                }
                if (!ConfigHandler.showLastWitherHUD && !ConfigHandler.witherHUDinSidebar) {
                    ConfigHandler.showLastWitherHUD = true;
                    break;
                }
                ConfigHandler.witherHUDinSidebar = false;
                ConfigHandler.showLastWitherHUD = false;
                break;
            case 19:
                ConfigHandler.useColoredScores = !ConfigHandler.useColoredScores;
                break;
            case 22:
                ConfigHandler.limitDroppedEntityRendered = !ConfigHandler.limitDroppedEntityRendered;
                break;
            case 17:
                ConfigHandler.playSoundLowHP = !ConfigHandler.playSoundLowHP;
                if (ConfigHandler.playSoundLowHP) {
                    SoundUtil.playLowHPSound();
                }
                break;
            case 4:
                mc.displayGuiScreen(parent);
                break;
            case 5:
                mc.displayGuiScreen(new PositionEditGuiScreen(KillCooldownHUD.instance, this));
                break;
            case 6:
                mc.displayGuiScreen(new PositionEditGuiScreen(ArrowHitHUD.instance, this));
                break;
            case 10:
                mc.displayGuiScreen(new PositionEditGuiScreen(LastWitherHPHUD.instance, this));
                break;
            case 13:
                mc.displayGuiScreen(new PositionEditGuiScreen(HunterStrengthHUD.instance, this));
                break;
            case 7:
                KillCooldownHUD.instance.guiPosition.setRelative(0d, 0d);
                break;
            case 8:
                ArrowHitHUD.instance.guiPosition.setRelative(0.5d, 9d / 20d);
                break;
            case 12:
                LastWitherHPHUD.instance.guiPosition.setRelative(0.75d, 0.05d);
                break;
            case 14:
                HunterStrengthHUD.instance.guiPosition.setRelative(0.5d, 8d / 20d);
                break;
            default:
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);
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
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle("Mega Walls Enhancements v" + MegaWallsEnhancementsMod.version, 2, (width / 2.0f), getYposForButton(-8), Integer.parseInt("55FF55", 16));
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawTooltips(mouseX, mouseY);
    }

}
