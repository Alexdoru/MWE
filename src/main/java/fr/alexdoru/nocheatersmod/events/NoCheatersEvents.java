package fr.alexdoru.nocheatersmod.events;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.nocheatersmod.dataobjects.WDR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoCheatersEvents {

	public static boolean togglewarnings = true;
	private static int ticks = 0;
	public static HashMap<String, WDR> wdred = new HashMap<String, WDR>();
	public static File wdrsFile;
	public static final IChatComponent iprefix = (IChatComponent) new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "\u26a0 ");
	public static final String prefix = iprefix.getFormattedText();
	public static final IChatComponent iprefix_bhop = (IChatComponent) new ChatComponentText(EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + "\u26a0 ");
	public static final String prefix_bhop = iprefix_bhop.getFormattedText();

	@SubscribeEvent
	public void onGui(GuiOpenEvent event) { //resets the ticks counter when you have a loading screen
		if (event.gui instanceof GuiDownloadTerrain)
			ticks = 0; 
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {  // scans your world 2 seconds avec joining
		if (!togglewarnings || !(Minecraft.getMinecraft()).inGameHasFocus) {
			return; 
		}
		if (ticks == 39) {
			scanCurrentWorld();
		} else if (ticks < 39) {
			ticks++;
		} 
	}

	@SubscribeEvent
	public void onPlayerJoin(EntityJoinWorldEvent event) { // check chaque nouveau joueur qui est rendu dans le jeu

		if (ticks < 40 || (Minecraft.getMinecraft()).thePlayer == null || !(event.entity instanceof EntityPlayer)) 
			return; 

		EntityPlayer player = (EntityPlayer)event.entity;
		String uuid = player.getUniqueID().toString().replace("-", "");
		WDR wdr = wdred.get(uuid);
		boolean printmsg = false;

		if (wdr != null) {

			if(wdr.hacks.contains("bhop")) {
				player.addPrefix(iprefix_bhop);
				player.refreshDisplayName();
				printmsg = true;
			} else {
				player.addPrefix(iprefix);
				player.refreshDisplayName();
				printmsg = true;
			}

			if(togglewarnings && printmsg) {
				String playerName = player.getName();
				String chatmessage = createwarningmessage(uuid, playerName, wdr) ;
				(Minecraft.getMinecraft()).thePlayer.addChatComponentMessage(IChatComponent.Serializer.jsonToComponent(chatmessage));
			}

		} else {

			IChatComponent imsg = CommandScanGame.getScanmap().get(uuid);

			if(imsg != null && !imsg.equals(CommandScanGame.nomatch)) {
				player.addPrefix(CommandScanGame.iprefix);
				player.refreshDisplayName();
			}

		}

	}

	public static void scanCurrentWorld() {

		try {

			Collection<NetworkPlayerInfo> playerCollection = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();

			for (NetworkPlayerInfo networkPlayerInfo : playerCollection) {	        

				String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
				WDR wdr = wdred.get(uuid);

				if (wdr == null) 
					continue; 

				String playerName = networkPlayerInfo.getGameProfile().getName();
				String chatmessage = createwarningmessage(uuid, playerName, wdr);
				(Minecraft.getMinecraft()).thePlayer.addChatComponentMessage(IChatComponent.Serializer.jsonToComponent(chatmessage));
			}

		} catch (Exception exception) {

			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
					EnumChatFormatting.RED + "Error, scan incomplete"));
		}

		ticks++;
	}

	public static void saveReportedPlayers() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(wdrsFile));
			for (Entry<String, WDR> entry : NoCheatersEvents.wdred.entrySet()) {

				String uuid = entry.getKey();
				WDR wdr = entry.getValue(); 
				writer.write(uuid + " " + wdr.timestamp);
				for (String hack : wdr.hacks) {
					writer.write(" " + hack); 
				}
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadReportedPlayers() { 
		if (!wdrsFile.exists())
			return; 
		try {

			long datenow = (new Date()).getTime();

			BufferedReader reader = new BufferedReader(new FileReader(wdrsFile));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				String[] split = line.split(" ");
				if (split.length >= 3) {

					long timestamp = 0L;
					try {
						timestamp = Long.parseLong(split[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}

					ArrayList<String> hacks = transformOldReports(split, datenow);

					wdred.put(split[0], new WDR(timestamp, hacks));
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Transforms the timestamped reports older than a month in normal reports
	 * 
	 * @param split
	 * @return
	 */
	private static ArrayList<String> transformOldReports(String[] split, long datenow) { 

		ArrayList<String> hacks = new ArrayList<String>();

		if(split[2].charAt(0) == '-' && datenow > Long.parseLong(split[5]) + 2592000000L ) {

			int j = 0; // indice of timestamp
			for (int i = 2; i < split.length; i++) {

				if(split[i].charAt(0) == '-') { // serverID 
					j=i;
				} else if(i>j+3) { // cheats
					hacks.add(split[i]);
				} 

			}

			return removeDuplicates(hacks);

		} else {

			for (int i = 2; i < split.length; i++) {
				hacks.add(split[i]);
			}
			return hacks;
		}

	}

	public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {

		ArrayList<T> newList = new ArrayList<T>();

		for (T element : list) {
			if (!newList.contains(element)) {
				newList.add(element);
			}
		}

		return newList;

	}

	public static String createwarningmessage(String uuid, String playername, WDR wdr) {
		// format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc
		if(wdr.hacks.get(0).charAt(0) == '-') { // is a timestamped report

			String [] formattedmessageArray = createPlayerTimestampedMsg(playername, wdr, "light_purple");
			String allCheats = formattedmessageArray[1];

			String message = "[\"\",{\"text\":\"Warning : \",\"color\":\"red\"}" + formattedmessageArray[0] + ",{\"text\":\" joined,\",\"color\":\"gray\"}";

			if((new Date()).getTime() - wdr.timestamp > 2*60*60*1000L ) { // montre le bouton pour re-report si l'ancien report est plus vieux que 2heures

				message = message + ",{\"text\":\" Report again\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/sendreportagain " + uuid + " " + playername +"\"}"

					+ ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click here to report this player again\",\"color\":\"yellow\"}]}}";

			}

			return message + ",{\"text\":\" Cheats : \",\"color\":\"gray\"},{\"text\":\"" + allCheats + "\",\"color\":\"dark_blue\"}]";

		} else { // report not timestamped

			String cheats = "";

			for (String hack : wdr.hacks) {
				cheats = cheats + " " + hack;
			}

			String message = "[\"\",{\"text\":\"Warning : \",\"color\":\"red\"},{\"text\":\"" + playername + "\",\"color\":\"light_purple\""

		    		+ ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":"

					+ "[\"\",{\"text\":\"" + playername + "\",\"color\":\"light_purple\"},{\"text\":\"\\n\"},"

					+ "{\"text\":\"Reported at : \",\"color\":\"green\"},{\"text\":\"" + DateUtil.localformatTimestamp(wdr.timestamp) + "\",\"color\":\"yellow\"},{\"text\":\"\\n\"},"

					+ "{\"text\":\"Reported for :\",\"color\":\"green\"},{\"text\":\"" + cheats + "\",\"color\":\"gold\"},{\"text\":\"\\n\\n\"},"

					+ "{\"text\":\"Click this message to stop receiving warnings for this player\",\"color\":\"yellow\"},{\"text\":\" \"}]}"

					+ ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/unwdr " + uuid + " " + playername + "\"}}"

					+ ",{\"text\":\" joined,\",\"color\":\"gray\"}";

			if((new Date()).getTime() - wdr.timestamp > 2*60*60*1000L) { // montre le bouton pour re-report si l'ancien report est plus vieux que 2heures

				message = message + ",{\"text\":\" Report again\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/sendreportagain " + uuid + " " + playername + "\"}"

					+ ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click here to report this player again\",\"color\":\"yellow\"}]}}";

			}

			message = message + ",{\"text\":\" Cheats : \",\"color\":\"gray\"}";

			for (String hack : wdr.hacks) {				
				if(hack.equalsIgnoreCase("bhop")) {					
					message = message + ",{\"text\":\"" + hack + " " + "\",\"color\":\"red\"}";					
				} else {
					message = message + ",{\"text\":\"" + hack + " " + "\",\"color\":\"gold\"}";					
				}											
			}

			return message + "]";

		}

	}

	/**
	 * Return an array with new String[]{message,allCheats};
	 * 
	 * "message" is a message with the player name and an hover event on top with the timestamped report info
	 * 
	 * allCheats is a list of all the hacks for this player
	 * @param uuid
	 * @param playername
	 * @param wdr
	 * @return
	 */
	public static String[] createPlayerTimestampedMsg(String playername, WDR wdr, String namecolor) { 

		String cheats = "";
		Long timestamphackreport = 0L ;
		String allCheats="";
		String serverID="";
		String timeronreplay="";
		String playernamewhenreported="";
		String oldname="";
		Long oldtimestamp=0L;
		String oldgameID="";
		String message = ",{\"text\":\"" + playername + "\",\"color\":\"" + namecolor + "\""

	    		+ ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":"

				+ "[\"\",{\"text\":\"" + playername + "\",\"color\":\"light_purple\"}";

		int j = 0; // indice of timestamp
		for(int i=0; i < wdr.hacks.size(); i++) { 

			if((wdr.hacks.get(i).charAt(0) == '-' && i!=0) || i==wdr.hacks.size()-1) { // constructmessage

				if(i==wdr.hacks.size()-1) { 
					cheats = cheats + " " + wdr.hacks.get(i);
					allCheats = allCheats + (allCheats.contains(wdr.hacks.get(i)) ? "" : " " + wdr.hacks.get(i));
				}

				if(serverID.equals(oldgameID) && Math.abs(timestamphackreport-oldtimestamp) < 3000000 && playernamewhenreported.equals(oldname) ) { // if it is same server ID and reports

					message = message + ",{\"text\":\"\\n\"},{\"text\":\"Reported at (EST - server time) : \",\"color\":\"green\"},{\"text\":\"" + DateUtil.ESTformatTimestamp(timestamphackreport) + "\",\"color\":\"yellow\"},{\"text\":\"\\n\"},"

				+ "{\"text\":\"Timer on replay (approx.) : \",\"color\":\"green\"},{\"text\":\"" + timeronreplay + "\",\"color\":\"gold\"},{\"text\":\"\\n\"},"

				+ "{\"text\":\"Timestamp for : \",\"color\":\"green\"},{\"text\":\"" + cheats + "\",\"color\":\"gold\"}" + ( (i==wdr.hacks.size()-1) ? "" : ",{\"text\":\"\\n\"}");

				} else {

					message = message + ",{\"text\":\"\\n\"},{\"text\":\"Reported at (EST - server time) : \",\"color\":\"green\"},{\"text\":\"" + DateUtil.ESTformatTimestamp(timestamphackreport) + "\",\"color\":\"yellow\"},{\"text\":\"\\n\"},"

				+ "{\"text\":\"Playername at the moment of the report : \",\"color\":\"green\"},{\"text\":\"" + playernamewhenreported + "\",\"color\":\"red\"},{\"text\":\"\\n\"},"

				+ "{\"text\":\"ServerID : \",\"color\":\"green\"},{\"text\":\"" + serverID + "\",\"color\":\"gold\"}," 

				+ "{\"text\":\" Timer on replay (approx.) : \",\"color\":\"green\"},{\"text\":\"" + timeronreplay + "\",\"color\":\"gold\"},{\"text\":\"\\n\"},"

				+ "{\"text\":\"Timestamp for : \",\"color\":\"green\"},{\"text\":\"" + cheats + "\",\"color\":\"gold\"}" + ( (i==wdr.hacks.size()-1) ? "" : ",{\"text\":\"\\n\"}");

				}

			}

			if(wdr.hacks.get(i).charAt(0) == '-') { // serverID 

				j=i;
				oldgameID=serverID;
				serverID = wdr.hacks.get(i).substring(1);
				cheats="";

			} else if(i==j+1) { // timer on replay

				timeronreplay = wdr.hacks.get(i);

			} else if(i==j+2) { // playernameduringgame

				oldname=playernamewhenreported;
				playernamewhenreported = wdr.hacks.get(i);

			} else if(i==j+3) { // timestampforcheat

				oldtimestamp=timestamphackreport;
				timestamphackreport = Long.parseLong(wdr.hacks.get(i));

			} else if(i>j+3 && i!=wdr.hacks.size()-1) { // cheats

				cheats = cheats + " " + wdr.hacks.get(i);
				allCheats = allCheats + (allCheats.contains(wdr.hacks.get(i)) ? "" : " " + wdr.hacks.get(i));

			} 

		}

		message = message + "]}}"; 

		String[] array = new String[]{message,allCheats};

		return array;
	}

}
