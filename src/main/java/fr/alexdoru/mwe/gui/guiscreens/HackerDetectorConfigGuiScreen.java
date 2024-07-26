package fr.alexdoru.mwe.gui.guiscreens;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.gui.elements.HUDSettingGuiButtons;
import fr.alexdoru.mwe.gui.elements.OptionGuiButton;
import fr.alexdoru.mwe.gui.elements.SimpleGuiButton;
import fr.alexdoru.mwe.gui.elements.TextElement;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
import fr.alexdoru.mwe.nocheaters.ReportQueue;
import net.minecraft.client.gui.GuiScreen;

import static net.minecraft.util.EnumChatFormatting.*;

public class HackerDetectorConfigGuiScreen extends MyGuiScreen {

    public HackerDetectorConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final int sideButtonWidth = 90;
        this.maxWidth = BUTTON_WIDTH + (10 + sideButtonWidth) * 2;
        this.maxHeight = (buttonsHeight + 4) * 14 + buttonsHeight;
        super.initGui();
        final int xPos = getxCenter() - BUTTON_WIDTH / 2;
        this.elementList.add(new TextElement(DARK_RED + "Hacker Detector", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.elementList.add(new TextElement(WHITE + "Disclaimer : this is not 100% accurate and can sometimes flag legit players,", getxCenter(), getButtonYPos(0)).makeCentered());
        this.elementList.add(new TextElement(WHITE + "it won't flag every cheater either, however players that", getxCenter(), getButtonYPos(0) + fontRendererObj.FONT_HEIGHT).makeCentered());
        this.elementList.add(new TextElement(WHITE + "are regularly flagging are definitely cheating", getxCenter(), getButtonYPos(0) + 2 * fontRendererObj.FONT_HEIGHT).makeCentered());
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(2),
                "Hacker Detector",
                (b) -> MWEConfig.hackerDetector = b,
                () -> MWEConfig.hackerDetector,
                GRAY + "Analyses movements and actions of players around you and gives a warning message if they are cheating"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(3),
                "Save in NoCheaters",
                (b) -> MWEConfig.addToReportList = b,
                () -> MWEConfig.addToReportList,
                GRAY + "Saves flagged players in NoCheaters to get warnings about them"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(4),
                "Auto-report cheaters",
                (b) -> {
                    MWEConfig.autoreportFlaggedPlayers = b;
                    if (!MWEConfig.autoreportFlaggedPlayers) {
                        ReportQueue.INSTANCE.queueList.clear();
                    }
                },
                () -> MWEConfig.autoreportFlaggedPlayers,
                GRAY + "Sends a /report automatically to Hypixel when it flags a cheater",
                YELLOW + "Only works in Mega Walls, sends one report per game per player, you need to stand still for the mod to type the report." +
                        " It will not send the report if you wait more than 30 seconds to send it."));
        this.buttonList.add(new OptionGuiButton(
                xPos + BUTTON_WIDTH + 4, getButtonYPos(4),
                sideButtonWidth, 20,
                "Debug",
                (b) -> MWEConfig.debugLogging = b,
                () -> MWEConfig.debugLogging,
                GRAY + "Logs every hacker detector related action in .minecraft/logs/HackerDetector.log"));
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(5),
                () -> {
                    if (MWEConfig.showReportHUDonlyInChat) {
                        return "Reports HUD : " + YELLOW + "Only in chat";
                    }
                    return "Reports HUD : " + getSuffix(MWEConfig.showReportHUD);
                },
                () -> {
                    if (MWEConfig.showReportHUD && !MWEConfig.showReportHUDonlyInChat) {
                        MWEConfig.showReportHUDonlyInChat = true;
                        return;
                    }
                    if (!MWEConfig.showReportHUD && !MWEConfig.showReportHUDonlyInChat) {
                        MWEConfig.showReportHUD = true;
                        return;
                    }
                    MWEConfig.showReportHUD = false;
                    MWEConfig.showReportHUDonlyInChat = false;
                },
                GuiManager.pendingReportHUD,
                this,
                GREEN + "Pending reports HUD",
                DARK_GRAY + "▪ " + GREEN + "Enabled" + GRAY + " : displays a small text when the mod has reports to send to the server, and when it's typing the report",
                DARK_GRAY + "▪ " + YELLOW + "Only in chat" + GRAY + " : only show when typing the report")
                .accept(this.buttonList);
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(6),
                "Sound when flagging",
                (b) -> MWEConfig.soundWhenFlagging = b,
                () -> MWEConfig.soundWhenFlagging,
                GRAY + "Plays a sound when it flags a player"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(7),
                "Show flag messages",
                (b) -> MWEConfig.showFlagMessages = b,
                () -> MWEConfig.showFlagMessages,
                GRAY + "Prints a message in chat when it detects a player using cheats"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(8),
                "Compact flags in chat",
                (b) -> MWEConfig.compactFlagMessages = b,
                () -> MWEConfig.compactFlagMessages,
                GRAY + "Deletes previous flag message when printing a new identical flag message"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(9),
                "Show single flag message",
                (b) -> MWEConfig.oneFlagMessagePerGame = b,
                () -> MWEConfig.oneFlagMessagePerGame,
                GRAY + "Prints flag messages only once per game per player"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(10),
                "Show flag type",
                (b) -> MWEConfig.showFlagMessageType = b,
                () -> MWEConfig.showFlagMessageType,
                GRAY + "Shows the flag type on the flag message. For example it will show : ",
                RED + "Player" + YELLOW + " flags " + RED + "\"KillAura(A)\"",
                RED + "Player" + YELLOW + " flags " + RED + "\"KillAura(B)\"",
                GRAY + " instead of : ",
                RED + "Player" + YELLOW + " flags " + RED + "\"KillAura\""));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(11),
                "Show report button on flags",
                (b) -> MWEConfig.showReportButtonOnFlags = b,
                () -> MWEConfig.showReportButtonOnFlags,
                GRAY + "Shows the report button on flag messages"));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(13), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

}
