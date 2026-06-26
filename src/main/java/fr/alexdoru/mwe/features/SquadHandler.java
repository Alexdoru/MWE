package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.events.SquadEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.scoreboard.ScoreboardUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SquadHandler {

    private static final HashMap<String, String> squadmap = new HashMap<>();

    @SubscribeEvent
    public void onNameFormat(NameFormat event) {
        final String squadname = squadmap.get(event.username);
        if (squadname != null) {
            event.displayname = MWEConfig.coloredSquadmates ? MWEConfig.squadmateColor + squadname : squadname;
        }
    }

    public static void addSelf() {
        if (squadmap.isEmpty()) {
            addPlayer(Minecraft.getMinecraft().thePlayer.getName());
            if (!MWEConfig.hypixelNick.isEmpty()) {
                addPlayer(MWEConfig.hypixelNick, MWEConfig.nickHider ? EnumChatFormatting.ITALIC + Minecraft.getMinecraft().thePlayer.getName() + EnumChatFormatting.RESET : MWEConfig.hypixelNick);
            }
        }
    }

    public static void addPlayer(String playername) {
        addPlayer(playername, playername);
    }

    public static void addPlayer(String playername, String friendlyName) {
        final String prevSquadname = squadmap.get(playername);
        squadmap.put(playername, friendlyName);
        NameFormatter.updatePlayerDataAndEntityData(playername);
        if (prevSquadname == null) {
            MinecraftForge.EVENT_BUS.post(new SquadEvent(SquadEvent.Type.ADDED, playername));
        } else if (!prevSquadname.equals(friendlyName)) {
            MinecraftForge.EVENT_BUS.post(new SquadEvent(SquadEvent.Type.NAME_CHANGED, playername));
        }
    }

    public static boolean removePlayer(String playername) {
        final boolean success = squadmap.remove(playername) != null;
        if (success) {
            NameFormatter.updatePlayerDataAndEntityData(playername);
            MinecraftForge.EVENT_BUS.post(new SquadEvent(SquadEvent.Type.REMOVED, playername));
        }
        return success;
    }

    public static void clearSquad() {
        new ArrayList<>(squadmap.keySet()).forEach(SquadHandler::removePlayer);
    }

    /**
     * Returns an unmodifiable view of the squad map
     */
    public static Map<String, String> getSquad() {
        return Collections.unmodifiableMap(squadmap);
    }

    /**
     * Returns the input name if the player is not in the squad
     * Returns the alias if the player is in the squad
     */
    @NotNull
    public static String getSquadname(@NotNull String playername) {
        final String squadname = squadmap.get(playername);
        if (squadname == null) {
            return playername;
        }
        return squadname;
    }

    @Nullable
    public static String getSquadnameUnsafe(@NotNull String playername) {
        return squadmap.get(playername);
    }

    public static boolean isSquadmate(String playername) {
        return squadmap.containsKey(playername);
    }

    /**
     * At the start of any game it checks the scoreboard for teamates and adds them to the team
     * if you have the same teamates it keeps the nicks you gave them
     */
    public static void formSquad() {

        if (!ScoreboardTracker.isMWEnvironement()) {
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
        final String myCustomNick = MWEConfig.hypixelNick.isEmpty() ? null : squadmap.get(MWEConfig.hypixelNick);

        clearSquad();
        newsquad.forEach(SquadHandler::addPlayer);

        if (myCustomName != null) {
            addPlayer(myName, myCustomName);
        }

        if (myCustomNick != null) {
            addPlayer(MWEConfig.hypixelNick, myCustomNick);
        }

        if (!squadmap.isEmpty()) {
            if (myCustomName == null) {
                addPlayer(myName);
            }
            if (myCustomNick == null && !MWEConfig.hypixelNick.isEmpty()) {
                addPlayer(MWEConfig.hypixelNick, MWEConfig.nickHider ? EnumChatFormatting.ITALIC + myName + EnumChatFormatting.RESET : MWEConfig.hypixelNick);
            }
        }

    }

}
