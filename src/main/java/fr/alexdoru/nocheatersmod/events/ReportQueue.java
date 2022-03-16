package fr.alexdoru.nocheatersmod.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class ReportQueue {

    public static final ReportQueue INSTANCE = new ReportQueue();
    private final Minecraft mc = Minecraft.getMinecraft();
    private int counter;
    private final List<String> reportQueue = new ArrayList<>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        if (counter <= 0 && !reportQueue.isEmpty() && mc.thePlayer != null) {
            String playername = reportQueue.remove(0);
            mc.thePlayer.sendChatMessage("/wdr " + playername + " cheating");
            counter = 500;
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
    }

    private boolean isReportQueueInactive() {
        return counter <= 0 && reportQueue.isEmpty();
    }

}
