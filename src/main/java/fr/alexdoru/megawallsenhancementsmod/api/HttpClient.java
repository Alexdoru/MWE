package fr.alexdoru.megawallsenhancementsmod.api;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    private String rawResponse;

    public HttpClient(String url) throws ApiException {

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.addRequestProperty("User-Agent", "Alexdoru's Mega Walls Enhancements Mod");

            final int status = connection.getResponseCode();

            if (status == 200) { //connection successfull

                final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                final StringBuilder responsecontent = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responsecontent.append(line);
                }
                reader.close();
                this.rawResponse = responsecontent.toString();

            } else if (status == 429 && url.contains("api.hypixel.net")) {
                throw new ApiException("Exceeding amount of requests per minute allowed by Hypixel");
            } else if (status == 403 && url.contains("api.hypixel.net")) {
                throw new ApiException("Invalid API key");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException("An error occured while contacting the Api");
        }

    }

    public String getRawResponse() {
        return this.rawResponse;
    }

}
