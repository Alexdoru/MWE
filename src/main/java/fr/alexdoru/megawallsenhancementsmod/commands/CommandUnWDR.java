package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.data.WdrData;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandUnWDR extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "unwdr";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length < 1 || args.length > 3) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + " <playername>");
            return;
        }

        if (args.length == 1) { // if you use /unwdr <playername>

            unwdrPlayer(args);

        } else if (args.length == 2) { // when you click the message it does /unwdr <UUID> <playername>

            final String uuid = args[0];
            final WDR wdr = WdrData.getWdr(uuid);

            if (wdr == null) {
                ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player not found in your report list.");
            } else {
                removeOrUpdateWDR(wdr, uuid);
                Minecraft.getMinecraft().addScheduledTask(() -> ChatHandler.deleteWarningMessagesFor(args[1]));
                NameUtil.updateMWPlayerDataAndEntityData(args[1], false);
                ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.RED + args[1] + EnumChatFormatting.GREEN + ".");
            }

        }

    }

    private void unwdrPlayer(String[] args) {
        MultithreadingUtil.addTaskToQueue(() -> {

            final MojangPlayernameToUUID apireq;
            String playername = args[0];
            String uuid;
            try {
                apireq = new MojangPlayernameToUUID(playername);
                playername = apireq.getName();
                uuid = apireq.getUuid();
            } catch (ApiException e) {
                uuid = playername;
            }

            final WDR wdr = WdrData.getWdr(uuid);

            if (wdr == null) {
                ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player not found in your report list.");
            } else {
                removeOrUpdateWDR(wdr, uuid);
                final String finalPlayername = playername;
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    ChatHandler.deleteWarningMessagesFor(finalPlayername);
                    NameUtil.updateMWPlayerDataAndEntityData(finalPlayername, false);
                });
                ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.RED + playername + EnumChatFormatting.GREEN + ".");
            }

            return null;

        });
    }

    private void removeOrUpdateWDR(WDR wdr, String uuid) {
        if (wdr.isIgnored()) {
            wdr.hacks.clear();
            wdr.hacks.add(WDR.IGNORED);
        } else {
            WdrData.remove(uuid);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

}
