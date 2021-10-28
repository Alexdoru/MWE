package fr.alexdoru.megawallsenhancementsmod.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsStats;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.misc.NameModifier;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;

public class CommandScanGame extends CommandBase {

	private static final HashMap<String, IChatComponent> scanmap = new HashMap<>();
	private static String scanGameId;
	public static final IChatComponent iprefix = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "\u26a0 ");
	public static final String prefix = iprefix.getFormattedText();
	/*
	 * fills the hashmap with this instead if null when there is no match
	 */
	public static final IChatComponent nomatch = new ChatComponentText("none");

	@Override
	public String getCommandName() {
		return "scangame";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/scangame";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

		if(HypixelApiKeyUtil.apiKeyIsNotSetup()) {
			addChatMessage(new ChatComponentText(ChatUtil.apikeyMissingErrorMsg()));
			return;
		}

		String currentGameId = GameInfoGrabber.getGameIDfromscoreboard();
		Collection<NetworkPlayerInfo> playerCollection = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();

		int nbcores = Runtime.getRuntime().availableProcessors();
		ExecutorService service = Executors.newFixedThreadPool(nbcores);

		int i = 0;

		if(!currentGameId.equals("?") && currentGameId.equals(scanGameId)) {

			for (NetworkPlayerInfo networkPlayerInfo : playerCollection) {

				String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
				IChatComponent imsg = scanmap.get(uuid);

				if(imsg == null) {
					i++;
					service.submit(new ScanPlayerTask(networkPlayerInfo));
				} else if (!imsg.equals(nomatch)) {
					addChatMessage(imsg);
				}

			}
			addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Scanning " + i + " more players..."));

		} else {

			scanGameId = GameInfoGrabber.getGameIDfromscoreboard();

			for (NetworkPlayerInfo networkPlayerInfo : playerCollection) {
				i++;
				service.submit(new ScanPlayerTask(networkPlayerInfo));
			}

			addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Scanning " + i + " players..."));

		}

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	public static void clearScanGameData() {
		scanGameId = null;
		scanmap.clear();
	}

	public static void onGameStart() {
		String currentGameId = GameInfoGrabber.getGameIDfromscoreboard();
		if(!currentGameId.equals("?") && scanGameId != null && !scanGameId.equals(currentGameId)) {
			clearScanGameData();
		}

	}

	public static HashMap<String, IChatComponent> getScanmap(){
		return scanmap;
	}

}

class ScanPlayerTask implements Callable<String> {

	final NetworkPlayerInfo networkPlayerInfo;

	public ScanPlayerTask(NetworkPlayerInfo networkPlayerInfoIn) {
		this.networkPlayerInfo = networkPlayerInfoIn;
	}

	@Override
	public String call() {

		try {

			String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
			String playername = networkPlayerInfo.getGameProfile().getName();
			CommandScanGame.getScanmap().put(uuid, new ChatComponentText("none"));

			HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
			MegaWallsStats megawallsstats = new MegaWallsStats(playerdata.getPlayerData());

			if( (megawallsstats.getGames_played() <= 25 && megawallsstats.getFkdr() > 3f) || 
					(megawallsstats.getGames_played() <= 250 && megawallsstats.getFkdr() > 5f)	|| // change it to 7
					(megawallsstats.getGames_played() <= 500 && megawallsstats.getFkdr() > 8f)	|| // change it to 10
					(megawallsstats.getFkdr() > 10f)) {
				// RETIRER String formattedname = getFormattedName(networkPlayerInfo); //get the name from the logindata formatted name
				IChatComponent msg = new ChatComponentText(ChatUtil.getTagMW())
						.appendSibling(ChatUtil.makeReportButtons(playername, "bhop", ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.SUGGEST_COMMAND))						
						.appendSibling(new ChatComponentText(getFormattedName(networkPlayerInfo)
								+ EnumChatFormatting.GRAY + " played : " + EnumChatFormatting.GOLD + megawallsstats.getGames_played() 
								+ EnumChatFormatting.GRAY + " games , fkd : " + EnumChatFormatting.GOLD + String.format("%.1f", megawallsstats.getFkdr()) 
								+ EnumChatFormatting.GRAY + " FK/game : " + EnumChatFormatting.GOLD + String.format("%.1f", megawallsstats.getFkpergame())
								+ EnumChatFormatting.GRAY + " W/L : " + EnumChatFormatting.GOLD + String.format("%.1f", megawallsstats.getWlr())));				

				addChatMessage(msg);
				CommandScanGame.getScanmap().put(uuid, msg);
				//if(NoCheatersMod.areIconsToggled()) {
					this.networkPlayerInfo.setDisplayName(NameModifier.getTransformedDisplayName(this.networkPlayerInfo));
				//}
				return null;
			}

			if(megawallsstats.getGames_played() == 0) { // TODO vérifier que la classe équipée est bien la class qui flag

				JsonObject classesdata = megawallsstats.getClassesdata();
				IChatComponent msg = new ChatComponentText("");

				for(Map.Entry<String, JsonElement> entry : classesdata.entrySet()) {
					if(entry.getValue() != null && entry.getValue().isJsonObject()) {
						JsonObject entryclassobj = entry.getValue().getAsJsonObject();

						int skill_level_a = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_a"),1); //skill
						int skill_level_b = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_b"),1); //passive1
						int skill_level_c = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_c"),1); //passive2
						int skill_level_d = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_d"),1); //kit								
						int skill_level_g = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_g"),1); //gathering

						if(skill_level_a >= 4 || skill_level_d >= 4) {
							// RETIRER String formattedname = getFormattedName(networkPlayerInfo); //get the name from the logindata formatted name
							msg.appendSibling(new ChatComponentText(ChatUtil.getTagMW())
									.appendSibling(ChatUtil.makeReportButtons(playername, "bhop", ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.SUGGEST_COMMAND))
									.appendSibling(new ChatComponentText(getFormattedName(networkPlayerInfo)
											+	EnumChatFormatting.GRAY + " never played and has : " + EnumChatFormatting.GOLD + entry.getKey() + " "							
											+ (skill_level_d == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_d) + " "
											+ (skill_level_a == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_a) + " "
											+ (skill_level_b == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_b) + " "
											+ (skill_level_c == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_c) + " "
											+ (skill_level_g == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_g) + "\n")));

						}							

					}

				}

				if(!msg.equals(new ChatComponentText(""))) {
					addChatMessage(msg);
					CommandScanGame.getScanmap().put(uuid, msg);
					//if(NoCheatersMod.areIconsToggled()) {
						this.networkPlayerInfo.setDisplayName(NameModifier.getTransformedDisplayName(this.networkPlayerInfo));
					//}
				}
				return null;
			}

		} catch (ApiException e) {

			if(e.getMessage().equals("This player never joined Hypixel")) {
				addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.DARK_GRAY + "This player never joined Hypixel"));
			} else {
				addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + e.getMessage()));
			}

		}

		return null;

	}

	private String getFormattedName(NetworkPlayerInfo networkPlayerInfoIn) {
		return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
	}

}

