package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.HUDSettingGuiButtons;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.OptionGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.PendingReportHUD;
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
        this.maxHeight = (buttonsHeight + 4) * 11 + buttonsHeight;
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
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(5),
                () -> {
                    if (ConfigHandler.showReportHUDonlyInChat) {
                        return "Reports HUD : " + EnumChatFormatting.YELLOW + "Only in chat";
                    }
                    return "Reports HUD : " + getSuffix(ConfigHandler.showReportHUD);
                },
                () -> {
                    if (ConfigHandler.showReportHUD && !ConfigHandler.showReportHUDonlyInChat) {
                        ConfigHandler.showReportHUDonlyInChat = true;
                        return;
                    }
                    if (!ConfigHandler.showReportHUD && !ConfigHandler.showReportHUDonlyInChat) {
                        ConfigHandler.showReportHUD = true;
                        return;
                    }
                    ConfigHandler.showReportHUD = false;
                    ConfigHandler.showReportHUDonlyInChat = false;
                },
                ConfigHandler.reportHUDPosition,
                PendingReportHUD.instance,
                this,
                EnumChatFormatting.GREEN + "Pending reports HUD",
                EnumChatFormatting.DARK_GRAY + "\u25AA " + EnumChatFormatting.GREEN + "Enabled" + EnumChatFormatting.GRAY + " : displays a small text when the mods has reports to send to the server, and when it's typing the reports",
                EnumChatFormatting.DARK_GRAY + "\u25AA " + EnumChatFormatting.YELLOW + "Only in chat" + EnumChatFormatting.GRAY + " : only show when typing the report")
                .accept(this.buttonList);
        this.buttonList.add(new OptionGuiButton(
                xPos + BUTTON_WIDTH + 4, getButtonYPos(4),
                sideButtonWidth, 20,
                "Debug",
                (b) -> ConfigHandler.debugLogging = b,
                () -> ConfigHandler.debugLogging,
                EnumChatFormatting.GRAY + "Logs every hacker detector related action in .minecraft/logs/HackerDetector.log"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(6),
                "Show flag messages",
                (b) -> ConfigHandler.showFlagMessages = b,
                () -> ConfigHandler.showFlagMessages,
                EnumChatFormatting.GRAY + "Prints a message in chat when it detects a player using cheats"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(7),
                "Compact flag messages",
                (b) -> ConfigHandler.compactFlagMessages = b,
                () -> ConfigHandler.compactFlagMessages,
                EnumChatFormatting.GRAY + "Compacts identical flag messages together"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(8),
                "Show single flag message",
                (b) -> ConfigHandler.oneFlagMessagePerGame = b,
                () -> ConfigHandler.oneFlagMessagePerGame,
                EnumChatFormatting.GRAY + "Print flag messages only once per game per player"));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(10), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

}
