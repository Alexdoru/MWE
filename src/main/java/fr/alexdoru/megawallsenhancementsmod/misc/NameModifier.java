package fr.alexdoru.megawallsenhancementsmod.misc;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class NameModifier {

    public static void transformDisplayName(String playername) {
        //if(!NoCheatersMod.areIconsToggled()) {return;}
        NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(playername);
        if (networkPlayerInfo != null) networkPlayerInfo.setDisplayName(getTransformedDisplayName(networkPlayerInfo));
    }

    public static IChatComponent getTransformedDisplayName(NetworkPlayerInfo networkPlayerInfo) {
        return getTransformedDisplayName(networkPlayerInfo.getGameProfile(), networkPlayerInfo.getDisplayName());
    }

    /*
     * replaces the names of squadmates
     * adds a tag to squadmates
     * adds a tag to reported players
     * unscrambles the tablist
     */
    public static IChatComponent getTransformedDisplayName(GameProfile gameProfile, IChatComponent displayName) {

        // public void handleTeams(S3EPacketTeams packetIn) in NetHandlerPlayClient
        // add a hook to transform the names
        // 0 : create a team
        // 1 : remove a team
        // 2 : Set team displayname/prefix/suffix and/or whether friendly fire is enabled
        // 3 : add players to team
        // 4 : remove players from team

        //***************************

        // TODO transform les player name sur les paquets de team
        // ca laisse des croix devant les pseudo quand qqu deco reco en mw
        // ca conserve pas les couleurs des teams en miniwalls
        // en miniwalls the display name est null pendant la game

        String username = gameProfile.getName();
        String uuid = gameProfile.getId().toString().replace("-", "");
        WDR wdr = WdredPlayers.getWdredMap().get(uuid);
        String squadname = SquadEvent.getSquad().get(username);
        String extraprefix = "";
        boolean issquadmate = false;
        boolean needtochange = false;

        if (wdr == null) {
            wdr = WdredPlayers.getWdredMap().get(username);
            if (wdr != null) {
                uuid = username;
            }
        }

        //if(NoCheatersMod.areIconsToggled()) {

        if (squadname != null) { // squad

            extraprefix = SquadEvent.getprefix();
            issquadmate = true;
            needtochange = true;

        } else if (wdr != null) { // nocheaters

            if (!wdr.isOnlyStalking()) {

                if (wdr.hacks.contains("bhop")) {
                    extraprefix += NoCheatersEvents.prefix_bhop;
                } else {
                    extraprefix += NoCheatersEvents.prefix;
                }

            }

            needtochange = true;

        } else { //scangame

            IChatComponent imsg = CommandScanGame.getScanmap().get(uuid);

            if (imsg != null && !imsg.equals(CommandScanGame.nomatch)) {
                extraprefix += CommandScanGame.prefix;
                needtochange = true;
            }

        }
        //}

        if (displayName != null) {

            String formattedname = displayName.getFormattedText().replace(SquadEvent.getprefix(), "").replace(NoCheatersEvents.prefix_bhop, "").replace(NoCheatersEvents.prefix, "").replace(CommandScanGame.prefix, "");

            if (needtochange) {
                return new ChatComponentText(extraprefix).appendSibling(new ChatComponentText((issquadmate ? formattedname.replace(username, squadname).replace("\u00a7k", "") : formattedname.replace("\u00a7k", ""))));
            } else {
                return new ChatComponentText(formattedname);
            }

        }

        ScorePlayerTeam team = Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(username);

        if (team != null) {

            String teamprefix = team.getColorPrefix();
            if (teamprefix.contains("\u00a7k")) {
                needtochange = true;
            }
            if (needtochange) {
                return new ChatComponentText(extraprefix + teamprefix.replace("\u00a7k", "").replace("O", "") + (issquadmate ? squadname : username) + team.getColorSuffix());
            }
        }

        return null;

    }

}
