package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;

public class CommandReport extends CommandBase {

    /* cheats recognized by hypixel*/
    public static final String[] recognizedcheats = {
            "aura",
            "aimbot",
            "bhop",
            "velocity",
            "reach",
            "speed",
            "ka",
            "killaura",
            "forcefield",
            "antiknockback",
            "autoclicker",
            "ac",
            "fly",
            "dolphin",
            "jesus"};
    /* cheats for the tabcompletion*/
    public static final String[] cheatsArray = {
            "aura",
            "aimbot",
            "bhop",
            "velocity",
            "reach",
            "speed",
            "ka",
            "killaura",
            "multiaura",
            "forcefield",
            "autoblock",
            "antiknockback",
            "antikb",
            "autoclicker",
            "ac",
            "fly",
            "dolphin",
            "jesus",
            "keepsprint",
            "noslowdown",
            "fastbreak",
            "speedmine",
            "cheating"
    };
    public static final List<String> cheatsList = Arrays.asList(cheatsArray);

    @Override
    public String getCommandName() {
        return "report";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/report <player> <cheats>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? (FKCounterMod.isInMwGame && FKCounterMod.isitPrepPhase ? getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()) : null) : (args.length > 1 ? getListOfStringsMatchingLastWord(args, cheatsArray) : null);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length < 1) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender));
            return;
        }

        final StringBuilder msg = new StringBuilder("/report " + args[0]);

        for (int i = 1; i < args.length; i++) {

            if (args[i].equalsIgnoreCase("fastbreak")) {
                msg.append(" speed");
            } else if (args[i].equalsIgnoreCase("autoblock") || args[i].equalsIgnoreCase("multiaura")) {
                msg.append(" killaura");
            } else if (args[i].equalsIgnoreCase("noslowdown") || args[i].equalsIgnoreCase("keepsprint")) {
                msg.append(" velocity");
            } else {
                msg.append(" ").append(args[i]); //reconstructs the message to send it to the server
            }

        }

        Minecraft.getMinecraft().thePlayer.sendChatMessage(msg.toString());

    }

}
