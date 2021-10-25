package fr.alexdoru.nocheatersmod.commands;

import java.util.List;

import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.misc.NameModifier;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandUnWDR extends CommandBase {

	public String getCommandName() {
		return "unwdr";
	}

	public void processCommand(ICommandSender sender, String[] args) {

		if (args.length < 1 || args.length > 3 ) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		}

		if (args.length == 1) { // if you use /unwdr <playername>
			
			(new Thread(() -> {
				
				CachedMojangUUID apireq;
				String playername = args[0];
				try {
					apireq = (new CachedMojangUUID(args[0]));
					playername = apireq.getName();
				} catch (ApiException e) {
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagNoCheaters()
							+ EnumChatFormatting.RED + e.getMessage()));
					return;
				}

				String uuid = apireq.getUuid();

				WDR wdr = WdredPlayers.getWdredMap().remove(uuid);

				if (wdr == null) {
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagNoCheaters() +
							EnumChatFormatting.RED + "Player not found in your report list."));
					return;
				} else {
					NameModifier.transformDisplayName(playername);
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagNoCheaters() + 
							EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.LIGHT_PURPLE + playername + EnumChatFormatting.GREEN + "."));
					return;
				}
				
			})).start();
			
		} else if(args.length == 2) { // when you click the message it does /unwdr <UUID> <playername>

			WDR wdr = WdredPlayers.getWdredMap().remove(args[0]);

			if (wdr == null) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagNoCheaters() +
						EnumChatFormatting.RED + "Player not found in your report list."));
				return;
			} else {
				NameModifier.transformDisplayName(args[1]);
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagNoCheaters() + 
						EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.LIGHT_PURPLE + args[1] + EnumChatFormatting.GREEN + "."  ));
				return;
			}

		}

	}

	public String getCommandUsage(ICommandSender sender) {
		return "/unwdr <playername>";
	}

	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
	}
}
