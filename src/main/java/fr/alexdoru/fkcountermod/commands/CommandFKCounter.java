package fr.alexdoru.fkcountermod.commands;

import java.util.List;
import java.util.stream.Collectors;

import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.fkcountermod.gui.FKCounterSettingsGui;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

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
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

		if(args.length > 0 && args[0].equalsIgnoreCase("settings")) {

			new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(new FKCounterSettingsGui()), 1);

		} else if(args.length > 0 && ( args[0].equalsIgnoreCase("players") || args[0].equalsIgnoreCase("player")  || args[0].equalsIgnoreCase("p") ) ) {

			if(KillCounter.getGameId() == null) {				
				return;
			}
			String msg = "";
			msg += KillCounter.getRedPrefix() + "RED" + EnumChatFormatting.WHITE + ": " + 
					(KillCounter.getPlayers(KillCounter.RED_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", "))) + "\n";
			msg += KillCounter.getGreenPrefix() + "GREEN" + EnumChatFormatting.WHITE + ": " + 
					(KillCounter.getPlayers(KillCounter.GREEN_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", "))) + "\n";
			msg += KillCounter.getYellowPrefix() + "YELLOW" + EnumChatFormatting.WHITE + ": " + 
					(KillCounter.getPlayers(KillCounter.YELLOW_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", "))) + "\n";
			msg += KillCounter.getBluePrefix() + "BLUE" + EnumChatFormatting.WHITE + ": " + 
					(KillCounter.getPlayers(KillCounter.BLUE_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));

			ChatUtil.addChatMessage(new ChatComponentText(msg));
			return;

		} else if(args.length > 0 && args[0].equalsIgnoreCase("say")) {

			if(KillCounter.getGameId() == null) {				
				return;
			}

			if(args.length == 1) {

				String msg = "";

				msg += "Red: " + KillCounter.getKills(KillCounter.RED_TEAM) +", ";
				msg += "Green: " + KillCounter.getKills(KillCounter.GREEN_TEAM) +", ";
				msg += "Yellow: " + KillCounter.getKills(KillCounter.YELLOW_TEAM) +", ";
				msg += "Blue: " + KillCounter.getKills(KillCounter.BLUE_TEAM);

				(Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);
				return;

			} else if(args.length == 2 && args[1].equalsIgnoreCase("red")) {
				
				String msg = "Red : " + (KillCounter.getPlayers(KillCounter.RED_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
				(Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);
				return;

			} else if(args.length == 2 && args[1].equalsIgnoreCase("green")) {
				
				String msg = "Green : " + (KillCounter.getPlayers(KillCounter.GREEN_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
				(Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);
				return;

			} else if(args.length == 2 && args[1].equalsIgnoreCase("yellow")) {
				
				String msg = "Yellow : " + (KillCounter.getPlayers(KillCounter.YELLOW_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
				(Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);
				return;

			} else if(args.length == 2 && args[1].equalsIgnoreCase("blue")) {
				
				String msg = "Blue : " + (KillCounter.getPlayers(KillCounter.BLUE_TEAM).entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
				(Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);
				return;

			}


		} else if(args.length > 0 && args[0].equalsIgnoreCase("help")) {

			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + "\n"
					+ EnumChatFormatting.RED + "/fks : prints the amount of finals per team in the chat \n"
					+ EnumChatFormatting.RED + "/fks p or players : prints the amount of finals per player in the chat \n "
					+ EnumChatFormatting.RED + "/fks say : makes you send a message in the chat with the amount of finals per team \n"
					+ EnumChatFormatting.RED + "/fks settings : opens the settings GUI"));
			return;

		} else {
			if(KillCounter.getGameId() == null) {				
				return;
			}
			String msg = "";
			msg += KillCounter.getRedPrefix() + "RED" + EnumChatFormatting.WHITE + ": " + KillCounter.getKills(KillCounter.RED_TEAM) + "\n";
			msg += KillCounter.getGreenPrefix() + "GREEN" + EnumChatFormatting.WHITE + ": " + KillCounter.getKills(KillCounter.GREEN_TEAM) + "\n";
			msg += KillCounter.getYellowPrefix() + "YELLOW" + EnumChatFormatting.WHITE + ": " + KillCounter.getKills(KillCounter.YELLOW_TEAM) + "\n";
			msg += KillCounter.getBluePrefix() + "BLUE" + EnumChatFormatting.WHITE + ": " + KillCounter.getKills(KillCounter.BLUE_TEAM);

			ChatUtil.addChatMessage(new ChatComponentText(msg));
			return;

		}

	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		String[] fksarguments = {"players","say","settings","help"};
		String[] colors = {"red","green","yellow","blue"};
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, fksarguments) : args.length == 2 ? getListOfStringsMatchingLastWord(args, colors) : null;
	}


}
