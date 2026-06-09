package fr.alexdoru.mwe.api.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class HackerFlagEvent extends Event {

    /**
     * The player that flagged
     */
    @NotNull
    public final EntityPlayer player;
    @NotNull
    public final String cheatname;
    @NotNull
    public final String flagtype;

    public HackerFlagEvent(@NotNull EntityPlayer player, @NotNull String cheatname, @NotNull String flagtype) {
        this.player = player;
        this.cheatname = cheatname;
        this.flagtype = flagtype;
    }
}
