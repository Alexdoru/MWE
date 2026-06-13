package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.gui.HUDRenderer;
import fr.alexdoru.mwe.utils.MapUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

public class CommandFKCounter extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "fks";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length > 0 && (args[0].equalsIgnoreCase("players") || args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p"))) {

            final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();

            if (fkCounter == null) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "This is not available right now");
                return;
            }

            final StringBuilder strBuilder = new StringBuilder();
            for (final MWTeam team : MWTeam.values()) {
                strBuilder.append(fkCounter.getColorPrefixOfTeam(team))
                        .append(team.getName())
                        .append(EnumChatFormatting.WHITE)
                        .append(": ");
                final Iterator<Map.Entry<String, Integer>> iterator = MapUtil.sortByDecreasingValue(fkCounter.getKillMapOfTeam(team)).entrySet().iterator();
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

            if (MWE.INSTANCE().getFinalKillCounter() == null) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "This is not available right now");
                return;
            }

            sendChatMessage("You shouldn't ask for finals and instead try to final kill everyone to win the game!");

        } else if (args.length > 0 && args[0].equalsIgnoreCase("help")) {

            this.printCommandHelp();

        } else {

            final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();

            if (fkCounter == null) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "This is not available right now");
                return;
            }

            final StringBuilder msg = new StringBuilder();
            final MWTeam[] TEAM_VALUES = MWTeam.values();
            for (int i = 0; i < TEAM_VALUES.length; i++) {
                final MWTeam team = TEAM_VALUES[i];
                msg.append(fkCounter.getColorPrefixOfTeam(team)).append(team.getName()).append(EnumChatFormatting.WHITE).append(": ").append(fkCounter.getKillsOfTeam(team));
                if (i != 3) {
                    msg.append('\n');
                }
            }
            ChatUtil.addChatMessage(msg.toString());

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
        final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
        if (fkCounter == null) return;
        for (final MWTeam team : MWTeam.values()) {
            final Map<String, Integer> killMapOfTeam = fkCounter.getKillMapOfTeam(team);
            final Integer kills = killMapOfTeam.get(playerName);
            if (kills != null) {
                fkCounter.tryRemoveKilledPlayer(playerName, team);
                HUDRenderer.fkCounterHUD.updateDisplayText();
                ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Removed " + fkCounter.getColorPrefixOfTeam(team) + playerName
                        + EnumChatFormatting.GREEN + " with " + EnumChatFormatting.GOLD + kills + EnumChatFormatting.GREEN + " final" + (kills > 1 ? "s" : "")
                        + " from the " + fkCounter.getColorPrefixOfTeam(team) + team.getName() + EnumChatFormatting.GREEN + " team.");
                return;
            }
        }
        ChatUtil.addChatMessage(EnumChatFormatting.RED + "Cannot find " + playerName + " in the FKCounter.");
    }

    private List<String> getPlayerListInKillCounter() {
        final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
        if (fkCounter == null) return Collections.emptyList();
        final ArrayList<String> playerList = new ArrayList<>();
        for (final MWTeam team : MWTeam.values()) {
            final Map<String, Integer> killMapOfTeam = fkCounter.getKillMapOfTeam(team);
            playerList.addAll(killMapOfTeam.keySet());
        }
        return playerList;
    }

}
