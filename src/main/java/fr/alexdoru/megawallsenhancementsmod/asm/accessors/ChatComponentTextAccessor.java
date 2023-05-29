package fr.alexdoru.megawallsenhancementsmod.asm.accessors;

import net.minecraft.util.ResourceLocation;

public interface ChatComponentTextAccessor {
    ResourceLocation getSkin();
    void setSkin(ResourceLocation skin);
}
