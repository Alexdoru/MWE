package fr.alexdoru.megawallsenhancementsmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatUtil {

	public static String getTagMW() {
		return EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "MWEn" + EnumChatFormatting.GOLD + "] ";
	}
	
	public static String getTagNoCheaters() {
		return EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] ";
	}
	
	public static void addChatMessage(IChatComponent msg) {
		
		if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer != null) {			
			Minecraft.getMinecraft().thePlayer.addChatMessage(msg);		
		}
		
	}
	
	public static IChatComponent makeiChatList(String listtitle, IChatComponent imessagebody, int displaypage, int nbpage, String command) {
		
		IChatComponent imsgstart = new ChatComponentText("");
		
		
		if(displaypage > 1) {
		
		imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + bar() + "\n" + "             "));
		
		imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " <<")
				.setChatStyle(new ChatStyle()
						.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage - 1))))
						.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage - 1)))));
		
		imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + " " + listtitle + " (Page " + displaypage + " of " + nbpage + ")"));				
		
		} else {
			
			imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + bar() + "\n" + "                "));							
			imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + " " + listtitle + " (Page " + displaypage + " of " + nbpage + ")"));	
			
		}
		
		if(displaypage < nbpage) { 
			
			imsgstart.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " >>" + "\n")
					.setChatStyle(new ChatStyle()
							.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage + 1))))
							.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage + 1)))));
			
		} else {			
			imsgstart.appendSibling(new ChatComponentText("\n"));		
		}
		
		IChatComponent imsgend = new ChatComponentText(EnumChatFormatting.GOLD + bar());
				
		return imsgstart.appendSibling(imessagebody).appendSibling(imsgend);
	}

	public static String makeChatList(String listtitle, String messagebody, int displaypage, int nbpage, String command) {

		String messagestart = "";

		if(displaypage > 1) { // put previous page arrow

			messagestart = "[\"\",{\"text\":\"" + bar() + "\",\"color\":\"blue\"},{\"text\":\"\\n\"}"

					+ ",{\"text\":\"             \",\"color\":\"white\"}"

					+ ",{\"text\":\" <<\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + " " + (displaypage - 1) 

					+ "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to view page " + (displaypage - 1) + "\",\"color\":\"yellow\"}]}}"

					+ ",{\"text\":\" " + listtitle + " (Page " + displaypage + " of " + nbpage + ")\",\"color\":\"gold\"}" ; // ajouter des boutons if c pas la premiere page etc,

		} else { // don't put previous page arrow

			messagestart = "[\"\",{\"text\":\"" + bar() + "\",\"color\":\"blue\"},{\"text\":\"\\n\"}"

					+ ",{\"text\":\"                " + listtitle + " (Page " + displaypage + " of " + nbpage + ")\",\"color\":\"gold\"}" ;

		}

		if(displaypage < nbpage) { 

			messagestart = messagestart + ",{\"text\":\" >>\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\""  +command + " " + (displaypage + 1) 

					+ "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to view page " + (displaypage + 1) + "\",\"color\":\"yellow\"}]}}" 

					+ ",{\"text\":\"\\n\"}";

		} else {

			messagestart = messagestart + ",{\"text\":\"\\n\"}";

		}

		String messageend = ",{\"text\":\"" + bar() + "\",\"color\":\"blue\"}]" ;

		return messagestart + messagebody + messageend;

	}

	public static String apikeyMissingErrorMsg() {
		return EnumChatFormatting.RED + "You didn't set up your Api key. \n"
				+ EnumChatFormatting.RED + "Connect on Hypixel and type \"/api new\" to get an Api key, the mod should automatically detect the key and save it. \n"
				+ EnumChatFormatting.RED + "If it doesn't, type \"/setapikey <key>\" to setup the key.";
	}
	
	public static String invalidplayernameMsg(String playername) {
		return EnumChatFormatting.RED + "The name " + EnumChatFormatting.YELLOW + playername + EnumChatFormatting.RED + " doesn't exist, it might be a nick.";
	}

	/**
	 * Draws a bar that takes the width of the chat window
	 * @return
	 */
	public static String bar() {

		char separator = '-';
		int chatWidth = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatWidth();
		int separatorWidth = Minecraft.getMinecraft().fontRendererObj.getCharWidth(separator);

		return new String(new char[chatWidth/separatorWidth]).replace("\0", "-");
	}
	
	/**
	 * Return the message with spaces at the start to make the message centered in the chat box
	 * 
	 * @param message
	 * @return
	 */
	public static String centerLine(String message) {

		char space = ' ';
		int chatWidth = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatWidth();
		int separatorWidth = Minecraft.getMinecraft().fontRendererObj.getCharWidth(space);
		int messageWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(message);

		if(messageWidth >= chatWidth)
			return message;

		String separatorText = new String(new char[(chatWidth-messageWidth)/(2*separatorWidth)]).replace("\0", " ");

		return separatorText + message;
	}
	
	/**
	 * Returns the amounts of spaces needed to make a message centered 
	 * 
	 * @param message
	 * @return
	 */
	public static String getSeparatorToCenter(String message) {

		char space = ' ';
		int chatWidth = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatWidth();
		int separatorWidth = Minecraft.getMinecraft().fontRendererObj.getCharWidth(space);
		int messageWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(message);

		if(messageWidth >= chatWidth)
			return "";

		return new String(new char[(chatWidth-messageWidth)/(2*separatorWidth)]).replace("\0", " ");
	}

	/**
	 * Returns a formatted message, the input matrix needs to be square.
	 * If the message cannot be formatted (chat box too small for instance) the unformatted message is returned
	 * 
	 * @param messagematrix
	 * @return
	 */
	public static String alignText(String[][] messagematrix) {

		char separator = ' ';
		int chatWidth = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatWidth();
		int separatorWidth = Minecraft.getMinecraft().fontRendererObj.getCharWidth(separator);
		int columnWidth = 0;
		int maxLineWidth = 0;

		for (String [] line : messagematrix) {
			
			String linemessage = "";
			
			for (String msg : line) {			
				linemessage = linemessage + msg;
				columnWidth = Math.max(columnWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(msg));				
			}
			
			maxLineWidth = Math.max(maxLineWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(linemessage));
			
		}
		
		String leftSeparatorText ="";
		
		if(chatWidth > maxLineWidth) 
			leftSeparatorText = new String(new char[(chatWidth-maxLineWidth)/(2*separatorWidth)]).replace("\0",String.valueOf(separator));
			
		String message = "";
		
		for(int i = 0; i < messagematrix.length; i++) { // lines
			for(int j = 0; j < messagematrix[i].length; j++) { // columns
				
				if(j==0) { // first element on the left
					
					int messageWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(messagematrix[i][j]);				
					message = message + leftSeparatorText + messagematrix[i][j] + new String(new char[(columnWidth-messageWidth)/(separatorWidth)]).replace("\0",String.valueOf(separator));
					
				} else if(j==messagematrix[i].length-1) { // last element on the right

					message = message + messagematrix[i][j] + "\n";
					
				} else { // element in the middle

					int messageWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(messagematrix[i][j]);					
					message = message + messagematrix[i][j] + new String(new char[(columnWidth-messageWidth)/(separatorWidth)]).replace("\0",String.valueOf(separator));
					
				}
				
			}
			
		}	
		
		return message;

	}
	
	/**
	 * Returns the integer as a String with a space for thousands delimiter
	 * @param number
	 * @return
	 */
	public static String formatInt(int number) {
		String str = String.valueOf(number);
		char separator = ' ';
		int iterator = 1;
		String msg = "";
		
		for(int i = str.length()-1; i >= 0 ; i--) {
			
			msg = ( (iterator == 3 && i != 0) ? String.valueOf(separator):"") + str.charAt(i) + msg;
			
			if(iterator == 3 ) {
				iterator = 1;
			} else {
				iterator++;
			}
			
		}
		
		return msg;
	}
	
	/**
	 * Converts int from 1 to 5 to roman
	 * 
	 * @param number
	 * @return
	 */
	public static String intToRoman(int number) {
		
		switch(number) {
		case(1):
			return "I";
		case(2):
			return "II";
		case(3):
			return "III";
		case(4):
			return "IV";
		case(5):
			return "V";
		default:
			return String.valueOf(number);
		
		}
		
	}
	
	public static IChatComponent PlanckeHeaderText(String formattedname, String playername, String titletext) {
		
		return new ChatComponentText(getSeparatorToCenter(formattedname + EnumChatFormatting.GOLD + titletext ))
				.appendSibling(new ChatComponentText(formattedname)
						.setChatStyle(new ChatStyle()
								.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/names " + playername))
								.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click for name history")))))

				.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + titletext)
						.setChatStyle(new ChatStyle()
								.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://plancke.io/hypixel/player/stats/" + playername))
								.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open Plancke in browser")))));
	}
	
	public static IChatComponent makeReportButtons(String playername, String cheat, ClickEvent.Action actionreport, ClickEvent.Action actionwdr) {
		
		return new ChatComponentText(EnumChatFormatting.DARK_GREEN + "Report")
				.setChatStyle(new ChatStyle()
						.setChatClickEvent(new ClickEvent(actionreport, "/report " + playername + " " + cheat))
						.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
										+ EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/report " + playername + " " + cheat + "\n"
										+ EnumChatFormatting.GRAY + "Using the report option won't save the cheater's name in the mod NoCheaters"))))

		.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + " WDR ")
				.setChatStyle(new ChatStyle()
						.setChatClickEvent(new ClickEvent(actionwdr, "/wdr " + playername + " " + cheat))
						.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
										+ EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/wdr " + playername + " " + cheat + "\n"
										+ EnumChatFormatting.GRAY + "Using the wdr option will give you warnings about this player ingame")))));
	}

}
