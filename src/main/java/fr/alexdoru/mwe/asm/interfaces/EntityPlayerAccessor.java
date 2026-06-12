package fr.alexdoru.mwe.asm.interfaces;

import fr.alexdoru.mwe.api.IPlayerInfoAccessor;
import fr.alexdoru.mwe.api.enums.MWClass;
import fr.alexdoru.mwe.hackerdetector.data.PlayerDataSamples;

public interface EntityPlayerAccessor extends IPlayerInfoAccessor {

    PlayerDataSamples getPlayerDataSamples();

    void setPlayerTeamColor(char color);

    void setMWClass(MWClass mwclass);

}
