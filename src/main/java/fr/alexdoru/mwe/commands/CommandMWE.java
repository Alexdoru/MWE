package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.MWEApi;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.gui.huds.DebugScoreboardHUD;
import fr.alexdoru.mwe.http.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.scoreboard.ScoreboardUtils;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class CommandMWE extends MyAbstractCommand {

    private final TimerUtil timer = new TimerUtil(5 * 60 * 1000);
    private DebugScoreboardHUD debugHUD;

    @Override
    public String getCommandName() {
        return "mwe";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("debugscoreboard")) {
            debugScoreboard();
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("getscoreboard")) {
            final ScoreObjective objective = ScoreboardUtils.getActiveObjective();
            if (objective != null) {
                ChatUtil.addChatMessage(objective.getDisplayName());
            }
            ScoreboardUtils.getFormattedSidebarText().forEach(ChatUtil::addChatMessage);
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("setapikey")) {
            if (args.length != 2) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + " setapikey <key>");
            } else {
                HypixelApiKeyUtil.setApiKey(args[1]);
            }
        } else if (args.length >= 1 && (args[0].equalsIgnoreCase("howplaygame") || args[0].equalsIgnoreCase("hpg") || args[0].equalsIgnoreCase("explain"))) {
            if (ScoreboardTracker.isMWEnvironement()) {
                if (this.timer.update()) {
                    final String msg1 = "Before the walls fall, mine iron to make armor. Store armor, blocks and food in your ender chest.";
                    final String msg2 = "When the walls fall, rush mid for diamonds then attack enemy withers or defend yours.";
                    final String msg3 = "After your wither dies, you can't respawn, so meet your team at spawn and stay together.";
                    final String msg4 = "Win by being the last team standing, or the team with most damage during deathmatch.";
                    final String msg5 = "You can share resources with your team using /tc. Upgrade your kit using the shop in the lobby.";
                    sendChatMessage(msg1);
                    new DelayedTask(() -> sendChatMessage(msg2), 80);
                    new DelayedTask(() -> sendChatMessage(msg3), 155);
                    new DelayedTask(() -> sendChatMessage(msg4), 240);
                    new DelayedTask(() -> sendChatMessage(msg5), 320);
                } else {
                    ChatUtil.addChatMessage(EnumChatFormatting.RED + "Command is on cooldown");
                }
            } else {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Command only works in Mega Walls");
            }
        } else {
            MWEApi.Config.displayConfigGuiScreen();
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final String[] possibilities = {"howplaygame", "setapikey"};
        return getListOfStringsMatchingLastWord(args, possibilities);
    }

    private void debugScoreboard() {
        if (this.debugHUD == null) {
            this.debugHUD = new DebugScoreboardHUD();
            MinecraftForge.EVENT_BUS.register(this.debugHUD);
        } else {
            MinecraftForge.EVENT_BUS.unregister(this.debugHUD);
            this.debugHUD = null;
        }
    }

}
