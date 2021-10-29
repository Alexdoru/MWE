package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
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
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        (Minecraft.getMinecraft()).thePlayer.sendChatMessage("/msg " + CommandBase.buildString(args, 0));
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
        //return (GameInfoGrabber.isitPrepPhase() ? null : getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()));
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

}
