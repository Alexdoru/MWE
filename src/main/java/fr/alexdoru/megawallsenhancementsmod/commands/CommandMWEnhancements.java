package fr.alexdoru.megawallsenhancementsmod.commands;

import java.util.Arrays;
import java.util.List;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.gui.GeneralConfigGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandMWEnhancements extends CommandBase {

	@Override
	public String getCommandName() {
		return "mwenhancements";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/mwenhancements";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GeneralConfigGuiScreen()), 1);
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
	
	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.<String>asList(new String[] {"megawallsenhancements"});
	}

}
