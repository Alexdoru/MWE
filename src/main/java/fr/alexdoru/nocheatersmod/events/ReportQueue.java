package fr.alexdoru.nocheatersmod.events;

import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;

public class ReportQueue {

    public static final ReportQueue INSTANCE = new ReportQueue();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int TIME_BETWEEN_REPORTS_MAX = 4 * 60 * 20;
    private static final int TIME_BETWEEN_REPORTS_MIN = 2 * 60 * 20;

    private int counter;
    private final List<ReportInQueue> queueList = new ArrayList<>();
    private final List<Long> timestampsLastReports = new ArrayList<>();
    private final List<StringLong> playersReportedThisGame = new ArrayList<>();
    private final Random random = new Random();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        if (counter <= 0 && !queueList.isEmpty() && mc.thePlayer != null) {
            String playername = queueList.remove(queueList.size() - 1).reportedPlayer;
            mc.thePlayer.sendChatMessage("/wdr " + playername + " cheating");
            final int i = TIME_BETWEEN_REPORTS_MAX - 12 * 20 * (queueList.isEmpty() ? 0 : queueList.size() - 1);
            counter = (int) ((10d * random.nextGaussian() / 6d) + Math.max(i, TIME_BETWEEN_REPORTS_MIN));
            addReportTimestamp(false);
        }

        if (isReportQueueInactive()) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        counter--;

    }

    public void addPlayerToQueue(String playername, boolean printDelayMsg) {
        if (isReportQueueInactive()) {
            MinecraftForge.EVENT_BUS.register(this);
            counter = 0;
        } else if (printDelayMsg) {
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Sending report in a moment..."));
        }
        queueList.add(new ReportInQueue(null, playername));
    }

    /**
     * Called from the auto-report suggestions
     * Returns true if it adds the players to the report queue
     */
    public boolean addPlayerToQueueRandom(String suggestionSender, String reportedPlayer) {
        if (canReportPlayerThisGame(reportedPlayer)) {
            addPlayerToQueue(suggestionSender, reportedPlayer, (int) (100d + Math.abs(100d * random.nextGaussian() + 240d)));
            return true;
        }
        return false;
    }

    private void addPlayerToQueue(String suggestionSender, String playername, int tickDelay) {
        if (isReportQueueInactive()) {
            MinecraftForge.EVENT_BUS.register(this);
            counter = tickDelay;
        }
        queueList.add(new ReportInQueue(suggestionSender, playername));
    }

    private boolean isReportQueueInactive() {
        return counter <= 0 && queueList.isEmpty();
    }

    /**
     * Handles the auto report feature
     *
     * @return true if it sends a report
     */
    public boolean sendAutoReport(long datenow, String playerName, WDR wdr) {
        if (wdr.canBeAutoreported(datenow) && wdr.hasValidCheats()) {
            wdr.timestamp = datenow;
            if (canReportPlayerThisGame(playerName)) {
                addPlayerToQueue(playerName, false);
            }
            return true;
        }
        return false;
    }

    public void clearReportsSentBy(String playername) {
        StringBuilder stringBuilder = new StringBuilder();
        final Iterator<ReportInQueue> iterator = queueList.iterator();
        while (iterator.hasNext()) {
            final ReportInQueue reportInQueue = iterator.next();
            if (reportInQueue.messageSender != null && reportInQueue.messageSender.equals(playername)) {
                stringBuilder.append(" ").append(reportInQueue.reportedPlayer);
                iterator.remove();
            }
        }
        final String msg = stringBuilder.toString();
        if (!msg.equals("")) {
            addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "Removed reports targeting :" + EnumChatFormatting.GOLD + msg));
        }
    }

    public void clearSuggestionsInReportQueue() {
        StringBuilder stringBuilder = new StringBuilder();
        final Iterator<ReportInQueue> iterator = queueList.iterator();
        while (iterator.hasNext()) {
            final ReportInQueue reportInQueue = iterator.next();
            if (reportInQueue.messageSender != null) {
                stringBuilder.append(" ").append(reportInQueue.reportedPlayer);
                iterator.remove();
            }
        }
        final String msg = stringBuilder.toString();
        if (!msg.equals("")) {
            addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "Removed reports targeting :" + EnumChatFormatting.GOLD + msg));
        }
    }

    public void clearReportsFor(String reportedPlayer) {
        StringBuilder stringBuilder = new StringBuilder();
        final Iterator<ReportInQueue> iterator = queueList.iterator();
        while (iterator.hasNext()) {
            final ReportInQueue reportInQueue = iterator.next();
            if (reportInQueue.reportedPlayer != null && reportInQueue.reportedPlayer.equals(reportedPlayer)) {
                stringBuilder.append(" ").append(reportInQueue.reportedPlayer);
                iterator.remove();
            }
        }
        final String msg = stringBuilder.toString();
        if (!msg.equals("")) {
            addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "Removed all reports targeting :" + EnumChatFormatting.GOLD + msg));
        }
    }

    /**
     * Called everytime a report is sent to check that the player doesn't send too many reports in a short time
     */
    public void addReportTimestamp(boolean manualReport) {
        long l = System.currentTimeMillis();
        timestampsLastReports.removeIf(o -> (o + 2 * 60 * 1000L < l));
        if (manualReport && timestampsLastReports.size() > 4) {
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Don't report too many players at once or Hypixel will ignore your reports thinking you are a bot trying to flood their system"));
        }
        timestampsLastReports.add(l);
    }

    public void clearPlayersReportedThisGame() {
        playersReportedThisGame.clear();
    }

    private boolean canReportPlayerThisGame(String playername) {
        long timestamp = System.currentTimeMillis();
        playersReportedThisGame.removeIf(o -> (o.timestamp + 40L * 60L * 1000L < timestamp));
        for (StringLong stringLong : playersReportedThisGame) {
            if (stringLong.message != null && stringLong.message.equals(playername)) {
                return false;
            }
        }
        playersReportedThisGame.add(new StringLong(timestamp, playername));
        return true;
    }

}

class ReportInQueue {

    public String messageSender;
    public String reportedPlayer;

    public ReportInQueue(String messageSender, String reportedPlayer) {
        this.messageSender = messageSender;
        this.reportedPlayer = reportedPlayer;
    }

}
