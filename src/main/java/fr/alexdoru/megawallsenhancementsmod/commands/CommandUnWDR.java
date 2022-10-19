package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.data.WdredPlayers;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandUnWDR extends CommandBase {

    @Override
    public String getCommandName() {
        return "unwdr";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/unwdr <playername>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length < 1 || args.length > 3) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender));
            return;
        }

        if (args.length == 1) { // if you use /unwdr <playername>

            MultithreadingUtil.addTaskToQueue(() -> {

                final CachedMojangUUID apireq;
                String playername = args[0];
                try {
                    apireq = (new CachedMojangUUID(playername));
                    playername = apireq.getName();
                } catch (ApiException e) {
                    ChatUtil.addChatMessage(EnumChatFormatting.RED + e.getMessage());
                    return null;
                }

                final String uuid = apireq.getUuid();
                final WDR wdr = WdredPlayers.getWdredMap().get(uuid);

                if (wdr == null) {
                    ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player not found in your report list.");
                } else {
                    removeOrUpdateWDR(wdr, uuid);
                    NameUtil.updateGameProfileAndName(playername, false);
                    ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.RED + playername + EnumChatFormatting.GREEN + ".");
                }

                return null;

            });

        } else if (args.length == 2) { // when you click the message it does /unwdr <UUID> <playername>

            final String uuid = args[0];
            final WDR wdr = WdredPlayers.getWdredMap().get(uuid);

            if (wdr == null) {
                ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player not found in your report list.");
            } else {
                removeOrUpdateWDR(wdr, uuid);
                NameUtil.updateGameProfileAndName(args[1], false);
                ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.RED + args[1] + EnumChatFormatting.GREEN + ".");
            }

        }

    }

    private void removeOrUpdateWDR(WDR wdr, String uuid) {
        if (wdr.isIgnored()) {
            wdr.hacks.clear();
            wdr.hacks.add(WDR.IGNORED);
        } else {
            WdredPlayers.getWdredMap().remove(uuid);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

}
