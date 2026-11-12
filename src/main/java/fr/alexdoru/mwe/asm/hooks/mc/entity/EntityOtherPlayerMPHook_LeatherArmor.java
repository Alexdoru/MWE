package fr.alexdoru.mwe.asm.hooks.mc.entity;

import fr.alexdoru.mwe.MWE;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;

public class EntityOtherPlayerMPHook_LeatherArmor {

    public static ItemStack getLeatherArmor(EntityOtherPlayerMP player, int slotIn, ItemStack stack) {
        return MWE.INSTANCE().getLeatherArmorManager().replaceIronArmor(player, slotIn, stack);
    }

}
