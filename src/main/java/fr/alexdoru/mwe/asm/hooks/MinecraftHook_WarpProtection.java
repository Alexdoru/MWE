package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class MinecraftHook_WarpProtection {

    private static final TimerUtil warpTimer = new TimerUtil(5000L);

    public static boolean shouldCancelRightClick(ItemStack itemStack) {
        if (MWEConfig.warpProtection && itemStack != null && itemStack.getItem() == Items.paper) {
            final NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                final NBTTagCompound displayTag = tagCompound.getCompoundTag("display");
                if (displayTag.hasKey("Name", 8)) {
                    final String itemName = displayTag.getString("Name");
                    if (itemName.contains("Play Again")) {
                        if (!SquadHandler.getSquad().isEmpty() && warpTimer.update()) {
                            ChatUtil.addChatMessage(EnumChatFormatting.YELLOW + ChatUtil.bar() + "\n"
                                    + EnumChatFormatting.YELLOW + "You have players in your Squad, click again to warp" + "\n"
                                    + EnumChatFormatting.YELLOW + ChatUtil.bar());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
