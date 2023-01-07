package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HackerDetectorConfigGuiScreen extends MyGuiScreen {

    private final String msg = EnumChatFormatting.WHITE + "Disclaimer : this is not 100% accurate and can sometimes flag legit players,";

    public HackerDetectorConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final int buttonsWidth = 200;
        this.maxWidth = fontRendererObj.getStringWidth(msg);
        this.maxHeight = (buttonsHeight + 4) * 10 + buttonsHeight;
        super.initGui();
        final int xPos = getxCenter() - buttonsWidth / 2;
        this.buttonList.add(new GuiButton(0, xPos, getButtonYPos(2), buttonsWidth, buttonsHeight, getButtonDisplayString(0)));
        this.buttonList.add(new GuiButton(1, xPos, getButtonYPos(3), buttonsWidth, buttonsHeight, getButtonDisplayString(1)));
        this.buttonList.add(new GuiButton(2, xPos, getButtonYPos(4), buttonsWidth, buttonsHeight, getButtonDisplayString(2)));
        this.buttonList.add(new GuiButton(3, xPos, getButtonYPos(5), buttonsWidth, buttonsHeight, getButtonDisplayString(3)));
        this.buttonList.add(new GuiButton(4, xPos, getButtonYPos(6), buttonsWidth, buttonsHeight, getButtonDisplayString(4)));
        this.buttonList.add(new GuiButton(5, xPos, getButtonYPos(7), buttonsWidth, buttonsHeight, getButtonDisplayString(5)));
        this.buttonList.add(new GuiButton(6, xPos, getButtonYPos(9), buttonsWidth, buttonsHeight, getButtonDisplayString(6)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawCenteredTitle(EnumChatFormatting.DARK_RED + "Hacker Detector", 2, getxCenter(), getButtonYPos(-1));
        drawCenteredString(fontRendererObj, msg, getxCenter(), getButtonYPos(0), 0);
        final String msg0 = EnumChatFormatting.WHITE + "it won't flag every cheater either, however players that";
        drawCenteredString(fontRendererObj, msg0, getxCenter(), getButtonYPos(0) + fontRendererObj.FONT_HEIGHT, 0);
        final String msg1 = EnumChatFormatting.WHITE + "are regularly flagging are definitely cheating";
        drawCenteredString(fontRendererObj, msg1, getxCenter(), getButtonYPos(0) + 2 * fontRendererObj.FONT_HEIGHT, 0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawTooltips(mouseX, mouseY);
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 0:
                return "Hacker Detector : " + getSuffix(ConfigHandler.hackerDetector);
            case 1:
                return "Save in NoCheaters : " + getSuffix(ConfigHandler.addToReportList);
            case 2:
                return "Report Flagged players : " + getSuffix(ConfigHandler.autoreportFlaggedPlayers);
            case 3:
                return "Show flag messages : " + getSuffix(ConfigHandler.showFlagMessages);
            case 4:
                return "Compact flag messages : " + getSuffix(ConfigHandler.compactFlagMessages);
            case 5:
                return "Show single flag message : " + getSuffix(ConfigHandler.oneFlagMessagePerGame);
            case 6:
                return parent == null ? "Close" : "Done";
            default:
                return "invalid button id";
        }
    }

    @Override
    protected List<String> getTooltipText(int id) {
        final List<String> textLines = new ArrayList<>();
        switch (id) {
            case 0:
                textLines.add(EnumChatFormatting.GREEN + "Analyses movements and actions of players in your game and tells you if they are cheating");
                textLines.add("");
                textLines.add(EnumChatFormatting.GREEN + "Currently has checks for : " + EnumChatFormatting.YELLOW + "Autoblock, Fastbreak, Keepsprint, Noslowdown");
                break;
            case 1:
                textLines.add(EnumChatFormatting.GREEN + "Saves flagged players in NoCheaters");
                break;
            case 2:
                textLines.add(EnumChatFormatting.GREEN + "Sends a report automatically to Hypixel when it flags a cheater");
                textLines.add(EnumChatFormatting.GRAY + "Only works in Mega Walls, sends one report per game per player");
                break;
            case 3:
                textLines.add(EnumChatFormatting.GREEN + "Prints a message in chat when it detects a player using cheats");
                break;
            case 4:
                textLines.add(EnumChatFormatting.GREEN + "Compacts identical flag messages together");
                break;
            case 5:
                textLines.add(EnumChatFormatting.GREEN + "Print flag messages only once per game per player");
                break;
        }
        return textLines;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                ConfigHandler.hackerDetector = !ConfigHandler.hackerDetector;
                break;
            case 1:
                ConfigHandler.addToReportList = !ConfigHandler.addToReportList;
                break;
            case 2:
                ConfigHandler.autoreportFlaggedPlayers = !ConfigHandler.autoreportFlaggedPlayers;
                break;
            case 3:
                ConfigHandler.showFlagMessages = !ConfigHandler.showFlagMessages;
                break;
            case 4:
                ConfigHandler.compactFlagMessages = !ConfigHandler.compactFlagMessages;
                break;
            case 5:
                ConfigHandler.oneFlagMessagePerGame = !ConfigHandler.oneFlagMessagePerGame;
                break;
            case 6:
                mc.displayGuiScreen(parent);
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);
    }

}
