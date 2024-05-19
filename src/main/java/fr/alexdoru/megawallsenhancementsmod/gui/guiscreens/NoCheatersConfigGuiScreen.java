package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.FancyGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.OptionGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WarningMessagesHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

public class NoCheatersConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    public NoCheatersConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final String msg = WHITE + "NoCheaters saves players reported via " + YELLOW + "/wdr playername" + WHITE + " (not /report)";
        this.maxWidth = fontRendererObj.getStringWidth(msg);
        this.maxHeight = (buttonsHeight + 4) * 10 + buttonsHeight;
        super.initGui();
        final int xPos = getxCenter() - BUTTON_WIDTH / 2;
        this.elementList.add(new TextElement(RED + "NoCheaters", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.elementList.add(new TextElement(msg, getxCenter(), getButtonYPos(0)).makeCentered());
        this.elementList.add(new TextElement(WHITE + "and warns you about them ingame (icon on name, chat message)", getxCenter(), getButtonYPos(0) + fontRendererObj.FONT_HEIGHT).makeCentered());
        this.elementList.add(new TextElement(WHITE + "If you want to remove a player from your report list use :", getxCenter(), getButtonYPos(0) + 2 * fontRendererObj.FONT_HEIGHT).makeCentered());
        this.elementList.add(new TextElement(YELLOW + "/unwdr playername" + WHITE + " or click the name on the warning message", getxCenter(), getButtonYPos(0) + 3 * fontRendererObj.FONT_HEIGHT).makeCentered());
        this.buttonList.add(new FancyGuiButton(
                xPos, getButtonYPos(2),
                () -> "Warning messages in chat : " + getSuffix(ConfigHandler.warningMessages),
                () -> {
                    ConfigHandler.warningMessages = !ConfigHandler.warningMessages;
                    if (ConfigHandler.warningMessages) {
                        WarningMessagesHandler.printReportMessagesForWorld(false);
                    } else {
                        ChatHandler.deleteAllWarningMessages();
                    }
                }, GREEN + "Warning messages in chat",
                GRAY + "Prints a warning message in chat when a reported player joins your world, those messages have built in compact chat."));
        this.buttonList.add(new FancyGuiButton(
                xPos, getButtonYPos(3),
                () -> "Report suggestions in chat : " + getSuffix(ConfigHandler.reportSuggestions),
                () -> {
                    ConfigHandler.reportSuggestions = !ConfigHandler.reportSuggestions;
                    if (ConfigHandler.reportSuggestions) {
                        SoundUtil.playReportSuggestionSound();
                    }
                }, GREEN + "Report suggestions in chat",
                GRAY + "When there is a message that respects the following patterns, it will print a report suggestion in chat",
                GREEN + "Player: " + WHITE + "playername is bhoping",
                GREEN + "Player: " + WHITE + "wdr playername cheat",
                GREEN + "Player: " + WHITE + "report playername cheat"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(4),
                "Delete old reports",
                (b) -> ConfigHandler.deleteOldReports = b,
                () -> ConfigHandler.deleteOldReports,
                GRAY + "Deletes reports older than the specified value, the deletion occurs when you start Minecraft."));
        this.buttonList.add(new GuiSlider(7, xPos, getButtonYPos(5), BUTTON_WIDTH, 20, "Delete reports older than : ", " days", 1d, 365d, ConfigHandler.timeDeleteReport, false, true, this));
        this.buttonList.add(new FancyGuiButton(
                xPos, getButtonYPos(6),
                () -> "Censor cheaters in chat : " + (ConfigHandler.deleteCheaterChatMsg ? GREEN + "Delete" : (ConfigHandler.censorCheaterChatMsg ? YELLOW + "Censor" : RED + "Disabled")),
                () -> {
                    if (ConfigHandler.censorCheaterChatMsg && !ConfigHandler.deleteCheaterChatMsg) {
                        ConfigHandler.deleteCheaterChatMsg = true;
                        return;
                    }
                    if (!ConfigHandler.censorCheaterChatMsg && !ConfigHandler.deleteCheaterChatMsg) {
                        ConfigHandler.censorCheaterChatMsg = true;
                        return;
                    }
                    ConfigHandler.deleteCheaterChatMsg = false;
                    ConfigHandler.censorCheaterChatMsg = false;
                },
                GREEN + "Censor cheaters in chat",
                GRAY + "Deletes or censors chat messages sent by reported players"));
        final List<String> iconsTooltip = new ArrayList<>();
        iconsTooltip.add(GREEN + "Warning Icon on names");
        iconsTooltip.add("");
        iconsTooltip.add(DARK_GRAY + "▪ " + GREEN + "Enabled" + GRAY + " : displays a warning icon in front of names on nametags and in the tablist");
        iconsTooltip.add("");
        iconsTooltip.add(DARK_GRAY + "▪ " + GREEN + "Tab Only" + GRAY + " : displays a warning icon in front of names in the tablist only");
        iconsTooltip.add("");
        iconsTooltip.add(NameUtil.RED_WARNING_ICON + GRAY + ": players reported for blatant cheats");
        iconsTooltip.add(NameUtil.WARNING_ICON + GRAY + ": players reported for cheating");
        iconsTooltip.add(NameUtil.PINK_WARNING_ICON + GRAY + ": players flagged by the /scangame command");
        this.buttonList.add(new FancyGuiButton(
                xPos, getButtonYPos(7),
                () -> "Warning Icon on names : " + (ConfigHandler.warningIconsTabOnly ? GREEN + "Tab Only" : getSuffix(ConfigHandler.warningIconsOnNames)),
                () -> {
                    if (ConfigHandler.warningIconsOnNames && !ConfigHandler.warningIconsTabOnly) {
                        ConfigHandler.warningIconsOnNames = false;
                        ConfigHandler.warningIconsTabOnly = true;
                    } else if (ConfigHandler.warningIconsTabOnly) {
                        ConfigHandler.warningIconsTabOnly = false;
                    } else {
                        ConfigHandler.warningIconsOnNames = true;
                    }
                    NameUtil.refreshAllNamesInWorld();
                },
                iconsTooltip));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(9), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 7) {
            ConfigHandler.timeDeleteReport = slider.getValueInt();
        }
    }

}
