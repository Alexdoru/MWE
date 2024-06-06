package fr.alexdoru.megawallsenhancementsmod.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.RateLimitException;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    private final JsonObject jsonResponse;
    private final JsonArray jsonArray;

    public HttpClient(String url) throws ApiException {

        try {

            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.addRequestProperty("User-Agent", "Alexdoru's Mega Walls Enhancements Mod");
            final boolean isHypixelApi = url.contains("api.hypixel.net");
            if (isHypixelApi) {
                connection.addRequestProperty("Api-Key", HypixelApiKeyUtil.getApiKey());
            }
            final int status = connection.getResponseCode();

            if (status != 200) {
                if (isHypixelApi) {
                    if (status == 400) {
                        throw new ApiException("Missing one or more fields");
                    } else if (status == 403) {
                        Minecraft.getMinecraft().addScheduledTask(() -> {
                            ConfigHandler.APIKey = "";
                            ConfigHandler.saveConfig();
                            return null;
                        });
                        throw new ApiException("Invalid API key");
                    } else if (status == 404) {
                        throw new ApiException("Page not found");
                    } else if (status == 422) {
                        throw new ApiException("Malformed UUID");
                    } else if (status == 429) {
                        throw new RateLimitException("Exceeding amount of requests per minute allowed by Hypixel");
                    } else if (status == 503) {
                        throw new ApiException("Leaderboard data has not yet been populated");
                    }
                } else if (url.contains("api.mojang.com")) {
                    if (status == 204 || status == 404) {
                        throw new ApiException(ChatUtil.inexistantMinecraftNameMsg(stripLastElementOfUrl(url)));
                    } else if (status == 400) {
                        throw new ApiException(ChatUtil.invalidMinecraftNameMsg(stripLastElementOfUrl(url)));
                    }
                }
                throw new ApiException("Http error code : " + status);
            }

            final StringBuilder responseContent = new StringBuilder();
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
            }

            if (url.contains("api.github.com")) {
                final JsonElement element = new JsonParser().parse(responseContent.toString());
                if (element != null && element.isJsonArray()) {
                    this.jsonResponse = null;
                    this.jsonArray = element.getAsJsonArray();
                    return;
                }
            }

            final JsonObject obj = new JsonParser().parse(responseContent.toString()).getAsJsonObject();

            if (obj == null) {
                throw new ApiException("Cannot parse Api response to Json");
            }

            // I think this will never run because a code != 200 will be received
            if (!url.contains("api.mojang.com") && !url.contains("laby.net/api")) {
                if (!JsonUtil.getBoolean(obj, "success")) {
                    final String msg = JsonUtil.getString(obj, "cause");
                    if (msg == null) {
                        throw new ApiException("Request to api unsuccessful");
                    } else {
                        throw new ApiException(msg);
                    }
                }
            }

            this.jsonResponse = obj;
            this.jsonArray = null;

        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException("An error occured while contacting the Api");
        }

    }

    public JsonObject getJsonResponse() {
        return jsonResponse;
    }

    public JsonArray getJsonArray() {
        return jsonArray;
    }

    private String stripLastElementOfUrl(String url) {
        final String[] split = url.split("/");
        return split[split.length - 1];
    }

}
