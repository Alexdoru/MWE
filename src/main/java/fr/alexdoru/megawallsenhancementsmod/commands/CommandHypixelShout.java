package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import fr.alexdoru.nocheatersmod.util.ReportSuggestionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandHypixelShout extends CommandBase {

    private static String latestShoutSent;

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
        final String msg = buildString(args, 0);
        if (ReportSuggestionHandler.shouldCancelShout(msg)) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED +
                    "Shout was canceled since the player you are trying to report isn't in the tablist anymore," +
                    " your report wouldn't have worked, try again in a few second." +
                    " This can happen when the targeted player just respawned.");
        } else {
            latestShoutSent = msg;
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/shout " + msg);
        }
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

    public static String getLatestShoutSent() {
        return latestShoutSent;
    }

    public static void resetLastShout() {
        latestShoutSent = null;
    }

}