package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.HUDSettingGuiButtons;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.*;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

public class HUDsConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    public HUDsConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.maxWidth = 90 * 2 + BUTTON_WIDTH + 4 * 2;
        this.maxHeight = (buttonsHeight + 4) * 13 + buttonsHeight;
        super.initGui();
        this.elementList.add(new TextElement(EnumChatFormatting.DARK_PURPLE + "HUDs", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(1),
                "Arrow Hit HUD",
                (b) -> ConfigHandler.showArrowHitHUD = b,
                () -> ConfigHandler.showArrowHitHUD,
                ConfigHandler.arrowHitHUDPosition,
                ArrowHitHUD.instance,
                this,
                EnumChatFormatting.GRAY + "Displays the health of your opponent on arrow hits")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(2),
                "/kill cooldown HUD",
                (b) -> ConfigHandler.showKillCooldownHUD = b,
                () -> ConfigHandler.showKillCooldownHUD,
                ConfigHandler.killCooldownHUDPosition,
                KillCooldownHUD.instance,
                this,
                EnumChatFormatting.GRAY + "Displays the cooldown of the /kill command in " + EnumChatFormatting.YELLOW + "Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(3),
                "Squad HUD",
                (b) -> ConfigHandler.showSquadHUD = b,
                () -> ConfigHandler.showSquadHUD,
                ConfigHandler.squadHUDPosition,
                SquadHealthHUD.instance,
                this,
                EnumChatFormatting.GRAY + "Displays a mini tablist with just your squadmates")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(4),
                () -> "Strength HUD : " + getSuffix(ConfigHandler.showStrengthHUD),
                () -> {
                    ConfigHandler.showStrengthHUD = !ConfigHandler.showStrengthHUD;
                    if (ConfigHandler.showStrengthHUD) {
                        SoundUtil.playStrengthSound();
                    }
                },
                ConfigHandler.hunterStrengthHUDPosition,
                HunterStrengthHUD.instance,
                this,
                EnumChatFormatting.GREEN + "Strength HUD",
                EnumChatFormatting.GRAY + "Displays the duration of the strength " + EnumChatFormatting.YELLOW + "in Mega Walls" + EnumChatFormatting.GRAY + " when it is obtained or about to be obtained, with Dreadlord, Herobrine, Hunter and Zombie.")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(5),
                () -> "Wither death time HUD : " + (ConfigHandler.witherHUDinSidebar ? EnumChatFormatting.YELLOW + "in Sidebar" : getSuffix(ConfigHandler.showLastWitherHUD)),
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
                ConfigHandler.lastWitherHUDPosition,
                LastWitherHPHUD.instance,
                this,
                EnumChatFormatting.GREEN + "Wither death time HUD",
                EnumChatFormatting.GRAY + "Displays the time it takes for the last wither to die " + EnumChatFormatting.YELLOW + "in Mega Walls" + EnumChatFormatting.GRAY + ". The HUD can be configured to appear in the " + EnumChatFormatting.YELLOW + "sidebar" + EnumChatFormatting.GRAY + ".")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(6),
                "Creeper primed TNT HUD",
                (b) -> ConfigHandler.showPrimedTNTHUD = b,
                () -> ConfigHandler.showPrimedTNTHUD,
                ConfigHandler.creeperTNTHUDPosition,
                CreeperPrimedTntHUD.instance,
                this,
                EnumChatFormatting.GRAY + "Displays the cooldown of the primed TNT when playing Creeper" + EnumChatFormatting.YELLOW + " in Mega Walls")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(7),
                "Energy display HUD",
                (b) -> ConfigHandler.showEnergyDisplayHUD = b,
                () -> ConfigHandler.showEnergyDisplayHUD,
                ConfigHandler.energyDisplayHUDPosition,
                EnergyDisplayHUD.instance,
                this,
                EnumChatFormatting.GRAY + "Displays a HUD with the amount of energy you have" + EnumChatFormatting.YELLOW + " in Mega Walls" + EnumChatFormatting.GRAY + ". Turns "
                        + EnumChatFormatting.AQUA + "aqua" + EnumChatFormatting.GRAY + " when your energy level exceeds the amount set below.")
                .accept(this.buttonList);
        this.buttonList.add(new GuiSlider(21, getxCenter() - BUTTON_WIDTH / 2, getButtonYPos(8), BUTTON_WIDTH, buttonsHeight, "Energy threshold : ", "", 1d, 160d, ConfigHandler.aquaEnergyDisplayThreshold, false, true, this));
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(9),
                "Speed HUD",
                (b) -> ConfigHandler.showSpeedHUD = b,
                () -> ConfigHandler.showSpeedHUD,
                ConfigHandler.speedHUDPosition,
                SpeedHUD.instance,
                this,
                EnumChatFormatting.GRAY + "Displays your own speed in the XZ plane")
                .accept(this.buttonList);
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(10),
                "Phoenix bond HUD",
                (b) -> ConfigHandler.showPhxBondHUD = b,
                () -> ConfigHandler.showPhxBondHUD,
                ConfigHandler.phxBondHUDPosition,
                PhoenixBondHUD.instance,
                this,
                EnumChatFormatting.GRAY + "Displays the hearts healed from a Phoenix bond" + EnumChatFormatting.YELLOW + " in Mega Walls")
                .accept(this.buttonList);
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(12), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 21) {
            ConfigHandler.aquaEnergyDisplayThreshold = (int) slider.getValue();
        }
    }

}
