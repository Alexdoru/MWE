package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemSword;

@SuppressWarnings("unused")
public class MinecraftHook_DropProtection {

    private static long lastSlotChangeFromSwordSlot;

    public static boolean shouldDropItem(Minecraft mc) {
        return !ConfigHandler.safeInventory || (System.currentTimeMillis() >= lastSlotChangeFromSwordSlot + 100 && !isHoldingSword(mc.thePlayer));
    }

    public static void updateCurrentSlot(Minecraft mc) {
        if (ConfigHandler.safeInventory && isHoldingSword(mc.thePlayer)) {
            lastSlotChangeFromSwordSlot = System.currentTimeMillis();
        }
    }

    private static boolean isHoldingSword(EntityPlayerSP player) {
        return player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

}
