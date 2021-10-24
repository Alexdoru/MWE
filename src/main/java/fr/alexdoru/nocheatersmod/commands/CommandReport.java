package fr.alexdoru.nocheatersmod.commands;

import java.util.Arrays;
import java.util.List;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandReport extends CommandBase {
	
	/* cheats recognized by hypixel*/
	public static final String[] recognizedcheats = {"aura","aimbot","bhop","velocity","reach","speed","ka","killaura","forcefield","antiknockback","autoclicker","fly","dolphin","jesus"};
	/* cheats for the tabcompletion*/
	public static final String[] cheatsArray = {"aura","aimbot","bhop","velocity","reach","speed","ka","killaura","forcefield","autoblock","antiknockback","autoclicker","fly","dolphin","jesus","keepsprint","noslowdown"};
	public static final List<String> cheatsList = Arrays.asList(cheatsArray);
	// when reporting with the book on hypixel
	// if you select cheating and the confirm
	// it send /report playername -b CTG -C
	// but it opens the book the send thank you

	@Override
	public String getCommandName() {
		return "report";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/report <player> <cheats>";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		//return args.length == 1 ? ( FKCounterMod.isInMwGame() && !GameInfoGrabber.isitPrepPhase() ? getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()) : null ) : (args.length > 1 ? getListOfStringsMatchingLastWord(args, cheats) : null);
		return (args.length > 1 ? getListOfStringsMatchingLastWord(args, cheatsArray) : null);

	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		
		if (args.length < 1) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		}
		
		String msg = "/report " + args[0];
		
		for (int i = 1; i < args.length; i++) {
			
			if(args[i].equalsIgnoreCase("bhop")) {

				msg = msg + " bhop aura reach velocity speed antiknockback";
				
			} else if(args[i].equalsIgnoreCase("autoblock")) {

				msg = msg + " killaura";
				
			} else if(args[i].equalsIgnoreCase("noslowdown") || args[i].equalsIgnoreCase("keepsprint")) {

				msg = msg + " velocity";
				
			} else {

				msg = msg + " " + args[i]; //reconstructs the message to send it to the server

			}
			
		}
		
		(Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg);

	}

}
