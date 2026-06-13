package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.gui.HUDRenderer;
import fr.alexdoru.mwe.utils.MapUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

import static fr.alexdoru.mwe.features.FinalKillCounter.*;

public class CommandFKCounter extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "fks";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length > 0 && (args[0].equalsIgnoreCase("players") || args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p"))) {

            if (getGameId() == null) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "This is not available right now");
                return;
            }

            final StringBuilder strBuilder = new StringBuilder();
            for (int TEAM = RED_TEAM; TEAM <= BLUE_TEAM; TEAM++) {
                strBuilder.append(getColorPrefixOfTeam(TEAM))
                        .append(getNameOfTeam(TEAM))
                        .append(EnumChatFormatting.WHITE)
                        .append(": ");
                final Iterator<Map.Entry<String, Integer>> iterator = MapUtil.sortByDecreasingValue(getPlayersOfTeam(TEAM)).entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry<String, Integer> entry = iterator.next();
                    strBuilder.append(SquadHandler.getSquadname(entry.getKey())).append(" (").append(entry.getValue()).append(")");
                    if (iterator.hasNext()) {
                        strBuilder.append(", ");
                    }
                }
                strBuilder.append("\n");
            }

            ChatUtil.addChatMessage(strBuilder.toString());

        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {

            removePlayer(args[1]);

        } else if (args.length > 0 && args[0].equalsIgnoreCase("say")) {

            if (getGameId() == null) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "This is not available right now");
                return;
            }

            sendChatMessage("You shouldn't ask for finals and instead try to final kill everyone to win the game!");

        } else if (args.length > 0 && args[0].equalsIgnoreCase("help")) {

            this.printCommandHelp();

        } else {

            if (getGameId() == null) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "This is not available right now");
                return;
            }
            final String msg = getColorPrefixOfTeam(RED_TEAM) + getNameOfTeam(RED_TEAM) + EnumChatFormatting.WHITE + ": " + getKillsOfTeam(RED_TEAM) + "\n" +
                    getColorPrefixOfTeam(GREEN_TEAM) + getNameOfTeam(GREEN_TEAM) + EnumChatFormatting.WHITE + ": " + getKillsOfTeam(GREEN_TEAM) + "\n" +
                    getColorPrefixOfTeam(YELLOW_TEAM) + getNameOfTeam(YELLOW_TEAM) + EnumChatFormatting.WHITE + ": " + getKillsOfTeam(YELLOW_TEAM) + "\n" +
                    getColorPrefixOfTeam(BLUE_TEAM) + getNameOfTeam(BLUE_TEAM) + EnumChatFormatting.WHITE + ": " + getKillsOfTeam(BLUE_TEAM);
            ChatUtil.addChatMessage(msg);

        }

    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("finalkillcounter");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final String[] fksarguments = {"players", "remove", "settings", "help"};
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, fksarguments);
        }
        if (args.length == 2) {
            if (args[0].equals("remove")) {
                return getListOfStringsMatchingLastWord(args, getPlayerListInKillCounter());
            }
        }
        return null;
    }

    @Override
    protected void printCommandHelp() {
        ChatUtil.addChatMessage(
                EnumChatFormatting.AQUA + ChatUtil.bar() + "\n"
                        + ChatUtil.centerLine(EnumChatFormatting.GOLD + "Fks Help") + "\n\n"
                        + EnumChatFormatting.YELLOW + "/fks" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the amount of finals per team in the chat\n"
                        + EnumChatFormatting.YELLOW + "/fks players" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the amount of finals per player in the chat\n"
                        + EnumChatFormatting.YELLOW + "/fks settings" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "opens the settings GUI\n"
                        + EnumChatFormatting.AQUA + ChatUtil.bar()
        );
    }

    private void removePlayer(String playerName) {
        final HashMap<String, Integer>[] teamKillsArray = getTeamKillsArray();
        if (teamKillsArray != null) {
            for (int team = 0; team < TEAMS; team++) {
                final Integer kills = teamKillsArray[team].get(playerName);
                if (kills != null) {
                    tryRemoveKilledPlayer(playerName, team);
                    HUDRenderer.fkCounterHUD.updateDisplayText();
                    ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Removed " + getColorPrefixOfTeam(team) + playerName
                            + EnumChatFormatting.GREEN + " with " + EnumChatFormatting.GOLD + kills + EnumChatFormatting.GREEN + " final" + (kills > 1 ? "s" : "") + " from the " + getColorPrefixOfTeam(team) + getNameOfTeam(team) + EnumChatFormatting.GREEN + " team.");
                    return;
                }
            }
        }
        ChatUtil.addChatMessage(EnumChatFormatting.RED + "Cannot find " + playerName + " in the FKCounter.");
    }

    private ArrayList<String> getPlayerListInKillCounter() {
        final ArrayList<String> playerList = new ArrayList<>();
        final HashMap<String, Integer>[] teamKillsArray = getTeamKillsArray();
        if (teamKillsArray == null) {
            return playerList;
        }
        for (final HashMap<String, Integer> teamMap : teamKillsArray) {
            for (final Map.Entry<String, Integer> entry : teamMap.entrySet()) {
                playerList.add(entry.getKey());
            }
        }
        return playerList;
    }

}
