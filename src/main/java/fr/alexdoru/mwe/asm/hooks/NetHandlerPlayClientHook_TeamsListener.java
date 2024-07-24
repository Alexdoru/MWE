package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.utils.NameUtil;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.ScorePlayerTeam;

@SuppressWarnings("unused")
public class NetHandlerPlayClientHook_TeamsListener {

    public static void handleTeamPacket(S3EPacketTeams teamPacket, ScorePlayerTeam team) {
        // 0 : created team, might have added players to it
        // 1 : removed team
        // 2 : updated team's attribute, prefix, suffix...
        // 3 : add players to team
        // 4 : remove players from team
        final int action = teamPacket.getAction();
        if (action == 0 || action == 3 || action == 4) {
            for (final String playername : teamPacket.getPlayers()) {
                NameUtil.onTeamPacket(playername);
            }
        } else if (team != null && (action == 1 || action == 2)) {
            for (final String playername : team.getMembershipCollection()) {
                NameUtil.onTeamPacket(playername);
            }
        }
    }

}
