package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class MinecraftHook {

    private static final Minecraft mc = Minecraft.getMinecraft();
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

    public static void onSettingChange(boolean debugBoundingBoxIn, String settingName) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Debug]: " + EnumChatFormatting.WHITE + settingName + (debugBoundingBoxIn ? EnumChatFormatting.GREEN + ": On" : EnumChatFormatting.RED + ": Off")));
        }
    }

    private static boolean checkIfHoldingSword(EntityPlayerSP thePlayer) {
        return thePlayer.getCurrentEquippedItem() != null && thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

}
