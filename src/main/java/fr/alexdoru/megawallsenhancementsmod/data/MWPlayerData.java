package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.nocheatersmod.data.WDR;

public class MWPlayerData {
    // TODO faut update les fields quand on tape un nouveau WDR, update un WDR ou enleve un WDR, pareil pour scangame, squadname etc
    public WDR wdr;
    public String extraPrefix;
    public String squadname;
    public int playerFinalkills;
    //public boolean isNicked;
    // scangame
    // player finals
    // prestrige 5
    // prefix
    // suffix

    public MWPlayerData(WDR wdr, String extraPrefix, String squadname, int playerFinalkills) {
        this.wdr = wdr;
        this.extraPrefix = extraPrefix;
        this.squadname = squadname;
        this.playerFinalkills = playerFinalkills;
    }
}
