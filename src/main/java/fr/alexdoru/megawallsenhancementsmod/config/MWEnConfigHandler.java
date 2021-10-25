package fr.alexdoru.megawallsenhancementsmod.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

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

	/*GUI config*/
	public static boolean show_killcooldownGUI;
	public static double xpos_killcooldownGUI;
	public static double ypos_killcooldownGUI;	
	public static boolean show_ArrowHitGui;
	public static double xpos_ArrowHitGui;
	public static double ypos_ArrowHitGui;

	/*NoCheaters Config*/
	public static boolean toggleicons;
	public static boolean togglewarnings;
	public static boolean toggleautoreport;
	public static long timeBetweenReports;
	public static long timeAutoReport;

	public static void preinit(File file) {
		config = new Configuration(file);
		syncFromFiles();
	}

	private static void syncConfig(boolean loadFromConfigFile, boolean readFieldsFromConfig) {

		if(loadFromConfigFile) {
			config.load();
		}

		Property propertyAPIKey = config.get(CATEGORY_MWENh, "APIKey", "", "Your Hypixel API Key");
		Property propertyshortencoinmessage = config.get(CATEGORY_MWENh, "Shorten coin message", true, "Shorten the coins messages by removing the network booster info");
		Property propertyreportsuggestions = config.get(CATEGORY_MWENh, "Report suggestion", true, "Give report suggestions in the chat based on messages in shouts");

		Property propertyshow_killcooldownGUI = config.get(CATEGORY_GUI, "Show kill cooldown GUI", true, "Displays the cooldown for the /kill command when in MegaWalls");
		Property propertyxpos_killcooldownGUI = config.get(CATEGORY_GUI, "Xpos killcooldown GUI", 0d, "The x position of the killcooldown GUI, value ranges from 0 to 1");
		Property propertyypos_killcooldownGUI = config.get(CATEGORY_GUI, "Ypos killcooldown GUI", 0d, "The y position of the killcooldown GUI, value ranges from 0 to 1");
		Property propertyshow_ArrowHitGui = config.get(CATEGORY_GUI, "Show arrow hit GUI", true, "Displays the HP of opponents on arrow hits");
		Property propertyxpos_ArrowHitGui = config.get(CATEGORY_GUI, "Xpos ArrowHit Gui", 0.5d, "The x position of the ArrowHitGui, value ranges from 0 to 1");
		Property propertyypos_ArrowHitGui = config.get(CATEGORY_GUI, "Ypos ArrowHit Gui", 9d/20d, "The y position of the ArrowHitGui, value ranges from 0 to 1");

		Property propertytoggleicons = config.get(CATEGORY_NOCHEATERS, "Toggle Icons", true, "Display warning symbol on nametags of reported players");
		Property propertytogglewarnings = config.get(CATEGORY_NOCHEATERS, "Toggle Warnings", true, "Gives warning messages in chat for reported players");
		Property propertytoggleautoreport = config.get(CATEGORY_NOCHEATERS, "Toggle Autoreport", false, "Automatically report previously reported players when they are in your lobby");
		Property propertytimeBetweenReports = config.get(CATEGORY_NOCHEATERS, "Time between reports", 6, "Time before the mod suggests to report the player again (hours)");
		Property propertytimeAutoReport = config.get(CATEGORY_NOCHEATERS, "Time for autoreports", 336, "It won't autoreport players whose last report is older than this (hours)");
		
		List<String> propertyOrderMWWENh = new ArrayList<String>();
		propertyOrderMWWENh.add(propertyAPIKey.getName());
		propertyOrderMWWENh.add(propertyshortencoinmessage.getName());
		propertyOrderMWWENh.add(propertyreportsuggestions.getName());
		config.setCategoryPropertyOrder(CATEGORY_MWENh, propertyOrderMWWENh);
		
		List<String> propertyOrderGUI = new ArrayList<String>();
		propertyOrderGUI.add(propertyshow_killcooldownGUI.getName());
		propertyOrderGUI.add(propertyxpos_killcooldownGUI.getName());
		propertyOrderGUI.add(propertyypos_killcooldownGUI.getName());
		propertyOrderGUI.add(propertyshow_ArrowHitGui.getName());
		propertyOrderGUI.add(propertyxpos_ArrowHitGui.getName());
		propertyOrderGUI.add(propertyypos_ArrowHitGui.getName());
		config.setCategoryPropertyOrder(CATEGORY_GUI, propertyOrderGUI);
		
		List<String> propertyOrderNOCHEATERS = new ArrayList<String>();
		propertyOrderNOCHEATERS.add(propertytoggleicons.getName());
		propertyOrderNOCHEATERS.add(propertytogglewarnings.getName());
		propertyOrderNOCHEATERS.add(propertytoggleautoreport.getName());
		propertyOrderNOCHEATERS.add(propertytimeBetweenReports.getName());
		propertyOrderNOCHEATERS.add(propertytimeAutoReport.getName());
		config.setCategoryPropertyOrder(CATEGORY_NOCHEATERS, propertyOrderNOCHEATERS);
		
		if(readFieldsFromConfig) {		
			APIKey = propertyAPIKey.getString();
			shortencoinmessage = propertyshortencoinmessage.getBoolean();
			reportsuggestions = propertyreportsuggestions.getBoolean();
			
			show_killcooldownGUI = propertyshow_killcooldownGUI.getBoolean();
			xpos_killcooldownGUI = propertyxpos_killcooldownGUI.getDouble();
			ypos_killcooldownGUI = propertyypos_killcooldownGUI.getDouble();
			show_ArrowHitGui = propertyshow_ArrowHitGui.getBoolean();
			xpos_ArrowHitGui = propertyxpos_ArrowHitGui.getDouble();
			ypos_ArrowHitGui = propertyypos_ArrowHitGui.getDouble();
			
			toggleicons = propertytoggleicons.getBoolean();
			togglewarnings = propertytogglewarnings.getBoolean();
			toggleautoreport = propertytoggleautoreport.getBoolean();
			timeBetweenReports = (long) 3600l*1000l*propertytimeBetweenReports.getInt();
			timeAutoReport = (long) 3600l*1000l*propertytimeAutoReport.getInt();
		}
		
		propertyAPIKey.set(APIKey);
		propertyshortencoinmessage.set(shortencoinmessage);
		propertyreportsuggestions.set(reportsuggestions);
		
		propertyshow_killcooldownGUI.set(show_killcooldownGUI);
		propertyxpos_killcooldownGUI.set(xpos_killcooldownGUI);
		propertyypos_killcooldownGUI.set(ypos_killcooldownGUI);
		propertyshow_ArrowHitGui.set(show_ArrowHitGui);
		propertyxpos_ArrowHitGui.set(xpos_ArrowHitGui);
		propertyypos_ArrowHitGui.set(ypos_ArrowHitGui);
		
		propertytoggleicons.set(toggleicons);
		propertytogglewarnings.set(togglewarnings);
		propertytoggleautoreport.set(toggleautoreport);
		propertytimeBetweenReports.set((int)timeBetweenReports/(3600l*1000l));
		propertytimeAutoReport.set((int)timeAutoReport/(3600l*1000l));
		
		if(config.hasChanged()) {
			config.save();
		}

	}
	
	public static void syncFromFiles() {
		syncConfig(true, true);
	}
	
	/*
	 * call this method to save in the config file the modifications made to the fields of this class that happened via command, GUI etc
	 */
	public static void syncFromInGameChange() {
		syncConfig(false, false);
	}

}
