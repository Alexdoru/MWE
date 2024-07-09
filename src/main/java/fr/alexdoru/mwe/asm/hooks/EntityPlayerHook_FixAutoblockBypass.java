package fr.alexdoru.mwe.asm.hooks;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.Objects;

@SuppressWarnings("unused")
public class EntityPlayerHook_FixAutoblockBypass {

    public static boolean areItemStackSemiEquals(ItemStack currentStack, ItemStack itemInUse, EntityPlayer player) {
        if (player.getClass() != EntityOtherPlayerMP.class) {
            return currentStack == itemInUse;
        }
        if (currentStack == itemInUse) {
            return true;
        } else {
            if (currentStack != null && itemInUse != null && currentStack.getItem() instanceof ItemSword && areItemStacksSemiEquals(currentStack, itemInUse)) {
                itemInUse.setItemDamage(currentStack.getItemDamage());
                player.inventory.mainInventory[player.inventory.currentItem] = itemInUse;
                return true;
            }
        }
        return false;
    }

    private static boolean areItemStacksSemiEquals(ItemStack stackA, ItemStack stackB) {
        return stackA.stackSize == stackB.stackSize && stackA.getItem() == stackB.getItem() && Objects.equals(stackA.getTagCompound(), stackB.getTagCompound());
    }

}
