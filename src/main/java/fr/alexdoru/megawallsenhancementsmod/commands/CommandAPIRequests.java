//package fr.alexdoru.megawallsenhancementsmod.commands;
//
//import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
//import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.util.ChatComponentText;
//import net.minecraft.util.EnumChatFormatting;
//import net.minecraft.util.IChatComponent;
//
//public class CommandAPIRequests extends CommandBase {
//
//    @Override
//    public String getCommandName() {
//        return "apirequests";
//    }
//
//    @Override
//    public String getCommandUsage(ICommandSender sender) {
//        return "/apirequests";
//    }
//
//    @Override
//    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
//
//        int i = HypixelApiKeyUtil.getRequestAmount();
//
//        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() +
//                EnumChatFormatting.GREEN + "You sent " + i + " requests in the last minute, " + (HypixelApiKeyUtil.NB_MAX_REQUESTS - i) + " left."));
//    }
//
//    @Override
//    public boolean canCommandSenderUseCommand(ICommandSender sender) {
//        return true;
//    }
//
//}
