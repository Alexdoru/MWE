package fr.alexdoru.megawallsenhancementsmod.events;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.nocheatersmod.commands.CommandReport;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatEvents {

	private static final String GENERAL_START_MESSAGE = "The game starts in 1 second!";
	private static final String OWN_WITHER_DEATH_MESSAGE = "Your wither has died. You can no longer respawn!";

	private static final String api_key_msg = "Your new API key is ";
	private static final int api_key_msg_len = api_key_msg.length();

	@SubscribeEvent
	public void onChatMessage(ClientChatReceivedEvent event) {

		/*
		 * returns if it is not a chat message
		 */
		if(event.type != 0) {return;}

		String msg = event.message.getUnformattedText();
		String fmsg = event.message.getFormattedText();

		/*
		 *  cancels hunger message in mega walls
		 */
		if (msg.equals("Get to the middle to stop the hunger!")) { 
			event.setCanceled(true);
			return;
		}   	      	    

		if(msg.equals(GENERAL_START_MESSAGE)) {
			GameInfoGrabber.saveinfoOnGameStart();
			SquadEvent.formSquad();
			return;
		}

		if(msg.equals(OWN_WITHER_DEATH_MESSAGE)) {
			KillCooldownEvent.hideGUI();
			return;
		}

		/*
		 *  automatically sets up the api key on hypixel when you type /api new
		 */
		if(msg.length() > api_key_msg_len && msg.substring(0, api_key_msg_len).equals(api_key_msg)) {	    	
			String api_key = msg.substring(api_key_msg_len, msg.length());	
			HypixelApiKeyUtil.setApiKey(api_key);	    	    		    	
			return;
		}

		if(KillCounter.processMessage(fmsg, msg)) {return;}
		if(ArrowHitLeapHitEvent.processMessage(msg)) {return;}
		if(parseReportMessage(msg)) {return;}
		if(MWGameStatsEvent.processMessage(msg)) {return;}
	}

	private static boolean parseReportMessage(String msgIn) {
		
		Matcher matcher1 = Pattern.compile("^\\[shout\\].+?(\\w+) is b?hop?ping",Pattern.CASE_INSENSITIVE).matcher(msgIn);
		Matcher matcher2 = Pattern.compile("^\\[shout\\].+?(?:wdr|report) (\\w+) (\\w+)",Pattern.CASE_INSENSITIVE).matcher(msgIn);
	
		if(matcher1.matches()) {
			String name = matcher1.group(1);
			String cheat = "bhop";
			if(isAValidName(name)) {
				printReportSuggestion(name, cheat);
			}
			return true;
		} else if(matcher2.matches()) {
			String name = matcher2.group(1);
			String cheat = matcher2.group(2);
			if(isAValidCheat(cheat) || isAValidName(name)) {
				printReportSuggestion(name, cheat);
			}
			return true;
		} 

		return false;
		
	}
	
	private static void printReportSuggestion(String playername, String cheat) {
		IChatComponent imsg = new ChatComponentText(ChatUtil.getTag() + EnumChatFormatting.DARK_GREEN + "Command suggestion : ")
				.appendSibling(ChatUtil.makeReportButtons(playername, cheat, ClickEvent.Action.SUGGEST_COMMAND, ClickEvent.Action.SUGGEST_COMMAND));
		ChatUtil.addChatMessage(imsg);
	}
	
	private static boolean isAValidName(String playername) {
		
		Collection<NetworkPlayerInfo> playerCollection = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
		
		for (NetworkPlayerInfo networkplayerinfo : playerCollection)
        {
            if (networkplayerinfo.getGameProfile().getName().equalsIgnoreCase(playername))
            {
                return true;
            }
        }
	
		return false;
	}
	
	private static boolean isAValidCheat(String cheat) {
		return CommandReport.cheatsList.contains(cheat);
	}
	
}
