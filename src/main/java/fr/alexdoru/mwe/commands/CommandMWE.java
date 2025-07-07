package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DelayedTask;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandMWE extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "mwe";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("setapikey")) {
            if (args.length != 2) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + " setapikey <key>");
            } else {
                HypixelApiKeyUtil.setApiKey(args[1]);
            }
        } else if (args.length >= 1 && (args[0].equalsIgnoreCase("howplaygame") || args[0].equalsIgnoreCase("hpg") || args[0].equalsIgnoreCase("explain"))) {
            if (ScoreboardTracker.isMWEnvironement()) {
                final String msg1 = "At the start mine iron to make armor. Store armor + food in echest.";
                final String msg2 = "When the walls fall, rush mid for diamonds then attack enemy withers or defend yours.";
                final String msg3 = "When your wither dies, you can't respawn so meet team at spawn and stay together.";
                final String msg4 = "Win by causing the most damage after all withers die or by being the last team alive.";
                final String msg5 = "You can share resources with your team using /tc and upgrade your kit in the lobby.";
                sendChatMessage(msg1);
                new DelayedTask(() -> sendChatMessage(msg2), 80);
                new DelayedTask(() -> sendChatMessage(msg3), 155);
                new DelayedTask(() -> sendChatMessage(msg4), 240);
                new DelayedTask(() -> sendChatMessage(msg5), 320);
            } else {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "Command only works in Mega Walls");
            }
        } else {
            MWEConfig.displayConfigGuiScreen();
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final String[] possibilities = {"howplaygame", "setapikey"};
        return getListOfStringsMatchingLastWord(args, possibilities);
    }

}
