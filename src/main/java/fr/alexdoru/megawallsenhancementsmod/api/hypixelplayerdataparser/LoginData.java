package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.util.EnumChatFormatting;

public class LoginData {

    private long firstLogin;
    private long lastLogin;
    private long lastLogout;
    private String mostRecentGameType;
    private String rank;
    private String displayname;
    private String formattedname;
    private String monthlyPackageRank;

    /**
     * Parses the player's login data
     */
    public LoginData(JsonObject playerData) { // TODO merge this with the constructor above

        if (playerData == null)
            return;

        /*
         * the 8 first characters on the _id entry of the API happen to be the amount of seconds since 1st jan 1970 in hexadecimal
         */
        String _id = JsonUtil.getString(playerData, "_id");

        if (_id != null) {
            String substring = _id.substring(0, 8);
            this.firstLogin = Long.parseLong(substring, 16) * 1000L;
        }

        //this.firstLogin = JsonUtil.getLong(playerData,"firstLogin");
        this.lastLogin = JsonUtil.getLong(playerData, "lastLogin");
        this.lastLogout = JsonUtil.getLong(playerData, "lastLogout");
        this.mostRecentGameType = JsonUtil.getString(playerData, "mostRecentGameType");
        this.rank = JsonUtil.getString(playerData, "rank");
        this.displayname = JsonUtil.getString(playerData, "displayname");
        this.formattedname = ParseFormattedName(playerData);

    }

    /**
     * Be carefull to only call this method once and then store it otherwise it will parse the information everytime
     */
    private String ParseFormattedName(JsonObject playerData) {

        if (playerData == null)
            return "?";

        String prefix = JsonUtil.getString(playerData, "prefix");
        String rankPlusColor = JsonUtil.getString(playerData, "rankPlusColor");
        this.monthlyPackageRank = JsonUtil.getString(playerData, "monthlyPackageRank");
        String packageRank = JsonUtil.getString(playerData, "packageRank");
        String newPackageRank = JsonUtil.getString(playerData, "newPackageRank");
        String monthlyRankColor = JsonUtil.getString(playerData, "monthlyRankColor");

        if (prefix != null) {
            if ("\u00a7[OWNER]".equals(rank)) {
                return EnumChatFormatting.RED + "[OWNER]" + " " + this.displayname;
            }
        }

        if (rank != null) {
            switch (rank) {
                case "HELPER":
                    return EnumChatFormatting.BLUE + "[HELPER]" + " " + this.displayname;
                case "MODERATOR":
                    return EnumChatFormatting.DARK_GREEN + "[MOD]" + " " + this.displayname;
                case "GAME_MASTER":
                    return EnumChatFormatting.DARK_GREEN + "[GM]" + " " + this.displayname;
                case "ADMIN":
                    return EnumChatFormatting.RED + "[ADMIN]" + " " + this.displayname;
                case "YOUTUBER":
                    return EnumChatFormatting.RED + "[" + EnumChatFormatting.WHITE + "YOUTUBE" + EnumChatFormatting.RED + "]" + " " + this.displayname;
                case "NORMAL": // the player used to have a rank in the hypixel staff but got demoted
                    break;
                default:
                    break;
            }
        }

        if (rankPlusColor == null) {
            rankPlusColor = "RED";
        }

        if (monthlyPackageRank != null && monthlyPackageRank.equals("SUPERSTAR")) { // MVP++
            EnumChatFormatting rankPlusPlusColor = monthlyRankColor != null ? EnumChatFormatting.getValueByName(monthlyRankColor) : EnumChatFormatting.GOLD;
            return rankPlusPlusColor + "[MVP" + EnumChatFormatting.getValueByName(rankPlusColor) + "++" + rankPlusPlusColor + "]" + " " + this.displayname;
        }

        if (newPackageRank != null) {
            switch (newPackageRank) {
                case "VIP":
                    return EnumChatFormatting.GREEN + "[VIP]" + " " + this.displayname;
                case "VIP_PLUS":
                    return EnumChatFormatting.GREEN + "[VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GREEN + "]" + " " + this.displayname;
                case "MVP":
                    return EnumChatFormatting.AQUA + "[MVP]" + " " + this.displayname;
                case "MVP_PLUS":
                    return EnumChatFormatting.AQUA + "[MVP" + EnumChatFormatting.getValueByName(rankPlusColor) + "+" + EnumChatFormatting.AQUA + "]" + " " + this.displayname;
                default:
                    return EnumChatFormatting.GRAY + " " + this.displayname;
            }
        }

        if (packageRank != null) {
            switch (packageRank) {
                case "VIP":
                    return EnumChatFormatting.GREEN + "[VIP]" + " " + this.displayname;
                case "VIP_PLUS":
                    return EnumChatFormatting.GREEN + "[VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GREEN + "]" + " " + this.displayname;
                case "MVP":
                    return EnumChatFormatting.AQUA + "[MVP]" + " " + this.displayname;
                case "MVP_PLUS":
                    return EnumChatFormatting.AQUA + "[MVP" + EnumChatFormatting.getValueByName(rankPlusColor) + "+" + EnumChatFormatting.AQUA + "]" + " " + this.displayname;
                default:
                    return EnumChatFormatting.GRAY + " " + this.displayname;
            }
        }

        return EnumChatFormatting.GRAY + " " + this.displayname;
    }

    public String getdisplayname() {
        return this.displayname;
    }

    public String getFormattedName() {
        return this.formattedname;
    }

    public boolean hasNeverJoinedHypixel() {
        return displayname == null;
    }

    public boolean isHidingFromAPI() {
        return !hasNeverJoinedHypixel() && this.mostRecentGameType == null && this.lastLogin == 0 && this.lastLogout == 0;
    }

    public boolean isStaffonHypixel() { // for moderators, admins etc
        return rank != null && lastLogout == 0 && lastLogin == 0;
    }

    public boolean isMVPPlusPlus() {
        return monthlyPackageRank != null && monthlyPackageRank.equals("SUPERSTAR");
    }

    public long getFirstLogin() {
        return this.firstLogin;
    }

    public long getLastLogin() {
        return this.lastLogin;
    }

    public long getLastLogout() {
        return this.lastLogout;
    }

    public boolean isOnline() {
        return this.lastLogin > this.lastLogout;
    }

    public String getMostRecentGameType() {

        if (this.mostRecentGameType == null)
            return "?";

        switch (this.mostRecentGameType) {
            case "QUAKECRAFT":
                return "Quake";
            case "WALLS":
                return "Walls";
            case "PAINTBALL":
                return "Paintball";
            case "SURVIVAL_GAMES":
                return "Blitz Survival Games";
            case "TNTGAMES":
                return "TNT Games";
            case "VAMPIREZ":
                return "VampireZ";
            case "WALLS3":
                return "Mega Walls";
            case "ARCADE":
                return "Arcade";
            case "ARENA":
                return "Arena";
            case "UHC":
                return "UHC Champions";
            case "MCGO":
                return "Cops and Crims";
            case "BATTLEGROUND":
                return "Warlords";
            case "SUPER_SMASH":
                return "Smash Heroes";
            case "GINGERBREAD":
                return "Turbo Kart Racers";
            case "HOUSING":
                return "Housing";
            case "SKYWARS":
                return "SkyWars";
            case "TRUE_COMBAT":
                return "Crazy Walls";
            case "SPEED_UHC":
                return "Speed UHC";
            case "SKYCLASH":
                return "SkyClash";
            case "LEGACY":
                return "Classic Games";
            case "PROTOTYPE":
                return "Prototype";
            case "BEDWARS":
                return "Bed Wars";
            case "MURDER_MYSTERY":
                return "Murder Mystery";
            case "BUILD_BATTLE":
                return "Build Battle";
            case "DUELS":
                return "Duels";
            case "SKYBLOCK":
                return "SkyBlock";
            case "PIT":
                return "Pit";
            case "REPLAY":
                return "Replay";
            case "SMP":
                return "SMP";
            default:
                return this.mostRecentGameType;

        }

    }

}
