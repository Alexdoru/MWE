package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.nocheatersmod.NoCheatersMod;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoCheatersConfigGuiScreen extends MyGuiScreen implements GuiSlider.ISlider {

    private final int ButtonsHeight = 20;
    private final GuiScreen parent;

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
        this.buttonList.add(new GuiButton(1, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4) * 3, buttonsWidth, ButtonsHeight, getButtonDisplayString(1)));
        this.buttonList.add(new GuiButton(2, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4) * 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        this.buttonList.add(new GuiSlider(4, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4), buttonsWidth, 20, "Time between reports : ", " hours", 6d, 48d, ConfigHandler.timeBetweenReports / 3600000f, false, true, this));
        this.buttonList.add(new GuiSlider(5, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2, buttonsWidth, 20, "Time max autoreport : ", " days", 1d, 30d, ConfigHandler.timeAutoReport / (24f * 3600f * 1000f), false, true, this));
        this.buttonList.add(new GuiButton(6, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, getButtonDisplayString(6)));
        this.buttonList.add(new GuiSlider(7, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 2, buttonsWidth, 20, "Delete reports older than : ", " days", 1d, 365d, ConfigHandler.timeDeleteReport / (24f * 3600f * 1000f), false, true, this));
        this.buttonList.add(new GuiButton(3, getxCenter() - 150 / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 4, 150, ButtonsHeight, getButtonDisplayString(3)));
        super.initGui();
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 1:
                return "Warning messages in chat : " + getSuffix(ConfigHandler.togglewarnings);
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
                textLines.add(EnumChatFormatting.GREEN + "Automatically reports cheaters whose last report is older than the time between report and more recent than the time max autoreport");
                break;
            case 6:
                textLines.add(EnumChatFormatting.GREEN + "Deletes reports older than the specified value when the game starts");
                break;
        }
        return textLines;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                ConfigHandler.togglewarnings = !ConfigHandler.togglewarnings;
                if (ConfigHandler.togglewarnings) {
                    NoCheatersEvents.scanCurrentWorld();
                }
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
        drawCenteredTitle("NoCheaters v" + NoCheatersMod.version, 2, (width / 2.0f), getyCenter() - (ButtonsHeight + 4) * 6);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawTooltips(mouseX, mouseY);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        switch (slider.id) {
            case 4:
                ConfigHandler.timeBetweenReports = Math.max(6L * 3600L * 1000L, Math.min(3600L * 1000L * ((long) slider.getValue()), 48L * 3600L * 1000L));
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
