package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LabyModNameHistory {

    private static List<String> nameLines;
    private static UUID uuid;

    public static synchronized List<String> getNameHistory(UUID uuidIn) throws ApiException {

        if (uuid != null && uuid.equals(uuidIn)) {
            return nameLines;
        }

        final HttpClient httpClient = new HttpClient("https://laby.net/api/user/" + uuidIn.toString() + "/get-snippet");
        final JsonObject jsonResponse = httpClient.getJsonResponse();
        final JsonArray nameHistory = JsonUtil.getJsonArray(jsonResponse, "name_history");
        if (nameHistory == null || nameHistory.size() == 0) {
            throw new ApiException("Name history data is empty");
        }

        final List<String> lines = new ArrayList<>();

        for (int i = 0; i < nameHistory.size(); i++) {
            final JsonObject obj = nameHistory.get(i).getAsJsonObject();
            final String name = JsonUtil.getString(obj, "username");
            final String time = JsonUtil.getString(obj, "changed_at");
            final boolean accurate = JsonUtil.getBoolean(obj, "accurate");
            final boolean hidden = JsonUtil.getBoolean(obj, "hidden");
            final StringBuilder sb = new StringBuilder();
            sb.append(EnumChatFormatting.GOLD);
            if (hidden) {
                sb.append(EnumChatFormatting.OBFUSCATED).append("--------");
            } else {
                sb.append(name);
            }
            if (time != null) {
                sb.append(EnumChatFormatting.RESET).append(EnumChatFormatting.GRAY).append(" ");
                if (i == nameHistory.size() - 1) {
                    sb.append("since ");
                }
                if (!accurate) {
                    sb.append("~");
                }
                sb.append(formatTime(time));
            }
            lines.add(sb.toString());
        }

        Collections.reverse(lines);
        nameLines = lines;
        uuid = uuidIn;
        return nameLines;

    }

    private static String formatTime(String time) {
        if (time == null) return null;
        final int i = time.indexOf("T");
        if (i <= 0) return time;
        final String substring = time.substring(0, i);
        final String[] split = substring.split("-");
        if (split.length == 3) {
            return split[2] + "/" + split[1] + "/" + split[0];
        }
        return substring;
    }

}
