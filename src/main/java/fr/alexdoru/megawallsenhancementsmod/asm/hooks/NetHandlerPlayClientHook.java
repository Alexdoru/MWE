package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.google.common.collect.EvictingQueue;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
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

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class NetHandlerPlayClientHook {

    public static final HashMap<String, NetworkPlayerInfo> playerInfoMap = new HashMap<>();
    private static final EvictingQueue<DisconnectedPlayer> latestDisconnected = EvictingQueue.create(20);

    public static void putPlayerInMap(String playerName, NetworkPlayerInfo networkplayerinfo) {
        playerInfoMap.put(playerName, networkplayerinfo);
    }

    public static void removePlayerFromMap(Object o) {
        if (o instanceof NetworkPlayerInfo) {
            final String playerName = ((NetworkPlayerInfo) o).getGameProfile().getName();
            final NetworkPlayerInfo removedInfo = playerInfoMap.remove(playerName);
            latestDisconnected.add(new DisconnectedPlayer(System.currentTimeMillis(), playerName, removedInfo == null ? playerName : NameUtil.getFormattedName(removedInfo)));
            MWPlayerData.remove(((NetworkPlayerInfo) o).getGameProfile().getId());
        }
    }

    public static void clearPlayerMap() {
        playerInfoMap.clear();
        latestDisconnected.clear();
        MWPlayerData.clearData();
    }

    public static void handleTeamPacket(S3EPacketTeams packetIn, ScorePlayerTeam scoreplayerteam) {
        if (packetIn.getAction() == 2) {
            scoreplayerteam.getMembershipCollection().forEach(NameUtil::onScoreboardPacket);
        }
    }

    public static void printDisconnectedPlayers() {
        final List<String> disconnectList = new ArrayList<>();
        final StringBuilder commandBuilder = new StringBuilder();
        final StringBuilder messageBuilder = new StringBuilder();
        final long timenow = System.currentTimeMillis();
        for (final DisconnectedPlayer disconnectedPlayer : latestDisconnected) {
            final String playername = disconnectedPlayer.playername;
            if (playername != null && timenow - disconnectedPlayer.disconnectTime <= 1000L && !disconnectList.contains(playername)) {
                disconnectList.add(playername);
                commandBuilder.append(" ").append(playername);
                messageBuilder.append(" ").append(disconnectedPlayer.formattedName);
            }
        }
        if (disconnectList.isEmpty()) {
            return;
        }
        final String command = commandBuilder.toString();
        final String formattedString = messageBuilder.toString();
        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player" + (disconnectList.size() == 1 ? "" : "s") + " disconnected :" + EnumChatFormatting.AQUA + command)
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                EnumChatFormatting.GREEN + "Player" + (disconnectList.size() == 1 ? "" : "s") + " disconnected in the last second :" + formattedString + "\n\n" +
                                        EnumChatFormatting.YELLOW + "Click this message to run : \n" + EnumChatFormatting.YELLOW + "/stalk" + command)))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk" + command))));
    }

    ///**
    // * The client seems to receive a packet for blocks in a 32 blocks radius maximum
    // */
    //public static void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn) {
    //    if (mc.theWorld != null) {
    //        final Entity breakerEntity = mc.theWorld.getEntityByID(packetIn.getBreakerId());
    //        if (breakerEntity instanceof EntityPlayerAccessor) {
    //            final PlayerDataSamples data = ((EntityPlayerAccessor) breakerEntity).getPlayerDataSamples();
    //            if (!data.lastBlockPos.equals(packetIn.getPosition())) {
    //                data.lastBlockPos = packetIn.getPosition();
    //                data.entityAgeAtLastBlockChange = breakerEntity.ticksExisted;
    //            }
    //            data.entityAgeAtLastInteractionWithBlock = breakerEntity.ticksExisted;
    //        }
    //    }
    //}

    static class DisconnectedPlayer {

        public final long disconnectTime;
        public final String playername;
        public final String formattedName;

        public DisconnectedPlayer(long disconnectTime, String playername, String formattedName) {
            this.disconnectTime = disconnectTime;
            this.playername = playername;
            this.formattedName = formattedName;
        }

    }

}
