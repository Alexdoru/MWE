package fr.alexdoru.megawallsenhancementsmod.api;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    private final String rawResponse;

    public HttpClient(String url) throws ApiException {

        try {

            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.addRequestProperty("User-Agent", "Alexdoru's Mega Walls Enhancements Mod");
            final int status = connection.getResponseCode();

            if (status != 200) {
                if (url.contains("api.hypixel.net")) {
                    if (status == 429) {
                        throw new ApiException("Exceeding amount of requests per minute allowed by Hypixel");
                    } else if (status == 403) {
                        throw new ApiException("Invalid API key");
                    }
                }
                throw new ApiException("Http error code : " + status);
            }

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            final StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();
            this.rawResponse = responseContent.toString();

        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException("An error occured while contacting the Api");
        }

    }

    public String getRawResponse() {
        return this.rawResponse;
    }

}
