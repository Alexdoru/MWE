package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.OptionGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

public class HackerDetectorConfigGuiScreen extends MyGuiScreen {

    public HackerDetectorConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final int sideButtonWidth = 90;
        this.maxWidth = BUTTON_WIDTH + (10 + sideButtonWidth) * 2;
        this.maxHeight = (buttonsHeight + 4) * 10 + buttonsHeight;
        super.initGui();
        final int xPos = getxCenter() - BUTTON_WIDTH / 2;
        this.elementList.add(new TextElement(EnumChatFormatting.DARK_RED + "Hacker Detector", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.elementList.add(new TextElement(EnumChatFormatting.WHITE + "Disclaimer : this is not 100% accurate and can sometimes flag legit players,", getxCenter(), getButtonYPos(0)).makeCentered());
        this.elementList.add(new TextElement(EnumChatFormatting.WHITE + "it won't flag every cheater either, however players that", getxCenter(), getButtonYPos(0) + fontRendererObj.FONT_HEIGHT).makeCentered());
        this.elementList.add(new TextElement(EnumChatFormatting.WHITE + "are regularly flagging are definitely cheating", getxCenter(), getButtonYPos(0) + 2 * fontRendererObj.FONT_HEIGHT).makeCentered());
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(2),
                "Hacker Detector",
                (b) -> ConfigHandler.hackerDetector = b,
                () -> ConfigHandler.hackerDetector,
                EnumChatFormatting.GRAY + "Analyses movements and actions of players in your game and tells you if they are cheating. Currently has checks for : Autoblock, Fastbreak, Keepsprint, Noslowdown"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(3),
                "Save in NoCheaters",
                (b) -> ConfigHandler.addToReportList = b,
                () -> ConfigHandler.addToReportList,
                EnumChatFormatting.GRAY + "Saves flagged players in NoCheaters"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(4),
                "Report Flagged players",
                (b) -> ConfigHandler.autoreportFlaggedPlayers = b,
                () -> ConfigHandler.autoreportFlaggedPlayers,
                EnumChatFormatting.GRAY + "Sends a report automatically to Hypixel when it flags a cheater",
                EnumChatFormatting.YELLOW + "Only works in Mega Walls, sends one report per game per player"));
        this.buttonList.add(new OptionGuiButton(
                xPos + BUTTON_WIDTH + 10, getButtonYPos(4),
                sideButtonWidth, 20,
                "Debug",
                (b) -> ConfigHandler.debugLogging = b,
                () -> ConfigHandler.debugLogging,
                EnumChatFormatting.GRAY + "Logs every hacker detector related action in the .minecraft/logs/fml-client-latest.log"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(5),
                "Show flag messages",
                (b) -> ConfigHandler.showFlagMessages = b,
                () -> ConfigHandler.showFlagMessages,
                EnumChatFormatting.GRAY + "Prints a message in chat when it detects a player using cheats"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(6),
                "Compact flag messages",
                (b) -> ConfigHandler.compactFlagMessages = b,
                () -> ConfigHandler.compactFlagMessages,
                EnumChatFormatting.GRAY + "Compacts identical flag messages together"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(7),
                "Show single flag message",
                (b) -> ConfigHandler.oneFlagMessagePerGame = b,
                () -> ConfigHandler.oneFlagMessagePerGame,
                EnumChatFormatting.GRAY + "Print flag messages only once per game per player"));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(9), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

}
