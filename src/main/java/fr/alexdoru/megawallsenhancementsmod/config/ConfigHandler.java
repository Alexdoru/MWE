package fr.alexdoru.megawallsenhancementsmod.config;

import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private static Configuration config;

    /**
     * FKCounter config
     */
    public static final GuiPosition fkcounterHUDPosition = new GuiPosition(0d, 0d);
    public static boolean showfkcounterHUD;
    public static boolean fkcounterHUDCompact;
    public static boolean fkcounterHUDShowPlayers;
    public static boolean fkcounterHUDinSidebar;
    public static boolean fkcounterHUDDrawBackground;
    public static boolean fkcounterHUDTextShadow;
    public static double fkcounterHUDSize;
    public static int fkcounterHUDPlayerAmount;
    public static boolean fkcounterHUDTablist;

    /**
     * MWEnhancements config
     */
    public static String APIKey;
    public static String hypixelNick;
    public static boolean strengthParticules;
    public static boolean shortCoinMessage;
    public static boolean playSoundLowHP;
    public static double healthThreshold;
    public static boolean keepNightVisionEffect;
    public static boolean useColoredScores;
    public static boolean safeInventory;
    public static boolean limitDroppedEntityRendered;
    public static int maxDroppedEntityRendered;
    public static boolean prestigeV;
    public static boolean hideRepetitiveMWChatMsg;
    public static boolean clearVision;
    public static boolean automaticUpdate;
    public static boolean nickHider;
    public static boolean hideTablistHeaderFooter;
    public static boolean showPlayercountTablist;
    public static int tablistSize;
    public static boolean fixActionbarTextOverlap;

    /**
     * HUD config
     */
    public static boolean showKillCooldownHUD;
    public static final GuiPosition killCooldownHUDPosition = new GuiPosition(0d, 0d);
    public static boolean showArrowHitHUD;
    public static final GuiPosition arrowHitHUDPosition = new GuiPosition(0d, 0d);
    public static boolean showLastWitherHUD;
    public static boolean witherHUDinSidebar;
    public static final GuiPosition lastWitherHUDPosition = new GuiPosition(0d, 0d);
    public static boolean showStrengthHUD;
    public static final GuiPosition hunterStrengthHUDPosition = new GuiPosition(0d, 0d);
    public static boolean showSquadHealthHUD;// TODO add config
    public static final GuiPosition squadHealthHUDPosition = new GuiPosition(0d, 0d);// TODO add config

    /**
     * NoCheaters Config
     */
    public static boolean iconsOnNames;
    public static boolean warningMessages;
    public static boolean toggleAutoreport;
    public static boolean reportSuggestions;
    public static boolean autoreportSuggestions;
    public static boolean deleteOldReports;
    public static long timeDeleteReport;
    public static boolean censorCheaterChatMsg;
    public static boolean deleteCheaterChatMsg;

    /**
     * Hitbox Config
     */
    /*Used to remember if it was toggled after restarting the game*/
    public static boolean isDebugHitboxOn;
    public static boolean drawHitboxForPlayers;
    public static boolean drawHitboxForGroundedArrows;
    public static boolean drawHitboxForPinnedArrows;
    public static boolean drawHitboxForFlyingArrows;
    public static boolean drawHitboxForDroppedItems;
    public static boolean drawHitboxForPassiveMobs;
    public static boolean drawHitboxForAggressiveMobs;
    public static boolean drawHitboxItemFrame;
    public static boolean drawHitboxForOtherEntity;
    public static boolean drawRedBox;
    public static boolean hideBlueVect;
    public static boolean drawBlueVectForPlayersOnly;
    public static boolean makeBlueVect3Meters;
    public static boolean realSizeHitbox;
    public static boolean drawRangedHitbox;
    public static float hitboxDrawRange;

    public static void preinit(File file) {
        config = new Configuration(file);
        syncConfig(true, true, false);
    }

    private static void syncConfig(boolean loadFromConfigFile, boolean readFieldsFromConfig, boolean saveFieldsToConfig) {

        if (config == null) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.DARK_RED + "Config didn't load when the game started, this shouldn't happen !");
            return;
        }

        if (loadFromConfigFile) {
            config.load();
        }

        /*Reads the fields in the config and stores them in the property objects, and defines a default value if the fields doesn't exist*/
        final String CATEGORY_FKCounter = "Final Kill Counter";
        final Property pXpos_fkcHUD = config.get(CATEGORY_FKCounter, "Xpos FKCounter HUD", 0d, "The x position of the final kill counter HUD, value ranges from 0 to 1");
        final Property pYpos_fkcHUD = config.get(CATEGORY_FKCounter, "Ypos FKCounter HUD", 0.1d, "The y position of the final kill counter HUD, value ranges from 0 to 1");
        final Property pshow_fkcHUD = config.get(CATEGORY_FKCounter, "Show FKCounter HUD", true, "Displays the HUD of the final kill counter");
        final Property pcompactHUD = config.get(CATEGORY_FKCounter, "Compact FKCounter HUD", false, "Use a compact HUD for the final kill counter");
        final Property pshow_players = config.get(CATEGORY_FKCounter, "Show players", false, "Displays players with most finals in each team");
        final Property pinSidebar = config.get(CATEGORY_FKCounter, "HUD in sidebar", false, "Places the fkcounter in the sidebar");
        final Property pdraw_background = config.get(CATEGORY_FKCounter, "Draw background", false, "Draws a box around the HUD of the final kill counter");
        final Property ptext_shadow = config.get(CATEGORY_FKCounter, "Text shadow", true, "Draws text shadow");
        final Property pfkc_hud_size = config.get(CATEGORY_FKCounter, "HUD Size", 1.0f, "Size of the final kill counter HUD");
        final Property pPlayerAmount = config.get(CATEGORY_FKCounter, "Player amount", 3, "Amount of players displayed on screen when you use the \"Show players\" setting");
        final Property pfinalsInTablist = config.get(CATEGORY_FKCounter, "Fks in tablist", true, "Draws the finals in the tablist");

        final String CATEGORY_MWENh = "MegaWallsEnhancements";
        final Property pAPIKey = config.get(CATEGORY_MWENh, "APIKey", "", "Your Hypixel API Key");
        final Property phypixelNick = config.get(CATEGORY_MWENh, "Hypixel Nick", "", "Your nick on Hypixel");
        final Property pstrengthParticules = config.get(CATEGORY_MWENh, "Strength particules", true, "Spawns strength particules when an herobrine or dreadlord get a final");
        final Property pShortencoinmessage = config.get(CATEGORY_MWENh, "Shorten coin message", false, "Shorten the coins messages by removing the network booster info");
        final Property pPlaySoundLowHP = config.get(CATEGORY_MWENh, "Sound low HP", false, "Plays a sound when your health falls below a certain threshold");
        final Property pHealthThreshold = config.get(CATEGORY_MWENh, "Health Threshold", 0.5d, "The health threshold at witch it will play a sound, value ranges from 0 to 1");
        final Property pCancelNightVisionEffect = config.get(CATEGORY_MWENh, "Cancel Night Vision Effect", false, "Removes the visual effets of night vision");
        final Property puseColoredScores = config.get(CATEGORY_MWENh, "Colored Tablist Scores", true, "Makes the scores in the tablist use a greend to red color gradient depending of the value");
        final Property psafeInventory = config.get(CATEGORY_MWENh, "Safe Inventory", true, "Prevents sword dropping and hotkeying kit items");
        final Property plimitDroppedEntityRendered = config.get(CATEGORY_MWENh, "Limit dropped item rendered", true, "Limit dropped item rendered");
        final Property pmaxDroppedEntityRendered = config.get(CATEGORY_MWENh, "Max amount of item rendered", 120, "Max amount of item rendered");
        final Property pprestigeV = config.get(CATEGORY_MWENh, "Prestige V colored Tag", false, "Prestige V colored Tag");
        final Property phideRepetitiveMWChatMsg = config.get(CATEGORY_MWENh, "Delete repetitive chat messages in mw", true, "Delete repetitive chat messages in mw");
        final Property pclearVision = config.get(CATEGORY_MWENh, "Clear Vision", true, "Hides particles too close to the camera");
        final Property pautomaticUpdate = config.get(CATEGORY_MWENh, "Automatic Update", true, "Updates the mod automatically");
        final Property pNickHider = config.get(CATEGORY_MWENh, "Nick Hider", true, "Shows your real name instead of your nick when in squad");
        final Property phideTablistHeaderFooter = config.get(CATEGORY_MWENh, "Hide Header Footer Tablist", false, "Hides the header and footer text in the Tablist");
        final Property pshowPlayercountTablist = config.get(CATEGORY_MWENh, "Show playercount Tablist", true, "Shows the amount of players in the lobby at the top of the Tablist");
        final Property ptablistSize = config.get(CATEGORY_MWENh, "Tablist size", 100, "Amount of players displayed in the tablist (Vanilla 80)");
        final Property pfixActionbarTextOverlap = config.get(CATEGORY_MWENh, "Fix actionbar text overlap", true, "Prevents the actionbar text from overlapping with the armor bar");

        final String CATEGORY_GUI = "GUI";
        final Property pShow_killcooldownHUD = config.get(CATEGORY_GUI, "Show kill cooldown HUD", true, "Displays the cooldown for the /kill command when in MegaWalls");
        final Property pXpos_killcooldownHUD = config.get(CATEGORY_GUI, "Xpos kill cooldown HUD", 0d, "The x position of the killcooldown HUD, value ranges from 0 to 1");
        final Property pYpos_killcooldownHUD = config.get(CATEGORY_GUI, "Ypos kill cooldown HUD", 0d, "The y position of the killcooldown HUD, value ranges from 0 to 1");
        final Property pShow_ArrowHitHUD = config.get(CATEGORY_GUI, "Show Arrow Hit HUD", true, "Displays the HP of opponents on arrow hits");
        final Property pXpos_ArrowHitHUD = config.get(CATEGORY_GUI, "Xpos Arrow Hit HUD", 0.5d, "The x position of the ArrowHitHUD, value ranges from 0 to 1");
        final Property pYpos_ArrowHitHUD = config.get(CATEGORY_GUI, "Ypos Arrow Hit HUD", 9d / 20d, "The y position of the ArrowHitHUD, value ranges from 0 to 1");
        final Property pShow_lastWitherHUD = config.get(CATEGORY_GUI, "Show last wither HUD", true, "Displays the time it takes for the last wither to die");
        final Property pwitherHUDinSidebar = config.get(CATEGORY_GUI, "Wiher HUD in sidebar", true, "Displays the time it takes for the last wither to die in the sidebar");
        final Property pXpos_lastWitherHUD = config.get(CATEGORY_GUI, "Xpos last wither HUD", 0.75d, "The x position of the LastWitherHUD, value ranges from 0 to 1");
        final Property pYpos_lastWitherHUD = config.get(CATEGORY_GUI, "Ypos last wither HUD", 0.05d, "The y position of the LastWitherHUD, value ranges from 0 to 1");
        final Property pHunterStrengthHUD = config.get(CATEGORY_GUI, "Hunter Strength HUD", true, "Displays HUD and plays a sound 10 seconds before getting strength with hunter");
        final Property pXpos_hunterHUD = config.get(CATEGORY_GUI, "Xpos hunter Strength HUD", 0.5d, "The x position of the Hunter Strength HUD, value ranges from 0 to 1");
        final Property pYpos_hunterHUD = config.get(CATEGORY_GUI, "Ypos hunter Strength HUD", 8d / 20d, "The y position of the Hunter Strength HUD, value ranges from 0 to 1");

        final String CATEGORY_NOCHEATERS = "NoCheaters";
        final Property pToggleicons = config.get(CATEGORY_NOCHEATERS, "Toggle Icons", true, "Display warning symbol on nametags of reported players");
        final Property pTogglewarnings = config.get(CATEGORY_NOCHEATERS, "Toggle Warnings", false, "Gives warning messages in chat for reported players");
        final Property preportsuggestions = config.get(CATEGORY_NOCHEATERS, "Report suggestion", true, "Give report suggestions in the chat based on messages in shouts");
        final Property pautoreportSuggestions = config.get(CATEGORY_NOCHEATERS, "Send report suggestions", true, "Send report suggestions");
        final Property pToggleautoreport = config.get(CATEGORY_NOCHEATERS, "Autoreport saved cheaters", true, "Automatically reports previously reported players when they are in your game");
        final Property pdeleteReports = config.get(CATEGORY_NOCHEATERS, "Delete Old Report", false, "Deletes reports older than the specified value");
        final Property ptimeDeleteReport = config.get(CATEGORY_NOCHEATERS, "Time delete reports", 365, "Reports older than this will be deleted on game start (days)");
        final Property pcensorCheaterChatMsg = config.get(CATEGORY_NOCHEATERS, "Censor Cheater Chat", false, "Censors chat messages sent by reported cheaters");
        final Property pdeleteCheaterChatMsg = config.get(CATEGORY_NOCHEATERS, "Delete Cheater Chat", false, "Deletes chat messages sent by reported cheaters");

        final String CATEGORY_HITBOX = "Hitbox";
        final Property pisDebugHitboxOn = config.get(CATEGORY_HITBOX, "Toggle hitbox", false, "Toggle hitbox");
        final Property pdrawHitboxForPlayers = config.get(CATEGORY_HITBOX, "Hitbox for players", true, "Hitbox for players");
        final Property pdrawHitboxForGroundedArrows = config.get(CATEGORY_HITBOX, "Hitbox for grounded arrows", true, "Hitbox for grounded arrows");
        final Property pdrawHitboxForPinnedArrows = config.get(CATEGORY_HITBOX, "Hitbox for pinned arrows", true, "Hitbox for pinned arrows");
        final Property pdrawHitboxForFlyingArrows = config.get(CATEGORY_HITBOX, "Hitbox for flying arrows", true, "Hitbox for flying arrows");
        final Property pdrawHitboxForDroppedItems = config.get(CATEGORY_HITBOX, "Hitbox for dropped items", true, "Hitbox for dropped items");
        final Property pdrawHitboxForPassiveMobs = config.get(CATEGORY_HITBOX, "Hitbox for passive mobs", true, "Hitbox for passive mobs");
        final Property pdrawHitboxForAggressiveMobs = config.get(CATEGORY_HITBOX, "Hitbox for aggressive mobs", true, "Hitbox for aggressive mobs");
        final Property pdrawHitboxItemFrame = config.get(CATEGORY_HITBOX, "Hitbox for item frame", true, "Hitbox for item frame");
        final Property pdrawHitboxForOtherEntity = config.get(CATEGORY_HITBOX, "Hitbox for other entity", true, "Hitbox for other entity");
        final Property pdrawRedBox = config.get(CATEGORY_HITBOX, "Draw red box", true, "Draw red box");
        final Property pHideBlueVect = config.get(CATEGORY_HITBOX, "Hide blue vector", false, "Hide blue vector");
        final Property pdrawBlueVectForPlayersOnly = config.get(CATEGORY_HITBOX, "Blue vect for players only", false, "Blue vect for players only");
        final Property pmakeBlueVect3Meters = config.get(CATEGORY_HITBOX, "Make blue vector 3m long", false, "Make blue vector 3m long");
        final Property prealSizeHitbox = config.get(CATEGORY_HITBOX, "Real size hitbox", false, "Make hitbox their real size");
        final Property pdrawRangedHitbox = config.get(CATEGORY_HITBOX, "Don't render close hitbox", false, "Doesn't render the hitbox of entities close to you");
        final Property phitboxDrawRange = config.get(CATEGORY_HITBOX, "Hitbox render range", 6f, "Doesn't render the hitbox of entities closer than this");

        /*Set the Order in which the config entries appear in the config file */
        final List<String> pOrderFKC = new ArrayList<>();
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

        final List<String> pOrderMWWENh = new ArrayList<>();
        pOrderMWWENh.add(pAPIKey.getName());
        pOrderMWWENh.add(phypixelNick.getName());
        pOrderMWWENh.add(pstrengthParticules.getName());
        pOrderMWWENh.add(pShortencoinmessage.getName());
        pOrderMWWENh.add(pPlaySoundLowHP.getName());
        pOrderMWWENh.add(pHealthThreshold.getName());
        pOrderMWWENh.add(pCancelNightVisionEffect.getName());
        pOrderMWWENh.add(puseColoredScores.getName());
        pOrderMWWENh.add(psafeInventory.getName());
        pOrderMWWENh.add(plimitDroppedEntityRendered.getName());
        pOrderMWWENh.add(pmaxDroppedEntityRendered.getName());
        pOrderMWWENh.add(pprestigeV.getName());
        pOrderMWWENh.add(phideRepetitiveMWChatMsg.getName());
        pOrderMWWENh.add(pclearVision.getName());
        pOrderMWWENh.add(pautomaticUpdate.getName());
        pOrderMWWENh.add(pNickHider.getName());
        pOrderMWWENh.add(phideTablistHeaderFooter.getName());
        pOrderMWWENh.add(pshowPlayercountTablist.getName());
        pOrderMWWENh.add(ptablistSize.getName());
        pOrderMWWENh.add(pfixActionbarTextOverlap.getName());
        config.setCategoryPropertyOrder(CATEGORY_MWENh, pOrderMWWENh);

        final List<String> pOrderGUI = new ArrayList<>();
        pOrderGUI.add(pShow_killcooldownHUD.getName());
        pOrderGUI.add(pXpos_killcooldownHUD.getName());
        pOrderGUI.add(pYpos_killcooldownHUD.getName());
        pOrderGUI.add(pShow_ArrowHitHUD.getName());
        pOrderGUI.add(pXpos_ArrowHitHUD.getName());
        pOrderGUI.add(pYpos_ArrowHitHUD.getName());
        pOrderGUI.add(pShow_lastWitherHUD.getName());
        pOrderGUI.add(pwitherHUDinSidebar.getName());
        pOrderGUI.add(pXpos_lastWitherHUD.getName());
        pOrderGUI.add(pYpos_lastWitherHUD.getName());
        pOrderGUI.add(pHunterStrengthHUD.getName());
        pOrderGUI.add(pXpos_hunterHUD.getName());
        pOrderGUI.add(pYpos_hunterHUD.getName());
        config.setCategoryPropertyOrder(CATEGORY_GUI, pOrderGUI);

        final List<String> pOrderNOCHEATERS = new ArrayList<>();
        pOrderNOCHEATERS.add(pToggleicons.getName());
        pOrderNOCHEATERS.add(pTogglewarnings.getName());
        pOrderNOCHEATERS.add(preportsuggestions.getName());
        pOrderNOCHEATERS.add(pToggleautoreport.getName());
        pOrderNOCHEATERS.add(pautoreportSuggestions.getName());
        pOrderNOCHEATERS.add(pdeleteReports.getName());
        pOrderNOCHEATERS.add(ptimeDeleteReport.getName());
        pOrderNOCHEATERS.add(pcensorCheaterChatMsg.getName());
        pOrderNOCHEATERS.add(pdeleteCheaterChatMsg.getName());
        config.setCategoryPropertyOrder(CATEGORY_NOCHEATERS, pOrderNOCHEATERS);

        final List<String> pOrderHitbox = new ArrayList<>();
        pOrderHitbox.add(pisDebugHitboxOn.getName());
        pOrderHitbox.add(pdrawHitboxForPlayers.getName());
        pOrderHitbox.add(pdrawHitboxForGroundedArrows.getName());
        pOrderHitbox.add(pdrawHitboxForPinnedArrows.getName());
        pOrderHitbox.add(pdrawHitboxForFlyingArrows.getName());
        pOrderHitbox.add(pdrawHitboxForDroppedItems.getName());
        pOrderHitbox.add(pdrawHitboxForPassiveMobs.getName());
        pOrderHitbox.add(pdrawHitboxForAggressiveMobs.getName());
        pOrderHitbox.add(pdrawHitboxItemFrame.getName());
        pOrderHitbox.add(pdrawHitboxForOtherEntity.getName());
        pOrderHitbox.add(pdrawRedBox.getName());
        pOrderHitbox.add(pHideBlueVect.getName());
        pOrderHitbox.add(pdrawBlueVectForPlayersOnly.getName());
        pOrderHitbox.add(pmakeBlueVect3Meters.getName());
        pOrderHitbox.add(prealSizeHitbox.getName());
        pOrderHitbox.add(pdrawRangedHitbox.getName());
        pOrderHitbox.add(phitboxDrawRange.getName());
        config.setCategoryPropertyOrder(CATEGORY_HITBOX, pOrderHitbox);

        /*sets the fields of this class to the fields in the properties*/
        if (readFieldsFromConfig) {

            fkcounterHUDPosition.setRelative(pXpos_fkcHUD.getDouble(), pYpos_fkcHUD.getDouble());
            showfkcounterHUD = pshow_fkcHUD.getBoolean();
            fkcounterHUDCompact = pcompactHUD.getBoolean();
            fkcounterHUDShowPlayers = pshow_players.getBoolean();
            fkcounterHUDinSidebar = pinSidebar.getBoolean();
            fkcounterHUDDrawBackground = pdraw_background.getBoolean();
            fkcounterHUDTextShadow = ptext_shadow.getBoolean();
            fkcounterHUDSize = pfkc_hud_size.getDouble();
            fkcounterHUDPlayerAmount = pPlayerAmount.getInt();
            fkcounterHUDTablist = pfinalsInTablist.getBoolean();

            APIKey = pAPIKey.getString();
            hypixelNick = phypixelNick.getString();
            strengthParticules = pstrengthParticules.getBoolean();
            shortCoinMessage = pShortencoinmessage.getBoolean();
            playSoundLowHP = pPlaySoundLowHP.getBoolean();
            healthThreshold = pHealthThreshold.getDouble();
            keepNightVisionEffect = !pCancelNightVisionEffect.getBoolean();
            useColoredScores = puseColoredScores.getBoolean();
            safeInventory = psafeInventory.getBoolean();
            limitDroppedEntityRendered = plimitDroppedEntityRendered.getBoolean();
            maxDroppedEntityRendered = pmaxDroppedEntityRendered.getInt();
            prestigeV = !HypixelApiKeyUtil.apiKeyIsNotSetup() && pprestigeV.getBoolean();
            hideRepetitiveMWChatMsg = phideRepetitiveMWChatMsg.getBoolean();
            clearVision = pclearVision.getBoolean();
            automaticUpdate = pautomaticUpdate.getBoolean();
            nickHider = pNickHider.getBoolean();
            hideTablistHeaderFooter = phideTablistHeaderFooter.getBoolean();
            showPlayercountTablist = pshowPlayercountTablist.getBoolean();
            tablistSize = ptablistSize.getInt();
            fixActionbarTextOverlap = pfixActionbarTextOverlap.getBoolean();

            showKillCooldownHUD = pShow_killcooldownHUD.getBoolean();
            killCooldownHUDPosition.setRelative(pXpos_killcooldownHUD.getDouble(), pYpos_killcooldownHUD.getDouble());
            showArrowHitHUD = pShow_ArrowHitHUD.getBoolean();
            arrowHitHUDPosition.setRelative(pXpos_ArrowHitHUD.getDouble(), pYpos_ArrowHitHUD.getDouble());
            showLastWitherHUD = pShow_lastWitherHUD.getBoolean();
            witherHUDinSidebar = pwitherHUDinSidebar.getBoolean();
            lastWitherHUDPosition.setRelative(pXpos_lastWitherHUD.getDouble(), pYpos_lastWitherHUD.getDouble());
            showStrengthHUD = pHunterStrengthHUD.getBoolean();
            hunterStrengthHUDPosition.setRelative(pXpos_hunterHUD.getDouble(), pYpos_hunterHUD.getDouble());

            iconsOnNames = pToggleicons.getBoolean();
            warningMessages = pTogglewarnings.getBoolean();
            toggleAutoreport = pToggleautoreport.getBoolean();
            reportSuggestions = preportsuggestions.getBoolean();
            autoreportSuggestions = pautoreportSuggestions.getBoolean();
            deleteOldReports = pdeleteReports.getBoolean();
            timeDeleteReport = 24L * 3600L * 1000L * ((long) ptimeDeleteReport.getInt());
            censorCheaterChatMsg = pcensorCheaterChatMsg.getBoolean();
            deleteCheaterChatMsg = pdeleteCheaterChatMsg.getBoolean();

            isDebugHitboxOn = pisDebugHitboxOn.getBoolean();
            drawHitboxForPlayers = pdrawHitboxForPlayers.getBoolean();
            drawHitboxForGroundedArrows = pdrawHitboxForGroundedArrows.getBoolean();
            drawHitboxForPinnedArrows = pdrawHitboxForPinnedArrows.getBoolean();
            drawHitboxForFlyingArrows = pdrawHitboxForFlyingArrows.getBoolean();
            drawHitboxForDroppedItems = pdrawHitboxForDroppedItems.getBoolean();
            drawHitboxForPassiveMobs = pdrawHitboxForPassiveMobs.getBoolean();
            drawHitboxForAggressiveMobs = pdrawHitboxForAggressiveMobs.getBoolean();
            drawHitboxItemFrame = pdrawHitboxItemFrame.getBoolean();
            drawHitboxForOtherEntity = pdrawHitboxForOtherEntity.getBoolean();
            drawRedBox = pdrawRedBox.getBoolean();
            hideBlueVect = pHideBlueVect.getBoolean();
            drawBlueVectForPlayersOnly = pdrawBlueVectForPlayersOnly.getBoolean();
            makeBlueVect3Meters = pmakeBlueVect3Meters.getBoolean();
            realSizeHitbox = prealSizeHitbox.getBoolean();
            drawRangedHitbox = pdrawRangedHitbox.getBoolean();
            hitboxDrawRange = (float) phitboxDrawRange.getDouble();

        }

        if (saveFieldsToConfig) {

            final double[] fkcHUDarray = fkcounterHUDPosition.getRelativePosition();
            pXpos_fkcHUD.set(fkcHUDarray[0]);
            pYpos_fkcHUD.set(fkcHUDarray[1]);
            pshow_fkcHUD.set(showfkcounterHUD);
            pcompactHUD.set(fkcounterHUDCompact);
            pshow_players.set(fkcounterHUDShowPlayers);
            pinSidebar.set(fkcounterHUDinSidebar);
            pdraw_background.set(fkcounterHUDDrawBackground);
            ptext_shadow.set(fkcounterHUDTextShadow);
            pfkc_hud_size.set(fkcounterHUDSize);
            pPlayerAmount.set(fkcounterHUDPlayerAmount);
            pfinalsInTablist.set(fkcounterHUDTablist);

            pAPIKey.set(APIKey);
            phypixelNick.set(hypixelNick);
            pstrengthParticules.set(strengthParticules);
            pShortencoinmessage.set(shortCoinMessage);
            pPlaySoundLowHP.set(playSoundLowHP);
            pHealthThreshold.set(healthThreshold);
            pCancelNightVisionEffect.set(!keepNightVisionEffect);
            puseColoredScores.set(useColoredScores);
            psafeInventory.set(safeInventory);
            plimitDroppedEntityRendered.set(limitDroppedEntityRendered);
            pmaxDroppedEntityRendered.set(maxDroppedEntityRendered);
            pprestigeV.set(prestigeV);
            phideRepetitiveMWChatMsg.set(hideRepetitiveMWChatMsg);
            pclearVision.set(clearVision);
            pautomaticUpdate.set(automaticUpdate);
            pNickHider.set(nickHider);
            phideTablistHeaderFooter.set(hideTablistHeaderFooter);
            pshowPlayercountTablist.set(showPlayercountTablist);
            ptablistSize.set(tablistSize);
            pfixActionbarTextOverlap.set(fixActionbarTextOverlap);

            pShow_killcooldownHUD.set(showKillCooldownHUD);
            final double[] killcooldownHUDarray = killCooldownHUDPosition.getRelativePosition();
            pXpos_killcooldownHUD.set(killcooldownHUDarray[0]);
            pYpos_killcooldownHUD.set(killcooldownHUDarray[1]);

            pShow_ArrowHitHUD.set(showArrowHitHUD);
            final double[] ArrowHitHUDarray = arrowHitHUDPosition.getRelativePosition();
            pXpos_ArrowHitHUD.set(ArrowHitHUDarray[0]);
            pYpos_ArrowHitHUD.set(ArrowHitHUDarray[1]);

            pShow_lastWitherHUD.set(showLastWitherHUD);
            pwitherHUDinSidebar.set(witherHUDinSidebar);
            final double[] lastWitherHUDarray = lastWitherHUDPosition.getRelativePosition();
            pXpos_lastWitherHUD.set(lastWitherHUDarray[0]);
            pYpos_lastWitherHUD.set(lastWitherHUDarray[1]);

            pHunterStrengthHUD.set(showStrengthHUD);
            final double[] hunterStrengtharray = hunterStrengthHUDPosition.getRelativePosition();
            pXpos_hunterHUD.set(hunterStrengtharray[0]);
            pYpos_hunterHUD.set(hunterStrengtharray[1]);

            pToggleicons.set(iconsOnNames);
            pTogglewarnings.set(warningMessages);
            pToggleautoreport.set(toggleAutoreport);
            preportsuggestions.set(reportSuggestions);
            pautoreportSuggestions.set(autoreportSuggestions);
            pdeleteReports.set(deleteOldReports);
            ptimeDeleteReport.set((int) (timeDeleteReport / (24L * 3600L * 1000L)));
            pcensorCheaterChatMsg.set(censorCheaterChatMsg);
            pdeleteCheaterChatMsg.set(deleteCheaterChatMsg);

            pisDebugHitboxOn.set(isDebugHitboxOn);
            pdrawHitboxForPlayers.set(drawHitboxForPlayers);
            pdrawHitboxForGroundedArrows.set(drawHitboxForGroundedArrows);
            pdrawHitboxForPinnedArrows.set(drawHitboxForPinnedArrows);
            pdrawHitboxForFlyingArrows.set(drawHitboxForFlyingArrows);
            pdrawHitboxForDroppedItems.set(drawHitboxForDroppedItems);
            pdrawHitboxForPassiveMobs.set(drawHitboxForPassiveMobs);
            pdrawHitboxForAggressiveMobs.set(drawHitboxForAggressiveMobs);
            pdrawHitboxItemFrame.set(drawHitboxItemFrame);
            pdrawHitboxForOtherEntity.set(drawHitboxForOtherEntity);
            pdrawRedBox.set(drawRedBox);
            pHideBlueVect.set(hideBlueVect);
            pdrawBlueVectForPlayersOnly.set(drawBlueVectForPlayersOnly);
            pmakeBlueVect3Meters.set(makeBlueVect3Meters);
            prealSizeHitbox.set(realSizeHitbox);
            pdrawRangedHitbox.set(drawRangedHitbox);
            phitboxDrawRange.set(hitboxDrawRange);

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
