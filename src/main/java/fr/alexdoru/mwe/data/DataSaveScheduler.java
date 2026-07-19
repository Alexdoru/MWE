package fr.alexdoru.mwe.data;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class DataSaveScheduler {

    private static final int SAVE_INTERVAL = 20 * 60 * 10; // 10 mins
    private int counter;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.counter++;
            if (this.counter > SAVE_INTERVAL) {
                this.counter = 0;
                AliasDataManager.saveIfDirty();
                WdrDataManager.saveIfDirty();
            }
        }
    }

}
