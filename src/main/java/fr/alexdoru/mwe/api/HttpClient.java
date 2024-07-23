package fr.alexdoru.mwe.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.mwe.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.api.exceptions.ApiException;
import fr.alexdoru.mwe.api.exceptions.RateLimitException;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    public static JsonArray getAsJsonArray(String url) throws ApiException {
        final JsonElement jsonElement = new JsonParser().parse(HttpClient.get(url));
        if (jsonElement == null || !jsonElement.isJsonArray()) {
            throw new ApiException("Cannot parse Api response to Json Object");
        }
        return jsonElement.getAsJsonArray();
    }

    public static JsonObject getAsJsonObject(String url) throws ApiException {
        final JsonElement jsonElement = new JsonParser().parse(HttpClient.get(url));
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            throw new ApiException("Cannot parse Api response to Json Object");
        }
        return jsonElement.getAsJsonObject();
    }

    public static String get(String url) throws ApiException {

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

            final StringBuilder sb = new StringBuilder();
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            final String s = sb.toString();
            if (s.isEmpty()) {
                throw new ApiException("Response is Empty!");
            }
            return s;

        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException("An error occured while contacting the Api");
        }

    }

    private static String stripLastElementOfUrl(String url) {
        final String[] split = url.split("/");
        return split[split.length - 1];
    }

}
