package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public class MinecraftHook {

    private static long lastSlotChangeFromSwordSlot;

    public static void dropOneItem(EntityPlayerSP thePlayer) {
        if (ConfigHandler.safeInventory && (System.currentTimeMillis() < lastSlotChangeFromSwordSlot + 100 || checkIfHoldingSword(thePlayer))) {
            return;
        }
        thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
    }

    public static void updateCurrentSlot(Minecraft mc) {
        if (ConfigHandler.safeInventory && checkIfHoldingSword(mc.thePlayer)) {
            lastSlotChangeFromSwordSlot = System.currentTimeMillis();
        }
    }

    public static void onSettingChange(Minecraft mc, boolean settingIn, String settingName) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Debug]: " + EnumChatFormatting.WHITE + settingName + ":" + (settingIn ? EnumChatFormatting.GREEN + " On" : EnumChatFormatting.RED + " Off")));
        }
        if ("Hitboxes".equals(settingName)) {
            ConfigHandler.isDebugHitboxOn = settingIn;
            ConfigHandler.saveConfig();
        }
    }

    public static void onReloadChunks(Minecraft mc) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Debug]: " + EnumChatFormatting.WHITE + "Reloading all chunks"));
        }
    }

    private static boolean checkIfHoldingSword(EntityPlayerSP thePlayer) {
        return thePlayer.getCurrentEquippedItem() != null && thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    public static boolean shouldCancelRightClick(ItemStack itemStack) {
        return false;
    }

}
