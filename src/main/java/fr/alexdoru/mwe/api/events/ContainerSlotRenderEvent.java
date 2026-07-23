package fr.alexdoru.mwe.api.events;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired before rendering a Slot inside a GuiContainer.
 * This event is cancellable, cancelling this event will cause it to not render the ItemStack for the Slot.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public final class ContainerSlotRenderEvent extends Event {

    @NotNull
    public final GuiContainer guiContainer;
    @NotNull
    public final Slot slot;
    @Nullable
    public final ItemStack itemStack;

    public ContainerSlotRenderEvent(@NotNull GuiContainer guiContainer, @NotNull Slot slot, @Nullable ItemStack itemStack) {
        this.guiContainer = guiContainer;
        this.slot = slot;
        this.itemStack = itemStack;
    }

}
