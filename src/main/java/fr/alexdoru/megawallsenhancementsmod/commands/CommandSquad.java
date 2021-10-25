package fr.alexdoru.megawallsenhancementsmod.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import fr.alexdoru.nocheatersmod.NoCheatersMod;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandSquad extends CommandBase {

	@Override
	public String getCommandName() {
		return "squad";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/squad <add|disband|list|remove>";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

		if(args.length < 1) {
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
			return;
		}

		if(args[0].equalsIgnoreCase("add")) {

			if(args.length < 2) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : /squad <add> <playername>"));
				return;
			}

			if(args.length == 4 && args[2].equalsIgnoreCase("as")) { // TODO refresh le name en tab
				
				SquadEvent.addPlayer(args[1], args[3]);
				EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(args[1]);

				if(NoCheatersMod.areIconsToggled() && player !=null) {
					player.refreshDisplayName();
				}
				
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagMW() +
						EnumChatFormatting.GREEN + "Added " + EnumChatFormatting.GOLD + args[1] + EnumChatFormatting.GREEN + " as " +
						EnumChatFormatting.GOLD + args[3] + EnumChatFormatting.GREEN + " to the squad."));
				return;
			}
			
			for(int i = 1; i < args.length; i++) {

				SquadEvent.addPlayer(args[i]);
				EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(args[i]);

				if(NoCheatersMod.areIconsToggled() && player !=null) {
					player.refreshDisplayName();
				}

				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagMW() +
						EnumChatFormatting.GREEN + "Added " + EnumChatFormatting.GOLD + args[i] + EnumChatFormatting.GREEN + " to the squad." ));
			}
			return;

			/*
			 * this doesn't update the nametag instantly since we can only add and not remove a prefix and then refresh the nametag
			 */
		} else if(args[0].equalsIgnoreCase("disband")) {

			SquadEvent.clearSquad();
			ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Removed all players from the squad."));
			return;

		} else if(args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("list")) {

			HashMap<String, String> squad = SquadEvent.getSquad();

			if(squad.size() == 0) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagMW() +
						EnumChatFormatting.RED + "No one in the squad right now."));
				return;
			}

			IChatComponent imsg = (IChatComponent)new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Players in your squad : \n");

			for(Entry<String, String> entry : squad.entrySet()) {

				String displayname = entry.getKey();
				String fakename = entry.getValue();

				imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GRAY + "- " + EnumChatFormatting.GOLD + displayname					
					+ (displayname.equals(fakename) ? "" : EnumChatFormatting.GREEN + " renamed as : " + EnumChatFormatting.GOLD + entry.getValue()) + "\n"));		

			}
			ChatUtil.addChatMessage(imsg);
			return;

			/*
			 * this doesn't update the nametag instantly since we can only add and not remove a prefix and then refresh the nametag
			 */
		} else if(args[0].equalsIgnoreCase("remove")) {

			if(args.length < 2) {
				ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.RED + "Usage : /squad <remove> <playername>"));
				return;
			}

			for(int i = 1; i < args.length; i++) {

				if(SquadEvent.removePlayer(args[i])) {

					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagMW() +
							EnumChatFormatting.GREEN + "Removed " + EnumChatFormatting.GOLD + args[i] + EnumChatFormatting.GREEN + " from the squad." ));
				} else {
					ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagMW() +
							EnumChatFormatting.GOLD + args[i] + EnumChatFormatting.RED + " isn't in the squad." ));
				}
			}

			return;

		}			

	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		String[] args1 = {"add","disband","list","remove"};
		//return args.length == 1 ? getListOfStringsMatchingLastWord(args, args1) : args.length == 2 ? ( FKCounterMod.isInMwGame() && !GameInfoGrabber.isitPrepPhase() ? getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()) : null ) : null ;
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, args1) : args.length >= 2 ? getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()) : null;
	}

}
