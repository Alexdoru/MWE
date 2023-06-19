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

    protected void printCommandHelp() {}

    protected static void sendChatMessage(String msg) {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage(msg);
        }
    }

    protected static void sendChatMessage(String command, String[] args) {
        sendChatMessage(command + buildString(args, 0));
    }

}
