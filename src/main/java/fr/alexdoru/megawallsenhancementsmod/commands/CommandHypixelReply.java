package fr.alexdoru.megawallsenhancementsmod.commands;

import java.util.Arrays;
import java.util.List;

import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class CommandHypixelReply extends CommandBase {

	@Override
	public String getCommandName() {
		return "r";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/r <message>";
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		(Minecraft.getMinecraft()).thePlayer.sendChatMessage("/r " + CommandBase.buildString(args, 0));	
	}
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		//return (GameInfoGrabber.isitPrepPhase() ? null : getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()));
		return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
	}
	
	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.<String>asList(new String[] {"R"});
	}
	
}
