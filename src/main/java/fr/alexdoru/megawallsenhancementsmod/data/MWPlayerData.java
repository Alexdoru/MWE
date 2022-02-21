package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.nocheatersmod.data.WDR;
import net.minecraft.util.IChatComponent;

public class MWPlayerData {
    // TODO faut update les fields quand on tape un nouveau WDR, update un WDR ou enleve un WDR, pareil pour scangame, squadname etc dans move player de la stalklist
    public WDR wdr;
    public IChatComponent extraPrefix;
    public String squadname;
    public IChatComponent displayName;
    public int playerFinalkills;
    //public boolean isNicked;
    // prestrige 5
    // suffix

    public MWPlayerData(WDR wdr, IChatComponent extraPrefix, String squadname, IChatComponent displayNameIn, int playerFinalkills) {
        this.wdr = wdr;
        this.extraPrefix = extraPrefix;
        this.squadname = squadname;
        this.displayName = displayNameIn;
        this.playerFinalkills = playerFinalkills;
    }
}
