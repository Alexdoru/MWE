package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.Map.Entry;

public class CommandSquad extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "squad";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (ScoreboardUtils.isPlayingHypixelPit()) {
            sendChatMessage("/squad ", args);
            return;
        }

        if (args.length < 1) {
            this.printCommandHelp();
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {

            this.addSquadMembers(args);

        } else if (args[0].equalsIgnoreCase("addteam")) {

            this.addTeamToSquad();

        } else if (args[0].equalsIgnoreCase("disband")) {

            this.disbandSquad();

        } else if (args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("list")) {

            this.listSquadMembers();

        } else if (args[0].equalsIgnoreCase("remove")) {

            this.removeSquadMembers(args);

        } else {

            this.printCommandHelp();

        }

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final String[] args1 = {"add", "addteam", "disband", "list", "remove"};
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, args1);
        }
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("add")) {
                return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
            } else if (args[0].equalsIgnoreCase("remove")) {
                return getListOfStringsMatchingLastWord(args, SquadHandler.getSquad().keySet());
            }
        }
        return null;
    }

    @Override
    protected void printCommandHelp() {
        ChatUtil.addChatMessage(
                EnumChatFormatting.GREEN + ChatUtil.bar() + "\n"
                        + ChatUtil.centerLine(EnumChatFormatting.GOLD + "Squad Help\n\n")
                        + EnumChatFormatting.YELLOW + "/squad add <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "add a player to the squad\n"
                        + EnumChatFormatting.YELLOW + "/squad add <player> as Nickname" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "add a player to the squad and change their name\n"
                        + EnumChatFormatting.YELLOW + "/squad addteam" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "add all your teamates to the squad\n"
                        + EnumChatFormatting.YELLOW + "/squad remove <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "remove a player from the squad\n"
                        + EnumChatFormatting.YELLOW + "/squad list" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "list players in the squad\n"
                        + EnumChatFormatting.YELLOW + "/squad disband" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "disband the squad\n"
                        + EnumChatFormatting.GREEN + ChatUtil.bar()
        );
    }

    private void addSquadMembers(String[] args) {
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
                stringBuilder.append(args[i]);
                if (i != args.length - 1) {
                    stringBuilder.append(" ");
                }
            }
            String alias = stringBuilder.toString();
            if (!args[1].equals(Minecraft.getMinecraft().thePlayer.getName()) && !args[1].equals(ConfigHandler.hypixelNick)) {
                if (ConfigHandler.keepFirstLetterSquadnames) {
                    if (args[1].length() > 0 && alias.length() > 0) {
                        if (args[1].charAt(0) != alias.charAt(0)) {
                            alias = args[1].charAt(0) + " \u2758 " + alias;
                        }
                    }
                }
            }
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
    }

    private void addTeamToSquad() {
        if (mc.thePlayer == null) {
            return;
        }
        final NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
        if (playerInfo == null) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "Couldn't find your team!");
            return;
        }
        final char myColor = this.getTeamColor(playerInfo);
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getId().equals(mc.thePlayer.getUniqueID())) {
                continue;
            }
            if (myColor == this.getTeamColor(netInfo)) {
                if (SquadHandler.getSquad().isEmpty()) {
                    SquadHandler.addPlayer(Minecraft.getMinecraft().thePlayer.getName());
                    if (!ConfigHandler.hypixelNick.equals("")) {
                        SquadHandler.addPlayer(ConfigHandler.hypixelNick, ConfigHandler.nickHider ? EnumChatFormatting.ITALIC + Minecraft.getMinecraft().thePlayer.getName() + EnumChatFormatting.RESET : ConfigHandler.hypixelNick);
                    }
                }
                SquadHandler.addPlayer(netInfo.getGameProfile().getName());
            }
        }
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Added all your team to the squad!");
    }

    private char getTeamColor(NetworkPlayerInfo networkPlayerInfo) {
        if (networkPlayerInfo == null || networkPlayerInfo.getPlayerTeam() == null) {
            return 'f';
        }
        return StringUtil.getLastColorCharOf(networkPlayerInfo.getPlayerTeam().getColorPrefix());
    }

    private void removeSquadMembers(String[] args) {
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
    }

    private void listSquadMembers() {
        if (SquadHandler.getSquad().isEmpty()) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "No one in the squad right now.");
            return;
        }
        final IChatComponent imsg = new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Players in your squad : \n");
        for (final Entry<String, String> entry : SquadHandler.getSquad().entrySet()) {
            final String displayname = entry.getKey();
            final String squadname = entry.getValue();
            imsg.appendText(EnumChatFormatting.DARK_GRAY + "- " + EnumChatFormatting.GOLD + displayname
                    + (displayname.equals(squadname) ? "" : EnumChatFormatting.GREEN + " renamed as : " + EnumChatFormatting.GOLD + entry.getValue()) + "\n");
        }
        ChatUtil.addChatMessage(imsg);
    }

    private void disbandSquad() {
        SquadHandler.clearSquad();
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Removed all players from the squad.");
    }

}
