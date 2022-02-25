package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.nocheatersmod.data.WDR;
import net.minecraft.util.IChatComponent;

import java.util.HashMap;
import java.util.UUID;

/**
 * An instance of this field is stored in networkplayerinfo.gameprofile and entityplayer.gameprofile
 * and acts as a cache to store the infos about a player needed for the mod
 */
public class MWPlayerData {

    public static final HashMap<UUID, MWPlayerData> dataCache = new HashMap<>();

    public WDR wdr;
    public IChatComponent extraPrefix;
    public String squadname;
    public IChatComponent displayName;
    public int playerFinalkills;

    public MWPlayerData(UUID id, WDR wdr, IChatComponent extraPrefix, String squadname, IChatComponent displayNameIn, int playerFinalkills) {
        this.wdr = wdr;
        this.extraPrefix = extraPrefix;
        this.squadname = squadname;
        this.displayName = displayNameIn;
        this.playerFinalkills = playerFinalkills;
        dataCache.put(id, this);
    }

    public void setData(UUID id, WDR wdr, IChatComponent extraPrefix, String squadname, IChatComponent displayNameIn, int playerFinalkills) {
        this.wdr = wdr;
        this.extraPrefix = extraPrefix;
        this.squadname = squadname;
        this.displayName = displayNameIn;
        this.playerFinalkills = playerFinalkills;
        dataCache.put(id, this);
    }

}
