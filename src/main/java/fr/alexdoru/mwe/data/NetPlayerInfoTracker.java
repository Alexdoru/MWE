package fr.alexdoru.mwe.data;

import fr.alexdoru.mwe.asm.interfaces.ChatComponentTextAccessor;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.chat.SkinChatHead;
import fr.alexdoru.mwe.hackerdetector.data.SampleList;
import fr.alexdoru.mwe.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

import java.util.*;

public final class NetPlayerInfoTracker {

    private static final int SKIN_CACHE_SIZE = 256;
    private static final SampleList<DisconnectedPlayer> latestDisconnected = new SampleList<>(32);
    private static final Map<String, NetworkPlayerInfo> NET_INFO_CACHE = new HashMap<>();
    private static final Map<String, ResourceLocation> SKIN_CACHE = new LinkedHashMap<String, ResourceLocation>(SKIN_CACHE_SIZE) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, ResourceLocation> eldest) {
            return size() > SKIN_CACHE_SIZE;
        }
    };

    private NetPlayerInfoTracker() {}

    public static void clearData() {
        NET_INFO_CACHE.clear();
        latestDisconnected.clear();
        MWPlayerData.clearData();
    }

    public static void addPlayer(NetworkPlayerInfo netInfo) {
        NET_INFO_CACHE.put(netInfo.getGameProfile().getName(), netInfo);
    }

    public static Object removePlayer(Object o) {
        if (o instanceof NetworkPlayerInfo) {
            final NetworkPlayerInfo netInfo = (NetworkPlayerInfo) o;
            NET_INFO_CACHE.remove(netInfo.getGameProfile().getName());
            latestDisconnected.add(new DisconnectedPlayer(netInfo));
            MWPlayerData.remove(netInfo.getGameProfile().getId());
            if (netInfo.hasLocationSkin()) {
                SKIN_CACHE.put(netInfo.getGameProfile().getName(), netInfo.getLocationSkin());
            }
        }
        return o;
    }

    public static NetworkPlayerInfo getPlayerInfo(String playername) {
        return NET_INFO_CACHE.get(playername);
    }

    public static ResourceLocation getSkinFromCache(String playername) {
        return SKIN_CACHE.get(playername);
    }

    public static void printDisconnectedPlayers() {
        final List<String> disconnectList = new ArrayList<>();
        final StringBuilder commandBuilder = new StringBuilder();
        final StringBuilder messageBuilder = new StringBuilder();
        final long timenow = System.currentTimeMillis();
        ResourceLocation skin = null;
        for (int i = 0; i < latestDisconnected.size(); i++) {
            final DisconnectedPlayer disconnectedPlayer = latestDisconnected.get(i);
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
                        skin = SKIN_CACHE.get(disconnectedPlayer.playername);
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
