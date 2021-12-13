package fr.alexdoru.fkcountermod.commands;

import fr.alexdoru.fkcountermod.gui.FKConfigGuiScreen;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;
import java.util.stream.Collectors;

import static fr.alexdoru.fkcountermod.events.KillCounter.*;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;

public class CommandFKCounter extends CommandBase {

    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public String getCommandName() {
        return "fks";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/fks <help|p|players|say|settings>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("settings")) {

            new DelayedTask(() -> mc.displayGuiScreen(new FKConfigGuiScreen(null)), 1);

        } else if (args.length > 0 && (args[0].equalsIgnoreCase("players") || args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p"))) {

            if (getGameId() == null) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This is not available right now"));
                return;
            }

            StringBuilder strBuilder = new StringBuilder();
            for (int TEAM = RED_TEAM; TEAM <= BLUE_TEAM; TEAM++) {
                strBuilder.append(getColorPrefixFromTeam(TEAM))
                        .append(getTeamNameFromTeam(TEAM))
                        .append(EnumChatFormatting.WHITE)
                        .append(": ");
                for (Iterator<Map.Entry<String, Integer>> iterator = sortByDecreasingValue1(getPlayers(TEAM)).entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, Integer> entry = iterator.next();
                    String name = entry.getKey();
                    String squadname = SquadEvent.getSquad().get(name);
                    if (squadname != null) {
                        strBuilder.append(squadname).append(" (").append(entry.getValue()).append(")");
                    } else {
                        strBuilder.append(name).append(" (").append(entry.getValue()).append(")");
                    }
                    if (iterator.hasNext()) {
                        strBuilder.append(", ");
                    }
                }
                strBuilder.append("\n");
            }

            addChatMessage(new ChatComponentText(strBuilder.toString()));

        } else if (args.length > 0 && args[0].equalsIgnoreCase("say")) {

            if (getGameId() == null) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This is not available right now"));
                return;
            }

            StringBuilder strBuilder = new StringBuilder();

            if (args.length == 1) {

                HashMap<Integer, Integer> sortedmap = getSortedTeamKillsMap();
                int i = 0;
                for (Map.Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                    if (i != 0) {
                        strBuilder.append(", ");
                    }
                    strBuilder.append(getTeamNameFromTeam(entry.getKey())).append(": ").append(entry.getValue());
                    i++;
                }

            } else if (args.length == 2 && args[1].equalsIgnoreCase("red")) {
                strBuilder.append(getTeamNameFromTeam(RED_TEAM)).append(": ").append(sortByDecreasingValue1(getPlayers(RED_TEAM)).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
            } else if (args.length == 2 && args[1].equalsIgnoreCase("green")) {
                strBuilder.append(getTeamNameFromTeam(GREEN_TEAM)).append(": ").append(sortByDecreasingValue1(getPlayers(GREEN_TEAM)).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
            } else if (args.length == 2 && args[1].equalsIgnoreCase("yellow")) {
                strBuilder.append(getTeamNameFromTeam(YELLOW_TEAM)).append(": ").append(sortByDecreasingValue1(getPlayers(YELLOW_TEAM)).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
            } else if (args.length == 2 && args[1].equalsIgnoreCase("blue")) {
                strBuilder.append(getTeamNameFromTeam(BLUE_TEAM)).append(": ").append(sortByDecreasingValue1(getPlayers(BLUE_TEAM)).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
            }

            mc.thePlayer.sendChatMessage(strBuilder.toString());

        } else if (args.length > 0 && args[0].equalsIgnoreCase("help")) {

            addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + "\n"
                    + EnumChatFormatting.RED + "/fks : prints the amount of finals per team in the chat \n"
                    + EnumChatFormatting.RED + "/fks p or players : prints the amount of finals per player in the chat \n "
                    + EnumChatFormatting.RED + "/fks say : makes you send a message in the chat with the amount of finals per team \n"
                    + EnumChatFormatting.RED + "/fks settings : opens the settings GUI"));

        } else {
            if (getGameId() == null) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This is not available right now"));
                return;
            }
            String strBuilder = getColorPrefixFromTeam(RED_TEAM) + getTeamNameFromTeam(RED_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(RED_TEAM) + "\n" +
                    getColorPrefixFromTeam(GREEN_TEAM) + getTeamNameFromTeam(GREEN_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(GREEN_TEAM) + "\n" +
                    getColorPrefixFromTeam(YELLOW_TEAM) + getTeamNameFromTeam(YELLOW_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(YELLOW_TEAM) + "\n" +
                    getColorPrefixFromTeam(BLUE_TEAM) + getTeamNameFromTeam(BLUE_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(BLUE_TEAM);
            addChatMessage(new ChatComponentText(strBuilder));

        }

    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("finalkillcounter");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        String[] fksarguments = {"players", "say", "settings", "help"};
        String[] colors = {"red", "green", "yellow", "blue"};
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, fksarguments) : args.length == 2 ? getListOfStringsMatchingLastWord(args, colors) : null;
    }

}
