package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.features.LeatherArmorManager;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public class EntityOtherPlayerMPHook_LeatherArmor {

    public static ItemStack getLeatherArmor(EntityOtherPlayerMP player, int slotIn, ItemStack stack) {
        return LeatherArmorManager.replaceIronArmor(player, slotIn, stack);
    }

}
