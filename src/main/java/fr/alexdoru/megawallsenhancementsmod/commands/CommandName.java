package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

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

            final String playername = args[0];
            try {
                new MojangPlayernameToUUID(playername);
            } catch (ApiException e1) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + e1.getMessage());
                return null;
            }

            final String namesMC_URL = "https://namemc.com/search?q=" + playername;
            final IChatComponent imsg = new ChatComponentText(EnumChatFormatting.BLUE + ChatUtil.bar() + "\n"
                    + ChatUtil.centerLine(EnumChatFormatting.GOLD + "Name History - " + playername) + "\n\n" +
                    EnumChatFormatting.GRAY + "Name History API had been removed by Microsoft RIP\n")
                    .appendSibling(new ChatComponentText(
                            EnumChatFormatting.GREEN + "Click to open " +
                                    EnumChatFormatting.BLUE + EnumChatFormatting.BOLD + "NamesMC" +
                                    EnumChatFormatting.GREEN + " in browser\n\n")
                            .setChatStyle(new ChatStyle()
                                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, namesMC_URL))
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + namesMC_URL)))))
                    .appendText(EnumChatFormatting.BLUE + ChatUtil.bar());
            ChatUtil.addChatMessage(imsg);

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

}
