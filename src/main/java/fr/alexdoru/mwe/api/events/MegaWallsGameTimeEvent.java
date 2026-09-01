package fr.alexdoru.mwe.api.events;

import fr.alexdoru.mwe.utils.DateUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * The event is fired when the time on the scoreboard changes during Mega Walls games.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public final class MegaWallsGameTimeEvent extends Event {

    /**
     * Time since game started in seconds
     */
    public final int time;

    public MegaWallsGameTimeEvent(int time) {
        this.time = time;
    }

    public String getFormatted() {
        return DateUtil.formatTime(this.time);
    }

}
