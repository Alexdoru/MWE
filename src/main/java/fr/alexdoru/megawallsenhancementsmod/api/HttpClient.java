package fr.alexdoru.megawallsenhancementsmod.api;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    private final String urlstr;
    private URL url;

    public HttpClient(String urlstr) {

        this.urlstr = urlstr;

        try {
            this.url = new URL(urlstr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public String getrawresponse() throws ApiException {

        final BufferedReader reader;
        String line;
        final StringBuilder responsecontent = new StringBuilder();

        try {
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.addRequestProperty("User-Agent", "Alexdoru's Mega Walls Enhancements Mod");

            final int status = connection.getResponseCode();

            if (status == 200) { //connection successfull

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

                while ((line = reader.readLine()) != null) {
                    responsecontent.append(line);
                }
                reader.close();
                return responsecontent.toString();

            } else if (status == 429 && this.urlstr.contains("api.hypixel.net")) {
                throw new ApiException("Exceeding amount of requests per minute allowed by Hypixel");
            } else if (status == 403 && this.urlstr.contains("api.hypixel.net")) {
                throw new ApiException("Invalid API key");
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException("An error occured while contacting the Api");
        }

        return null;

    }

}
