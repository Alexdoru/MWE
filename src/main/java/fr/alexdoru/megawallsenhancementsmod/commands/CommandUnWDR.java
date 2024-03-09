package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WDR;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WdrData;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.UUIDUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.UUID;

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
            this.unwdrPlayer(args[0]);
        } else if (args.length == 2) { // when you click the message it does /unwdr <UUID> <playername>
            this.unwdr(args[0], args[1]);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

    private void unwdrPlayer(String playername) {
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final MojangPlayernameToUUID apireq = new MojangPlayernameToUUID(playername);
                mc.addScheduledTask(() -> this.unwdr(apireq.getUuid(), apireq.getName()));
            } catch (ApiException e) {
                mc.addScheduledTask(() -> this.unwdr(null, playername));
            }
            return null;
        });
    }

    private void unwdr(String uuidstr, String playername) {
        final UUID uuid = UUIDUtil.fromString(uuidstr);
        final WDR wdr = WdrData.remove(uuid, playername);
        if (wdr == null) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player not found in your report list.");
            return;
        }
        WdrData.saveReportedPlayers();
        ChatHandler.deleteWarningFromChat(playername);
        NameUtil.updateMWPlayerDataAndEntityData(playername, false);
        ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.RED + playername + EnumChatFormatting.GREEN + ".");
    }

}
