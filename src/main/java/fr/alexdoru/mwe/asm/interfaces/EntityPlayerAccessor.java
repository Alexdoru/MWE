package fr.alexdoru.mwe.asm.interfaces;

import fr.alexdoru.mwe.enums.MWClass;
import fr.alexdoru.mwe.hackerdetector.data.PlayerDataSamples;

public interface EntityPlayerAccessor {
    PlayerDataSamples getPlayerDataSamples();
    char getPlayerTeamColor();
    void setPlayerTeamColor(char color);
    int getPlayerTeamColorInt();
    void setPlayerTeamColorInt(int color);
    MWClass getMWClass();
    void setMWClass(MWClass mwclass);
}
