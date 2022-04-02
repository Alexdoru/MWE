package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import fr.alexdoru.nocheatersmod.commands.CommandReport;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;

public class CommandHypixelShout extends CommandBase {

    private static final String guide_url = "https://hypixel.net/threads/the-complete-mega-walls-guide.3489088/";

    @Override
    public String getCommandName() {
        return "shout";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/shout <message>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        StringBuilder msg = new StringBuilder("/shout");

        for (String arg : args) {

            if (arg.equalsIgnoreCase("/guide")) {
                msg.append(" " + guide_url);
            } else {
                msg.append(" ").append(arg);
            }

        }

        (Minecraft.getMinecraft()).thePlayer.sendChatMessage(msg.toString());
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("SHOUT", "Shout");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {

        if (args.length >= 2 && (args[args.length - 2].equalsIgnoreCase("report") || args[args.length - 2].equalsIgnoreCase("wdr"))) {
            if (FKCounterMod.isitPrepPhase) {
                return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
            } else {
                return null;
            }
        }

        if (args.length >= 3 && (args[args.length - 3].equalsIgnoreCase("report") || args[args.length - 3].equalsIgnoreCase("wdr"))) {
            return getListOfStringsMatchingLastWord(args, CommandReport.cheatsArray);
        }

        if (FKCounterMod.isitPrepPhase) {
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
        }

        return null;

    }

}