package fr.alexdoru.megawallsenhancementsmod.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public abstract class MyAbstractCommand extends CommandBase {

    protected static final Minecraft mc = Minecraft.getMinecraft();

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
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage(msg);
        }
    }

}
