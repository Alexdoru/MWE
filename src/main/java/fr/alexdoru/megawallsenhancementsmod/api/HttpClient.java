package fr.alexdoru.megawallsenhancementsmod.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;

public class HttpClient {

	private URL url;
	private String urlstr;

	public HttpClient(String urlstr) {

		this.urlstr = urlstr;

		try {
			this.url=new URL(urlstr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	public String getrawresponse() throws ApiException {

		BufferedReader reader;
		String line;
		StringBuffer responsecontent = new StringBuffer();

		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			int status = connection.getResponseCode();

			if(status == 200) { //connection successfull

				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				while((line = reader.readLine()) != null  ) {
					responsecontent.append(line);					
				}
				reader.close();
				return responsecontent.toString();

			} else if(status == 429 && this.urlstr.contains("api.hypixel.net")) {
				throw new ApiException("Exceeding amount of requests per minute allowed by Hypixel");
			} else if(status == 403 && this.urlstr.contains("api.hypixel.net")) {
				throw new ApiException("Invalid API key");
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException("An error occured while contacting the Api");
		}

		return null;

	}
	
}
