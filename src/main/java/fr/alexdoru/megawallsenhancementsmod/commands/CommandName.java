package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangNameHistory;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.List;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.printIChatList;

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
            addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
            return;
        }

        Multithreading.addTaskToQueue(() -> {

            CachedMojangUUID apiname;
            try {
                apiname = new CachedMojangUUID(args[0]);
            } catch (ApiException e1) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + e1.getMessage()));
                return null;
            }
            String uuid = apiname.getUuid();

            MojangNameHistory apinamehistory;
            try {
                apinamehistory = new MojangNameHistory(uuid);
            } catch (ApiException e1) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + e1.getMessage()));
                return null;
            }

            int displaypage = 1;
            int nbnames = 1;
            int nbpage = 1;

            if (args.length > 1) {
                try {
                    displaypage = parseInt(args[1]);
                } catch (NumberInvalidException e) {
                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + args[1] + " isn't a valid number."));
                    return null;
                }
            }

            int n = apinamehistory.getNames().size();
            IChatComponent imsgbody = new ChatComponentText("");
            boolean warning = true;

            for (int i = n - 1; i >= 0; i--) {
                if (nbnames == 9) {
                    nbnames = 1;
                    nbpage++;
                }
                if (nbpage == displaypage) {
                    if (i == 0) { // original name
                        imsgbody.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + apinamehistory.getNames().get(i) + EnumChatFormatting.GRAY + " Original name\n"));
                    } else if (i == n - 1) {
                        imsgbody.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + apinamehistory.getNames().get(i) + EnumChatFormatting.GRAY + " since " + DateUtil.localformatTimestampday(apinamehistory.getTimestamps().get(i)) + "\n"));
                    } else {
                        imsgbody.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + apinamehistory.getNames().get(i) + EnumChatFormatting.GRAY + " " + DateUtil.localformatTimestampday(apinamehistory.getTimestamps().get(i)) + "\n"));
                    }
                    warning = false;
                }
                nbnames++;
            }

            if (warning) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No names to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available."));
            } else {
                printIChatList(
                        "Name History",
                        imsgbody,
                        displaypage,
                        nbpage,
                        "/name " + args[0],
                        EnumChatFormatting.BLUE,
                        new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open " + EnumChatFormatting.BLUE + "NamesMC" + EnumChatFormatting.YELLOW + " in browser"),
                        "https://namemc.com/search?q=" + args[0]
                );
            }

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
