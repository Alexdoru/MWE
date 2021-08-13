package fr.alexdoru.megawallsenhancementsmod.commands;

import java.util.Arrays;
import java.util.List;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangNameHistory;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandName extends CommandBase {

	@Override
	public String getCommandName() {
		return "name";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/name <playername>";
	}

	/**
	 * Displays name history for a player
	 */
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

		if (args.length < 1) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		} 

		(new Thread(() -> {

			CachedMojangUUID apiname;
			try {
				apiname = new CachedMojangUUID(args[0]);
			} catch (ApiException e1) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTag() + EnumChatFormatting.RED + e1.getMessage()));
				return;
			}
			String uuid = apiname.getUuid();

			MojangNameHistory apinamehistory;
			try {
				apinamehistory = new MojangNameHistory(uuid);
			} catch (ApiException e1) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTag() + EnumChatFormatting.RED + e1.getMessage()));
				return;
			}

			int displaypage = 1;
			int nbnames = 1; 
			int nbpage = 1;

			String messagebody = "";

			if(args.length > 1) {
				try {
					displaypage = parseInt(args[1]);
				} catch (NumberInvalidException e) {
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + args[1] + " isn't a valid number."));
					e.printStackTrace();
					return;
				}
			}

			int n = apinamehistory.getNames().size();

			for(int i = n-1; i >= 0; i-- ) {	

				if(nbnames == 9) {
					nbnames = 1;
					nbpage++;
				}

				if(nbpage==displaypage) {

					if(i == 0) { // original name

						messagebody = messagebody + ",{\"text\":\"" + apinamehistory.getNames().get(i) + "  \",\"color\":\"gold\"}"
								+ ",{\"text\":\"Original name\",\"color\":\"dark_gray\"}"
								+ ",{\"text\":\"\\n\"}";

					} else if (i == n-1) {

						messagebody = messagebody + ",{\"text\":\"" + apinamehistory.getNames().get(i) + "  \",\"color\":\"gold\"}"
								+ ",{\"text\":\"since " + DateUtil.localformatTimestampday(apinamehistory.getTimestamps().get(i)) + "\",\"color\":\"dark_gray\"}"
								+ ",{\"text\":\"\\n\"}";

					} else {

						messagebody = messagebody + ",{\"text\":\"" + apinamehistory.getNames().get(i) + "  \",\"color\":\"gold\"}"
								+ ",{\"text\":\"" + DateUtil.localformatTimestampday(apinamehistory.getTimestamps().get(i)) + "\",\"color\":\"dark_gray\"}"
								+ ",{\"text\":\"\\n\"}";

					}

				}

				nbnames++;
			}

			if(!messagebody.equals("")) {

				ChatUtil.addChatMessage(IChatComponent.Serializer.jsonToComponent(ChatUtil.makeChatList("Name History", messagebody, displaypage, nbpage, "/name " + args[0])));

			} else {

				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "No names to display, " + nbpage + " page" + (nbpage==1?"":"s") + " available."  ));

			}

		})).start();

	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.<String>asList(new String[] {"names"});
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return (FKCounterMod.isitPrepPhase() ? null : getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()));
	}

}
