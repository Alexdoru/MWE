package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandBase;
import net.minecraftforge.client.ClientCommandHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public class GuiChatHook {

    private static List<String> tabCompleteOptions;

    public static boolean autoComplete(boolean waitingOnAutocompleteIn, String leftOfCursor, String full) {
        tabCompleteOptions = null;
        if (leftOfCursor.length() >= 1) {
            if (leftOfCursor.charAt(0) != '/') {
                final String[] args = leftOfCursor.split(" ", -1);
                tabCompleteOptions = CommandBase.getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
            } else {
                final String lowerCase = leftOfCursor.toLowerCase();
                if (lowerCase.startsWith("/msg ") || lowerCase.startsWith("/w ") || lowerCase.startsWith("/r ")) {
                    final String[] args = leftOfCursor.split(" ", -1);
                    tabCompleteOptions = CommandBase.getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
                }
            }
            return waitingOnAutocompleteIn;
        } else {
            ClientCommandHandler.instance.latestAutoComplete = null;
            tabCompleteOptions = TabCompletionUtil.getOnlinePlayersByName();
            new DelayedTask(() -> {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
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

}
