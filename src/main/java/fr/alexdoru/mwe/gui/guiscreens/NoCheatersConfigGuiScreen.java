package fr.alexdoru.mwe.gui.guiscreens;

import fr.alexdoru.mwe.chat.ChatHandler;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.gui.elements.FancyGuiButton;
import fr.alexdoru.mwe.gui.elements.OptionGuiButton;
import fr.alexdoru.mwe.gui.elements.SimpleGuiButton;
import fr.alexdoru.mwe.gui.elements.TextElement;
import fr.alexdoru.mwe.nocheaters.WarningMessages;
import fr.alexdoru.mwe.utils.NameUtil;
import fr.alexdoru.mwe.utils.SoundUtil;
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
                () -> "Warning messages in chat : " + getSuffix(MWEConfig.warningMessages),
                () -> {
                    MWEConfig.warningMessages = !MWEConfig.warningMessages;
                    if (MWEConfig.warningMessages) {
                        WarningMessages.printReportMessagesForWorld(false);
                    } else {
                        ChatHandler.deleteAllWarningMessages();
                    }
                }, GREEN + "Warning messages in chat",
                GRAY + "Prints a warning message in chat when a reported player joins your world, those messages have built in compact chat."));
        this.buttonList.add(new FancyGuiButton(
                xPos, getButtonYPos(3),
                () -> "Report suggestions in chat : " + getSuffix(MWEConfig.reportSuggestions),
                () -> {
                    MWEConfig.reportSuggestions = !MWEConfig.reportSuggestions;
                    if (MWEConfig.reportSuggestions) {
                        SoundUtil.playChatNotifSound();
                    }
                }, GREEN + "Report suggestions in chat",
                GRAY + "When there is a message that respects the following patterns, it will print a report suggestion in chat",
                GREEN + "Player: " + WHITE + "playername is bhoping",
                GREEN + "Player: " + WHITE + "wdr playername cheat",
                GREEN + "Player: " + WHITE + "report playername cheat"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(4),
                "Delete old reports",
                (b) -> MWEConfig.deleteOldReports = b,
                () -> MWEConfig.deleteOldReports,
                GRAY + "Deletes reports older than the specified value, the deletion occurs when you start Minecraft."));
        this.buttonList.add(new GuiSlider(7, xPos, getButtonYPos(5), BUTTON_WIDTH, 20, "Delete reports older than : ", " days", 1d, 365d, MWEConfig.timeDeleteReport, false, true, this));
        this.buttonList.add(new FancyGuiButton(
                xPos, getButtonYPos(6),
                () -> "Censor cheaters in chat : " + (MWEConfig.deleteCheaterChatMsg ? GREEN + "Delete" : (MWEConfig.censorCheaterChatMsg ? YELLOW + "Censor" : RED + "Disabled")),
                () -> {
                    if (MWEConfig.censorCheaterChatMsg && !MWEConfig.deleteCheaterChatMsg) {
                        MWEConfig.deleteCheaterChatMsg = true;
                        return;
                    }
                    if (!MWEConfig.censorCheaterChatMsg && !MWEConfig.deleteCheaterChatMsg) {
                        MWEConfig.censorCheaterChatMsg = true;
                        return;
                    }
                    MWEConfig.deleteCheaterChatMsg = false;
                    MWEConfig.censorCheaterChatMsg = false;
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
                () -> "Warning Icon on names : " + (MWEConfig.warningIconsTabOnly ? GREEN + "Tab Only" : getSuffix(MWEConfig.warningIconsOnNames)),
                () -> {
                    if (MWEConfig.warningIconsOnNames && !MWEConfig.warningIconsTabOnly) {
                        MWEConfig.warningIconsOnNames = false;
                        MWEConfig.warningIconsTabOnly = true;
                    } else if (MWEConfig.warningIconsTabOnly) {
                        MWEConfig.warningIconsTabOnly = false;
                    } else {
                        MWEConfig.warningIconsOnNames = true;
                    }
                    NameUtil.refreshAllNamesInWorld();
                },
                iconsTooltip));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(9), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 7) {
            MWEConfig.timeDeleteReport = slider.getValueInt();
        }
    }

}
