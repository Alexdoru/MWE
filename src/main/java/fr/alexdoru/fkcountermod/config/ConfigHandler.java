package fr.alexdoru.fkcountermod.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class ConfigHandler {

    private final File configFile;
    private JsonObject configJson;

    public ConfigHandler(File file) {
        configFile = file;
    }

    public void loadConfig() {
        if (configFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(configFile));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                configJson = new JsonParser().parse(builder.toString()).getAsJsonObject();
                br.close();

                for (EnumFKConfigSetting setting : EnumFKConfigSetting.values()) {
                    if (configJson.has(setting.getTitle())) {
                        setting.setValue(configJson.get(setting.getTitle()).getAsBoolean());
                    }

                    if (configJson.has(setting.getTitle() + "_POS")) {
                        JsonArray posArray = configJson.getAsJsonArray(setting.getTitle() + "_POS");
                        setting.getHUDPosition().setRelative(posArray.get(0).getAsDouble(), posArray.get(1).getAsDouble());
                    }
                }
            } catch (Exception e) {
                System.out.println("[FKCounter] Failed to read config!");
            }
        } else {
            saveConfig();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveConfig() {
        configJson = new JsonObject();
        try {
            configFile.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(configFile));
            for (EnumFKConfigSetting setting : EnumFKConfigSetting.values()) {
                configJson.addProperty(setting.getTitle(), setting.getValue());
                if (setting.getHUDPosition() != null) {
                    JsonArray posArray = new JsonArray();
                    posArray.add(new GsonBuilder().create().toJsonTree(setting.getHUDPosition().getRelativeX()));
                    posArray.add(new GsonBuilder().create().toJsonTree(setting.getHUDPosition().getRelativeY()));
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
