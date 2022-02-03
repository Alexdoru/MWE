package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemSword;

public class MinecraftHook {

    public static void dropOneItem(EntityPlayerSP thePlayer) {
        if (thePlayer.getCurrentEquippedItem() == null || !(thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword)) {
            thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
        }
    }

}
