package fr.alexdoru.mwe.asm.hooks.mc.gui;

import fr.alexdoru.mwe.api.events.ContainerSlotRenderEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class GuiContainerHook_CustomSlotRenderer {

    public static boolean drawCustomSlot(boolean original, GuiContainer guiContainer, Slot slot, ItemStack itemStack) {
        if (original) { // original code doesn't render the Slot
            return true;
        }
        return MinecraftForge.EVENT_BUS.post(new ContainerSlotRenderEvent(guiContainer, slot, itemStack));
    }

}
