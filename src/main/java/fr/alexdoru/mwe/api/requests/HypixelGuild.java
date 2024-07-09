package fr.alexdoru.mwe.api.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.api.HttpClient;
import fr.alexdoru.mwe.api.exceptions.ApiException;
import fr.alexdoru.mwe.utils.JsonUtil;
import net.minecraft.util.EnumChatFormatting;

public class HypixelGuild {

    private String guildName;
    private String formattedGuildTag;

    public HypixelGuild(String uuid) throws ApiException {
        final HttpClient httpClient = new HttpClient("https://api.hypixel.net/guild?player=" + uuid);
        final JsonObject obj = httpClient.getJsonResponse();
        final JsonObject guildData = JsonUtil.getJsonObject(obj, "guild");
        if (guildData == null) {
            return;
        }
        this.guildName = JsonUtil.getString(guildData, "name");
        final String tag = JsonUtil.getString(guildData, "tag");
        final EnumChatFormatting tagColor = EnumChatFormatting.getValueByName(JsonUtil.getString(guildData, "tagColor"));
        this.formattedGuildTag = " " + (tag == null ? "" : (tagColor == null ? "[" + tag + "]" : tagColor + "[" + tag + "]"));
    }

    public String getGuildName() {
        return guildName;
    }

    public String getFormattedGuildTag() {
        return formattedGuildTag;
    }

}
