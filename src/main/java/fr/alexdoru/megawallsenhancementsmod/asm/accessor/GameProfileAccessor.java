package fr.alexdoru.megawallsenhancementsmod.asm.accessor;

import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;

public interface GameProfileAccessor {
    MWPlayerData getMWPlayerData();
    void setMWPlayerData(MWPlayerData mwPlayerData);
}
