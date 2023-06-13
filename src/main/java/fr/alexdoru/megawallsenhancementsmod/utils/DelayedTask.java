package fr.alexdoru.megawallsenhancementsmod.utils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class DelayedTask {

    private final Runnable runnable;
    private int counter;

    public DelayedTask(Runnable task) {
        this.counter = 0;
        this.runnable = task;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public DelayedTask(Runnable task, int ticks) {
        this.counter = ticks;
        this.runnable = task;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.START) {
            if (this.counter <= 0) {
                MinecraftForge.EVENT_BUS.unregister(this);
                this.runnable.run();
            }
            this.counter--;
        }
    }

}
