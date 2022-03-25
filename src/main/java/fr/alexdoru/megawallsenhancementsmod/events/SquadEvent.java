package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.fkcountermod.utils.MinecraftUtils;
import fr.alexdoru.fkcountermod.utils.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SquadEvent {

    private static final HashMap<String, String> squadmap = new HashMap<>();
    private static String myNick;
    private static String myFriendlyName;

    @SubscribeEvent
    public void onNameFormat(NameFormat event) {
        String squadname = squadmap.get(event.username);
        if (squadname != null) {
            event.displayname = squadname;
        }
    }

    public static void addMyself(String myNickIn) {
        myNick = myNickIn;
        addPlayer(myNickIn);
    }

    public static void addMyself(String myNickIn, String myFriendlyNameIn) {
        myNick = myNickIn;
        myFriendlyName = myFriendlyNameIn;
        addPlayer(myNickIn, myFriendlyNameIn);
    }

    public static void addPlayer(String playername) {
        addPlayer(playername, playername);
    }

    public static void addPlayer(String playername, String friendlyName) {
        squadmap.put(playername, friendlyName);
        NameUtil.updateGameProfileAndName(playername, true);
    }

    public static boolean removePlayer(String playername) {
        boolean success = squadmap.remove(playername) != null;
        if (success) {
            NameUtil.updateGameProfileAndName(playername, true);
        }
        if (squadmap.isEmpty()) {
            myNick = null;
            myFriendlyName = null;
        }
        return success;
    }

    public static void clearSquad() {
        List<String> playerlist = new ArrayList<>();
        squadmap.forEach((key, value) -> playerlist.add(key));
        squadmap.clear();
        myNick = null;
        myFriendlyName = null;

        for (String playername : playerlist) {
            NameUtil.updateGameProfileAndName(playername, true);
        }

    }

    public static HashMap<String, String> getSquad() {
        return squadmap;
    }

    /**
     * At the start of any game it checks the scoreboard for teamates and adds them to the team
     * if you have the same teamates it keeps the nicks you gave them
     */
    public static void formSquad() {

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null || !MinecraftUtils.isHypixel()) {
            return;
        }

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return;
        }

        boolean isinMW = ScoreboardUtils.getUnformattedSidebarTitle(scoreboard).contains("MEGA WALLS");

        if (!isinMW) {
            return;
        }

        List<String> scoresRaw = ScoreboardUtils.getUnformattedSidebarText();
        boolean found_teammates = false;

        HashMap<String, String> newsquad = new HashMap<>();

        for (String line : scoresRaw) {

            if (found_teammates) {

                if (line.contains("www.hypixel.net") || line.contains("HAPPY HOUR!") || line.equals("")) {
                    break;
                }

                String nameonscoreboard = line.replace(" ", "");
                String squadmate = squadmap.get(nameonscoreboard);
                /*
                 * the player was already in the squad before, reuse the same name transformation
                 */
                if (squadmate == null) {
                    newsquad.put(nameonscoreboard, nameonscoreboard);
                } else {
                    newsquad.put(nameonscoreboard, squadmate);
                }

            }

            if (line.contains("Teammates:")) {
                found_teammates = true;
            }

        }

        squadmap.clear();
        squadmap.putAll(newsquad);

        if (!squadmap.isEmpty()) {

            if (myNick == null) {
                addPlayer(Minecraft.getMinecraft().thePlayer.getName());
            } else {

                if (myFriendlyName == null) {
                    addPlayer(myNick, myNick);
                } else {
                    addPlayer(myNick, myFriendlyName);
                }

            }

        }

    }

}
