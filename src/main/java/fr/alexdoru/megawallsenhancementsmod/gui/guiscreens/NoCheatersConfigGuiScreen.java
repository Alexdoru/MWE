package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.WarningMessagesHandler;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoCheatersConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    private GuiButton reportSuggestionButton;
    private GuiButton autoreportSuggestionButton;

    public NoCheatersConfigGuiScreen() {
        this.parent = null;
    }

    public NoCheatersConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        /*
         * Defines the button list
         */
        final int buttonsWidth = 200;
        final int xPos = getxCenter() - buttonsWidth / 2;
        buttonList.add(new GuiButton(1, xPos, getButtonYPos(2), buttonsWidth, ButtonsHeight, getButtonDisplayString(1)));
        buttonList.add(reportSuggestionButton = new GuiButton(8, xPos, getButtonYPos(3), buttonsWidth, ButtonsHeight, getButtonDisplayString(8)));
        buttonList.add(autoreportSuggestionButton = new GuiButton(9, xPos, getButtonYPos(4), buttonsWidth, ButtonsHeight, getButtonDisplayString(9)));
        buttonList.add(new GuiButton(2, xPos, getButtonYPos(5), buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        buttonList.add(new GuiButton(11, xPos, getButtonYPos(6), buttonsWidth, ButtonsHeight, getButtonDisplayString(11)));
        buttonList.add(new GuiButton(6, xPos, getButtonYPos(7), buttonsWidth, ButtonsHeight, getButtonDisplayString(6)));
        buttonList.add(new GuiSlider(7, xPos, getButtonYPos(8), buttonsWidth, 20, "Delete reports older than : ", " days", 1d, 365d, ConfigHandler.timeDeleteReport / (24f * 3600f * 1000f), false, true, this));
        buttonList.add(new GuiButton(10, xPos, getButtonYPos(9), buttonsWidth, ButtonsHeight, getButtonDisplayString(10)));
        buttonList.add(new GuiButton(3, getxCenter() - 150 / 2, getButtonYPos(11), 150, ButtonsHeight, getButtonDisplayString(3)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle(EnumChatFormatting.RED + "NoCheaters", 2, getxCenter(), getButtonYPos(-1));
        final String msg = EnumChatFormatting.WHITE + "NoCheaters saves players reported via " + EnumChatFormatting.YELLOW + "/wdr playername" + EnumChatFormatting.WHITE + " (not /report)";
        drawCenteredString(fontRendererObj, msg, getxCenter(), getButtonYPos(0) + fontRendererObj.FONT_HEIGHT, 0);
        final String msg1 = EnumChatFormatting.WHITE + "If you want to remove a player from your report list use :";
        drawCenteredString(fontRendererObj, msg1, getxCenter(), getButtonYPos(0) + 2 * fontRendererObj.FONT_HEIGHT, 0);
        final String msg2 = EnumChatFormatting.YELLOW + "/unwdr playername" + EnumChatFormatting.WHITE + " or click the name on the warning message";
        drawCenteredString(fontRendererObj, msg2, getxCenter(), getButtonYPos(0) + 3 * fontRendererObj.FONT_HEIGHT, 0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawTooltips(mouseX, mouseY);
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 1:
                return "Warning messages in chat : " + getSuffix(ConfigHandler.warningMessages);
            case 8:
                return "Report suggestions in chat : " + getSuffix(ConfigHandler.reportSuggestions);
            case 9:
                return "Auto-report suggestions : " + getSuffix(ConfigHandler.autoreportSuggestions);
            case 2:
                return "Auto-report cheaters : " + getSuffix(ConfigHandler.toggleAutoreport);
            case 11:
                return "Stop report after a week : " + getSuffix(ConfigHandler.stopAutoreportAfterWeek);
            case 6:
                return "Delete old reports : " + getSuffix(ConfigHandler.deleteOldReports);
            case 10:
                return "Censor cheaters in chat : " + (ConfigHandler.deleteCheaterChatMsg ? EnumChatFormatting.GREEN + "Delete" : (ConfigHandler.censorCheaterChatMsg ? EnumChatFormatting.YELLOW + "Censor" : EnumChatFormatting.RED + "Disabled"));
            case 3:
                return parent == null ? "Close" : "Done";
            default:
                return "invalid button id";
        }
    }

    @Override
    protected List<String> getTooltipText(int id) {
        final List<String> textLines = new ArrayList<>();
        switch (id) {
            case 1:
                textLines.add(EnumChatFormatting.GREEN + "Prints a warning message in chat when a reported player joins your world");
                textLines.add("");
                textLines.add(EnumChatFormatting.RED + "Warning : " + EnumChatFormatting.LIGHT_PURPLE + "player" + EnumChatFormatting.GRAY + " joined, Cheats : " + EnumChatFormatting.GOLD + "cheat");
                textLines.add("");
                textLines.add(EnumChatFormatting.GRAY + "Those messages have built in Compact Chat");
                break;
            case 2:
                textLines.add(EnumChatFormatting.GREEN + "Every game it automatically reports players saved in NoCheaters");
                textLines.add(EnumChatFormatting.GREEN + "It does it for a week before asking you if they are still cheating or not");
                textLines.add(EnumChatFormatting.GRAY + "Only works in Mega Walls, the reports are sent after the walls fall.");
                textLines.add("");
                textLines.add(EnumChatFormatting.DARK_RED + "Don't keep players that don't cheat anymore in your report list");
                textLines.add(EnumChatFormatting.GREEN + "Use : " + EnumChatFormatting.YELLOW + "/unwdr playername" + EnumChatFormatting.GREEN + " to remove them from your report list");
                break;
            case 11:
                textLines.add(EnumChatFormatting.GREEN + "Stop auto-reporting players for whom your last report is more than a week old");
                textLines.add(EnumChatFormatting.GREEN + "The icon on their name will turn gray");
                break;
            case 6:
                textLines.add(EnumChatFormatting.GREEN + "Deletes reports older than the specified value");
                textLines.add(EnumChatFormatting.GRAY + "The deletion occurs when you start minecraft");
                break;
            case 8:
                textLines.add(EnumChatFormatting.GREEN + "When there is a message that respects the following patterns,");
                textLines.add(EnumChatFormatting.GREEN + "it will print a report suggestion in chat");
                textLines.add("");
                textLines.add(EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "playername is bhoping");
                textLines.add(EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "wdr playername cheat");
                textLines.add(EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "report playername cheat");
                break;
            case 9:
                textLines.add(EnumChatFormatting.GREEN + "Automatically sends the report command to the server");
                textLines.add(EnumChatFormatting.GREEN + "when there is a report suggestion in chat");
                textLines.add(EnumChatFormatting.GRAY + "Only works in Mega Walls after the walls fall");
                textLines.add(EnumChatFormatting.GRAY + "Ignores command suggestions sent by ignored players, reported players, scangame players and nicked players");
                textLines.add(EnumChatFormatting.GRAY + "You can ignore players by using " + EnumChatFormatting.YELLOW + "/nocheaters ignore <playername>");
                break;
            case 10:
                textLines.add(EnumChatFormatting.GREEN + "Deletes or censors chat messages sent by reported players");
                break;
        }
        return textLines;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                ConfigHandler.warningMessages = !ConfigHandler.warningMessages;
                if (ConfigHandler.warningMessages) {
                    WarningMessagesHandler.printReportMessagesForWorld(false);
                } else {
                    ChatHandler.deleteAllWarningMessages();
                }
                break;
            case 8:
                ConfigHandler.reportSuggestions = !ConfigHandler.reportSuggestions;
                if (ConfigHandler.reportSuggestions) {
                    SoundUtil.playReportSuggestionSound();
                }
                if (!ConfigHandler.reportSuggestions) {
                    ConfigHandler.autoreportSuggestions = false;
                    autoreportSuggestionButton.displayString = getButtonDisplayString(9);
                }
                break;
            case 9:
                ConfigHandler.autoreportSuggestions = !ConfigHandler.autoreportSuggestions;
                if (ConfigHandler.autoreportSuggestions) {
                    ConfigHandler.reportSuggestions = true;
                    reportSuggestionButton.displayString = getButtonDisplayString(8);
                } else {
                    ReportQueue.INSTANCE.clearSuggestionsInReportQueue();
                }
                break;
            case 10:
                if (ConfigHandler.censorCheaterChatMsg && !ConfigHandler.deleteCheaterChatMsg) {
                    ConfigHandler.deleteCheaterChatMsg = true;
                    break;
                }
                if (!ConfigHandler.censorCheaterChatMsg && !ConfigHandler.deleteCheaterChatMsg) {
                    ConfigHandler.censorCheaterChatMsg = true;
                    break;
                }
                ConfigHandler.deleteCheaterChatMsg = false;
                ConfigHandler.censorCheaterChatMsg = false;
                break;
            case 2:
                ConfigHandler.toggleAutoreport = !ConfigHandler.toggleAutoreport;
                NameUtil.refreshAllNamesInWorld();
                break;
            case 3:
                mc.displayGuiScreen(parent);
                break;
            case 6:
                ConfigHandler.deleteOldReports = !ConfigHandler.deleteOldReports;
                break;
            case 11:
                ConfigHandler.stopAutoreportAfterWeek = !ConfigHandler.stopAutoreportAfterWeek;
                NameUtil.refreshAllNamesInWorld();
                break;
            default:
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.id == 7) {
            ConfigHandler.timeDeleteReport = 24L * 3600L * 1000L * ((long) slider.getValue());
        }
    }

}
