package fr.alexdoru.nocheatersmod.events;

import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReportQueue {

    public static final ReportQueue INSTANCE = new ReportQueue();
    private static final int TIME_BETWEEN_REPORTS = 40 * 20; // ticks
    private final Minecraft mc = Minecraft.getMinecraft();
    private int counter;
    private final List<String> reportQueue = new ArrayList<>();
    private static final Random random = new Random();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        if (counter <= 0 && !reportQueue.isEmpty() && mc.thePlayer != null) {
            String playername = reportQueue.remove(0);
            mc.thePlayer.sendChatMessage("/wdr " + playername + " cheating");
            counter = (int) (TIME_BETWEEN_REPORTS + (10d * random.nextGaussian() / 6d));
        }

        if (isReportQueueInactive()) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        counter--;

    }

    public void addPlayerToQueue(String playername) {
        if (isReportQueueInactive()) {
            MinecraftForge.EVENT_BUS.register(this);
            counter = 0;
        }
        reportQueue.add(playername);
    }

    public void addPlayerToQueue(String playername, int tickDelay) {
        if (isReportQueueInactive()) {
            MinecraftForge.EVENT_BUS.register(this);
            counter = tickDelay;
        }
        reportQueue.add(playername);
        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GRAY + "Sending report command with a delay..."));
    }

    private boolean isReportQueueInactive() {
        return counter <= 0 && reportQueue.isEmpty();
    }

    /**
     * Sends reports with minimum of 3sec after the msg, on average 15sec after the message
     * After 27sec 99% of reports are sent
     */
    public void addPlayerToQueueRandom(String reportedPlayer) {
        final double average = 12d * 20d;
        final double sigma = average / 3d;
        addPlayerToQueue(reportedPlayer, (int) (60d + Math.abs(sigma * random.nextGaussian() + average)));
    }

    /**
     * Handles the auto report feature
     *
     * @return true if it sends a report
     */
    public boolean sendAutoReport(long datenow, String playerName, WDR wdr) {
        if (wdr.canBeAutoreported(datenow)) {
            wdr.timestamp = datenow;
            addPlayerToQueue(playerName);
            return true;
        }
        return false;
    }

}
