package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.NameFormatter;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
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

            final String lastKey = lastPlayerJoining.toLowerCase();
            final String curKey = playername.toLowerCase();
            final Set<String> partyLast = PARTYS.get(lastKey);
            final Set<String> partyCur = PARTYS.get(curKey);

            if (partyLast == null && partyCur == null) {

                final Set<String> party = new HashSet<>(Arrays.asList(lastPlayerJoining, playername));
                PARTYS.put(lastKey, party);
                PARTYS.put(curKey, party);

            } else if (partyLast == null || partyCur == null) {

                if (partyLast == null) {

                    partyCur.add(lastPlayerJoining);
                    PARTYS.put(lastKey, partyCur);

                } else { // partyCur == null

                    partyLast.add(playername);
                    PARTYS.put(curKey, partyLast);

                }

            } else { // partyLast != null && partyCur != null

                partyLast.addAll(partyCur);
                partyLast.forEach(p -> PARTYS.put(p.toLowerCase(), partyLast));

            }
        }

        if (serverID != null) savedServerID = serverID;
        lastPlayerJoining = playername;
        timeLastJoin = jointime;

    }

    public static void printBoostingReportAdvice(String playername) {
        if (playername == null || !MWEConfig.showBoostingReportAdvance) return;
        final Set<String> party = PARTYS.get(playername.toLowerCase());
        if (party == null) return;
        final Minecraft mc = Minecraft.getMinecraft();
        final NetworkPlayerInfo cheaterNetInfo = mc.getNetHandler().getPlayerInfo(playername);
        if (cheaterNetInfo == null) {
            return;
        }
        final char cheaterTeamColor = getTeamColor(cheaterNetInfo);
        final ChatComponentText imsg = new ChatComponentText(EnumChatFormatting.GREEN + "This player joined in a party with : ");
        boolean containsPlayers = false;
        for (final String player : party) {
            if (!player.equals(playername)) {
                final NetworkPlayerInfo netInfo = mc.getNetHandler().getPlayerInfo(player);
                if (netInfo != null) {
                    final char teamColorPlayer = getTeamColor(netInfo);
                    if (cheaterTeamColor == teamColorPlayer) {
                        containsPlayers = true;
                        imsg.appendSibling(new ChatComponentText(NameFormatter.getTablistName(netInfo) + " ")
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

    private static char getTeamColor(NetworkPlayerInfo netInfo) {
        return StringUtil.getLastColorCharBefore(NameFormatter.getVanillaName(netInfo), netInfo.getGameProfile().getName());
    }

    @NotNull
    public static Set<String> getPlayersWithParty() {
        return Collections.unmodifiableSet(PARTYS.keySet());
    }

    @NotNull
    public static Set<String> getPartyOf(String playername) {
        if (playername == null) return Collections.emptySet();
        final Set<String> p = PARTYS.get(playername.toLowerCase());
        if (p == null) return Collections.emptySet();
        return Collections.unmodifiableSet(p);
    }

    @Nullable
    public static String getServerID() {
        return savedServerID;
    }

}


