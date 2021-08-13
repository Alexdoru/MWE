package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.utils.ClipboardUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandCopyToClipboard extends CommandBase {

	@Override
	public String getCommandName() {
		return "copytoclipboard";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/copytoclipboard <text>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		
		ClipboardUtil.copyString(args[0]);
				
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

}
