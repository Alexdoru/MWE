package fr.alexdoru.megawallsenhancementsmod.asm.accessors;

import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;

public interface EntityPlayerAccessor {
    String getPrestige4Tag();
    void setPrestige4Tag(String prestige4tag);
    String getPrestige5Tag();
    void setPrestige5Tag(String prestige5tag);
    PlayerDataSamples getPlayerDataSamples();
    char getPlayerTeamColor();
    void setPlayerTeamColor(char color);
    int getPlayerTeamColorInt();
    void setPlayerTeamColorInt(int color);
    MWClass getMWClass();
    void setMWClass(MWClass mwclass);
}
