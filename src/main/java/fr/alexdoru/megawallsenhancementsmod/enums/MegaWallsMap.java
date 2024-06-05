package fr.alexdoru.megawallsenhancementsmod.enums;

import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

import static fr.alexdoru.megawallsenhancementsmod.enums.MegaWallsMap.TeamColor.*;

public enum MegaWallsMap {

    ANCHORED(RED, YELLOW, BLUE, GREEN, -53, 54, 54, -53, 65),
    AZTEC(YELLOW, BLUE, RED, GREEN, 820, -902, 927, -1009, 36),
    BARRAGE(YELLOW, BLUE, RED, GREEN, -189, -723, -87, -825, 38),
    BLOODMOON(YELLOW, BLUE, RED, GREEN, -53, 54, 54, -53, 65),
    CITY(GREEN, RED, BLUE, YELLOW, -189, -725, -89, -825, 34),
    DRAGONKEEP(YELLOW, BLUE, RED, GREEN, -190, -724, -88, -826, 38),
    DUSKFORGE(BLUE, YELLOW, RED, GREEN, -53, 54, 54, -53, 65),
    DYNASTY(GREEN, RED, BLUE, YELLOW, -54, 54, 53, -53, 57),
    EBONVEIL(BLUE, RED, GREEN, YELLOW, -53, 54, 54, -53, 71),
    EGYPT(RED, YELLOW, BLUE, GREEN, -190, -724, -88, -826, 38),
    FORSAKEN(BLUE, RED, GREEN, YELLOW, -190, -724, -88, -826, 38),
    GOLDFORGE(YELLOW, RED, GREEN, BLUE, -53, 54, 54, -53, 57),
    IMPERIAL(YELLOW, BLUE, RED, GREEN, -53, 54, 54, -53, 65),
    KINGDOM(GREEN, RED, BLUE, YELLOW, -190, -724, -89, -825, 38),
    KIROBIRO(BLUE, RED, GREEN, YELLOW, -52, 55, 55, -52, 57),
    LAUNCHSITE(BLUE, YELLOW, GREEN, RED, -50, 51, 51, -50, 42),
    MADPIXEL(YELLOW, BLUE, RED, GREEN, -51, 51, 51, -51, 29),
    OASIS(GREEN, BLUE, YELLOW, RED, -50, 50, 52, -52, 60),
    SERENITY(BLUE, RED, GREEN, YELLOW, -44, 51, 56, -49, 41),
    SERPENTS(GREEN, RED, BLUE, YELLOW, -189, -722, -88, -823, 49),
    SHADOWSTONE(BLUE, YELLOW, RED, GREEN, -53, 54, 54, -53, 65),
    SHANTYBAY(BLUE, RED, GREEN, YELLOW, -53, 54, 54, -53, 71),
    SOLACE(BLUE, YELLOW, GREEN, RED, -131, -696, -24, -803, 57),
    STEPPES(YELLOW, GREEN, RED, BLUE, -838, -234, -736, -336, 60),
    STONEHOLD(YELLOW, BLUE, RED, GREEN, -53, 54, 54, -53, 65),
    WONDERLAND(GREEN, YELLOW, BLUE, RED, -53, 54, 54, -53, 58);

    private final TeamColor northBase, eastBase, southBase, westBase;
    private final double northLimit, eastLimit, southLimit, westLimit;
    private final double innerNorthLimit, innerEastLimit, innerSouthLimit, innerWestLimit;
    private final double surfaceLevel;
    private static final Map<String, MegaWallsMap> fromNameMap = new HashMap<>();

    static {
        for (final MegaWallsMap map : MegaWallsMap.values()) {
            fromNameMap.put(map.name().toLowerCase(), map);
        }
    }

    MegaWallsMap(TeamColor northBase, TeamColor eastBase, TeamColor southBase, TeamColor westBase, double northLimit, double eastLimit, double southLimit, double westLimit, double surfaceLevel) {
        this.northBase = northBase;
        this.eastBase = eastBase;
        this.southBase = southBase;
        this.westBase = westBase;
        this.northLimit = northLimit;
        this.eastLimit = eastLimit;
        this.southLimit = southLimit;
        this.westLimit = westLimit;
        this.surfaceLevel = surfaceLevel;
        final int FRONT_BASE_WIDTH = 12;
        this.innerNorthLimit = this.northLimit + FRONT_BASE_WIDTH;
        this.innerEastLimit = this.eastLimit - FRONT_BASE_WIDTH;
        this.innerSouthLimit = this.southLimit - FRONT_BASE_WIDTH;
        this.innerWestLimit = this.westLimit + FRONT_BASE_WIDTH;
    }

    public String getPlayerBaseLocation(EntityPlayer player) {
        //             NORTH -Z
        //   WEST -X             EAST +X
        //             SOUTH +Z
        if (isPlayerAtMiddle(player)) {
            if (isPlayerAtMidMid(player)) {
                return "MIDDLE";
            } else if (ab(player) && player.posX > innerEastLimit && cd(player)) {
                return "MIDDLE(" + eastBase.formattedName() + EnumChatFormatting.RESET + ")";
            } else if (!cd(player) && player.posZ > innerSouthLimit && ab(player)) {
                return "MIDDLE(" + southBase.formattedName() + EnumChatFormatting.RESET + ")";
            } else if (!ab(player) && player.posX < innerWestLimit && !cd(player)) {
                return "MIDDLE(" + westBase.formattedName() + EnumChatFormatting.RESET + ")";
            } else if (cd(player) && player.posZ < innerNorthLimit && !ab(player)) {
                return "MIDDLE(" + northBase.formattedName() + EnumChatFormatting.RESET + ")";
            }
        } else {
            if (player.posZ < northLimit && player.posX > westLimit) {
                return northBase.formattedName();
            } else if (player.posX > eastLimit && player.posZ > northLimit) {
                return eastBase.formattedName();
            } else if (player.posZ > southLimit && player.posX < eastLimit) {
                return southBase.formattedName();
            } else if (player.posX < westLimit && player.posZ < southLimit) {
                return westBase.formattedName();
            }
        }
        return "";
    }

    private boolean isPlayerAtMiddle(EntityPlayer player) {
        return player.posX < eastLimit && player.posX > westLimit && player.posZ < southLimit && player.posZ > northLimit;
    }

    private boolean isPlayerAtMidMid(EntityPlayer player) {
        return player.posX < innerEastLimit && player.posX > innerWestLimit && player.posZ < innerSouthLimit && player.posZ > innerNorthLimit;
    }

    private boolean ab(EntityPlayer player) {
        return player.posX > -player.posZ + eastLimit + northLimit;
    }

    private boolean cd(EntityPlayer player) {
        return player.posX > player.posZ + westLimit - northLimit;
    }

    public static MegaWallsMap fromName(String name) {
        return fromNameMap.get(name.replace(" ", "").toLowerCase());
    }

    enum TeamColor {

        BLUE,
        GREEN,
        RED,
        YELLOW;

        public String formattedName() {
            switch (this) {
                case BLUE:
                    return FinalKillCounter.getColorPrefixFromTeam(3) + "BLUE";
                case GREEN:
                    return FinalKillCounter.getColorPrefixFromTeam(1) + "GREEN";
                case RED:
                    return FinalKillCounter.getColorPrefixFromTeam(0) + "RED";
                case YELLOW:
                    return FinalKillCounter.getColorPrefixFromTeam(2) + "YELLOW";
                default:
                    throw new IllegalStateException(this.name() + " shoudn't exist in this enum");
            }
        }
    }

}
