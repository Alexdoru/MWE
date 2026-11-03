package fr.alexdoru.mwe.asm.interfaces;

import net.minecraft.entity.EntityLivingBase;

public interface EntityArrowAccessor {

    boolean mwe$isInGround();

    EntityLivingBase mwe$PinnedEntity();

    boolean mwe$isPinnedToEntity();

}
