package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandBase;

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
            }
            return waitingOnAutocompleteIn;
        } else {
            tabCompleteOptions = TabCompletionUtil.getOnlinePlayersByName();
            new DelayedTask(() -> {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
                    final GuiChat guichat = (GuiChat) Minecraft.getMinecraft().currentScreen;
                    guichat.onAutocompleteResponse(tabCompleteOptions.toArray(new String[0]));
                    tabCompleteOptions = null;
                }
            }, 1);
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
