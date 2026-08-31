package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.events.ChatMessageSentEvent;
import fr.alexdoru.mwe.commands.CommandWDR;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ReportSentListener {

    @SubscribeEvent
    public void onChatSent(ChatMessageSentEvent event) {
        if (event.message.toLowerCase().startsWith("/report")) {
            final String[] args = event.message.split(" ");
            if (args.length > 2) {
                final String playername = args[1];
                final String cheat = args[2].toLowerCase();
                if (CommandWDR.cheatsSet.contains(cheat)) {
                    PartyDetection.printBoostingReportAdvice(playername);
                }
            }
        }
    }

}
