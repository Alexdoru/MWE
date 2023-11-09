package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
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

    private static boolean areItemStacksSemiEquals(ItemStack stackA, ItemStack stackB) {
        return stackA.stackSize == stackB.stackSize && stackA.getItem() == stackB.getItem() && Objects.equals(stackA.getTagCompound(), stackB.getTagCompound());
    }

}
