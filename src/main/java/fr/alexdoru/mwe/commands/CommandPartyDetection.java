package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.data.NetPlayerInfoTracker;
import fr.alexdoru.mwe.features.NameFormatter;
import fr.alexdoru.mwe.features.PartyDetection;
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

        ChatUtil.addChatMessage(EnumChatFormatting.GREEN + ChatUtil.bar());
        ChatUtil.addChatMessage(ChatUtil.centerLine(EnumChatFormatting.GREEN + "Partys in lobby " + EnumChatFormatting.YELLOW + PartyDetection.getServerID()));

        final Set<String> playerPrinted = new HashSet<>();
        for (final String player : PartyDetection.getPlayersWithParty()) {
            if (playerPrinted.contains(player)) continue;
            final Set<String> party = PartyDetection.getPartyOf(player);
            playerPrinted.addAll(party);
            final StringBuilder sb = new StringBuilder();
            sb.append(EnumChatFormatting.DARK_GRAY).append("- ");
            for (final String playername : party) {
                final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
                if (netInfo == null) {
                    sb.append(EnumChatFormatting.DARK_GRAY).append(playername).append(" ");
                } else {
                    sb.append(EnumChatFormatting.RESET).append(NameFormatter.getFormattedName(netInfo)).append(" ");
                }
            }
            ChatUtil.addChatMessage(sb.toString());
        }

        ChatUtil.addChatMessage(EnumChatFormatting.GREEN + ChatUtil.bar());
    }

}
