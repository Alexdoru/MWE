package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandSetupApiKey extends CommandBase {

	@Override
	public String getCommandName() {
		return "setapikey";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/setapikey <key>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		
		if(args.length != 1) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + "\n"
					+ EnumChatFormatting.RED + "Connect on Hypixel and type \"/api new\" to get an Api key"));
			return;	
		} else {
			HypixelApiKeyUtil.setApiKey(args[0]);
			return;	
		}
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

}
