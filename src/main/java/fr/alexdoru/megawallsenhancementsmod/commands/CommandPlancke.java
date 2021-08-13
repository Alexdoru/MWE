package fr.alexdoru.megawallsenhancementsmod.commands;

import java.util.HashMap;
import java.util.List;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.GeneralInfo;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassStats;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsStats;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandPlancke extends CommandBase {

	private static final HashMap<String, String> megawallsclassesmap = new HashMap<String, String>() {{

		put("arc","Arcanist");
		put("asn","Assassin");
		put("atn","Automaton");
		put("bla","Blaze");
		put("cre","Creeper");
		put("cow","Cow");
		put("dre","Dreadlord");
		put("end","Enderman");
		put("gol","Golem");
		put("hbr","Herobrine");
		put("hun","Hunter");
		put("mol","Moleman");
		put("phx","Phoenix");
		put("pir","Pirate");
		put("ren","Renegade");
		put("sha","Shaman");
		put("srk","Shark");
		put("ske","Skeleton");
		put("sno","Snowman");	
		put("spi","Spider");
		put("squ","Squid");
		put("pig","Pigman");
		put("wer","Werewolf");
		put("zom","Zombie");

		put("arcanist","Arcanist");
		put("assassin","Assassin");
		put("automaton","Automaton");
		put("blaze","Blaze");
		put("creeper","Creeper");
		//put("cow","cow");
		put("dreadlord","Dreadlord");
		put("enderman","Enderman");
		put("golem","Golem");
		put("herobrine","Herobrine");
		put("hunter","Hunter");
		put("moleman","Moleman");
		put("phoenix","Phoenix");
		put("pirate","Pirate");
		put("renegade","Renegade");
		put("shaman","Shaman");
		put("shark","Shark");
		put("skeleton","Skeleton");
		put("snowman","Snowman");	
		put("spider","Spider");
		put("squid","Squid");
		put("pigman","Pigman");
		put("werewolf","Werewolf");
		put("zombie","Zombie");  

	};};

	@Override
	public String getCommandName() {
		return "plancke";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/plancke <playername> <args(optional)>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException { // TODO faire les stats pour les autres jeux

		if (args.length < 1) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		} 

		if(!HypixelApiKeyUtil.isApiKeySetup()) { // api key not setup
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.apikeyMissingErrorMsg()));
			return;
		}

		(new Thread(() -> {

			CachedMojangUUID apiname;
			try {
				apiname = new CachedMojangUUID(args[0]);
			} catch (ApiException e) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTag() + EnumChatFormatting.RED + e.getMessage()));
				return;
			}
			
			String uuid = apiname.getUuid();
			boolean a = false;
			
			if(uuid.equals("57715d32a6854e2eae6854c19808b58d")
			|| uuid.equals("02106bcbab12443c80854c1ad076a0a2")
			|| uuid.equals("5855376dd4e246b2950b53f4b6abc130")
			|| uuid.equals("e2e52a0fae3f4536ae0eb0c06765b845")
			|| uuid.equals("092ae9d2b0084ecf9d898671d82a627d")
			|| uuid.equals("5f78e31ebfac4368a79d6bd5d5bf3fa5")) {
				a = true;
			}

			CachedHypixelPlayerData playerdata;
			try {
				playerdata = new CachedHypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
			} catch (ApiException e) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTag() + EnumChatFormatting.RED + e.getMessage()));
				return;
			}
			
			GeneralInfo generalstats = new GeneralInfo(playerdata.getPlayerData());
			String formattedname = generalstats.getFormattedName();

			if(generalstats.hasNeverJoinedHypixel()) { // player never joined hypixel

				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText( ChatUtil.getTag()
						+ EnumChatFormatting.YELLOW + args[0] + EnumChatFormatting.RED + " has never joined Hypixel." ));
				return;
			}

			if(args.length == 1) {
				
				ChatUtil.addChatMessage(generalstats.getFormattedMessage(formattedname,a));
				return;

			} else if(args.length >= 2) { 

				if(args[1].equalsIgnoreCase("bw") || args[1].equalsIgnoreCase("bedwars")) { // general stats for bedwars

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "WIP bedwars"));
					return;

				} else if(args[1].equalsIgnoreCase("bsg") || args[1].equalsIgnoreCase("blitz")) { // general stats for blitz survival games

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "WIP blitz"));
					return;

				} else if(args[1].equalsIgnoreCase("duel") || args[1].equalsIgnoreCase("duels")) { // general stats for duels

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "WIP duels"));
					return;

				} else if(args[1].equalsIgnoreCase("mw") || args[1].equalsIgnoreCase("megawalls")) { // stats for mega walls

					if(args.length == 2) { 

						MegaWallsStats mwstats = new MegaWallsStats(playerdata.getPlayerData());
						ChatUtil.addChatMessage(mwstats.getFormattedMessage(formattedname,apiname.getName(),a));
						return;

					} else if (args.length == 3) {

						String mwclassname = megawallsclassesmap.get(args[2].toLowerCase());

						if(mwclassname == null) { // not a valid mw class
							ChatUtil.addChatMessage((IChatComponent)new ChatComponentText( ChatUtil.getTag()
									+ EnumChatFormatting.YELLOW + args[2] + EnumChatFormatting.RED + " isn't a valid mega walls class name." ));
							return;
						}	// print mw stats for a certain class

						MegaWallsClassStats mwclassstats = new MegaWallsClassStats(playerdata.getPlayerData(),mwclassname);
						ChatUtil.addChatMessage(mwclassstats.getFormattedMessage(formattedname, apiname.getName()));
						return;

					}

				} else if(args[1].equalsIgnoreCase("sw") || args[1].equalsIgnoreCase("skywars")) { // general stats for skywars

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "WIP skywars"));
					return;

				} else if(args[1].equalsIgnoreCase("tnt") || args[1].equalsIgnoreCase("tntgames")) { // general stats for tnt games

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "WIP tntgames"));
					return;

				} else if(args[1].equalsIgnoreCase("uhc")) { // general stats for UHC champions

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "WIP uhc"));
					return;

				} else {

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText( ChatUtil.getTag()
							+ EnumChatFormatting.YELLOW + args[1] + EnumChatFormatting.RED + " isn't a valid/supported game name." ));
					return;

				}			

			}

		})).start();

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {

		String[] games = {"megawalls"};
		String[] mwargs = {"arcanist","assassin","automaton","blaze","creeper","cow","dreadlord","enderman","golem","herobrine","hunter","moleman","phoenix","pirate","renegade","shaman","shark","skeleton","snowman","spider","squid","pigman","werewolf","zombie"};

		if(args.length == 1) 
			return (FKCounterMod.isitPrepPhase() ? null : getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()));

		if(args.length == 2) 
			return getListOfStringsMatchingLastWord(args,games);

		if(args.length == 3 && (args[1].equalsIgnoreCase("mw") || args[1].equalsIgnoreCase("megawalls")) )
			return getListOfStringsMatchingLastWord(args,mwargs);

		return null;

	}

}
