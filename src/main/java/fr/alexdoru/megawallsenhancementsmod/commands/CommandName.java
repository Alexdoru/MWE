package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

import java.util.Collections;
import java.util.List;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.bar;

public class CommandName extends CommandBase {

    @Override
    public String getCommandName() {
        return "name";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/name <playername>";
    }

    /**
     * Displays name history for a player
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length < 1) {
            addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender));
            return;
        }

        Multithreading.addTaskToQueue(() -> {

            final String playername = args[0];
            try {
                new CachedMojangUUID(playername);
            } catch (ApiException e1) {
                addChatMessage(EnumChatFormatting.RED + e1.getMessage());
                return null;
            }

            final String namesMC_URL = "https://namemc.com/search?q=" + playername;
            final IChatComponent imsg = new ChatComponentText(EnumChatFormatting.BLUE + bar() + "\n" + "                " +
                    EnumChatFormatting.GOLD + "Name History - " + playername + "\n\n" +
                    EnumChatFormatting.GRAY + "Name History API had been removed by Microsoft RIP\n")
                    .appendSibling(new ChatComponentText(
                            EnumChatFormatting.GREEN + "Click to open " +
                                    EnumChatFormatting.BLUE + EnumChatFormatting.BOLD + "NamesMC" +
                                    EnumChatFormatting.GREEN + " in browser\n\n")
                            .setChatStyle(new ChatStyle()
                                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, namesMC_URL))
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + namesMC_URL)))))
                    .appendSibling(new ChatComponentText(EnumChatFormatting.BLUE + bar()));
            addChatMessage(imsg);

            return null;

        });

    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("names");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

}
