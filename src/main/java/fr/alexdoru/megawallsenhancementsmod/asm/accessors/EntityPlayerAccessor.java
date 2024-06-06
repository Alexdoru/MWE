package fr.alexdoru.megawallsenhancementsmod.asm.accessors;

import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;

public interface EntityPlayerAccessor {
    PlayerDataSamples getPlayerDataSamples();
    char getPlayerTeamColor();
    void setPlayerTeamColor(char color);
    int getPlayerTeamColorInt();
    void setPlayerTeamColorInt(int color);
    MWClass getMWClass();
    void setMWClass(MWClass mwclass);
}
