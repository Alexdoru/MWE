package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandSetupApiKey extends CommandBase {

    @Override
    public String getCommandName() {
        return "setapikey";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/setapikey <key>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length != 1) {
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + "\n"
                    + EnumChatFormatting.RED + "Connect on Hypixel and type \"/api new\" to get an Api key"));
        } else {
            HypixelApiKeyUtil.setApiKey(args[0]);
        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

}
