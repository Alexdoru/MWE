package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemSword;

public class MinecraftHook {

    private static long lastSlotChangeFromSwordSlot;

    public static void dropOneItem(EntityPlayerSP thePlayer) {
        if (System.currentTimeMillis() < lastSlotChangeFromSwordSlot + 100 || checkIfHoldingSword(thePlayer)) {
            return;
        }
        thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
    }

    public static void updateCurrentSlot(Minecraft mc) {
        if (checkIfHoldingSword(mc.thePlayer)) {
            lastSlotChangeFromSwordSlot = System.currentTimeMillis();
        }
    }

    private static boolean checkIfHoldingSword(EntityPlayerSP thePlayer) {
        return thePlayer.getCurrentEquippedItem() != null && thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

}
