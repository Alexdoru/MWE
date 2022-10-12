package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public class MinecraftHook {

    private static long lastSlotChangeFromSwordSlot;
    private static final TimerUtil warpTimer = new TimerUtil(5000L);

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
        if (FKCounterMod.isInMwGame && itemStack != null && itemStack.getItem() == Items.paper) {
            final NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                final NBTTagCompound displayTag = tagCompound.getCompoundTag("display");
                if (displayTag.hasKey("Name", 8)) {
                    final String itemName = displayTag.getString("Name");
                    if (itemName.contains("Play Again")) {
                        if (!SquadEvent.getSquad().isEmpty() && warpTimer.update()) {
                            ChatUtil.addChatMessage(EnumChatFormatting.YELLOW + ChatUtil.bar());
                            ChatUtil.addChatMessage(EnumChatFormatting.YELLOW + "You have players in your Squad, click again to warp");
                            ChatUtil.addChatMessage(EnumChatFormatting.YELLOW + ChatUtil.bar());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
