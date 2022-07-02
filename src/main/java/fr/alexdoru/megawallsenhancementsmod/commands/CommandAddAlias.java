package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandAddAlias extends CommandBase {

    public static final HashMap<String, String> renamingMap = new HashMap<>();

    @Override
    public String getCommandName() {
        return "addalias";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/addalias";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equals("clearall")) {
            renamingMap.clear();
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Cleared alias for all players."));
            return;
        } else if (args.length == 1 && args[0].equals("list")) {
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "In this lobby :\n"));
            for (NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                final String alias = renamingMap.get(networkPlayerInfo.getGameProfile().getName());
                if (alias != null) {
                    ChatUtil.addChatMessage(new ChatComponentText(NameUtil.getFormattedName(networkPlayerInfo.getGameProfile().getName()) + EnumChatFormatting.RESET + " (" + alias + ")"));
                }
            }
            return;
        }
        if (args.length != 2) {
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : /addalias <playername> <alias>"));
            return;
        }
        if (args[0].equals("remove")) {
            renamingMap.remove(args[1]);
            NameUtil.updateGameProfileAndName(args[1], true);
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Removed alias for " + EnumChatFormatting.GOLD + args[1]));
            return;
        }
        renamingMap.put(args[0], args[1]);
        NameUtil.updateGameProfileAndName(args[0], true);
        ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Added alias for " + EnumChatFormatting.GOLD + args[0] + EnumChatFormatting.GREEN + " : " + EnumChatFormatting.GOLD + args[1]));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final List<String> onlinePlayersByName = TabCompletionUtil.getOnlinePlayersByName();
        onlinePlayersByName.addAll(Arrays.asList("clearall", "list", "remove"));
        return getListOfStringsMatchingLastWord(args, onlinePlayersByName);
    }

}
