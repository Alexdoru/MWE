package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class CommandSquad extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "squad";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/squad <add|disband|list|remove>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (ScoreboardUtils.isPlayingHypixelPit()) {
            sendChatMessage("/squad ", args);
            return;
        }

        if (args.length < 1) {
            ChatUtil.addChatMessage(getCommandHelp());
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {

            if (args.length < 2) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : /squad <add> <playername>");
                return;
            }

            if (SquadHandler.getSquad().isEmpty()) {
                SquadHandler.addPlayer(Minecraft.getMinecraft().thePlayer.getName());
                if (!ConfigHandler.hypixelNick.equals("")) {
                    SquadHandler.addPlayer(ConfigHandler.hypixelNick, ConfigHandler.nickHider ? EnumChatFormatting.ITALIC + Minecraft.getMinecraft().thePlayer.getName() + EnumChatFormatting.RESET : ConfigHandler.hypixelNick);
                }
            }

            if (args.length >= 4 && args[2].equalsIgnoreCase("as")) {
                final StringBuilder stringBuilder = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    stringBuilder.append(args[i]).append((i == args.length - 1) ? "" : " ");
                }
                final String alias = stringBuilder.toString();
                SquadHandler.addPlayer(args[1], alias);
                ChatUtil.addChatMessage(ChatUtil.getTagMW() +
                        EnumChatFormatting.GREEN + "Added " + EnumChatFormatting.GOLD + args[1] + EnumChatFormatting.GREEN + " as " +
                        EnumChatFormatting.GOLD + alias + EnumChatFormatting.GREEN + " to the squad.");
                return;
            }

            for (int i = 1; i < args.length; i++) {
                SquadHandler.addPlayer(args[i]);
                ChatUtil.addChatMessage(ChatUtil.getTagMW() +
                        EnumChatFormatting.GREEN + "Added " + EnumChatFormatting.GOLD + args[i] + EnumChatFormatting.GREEN + " to the squad.");
            }

        } else if (args[0].equalsIgnoreCase("disband")) {

            SquadHandler.clearSquad();
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Removed all players from the squad.");

        } else if (args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("list")) {

            final HashMap<String, String> squad = SquadHandler.getSquad();

            if (squad.isEmpty()) {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "No one in the squad right now.");
                return;
            }

            final IChatComponent imsg = new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Players in your squad : \n");

            for (final Entry<String, String> entry : squad.entrySet()) {
                final String displayname = entry.getKey();
                final String squadname = entry.getValue();
                imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GRAY + "- " + EnumChatFormatting.GOLD + displayname
                        + (displayname.equals(squadname) ? "" : EnumChatFormatting.GREEN + " renamed as : " + EnumChatFormatting.GOLD + entry.getValue()) + "\n"));
            }

            ChatUtil.addChatMessage(imsg);

        } else if (args[0].equalsIgnoreCase("remove")) {

            if (args.length < 2) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : /squad <remove> <playername>");
                return;
            }

            for (int i = 1; i < args.length; i++) {
                if (SquadHandler.removePlayer(args[i])) {
                    ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Removed " + EnumChatFormatting.GOLD + args[i] + EnumChatFormatting.GREEN + " from the squad.");
                } else {
                    ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GOLD + args[i] + EnumChatFormatting.RED + " isn't in the squad.");
                }
            }

        } else {
            ChatUtil.addChatMessage(getCommandHelp());
        }

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final String[] args1 = {"add", "disband", "list", "remove"};
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, args1) : args.length >= 2 ? getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()) : null;
    }

    private IChatComponent getCommandHelp() {

        return new ChatComponentText(EnumChatFormatting.GREEN + ChatUtil.bar() + "\n"
                + ChatUtil.centerLine(EnumChatFormatting.GOLD + "Squad Help\n\n")
                + EnumChatFormatting.YELLOW + "/squad add <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "add a player to the squad\n"
                + EnumChatFormatting.YELLOW + "/squad add <player> as Nickname" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "add a player to the squad and change their name\n"
                + EnumChatFormatting.YELLOW + "/squad remove <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "remove a player from the squad\n"
                + EnumChatFormatting.YELLOW + "/squad list" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "list players in the squad\n"
                + EnumChatFormatting.YELLOW + "/squad disband" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "disband the squad\n"
                + EnumChatFormatting.GREEN + ChatUtil.bar()
        );

    }

}
