package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.GeneralConfigGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.List;

public class CommandMWEnhancements extends CommandBase {

    @Override
    public String getCommandName() {
        return "mwenhancements";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mwenhancements";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("refreshconfig")) {
            ConfigHandler.preinit(MegaWallsEnhancementsMod.configurationFile);
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Reloaded values from the config file."));
            return;
        } else if(args.length >= 1 && args[0].equalsIgnoreCase("setapikey")) {
            if (args.length != 2) {
                ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + " setapikey <key>" + "\n"
                        + EnumChatFormatting.RED + "Connect on Hypixel and type \"/api new\" to get an Api key"));
            } else {
                HypixelApiKeyUtil.setApiKey(args[1]);
            }
            return;
        }
        new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GeneralConfigGuiScreen()), 1);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        String[] possibilities = {"refreshconfig" , "setapikey"};
        return getListOfStringsMatchingLastWord(args, possibilities);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("megawallsenhancements");
    }

}
