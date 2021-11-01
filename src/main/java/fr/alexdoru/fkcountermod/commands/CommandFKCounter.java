package fr.alexdoru.fkcountermod.commands;

import fr.alexdoru.fkcountermod.gui.FKConfigGuiScreen;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static fr.alexdoru.fkcountermod.events.KillCounter.*;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;

public class CommandFKCounter extends CommandBase {

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

            new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(new FKConfigGuiScreen()), 1);

        } else if (args.length > 0 && (args[0].equalsIgnoreCase("players") || args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p"))) {

            if (getGameId() == null) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This is not available right now"));
                return;
            }
            String msg = "";
            msg += getColorPrefixFromTeam(RED_TEAM) + getTeamNameFromTeam(RED_TEAM) + EnumChatFormatting.WHITE + ": " +
                    (getPlayers(RED_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", "))) + "\n";

            msg += getColorPrefixFromTeam(GREEN_TEAM) + getTeamNameFromTeam(GREEN_TEAM) + EnumChatFormatting.WHITE + ": " +
                    (getPlayers(GREEN_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", "))) + "\n";

            msg += getColorPrefixFromTeam(YELLOW_TEAM) + getTeamNameFromTeam(YELLOW_TEAM) + EnumChatFormatting.WHITE + ": " +
                    (getPlayers(YELLOW_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", "))) + "\n";

            msg += getColorPrefixFromTeam(BLUE_TEAM) + getTeamNameFromTeam(BLUE_TEAM) + EnumChatFormatting.WHITE + ": " +
                    (getPlayers(BLUE_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));

            addChatMessage(new ChatComponentText(msg));

        } else if (args.length > 0 && args[0].equalsIgnoreCase("say")) {

            if (getGameId() == null) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This is not available right now"));
                return;
            }

            if (args.length == 1) {

                String msg = "";

                msg += getTeamNameFromTeam(RED_TEAM) + ": " + getKills(RED_TEAM) + ", ";
                msg += getTeamNameFromTeam(GREEN_TEAM) + ": " + getKills(GREEN_TEAM) + ", ";
                msg += getTeamNameFromTeam(YELLOW_TEAM) + ": " + getKills(YELLOW_TEAM) + ", ";
                msg += getTeamNameFromTeam(BLUE_TEAM) + ": " + getKills(BLUE_TEAM);

                (Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);

            } else if (args.length == 2 && args[1].equalsIgnoreCase("red")) {

                String msg = getTeamNameFromTeam(RED_TEAM) + ": " + (getPlayers(RED_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
                (Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);

            } else if (args.length == 2 && args[1].equalsIgnoreCase("green")) {

                String msg = getTeamNameFromTeam(GREEN_TEAM) + ": " + (getPlayers(GREEN_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
                (Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);

            } else if (args.length == 2 && args[1].equalsIgnoreCase("yellow")) {

                String msg = getTeamNameFromTeam(YELLOW_TEAM) + ": " + (getPlayers(YELLOW_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
                (Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);

            } else if (args.length == 2 && args[1].equalsIgnoreCase("blue")) {

                String msg = getTeamNameFromTeam(BLUE_TEAM) + ": " + (getPlayers(BLUE_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
                (Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);

            }

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
            String msg = "";
            msg += getColorPrefixFromTeam(RED_TEAM) + getTeamNameFromTeam(RED_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(RED_TEAM) + "\n";
            msg += getColorPrefixFromTeam(GREEN_TEAM) + getTeamNameFromTeam(GREEN_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(GREEN_TEAM) + "\n";
            msg += getColorPrefixFromTeam(YELLOW_TEAM) + getTeamNameFromTeam(YELLOW_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(YELLOW_TEAM) + "\n";
            msg += getColorPrefixFromTeam(BLUE_TEAM) + getTeamNameFromTeam(BLUE_TEAM) + EnumChatFormatting.WHITE + ": " + getKills(BLUE_TEAM);
            addChatMessage(new ChatComponentText(msg));

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
