package fr.alexdoru.mwe.nocheaters;

import fr.alexdoru.mwe.chat.ChatHandler;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.chat.WarningChatComponent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.NameFormatter;
import fr.alexdoru.mwe.data.PlayerDataManager;
import fr.alexdoru.mwe.data.WdrDataManager;
import fr.alexdoru.mwe.utils.DateUtil;
import fr.alexdoru.mwe.utils.NetInfoOrdering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.util.EnumChatFormatting.*;

public final class WarningMessageHandler {

    private final Set<UUID> msgPrinted = new HashSet<>();
    private long lastDeathTime;

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote && (System.currentTimeMillis() - lastDeathTime > 5000L)) {
            msgPrinted.clear();
        }
    }

    @SubscribeEvent
    public void onGuiScreen(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (event.gui instanceof GuiGameOver) {
            lastDeathTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (MWEConfig.warningMessages && event.entity.worldObj.isRemote && event.entity instanceof EntityOtherPlayerMP) {
            try {
                final EntityPlayer player = (EntityPlayer) event.entity;
                if (!msgPrinted.contains(player.getUniqueID())) {
                    final WDR wdr = WdrDataManager.getWdr(player.getUniqueID(), player.getName());
                    if (wdr != null) {
                        msgPrinted.add(player.getUniqueID());
                        ChatHandler.deleteWarningFromChat(player.getName());
                        printWarningMessage(player.getUniqueID(), player.getTeam(), player.getName(), wdr);
                    }
                }
            } catch (Exception e) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Caught an exception when spawning " + event.entity.getName());
                e.printStackTrace();
            }
        }
    }

    public static int printReportMessagesForWorld() {
        ChatHandler.deleteAllWarningMessages();
        int count = 0;
        for (final NetworkPlayerInfo netInfo : NetInfoOrdering.vanillaSortingCopyOf(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap())) {
            final UUID uuid = netInfo.getGameProfile().getId();
            final String playerName = netInfo.getGameProfile().getName();
            final WDR wdr = WdrDataManager.getWdr(uuid, playerName);
            if (wdr == null) {
                continue;
            }
            count++;
            printWarningMessage(uuid, netInfo.getPlayerTeam(), playerName, wdr);
        }
        return count;
    }

    private static void printWarningMessage(UUID uuid, Team team, String playername, WDR wdr) {
        final String wdrmapKey = PlayerDataManager.isRealPlayer(uuid) ? uuid.toString() : playername;
        final IChatComponent imsg = new WarningChatComponent(playername, RED + "Warning : ")
                .appendSibling(getPlayernameWithHoverText(null, team, playername, wdrmapKey, wdr))
                .appendText(GRAY + " joined, Cheats :")
                .appendSibling(wdr.getFormattedCheats());
        ChatUtil.addSkinToComponent(imsg, playername);
        ChatUtil.addChatMessage(imsg);
    }

    public static IChatComponent getPlayernameWithHoverText(String formattedName, Team team, String playername, String wdrmapKey, WDR wdr) {
        if (formattedName == null) {
            formattedName = NameFormatter.getFormattedNameWithoutIcons(team, playername);
        }
        return new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + wdrmapKey + " " + playername))
                .setChatHoverEvent(getWDRHoverEvent(formattedName, wdr)));
    }

    public static HoverEvent getWDRHoverEvent(String formattedName, WDR wdr) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                formattedName + "\n"
                        + GREEN + "Last reported : " + YELLOW + DateUtil.timeSince(wdr.getTimestamp()) + " ago, on " + DateUtil.localFormatTime(wdr.getTimestamp()) + "\n"
                        + GREEN + "Reported for :" + GOLD + wdr.cheatsToString() + "\n\n"
                        + YELLOW + "Click here to remove this player from your report list")
        );
    }

}
