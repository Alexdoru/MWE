package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.ScorePlayerTeam;

@SuppressWarnings("unused")
public class NetHandlerPlayClientHook_TeamsListener {

    public static void handleTeamPacket(S3EPacketTeams packetIn, ScorePlayerTeam scoreplayerteam) {
        if (packetIn.getAction() == 2) {
            scoreplayerteam.getMembershipCollection().forEach(NameUtil::onScoreboardPacket);
        }
    }

}
