package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WDR;
import net.minecraft.util.IChatComponent;

import java.util.HashMap;
import java.util.UUID;

/**
 * An instance of this field is stored in networkplayerinfo.gameprofile and entityplayer.gameprofile
 * and acts as a cache to store the infos about a player needed for the mod
 */
public class MWPlayerData {

    private static final HashMap<UUID, PlayerData> dataCache = new HashMap<>();

    public static void clearData() {
        dataCache.clear();
    }

    public static void put(UUID uuid, PlayerData data) {
        dataCache.put(uuid, data);
    }

    public static PlayerData get(UUID uuid) {
        return dataCache.get(uuid);
    }

    public static void remove(UUID uuid) {
        dataCache.remove(uuid);
    }

    public static class PlayerData {

        public WDR wdr;
        public IChatComponent extraPrefix;
        public String squadname;
        public IChatComponent displayName;
        public String originalP4Tag;
        public String P5Tag;
        public char teamColor;
        public MWClass mwClass;

        public PlayerData(UUID id, WDR wdr, IChatComponent extraPrefix, String squadname, IChatComponent displayNameIn, String originalP4Tag, String P5Tag, char teamColor, MWClass mwClass) {
            this.wdr = wdr;
            this.extraPrefix = extraPrefix;
            this.squadname = squadname;
            this.displayName = displayNameIn;
            this.originalP4Tag = originalP4Tag;
            this.P5Tag = P5Tag;
            this.teamColor = teamColor;
            this.mwClass = mwClass;
            dataCache.put(id, this);
        }

        public void setData(WDR wdr, IChatComponent extraPrefix, String squadname, IChatComponent displayNameIn, String originalP4Tag, String P5Tag, char teamColor, MWClass mwClass) {
            this.wdr = wdr;
            this.extraPrefix = extraPrefix;
            this.squadname = squadname;
            this.displayName = displayNameIn;
            this.originalP4Tag = originalP4Tag;
            this.P5Tag = P5Tag;
            this.teamColor = teamColor;
            this.mwClass = mwClass;
        }

    }

}
