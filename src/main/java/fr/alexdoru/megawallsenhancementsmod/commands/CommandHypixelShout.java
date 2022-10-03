package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import fr.alexdoru.nocheatersmod.commands.CommandReport;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandHypixelShout extends CommandBase {

    @Override
    public String getCommandName() {
        return "shout";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/shout <message>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(Pattern.compile("seks", Pattern.CASE_INSENSITIVE).matcher("/shout " + buildString(args, 0)).replaceAll("****"));
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("SHOUT", "Shout");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length >= 2 && isKeywordreport(2, args)) {
            if (FKCounterMod.isitPrepPhase) {
                return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
            } else if (FKCounterMod.isInMwGame) {
                final List<String> playersInThisGame = KillCounter.getPlayersInThisGame();
                playersInThisGame.removeAll(TabCompletionUtil.getOnlinePlayersByName());
                return getListOfStringsMatchingLastWord(args, playersInThisGame);
            }
        }
        if (args.length >= 3 && (isKeywordreport(3, args))) {
            return getListOfStringsMatchingLastWord(args, CommandReport.cheatsArray);
        }
        if (FKCounterMod.isitPrepPhase) {
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
        }
        return null;
    }

    private boolean isKeywordreport(int i, String[] args) {
        return args[args.length - i].equalsIgnoreCase("report") || args[args.length - i].equalsIgnoreCase("wdr") || args[args.length - i].equalsIgnoreCase("/report") || args[args.length - i].equalsIgnoreCase("/wdr");
    }

}