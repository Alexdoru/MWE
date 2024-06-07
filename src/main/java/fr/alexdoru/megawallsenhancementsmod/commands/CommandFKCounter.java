package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiscreens.FKConfigGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.FKCounterHUD;
import fr.alexdoru.megawallsenhancementsmod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.MapUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

import static fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter.*;

public class CommandFKCounter extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "fks";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("settings")) {

            new DelayedTask(() -> mc.displayGuiScreen(new FKConfigGuiScreen(null)));

        } else if (args.length > 0 && (args[0].equalsIgnoreCase("players") || args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p"))) {

            if (getGameId() == null) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "This is not available right now");
                return;
            }

            final StringBuilder strBuilder = new StringBuilder();
            for (int TEAM = RED_TEAM; TEAM <= BLUE_TEAM; TEAM++) {
                strBuilder.append(getColorPrefixFromTeam(TEAM))
                        .append(getTeamNameFromTeam(TEAM))
                        .append(EnumChatFormatting.WHITE)
                        .append(": ");
                final Iterator<Map.Entry<String, Integer>> iterator = MapUtil.sortByDecreasingValue(getPlayers(TEAM)).entrySet().iterator();
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
            final String msg = getColorPrefixFromTeam(RED_TEAM) + getTeamNameFromTeam(RED_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(RED_TEAM) + "\n" +
                    getColorPrefixFromTeam(GREEN_TEAM) + getTeamNameFromTeam(GREEN_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(GREEN_TEAM) + "\n" +
                    getColorPrefixFromTeam(YELLOW_TEAM) + getTeamNameFromTeam(YELLOW_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(YELLOW_TEAM) + "\n" +
                    getColorPrefixFromTeam(BLUE_TEAM) + getTeamNameFromTeam(BLUE_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(BLUE_TEAM);
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
                    removeKilledPlayer(playerName, team);
                    FKCounterHUD.instance.updateDisplayText();
                    ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Removed " + getColorPrefixFromTeam(team) + playerName
                            + EnumChatFormatting.GREEN + " with " + EnumChatFormatting.GOLD + kills + EnumChatFormatting.GREEN + " final" + (kills > 1 ? "s" : "") + " from the " + getColorPrefixFromTeam(team) + getTeamNameFromTeam(team) + EnumChatFormatting.GREEN + " team.");
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
