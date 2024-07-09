package fr.alexdoru.mwe.asm.accessors;

import net.minecraft.util.IChatComponent;

public interface NetworkPlayerInfoAccessor {
    void setFinalKills(int playerFinalkills);
    int getFinalKills();
    void setCustomDisplayname(IChatComponent customDisplaynameIn);
}