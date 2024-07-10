package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.api.exceptions.ApiException;
import fr.alexdoru.mwe.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.mwe.api.requests.HypixelPlayerData;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.gui.huds.DebugScoreboardHUD;
import fr.alexdoru.mwe.nocheaters.WDR;
import fr.alexdoru.mwe.nocheaters.WarningMessagesHandler;
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
import net.minecraft.util.*;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CommandNocheaters extends MyAbstractCommand {

    private DebugScoreboardHUD debugHUD;

    @Override
    public String getCommandName() {
        return "nocheaters";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length == 0) {
            WarningMessagesHandler.printReportMessagesForWorld(true);
            return;
        }

        if (args[0].equalsIgnoreCase("reportlist") || args[0].equalsIgnoreCase("debugreportlist")) {

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
                EnumChatFormatting.RED + ChatUtil.bar() + "\n"
                        + ChatUtil.centerLine(EnumChatFormatting.GOLD + "NoCheaters Help\n\n")
                        + EnumChatFormatting.YELLOW + getCommandUsage(null) + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the list of reported players in your current world\n"
                        + EnumChatFormatting.YELLOW + getCommandUsage(null) + " reportlist" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the list of reported players\n"
                        + EnumChatFormatting.RED + ChatUtil.bar()
        );
    }

    private void printReportList(String[] args) {

        final boolean doStalk = args[0].equalsIgnoreCase("reportlist");
        if (doStalk) {
            if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                ChatUtil.printApikeySetupInfo();
                return;
            }
        }

        final int displaypage;
        if (args.length > 1) {
            try {
                displaypage = parseInt(args[1]);
            } catch (NumberInvalidException e) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Not a valid page number");
                return;
            }
        } else {
            displaypage = 1;
        }

        final Map<Object, WDR> sortedMap = MapUtil.sortByDecreasingValue(WdrData.getAllWDRs());
        final long timeNow = new Date().getTime();
        final List<Future<IChatComponent>> futureList = new ArrayList<>();
        int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
        int nbpage = 1;
        boolean warning = true;

        for (final Map.Entry<Object, WDR> entry : sortedMap.entrySet()) {
            if (nbreport == 11) {
                nbreport = 1;
                nbpage++;
            }
            if (nbpage == displaypage) {
                warning = false;
                futureList.add(MultithreadingUtil.addTaskToQueue(new CreateReportLineTask(entry.getKey(), entry.getValue(), doStalk, timeNow)));
            }
            nbreport++;
        }

        if (sortedMap.isEmpty()) {
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "You have no one reported!");
            return;
        }

        if (warning) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "No reports to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available.");
            return;
        }

        final int finalNbpage = nbpage;
        MultithreadingUtil.addTaskToQueue(() -> {
            final IChatComponent imsgbody = new ChatComponentText("");
            for (final Future<IChatComponent> iChatComponentFuture : futureList) {
                imsgbody.appendSibling(iChatComponentFuture.get());
            }
            ChatUtil.printIChatList(
                    "Report list",
                    imsgbody,
                    displaypage,
                    finalNbpage,
                    doStalk ? getCommandUsage(null) + " reportlist" : getCommandUsage(null) + " debugreportlist",
                    EnumChatFormatting.RED,
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
    private final long timeNow;

    public CreateReportLineTask(Object mapKey, WDR wdr, boolean doStalk, long timeNow) {
        if (mapKey instanceof UUID) {
            this.uuid = (UUID) mapKey;
            this.nickname = null;
        } else if (mapKey instanceof String) {
            this.uuid = null;
            this.nickname = (String) mapKey;
        } else throw new IllegalArgumentException();
        this.wdr = wdr;
        this.doStalk = doStalk;
        this.timeNow = timeNow;
    }

    @Override
    public IChatComponent call() {

        try {

            final IChatComponent imsg;

            if (doStalk) {

                if (this.nickname != null) {

                    imsg = WarningMessagesHandler.createPlayerNameWithHoverText(EnumChatFormatting.DARK_PURPLE + "[Nick] " + EnumChatFormatting.GOLD + nickname, nickname, nickname, wdr, EnumChatFormatting.WHITE);

                } else {

                    assert uuid != null;
                    final HypixelPlayerData playerdata = new HypixelPlayerData(uuid);
                    final LoginData logindata = new LoginData(playerdata.getPlayerData());
                    imsg = WarningMessagesHandler.createPlayerNameWithHoverText(logindata.getFormattedName(), logindata.getdisplayname(), uuid.toString(), wdr, EnumChatFormatting.WHITE);

                    final IChatComponent ismgStatus = new ChatComponentText("");

                    if (logindata.isHidingFromAPI()) {

                        logindata.parseLatestActivity(playerdata.getPlayerData());
                        final long latestActivityTime = logindata.getLatestActivityTime();
                        final String latestActivity = logindata.getLatestActivity();
                        final boolean isProbBanned = isProbBanned(latestActivityTime);
                        ismgStatus.appendText(EnumChatFormatting.GRAY + " " + latestActivity + " " + (isProbBanned ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.YELLOW) + DateUtil.timeSince(latestActivityTime));

                    } else if (logindata.isOnline()) { // player is online

                        ismgStatus.appendText(EnumChatFormatting.GREEN + "    Online");

                    } else { // print lastlogout

                        final boolean isProbBanned = isProbBanned(logindata.getLastLogout());
                        ismgStatus.appendText(EnumChatFormatting.GRAY + " Lastlogout " + (isProbBanned ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.YELLOW) + DateUtil.timeSince(logindata.getLastLogout()));

                    }

                    ismgStatus.setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to run : /stalk " + logindata.getdisplayname())))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk " + logindata.getdisplayname())));

                    imsg.appendSibling(ismgStatus);

                }

            } else {

                if (nickname != null) {
                    imsg = new ChatComponentText(EnumChatFormatting.RED + nickname + EnumChatFormatting.GRAY + " reported : " + EnumChatFormatting.YELLOW + DateUtil.timeSince(wdr.getTimestamp()));
                } else {
                    assert uuid != null;
                    imsg = new ChatComponentText(EnumChatFormatting.RED + uuid.toString() + EnumChatFormatting.GRAY + " reported : " + EnumChatFormatting.YELLOW + DateUtil.timeSince(wdr.getTimestamp()));
                }

            }

            return imsg.appendText("\n");

        } catch (ApiException e) {
            return new ChatComponentText(EnumChatFormatting.RED + e.getMessage() + "\n");
        }

    }

    private boolean isProbBanned(long latestActivityTime) {
        return (Math.abs(latestActivityTime - wdr.getTimestamp()) < 24L * 60L * 60L * 1000L && Math.abs(timeNow - wdr.getTimestamp()) > 3L * 24L * 60L * 60L * 1000L) || Math.abs(timeNow - latestActivityTime) > 14L * 24L * 60L * 60L * 1000L;
    }

}