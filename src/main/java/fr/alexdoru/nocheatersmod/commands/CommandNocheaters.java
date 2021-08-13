package fr.alexdoru.nocheatersmod.commands;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.nocheatersmod.dataobjects.WDR;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandNocheaters extends CommandBase {

	private static HashMap<String, WDR> sortedmap = new HashMap<String, WDR>();

	@Override
	public String getCommandName() {
		return "nocheaters";

	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/nocheaters";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

		if(args.length == 0) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
					EnumChatFormatting.GREEN + "Reported players in your world :"));
			NoCheatersEvents.scanCurrentWorld();
			return;
		}

		if(args.length == 1 && args[0].equalsIgnoreCase("toggle")) {

			if(NoCheatersEvents.togglewarnings) {

				NoCheatersEvents.togglewarnings = false;
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
						EnumChatFormatting.RED + "Warnings disabled"));
				return;

			} else {

				NoCheatersEvents.togglewarnings = true;
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
						EnumChatFormatting.GREEN + "Warnings enabled"));
				NoCheatersEvents.scanCurrentWorld();
				return;

			}

		} else if (args.length == 1 && args[0].equalsIgnoreCase("getgameid")) {

			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
					EnumChatFormatting.GREEN + "Current server : " + EnumChatFormatting.DARK_GRAY + GameInfoGrabber.getGameIDfromscoreboard()));
			return;


		} else if (args.length == 1 && args[0].equalsIgnoreCase("getstoreddata")) {

			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
					EnumChatFormatting.GREEN + "ServerID stored : " + EnumChatFormatting.DARK_GRAY + GameInfoGrabber.getstoredGameID() +"\n"
					+ EnumChatFormatting.GREEN + "Timestamp stored (local) : " + EnumChatFormatting.DARK_GRAY + DateUtil.localformatTimestamp(GameInfoGrabber.getstoredTimestamp()) +"\n" 
					+ EnumChatFormatting.GREEN + "Timestamp stored (EST) : " + EnumChatFormatting.DARK_GRAY + DateUtil.ESTformatTimestamp(GameInfoGrabber.getstoredTimestamp())));
			return;

		} else if (args.length == 1 && args[0].equalsIgnoreCase("isitprepphase")) {

			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
					EnumChatFormatting.GREEN + "Preparation phase : " + EnumChatFormatting.DARK_GRAY + (FKCounterMod.isitPrepPhase() ? "True" : "False" )));
			return;

		} else if (args.length == 1 && args[0].equalsIgnoreCase("getscoreboard")) {

			GameInfoGrabber.debugGetScoreboard();
			return;

		} else if (args.length >= 1 && (args[0].equalsIgnoreCase("reportlist") || args[0].equalsIgnoreCase("stalkreportlist"))) {

			if(args[0].equalsIgnoreCase("stalkreportlist")) {

				if(!HypixelApiKeyUtil.isApiKeySetup()) { //api key not setup
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.apikeyMissingErrorMsg()));
					return;
				}

				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GRAY + "Processing command..."));
			}

			(new Thread(() -> {

				int displaypage = 1;
				int nbreport = 1; // pour compter le nb de report et en afficher que 10 par page
				int nbpage = 1;

				String messagebody = "";

				if(args.length > 1) {
					try {
						displaypage = parseInt(args[1]);
					} catch (NumberInvalidException e) {
						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Not a valid number"));
						e.printStackTrace();
					}
				}							

				if(args.length == 1 || this.sortedmap.isEmpty()) {

					// fill a new hashmap with only timestamped reports
					HashMap<String, WDR> timestampedmap = new HashMap<String, WDR>();
					for (Entry<String, WDR> entry : NoCheatersEvents.wdred.entrySet()) { 					
						if(entry.getValue().hacks.get(0).charAt(0) == '-') {						
							timestampedmap.put(entry.getKey(), entry.getValue()); 						
						}					
					}

					// sorts the map
					this.sortedmap = sortByValue(timestampedmap);
				}

				for (Entry<String, WDR> entry : this.sortedmap.entrySet()) {

					String uuid = entry.getKey();
					WDR wdr = entry.getValue(); 

					if(nbreport == 9) {
						nbreport = 1;
						nbpage++;
					}

					if(nbpage==displaypage) {
						try {
							messagebody = messagebody + createReportLine(uuid, wdr, args[0].equalsIgnoreCase("stalkreportlist")) + ",{\"text\":\"\\n\"}";
						} catch (ApiException e) {
							e.printStackTrace();							
							ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTag() + EnumChatFormatting.RED + e.getMessage()));
						}  
					}

					nbreport++;

				}

				if(!messagebody.equals("")) {

					ChatUtil.addChatMessage(IChatComponent.Serializer.jsonToComponent(
							ChatUtil.makeChatList("Timestamped Reports", messagebody, displaypage, nbpage, (args[0].equalsIgnoreCase("stalkreportlist")? "/nocheaters stalkreportlist" : "/nocheaters reportlist"))));

				} else {

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "No reports to display, " + nbpage + " page"+ (nbpage==1?"":"s") + " available."  ));

				}

				return;

			})).start();

		} else if(args.length == 1 && args[0].equalsIgnoreCase("help")) {

			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(
							EnumChatFormatting.RED + "/nocheaters : scans the world and warns you about previously reported players \n" +
							EnumChatFormatting.RED + "/nocheaters toggle : toggles the warning messages on or off \n" +
							EnumChatFormatting.RED + "/nocheaters reportlist : prints the reports with timestamps"));
			return;

		} else if(args.length == 1 && args[0].equalsIgnoreCase("say")) {
			// TODO say, ajouter dans le tabcompletion
		}

	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		//String[] arguments = {"help","toggle","getgameid","getstoreddata","isitprepphase","getscoreboard","reportlist","stalkreportlist"}; // debug
		String[] arguments = {"help","toggle","reportlist","stalkreportlist"};
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, arguments) : null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	/**
	 * Builds a chat line with the name of the player, the date when you reported him and his online status
	 * 
	 * @param uuid
	 * @param wdr
	 * @param bool - do you want to stalk the reported player
	 * @return
	 * @throws ApiException 
	 */
	public static String createReportLine(String uuid, WDR wdr, boolean bool) throws ApiException {
		// format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc
		String playername = wdr.hacks.get(2);		
		String message = NoCheatersEvents.createPlayerTimestampedMsg(playername, wdr, "red")[0] 

				+ ",{\"text\":\" reported on : \",\"color\":\"dark_gray\"}" 

				+ ",{\"text\":\"" + (new SimpleDateFormat("dd/MM")).format((Long.parseLong(wdr.hacks.get(3)))) + "\",\"color\":\"yellow\"}";

		if(bool) {

			HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
			LoginData logindata = new LoginData(playerdata.getPlayerData());

			if(logindata.getLastLogin() > logindata.getLastLogout()) { // player is online

				message = message + ",{\"text\":\" Online\",\"color\":\"green\"}";

			} else { // print lastlogout

				message = message + ",{\"text\":\" Lastlogout \",\"color\":\"dark_gray\"}" + ",{\"text\":\"" + DateUtil.timeSince(logindata.getLastLogout()) + "\",\"color\":\"yellow\"}";

			}

		}

		return message;

	}

	/**
	 * Returns a sorted hashmap of timestamped reports 
	 * 
	 * @param hm
	 * @return
	 */
	public static HashMap<String, WDR> sortByValue(HashMap<String, WDR> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, WDR> > list = new LinkedList<Map.Entry<String, WDR> >(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, WDR> >() {

			public int compare(Map.Entry<String, WDR> o1, Map.Entry<String, WDR> o2) {
				return (o1.getValue()).compareToInvert(o2.getValue());
			}

		});

		// put data from sorted list to hashmap
		HashMap<String, WDR> temp = new LinkedHashMap<String, WDR>();
		for (Map.Entry<String, WDR> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

}
