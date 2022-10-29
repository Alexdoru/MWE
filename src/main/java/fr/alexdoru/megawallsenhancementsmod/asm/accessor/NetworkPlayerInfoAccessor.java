package fr.alexdoru.megawallsenhancementsmod.asm.accessor;

import net.minecraft.util.IChatComponent;

public interface NetworkPlayerInfoAccessor {
    void setPlayerFinalkills(int playerFinalkills);
    void setCustomDisplayname(IChatComponent customDisplaynameIn);
}