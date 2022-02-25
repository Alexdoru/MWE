package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.nocheatersmod.data.WDR;
import net.minecraft.util.IChatComponent;

public class MWPlayerData {

    public WDR wdr;
    public IChatComponent extraPrefix;
    public String squadname;
    public IChatComponent displayName;
    public int playerFinalkills;

    public MWPlayerData(WDR wdr, IChatComponent extraPrefix, String squadname, IChatComponent displayNameIn, int playerFinalkills) {
        this.wdr = wdr;
        this.extraPrefix = extraPrefix;
        this.squadname = squadname;
        this.displayName = displayNameIn;
        this.playerFinalkills = playerFinalkills;
    }

    public void setData(WDR wdr, IChatComponent extraPrefix, String squadname, IChatComponent displayNameIn, int playerFinalkills) {
        this.wdr = wdr;
        this.extraPrefix = extraPrefix;
        this.squadname = squadname;
        this.displayName = displayNameIn;
        this.playerFinalkills = playerFinalkills;
    }

}
