package fr.alexdoru.mwe.asm.interfaces;

import net.minecraft.util.IChatComponent;

public interface NetworkPlayerInfoAccessor {

    int getFinalKills();

    void setFinalKills(int playerFinalkills);

    void setCustomDisplayname(IChatComponent customDisplaynameIn);
}