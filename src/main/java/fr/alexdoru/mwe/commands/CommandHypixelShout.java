package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.nocheaters.ReportSuggestionHandler;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandHypixelShout extends MyAbstractCommand {

    private static String latestShoutSent;

    @Override
    public String getCommandName() {
        return "shout";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        final String msg = buildString(args, 0);
        if (ReportSuggestionHandler.shouldCancelShout(msg)) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED +
                    "Shout was canceled since the player you are trying to report isn't in the tablist anymore," +
                    " your report wouldn't have worked, try again in a few second." +
                    " This can happen when the targeted player just respawned.");
        } else {
            latestShoutSent = msg;
            sendChatMessage("/shout " + msg);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length >= 2 && isKeywordreport(2, args)) {
            if (ScoreboardTracker.isPrepPhase()) {
                return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
            } else if (ScoreboardTracker.isInMwGame()) {
                final List<String> playersInThisGame = FinalKillCounter.getPlayersInThisGame();
                playersInThisGame.removeAll(TabCompletionUtil.getOnlinePlayersByName());
                return getListOfStringsMatchingLastWord(args, playersInThisGame);
            }
        }
        if (args.length >= 3 && (isKeywordreport(3, args))) {
            return getListOfStringsMatchingLastWord(args, CommandReport.cheatsArray);
        }
        if (ScoreboardTracker.isPrepPhase()) {
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
        }
        return null;
    }

    private boolean isKeywordreport(int i, String[] args) {
        return args[args.length - i].equalsIgnoreCase("report") || args[args.length - i].equalsIgnoreCase("wdr") || args[args.length - i].equalsIgnoreCase("/report") || args[args.length - i].equalsIgnoreCase("/wdr");
    }

    public static String getLatestShoutSent() {
        return latestShoutSent;
    }

    public static void resetLastShout() {
        latestShoutSent = null;
    }

}