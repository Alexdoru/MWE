package fr.alexdoru.megawallsenhancementsmod.commands;

import java.util.Arrays;
import java.util.List;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class CommandHypixelShout extends CommandBase {
	
	private static final String guide_url = "https://hypixel.net/threads/the-complete-mega-walls-guide.3489088/";

	@Override
	public String getCommandName() {
		return "shout";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/shout <message>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		
		String msg = "/shout";
		
		for(String arg : args) {
			
			if(arg.equalsIgnoreCase("/guide")) {
				msg += " " + guide_url;
			} else {
				msg += " " + arg;
			}
			
		}
		
		(Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);
	}

	@Override
	public List<String> getCommandAliases() {
		return Arrays.<String>asList(new String[] {"SHOUT","Shout"});
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

}