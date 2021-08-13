package fr.alexdoru.megawallsenhancementsmod.commands;

import java.util.List;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerStatus;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandStalk extends CommandBase {

	@Override
	public String getCommandName() {
		return "stalk";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/stalk <playernames>";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

		if (args.length < 1) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		} 

		if(!HypixelApiKeyUtil.isApiKeySetup()) { // api key not setup
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.apikeyMissingErrorMsg()));
			return;
		}

		(new Thread(()->{

			try {	
				MojangPlayernameToUUID apiname = new MojangPlayernameToUUID(args[0]);
				String uuid = apiname.getUuid();

				// player found on mojang's api 

				String playername = apiname.getName();
				HypixelPlayerStatus apistatus = new HypixelPlayerStatus(uuid, HypixelApiKeyUtil.getApiKey());

				if(apistatus.getOnline()) { // player is online

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText( ChatUtil.getTag()
							+ EnumChatFormatting.YELLOW + playername + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + apistatus.getGamemode() + " " + apistatus.getMode() + 
							( apistatus.getMap() == null ? "" : ( EnumChatFormatting.GREEN + " on " + EnumChatFormatting.YELLOW + apistatus.getMap()) )  ));
					return;

				} else {                   // player is offline, stalk the playerdata info

					HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
					LoginData logindata = new LoginData(playerdata.getPlayerData());
					String formattedname = logindata.getFormattedName();

					if(playerdata.getPlayerData() == null) { // Failed to contact hypixel's API

						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText( ChatUtil.getTag()
								+ EnumChatFormatting.RED + "Failed to retrieve information from Hypixel's api for : " + playername + EnumChatFormatting.RED + "."));
						return;

					} else if(logindata.hasNeverJoinedHypixel()) { // player never joined hypixel

						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText( ChatUtil.getTag()
								+ EnumChatFormatting.YELLOW + playername + EnumChatFormatting.RED + " has never joined Hypixel." ));
						return;

					} else if(logindata.isStaffonHypixel()) { // player is a a staff member

						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText( ChatUtil.getTag()
								+ formattedname + EnumChatFormatting.RED + " is completely hiding their online status from the API."
								+ EnumChatFormatting.DARK_GRAY + " It happens for staff members."));
						return;

					} else if(logindata.isOnline()) { // player is online but hiding their session

						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText( ChatUtil.getTag()
								+ formattedname + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType() 
								+ EnumChatFormatting.GREEN + "." +EnumChatFormatting.DARK_GRAY + " (This player hides their session.)"));
						return;

					} else { // offline

						String offlinesince = DateUtil.timeSince(logindata.getLastLogout());
						ChatUtil.addChatMessage((IChatComponent)new ChatComponentText( ChatUtil.getTag()
								+ formattedname + EnumChatFormatting.RED + " has been offline for " + EnumChatFormatting.YELLOW + offlinesince 
								+ EnumChatFormatting.RED + "." + (logindata.getMostRecentGameType().equals("?") ? "" : EnumChatFormatting.RED + " Last seen in : " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType())));
						return;
					}

				}
			} catch (ApiException e) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTag() + EnumChatFormatting.RED + e.getMessage()));
			}

		})).start();

	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return (FKCounterMod.isitPrepPhase() ? null : getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()));
	}

}
