package fr.alexdoru.mwe.asm.hooks;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.Objects;

@SuppressWarnings("unused")
public class EntityOtherPlayerMPHook_FixAutoblockBypass {

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
