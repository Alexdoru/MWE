package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatListener;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.HUDSettingGuiButtons;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.*;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import static net.minecraft.util.EnumChatFormatting.*;

public class HUDsConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    public HUDsConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.maxWidth = 90 * 2 + BUTTON_WIDTH + 4 * 2;
        this.maxHeight = (buttonsHeight + 4) * 14 + buttonsHeight;
        super.initGui();
        this.elementList.add(new TextElement(DARK_PURPLE + "HUDs", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(1),
                () -> {
                    if (ConfigHandler.showArrowHitHUD && !ConfigHandler.showHeadOnArrowHitHUD) {
                        return "Arrow Hit HUD : " + GREEN + "Enabled";
                    } else if (ConfigHandler.showArrowHitHUD) {
                        return "Arrow Hit HUD : " + GREEN + "Show Head";
                    } else {
                        return "Arrow Hit HUD : " + RED + "Disabled";
                    }
                },
                () -> {
                    if (ConfigHandler.showArrowHitHUD && !ConfigHandler.showHeadOnArrowHitHUD) {
                        ConfigHandler.showHeadOnArrowHitHUD = true;
                    } else if (ConfigHandler.showArrowHitHUD) {
                        ConfigHandler.showArrowHitHUD = false;
                        ConfigHandler.showHeadOnArrowHitHUD = false;
                    } else {
                        ConfigHandler.showArrowHitHUD = true;
                        ConfigHandler.showHeadOnArrowHitHUD = false;
                    }
                },
                ArrowHitHUD.instance,
                this,
                GREEN + "Arrow Hit HUD",
                GRAY + "Displays the health of your opponent on arrow hits",
                DARK_GRAY + "▪ " + GREEN + "Enabled",
                DARK_GRAY + "▪ " + GREEN + "Show Head" + GRAY + " : Shows player head on hit",
                DARK_GRAY + "▪ " + RED + "Disabled")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(2),
                () -> "Base Location HUD : " + getSuffix(ConfigHandler.showBaseLocationHUD),
                () -> {
                    ConfigHandler.showBaseLocationHUD = !ConfigHandler.showBaseLocationHUD;
                    if (ConfigHandler.showBaseLocationHUD && ScoreboardTracker.isInMwGame) {
                        ChatListener.setMegaWallsMap();
                    }
                },
                BaseLocationHUD.instance,
                this,
                GREEN + "Base Location HUD",
                GRAY + "Displays in which base you are currently in Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(3),
                "Creeper primed TNT HUD",
                (b) -> ConfigHandler.showPrimedTNTHUD = b,
                () -> ConfigHandler.showPrimedTNTHUD,
                CreeperPrimedTntHUD.instance,
                this,
                GRAY + "Displays the cooldown of the primed TNT when playing Creeper" + YELLOW + " in Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(4),
                "Energy display HUD",
                (b) -> ConfigHandler.showEnergyDisplayHUD = b,
                () -> ConfigHandler.showEnergyDisplayHUD,
                EnergyDisplayHUD.instance,
                this,
                GRAY + "Displays a HUD with the amount of energy you have" + YELLOW + " in Mega Walls" + GRAY + ". Turns "
                        + AQUA + "aqua" + GRAY + " when your energy level exceeds the amount set below.")
                .accept(this.buttonList);
        this.buttonList.add(new GuiSlider(21, getxCenter() - BUTTON_WIDTH / 2, getButtonYPos(5), BUTTON_WIDTH, buttonsHeight, "Energy threshold : ", "", 1d, 160d, ConfigHandler.aquaEnergyDisplayThreshold, false, true, this));
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(6),
                "Kill cooldown HUD",
                (b) -> ConfigHandler.showKillCooldownHUD = b,
                () -> ConfigHandler.showKillCooldownHUD,
                KillCooldownHUD.instance,
                this,
                GRAY + "Displays the cooldown of the /kill command in " + YELLOW + "Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(7),
                () -> {
                    if (ConfigHandler.showMiniPotionHUD) {
                        return "Mini potion HUD : " + GREEN + "Enabled";
                    } else if (ConfigHandler.showMiniPotionHUDOnlyMW) {
                        return "Mini potion HUD : " + GREEN + "Only in MW";
                    } else {
                        return "Mini potion HUD : " + RED + "Disabled";
                    }
                },
                () -> {
                    if (ConfigHandler.showMiniPotionHUD) {
                        ConfigHandler.showMiniPotionHUD = false;
                        ConfigHandler.showMiniPotionHUDOnlyMW = true;
                    } else if (ConfigHandler.showMiniPotionHUDOnlyMW) {
                        ConfigHandler.showMiniPotionHUDOnlyMW = false;
                    } else {
                        ConfigHandler.showMiniPotionHUD = true;
                    }
                },
                MiniPotionHUD.instance,
                this,
                GREEN + "Mini potion HUD",
                GRAY + "Displays remaining duration of the following potion buffs : "
                        + LIGHT_PURPLE + "regeneration" + GRAY + ", "
                        + DARK_GRAY + "resistance" + GRAY + ", "
                        + AQUA + "speed" + GRAY + ", "
                        + RED + "strength" + GRAY + ", "
                        + WHITE + "invisibility" + GRAY + ", "
                        + GREEN + "jump boost" + GRAY + ", "
                        + "has different modes :",
                DARK_GRAY + "▪ " + GREEN + "Always Enabled",
                DARK_GRAY + "▪ " + GREEN + "Only in Mega Walls",
                DARK_GRAY + "▪ " + RED + "Disabled")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(8),
                "Phoenix bond HUD",
                (b) -> ConfigHandler.showPhxBondHUD = b,
                () -> ConfigHandler.showPhxBondHUD,
                PhoenixBondHUD.instance,
                this,
                GRAY + "Displays the hearts healed from a Phoenix bond" + YELLOW + " in Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(9),
                "Squad HUD",
                (b) -> ConfigHandler.showSquadHUD = b,
                () -> ConfigHandler.showSquadHUD,
                SquadHealthHUD.instance,
                this,
                GRAY + "Displays a mini tablist with just your squadmates")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(10),
                "Speed HUD",
                (b) -> ConfigHandler.showSpeedHUD = b,
                () -> ConfigHandler.showSpeedHUD,
                SpeedHUD.instance,
                this,
                GRAY + "Displays your own speed in the XZ plane")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(11),
                () -> "Strength HUD : " + getSuffix(ConfigHandler.showStrengthHUD),
                () -> {
                    ConfigHandler.showStrengthHUD = !ConfigHandler.showStrengthHUD;
                    if (ConfigHandler.showStrengthHUD) {
                        SoundUtil.playStrengthSound();
                    }
                },
                HunterStrengthHUD.instance,
                this,
                GREEN + "Strength HUD",
                GRAY + "Displays the duration of the strength " + YELLOW + "in Mega Walls" + GRAY + " when it is obtained or about to be obtained, with Dreadlord, Herobrine, Hunter and Zombie.")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(12),
                () -> "Wither death time HUD : " + (ConfigHandler.witherHUDinSidebar ? YELLOW + "in Sidebar" : getSuffix(ConfigHandler.showLastWitherHUD)),
                () -> {
                    if (ConfigHandler.showLastWitherHUD && !ConfigHandler.witherHUDinSidebar) {
                        ConfigHandler.witherHUDinSidebar = true;
                        return;
                    }
                    if (!ConfigHandler.showLastWitherHUD && !ConfigHandler.witherHUDinSidebar) {
                        ConfigHandler.showLastWitherHUD = true;
                        return;
                    }
                    ConfigHandler.witherHUDinSidebar = false;
                    ConfigHandler.showLastWitherHUD = false;
                },
                LastWitherHPHUD.instance,
                this,
                GREEN + "Wither death time HUD",
                GRAY + "Displays the time it takes for the last wither to die " + YELLOW + "in Mega Walls",
                DARK_GRAY + "▪ " + GREEN + "Enabled" + GRAY + " : place the HUD anywhere",
                DARK_GRAY + "▪ " + YELLOW + "In Sidebar" + GRAY + " : The HUD is placed in the sidebar",
                DARK_GRAY + "▪ " + RED + "Disabled")
                .accept(this.buttonList);
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(14), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 21) {
            ConfigHandler.aquaEnergyDisplayThreshold = slider.getValueInt();
        }
    }

}
