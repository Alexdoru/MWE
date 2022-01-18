package fr.alexdoru.megawallsenhancementsmod.config;

import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static Configuration config;
    private static final String CATEGORY_FKCounter = "Final Kill Counter";
    private static final String CATEGORY_MWENh = "MegaWallsEnhancements";
    private static final String CATEGORY_GUI = "GUI";
    private static final String CATEGORY_NOCHEATERS = "NoCheaters";

    /* FKCounter config*/
    public static final GuiPosition fkcounterPosition = new GuiPosition(0d, 0d);
    public static boolean show_fkcHUD;
    public static boolean compact_hud;
    public static boolean show_players;
    public static boolean FKHUDinSidebar;
    public static boolean draw_background;
    public static boolean text_shadow;
    public static double fkc_hud_size;
    public static int playerAmount;
    public static boolean finalsInTablist;
    // TODO add the finals on nametags

    /*MWEnhancements config*/
    public static String APIKey;
    public static boolean strengthParticules;
    public static boolean shortencoinmessage;
    public static boolean reportsuggestions;
    public static boolean playSoundLowHP;
    public static double healthThreshold;
    public static boolean keepNightVisionEffect;
    public static boolean useColoredScores;

    /*GUI config*/
    public static boolean show_killcooldownHUD;
    public static final GuiPosition killcooldownHUDPosition = new GuiPosition(0d, 0d);
    public static boolean show_ArrowHitHUD;
    public static final GuiPosition arrowHitHUDPosition = new GuiPosition(0d, 0d);
    public static boolean show_lastWitherHUD;
    public static boolean witherHUDinSiderbar;
    public static final GuiPosition lastWitherHUDPosition = new GuiPosition(0d, 0d);
    public static boolean hunterStrengthHUD;
    public static final GuiPosition hunterStrengthHUDPosition = new GuiPosition(0d, 0d);

    /*NoCheaters Config*/
    public static boolean toggleicons;
    public static boolean togglewarnings;
    public static boolean toggleautoreport;
    /*those fields are in milliseconds*/
    public static long timeBetweenReports; // TODO baisser ca de fou pour le faire toute les heures, que dans la private version, le faire que pour les mecs qui bhop ?
    public static long timeAutoReport;
    public static boolean deleteReports;
    public static long timeDeleteReport;

    public static void preinit(File file) {
        config = new Configuration(file);
        syncConfig(true, true, false);
    }

    private static void syncConfig(boolean loadFromConfigFile, boolean readFieldsFromConfig, boolean saveFieldsToConfig) {

        if (loadFromConfigFile) {
            config.load();
        }

        /*Reads the fiels in the config and stores them in the property objects, and defines a default value if the fields doesn't exist*/
        Property pXpos_fkcHUD = config.get(CATEGORY_FKCounter, "Xpos FKCounter HUD", 0d, "The x position of the final kill counter HUD, value ranges from 0 to 1");
        Property pYpos_fkcHUD = config.get(CATEGORY_FKCounter, "Ypos FKCounter HUD", 0.1d, "The y position of the final kill counter HUD, value ranges from 0 to 1");
        Property pshow_fkcHUD = config.get(CATEGORY_FKCounter, "Show FKCounter HUD", true, "Displays the HUD of the final kill counter");
        Property pcompactHUD = config.get(CATEGORY_FKCounter, "Compact FKCounter HUD", false, "Use a compact HUD for the final kill counter");
        Property pshow_players = config.get(CATEGORY_FKCounter, "Show players", false, "Displays players with most finals in each team");
        Property pinSidebar = config.get(CATEGORY_FKCounter, "HUD in sidebar", false, "Places the fkcounter in the sidebar");
        Property pdraw_background = config.get(CATEGORY_FKCounter, "Draw background", false, "Draws a box around the HUD of the final kill counter");
        Property ptext_shadow = config.get(CATEGORY_FKCounter, "Text shadow", true, "Draws text shadow");
        Property pfkc_hud_size = config.get(CATEGORY_FKCounter, "HUD Size", 1.0f, "Size of the final kill counter HUD");
        Property pPlayerAmount = config.get(CATEGORY_FKCounter, "Player amount", (int) 3.0, "Amount of players displayed on screen when you use the \"Show players\" setting");
        Property pfinalsInTablist = config.get(CATEGORY_FKCounter, "Fks in tablist", true, "Draws the finals in the tablist");

        Property pAPIKey = config.get(CATEGORY_MWENh, "APIKey", "", "Your Hypixel API Key");
        Property pstrengthParticules = config.get(CATEGORY_MWENh, "Strength particules", true, "Spawns strength particules when an herobrine or dreadlord get a final");
        Property pShortencoinmessage = config.get(CATEGORY_MWENh, "Shorten coin message", false, "Shorten the coins messages by removing the network booster info");
        Property pReportsuggestions = config.get(CATEGORY_MWENh, "Report suggestion", true, "Give report suggestions in the chat based on messages in shouts");
        Property pPlaySoundLowHP = config.get(CATEGORY_MWENh, "Sound low HP", false, "Plays a sound when your health falls below a certain threshold");
        Property pHealthThreshold = config.get(CATEGORY_MWENh, "Health Threshold", 0.5d, "The health threshold at witch it will play a sound, value ranges from 0 to 1");
        Property pCancelNightVisionEffect = config.get(CATEGORY_MWENh, "Cancel Night Vision Effect", false, "Removes the visual effets of night vision");
        Property puseColoredScores = config.get(CATEGORY_MWENh, "Colored Tablist Scores", true, "Makes the scores in the tablist use a greend to red color gradient depending of the value");

        Property pShow_killcooldownHUD = config.get(CATEGORY_GUI, "Show kill cooldown HUD", true, "Displays the cooldown for the /kill command when in MegaWalls");
        Property pXpos_killcooldownHUD = config.get(CATEGORY_GUI, "Xpos kill cooldown HUD", 0d, "The x position of the killcooldown HUD, value ranges from 0 to 1");
        Property pYpos_killcooldownHUD = config.get(CATEGORY_GUI, "Ypos kill cooldown HUD", 0d, "The y position of the killcooldown HUD, value ranges from 0 to 1");
        Property pShow_ArrowHitHUD = config.get(CATEGORY_GUI, "Show Arrow Hit HUD", true, "Displays the HP of opponents on arrow hits");
        Property pXpos_ArrowHitHUD = config.get(CATEGORY_GUI, "Xpos Arrow Hit HUD", 0.5d, "The x position of the ArrowHitGui, value ranges from 0 to 1");
        Property pYpos_ArrowHitHUD = config.get(CATEGORY_GUI, "Ypos Arrow Hit HUD", 9d / 20d, "The y position of the ArrowHitGui, value ranges from 0 to 1");
        Property pShow_lastWitherHUD = config.get(CATEGORY_GUI, "Show last wither HUD", true, "Displays the time it takes for the last wither to die");
        Property pwitherHUDinSiderbar = config.get(CATEGORY_GUI, "Wiher HUD in sidebar", true, "Displays the time it takes for the last wither to die in the sidebar");
        Property pXpos_lastWitherHUD = config.get(CATEGORY_GUI, "Xpos last wither HUD", 0.75d, "The x position of the LastWitherHUD, value ranges from 0 to 1");
        Property pYpos_lastWitherHUD = config.get(CATEGORY_GUI, "Ypos last wither HUD", 0.05d, "The y position of the LastWitherHUD, value ranges from 0 to 1");
        Property pHunterStrengthHUD = config.get(CATEGORY_GUI, "Hunter Strength HUD", true, "Displays HUD and plays a sound 10 seconds before getting strength with hunter");
        Property pXpos_hunterHUD = config.get(CATEGORY_GUI, "Xpos hunter Strength HUD", 0.5d, "The x position of the Hunter Strength HUD, value ranges from 0 to 1");
        Property pYpos_hunterHUD = config.get(CATEGORY_GUI, "Ypos hunter Strength HUD", 8d / 20d, "The y position of the Hunter Strength HUD, value ranges from 0 to 1");

        Property pToggleicons = config.get(CATEGORY_NOCHEATERS, "Toggle Icons", true, "Display warning symbol on nametags of reported players");
        Property pTogglewarnings = config.get(CATEGORY_NOCHEATERS, "Toggle Warnings", false, "Gives warning messages in chat for reported players");
        Property pToggleautoreport = config.get(CATEGORY_NOCHEATERS, "Toggle Autoreport", false, "Automatically report previously reported players when they are in your lobby");
        Property pTimeBetweenReports = config.get(CATEGORY_NOCHEATERS, "Time between reports", 12, "Time before the mod suggests to report the player again (hours)");
        Property pTimeAutoReport = config.get(CATEGORY_NOCHEATERS, "Time for autoreports", 14, "It won't autoreport players whose last report is older than this (days)");
        Property pdeleteReports = config.get(CATEGORY_NOCHEATERS, "Delete Old Report", false, "Deletes reports older than the specified value");
        Property ptimeDeleteReport = config.get(CATEGORY_NOCHEATERS, "Time delete reports", 365, "Reports older than this will be deleted on game start (days)");

        /*Set the Order in which the config entries appear in the config file */
        List<String> pOrderFKC = new ArrayList<>();
        pOrderFKC.add(pXpos_fkcHUD.getName());
        pOrderFKC.add(pYpos_fkcHUD.getName());
        pOrderFKC.add(pshow_fkcHUD.getName());
        pOrderFKC.add(pcompactHUD.getName());
        pOrderFKC.add(pshow_players.getName());
        pOrderFKC.add(pinSidebar.getName());
        pOrderFKC.add(pdraw_background.getName());
        pOrderFKC.add(ptext_shadow.getName());
        pOrderFKC.add(pfkc_hud_size.getName());
        pOrderFKC.add(pPlayerAmount.getName());
        pOrderFKC.add(pfinalsInTablist.getName());
        config.setCategoryPropertyOrder(CATEGORY_FKCounter, pOrderFKC);

        List<String> pOrderMWWENh = new ArrayList<>();
        pOrderMWWENh.add(pAPIKey.getName());
        pOrderMWWENh.add(pstrengthParticules.getName());
        pOrderMWWENh.add(pShortencoinmessage.getName());
        pOrderMWWENh.add(pReportsuggestions.getName());
        pOrderMWWENh.add(pPlaySoundLowHP.getName());
        pOrderMWWENh.add(pHealthThreshold.getName());
        pOrderMWWENh.add(pCancelNightVisionEffect.getName());
        pOrderMWWENh.add(puseColoredScores.getName());
        config.setCategoryPropertyOrder(CATEGORY_MWENh, pOrderMWWENh);

        List<String> pOrderGUI = new ArrayList<>();
        pOrderGUI.add(pShow_killcooldownHUD.getName());
        pOrderGUI.add(pXpos_killcooldownHUD.getName());
        pOrderGUI.add(pYpos_killcooldownHUD.getName());
        pOrderGUI.add(pShow_ArrowHitHUD.getName());
        pOrderGUI.add(pXpos_ArrowHitHUD.getName());
        pOrderGUI.add(pYpos_ArrowHitHUD.getName());
        pOrderGUI.add(pShow_lastWitherHUD.getName());
        pOrderGUI.add(pwitherHUDinSiderbar.getName());
        pOrderGUI.add(pXpos_lastWitherHUD.getName());
        pOrderGUI.add(pYpos_lastWitherHUD.getName());
        pOrderGUI.add(pHunterStrengthHUD.getName());
        pOrderGUI.add(pXpos_hunterHUD.getName());
        pOrderGUI.add(pYpos_hunterHUD.getName());
        config.setCategoryPropertyOrder(CATEGORY_GUI, pOrderGUI);

        List<String> pOrderNOCHEATERS = new ArrayList<>();
        pOrderNOCHEATERS.add(pToggleicons.getName());
        pOrderNOCHEATERS.add(pTogglewarnings.getName());
        pOrderNOCHEATERS.add(pToggleautoreport.getName());
        pOrderNOCHEATERS.add(pTimeBetweenReports.getName());
        pOrderNOCHEATERS.add(pTimeAutoReport.getName());
        pOrderNOCHEATERS.add(pdeleteReports.getName());
        pOrderNOCHEATERS.add(ptimeDeleteReport.getName());
        config.setCategoryPropertyOrder(CATEGORY_NOCHEATERS, pOrderNOCHEATERS);

        /*sets the fields of this class to the fields in the properties*/
        if (readFieldsFromConfig) {

            fkcounterPosition.setRelative(pXpos_fkcHUD.getDouble(), pYpos_fkcHUD.getDouble());
            show_fkcHUD = pshow_fkcHUD.getBoolean();
            compact_hud = pcompactHUD.getBoolean();
            show_players = pshow_players.getBoolean();
            FKHUDinSidebar = pinSidebar.getBoolean();
            draw_background = pdraw_background.getBoolean();
            text_shadow = ptext_shadow.getBoolean();
            fkc_hud_size = pfkc_hud_size.getDouble();
            playerAmount = pPlayerAmount.getInt();
            finalsInTablist = pfinalsInTablist.getBoolean();

            APIKey = pAPIKey.getString();
            strengthParticules = pstrengthParticules.getBoolean();
            shortencoinmessage = pShortencoinmessage.getBoolean();
            reportsuggestions = pReportsuggestions.getBoolean();
            playSoundLowHP = pPlaySoundLowHP.getBoolean();
            healthThreshold = pHealthThreshold.getDouble();
            keepNightVisionEffect = !pCancelNightVisionEffect.getBoolean();
            useColoredScores = puseColoredScores.getBoolean();

            show_killcooldownHUD = pShow_killcooldownHUD.getBoolean();
            killcooldownHUDPosition.setRelative(pXpos_killcooldownHUD.getDouble(), pYpos_killcooldownHUD.getDouble());
            show_ArrowHitHUD = pShow_ArrowHitHUD.getBoolean();
            arrowHitHUDPosition.setRelative(pXpos_ArrowHitHUD.getDouble(), pYpos_ArrowHitHUD.getDouble());
            show_lastWitherHUD = pShow_lastWitherHUD.getBoolean();
            witherHUDinSiderbar = pwitherHUDinSiderbar.getBoolean();
            lastWitherHUDPosition.setRelative(pXpos_lastWitherHUD.getDouble(), pYpos_lastWitherHUD.getDouble());
            hunterStrengthHUD = pHunterStrengthHUD.getBoolean();
            hunterStrengthHUDPosition.setRelative(pXpos_hunterHUD.getDouble(), pYpos_hunterHUD.getDouble());

            toggleicons = pToggleicons.getBoolean();
            togglewarnings = pTogglewarnings.getBoolean();
            toggleautoreport = pToggleautoreport.getBoolean();
            timeBetweenReports = Math.max(6L * 3600L * 1000L, Math.min(3600L * 1000L * ((long) pTimeBetweenReports.getInt()), 48L * 3600L * 1000L));
            timeAutoReport = Math.max(24L * 3600L * 1000L, Math.min(24L * 3600L * 1000L * ((long) pTimeAutoReport.getInt()), 30L * 24L * 3600L * 1000L));
            deleteReports = pdeleteReports.getBoolean();
            timeDeleteReport = 24L * 3600L * 1000L * ((long) ptimeDeleteReport.getInt());

        }

        if (saveFieldsToConfig) {

            double[] fkcHUDarray = fkcounterPosition.getRelativePosition();
            pXpos_fkcHUD.set(fkcHUDarray[0]);
            pYpos_fkcHUD.set(fkcHUDarray[1]);
            pshow_fkcHUD.set(show_fkcHUD);
            pcompactHUD.set(compact_hud);
            pshow_players.set(show_players);
            pinSidebar.set(FKHUDinSidebar);
            pdraw_background.set(draw_background);
            ptext_shadow.set(text_shadow);
            pfkc_hud_size.set(fkc_hud_size);
            pPlayerAmount.set(playerAmount);
            pfinalsInTablist.set(finalsInTablist);

            pAPIKey.set(APIKey);
            pstrengthParticules.set(strengthParticules);
            pShortencoinmessage.set(shortencoinmessage);
            pReportsuggestions.set(reportsuggestions);
            pPlaySoundLowHP.set(playSoundLowHP);
            pHealthThreshold.set(healthThreshold);
            pCancelNightVisionEffect.set(!keepNightVisionEffect);
            puseColoredScores.set(useColoredScores);

            pShow_killcooldownHUD.set(show_killcooldownHUD);
            double[] killcooldownHUDarray = killcooldownHUDPosition.getRelativePosition();
            pXpos_killcooldownHUD.set(killcooldownHUDarray[0]);
            pYpos_killcooldownHUD.set(killcooldownHUDarray[1]);

            pShow_ArrowHitHUD.set(show_ArrowHitHUD);
            double[] ArrowHitHUDarray = arrowHitHUDPosition.getRelativePosition();
            pXpos_ArrowHitHUD.set(ArrowHitHUDarray[0]);
            pYpos_ArrowHitHUD.set(ArrowHitHUDarray[1]);

            pShow_lastWitherHUD.set(show_lastWitherHUD);
            pwitherHUDinSiderbar.set(witherHUDinSiderbar);
            double[] lastWitherHUDarray = lastWitherHUDPosition.getRelativePosition();
            pXpos_lastWitherHUD.set(lastWitherHUDarray[0]);
            pYpos_lastWitherHUD.set(lastWitherHUDarray[1]);

            pHunterStrengthHUD.set(hunterStrengthHUD);
            double[] hunterStrengtharray = hunterStrengthHUDPosition.getRelativePosition();
            pXpos_hunterHUD.set(hunterStrengtharray[0]);
            pYpos_hunterHUD.set(hunterStrengtharray[1]);

            pToggleicons.set(toggleicons);
            pTogglewarnings.set(togglewarnings);
            pToggleautoreport.set(toggleautoreport);
            pTimeBetweenReports.set((int) (timeBetweenReports / (3600L * 1000L)));
            pTimeAutoReport.set((int) (timeAutoReport / (24L * 3600L * 1000L)));
            pdeleteReports.set(deleteReports);
            ptimeDeleteReport.set((int) (timeDeleteReport / (24L * 3600L * 1000L)));

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

    public static void toggleIcons() {
        toggleicons = !toggleicons;
        if (toggleicons) {
            mc.theWorld.playerEntities.forEach(playerEntity -> {
                NameUtil.handlePlayer(playerEntity, true, false, false);
                NameUtil.transformNameTablist(playerEntity.getUniqueID());
            });
        } else {
            mc.theWorld.playerEntities.forEach(playerEntity -> {
                NameUtil.removeNametagIcons(playerEntity);
                NameUtil.transformNameTablist(playerEntity.getUniqueID());
            });
        }
    }

}
