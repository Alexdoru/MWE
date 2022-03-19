package fr.alexdoru.nocheatersmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.getTagNoCheaters;

public class CommandUnWDR extends CommandBase {

    @Override
    public String getCommandName() {
        return "unwdr";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/unwdr <playername>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        // TODO attention si on UNWDR un mec avec des cheats ou un mecs ignored

        if (args.length < 1 || args.length > 3) {
            addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
            return;
        }

        if (args.length == 1) { // if you use /unwdr <playername>

            Multithreading.addTaskToQueue(() -> {

                CachedMojangUUID apireq;
                String playername = args[0];
                try {
                    apireq = (new CachedMojangUUID(playername));
                    playername = apireq.getName();
                } catch (ApiException e) {
                    addChatMessage(new ChatComponentText(getTagNoCheaters()
                            + EnumChatFormatting.RED + e.getMessage()));
                    return null;
                }

                String uuid = apireq.getUuid();

                WDR wdr = WdredPlayers.getWdredMap().remove(uuid);

                if (wdr == null) {
                    addChatMessage(new ChatComponentText(getTagNoCheaters() +
                            EnumChatFormatting.RED + "Player not found in your report list."));
                } else {
                    NameUtil.updateGameProfileAndName(playername);
                    addChatMessage(new ChatComponentText(getTagNoCheaters() +
                            EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.RED + playername + EnumChatFormatting.GREEN + "."));
                }

                return null;

            });

        } else if (args.length == 2) { // when you click the message it does /unwdr <UUID> <playername>

            WDR wdr = WdredPlayers.getWdredMap().remove(args[0]);

            if (wdr == null) {
                addChatMessage(new ChatComponentText(getTagNoCheaters() +
                        EnumChatFormatting.RED + "Player not found in your report list."));
            } else {
                NameUtil.updateGameProfileAndName(args[1]);
                addChatMessage(new ChatComponentText(getTagNoCheaters() +
                        EnumChatFormatting.GREEN + "You will no longer receive warnings for " + EnumChatFormatting.RED + args[1] + EnumChatFormatting.GREEN + "."));
            }

        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

}
