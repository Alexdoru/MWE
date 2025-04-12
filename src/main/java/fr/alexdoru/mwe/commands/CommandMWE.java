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
                final String msg1 = "Before the walls fall, mine iron to make armor and store it + food in your enderchest.";
                final String msg2 = "When the walls fall, rush mid to fight for the initial diamonds. Each team has a wither.";
                final String msg3 = "Protect yours and kill enemy withers to stop player respawns. Win by being the last team alive or by";
                final String msg4 = "causing the most player damage during Deathmatch (the last phase). Upgrade your kit in the lobby.";
                final String msg5 = "Outdated but more detailed guide: https://hypixel.net/threads/the-complete-mega-walls-guide.3489088/";
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
