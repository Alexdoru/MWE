package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.data.NameFormatter;
import fr.alexdoru.mwe.data.NetPlayerInfoTracker;
import fr.alexdoru.mwe.features.PartyDetection;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashSet;
import java.util.Set;

public class CommandPartyDetection extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "partydetection";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (PartyDetection.getPlayersWithParty().isEmpty()) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "No party detected.");
            return;
        }

        boolean isFirst = true;

        final Set<String> playerPrinted = new HashSet<>();
        for (final String player : PartyDetection.getPlayersWithParty()) {
            if (playerPrinted.contains(player)) continue;
            final Set<String> party = PartyDetection.getPartyOf(player);
            playerPrinted.addAll(party);
            final StringBuilder sb = new StringBuilder();
            boolean flag = false;
            sb.append(EnumChatFormatting.DARK_GRAY).append("- ");
            for (final String playername : party) {
                final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
                sb.append(EnumChatFormatting.DARK_GRAY);
                if (netInfo == null) {
                    sb.append(playername);
                } else {
                    flag = true;
                    sb.append(NameFormatter.getTablistName(netInfo));
                }
                sb.append(EnumChatFormatting.RESET);
                if (ScoreboardTracker.isPreGameLobby() && netInfo != null) {
                    final MWSkin skin = MWSkin.ofPlayer(netInfo);
                    if (skin != null && skin.mwClass != null) {
                        sb.append(EnumChatFormatting.GRAY).append(" [").append(skin.mwClass.TAG).append("]");
                    }
                }
                sb.append(" ");
            }
            if (flag) {
                if (isFirst) {
                    ChatUtil.addChatMessage(EnumChatFormatting.GREEN + ChatUtil.bar());
                    ChatUtil.addChatMessage(ChatUtil.centerLine(EnumChatFormatting.GREEN + "Partys in lobby " + EnumChatFormatting.YELLOW + PartyDetection.getServerID()));
                    isFirst = false;
                }
                ChatUtil.addChatMessage("");
                ChatUtil.addChatMessage(sb.toString());
            }
        }

        if (isFirst) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "No party in current lobby.");
        } else {
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + ChatUtil.bar());
        }
    }

}
