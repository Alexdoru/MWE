package fr.alexdoru.nocheatersmod.commands;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GameProfileAccessor;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
import fr.alexdoru.megawallsenhancementsmod.gui.NoCheatersConfigGuiScreen;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

import java.util.Date;
import java.util.List;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.getTagNoCheaters;

public class CommandNocheaters extends CommandBase {

    private static final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public String getCommandName() {
        return "nocheaters";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/nocheaters";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        // TODO nocheaters say; shout

        if (args.length == 0) {

            List<IChatComponent> list = NoCheatersEvents.getReportMessagesforWorld();

            if (list.isEmpty()) {
                addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.GREEN + "No reported player here !"));
            } else {
                addChatMessage(new ChatComponentText(getTagNoCheaters() +
                        EnumChatFormatting.YELLOW + "Reported players : ")
                        .appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + "Report ALL"))
                        .setChatStyle(new ChatStyle()
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to report again all the reported players in your world")))
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nocheaters reportworld"))));
                for (IChatComponent report : list) {
                    addChatMessage(report);
                }

            }

            return;

        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reportworld")) {

            reportWorld();

        } else if (args.length == 1 && args[0].equalsIgnoreCase("config")) {

            new DelayedTask(() -> mc.displayGuiScreen(new NoCheatersConfigGuiScreen()), 1);

        }

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        String[] arguments = {"config", "reportworld"};
        return getListOfStringsMatchingLastWord(args, arguments);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    /**
     * This gets called after you typed /nocheaters if you click on the button Report World
     */
    private void reportWorld() {
        long datenow = (new Date()).getTime();
        int nbreport = 0;
        for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
            String playerName = networkPlayerInfo.getGameProfile().getName();
            MWPlayerData mwPlayerData = ((GameProfileAccessor) networkPlayerInfo.getGameProfile()).getMWPlayerData();
            if (mwPlayerData == null) {
                continue;
            }
            WDR wdr = mwPlayerData.wdr;
            if (wdr == null) {
                continue;
            }
            if (wdr.canBeReported(datenow - 900000)) {
                wdr.timestamp = datenow;
                new DelayedTask(() -> {
                    if (mc.thePlayer != null) {
                        mc.thePlayer.sendChatMessage("/wdr " + playerName + " cheating");
                    }
                }, 30 * nbreport);
                nbreport++;
            }
        }
    }

}
