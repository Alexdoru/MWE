package fr.alexdoru.fkcountermod.utils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class DelayedTask {

    private final Runnable run;
    private int counter;

    public DelayedTask(Runnable run, int ticks) {
        counter = ticks;
        this.run = run;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != Phase.START) return;

        if (counter <= 0) {
            MinecraftForge.EVENT_BUS.unregister(this);
            run.run();
        }

        counter--;

    }

}
