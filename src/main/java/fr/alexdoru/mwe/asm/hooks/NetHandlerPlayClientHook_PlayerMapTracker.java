package fr.alexdoru.mwe.asm.hooks;

import com.google.common.collect.EvictingQueue;
import fr.alexdoru.mwe.asm.accessors.ChatComponentTextAccessor;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.chat.SkinChatHead;
import fr.alexdoru.mwe.data.MWPlayerData;
import fr.alexdoru.mwe.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

import java.util.*;


public class NetHandlerPlayClientHook_PlayerMapTracker {

    @SuppressWarnings("UnstableApiUsage")
    private static final Queue<DisconnectedPlayer> latestDisconnected = EvictingQueue.create(20);
    private static final Map<String, NetworkPlayerInfo> playerInfoMap = new HashMap<>();
    private static final Map<String, ResourceLocation> skinMap = new HashMap<>();

    @SuppressWarnings("unused")
    public static void putPlayerInMap(NetworkPlayerInfo netInfo) {
        playerInfoMap.put(netInfo.getGameProfile().getName(), netInfo);
    }

    @SuppressWarnings("unused")
    public static Object removePlayerFromMap(Object o) {
        if (o instanceof NetworkPlayerInfo) {
            final NetworkPlayerInfo netInfo = (NetworkPlayerInfo) o;
            playerInfoMap.remove(netInfo.getGameProfile().getName());
            latestDisconnected.add(new DisconnectedPlayer(netInfo));
            MWPlayerData.remove(netInfo.getGameProfile().getId());
            if (netInfo.hasLocationSkin()) {
                skinMap.put(netInfo.getGameProfile().getName(), netInfo.getLocationSkin());
            }
        }
        return o;
    }

    @SuppressWarnings("unused")
    public static void clearPlayerMap() {
        playerInfoMap.clear();
        skinMap.clear();
        latestDisconnected.clear();
        MWPlayerData.clearData();
    }

    public static NetworkPlayerInfo getPlayerInfo(String playername) {
        return playerInfoMap.get(playername);
    }

    public static ResourceLocation getPlayerSkin(String playername) {
        return skinMap.get(playername);
    }

    public static void printDisconnectedPlayers() {
        final List<String> disconnectList = new ArrayList<>();
        final StringBuilder commandBuilder = new StringBuilder();
        final StringBuilder messageBuilder = new StringBuilder();
        final long timenow = System.currentTimeMillis();
        ResourceLocation skin = null;
        for (final DisconnectedPlayer disconnectedPlayer : latestDisconnected) {
            if (disconnectedPlayer.playername != null && timenow - disconnectedPlayer.disconnectTime <= 1000L && !disconnectList.contains(disconnectedPlayer.playername)) {
                final NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
                NetworkPlayerInfo netInfo = null;
                if (netHandler != null) {
                    netInfo = netHandler.getPlayerInfo(disconnectedPlayer.uuid);
                }
                if (netInfo == null) {
                    disconnectList.add(disconnectedPlayer.playername);
                    commandBuilder.append(" ").append(disconnectedPlayer.playername);
                    messageBuilder.append(" ").append(disconnectedPlayer.formattedName);
                    if (skin == null) {
                        skin = skinMap.get(disconnectedPlayer.playername);
                    }
                }
            }
        }
        if (disconnectList.isEmpty()) {
            return;
        }
        final String command = commandBuilder.toString();
        final String formattedString = messageBuilder.toString();
        final IChatComponent msg = new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player" + (disconnectList.size() == 1 ? "" : "s") + " disconnected :" + EnumChatFormatting.AQUA + command)
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                EnumChatFormatting.GREEN + "Player" + (disconnectList.size() == 1 ? "" : "s") + " disconnected in the last second :" + formattedString + "\n\n" +
                                        EnumChatFormatting.YELLOW + "Click this message to run : \n" + EnumChatFormatting.YELLOW + "/stalk" + command)))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk" + command)));
        if (skin != null && msg instanceof ChatComponentTextAccessor) {
            ((ChatComponentTextAccessor) msg).setSkinChatHead(new SkinChatHead(skin));
        }
        ChatUtil.addChatMessage(msg);
    }

    private static class DisconnectedPlayer {
        public final long disconnectTime;
        public final UUID uuid;
        public final String playername;
        public final String formattedName;

        public DisconnectedPlayer(NetworkPlayerInfo netInfo) {
            this.disconnectTime = System.currentTimeMillis();
            this.uuid = netInfo.getGameProfile().getId();
            this.playername = netInfo.getGameProfile().getName();
            this.formattedName = NameUtil.getFormattedName(netInfo);
        }
    }

}
