package fr.alexdoru.mwe.asm.hooks.mc.render;

import fr.alexdoru.mwe.MWE;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class LayerArmorBaseHook_LeatherArmor {

    public static ItemStack getArmorToRender(ItemStack stack, EntityLivingBase entity) {
        return MWE.INSTANCE().getLeatherArmorManager().getFakeArmorForLocalPlayerRender(entity, stack);
    }

}
