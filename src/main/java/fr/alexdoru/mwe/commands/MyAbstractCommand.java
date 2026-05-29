package fr.alexdoru.mwe.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public abstract class MyAbstractCommand extends CommandBase {

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName();
    }

    protected void printCommandHelp() {}

    protected void sendCommand() {
        sendChatMessage('/' + this.getCommandName());
    }

    protected void sendCommand(String[] args) {
        sendChatMessage('/' + this.getCommandName() + " " + buildString(args, 0));
    }

    protected static void sendChatMessage(String msg) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage(msg);
        }
    }

}
