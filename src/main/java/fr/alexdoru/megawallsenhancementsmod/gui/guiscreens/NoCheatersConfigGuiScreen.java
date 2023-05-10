package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.WarningMessagesHandler;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.FancyGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.OptionGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

public class NoCheatersConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    private FancyGuiButton reportSuggestionButton;
    private FancyGuiButton autoreportSuggestionButton;

    public NoCheatersConfigGuiScreen() {
        this.parent = null;
    }

    public NoCheatersConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final String msg = EnumChatFormatting.WHITE + "NoCheaters saves players reported via " + EnumChatFormatting.YELLOW + "/wdr playername" + EnumChatFormatting.WHITE + " (not /report)";
        this.maxWidth = fontRendererObj.getStringWidth(msg);
        this.maxHeight = (buttonsHeight + 4) * 10 + buttonsHeight;
        super.initGui();
        final int xPos = getxCenter() - BUTTON_WIDTH / 2;
        this.elementList.add(new TextElement(EnumChatFormatting.RED + "NoCheaters", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.elementList.add(new TextElement(msg, getxCenter(), getButtonYPos(0)).makeCentered());
        this.elementList.add(new TextElement(EnumChatFormatting.WHITE + "and warns you about them ingame (icon on name, chat message)", getxCenter(), getButtonYPos(0) + fontRendererObj.FONT_HEIGHT).makeCentered());
        this.elementList.add(new TextElement(EnumChatFormatting.WHITE + "If you want to remove a player from your report list use :", getxCenter(), getButtonYPos(0) + 2 * fontRendererObj.FONT_HEIGHT).makeCentered());
        this.elementList.add(new TextElement(EnumChatFormatting.YELLOW + "/unwdr playername" + EnumChatFormatting.WHITE + " or click the name on the warning message", getxCenter(), getButtonYPos(0) + 3 * fontRendererObj.FONT_HEIGHT).makeCentered());
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
                }, EnumChatFormatting.GREEN + "Warning messages in chat",
                EnumChatFormatting.GRAY + "Prints a warning message in chat when a reported player joins your world, those messages have built in compact chat."));
        this.buttonList.add(reportSuggestionButton = new FancyGuiButton(
                xPos, getButtonYPos(3),
                () -> "Report suggestions in chat : " + getSuffix(ConfigHandler.reportSuggestions),
                () -> {
                    ConfigHandler.reportSuggestions = !ConfigHandler.reportSuggestions;
                    if (ConfigHandler.reportSuggestions) {
                        SoundUtil.playReportSuggestionSound();
                    }
                    if (!ConfigHandler.reportSuggestions) {
                        ConfigHandler.autoreportSuggestions = false;
                        autoreportSuggestionButton.updateDisplayText();
                    }
                }, EnumChatFormatting.GREEN + "Report suggestions in chat",
                EnumChatFormatting.GRAY + "When there is a message that respects the following patterns, it will print a report suggestion in chat",
                EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "playername is bhoping",
                EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "wdr playername cheat",
                EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "report playername cheat"));
        this.buttonList.add(autoreportSuggestionButton = new FancyGuiButton(
                xPos, getButtonYPos(4),
                () -> "Auto-report suggestions : " + getSuffix(ConfigHandler.autoreportSuggestions),
                () -> {
                    ConfigHandler.autoreportSuggestions = !ConfigHandler.autoreportSuggestions;
                    if (ConfigHandler.autoreportSuggestions) {
                        ConfigHandler.reportSuggestions = true;
                        reportSuggestionButton.updateDisplayText();
                    } else {
                        ReportQueue.INSTANCE.clearSuggestionsInReportQueue();
                    }
                },
                EnumChatFormatting.GREEN + "Auto-report suggestions",
                EnumChatFormatting.GRAY + "Automatically sends the report command to the server when there is a report suggestion in chat." +
                        EnumChatFormatting.YELLOW + " Only works in Mega Walls after the walls fall.",
                EnumChatFormatting.GRAY + "Ignores command suggestions sent by ignored players, reported players and scangame players." +
                        EnumChatFormatting.GRAY + " You can ignore players by using " + EnumChatFormatting.YELLOW + "/nocheaters ignore <playername>"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(5),
                "Delete old reports",
                (b) -> ConfigHandler.deleteOldReports = b,
                () -> ConfigHandler.deleteOldReports,
                EnumChatFormatting.GRAY + "Deletes reports older than the specified value, the deletion occurs when you start Minecraft."));
        this.buttonList.add(new GuiSlider(7, xPos, getButtonYPos(6), BUTTON_WIDTH, 20, "Delete reports older than : ", " days", 1d, 365d, ConfigHandler.timeDeleteReport, false, true, this));
        this.buttonList.add(new FancyGuiButton(
                xPos, getButtonYPos(7),
                () -> "Censor cheaters in chat : " + (ConfigHandler.deleteCheaterChatMsg ? EnumChatFormatting.GREEN + "Delete" : (ConfigHandler.censorCheaterChatMsg ? EnumChatFormatting.YELLOW + "Censor" : EnumChatFormatting.RED + "Disabled")),
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
                EnumChatFormatting.GREEN + "Censor cheaters in chat",
                EnumChatFormatting.GRAY + "Deletes or censors chat messages sent by reported players"));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(9), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 7) {
            ConfigHandler.timeDeleteReport = (int) slider.getValue();
        }
    }

}
