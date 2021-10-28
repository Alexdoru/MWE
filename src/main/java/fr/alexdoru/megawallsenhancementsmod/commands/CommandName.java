package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangNameHistory;
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

import java.util.Collections;
import java.util.List;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

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
			addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		} 

		(new Thread(() -> {

			CachedMojangUUID apiname;
			try {
				apiname = new CachedMojangUUID(args[0]);
			} catch (ApiException e1) {
				addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e1.getMessage()));
				return;
			}
			String uuid = apiname.getUuid();

			MojangNameHistory apinamehistory;
			try {
				apinamehistory = new MojangNameHistory(uuid);
			} catch (ApiException e1) {
				addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e1.getMessage()));
				return;
			}

			int displaypage = 1;
			int nbnames = 1; 
			int nbpage = 1;

			StringBuilder messagebody = new StringBuilder();

			if(args.length > 1) {
				try {
					displaypage = parseInt(args[1]);
				} catch (NumberInvalidException e) {
					addChatMessage(new ChatComponentText(EnumChatFormatting.RED + args[1] + " isn't a valid number."));
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

						messagebody.append(",{\"text\":\"").append(apinamehistory.getNames().get(i)).append("  \",\"color\":\"gold\"}").append(",{\"text\":\"Original name\",\"color\":\"dark_gray\"}").append(",{\"text\":\"\\n\"}");

					} else if (i == n-1) {

						messagebody.append(",{\"text\":\"").append(apinamehistory.getNames().get(i)).append("  \",\"color\":\"gold\"}").append(",{\"text\":\"since ").append(DateUtil.localformatTimestampday(apinamehistory.getTimestamps().get(i))).append("\",\"color\":\"dark_gray\"}").append(",{\"text\":\"\\n\"}");

					} else {

						messagebody.append(",{\"text\":\"").append(apinamehistory.getNames().get(i)).append("  \",\"color\":\"gold\"}").append(",{\"text\":\"").append(DateUtil.localformatTimestampday(apinamehistory.getTimestamps().get(i))).append("\",\"color\":\"dark_gray\"}").append(",{\"text\":\"\\n\"}");

					}

				}

				nbnames++;
			}

			if(!messagebody.toString().equals("")) {

				addChatMessage(IChatComponent.Serializer.jsonToComponent(makeChatList("Name History", messagebody.toString(), displaypage, nbpage, "/name " + args[0])));

			} else {

				addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No names to display, " + nbpage + " page" + (nbpage==1?"":"s") + " available."  ));

			}

		})).start();

	}

	@Override
	public List<String> getCommandAliases()
	{
		return Collections.singletonList("names");
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		//return (GameInfoGrabber.isitPrepPhase() ? null : getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()));
		return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
	}

}
