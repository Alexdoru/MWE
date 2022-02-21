package fr.alexdoru.megawallsenhancementsmod.asm.accessor;

import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;

public interface GameProfileAccessor {
    void setMWPlayerData(MWPlayerData mwPlayerData);
    MWPlayerData getMWPlayerData();
}
