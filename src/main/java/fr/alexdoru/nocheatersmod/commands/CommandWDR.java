package fr.alexdoru.nocheatersmod.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import fr.alexdoru.nocheatersmod.dataobjects.TimeMark;
import fr.alexdoru.nocheatersmod.dataobjects.WDR;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandWDR extends CommandBase {

	private static int nbTimeMarks = 0;
	private static HashMap<String,TimeMark> TimeMarksMap = new HashMap<String,TimeMark>();

	private static final char timestampreportkey = '-';
	private static final char timemarkedreportkey = '#';

	@Override
	public String getCommandName() {
		return "watchdogreport";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		
		// TODO idée : trouver pendant combien de temps un nick est indisponible quand on le prend
		// enregistrer l'uuid des nicks et les enregistrer avec un tag special pendant X temps et les supprimer ensuite au démarrage du jeu ou dans le nocheaters event

		if (args.length < 2) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		}

		(new Thread(() -> {
			boolean isaTimestampedReport = false;
			boolean usesTimeMark = false;
			String message = "/wdr " + args[0];
			ArrayList<String> arraycheats = new ArrayList<String>();  // for WDR object
			long longtimetosubtract = 0;                                //en secondes
			String playername = args[0];
			String serverID = "?";
			long timestamp = 0;			
			String timerOnReplay = "?";

			for (int i = 1; i < args.length; i++) { // reads each arg one by one

				if(args[i].charAt(0) == timestampreportkey) { // handling timestamped reports

					if(isaTimestampedReport) {
						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] "
								+ EnumChatFormatting.RED + "You can't have more than one timestamp in the arguments."));
						return;
					}

					if(usesTimeMark) {
						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] "
								+ EnumChatFormatting.RED + "You can't use both special arguments in the same reports!"));
						return;
					}

					isaTimestampedReport = true;
					String rawtimestamp = args[i].substring(1);

					if(args[i] == "-") { // computing the -time argument
						longtimetosubtract=0;
					} else {
						Matcher Matcher0 = Pattern.compile("(\\d+)").matcher(rawtimestamp);
						Matcher Matcher1 = Pattern.compile("(\\d+)s").matcher(rawtimestamp);
						Matcher Matcher2 = Pattern.compile("(\\d+)m").matcher(rawtimestamp);
						Matcher Matcher3 = Pattern.compile("(\\d+)m(\\d+)s").matcher(rawtimestamp);

						if(Matcher0.matches()) {
							longtimetosubtract=Long.parseLong(Matcher0.group(1));
						} else if (Matcher1.matches()) {
							longtimetosubtract=Long.parseLong(Matcher1.group(1));
						} else if (Matcher2.matches()) {
							longtimetosubtract=60*Long.parseLong(Matcher2.group(1));
						} else if (Matcher3.matches()) {
							longtimetosubtract=60*Long.parseLong(Matcher3.group(1)) + Long.parseLong(Matcher3.group(2));
						} 
					}

					timestamp = (new Date()).getTime()-longtimetosubtract*1000; // Milliseconds
					serverID = GameInfoGrabber.getGameIDfromscoreboard();
					timerOnReplay = GameInfoGrabber.getTimeSinceGameStart(timestamp, serverID, (int)longtimetosubtract);

				} else if (args[i].charAt(0) == timemarkedreportkey) { // process the command if you use a stored timestamp 

					if(usesTimeMark) {
						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] "
								+ EnumChatFormatting.RED + "You can't use more than one #timestamp in the arguments."));
						return;
					}

					if(isaTimestampedReport) {
						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] "
								+ EnumChatFormatting.RED + "You can't use both special arguments in the same reports!"));
						return;
					}

					usesTimeMark = true;

					String key = args[i].substring(1);
					TimeMark timemark = TimeMarksMap.get(key);

					if(timemark == null) {

						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] "
								+ EnumChatFormatting.YELLOW + key + EnumChatFormatting.RED + " isn't a valid timestamp #"));
						return;	
					} else {

						timestamp = timemark.timestamp;
						serverID = timemark.serverID;
						timerOnReplay = timemark.timerOnReplay;

					}

				} else if(args[i].equalsIgnoreCase("bhop")) {

					arraycheats.add(args[i]);
					message = message + " bhop aura reach velocity speed antiknockback";
					
				} else if(args[i].equalsIgnoreCase("autoblock")) {

					arraycheats.add(args[i]);
					message = message + " killaura";
					
				} else if(args[i].equalsIgnoreCase("noslowdown") || args[i].equalsIgnoreCase("keepsprint")) {

					arraycheats.add(args[i]);
					message = message + " velocity";
					
				} else {

					arraycheats.add(args[i]);
					message = message + " " + args[i]; //reconstructs the message to send it to the server

				}

			}

			if((isaTimestampedReport || usesTimeMark) && args.length == 2) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
				return;
			}

			if(!message.equals("/wdr " + args[0])) {

				if((isaTimestampedReport || usesTimeMark)) {					
					message = message + " " + (timerOnReplay.equals("?") ? "" : timerOnReplay );
				}			
				
				(Minecraft.getMinecraft()).thePlayer.sendChatMessage(message); //sends command to server
			}

			CachedMojangUUID apireq;
			CachedHypixelPlayerData playerdata;
			String uuid = null;
			try {
				apireq = new CachedMojangUUID(args[0]);
				uuid = apireq.getUuid();
				playername = apireq.getName();

				if(uuid != null) {
					playerdata = new CachedHypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
				}

			} catch (ApiException e) {

				if(e.getMessage().equals("This player never joined Hypixel")){
					uuid = null;
				}

			}

			if (uuid == null) {  // couldn't find playername

				if(isaTimestampedReport || usesTimeMark) { // nicked player with timestamp

					String stringCheats ="";

					for(String cheat : arraycheats) {
						stringCheats+= cheat + " ";
					}

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " 
							+ EnumChatFormatting.GREEN + "You reported " + EnumChatFormatting.LIGHT_PURPLE + playername + EnumChatFormatting.GREEN +" with a " + EnumChatFormatting.YELLOW + "timestamp" 
							+ EnumChatFormatting.GREEN + ". This player might be "+ EnumChatFormatting.RED + "nicked.\n\n"
							+ EnumChatFormatting.GREEN + "Date (EST - server time) : " + EnumChatFormatting.GOLD + DateUtil.ESTformatTimestamp(timestamp) +" \n"
							+ EnumChatFormatting.GREEN + "ServerID : " + EnumChatFormatting.GOLD + serverID + " " + EnumChatFormatting.GREEN + " Timer on replay (approx.) : " + EnumChatFormatting.GOLD + timerOnReplay +"\n"
							+ EnumChatFormatting.GREEN + "Reported for : " + EnumChatFormatting.GOLD + stringCheats + "\n" 
							));
					return;

				} else {

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] "
							+ ChatUtil.invalidplayernameMsg(playername)));
					return;
				}

			} else {  // player found

				if(isaTimestampedReport || usesTimeMark) { // format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc

					ArrayList<String> argsinWDR = new ArrayList<String>();
					argsinWDR.add("-" + serverID);
					argsinWDR.add(timerOnReplay);
					argsinWDR.add(playername);
					argsinWDR.add(Long.toString(timestamp));

					for(String cheat : arraycheats) {
						argsinWDR.add(cheat);
					}

					WDR wdr = NoCheatersEvents.wdred.get(uuid); // look if he was already reported and add the previous cheats without duplicates

					if (wdr != null) { // the player was already reported before

						//if(wdr.hacks.get(0).charAt(0) == '-') { // previous report was also a timestamped report

							for (int i = 0; i < wdr.hacks.size(); i++) { // adds the previous arguments after the current report
								argsinWDR.add(wdr.hacks.get(i));
							}
						//} 
					}

					WDR newreport = new WDR(timestamp, argsinWDR);
					NoCheatersEvents.wdred.put(uuid, newreport);
					// bug transformName(playername, argsinWDR.contains("bhop"));

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
							EnumChatFormatting.GREEN +  "You reported ")
							.appendSibling(IChatComponent.Serializer.jsonToComponent("[\"\"" + NoCheatersEvents.createPlayerTimestampedMsg(playername, newreport, "light_purple")[0] + "]"))
							.appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " with a " + EnumChatFormatting.YELLOW +
									"timestamp" + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game.")));
					return;

				} else {  // isn't a timestamped report

					ArrayList<String> argsinWDR = new ArrayList<String>();
					WDR wdr = NoCheatersEvents.wdred.get(uuid); // look if he was already reported and add the previous cheats without duplicates

					if (wdr != null) { // the player was already reported before

						for (int i = 0; i < wdr.hacks.size(); i++) {
							argsinWDR.add(wdr.hacks.get(i));
						}

						for (int i = 0; i < arraycheats.size(); i++) { // adds the report at the end of the previous informations and avoids duplicates
							boolean doublon = false;
							for(String arg : argsinWDR) {
								if((arraycheats.get(i)).equals(arg)) {
									doublon = true;
								}
							}
							if(doublon==false) {
								argsinWDR.add(arraycheats.get(i));
							}
						}

					} else {
						for(String cheat : arraycheats) {
							argsinWDR.add(cheat);
						}
					}

					NoCheatersEvents.wdred.put(uuid, new WDR((new Date()).getTime(), argsinWDR));
					// bug transformName(playername, argsinWDR.contains("bhop"));
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
							EnumChatFormatting.GREEN +  "You reported " + EnumChatFormatting.LIGHT_PURPLE + playername + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game."));
					return;

				}

			}

		})).start();
	}

	@Override
	public List<String> getCommandAliases() {
		ArrayList<String> res = new ArrayList<String>();
		res.add("wdr");
		res.add("Wdr");
		res.add("WDR");
		return res;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/wdr <player> <cheats> <timestamp(optional)>";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return (args.length > 1 ? getListOfStringsMatchingLastWord(args, CommandReport.cheatsArray) : null);
	}

	public static void addTimeMark() {

		nbTimeMarks++;

		String key = String.valueOf(nbTimeMarks);
		long timestamp = (new Date()).getTime();
		String serverID = GameInfoGrabber.getGameIDfromscoreboard();
		String timerOnReplay = GameInfoGrabber.getTimeSinceGameStart(timestamp, serverID, 0);
		TimeMarksMap.put(key, new TimeMark(timestamp , serverID, timerOnReplay));

		ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] "

					+ EnumChatFormatting.GREEN + "Added timestamp : " + EnumChatFormatting.GOLD + "#" + key + EnumChatFormatting.GREEN + ".")

				.setChatStyle(new ChatStyle()
						
						.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 

						new ChatComponentText(EnumChatFormatting.GREEN + "Key : " + EnumChatFormatting.GOLD + "#" + key + "\n" +
								EnumChatFormatting.GREEN + "Timestamp : " + EnumChatFormatting.GOLD + DateUtil.ESTformatTimestamp(timestamp) + "\n" +
								EnumChatFormatting.GREEN + "ServerID : " + EnumChatFormatting.GOLD + serverID + "\n" +
								EnumChatFormatting.GREEN + "Timer on replay (approx.) : " + EnumChatFormatting.GOLD + timerOnReplay + "\n" + 
								EnumChatFormatting.YELLOW + "Click to fill a report with this timestmap")))
						.setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/wdr  " + "#" + key))));

	}
	
	private void transformName(String playername, boolean isBhoping) {
		NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(playername);
		if(networkPlayerInfo.getDisplayName() == null) {return;}
		String formattedname = networkPlayerInfo.getDisplayName().getFormattedText();
		if(formattedname.contains("\u26a0")) {return;}
		if(isBhoping) {
			networkPlayerInfo.setDisplayName(NoCheatersEvents.iprefix_bhop.appendSibling(networkPlayerInfo.getDisplayName()));
		} else {
			networkPlayerInfo.setDisplayName(NoCheatersEvents.iprefix.appendSibling(networkPlayerInfo.getDisplayName()));
		}
	}

}