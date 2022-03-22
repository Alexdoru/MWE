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

    public void addPlayerToQueueRandom(String reportedPlayer) {
        addPlayerToQueue(reportedPlayer, (int) (20d + Math.abs(100d * random.nextGaussian() + 240d)));
    }

    /**
     * Handles the auto report feature
     *
     * @return true if it sends a report
     */
    public boolean sendAutoReport(long datenow, String playerName, WDR wdr) {
        if (wdr.canBeAutoreported(datenow) && wdr.hasValidCheats()) {
            wdr.timestamp = datenow;
            addPlayerToQueue(playerName);
            return true;
        }
        return false;
    }

}
