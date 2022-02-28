package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.ChatEvents;
import fr.alexdoru.nocheatersmod.NoCheatersMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoCheatersConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    public NoCheatersConfigGuiScreen() {
        this.parent = null;
    }

    public NoCheatersConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        /*
         * Defines the button list
         */
        int buttonsWidth = 200;
        int xPos = getxCenter() - buttonsWidth / 2;
        buttonList.add(new GuiButton(1, xPos, getYposForButton(-4), buttonsWidth, ButtonsHeight, getButtonDisplayString(1)));
        buttonList.add(new GuiButton(8, xPos, getYposForButton(-3), buttonsWidth, ButtonsHeight, getButtonDisplayString(8)));
        buttonList.add(new GuiButton(9, xPos, getYposForButton(-2), buttonsWidth, ButtonsHeight, getButtonDisplayString(9)));
        buttonList.add(new GuiButton(2, xPos, getYposForButton(-1), buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        buttonList.add(new GuiSlider(4, xPos, getYposForButton(0), buttonsWidth, 20, "Time between reports : ", " hours", 0.75d, 24d, ConfigHandler.timeBetweenReports / 3600000f, false, true, this));
        buttonList.add(new GuiSlider(5, xPos, getYposForButton(1), buttonsWidth, 20, "Time max autoreport : ", " days", 1d, 30d, ConfigHandler.timeAutoReport / (24f * 3600f * 1000f), false, true, this));
        buttonList.add(new GuiButton(6, xPos, getYposForButton(2), buttonsWidth, ButtonsHeight, getButtonDisplayString(6)));
        buttonList.add(new GuiSlider(7, xPos, getYposForButton(3), buttonsWidth, 20, "Delete reports older than : ", " days", 1d, 365d, ConfigHandler.timeDeleteReport / (24f * 3600f * 1000f), false, true, this));
        buttonList.add(new GuiButton(3, getxCenter() - 150 / 2, getYposForButton(5), 150, ButtonsHeight, getButtonDisplayString(3)));
        super.initGui();
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 1:
                return "Warning messages in chat : " + getSuffix(ConfigHandler.togglewarnings);
            case 8:
                return "Report suggestions in chat : " + getSuffix(ConfigHandler.reportsuggestions);
            case 9:
                return "Auto-send suggestions : " + getSuffix(ConfigHandler.autoreportSuggestions);
            case 2:
                return "Autoreport cheaters : " + getSuffix(ConfigHandler.toggleautoreport);
            case 6:
                return "Delete old reports : " + getSuffix(ConfigHandler.deleteReports);
            case 3:
                return parent == null ? "Close" : "Done";
            default:
                return "invalid button id";
        }
    }

    @Override
    public List<String> getTooltipText(int id) {
        List<String> textLines = new ArrayList<>();
        switch (id) {
            case 2:
                textLines.add(EnumChatFormatting.GREEN + "Automatically sends a report for players saved in NoCheaters");
                textLines.add(EnumChatFormatting.GREEN + "Only works in Mega Walls, the reports are sent after the walls fall");
                textLines.add(EnumChatFormatting.GREEN + "It sends a report for players whose last report is older than the time between report and more recent than the time max autoreport");
                break;
            case 6:
                textLines.add(EnumChatFormatting.GREEN + "Deletes reports older than the specified value");
                textLines.add(EnumChatFormatting.GREEN + "The deletion occurs when you start minecraft");
                break;
            case 8:
                textLines.add(EnumChatFormatting.GREEN + "When there is a message that respects the following patterns it will print a report suggestion in chat");
                textLines.add(EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "playername is bhoping");
                textLines.add(EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "wdr playername cheat");
                textLines.add(EnumChatFormatting.BLUE + "[TEAM] " + EnumChatFormatting.GREEN + "Player: " + EnumChatFormatting.WHITE + "report playername cheat");
                break;
            case 9:
                textLines.add(EnumChatFormatting.GREEN + "Automatically sends the report command to the server when there is a report suggestion in chat");
                break;
        }
        return textLines;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                ConfigHandler.togglewarnings = !ConfigHandler.togglewarnings;
                break;
            case 8:
                if (!ConfigHandler.reportsuggestions) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(ChatEvents.reportSuggestionSound, 1.0F));
                }
                ConfigHandler.reportsuggestions = !ConfigHandler.reportsuggestions;
                break;
            case 9:
                ConfigHandler.autoreportSuggestions = !ConfigHandler.autoreportSuggestions;
                break;
            case 2:
                ConfigHandler.toggleautoreport = !ConfigHandler.toggleautoreport;
                break;
            case 3:
                mc.displayGuiScreen(parent);
                break;
            case 6:
                ConfigHandler.deleteReports = !ConfigHandler.deleteReports;
                break;
            default:
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle("NoCheaters v" + NoCheatersMod.version, 2, (width / 2.0f), getYposForButton(-6), Integer.parseInt("FF5555", 16));
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawTooltips(mouseX, mouseY);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        switch (slider.id) {
            case 4:
                ConfigHandler.timeBetweenReports = Math.max(2700000L, Math.min(3600L * 1000L * ((long) slider.getValue()), 24L * 3600L * 1000L));
                break;
            case 5:
                ConfigHandler.timeAutoReport = Math.max(24L * 3600L * 1000L, Math.min(24L * 3600L * 1000L * ((long) slider.getValue()), 30L * 24L * 3600L * 1000L));
                break;
            case 7:
                ConfigHandler.timeDeleteReport = 24L * 3600L * 1000L * ((long) slider.getValue());
                break;
        }
    }

}
