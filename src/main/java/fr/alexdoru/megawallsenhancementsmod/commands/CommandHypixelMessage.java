package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;

public class CommandHypixelMessage extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "message";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/msg <playername> <message>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sendChatMessage("/msg ", args);
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("w", "msg");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

}
