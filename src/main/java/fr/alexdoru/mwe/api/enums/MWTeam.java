package fr.alexdoru.mwe.api.enums;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public enum MWTeam {

    BLUE,
    GREEN,
    RED,
    YELLOW;

    /**
     * Returns the name of the team with the appropriate chat color
     */
    public String formattedName() {
        return this.getColorPrefix() + this.name();
    }

    /**
     * Returns the chat color code for the team according to the colorblind setting set by the player
     */
    public String getColorPrefix() {
        final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
        if (fkCounter == null) {
            return FinalKillCounter.getDefaultColorPrefix(this);
        }
        return fkCounter.getColorPrefixOfTeam(this);
    }

    public String getName() {
        switch (this) {
            case BLUE:
                return "Blue";
            case GREEN:
                return "Green";
            case RED:
                return "Red";
            case YELLOW:
                return "Yellow";
        }
        throw new IllegalStateException();
    }

    /**
     * Return the MWTeam from the color character of that team, may return null
     */
    @Nullable
    public static MWTeam fromColorChar(char color) {
        final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
        if (fkCounter == null) return null;
        return fkCounter.getTeamFromColor(color);
    }

    /**
     * Returns the MWTeam of a player, might be null
     */
    @Nullable
    public static MWTeam ofPlayer(@NotNull String playername) {
        final WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return null;
        final ScorePlayerTeam team = world.getScoreboard().getPlayersTeam(playername);
        if (team == null) return null;
        return MWTeam.fromColorChar(StringUtil.getLastColorCharOf(team.getColorPrefix()));
    }

    /**
     * Returns the MWTeam of a player, might be null
     */
    @Nullable
    public static MWTeam ofPlayer(@NotNull UUID uuid) {
        final NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler == null) return null;
        final ScorePlayerTeam team = netHandler.getPlayerInfo(uuid).getPlayerTeam();
        if (team == null) return null;
        return MWTeam.fromColorChar(StringUtil.getLastColorCharOf(team.getColorPrefix()));
    }
}
