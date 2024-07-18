package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandHypixelShout extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "shout";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        this.sendCommand(args);
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

}