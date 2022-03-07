package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.util.EnumChatFormatting;

public class HypixelGuild {

    private String guildName;
    private String formattedGuildTag;

    public HypixelGuild(String uuid, String apikey) throws ApiException {

        HttpClient httpclient = new HttpClient("https://api.hypixel.net/guild?key=" + apikey + "&player=" + uuid);
        String rawresponse = httpclient.getrawresponse();

        if (rawresponse == null) {
            throw new ApiException("No response from Hypixel's Api");
        }

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(rawresponse).getAsJsonObject();

        if (obj == null) {
            throw new ApiException("Cannot parse response from Hypixel's Api");
        }

        if (!obj.get("success").getAsBoolean()) {

            String msg = JsonUtil.getString(obj, "cause");

            if (msg == null) {
                throw new ApiException("Failed to retreive data from Hypixel's Api for this guild");
            } else {
                throw new ApiException(msg);
            }

        }

        JsonElement guildDataElem = obj.get("guild");

        if (guildDataElem == null || !guildDataElem.isJsonObject()) {
            return;
        }

        JsonObject guildData = guildDataElem.getAsJsonObject();

        if (guildData == null) {
            return;
        }

        this.guildName = JsonUtil.getString(guildData, "name");
        String tag = JsonUtil.getString(guildData, "tag");
        EnumChatFormatting tagColor = EnumChatFormatting.getValueByName(JsonUtil.getString(guildData, "tagColor"));
        this.formattedGuildTag = " " + (tag == null ? "" : (tagColor == null ? "[" + tag + "]" : tagColor + "[" + tag + "]"));

    }

    public String getGuildName() {
        return guildName;
    }

    public String getFormattedGuildTag() {
        return formattedGuildTag;
    }

}
