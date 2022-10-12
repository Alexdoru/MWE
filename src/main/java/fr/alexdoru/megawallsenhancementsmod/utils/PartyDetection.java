package fr.alexdoru.megawallsenhancementsmod.utils;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.utils.DelayedTask;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartyDetection {

    private static String lastPlayerJoining = "";
    private static long timeLastJoin = 0;
    private static final HashMap<String, List<String>> partysMap = new HashMap<>();

    public static void onPlayerJoin(String playername, long jointime) {

        if (timeLastJoin != 0 && jointime - timeLastJoin > 25L * 60L * 1000L) {
            partysMap.clear();
        }

        if (jointime - timeLastJoin < 101L && !lastPlayerJoining.equals("")) {
            final List<String> partyLastPlayer = partysMap.get(lastPlayerJoining);
            final List<String> partyPlayer = partysMap.get(playername);
            if (partyLastPlayer != null && partyPlayer != null) {
                addAllWithoutDuplicates(partyLastPlayer, partyPlayer);
                partysMap.put(playername, partyLastPlayer);
            } else if (partyLastPlayer != null) {
                partyLastPlayer.add(playername);
                partysMap.put(playername, partyLastPlayer);
            } else if (partyPlayer != null) {
                partyPlayer.add(lastPlayerJoining);
                partysMap.put(lastPlayerJoining, partyPlayer);
            } else {
                final List<String> newParty = new ArrayList<>();
                newParty.add(lastPlayerJoining);
                newParty.add(playername);
                partysMap.put(lastPlayerJoining, newParty);
                partysMap.put(playername, newParty);
            }
        }

        lastPlayerJoining = playername;
        timeLastJoin = jointime;

    }

    private static void addAllWithoutDuplicates(List<String> listToKeep, List<String> listToAdd) {
        for (final String s : listToAdd) {
            if (!listToKeep.contains(s)) {
                listToKeep.add(s);
            }
        }
    }

    public static void printBoostingReportAdvice(String playername) {
        final List<String> partyList = partysMap.get(playername);
        if (partyList != null) {
            final NetworkPlayerInfo infoPlayername = NetHandlerPlayClientHook.playerInfoMap.get(playername);
            if (infoPlayername == null) {
                return;
            }
            final String teamColorPlayername = StringUtil.getLastColorCodeBefore(ScorePlayerTeam.formatPlayerName(infoPlayername.getPlayerTeam(), playername), playername);
            final ChatComponentText imsg = new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "This player joined in a party with : ");
            boolean containsPlayers = false;
            for (final String player : partyList) {
                if (!player.equals(playername)) {
                    final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(player);
                    if (networkPlayerInfo != null) {
                        final String teamColorPlayer = StringUtil.getLastColorCodeBefore(ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), player), player);
                        if (!teamColorPlayername.equals("") && teamColorPlayername.equals(teamColorPlayer)) {
                            containsPlayers = true;
                            imsg.appendSibling(new ChatComponentText(NameUtil.getFormattedName(player) + " ")
                                    .setChatStyle(new ChatStyle()
                                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click here to report " + player + " for boosting")))
                                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + player + " -b BOO -C"))));
                        }
                    }
                }
            }
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + "you can click their name to report them for boosting."));
            if (containsPlayers) {
                new DelayedTask(() -> ChatUtil.addChatMessage(imsg), 10);
            }
        }
    }

}


