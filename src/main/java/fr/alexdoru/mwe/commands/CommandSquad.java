package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.scoreboard.ScoreboardUtils;
import fr.alexdoru.mwe.utils.StringUtil;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
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

            addSquadMembers(args);

        } else if (args[0].equalsIgnoreCase("addteam")) {

            if (ScoreboardTracker.isPreGameLobby()) {
                SquadHandler.formSquad();
            } else {
                addTeamToSquad();
            }

        } else if (args[0].equalsIgnoreCase("formsquad")) {

            SquadHandler.formSquad();

        } else if (args[0].equalsIgnoreCase("disband")) {

            disbandSquad();

        } else if (args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("list")) {

            listSquadMembers();

        } else if (args[0].equalsIgnoreCase("remove")) {

            removeSquadMembers(args);

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
                return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getPlayers());
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

    private static void addSquadMembers(String[] args) {
        if (args.length < 2) {
            ChatUtil.addChatMessage(RED + "Usage : /squad <add> <playername>");
            return;
        }

        SquadHandler.addSelf();

        if (args.length >= 4 && args[2].equalsIgnoreCase("as")) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                stringBuilder.append(args[i]);
                if (i != args.length - 1) {
                    stringBuilder.append(" ");
                }
            }
            String alias = stringBuilder.toString();
            final String playername = tryGetNameFromTab(args[1]);
            if (!playername.equals(Minecraft.getMinecraft().thePlayer.getName()) && !playername.equals(MWEConfig.hypixelNick)) {
                if (MWEConfig.keepFirstLetterSquadnames) {
                    if (!playername.isEmpty() && !alias.isEmpty()) {
                        if (playername.charAt(0) != alias.charAt(0)) {
                            alias = playername.charAt(0) + " ❘ " + alias;
                        }
                    }
                }
            }
            SquadHandler.addPlayer(playername, alias);
            ChatUtil.addChatMessage(GREEN + "Added " + GOLD + playername + GREEN + " as " + GOLD + alias + GREEN + " to the squad.");
            return;
        }

        for (int i = 1; i < args.length; i++) {
            final String playername = tryGetNameFromTab(args[i]);
            SquadHandler.addPlayer(playername);
            ChatUtil.addChatMessage(GREEN + "Added " + GOLD + playername + GREEN + " to the squad.");
        }
    }

    private static void addTeamToSquad() {
        final Minecraft mc = Minecraft.getMinecraft();
        final NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
        if (playerInfo == null) {
            ChatUtil.addChatMessage(RED + "Couldn't find your team!");
            return;
        }
        final char myColor = getTeamColor(playerInfo);
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getId().equals(mc.thePlayer.getUniqueID())) {
                continue;
            }
            if (myColor == getTeamColor(netInfo)) {
                SquadHandler.addSelf();
                SquadHandler.addPlayer(netInfo.getGameProfile().getName());
            }
        }
        ChatUtil.addChatMessage(GREEN + "Added all your team to the squad!");
    }

    private static char getTeamColor(NetworkPlayerInfo networkPlayerInfo) {
        if (networkPlayerInfo == null || networkPlayerInfo.getPlayerTeam() == null) {
            return 'f';
        }
        return StringUtil.getLastColorCharOf(networkPlayerInfo.getPlayerTeam().getColorPrefix());
    }

    private static void removeSquadMembers(String[] args) {
        if (args.length < 2) {
            ChatUtil.addChatMessage(RED + "Usage : /squad <remove> <playername>");
            return;
        }
        for (int i = 1; i < args.length; i++) {
            final String playername = tryGetNameFromTab(args[i]);
            if (SquadHandler.removePlayer(playername)) {
                ChatUtil.addChatMessage(GREEN + "Removed " + GOLD + playername + GREEN + " from the squad.");
            } else {
                ChatUtil.addChatMessage(GOLD + args[i] + RED + " isn't in the squad.");
            }
        }
    }

    private static void listSquadMembers() {
        if (SquadHandler.getSquad().isEmpty()) {
            ChatUtil.addChatMessage(RED + "Squad is empty.");
            return;
        }
        final IChatComponent imsg = new ChatComponentText(GREEN + "Players in your squad : \n");
        for (final Entry<String, String> entry : SquadHandler.getSquad().entrySet()) {
            final String displayname = entry.getKey();
            final String squadname = entry.getValue();
            imsg.appendText(DARK_GRAY + "- " + GOLD + displayname
                    + (displayname.equals(squadname) ? "" : GREEN + " renamed as : " + GOLD + entry.getValue()) + "\n");
        }
        ChatUtil.addChatMessage(imsg);
    }

    private static void disbandSquad() {
        SquadHandler.clearSquad();
        ChatUtil.addChatMessage(GREEN + "Removed all players from the squad.");
    }

    private static String tryGetNameFromTab(String name) {
        String candidateName = null;
        for (final NetworkPlayerInfo netInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getName().equalsIgnoreCase(name)) {
                candidateName = netInfo.getGameProfile().getName();
            }
        }
        return candidateName == null ? name : candidateName;
    }

}
