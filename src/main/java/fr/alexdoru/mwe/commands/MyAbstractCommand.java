package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.chat.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

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

    protected static boolean isHelpSubcommand(String subcommand) {
        return "h".equalsIgnoreCase(subcommand) || "help".equalsIgnoreCase(subcommand);
    }

    /**
     * Prints command help lines grouped inside 2 horizontal bars
     *
     * @see #printCommandHelpBlock(String, String[][])
     */
    protected static void printCommandHelpBlock(EnumChatFormatting barColor, String header, String[][] commandLines) {
        final String bar = barColor + ChatUtil.bar();
        ChatUtil.addChatMessage(new ChatComponentText(bar));
        MyAbstractCommand.printCommandHelpBlock(header, commandLines);
        ChatUtil.addChatMessage(new ChatComponentText(bar));
    }

    /**
     * @param header       Block Header
     * @param commandLines Each command-line must be in this format: { command, description, [commandToPutOnClick] }.
     *                     if (commandToPutOnClick) isn't provided will use (command)
     */
    private static void printCommandHelpBlock(String header, String[][] commandLines) {
        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.centerLine(EnumChatFormatting.GOLD + header)));
        for (final String[] line : commandLines) {
            if (line.length != 2) continue;
            final String command = line[0];
            final String desc = line[1];
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + command + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + desc)
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GRAY + "Click to put the command in chat.")))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)))
            );
        }
    }

}
