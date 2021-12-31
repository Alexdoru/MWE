package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;

public class CommandHypixelMessage extends CommandBase {

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
        (Minecraft.getMinecraft()).thePlayer.sendChatMessage("/msg " + buildString(args, 0));
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("w", "msg", "MSG", "Msg");
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
