package fr.alexdoru.nocheatersmod.commands;

import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Date;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;

public class CommandSendReportAgain extends CommandBase {

    private static final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public String getCommandName() {
        return "sendreportagain";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/sendreportagain <UUID> <playerName>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
            return;
        }
        String uuid = args[0];
        String playername = args[1];
        WDR wdr = WdredPlayers.getWdredMap().get(uuid);
        if (wdr != null) {
            mc.thePlayer.sendChatMessage("/wdr " + playername + " cheating");
            long time = (new Date()).getTime();
            wdr.timestamp = time;
            wdr.timeLastManualReport = time;
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

}
