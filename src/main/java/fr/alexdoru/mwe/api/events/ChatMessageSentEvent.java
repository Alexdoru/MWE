package fr.alexdoru.mwe.api.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when the player is sending a chat message.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public final class ChatMessageSentEvent extends Event {

    /**
     * The chat message sent
     */
    @NotNull
    public final String message;

    public ChatMessageSentEvent(@NotNull String message) {
        this.message = message;
    }

}
