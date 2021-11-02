package fr.alexdoru.megawallsenhancementsmod.config;

import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//https://github.com/CJMinecraft01/BitOfEverything/blob/6ab40135d1c4f9e97e898925d85a33334b13036e/src/main/java/cjminecraft/bitofeverything/config/BoeConfig.java
public class MWEnConfigHandler {

    public static Configuration config;
    private static final String CATEGORY_MWENh = "MegaWallsEnhancements";
    private static final String CATEGORY_GUI = "GUI";
    private static final String CATEGORY_NOCHEATERS = "NoCheaters";

    /*MWEnhancements config*/
    public static String APIKey;
    public static boolean shortencoinmessage;
    public static boolean reportsuggestions;
    public static boolean hunterStrengthSound;

    /*GUI config*/
    public static boolean show_killcooldownGUI;
    public static final GuiPosition killcooldownHUDPosition = new GuiPosition(0d,0d);
    public static boolean show_ArrowHitGui;
    public static final GuiPosition arrowHitHUDPosition = new GuiPosition(0d,0d);

    /*NoCheaters Config*/
    public static boolean toggleicons;
    public static boolean togglewarnings;
    public static boolean toggleautoreport;
    public static long timeBetweenReports;
    public static long timeAutoReport;

    public static void preinit(File file) {
        config = new Configuration(file);
        syncConfig(true, true, false);
    }

    private static void syncConfig(boolean loadFromConfigFile, boolean readFieldsFromConfig, boolean saveFieldsToConfig) {

        if (loadFromConfigFile) {
            config.load();
        }

        /*Reads the fiels in the config and stores them in the property objects, and defines a default value if the fields doesn't exist*/
        Property pAPIKey = config.get(CATEGORY_MWENh, "APIKey", "", "Your Hypixel API Key");
        Property pShortencoinmessage = config.get(CATEGORY_MWENh, "Shorten coin message", true, "Shorten the coins messages by removing the network booster info");
        Property pReportsuggestions = config.get(CATEGORY_MWENh, "Report suggestion", true, "Give report suggestions in the chat based on messages in shouts");
        Property pHunterStrengthSound = config.get(CATEGORY_MWENh, "Hunter Strength Sound", true, "Plays a sound 10 seconds before getting strength when playing hunter");

        Property pShow_killcooldownGUI = config.get(CATEGORY_GUI, "Show kill cooldown HUD", true, "Displays the cooldown for the /kill command when in MegaWalls");
        Property pXpos_killcooldownGUI = config.get(CATEGORY_GUI, "Xpos kill cooldown HUD", 0d, "The x position of the killcooldown GUI, value ranges from 0 to 1");
        Property pYpos_killcooldownGUI = config.get(CATEGORY_GUI, "Ypos kill cooldown HUD", 0d, "The y position of the killcooldown GUI, value ranges from 0 to 1");
        Property pShow_ArrowHitGui = config.get(CATEGORY_GUI, "Show Arrow Hit HUD", true, "Displays the HP of opponents on arrow hits");
        Property pXpos_ArrowHitGui = config.get(CATEGORY_GUI, "Xpos Arrow Hit HUD", 0.5d, "The x position of the ArrowHitGui, value ranges from 0 to 1");
        Property pYpos_ArrowHitGui = config.get(CATEGORY_GUI, "Ypos Arrow Hit HUD", 9d / 20d, "The y position of the ArrowHitGui, value ranges from 0 to 1");

        Property pToggleicons = config.get(CATEGORY_NOCHEATERS, "Toggle Icons", true, "Display warning symbol on nametags of reported players");
        Property pTogglewarnings = config.get(CATEGORY_NOCHEATERS, "Toggle Warnings", true, "Gives warning messages in chat for reported players");
        Property pToggleautoreport = config.get(CATEGORY_NOCHEATERS, "Toggle Autoreport", false, "Automatically report previously reported players when they are in your lobby");
        Property pTimeBetweenReports = config.get(CATEGORY_NOCHEATERS, "Time between reports", 6, "Time before the mod suggests to report the player again (hours)");
        Property pTimeAutoReport = config.get(CATEGORY_NOCHEATERS, "Time for autoreports", 336, "It won't autoreport players whose last report is older than this (hours)");

        /*Set the Order in which the config entries appear in the config file */
        List<String> propertyOrderMWWENh = new ArrayList<>();
        propertyOrderMWWENh.add(pAPIKey.getName());
        propertyOrderMWWENh.add(pShortencoinmessage.getName());
        propertyOrderMWWENh.add(pReportsuggestions.getName());
        propertyOrderMWWENh.add(pHunterStrengthSound.getName());
        config.setCategoryPropertyOrder(CATEGORY_MWENh, propertyOrderMWWENh);

        List<String> propertyOrderGUI = new ArrayList<>();
        propertyOrderGUI.add(pShow_killcooldownGUI.getName());
        propertyOrderGUI.add(pXpos_killcooldownGUI.getName());
        propertyOrderGUI.add(pYpos_killcooldownGUI.getName());
        propertyOrderGUI.add(pShow_ArrowHitGui.getName());
        propertyOrderGUI.add(pXpos_ArrowHitGui.getName());
        propertyOrderGUI.add(pYpos_ArrowHitGui.getName());
        config.setCategoryPropertyOrder(CATEGORY_GUI, propertyOrderGUI);

        List<String> propertyOrderNOCHEATERS = new ArrayList<>();
        propertyOrderNOCHEATERS.add(pToggleicons.getName());
        propertyOrderNOCHEATERS.add(pTogglewarnings.getName());
        propertyOrderNOCHEATERS.add(pToggleautoreport.getName());
        propertyOrderNOCHEATERS.add(pTimeBetweenReports.getName());
        propertyOrderNOCHEATERS.add(pTimeAutoReport.getName());
        config.setCategoryPropertyOrder(CATEGORY_NOCHEATERS, propertyOrderNOCHEATERS);

        /*sets the fields of this class to the fields in the properties*/
        if (readFieldsFromConfig) {
            APIKey = pAPIKey.getString();
            shortencoinmessage = pShortencoinmessage.getBoolean();
            reportsuggestions = pReportsuggestions.getBoolean();
            hunterStrengthSound = pHunterStrengthSound.getBoolean();

            show_killcooldownGUI = pShow_killcooldownGUI.getBoolean();
            killcooldownHUDPosition.setRelative(pXpos_killcooldownGUI.getDouble(), pYpos_killcooldownGUI.getDouble());
            show_ArrowHitGui = pShow_ArrowHitGui.getBoolean();
            arrowHitHUDPosition.setRelative(pXpos_ArrowHitGui.getDouble(), pYpos_ArrowHitGui.getDouble());

            toggleicons = pToggleicons.getBoolean();
            togglewarnings = pTogglewarnings.getBoolean();
            toggleautoreport = pToggleautoreport.getBoolean();
            timeBetweenReports = 3600L * 1000L * ((long) pTimeBetweenReports.getInt());
            timeAutoReport = 3600L * 1000L * ((long) pTimeAutoReport.getInt());
        }

        if (saveFieldsToConfig) {
            pAPIKey.set(APIKey);
            pShortencoinmessage.set(shortencoinmessage);
            pReportsuggestions.set(reportsuggestions);
            pHunterStrengthSound.set(hunterStrengthSound);

            pShow_killcooldownGUI.set(show_killcooldownGUI);
            double[] killcooldownGUIarray = killcooldownHUDPosition.getRelativePosition();
            pXpos_killcooldownGUI.set(killcooldownGUIarray[0]);
            pYpos_killcooldownGUI.set(killcooldownGUIarray[1]);
            pShow_ArrowHitGui.set(show_ArrowHitGui);
            double[] ArrowHitGuiarray = arrowHitHUDPosition.getRelativePosition();
            pXpos_ArrowHitGui.set(ArrowHitGuiarray[0]);
            pYpos_ArrowHitGui.set(ArrowHitGuiarray[1]);

            pToggleicons.set(toggleicons);
            pTogglewarnings.set(togglewarnings);
            pToggleautoreport.set(toggleautoreport);
            pTimeBetweenReports.set((int) timeBetweenReports / (3600 * 1000));
            pTimeAutoReport.set((int) timeAutoReport / (3600 * 1000));
        }

        /*automatically saves the values to the config file if any of the values change*/
        if (config.hasChanged()) {
            config.save();
        }

    }

    /*
     * call this method to save to the config file after a modifications was made to the fields of this class
     */
    public static void saveConfig() {
        syncConfig(false, false, true);
    }

}
