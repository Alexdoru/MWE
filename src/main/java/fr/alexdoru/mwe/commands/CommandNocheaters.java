package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.gui.huds.DebugScoreboardHUD;
import fr.alexdoru.mwe.http.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.hypixelplayerdataparser.LoginData;
import fr.alexdoru.mwe.http.requests.HypixelPlayerData;
import fr.alexdoru.mwe.http.requests.MojangUUIDToName;
import fr.alexdoru.mwe.nocheaters.WDR;
import fr.alexdoru.mwe.nocheaters.WarningMessages;
import fr.alexdoru.mwe.nocheaters.WdrData;
import fr.alexdoru.mwe.scoreboard.ScoreboardUtils;
import fr.alexdoru.mwe.utils.DateUtil;
import fr.alexdoru.mwe.utils.MapUtil;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static net.minecraft.util.EnumChatFormatting.*;

public class CommandNocheaters extends MyAbstractCommand {

    private DebugScoreboardHUD debugHUD;

    @Override
    public String getCommandName() {
        return "nocheaters";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length == 0) {
            WarningMessages.printReportMessagesForWorld(true);
            return;
        }

        if (args[0].equalsIgnoreCase("reportlist")) {

            printReportList(args);

        } else if (args[0].equalsIgnoreCase("getscoreboard")) {

            ScoreboardUtils.printScoreboard();

        } else if (args[0].equalsIgnoreCase("debugscoreboard")) {

            debugScoreboard();

        } else {

            this.printCommandHelp();

        }

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final String[] arguments = {"help", "reportlist"};
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, arguments);
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("log")) {
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
        }
        return null;
    }

    @Override
    protected void printCommandHelp() {
        ChatUtil.addChatMessage(
                RED + ChatUtil.bar() + "\n"
                        + ChatUtil.centerLine(GOLD + "NoCheaters Help\n\n")
                        + YELLOW + getCommandUsage(null) + GRAY + " - " + AQUA + "prints the list of reported players in your current world\n"
                        + YELLOW + getCommandUsage(null) + " reportlist" + GRAY + " - " + AQUA + "prints the list of reported players\n"
                        + RED + ChatUtil.bar()
        );
    }

    private void printReportList(String[] args) {

        final int displaypage;
        if (args.length > 1) {
            try {
                displaypage = parseInt(args[1]);
            } catch (NumberInvalidException e) {
                ChatUtil.addChatMessage(RED + "Not a valid page number");
                return;
            }
        } else {
            displaypage = 1;
        }

        final Map<Object, WDR> sortedMap = MapUtil.sortByDecreasingValue(WdrData.getAllWDRs());
        final List<Future<IChatComponent>> futureList = new ArrayList<>();
        int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
        int nbpage = 1;
        boolean warning = true;
        final boolean doStalk = !HypixelApiKeyUtil.apiKeyIsNotSetup();

        for (final Map.Entry<Object, WDR> entry : sortedMap.entrySet()) {
            if (nbreport == 11) {
                nbreport = 1;
                nbpage++;
            }
            if (nbpage == displaypage) {
                warning = false;
                futureList.add(MultithreadingUtil.addTaskToQueue(new CreateReportLineTask(entry.getKey(), entry.getValue(), doStalk)));
            }
            nbreport++;
        }

        if (sortedMap.isEmpty()) {
            ChatUtil.addChatMessage(GREEN + "You have no one reported!");
            return;
        }

        if (warning) {
            ChatUtil.addChatMessage(RED + "No reports to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available.");
            return;
        }

        final int finalNbpage = nbpage;
        MultithreadingUtil.addTaskToQueue(() -> {
            final IChatComponent imsgbody = new ChatComponentText("");
            for (final Future<IChatComponent> future : futureList) {
                imsgbody.appendSibling(future.get()).appendText("\n");
            }
            ChatUtil.printIChatList(
                    "Report list",
                    imsgbody,
                    displaypage,
                    finalNbpage,
                    getCommandUsage(null) + " reportlist",
                    RED,
                    null,
                    null
            );
            return null;
        });
    }

    private void debugScoreboard() {
        if (this.debugHUD == null) {
            this.debugHUD = new DebugScoreboardHUD();
            MinecraftForge.EVENT_BUS.register(this.debugHUD);
        } else {
            MinecraftForge.EVENT_BUS.unregister(this.debugHUD);
            this.debugHUD = null;
        }
    }

}

class CreateReportLineTask implements Callable<IChatComponent> {

    private final UUID uuid;
    private final String nickname;
    private final WDR wdr;
    private final boolean doStalk;

    public CreateReportLineTask(Object mapKey, WDR wdr, boolean doStalk) {
        if (mapKey instanceof UUID) {
            this.uuid = (UUID) mapKey;
            this.nickname = null;
        } else if (mapKey instanceof String) {
            this.uuid = null;
            this.nickname = (String) mapKey;
        } else throw new IllegalArgumentException();
        this.wdr = wdr;
        this.doStalk = doStalk;
    }

    @Override
    public IChatComponent call() {

        try {

            if (uuid == null) {
                return WarningMessages.getPlayernameWithHoverText(DARK_PURPLE + "[Nick] " + GOLD + nickname, null, nickname, nickname, wdr)
                        .appendText(GRAY + " reported : " + YELLOW + DateUtil.timeSince(wdr.getTimestamp()));
            } else if (!doStalk) {
                final String name = MojangUUIDToName.getName(uuid);
                return WarningMessages.getPlayernameWithHoverText(RED + name, null, name, uuid.toString(), wdr)
                        .appendText(GRAY + " reported : " + YELLOW + DateUtil.timeSince(wdr.getTimestamp()));
            }

            final HypixelPlayerData playerdata = new HypixelPlayerData(uuid);
            final LoginData logindata = new LoginData(playerdata.getPlayerData());
            final IChatComponent imsg = WarningMessages.getPlayernameWithHoverText(logindata.getFormattedName(), null, logindata.getdisplayname(), uuid.toString(), wdr);

            final IChatComponent ismgStatus = new ChatComponentText("");

            if (logindata.isHidingFromAPI()) {

                logindata.parseLatestActivity(playerdata.getPlayerData());
                final long latestActivityTime = logindata.getLatestActivityTime();
                final String latestActivity = logindata.getLatestActivity();
                final boolean isProbBanned = isProbBanned(latestActivityTime);
                ismgStatus.appendText(GRAY + " " + latestActivity + " " + (isProbBanned ? DARK_GRAY : YELLOW) + DateUtil.timeSince(latestActivityTime));

            } else if (logindata.isOnline()) { // player is online

                ismgStatus.appendText(GREEN + "    Online");

            } else { // print lastlogout

                final boolean isProbBanned = isProbBanned(logindata.getLastLogout());
                ismgStatus.appendText(GRAY + " Lastlogout " + (isProbBanned ? DARK_GRAY : YELLOW) + DateUtil.timeSince(logindata.getLastLogout()));

            }

            ismgStatus.setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(YELLOW + "Click to run : /stalk " + logindata.getdisplayname())))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk " + logindata.getdisplayname())));

            imsg.appendSibling(ismgStatus);

            return imsg;

        } catch (ApiException e) {
            return new ChatComponentText(RED + e.getMessage());
        }

    }

    private boolean isProbBanned(long latestActivityTime) {
        final long timeNow = new Date().getTime();
        return (Math.abs(latestActivityTime - wdr.getTimestamp()) < 24L * 60L * 60L * 1000L
                && Math.abs(timeNow - wdr.getTimestamp()) > 3L * 24L * 60L * 60L * 1000L)
                || Math.abs(timeNow - latestActivityTime) > 14L * 24L * 60L * 60L * 1000L;
    }

}