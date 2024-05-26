package fr.alexdoru.megawallsenhancementsmod.features;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SquadHandler {

    private static final HashMap<String, String> squadmap = new HashMap<>();

    @SubscribeEvent
    public void onNameFormat(NameFormat event) {
        final String squadname = squadmap.get(event.username);
        if (squadname != null) {
            event.displayname = ConfigHandler.pinkSquadmates ? EnumChatFormatting.LIGHT_PURPLE + squadname : squadname;
        }
    }

    public static void addPlayer(String playername) {
        addPlayer(playername, playername);
    }

    public static void addPlayer(String playername, String friendlyName) {
        squadmap.put(playername, friendlyName);
        NameUtil.updateMWPlayerDataAndEntityData(playername, true);
    }

    public static boolean removePlayer(String playername) {
        final boolean success = squadmap.remove(playername) != null;
        if (success) {
            NameUtil.updateMWPlayerDataAndEntityData(playername, true);
        }
        return success;
    }

    public static void clearSquad() {
        final List<String> playerlist = new ArrayList<>();
        squadmap.forEach((key, value) -> playerlist.add(key));
        squadmap.clear();
        for (final String playername : playerlist) {
            NameUtil.updateMWPlayerDataAndEntityData(playername, true);
        }
    }

    public static HashMap<String, String> getSquad() {
        return squadmap;
    }

    /**
     * Returns the input name if the player is not in the squad
     * Returns the alias if the player is in the squad
     */
    @Nonnull
    public static String getSquadname(@Nonnull String playername) {
        final String squadname = squadmap.get(playername);
        if (squadname == null) {
            return playername;
        }
        return squadname;
    }

    /**
     * At the start of any game it checks the scoreboard for teamates and adds them to the team
     * if you have the same teamates it keeps the nicks you gave them
     */
    public static void formSquad() {

        if (!ScoreboardTracker.isMWEnvironement) {
            return;
        }

        final List<String> scoresRaw = ScoreboardUtils.getUnformattedSidebarText();
        if (scoresRaw.isEmpty()) {
            return;
        }

        final HashMap<String, String> newsquad = new HashMap<>();
        boolean foundTeammates = false;
        for (final String line : scoresRaw) {
            if (foundTeammates) {
                if (line.contains("www.hypixel.net") || line.contains("HAPPY HOUR!") || line.isEmpty()) {
                    break;
                }
                final String nameonscoreboard = line.replace(" ", "");
                final String squadmate = squadmap.get(nameonscoreboard);
                // the player was already in the squad before, reuse the same name transformation
                if (squadmate == null) {
                    newsquad.put(nameonscoreboard, nameonscoreboard);
                } else {
                    newsquad.put(nameonscoreboard, squadmate);
                }
            }
            if (line.contains("Teammates:")) {
                foundTeammates = true;
            }
        }

        final String myName = Minecraft.getMinecraft().thePlayer.getName();
        final String myCustomName = squadmap.get(myName);
        final String myCustomNick = ConfigHandler.hypixelNick.isEmpty() ? null : squadmap.get(ConfigHandler.hypixelNick);

        clearSquad();
        newsquad.forEach(SquadHandler::addPlayer);

        if (myCustomName != null) {
            addPlayer(myName, myCustomName);
        }

        if (myCustomNick != null) {
            addPlayer(ConfigHandler.hypixelNick, myCustomNick);
        }

        if (!squadmap.isEmpty()) {
            if (myCustomName == null) {
                addPlayer(myName);
            }
            if (myCustomNick == null && !ConfigHandler.hypixelNick.isEmpty()) {
                addPlayer(ConfigHandler.hypixelNick, ConfigHandler.nickHider ? EnumChatFormatting.ITALIC + myName + EnumChatFormatting.RESET : ConfigHandler.hypixelNick);
            }
        }

    }

}
