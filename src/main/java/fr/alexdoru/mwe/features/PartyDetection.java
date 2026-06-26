package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.data.NetPlayerInfoTracker;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PartyDetection {

    private static final long TIME_RESET_PARTYS = 20L * 60L * 1000L;
    private static final long TIME_SAME_PARTY = 101L;

    private static final Map<String, Set<String>> PARTYS = new HashMap<>();
    private static String savedServerID;
    private static String lastPlayerJoining;
    private static long timeLastJoin = 0;

    public static void onPlayerJoin(String playername) {

        final long jointime = System.currentTimeMillis();
        final String serverID = ScoreboardTracker.getServerID();

        if (serverID != null && savedServerID != null && !serverID.equals(savedServerID)) {
            PARTYS.clear();
        }

        if (jointime - timeLastJoin > TIME_RESET_PARTYS) {
            PARTYS.clear();
        }

        if (lastPlayerJoining != null && jointime - timeLastJoin < TIME_SAME_PARTY) {
            final Set<String> partyLastPlayer = PARTYS.computeIfAbsent(lastPlayerJoining, name -> new HashSet<>(Collections.singletonList(name)));
            final Set<String> partyPlayer = PARTYS.computeIfAbsent(playername, name -> new HashSet<>(Collections.singletonList(name)));
            partyLastPlayer.addAll(partyPlayer);
            partyPlayer.addAll(partyLastPlayer);
        }

        if (serverID != null) savedServerID = serverID;
        lastPlayerJoining = playername;
        timeLastJoin = jointime;

    }

    public static void printBoostingReportAdvice(String playername) {
        final Set<String> party = PARTYS.get(playername);
        if (party == null) return;
        final NetworkPlayerInfo cheaterNetInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
        if (cheaterNetInfo == null) {
            return;
        }
        final String cheaterTeamColor = StringUtil.getLastColorCodeBefore(ScorePlayerTeam.formatPlayerName(cheaterNetInfo.getPlayerTeam(), playername), playername);
        final ChatComponentText imsg = new ChatComponentText(EnumChatFormatting.GREEN + "This player joined in a party with : ");
        boolean containsPlayers = false;
        for (final String player : party) {
            if (!player.equals(playername)) {
                final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(player);
                if (netInfo != null) {
                    final String teamColorPlayer = StringUtil.getLastColorCodeBefore(ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), player), player);
                    if (!cheaterTeamColor.isEmpty() && cheaterTeamColor.equals(teamColorPlayer)) {
                        containsPlayers = true;
                        imsg.appendSibling(new ChatComponentText(NameFormatter.getFormattedName(netInfo) + " ")
                                .setChatStyle(new ChatStyle()
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click here to report " + player + " for boosting")))
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + player + " boo"))));
                    }
                }
            }
        }
        imsg.appendText(EnumChatFormatting.GREEN + "you can click their name to report them for boosting.");
        if (containsPlayers) {
            new DelayedTask(() -> ChatUtil.addChatMessage(imsg), 10);
        }
    }

    @NotNull
    public static Set<String> getPlayersWithParty() {
        return Collections.unmodifiableSet(PARTYS.keySet());
    }

    @NotNull
    public static Set<String> getPartyOf(String playername) {
        final Set<String> p = PARTYS.get(playername);
        if (p == null) return Collections.emptySet();
        return Collections.unmodifiableSet(p);
    }

    @Nullable
    public static String getServerID() {
        return savedServerID;
    }

}


