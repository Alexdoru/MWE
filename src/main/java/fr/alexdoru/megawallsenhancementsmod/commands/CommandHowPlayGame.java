package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.fkcountermod.utils.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandHowPlayGame extends CommandBase {

    private static final String msg1 = "During the first 6 minutes you have to mine iron, make armor and store everything in your enderchest";
    private static final String msg2 = "Once the walls fall down you can go to mid and fight other players, each class has unique abilities";
    private static final String msg3 = "Every team has a wither, you have to protect yours and kill the withers from the other teams";
    private static final String msg4 = "Once a wither is dead the players from that team can't respawn, be the last team standing to win";
    private static final String msg5 = "More informations about the game: https://hypixel.net/threads/the-complete-mega-walls-guide.3489088/";

    @Override
    public String getCommandName() {
        return "howplaygame";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/howplaygame";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        String title = ScoreboardUtils.getUnformattedSidebarTitle();
        if (title != null && title.contains("MEGA WALLS")) {
            sendChatMessage(msg1);
            new DelayedTask(() -> sendChatMessage(msg2), 80);
            new DelayedTask(() -> sendChatMessage(msg3), 155);
            new DelayedTask(() -> sendChatMessage(msg4), 240);
            new DelayedTask(() -> sendChatMessage(msg5), 320);
        } else {
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + "Command only works in Mega Walls"));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    private void sendChatMessage(String msg) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage(msg);
        }
    }

}

