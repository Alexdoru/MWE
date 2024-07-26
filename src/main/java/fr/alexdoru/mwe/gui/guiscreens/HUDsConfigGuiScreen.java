package fr.alexdoru.mwe.gui.guiscreens;

import fr.alexdoru.mwe.chat.LocrawListener;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.gui.elements.HUDSettingGuiButtons;
import fr.alexdoru.mwe.gui.elements.SimpleGuiButton;
import fr.alexdoru.mwe.gui.elements.TextElement;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.SoundUtil;
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
        this.maxHeight = (buttonsHeight + 4) * 15 + buttonsHeight;
        super.initGui();
        this.elementList.add(new TextElement(DARK_PURPLE + "HUDs", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(1),
                () -> {
                    if (MWEConfig.showArrowHitHUD && !MWEConfig.showHeadOnArrowHitHUD) {
                        return "Arrow Hit HUD : " + GREEN + "Enabled";
                    } else if (MWEConfig.showArrowHitHUD) {
                        return "Arrow Hit HUD : " + GREEN + "Show Head";
                    } else {
                        return "Arrow Hit HUD : " + RED + "Disabled";
                    }
                },
                () -> {
                    if (MWEConfig.showArrowHitHUD && !MWEConfig.showHeadOnArrowHitHUD) {
                        MWEConfig.showHeadOnArrowHitHUD = true;
                    } else if (MWEConfig.showArrowHitHUD) {
                        MWEConfig.showArrowHitHUD = false;
                        MWEConfig.showHeadOnArrowHitHUD = false;
                    } else {
                        MWEConfig.showArrowHitHUD = true;
                        MWEConfig.showHeadOnArrowHitHUD = false;
                    }
                },
                GuiManager.arrowHitHUD,
                this,
                GREEN + "Arrow Hit HUD",
                GRAY + "Displays the health of your opponent on arrow hits",
                DARK_GRAY + "▪ " + GREEN + "Enabled",
                DARK_GRAY + "▪ " + GREEN + "Show Head" + GRAY + " : Shows player head on hit",
                DARK_GRAY + "▪ " + RED + "Disabled")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(2),
                () -> "Base Location HUD : " + getSuffix(MWEConfig.showBaseLocationHUD),
                () -> {
                    MWEConfig.showBaseLocationHUD = !MWEConfig.showBaseLocationHUD;
                    if (MWEConfig.showBaseLocationHUD && ScoreboardTracker.isInMwGame()) {
                        LocrawListener.setMegaWallsMap();
                    }
                },
                GuiManager.baseLocationHUD,
                this,
                GREEN + "Base Location HUD",
                GRAY + "Displays in which base you are currently in Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(3),
                "Creeper primed TNT HUD",
                (b) -> MWEConfig.showPrimedTNTHUD = b,
                () -> MWEConfig.showPrimedTNTHUD,
                GuiManager.creeperPrimedTntHUD,
                this,
                GRAY + "Displays the cooldown of the primed TNT when playing Creeper" + YELLOW + " in Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(4),
                "Energy display HUD",
                (b) -> MWEConfig.showEnergyDisplayHUD = b,
                () -> MWEConfig.showEnergyDisplayHUD,
                GuiManager.energyDisplayHUD,
                this,
                GRAY + "Displays a HUD with the amount of energy you have" + YELLOW + " in Mega Walls" + GRAY + ". Turns "
                        + AQUA + "aqua" + GRAY + " when your energy level exceeds the amount set below.")
                .accept(this.buttonList);
        this.buttonList.add(new GuiSlider(21, getxCenter() - BUTTON_WIDTH / 2, getButtonYPos(5), BUTTON_WIDTH, buttonsHeight, "Energy threshold : ", "", 1d, 160d, MWEConfig.aquaEnergyDisplayThreshold, false, true, this));
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(6),
                "Kill cooldown HUD",
                (b) -> MWEConfig.showKillCooldownHUD = b,
                () -> MWEConfig.showKillCooldownHUD,
                GuiManager.killCooldownHUD,
                this,
                GRAY + "Displays the cooldown of the /kill command in " + YELLOW + "Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(7),
                () -> {
                    if (MWEConfig.showMiniPotionHUD) {
                        return "Mini potion HUD : " + GREEN + "Enabled";
                    } else if (MWEConfig.showMiniPotionHUDOnlyMW) {
                        return "Mini potion HUD : " + GREEN + "Only in MW";
                    } else {
                        return "Mini potion HUD : " + RED + "Disabled";
                    }
                },
                () -> {
                    if (MWEConfig.showMiniPotionHUD) {
                        MWEConfig.showMiniPotionHUD = false;
                        MWEConfig.showMiniPotionHUDOnlyMW = true;
                    } else if (MWEConfig.showMiniPotionHUDOnlyMW) {
                        MWEConfig.showMiniPotionHUDOnlyMW = false;
                    } else {
                        MWEConfig.showMiniPotionHUD = true;
                    }
                },
                GuiManager.miniPotionHUD,
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
                (b) -> MWEConfig.showPhxBondHUD = b,
                () -> MWEConfig.showPhxBondHUD,
                GuiManager.phoenixBondHUD,
                this,
                GRAY + "Displays the hearts healed from a Phoenix bond" + YELLOW + " in Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(9),
                "Squad HUD",
                (b) -> MWEConfig.showSquadHUD = b,
                () -> MWEConfig.showSquadHUD,
                GuiManager.squadHealthHUD,
                this,
                GRAY + "Displays a mini tablist with just your squadmates")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(10),
                "Speed HUD",
                (b) -> MWEConfig.showSpeedHUD = b,
                () -> MWEConfig.showSpeedHUD,
                GuiManager.speedHUD,
                this,
                GRAY + "Displays your own speed in the XZ plane")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(11),
                () -> "Strength HUD : " + getSuffix(MWEConfig.showStrengthHUD),
                () -> {
                    MWEConfig.showStrengthHUD = !MWEConfig.showStrengthHUD;
                    if (MWEConfig.showStrengthHUD) {
                        SoundUtil.playStrengthSound();
                    }
                },
                GuiManager.strengthHUD,
                this,
                GREEN + "Strength HUD",
                GRAY + "Displays the duration of the strength " + YELLOW + "in Mega Walls" + GRAY + " when it is obtained or about to be obtained, with Dreadlord, Herobrine, Hunter and Zombie.")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(12),
                "Warcry HUD",
                (b) -> MWEConfig.showWarcryHUD = b,
                () -> MWEConfig.showWarcryHUD,
                GuiManager.warcryHUD,
                this,
                GRAY + "Displays the cooldown of the warcry in Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(13),
                () -> "Wither death time HUD : " + (MWEConfig.witherHUDinSidebar ? YELLOW + "in Sidebar" : getSuffix(MWEConfig.showLastWitherHUD)),
                () -> {
                    if (MWEConfig.showLastWitherHUD && !MWEConfig.witherHUDinSidebar) {
                        MWEConfig.witherHUDinSidebar = true;
                        return;
                    }
                    if (!MWEConfig.showLastWitherHUD && !MWEConfig.witherHUDinSidebar) {
                        MWEConfig.showLastWitherHUD = true;
                        return;
                    }
                    MWEConfig.witherHUDinSidebar = false;
                    MWEConfig.showLastWitherHUD = false;
                },
                GuiManager.lastWitherHPHUD,
                this,
                GREEN + "Wither death time HUD",
                GRAY + "Displays the time it takes for the last wither to die " + YELLOW + "in Mega Walls",
                DARK_GRAY + "▪ " + GREEN + "Enabled" + GRAY + " : place the HUD anywhere",
                DARK_GRAY + "▪ " + YELLOW + "In Sidebar" + GRAY + " : The HUD is placed in the sidebar",
                DARK_GRAY + "▪ " + RED + "Disabled")
                .accept(this.buttonList);
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(15), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 21) {
            MWEConfig.aquaEnergyDisplayThreshold = slider.getValueInt();
        }
    }

}
