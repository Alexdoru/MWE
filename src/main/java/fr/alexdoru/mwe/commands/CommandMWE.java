package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.gui.guiscreens.GeneralConfigGuiScreen;
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
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("howplaygame")) {
            if (ScoreboardTracker.isMWEnvironement()) {
                final String msg1 = "During the first 6 minutes you have to mine iron, make armor and store everything in your enderchest";
                final String msg2 = "Once the walls fall down you can go to mid and fight other players, each class has unique abilities";
                final String msg3 = "Every team has a wither, you have to protect yours and kill the withers from the other teams";
                final String msg4 = "Once a wither is dead the players from that team can't respawn, be the last team standing to win";
                final String msg5 = "More informations about the game: https://hypixel.net/threads/the-complete-mega-walls-guide.3489088/";
                sendChatMessage(msg1);
                new DelayedTask(() -> sendChatMessage(msg2), 80);
                new DelayedTask(() -> sendChatMessage(msg3), 155);
                new DelayedTask(() -> sendChatMessage(msg4), 240);
                new DelayedTask(() -> sendChatMessage(msg5), 320);
            } else {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "Command only works in Mega Walls");
            }
        } else {
            new DelayedTask(() -> mc.displayGuiScreen(new GeneralConfigGuiScreen()));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final String[] possibilities = {"howplaygame", "setapikey"};
        return getListOfStringsMatchingLastWord(args, possibilities);
    }

}
