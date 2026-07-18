package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.commands.CommandWDR;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandBase;
import net.minecraftforge.client.ClientCommandHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GuiChatHook_TabCompletePlayers {

    private static List<String> tabCompleteOptions;

    public static boolean autoComplete(boolean original, String leftOfCursor, String full) {
        tabCompleteOptions = null;
        if (!leftOfCursor.isEmpty()) {
            final String[] args = leftOfCursor.split(" ", -1);
            final boolean isCommand = leftOfCursor.charAt(0) == '/';
            if (isCommand) {
                final String lowerCase = leftOfCursor.toLowerCase();
                if (lowerCase.startsWith("/msg ") || lowerCase.startsWith("/w ") || lowerCase.startsWith("/r ") || ScoreboardTracker.isPrepPhase() && (lowerCase.startsWith("/shout ") || lowerCase.startsWith("/cr ") || lowerCase.startsWith("/chatreport "))) {
                    tabCompleteOptions = CommandBase.getListOfStringsMatchingLastWord(args, TabCompletionUtil.getPlayersAndAlias());
                } else if (lowerCase.startsWith("/report ")) {
                    tabCompleteReport(args);
                }
            } else {
                tabCompleteOptions = CommandBase.getListOfStringsMatchingLastWord(args, TabCompletionUtil.getPlayersAndAlias());
            }
            return original;
        } else {
            ClientCommandHandler.instance.latestAutoComplete = null;
            tabCompleteOptions = TabCompletionUtil.getPlayersAndAlias();
            new DelayedTask(() -> {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiChat && tabCompleteOptions != null) {
                    ((GuiChat) Minecraft.getMinecraft().currentScreen).onAutocompleteResponse(tabCompleteOptions.toArray(new String[0]));
                    tabCompleteOptions = null;
                }
            });
            return true;
        }
    }

    public static String[] getLatestAutoComplete(String[] array) {
        if (tabCompleteOptions == null) {
            return array;
        }
        final HashSet<String> optionSet = new HashSet<>(Arrays.asList(array));
        optionSet.addAll(tabCompleteOptions);
        return optionSet.toArray(new String[0]);
    }

    private static void tabCompleteReport(String[] args) {
        if (args.length == 2) {
            if (ScoreboardTracker.isInMwGame()) {
                if (ScoreboardTracker.isPrepPhase()) {
                    tabCompleteOptions = CommandBase.getListOfStringsMatchingLastWord(args, TabCompletionUtil.getPlayersAndAlias());
                    return;
                } else {
                    final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
                    if (fkCounter == null) return;
                    final List<String> playersInThisGame = fkCounter.getPlayersInThisGame();
                    playersInThisGame.removeAll(TabCompletionUtil.getPlayersAndAlias());
                    tabCompleteOptions = CommandBase.getListOfStringsMatchingLastWord(args, playersInThisGame);
                }
            }
            return;
        }
        if (args.length > 2) {
            final List<String> list = new ArrayList<>(CommandWDR.cheatsList);
            list.add("boosting");
            list.add("crossteaming");
            tabCompleteOptions = CommandBase.getListOfStringsMatchingLastWord(args, list);
        }
    }

}
