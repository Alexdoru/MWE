package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandReport extends MyAbstractCommand {

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
        this.sendCommand(args);
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
