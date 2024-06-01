package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.Map.Entry;

import static net.minecraft.util.EnumChatFormatting.*;

public class CommandSquad extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "squad";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (ScoreboardUtils.isPlayingHypixelPit()) {
            this.sendCommand(args);
            return;
        }

        if (args.length < 1) {
            this.printCommandHelp();
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {

            this.addSquadMembers(args);

        } else if (args[0].equalsIgnoreCase("addteam")) {

            if (ScoreboardTracker.isPreGameLobby) {
                SquadHandler.formSquad();
            } else {
                this.addTeamToSquad();
            }

        } else if (args[0].equalsIgnoreCase("formsquad")) {

            SquadHandler.formSquad();

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
        final String[] args1 = {"add", "addteam", "disband", "formsquad", "list", "remove"};
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
                GREEN + ChatUtil.bar() + "\n"
                        + ChatUtil.centerLine(GOLD + "Squad Help\n\n")
                        + YELLOW + "/squad add <player>" + GRAY + " - " + AQUA + "add a player to the squad\n"
                        + YELLOW + "/squad add <player> as Nickname" + GRAY + " - " + AQUA + "add a player to the squad and change their name\n"
                        + YELLOW + "/squad addteam" + GRAY + " - " + AQUA + "add all your teamates to the squad\n"
                        + YELLOW + "/squad formsquad" + GRAY + " - " + AQUA + "add your teamates to the squad when in a MW pre game\n"
                        + YELLOW + "/squad remove <player>" + GRAY + " - " + AQUA + "remove a player from the squad\n"
                        + YELLOW + "/squad list" + GRAY + " - " + AQUA + "list players in the squad\n"
                        + YELLOW + "/squad disband" + GRAY + " - " + AQUA + "disband the squad\n"
                        + GREEN + ChatUtil.bar()
        );
    }

    private void addSquadMembers(String[] args) {
        if (args.length < 2) {
            ChatUtil.addChatMessage(RED + "Usage : /squad <add> <playername>");
            return;
        }

        if (SquadHandler.getSquad().isEmpty()) {
            SquadHandler.addPlayer(Minecraft.getMinecraft().thePlayer.getName());
            if (!ConfigHandler.hypixelNick.isEmpty()) {
                SquadHandler.addPlayer(ConfigHandler.hypixelNick, ConfigHandler.nickHider ? ITALIC + Minecraft.getMinecraft().thePlayer.getName() + RESET : ConfigHandler.hypixelNick);
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
                    if (!args[1].isEmpty() && !alias.isEmpty()) {
                        if (args[1].charAt(0) != alias.charAt(0)) {
                            alias = args[1].charAt(0) + " ❘ " + alias;
                        }
                    }
                }
            }
            SquadHandler.addPlayer(args[1], alias);
            ChatUtil.addChatMessage(ChatUtil.getTagMW() +
                    GREEN + "Added " + GOLD + args[1] + GREEN + " as " +
                    GOLD + alias + GREEN + " to the squad.");
            return;
        }

        for (int i = 1; i < args.length; i++) {
            SquadHandler.addPlayer(args[i]);
            ChatUtil.addChatMessage(ChatUtil.getTagMW() +
                    GREEN + "Added " + GOLD + args[i] + GREEN + " to the squad.");
        }
    }

    private void addTeamToSquad() {
        if (mc.thePlayer == null) {
            return;
        }
        final NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
        if (playerInfo == null) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + RED + "Couldn't find your team!");
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
                    if (!ConfigHandler.hypixelNick.isEmpty()) {
                        SquadHandler.addPlayer(ConfigHandler.hypixelNick, ConfigHandler.nickHider ? ITALIC + Minecraft.getMinecraft().thePlayer.getName() + RESET : ConfigHandler.hypixelNick);
                    }
                }
                SquadHandler.addPlayer(netInfo.getGameProfile().getName());
            }
        }
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + GREEN + "Added all your team to the squad!");
    }

    private char getTeamColor(NetworkPlayerInfo networkPlayerInfo) {
        if (networkPlayerInfo == null || networkPlayerInfo.getPlayerTeam() == null) {
            return 'f';
        }
        return StringUtil.getLastColorCharOf(networkPlayerInfo.getPlayerTeam().getColorPrefix());
    }

    private void removeSquadMembers(String[] args) {
        if (args.length < 2) {
            ChatUtil.addChatMessage(RED + "Usage : /squad <remove> <playername>");
            return;
        }
        for (int i = 1; i < args.length; i++) {
            if (SquadHandler.removePlayer(args[i])) {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + GREEN + "Removed " + GOLD + args[i] + GREEN + " from the squad.");
            } else {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + GOLD + args[i] + RED + " isn't in the squad.");
            }
        }
    }

    private void listSquadMembers() {
        if (SquadHandler.getSquad().isEmpty()) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + RED + "No one in the squad right now.");
            return;
        }
        final IChatComponent imsg = new ChatComponentText(ChatUtil.getTagMW() + GREEN + "Players in your squad : \n");
        for (final Entry<String, String> entry : SquadHandler.getSquad().entrySet()) {
            final String displayname = entry.getKey();
            final String squadname = entry.getValue();
            imsg.appendText(DARK_GRAY + "- " + GOLD + displayname
                    + (displayname.equals(squadname) ? "" : GREEN + " renamed as : " + GOLD + entry.getValue()) + "\n");
        }
        ChatUtil.addChatMessage(imsg);
    }

    private void disbandSquad() {
        SquadHandler.clearSquad();
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + GREEN + "Removed all players from the squad.");
    }

}
