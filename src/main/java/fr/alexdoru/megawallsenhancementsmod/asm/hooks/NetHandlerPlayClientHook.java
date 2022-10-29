package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.google.common.collect.EvictingQueue;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class NetHandlerPlayClientHook {

    public static final HashMap<String, NetworkPlayerInfo> playerInfoMap = new HashMap<>();
    @SuppressWarnings("UnstableApiUsage")
    private static final EvictingQueue<StringLong> latestDisconnected = EvictingQueue.create(20);

    public static void putPlayerInMap(String playerName, NetworkPlayerInfo networkplayerinfo) {
        playerInfoMap.put(playerName, networkplayerinfo);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void removePlayerFromMap(Object o) {
        if (o instanceof NetworkPlayerInfo) {
            final String playerName = ((NetworkPlayerInfo) o).getGameProfile().getName();
            playerInfoMap.remove(playerName);
            latestDisconnected.add(new StringLong(System.currentTimeMillis(), playerName));
            MWPlayerData.dataCache.remove(((NetworkPlayerInfo) o).getGameProfile().getId());
        }
    }

    public static void clearPlayerMap() {
        playerInfoMap.clear();
        latestDisconnected.clear();
        MWPlayerData.dataCache.clear();
    }

    public static void handleTeamPacket(S3EPacketTeams packetIn, ScorePlayerTeam scoreplayerteam) {
        if (packetIn.getAction() == 2) {
            scoreplayerteam.getMembershipCollection().forEach(NameUtil::transformNameTablist);
        }
    }

    public static void printDisconnectedPlayers() {
        final List<String> disconnectedPlayers = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder();
        final long timenow = System.currentTimeMillis();
        for (final StringLong stringLong : latestDisconnected) {
            final String playername = stringLong.message;
            if (playername != null && timenow - stringLong.timestamp <= 1000L && !disconnectedPlayers.contains(playername)) {
                disconnectedPlayers.add(playername);
                stringBuilder.append(" ").append(playername);
            }
        }
        if (disconnectedPlayers.isEmpty()) {
            return;
        }
        final String str = stringBuilder.toString();
        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player" + (disconnectedPlayers.size() == 1 ? "" : "s") + " disconnected :" + EnumChatFormatting.AQUA + str).setChatStyle(new ChatStyle()
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Player" + (disconnectedPlayers.size() == 1 ? "" : "s") + " disconnected in the last second, click this message to run : \n\n" + EnumChatFormatting.YELLOW + "/stalk" + str)))
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk" + str))));
    }

}
