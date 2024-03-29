package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.LabyModNameHistory;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.List;

public class CommandName extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "name";
    }

    /**
     * Displays name history for a player
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length < 1) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + " <playername>");
            return;
        }

        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final MojangPlayernameToUUID nameApi = new MojangPlayernameToUUID(args[0]);
                final List<String> history = LabyModNameHistory.getNameHistory(nameApi.getUUID());
                mc.addScheduledTask(() -> printNameList(history, args));
            } catch (ApiException e1) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + e1.getMessage());
            }
            return null;
        });

    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("names");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

    private void printNameList(List<String> history, String[] args) {

        int displaypage = 1;
        int nbnames = 1;
        int nbpage = 1;

        if (args.length > 1) {
            try {
                displaypage = parseInt(args[1]);
            } catch (NumberInvalidException e) {
                ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + args[1] + " isn't a valid number."));
                return;
            }
        }

        final IChatComponent imsgbody = new ChatComponentText("");
        boolean warning = true;

        for (final String s : history) {
            if (nbnames == 9) {
                nbnames = 1;
                nbpage++;
            }
            if (nbpage == displaypage) {
                imsgbody.appendText(s + "\n");
                warning = false;
            }
            nbnames++;
        }

        if (warning) {
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No names to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available."));
        } else {
            ChatUtil.printIChatList(
                    "Name History",
                    imsgbody,
                    displaypage,
                    nbpage,
                    "/name " + args[0],
                    EnumChatFormatting.BLUE,
                    new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open " + EnumChatFormatting.BLUE + "NamesMC" + EnumChatFormatting.YELLOW + " in browser"),
                    "https://namemc.com/search?q=" + args[0]);
        }

    }

}
