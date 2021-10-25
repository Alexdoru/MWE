package fr.alexdoru.nocheatersmod.commands;

import java.util.ArrayList;
import java.util.Date;

import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandSendReportAgain extends CommandBase {

	@Override
	public String getCommandName() {
		return "sendreportagain";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/sendreportagain <UUID> <playerName>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

		if (args.length < 2) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		}

		// format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc
		String uuid = args[0];
		String playername = args[1];
		WDR wdr = WdredPlayers.getWdredMap().get(uuid);
		String cheatmessage = "";
		ArrayList<String> constructCheats = new ArrayList<String>();

		if(wdr.hacks.get(0).charAt(0) == '-') { // if timestamped report

			int j=0;

			for(int i=0; i < wdr.hacks.size(); i++) { 
				if(wdr.hacks.get(i).charAt(0) == '-') {	
					j=i;
				} else if(i>j+3) { // cheats
					constructCheats.add(wdr.hacks.get(i));
				} 
			}

		} else {

			for (String hack : wdr.hacks) {
				constructCheats.add(hack);
			}

		}

		ArrayList<String> arrayCheats = WdredPlayers.removeDuplicates(constructCheats); // Removes the duplicate cheats, in timestamped reports for instance

		for (String hack : arrayCheats) { // verify unaccepted terms an replace tem

			if (hack.equals("keepsprint") || hack.equals("noslowdown")) {

				cheatmessage = cheatmessage + " " + "velocity";

			} else if (hack.equals("autoblock")) {

				cheatmessage = cheatmessage + " " + "aura";

			} else if (hack.equals("bhop")) {

				cheatmessage = cheatmessage + " bhop aura reach velocity speed antiknockback";

			} else if (hack.equals("cheating") || hack.equals("fastbreak") || hack.contains("stalk") || hack.equals("nick")) {

				cheatmessage = cheatmessage + "";

			} else {

				for (String offhack : CommandReport.recognizedcheats) {
					if(hack.equals(offhack)) {
						cheatmessage = cheatmessage + " " + hack;
					}
				}

			}

		}

		String message = "/wdr " + playername + cheatmessage;
		(Minecraft.getMinecraft()).thePlayer.sendChatMessage(message);
		long timestamp = (new Date()).getTime();
		WdredPlayers.getWdredMap().put(uuid, new WDR(timestamp, wdr.hacks));
		return;

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

}
