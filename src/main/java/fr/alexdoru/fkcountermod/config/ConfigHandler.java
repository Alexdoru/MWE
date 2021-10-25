package fr.alexdoru.fkcountermod.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConfigHandler {

	private File configFile;
	private JsonObject configJson;

	public ConfigHandler(File file) {
		configFile = file;
	}

	public void loadConfig() {
		if(configFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(configFile));
				StringBuilder builder = new StringBuilder();
				String line;
				while((line = br.readLine()) != null) {
					builder.append(line);
				}
				configJson = new JsonParser().parse(builder.toString()).getAsJsonObject();
				br.close();

				for(ConfigSetting setting : ConfigSetting.values()) {
					if(configJson.has(setting.getTitle())) {
						setting.setValue(configJson.get(setting.getTitle()).getAsBoolean());
					}

					if(configJson.has(setting.getTitle() + "_POS")) {
						JsonArray posArray = configJson.getAsJsonArray(setting.getTitle() + "_POS");
						setting.getData().setScreenPos(posArray.get(0).getAsDouble(), posArray.get(1).getAsDouble());
					}
				}
			} catch (Exception e) {
				System.out.println("[FKCounter] Failed to read config!");
			}
		} else {
			saveConfig();
		}
	}

	public void saveConfig() {
		configJson = new JsonObject();
		try {
			configFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(configFile));
			for(ConfigSetting setting : ConfigSetting.values()) {
				configJson.addProperty(setting.getTitle(), setting.getValue());
				if(setting.getData() != null) {
					JsonArray posArray = new JsonArray();
					posArray.add(new GsonBuilder().create().toJsonTree(setting.getData().getScreenPos().getRelativeX()));
					posArray.add(new GsonBuilder().create().toJsonTree(setting.getData().getScreenPos().getRelativeY()));
					configJson.add(setting.getTitle() + "_POS", posArray);
				}
			}
			bw.write(configJson.toString());
			bw.close();
		} catch (Exception e) {
			System.out.println("[FKCounter] Failed to save config!");
		}
	}

}
