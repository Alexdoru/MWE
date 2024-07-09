package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.features.LeatherArmorManager;
import fr.alexdoru.mwe.hackerdetector.HackerDetector;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.Objects;

@SuppressWarnings("unused")
public class EntityOtherPlayerMPHook {

    public static void setPositionAndRotation(EntityOtherPlayerMP player, double x, double y, double z, float yaw, float pitch) {
        if (ConfigHandler.hackerDetector) {
            HackerDetector.addScheduledTask(
                    () -> ((EntityPlayerAccessor) player).getPlayerDataSamples().setPositionAndRotation(x, y, z, yaw, pitch)
            );
        }
    }

    public static boolean shouldCancelEquipmentUpdate(EntityOtherPlayerMP player, int slotIn, ItemStack stack) {
        if (slotIn == 0) {
            final ItemStack currentStack = player.inventory.mainInventory[player.inventory.currentItem];
            if (currentStack != null && stack != null && currentStack.getItem() instanceof ItemSword && areItemStacksSemiEquals(currentStack, stack)) {
                currentStack.setItemDamage(stack.getItemDamage());
                return true;
            }
        }
        return false;
    }

    public static ItemStack getLeatherArmor(EntityOtherPlayerMP player, int slotIn, ItemStack stack) {
        return LeatherArmorManager.replaceIronArmor(player, slotIn, stack);
    }

    private static boolean areItemStacksSemiEquals(ItemStack stackA, ItemStack stackB) {
        return stackA.stackSize == stackB.stackSize && stackA.getItem() == stackB.getItem() && Objects.equals(stackA.getTagCompound(), stackB.getTagCompound());
    }

}
