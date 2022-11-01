package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.data.AliasData;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandAddAlias extends MyAbstractCommand {

    public CommandAddAlias() {
        AliasData.init();
    }

    @Override
    public String getCommandName() {
        return "addalias";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/addalias";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            ChatUtil.addChatMessage(getCommandHelp());
            return;
        }
        if (args.length == 1 && args[0].equals("list")) {
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "In this lobby :\n");
            for (final NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                if (AliasData.getAlias(networkPlayerInfo.getGameProfile().getName()) != null) {
                    ChatUtil.addChatMessage(NameUtil.getFormattedName(networkPlayerInfo));
                }
            }
            return;
        }
        if (args.length != 2) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : /addalias <playername> <alias>");
            return;
        }
        if (args[0].equals("remove")) {
            AliasData.removeAlias(args[1]);
            NameUtil.updateGameProfileAndName(args[1], false);
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Removed alias for " + EnumChatFormatting.GOLD + args[1]);
            return;
        }
        AliasData.putAlias(args[0], args[1]);
        NameUtil.updateGameProfileAndName(args[0], false);
        ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Added alias for " + EnumChatFormatting.GOLD + args[0] + EnumChatFormatting.GREEN + " : " + EnumChatFormatting.GOLD + args[1]);
    }

    private IChatComponent getCommandHelp() {
        return new ChatComponentText(EnumChatFormatting.GREEN + ChatUtil.bar() + "\n"
                + ChatUtil.centerLine(EnumChatFormatting.GOLD + "AddAlias Help\n\n")
                + EnumChatFormatting.YELLOW + "/addalias <player> <alias>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Adds an alias for the player\n"
                + EnumChatFormatting.YELLOW + "/addalias <remove> <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Removes the alias for the player\n"
                + EnumChatFormatting.GREEN + ChatUtil.bar()
        );
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return getListOfStringsMatchingLastWord(args, AliasData.getAllNames());
        }
        final List<String> onlinePlayersByName = TabCompletionUtil.getOnlinePlayersByName();
        onlinePlayersByName.addAll(Arrays.asList("list", "remove"));
        return getListOfStringsMatchingLastWord(args, onlinePlayersByName);
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("ad");
    }

}
