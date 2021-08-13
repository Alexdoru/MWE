package fr.alexdoru.megawallsenhancementsmod.commands;

import java.util.Arrays;
import java.util.List;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.events.KillCooldownEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandKill extends CommandBase {

	@Override
	public String getCommandName() {
		return "kill";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/kill";
	}
	
	@Override
	public List<String> getCommandAliases() {
		return Arrays.<String>asList(new String[] {"Kill"});
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		(Minecraft.getMinecraft()).thePlayer.sendChatMessage("/kill");
		if(FKCounterMod.isInMwGame()) {
			KillCooldownEvent.drawCooldownGui();
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

}
