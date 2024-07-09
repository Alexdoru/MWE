package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.utils.NameUtil;

@SuppressWarnings("unused")
public class ScoreboardHook {

    public static void removeTeamHook(String playername) {
        NameUtil.onScoreboardPacket(playername);
    }

    public static void addPlayerToTeamHook(String playername) {
        NameUtil.onScoreboardPacket(playername);
    }

    public static void removePlayerFromTeamHook(String playername) {
        NameUtil.onScoreboardPacket(playername);
    }

}
