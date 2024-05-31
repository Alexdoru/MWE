package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.HUDSettingGuiButtons;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.OptionGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.PendingReportHUD;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
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
        this.maxHeight = (buttonsHeight + 4) * 12 + buttonsHeight;
        super.initGui();
        final int xPos = getxCenter() - BUTTON_WIDTH / 2;
        this.elementList.add(new TextElement(DARK_RED + "Hacker Detector", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.elementList.add(new TextElement(WHITE + "Disclaimer : this is not 100% accurate and can sometimes flag legit players,", getxCenter(), getButtonYPos(0)).makeCentered());
        this.elementList.add(new TextElement(WHITE + "it won't flag every cheater either, however players that", getxCenter(), getButtonYPos(0) + fontRendererObj.FONT_HEIGHT).makeCentered());
        this.elementList.add(new TextElement(WHITE + "are regularly flagging are definitely cheating", getxCenter(), getButtonYPos(0) + 2 * fontRendererObj.FONT_HEIGHT).makeCentered());
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(2),
                "Hacker Detector",
                (b) -> ConfigHandler.hackerDetector = b,
                () -> ConfigHandler.hackerDetector,
                GRAY + "Analyses movements and actions of players around you and gives a warning message if they are cheating."));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(3),
                "Save in NoCheaters",
                (b) -> ConfigHandler.addToReportList = b,
                () -> ConfigHandler.addToReportList,
                GRAY + "Saves flagged players in NoCheaters"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(4),
                "Auto-report cheaters",
                (b) -> {
                    ConfigHandler.autoreportFlaggedPlayers = b;
                    if (!ConfigHandler.autoreportFlaggedPlayers) {
                        ReportQueue.INSTANCE.queueList.clear();
                    }
                },
                () -> ConfigHandler.autoreportFlaggedPlayers,
                GRAY + "Sends a /report automatically to Hypixel when it flags a cheater",
                YELLOW + "Only works in Mega Walls, sends one report per game per player. It will abort the report after 30 seconds if you wait too long to send it."));
        new HUDSettingGuiButtons(
                getxCenter(), getButtonYPos(5),
                () -> {
                    if (ConfigHandler.showReportHUDonlyInChat) {
                        return "Reports HUD : " + YELLOW + "Only in chat";
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
                PendingReportHUD.INSTANCE,
                this,
                GREEN + "Pending reports HUD",
                DARK_GRAY + "▪ " + GREEN + "Enabled" + GRAY + " : displays a small text when the mods has reports to send to the server, and when it's typing the reports",
                DARK_GRAY + "▪ " + YELLOW + "Only in chat" + GRAY + " : only show when typing the report")
                .accept(this.buttonList);
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(6),
                "Show flag messages",
                (b) -> ConfigHandler.showFlagMessages = b,
                () -> ConfigHandler.showFlagMessages,
                GRAY + "Prints a message in chat when it detects a player using cheats"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(7),
                "Show flag type",
                (b) -> ConfigHandler.showFlagMessageType = b,
                () -> ConfigHandler.showFlagMessageType,
                GRAY + "Shows on the flag message the flag type. For example it will show \"KillAura(B)\" instead of just \"KillAura\""));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(8),
                "Compact flag messages",
                (b) -> ConfigHandler.compactFlagMessages = b,
                () -> ConfigHandler.compactFlagMessages,
                GRAY + "Compacts identical flag messages together"));
        this.buttonList.add(new OptionGuiButton(
                xPos, getButtonYPos(9),
                "Show single flag message",
                (b) -> ConfigHandler.oneFlagMessagePerGame = b,
                () -> ConfigHandler.oneFlagMessagePerGame,
                GRAY + "Print flag messages only once per game per player"));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(11), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

}
