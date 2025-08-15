package fr.alexdoru.mwe.http.parsers.hypixel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.mwe.enums.GameType;
import fr.alexdoru.mwe.utils.JsonUtil;
import net.minecraft.util.EnumChatFormatting;

import java.util.Map;

public class LoginData {

    private long firstLogin;
    private long lastLogin;
    private long lastLogout;
    private long latestActivityTime;
    private String latestActivity;
    private String mostRecentGameType;
    private String rank;
    private String displayname;
    private String formattedname;
    private String monthlyPackageRank;
    private boolean isNonRanked;

    /**
     * Parses the player's login data
     */
    public LoginData(JsonObject playerData) {

        if (playerData == null) {
            return;
        }

        // the 8 first characters on the _id entry of the API happen to be the amount of seconds since 1st jan 1970 in hexadecimal
        final String _id = JsonUtil.getString(playerData, "_id");

        if (_id != null) {
            final String substring = _id.substring(0, 8);
            this.firstLogin = Long.parseLong(substring, 16) * 1000L;
        }

        //this.firstLogin = JsonUtil.getLong(playerData,"firstLogin");
        this.lastLogin = JsonUtil.getLong(playerData, "lastLogin");
        this.lastLogout = JsonUtil.getLong(playerData, "lastLogout");
        this.mostRecentGameType = JsonUtil.getString(playerData, "mostRecentGameType");
        this.rank = JsonUtil.getString(playerData, "rank");
        this.displayname = JsonUtil.getString(playerData, "displayname");

        final String prefix = JsonUtil.getString(playerData, "prefix");
        final String rankPlusColor = JsonUtil.getString(playerData, "rankPlusColor");
        this.monthlyPackageRank = JsonUtil.getString(playerData, "monthlyPackageRank");
        final String packageRank = JsonUtil.getString(playerData, "packageRank");
        final String newPackageRank = JsonUtil.getString(playerData, "newPackageRank");
        final String monthlyRankColor = JsonUtil.getString(playerData, "monthlyRankColor");

        parseFormattedName(prefix, rankPlusColor, packageRank, newPackageRank, monthlyRankColor);
        this.isNonRanked = this.formattedname.startsWith(EnumChatFormatting.GRAY.toString());

    }

    public void parseLatestActivity(JsonObject playerData) {
        latestActivityTime = JsonUtil.getLong(playerData, "lastClaimedReward");
        latestActivity = "Claimed Daily Reward";
        final JsonObject questobj = JsonUtil.getJsonObject(playerData, "quests");
        if (questobj == null) {
            return;
        }
        for (final Map.Entry<String, JsonElement> questEntry : questobj.entrySet()) {
            if (questEntry.getValue().isJsonObject()) {
                final JsonObject gameObj = questEntry.getValue().getAsJsonObject();
                final JsonArray completionsArray = JsonUtil.getJsonArray(gameObj, "completions");
                if (completionsArray == null) {
                    continue;
                }
                for (final JsonElement element : completionsArray) {
                    if (element instanceof JsonObject) {
                        final long l = JsonUtil.getLong(element.getAsJsonObject(), "time");
                        if (l > latestActivityTime) {
                            latestActivityTime = l;
                            latestActivity = "Quest " + questEntry.getKey();
                        }
                    }
                }
            }
        }
    }

    private void parseFormattedName(String prefix, String rankPlusColor, String packageRank, String newPackageRank, String monthlyRankColor) {
        if (prefix != null) {
            this.formattedname = prefix + " " + this.displayname;
            return;
        }

        if (rank != null) {
            switch (rank) {
                case "HELPER":
                    this.formattedname = EnumChatFormatting.BLUE + "[HELPER]" + " " + this.displayname;
                    return;
                case "MODERATOR":
                    this.formattedname = EnumChatFormatting.DARK_GREEN + "[MOD]" + " " + this.displayname;
                    return;
                case "GAME_MASTER":
                    this.formattedname = EnumChatFormatting.DARK_GREEN + "[GM]" + " " + this.displayname;
                    return;
                case "ADMIN":
                    this.formattedname = EnumChatFormatting.RED + "[ADMIN]" + " " + this.displayname;
                    return;
                case "YOUTUBER":
                    this.formattedname = EnumChatFormatting.RED + "[" + EnumChatFormatting.WHITE + "YOUTUBE" + EnumChatFormatting.RED + "]" + " " + this.displayname;
                    return;
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
            final EnumChatFormatting rankPlusPlusColor = monthlyRankColor != null ? EnumChatFormatting.getValueByName(monthlyRankColor) : EnumChatFormatting.GOLD;
            this.formattedname = rankPlusPlusColor + "[MVP" + EnumChatFormatting.getValueByName(rankPlusColor) + "++" + rankPlusPlusColor + "]" + " " + this.displayname;
            return;
        }

        if (newPackageRank != null) {
            switch (newPackageRank) {
                case "VIP":
                    this.formattedname = EnumChatFormatting.GREEN + "[VIP]" + " " + this.displayname;
                    return;
                case "VIP_PLUS":
                    this.formattedname = EnumChatFormatting.GREEN + "[VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GREEN + "]" + " " + this.displayname;
                    return;
                case "MVP":
                    this.formattedname = EnumChatFormatting.AQUA + "[MVP]" + " " + this.displayname;
                    return;
                case "MVP_PLUS":
                    this.formattedname = EnumChatFormatting.AQUA + "[MVP" + EnumChatFormatting.getValueByName(rankPlusColor) + "+" + EnumChatFormatting.AQUA + "]" + " " + this.displayname;
                    return;
                default:
                    this.formattedname = EnumChatFormatting.GRAY + " " + this.displayname;
            }
        }

        if (packageRank != null) {
            switch (packageRank) {
                case "VIP":
                    this.formattedname = EnumChatFormatting.GREEN + "[VIP]" + " " + this.displayname;
                    return;
                case "VIP_PLUS":
                    this.formattedname = EnumChatFormatting.GREEN + "[VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GREEN + "]" + " " + this.displayname;
                    return;
                case "MVP":
                    this.formattedname = EnumChatFormatting.AQUA + "[MVP]" + " " + this.displayname;
                case "MVP_PLUS":
                    this.formattedname = EnumChatFormatting.AQUA + "[MVP" + EnumChatFormatting.getValueByName(rankPlusColor) + "+" + EnumChatFormatting.AQUA + "]" + " " + this.displayname;
                    return;
                default:
                    this.formattedname = EnumChatFormatting.GRAY + this.displayname;
                    return;
            }
        }

        this.formattedname = EnumChatFormatting.GRAY + this.displayname;
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
        return rank != null && !"NORMAL".equals(rank) && lastLogout == 0 && lastLogin == 0;
    }

    public boolean isMVPPlusPlus() {
        return "SUPERSTAR".equals(monthlyPackageRank);
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

    public long getLatestActivityTime() {
        return latestActivityTime;
    }

    public String getLatestActivity() {
        return latestActivity;
    }

    public boolean isOnline() {
        return this.lastLogin > this.lastLogout;
    }

    public String getMostRecentGameType() {
        if (this.mostRecentGameType == null) {
            return "?";
        }
        final GameType gameType = GameType.fromId(this.mostRecentGameType);
        return gameType == GameType.UNKNOWN ? this.mostRecentGameType : gameType.toString();
    }

    public boolean isNonRanked() {
        return isNonRanked;
    }

}
