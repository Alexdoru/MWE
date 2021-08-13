package fr.alexdoru.nocheatersmod.commands;

import java.util.List;
import java.util.regex.Pattern;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import fr.alexdoru.nocheatersmod.dataobjects.WDR;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandUnWDR extends CommandBase {

	private static Pattern pattern = Pattern.compile("[0-9a-f]{32}");

	public String getCommandName() {
		return "unwdr";
	}

	public void processCommand(ICommandSender sender, String[] args) {

		if (args.length < 1 || args.length > 3 ) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		}
		(new Thread(() -> {
			if (args.length == 1) { // if you use /unwdr <playername>

				CachedMojangUUID apireq;
				String playername = args[0];
				try {
					apireq = (new CachedMojangUUID(args[0]));
					playername = apireq.getName();
				} catch (ApiException e) {
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] "
							+ EnumChatFormatting.RED + e.getMessage()));
					return;
				}

				String uuid = apireq.getUuid();

				WDR wdr = NoCheatersEvents.wdred.remove(uuid);

				if (wdr == null) {
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
							EnumChatFormatting.RED + "Player not found in your report list."));
					return;
				} else {

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " + 
							EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.LIGHT_PURPLE + playername + EnumChatFormatting.GREEN + "."));
					return;
				}

			} else if(args.length == 2) { // when you click the message it does /unwdr <UUID> <playername>

				WDR wdr = NoCheatersEvents.wdred.remove(args[0]);

				if (wdr == null) {
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " +
							EnumChatFormatting.RED + "Player not found."));
					return;
				} else {

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "] " + 
							EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.LIGHT_PURPLE + args[1] + EnumChatFormatting.GREEN + "."  ));
					return;
				}

			}
		})).start();
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/unwdr <playername>";
	}

	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return (FKCounterMod.isitPrepPhase() ? null : getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()));
	}
}
