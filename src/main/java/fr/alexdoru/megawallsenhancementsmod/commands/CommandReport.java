package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandReport extends MyAbstractCommand {

    /* cheats recognized by hypixel*/
    public static final String[] recognizedcheats = {
            "aura",
            "aimbot",
            "bhop",
            "velocity",
            "reach",
            "speed",
            "ka",
            "killaura",
            "forcefield",
            "antiknockback",
            "autoclicker",
            "ac",
            "fly",
            "dolphin",
            "jesus"};
    /* cheats for the tabcompletion*/
    public static final String[] cheatsArray = {
            "aura",
            "aimbot",
            "bhop",
            "velocity",
            "reach",
            "speed",
            "ka",
            "killaura",
            "multiaura",
            "forcefield",
            "autoblock",
            "antiknockback",
            "antikb",
            "autoclicker",
            "ac",
            "fly",
            "dolphin",
            "jesus",
            "keepsprint",
            "noslowdown",
            "fastbreak",
            "speedmine",
            "cheating",
            "scaffold"
    };
    public static final List<String> cheatsList = Arrays.asList(cheatsArray);

    @Override
    public String getCommandName() {
        return "report";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length < 1) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + " <player> <cheats>");
            return;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("boosting")) {
            sendChatMessage("/report " + args[0] + " -b BOO -C");
            return;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("crossteaming")) {
            sendChatMessage("/report " + args[0] + " -b CTT -C");
            return;
        }

        final StringBuilder msg = new StringBuilder("/report " + args[0]);
        for (int i = 1; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("autoblock") || args[i].equalsIgnoreCase("multiaura")) {
                msg.append(" killaura");
            } else if (args[i].equalsIgnoreCase("noslowdown")
                    || args[i].equalsIgnoreCase("keepsprint")
                    || args[i].equalsIgnoreCase("fastbreak")
                    || args[i].equalsIgnoreCase("scaffold")) {
                msg.append(" cheating");
            } else {
                msg.append(" ").append(args[i]);
            }
        }
        sendChatMessage(msg.toString());

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            if (ScoreboardTracker.isInMwGame) {
                if (ScoreboardTracker.isPrepPhase) {
                    return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
                } else {
                    final List<String> playersInThisGame = FinalKillCounter.getPlayersInThisGame();
                    playersInThisGame.removeAll(TabCompletionUtil.getOnlinePlayersByName());
                    return getListOfStringsMatchingLastWord(args, playersInThisGame);
                }
            }
            return null;
        }
        if (args.length > 1) {
            final List<String> list = new ArrayList<>(Arrays.asList(cheatsArray));
            list.add("boosting");
            list.add("crossteaming");
            return getListOfStringsMatchingLastWord(args, list);
        }
        return null;
    }

}
