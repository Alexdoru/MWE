package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandHypixelReply extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "r";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/r <message>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sendChatMessage("/r ", args);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

}
