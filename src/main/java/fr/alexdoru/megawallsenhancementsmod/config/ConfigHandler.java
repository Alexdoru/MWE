package fr.alexdoru.megawallsenhancementsmod.config;

import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;

public class ConfigHandler extends AbstractConfig {

    private static final String FKCOUNTER = "Final Kill Counter";
    private static final String MWENHANCEMENTS = "MegaWallsEnhancements";
    private static final String GUI = "GUI";
    private static final String NOCHEATERS = "NoCheaters";
    private static final String HACKERDETECTOR = "HackerDetector";
    private static final String HITBOX = "Hitbox";

    /**
     * FKCounter config
     */
    @ConfigProperty(category = FKCOUNTER, name = "FKCounter HUD", comment = "position of the final kill counter HUD")
    public static final GuiPosition fkcounterHUDPosition = new GuiPosition(0d, 0.1d);

    @ConfigProperty(category = FKCOUNTER, name = "Show FKCounter HUD", comment = "Displays the HUD of the final kill counter")
    public static boolean showfkcounterHUD = true;

    @ConfigProperty(category = FKCOUNTER, name = "Compact FKCounter HUD", comment = "Use a compact HUD for the final kill counter")
    public static boolean fkcounterHUDCompact;

    @ConfigProperty(category = FKCOUNTER, name = "Show players", comment = "Displays players with most finals in each team")
    public static boolean fkcounterHUDShowPlayers;

    @ConfigProperty(category = FKCOUNTER, name = "HUD in sidebar", comment = "Places the fkcounter in the sidebar")
    public static boolean fkcounterHUDinSidebar;

    @ConfigProperty(category = FKCOUNTER, name = "Draw background", comment = "Draws a box around the HUD of the final kill counter")
    public static boolean fkcounterHUDDrawBackground;

    @ConfigProperty(category = FKCOUNTER, name = "Text shadow", comment = "Draws text shadow")
    public static boolean fkcounterHUDTextShadow = true;

    @ConfigProperty(category = FKCOUNTER, name = "HUD Size", comment = "Size of the final kill counter HUD")
    public static double fkcounterHUDSize = 1.0d;

    @ConfigProperty(category = FKCOUNTER, name = "Player amount", comment = "Amount of players displayed on screen when you use the \"Show players\" setting")
    public static int fkcounterHUDPlayerAmount = 3;

    @ConfigProperty(category = FKCOUNTER, name = "Fks in tablist", comment = "Draws the finals in the tablist")
    public static boolean fkcounterHUDTablist = true;

    /**
     * MWEnhancements config
     */
    @ConfigProperty(category = MWENHANCEMENTS, name = "APIKey", comment = "Your Hypixel API Key")
    public static String APIKey = "";

    @ConfigProperty(category = MWENHANCEMENTS, name = "Hypixel Nick", comment = "Your nick on Hypixel")
    public static String hypixelNick = "";

    @ConfigProperty(category = MWENHANCEMENTS, name = "Strength particules", comment = "Spawns strength particules when an herobrine or dreadlord get a final")
    public static boolean strengthParticules = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Shorten coin message", comment = "Shorten the coins messages by removing the network booster info")
    public static boolean shortCoinMessage = false;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Sound low HP", comment = "Plays a sound when your health falls below a certain threshold")
    public static boolean playSoundLowHP = false;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Health Threshold", comment = "The health threshold at witch it will play a sound, value ranges from 0 to 1")
    public static double healthThreshold = 0.5d;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Cancel Night Vision Effect", comment = "Removes the visual effets of night vision")
    public static boolean cancelNightVisionEffect = false;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Colored Tablist Scores", comment = "Makes the scores in the tablist use a greend to red color gradient depending of the value")
    public static boolean useColoredScores = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Safe Inventory", comment = "Prevents sword dropping and hotkeying kit items")
    public static boolean safeInventory = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Limit dropped item rendered", comment = "Limit dropped item rendered")
    public static boolean limitDroppedEntityRendered = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Max amount of item rendered", comment = "Max amount of item rendered")
    public static int maxDroppedEntityRendered = 80;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Prestige V colored Tag", comment = "Prestige V colored Tag")
    public static boolean prestigeV;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Delete repetitive chat messages in mw", comment = "Delete repetitive chat messages in mw")
    public static boolean hideRepetitiveMWChatMsg = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Clear Vision", comment = "Hides particles too close to the camera")
    public static boolean clearVision = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Automatic Update", comment = "Updates the mod automatically")
    public static boolean automaticUpdate = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Nick Hider", comment = "Shows your real name instead of your nick when in squad")
    public static boolean nickHider = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Hide Header Footer Tablist", comment = "Hides the header and footer text in the Tablist")
    public static boolean hideTablistHeaderFooter;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Show playercount Tablist", comment = "Shows the amount of players in the lobby at the top of the Tablist")
    public static boolean showPlayercountTablist = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Tablist size", comment = "Amount of players displayed in the tablist (Vanilla 80)")
    public static int tablistSize = 100;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Fix actionbar text overlap", comment = "Prevents the actionbar text from overlapping with the armor bar")
    public static boolean fixActionbarTextOverlap = true;

    @ConfigProperty(category = MWENHANCEMENTS, name = "Hide ping tablist", comment = "Don't render the ping in the tablist if all values are equal to 1")
    public static boolean hidePingTablist = true;

    /**
     * HUD config
     */
    @ConfigProperty(category = GUI, name = "Show kill cooldown HUD", comment = "Displays the cooldown for the /kill command when in MegaWalls")
    public static boolean showKillCooldownHUD = true;

    @ConfigProperty(category = GUI, name = "kill cooldown HUD", comment = "position of the killcooldown HUD")
    public static final GuiPosition killCooldownHUDPosition = new GuiPosition(0d, 0d);

    @ConfigProperty(category = GUI, name = "Show Arrow Hit HUD", comment = "Displays the HP of opponents on arrow hits")
    public static boolean showArrowHitHUD = true;

    @ConfigProperty(category = GUI, name = "Arrow Hit HUD", comment = "position of the ArrowHitHUD")
    public static final GuiPosition arrowHitHUDPosition = new GuiPosition(0.5d, 9d / 20d);

    @ConfigProperty(category = GUI, name = "Show last wither HUD", comment = "Displays the time it takes for the last wither to die")
    public static boolean showLastWitherHUD = true;

    @ConfigProperty(category = GUI, name = "Wiher HUD in sidebar", comment = "Displays the time it takes for the last wither to die in the sidebar")
    public static boolean witherHUDinSidebar = true;

    @ConfigProperty(category = GUI, name = "last wither HUD", comment = "position of the LastWitherHUD")
    public static final GuiPosition lastWitherHUDPosition = new GuiPosition(0.75d, 0.05d);

    @ConfigProperty(category = GUI, name = "Hunter Strength HUD", comment = "Displays HUD and plays a sound 10 seconds before getting strength with hunter")
    public static boolean showStrengthHUD = true;

    @ConfigProperty(category = GUI, name = "hunter Strength HUD", comment = "position of the Hunter Strength HUD")
    public static final GuiPosition hunterStrengthHUDPosition = new GuiPosition(0.5d, 8d / 20d);

    @ConfigProperty(category = GUI, name = "Squad HUD", comment = "Displays a mini-tablist with only your squadmates")
    public static boolean showSquadHUD = true;

    @ConfigProperty(category = GUI, name = "squad HUD", comment = "position of the squad HUD")
    public static final GuiPosition squadHUDPosition = new GuiPosition(0.25d, 0d);

    @ConfigProperty(category = GUI, name = "Creeper Primed TNT HUD", comment = "Displays HUD showing the cooldown on your primed tnt with creeper")
    public static boolean showPrimedTNTHUD = true;

    @ConfigProperty(category = GUI, name = "creeper primed TNT HUD", comment = "position of the Creeper Primed TNT HUD")
    public static final GuiPosition creeperTNTHUDPosition = new GuiPosition(0.5d, 8d / 20d);

    @ConfigProperty(category = GUI, name = "Energy Display HUD", comment = "Displays HUD showing your current energy when you hit someone")
    public static boolean showEnergyDisplayHUD;

    @ConfigProperty(category = GUI, name = "energy display HUD", comment = "position of the Energy Display HUD")
    public static final GuiPosition energyDisplayHUDPosition = new GuiPosition(0.5d, 10.5 / 20d);

    @ConfigProperty(category = GUI, name = "Threshold to make energy display aqua", comment = "The threshold number that when hit will cause the energy display to turn aqua")
    public static int aquaEnergyDisplayThreshold = 100;

    /**
     * NoCheaters Config
     */
    @ConfigProperty(category = NOCHEATERS, name = "Toggle Icons", comment = "Display warning symbol on nametags of reported players")
    public static boolean iconsOnNames = true;

    @ConfigProperty(category = NOCHEATERS, name = "Toggle Warnings", comment = "Gives warning messages in chat for reported players")
    public static boolean warningMessages;

    //public static boolean toggleAutoreport;
    //public static boolean stopAutoreportAfterWeek;@ConfigProperty(category = "", name = "", comment = "")

    @ConfigProperty(category = NOCHEATERS, name = "Report suggestion", comment = "Give report suggestions in the chat based on messages in shouts")
    public static boolean reportSuggestions = true;

    @ConfigProperty(category = NOCHEATERS, name = "Send report suggestions", comment = "Send report suggestions")
    public static boolean autoreportSuggestions = true;

    @ConfigProperty(category = NOCHEATERS, name = "Delete Old Report", comment = "Deletes reports older than the specified value")
    public static boolean deleteOldReports;

    @ConfigProperty(category = NOCHEATERS, name = "Time delete reports", comment = "Reports older than this will be deleted on game start (days)")
    public static int timeDeleteReport = 365;

    @ConfigProperty(category = NOCHEATERS, name = "Censor Cheater Chat", comment = "Censors chat messages sent by reported cheaters")
    public static boolean censorCheaterChatMsg;

    @ConfigProperty(category = NOCHEATERS, name = "Delete Cheater Chat", comment = "Deletes chat messages sent by reported cheaters")
    public static boolean deleteCheaterChatMsg;

    /**
     * Hacker Detector Config
     */
    @ConfigProperty(category = HACKERDETECTOR, name = "Hacker Detector", comment = "Detects cheaters in your game")
    public static boolean hackerDetector = true;

    @ConfigProperty(category = HACKERDETECTOR, name = "Enable debug log", comment = "Logs player failing checks in for debugging")
    public static boolean debugLogging;

    @ConfigProperty(category = HACKERDETECTOR, name = "Add to report list", comment = "Adds flagged players to your report list")
    public static boolean addToReportList = true;

    @ConfigProperty(category = HACKERDETECTOR, name = "Show flag messages", comment = "Prints a message in chat when a player flags")
    public static boolean showFlagMessages = true;

    @ConfigProperty(category = HACKERDETECTOR, name = "Compact alerts", comment = "Compact flag messages with previous ones")
    public static boolean compactFlagMessages = true;

    @ConfigProperty(category = HACKERDETECTOR, name = "One message per game", comment = "Prints flag message for each player once per game")
    public static boolean oneFlagMessagePerGame;

    @ConfigProperty(category = HACKERDETECTOR, name = "Report flagged players", comment = "Sends a report for flagged players")
    public static boolean autoreportFlaggedPlayers = true;

    /**
     * Hitbox Config
     */
    @ConfigProperty(category = HITBOX, name = "Toggle hitbox", comment = "Toggle hitbox when starting game")
    public static boolean isDebugHitboxOn;

    @ConfigProperty(category = HITBOX, name = "Hitbox for players", comment = "Hitbox for players")
    public static boolean drawHitboxForPlayers = true;

    @ConfigProperty(category = HITBOX, name = "Hitbox for grounded arrows", comment = "Hitbox for grounded arrows")
    public static boolean drawHitboxForGroundedArrows = true;

    @ConfigProperty(category = HITBOX, name = "Hitbox for pinned arrows", comment = "Hitbox for pinned arrows")
    public static boolean drawHitboxForPinnedArrows = true;

    @ConfigProperty(category = HITBOX, name = "Hitbox for flying arrows", comment = "Hitbox for flying arrows")
    public static boolean drawHitboxForFlyingArrows = true;

    @ConfigProperty(category = HITBOX, name = "Hitbox for dropped items", comment = "Hitbox for dropped items")
    public static boolean drawHitboxForDroppedItems = true;

    @ConfigProperty(category = HITBOX, name = "Hitbox for passive mobs", comment = "Hitbox for passive mobs")
    public static boolean drawHitboxForPassiveMobs = true;

    @ConfigProperty(category = HITBOX, name = "Hitbox for aggressive mobs", comment = "Hitbox for aggressive mobs")
    public static boolean drawHitboxForAggressiveMobs = true;

    @ConfigProperty(category = HITBOX, name = "Hitbox for item frame", comment = "Hitbox for item frame")
    public static boolean drawHitboxItemFrame = true;

    @ConfigProperty(category = HITBOX, name = "Hitbox for other entity", comment = "Hitbox for other entity")
    public static boolean drawHitboxForOtherEntity = true;

    @ConfigProperty(category = HITBOX, name = "Draw red box", comment = "Draw red box")
    public static boolean drawRedBox = true;

    @ConfigProperty(category = HITBOX, name = "Hide blue vector", comment = "Hide blue vector")
    public static boolean hideBlueVect = false;

    @ConfigProperty(category = HITBOX, name = "Blue vect for players only", comment = "Blue vect for players only")
    public static boolean drawBlueVectForPlayersOnly;

    @ConfigProperty(category = HITBOX, name = "Make blue vector 3m long", comment = "Make blue vector 3m long")
    public static boolean makeBlueVect3Meters;

    @ConfigProperty(category = HITBOX, name = "Real size hitbox", comment = "Make hitbox their real size")
    public static boolean realSizeHitbox;

    @ConfigProperty(category = HITBOX, name = "Don't render close hitbox", comment = "Doesn't render the hitbox of entities close to you")
    public static boolean drawRangedHitbox;

    @ConfigProperty(category = HITBOX, name = "Hitbox render range", comment = "Doesn't render the hitbox of entities closer than this")
    public static double hitboxDrawRange = 6f;

    @ConfigProperty(category = HITBOX, name = "Team colored hitbox", comment = "Makes the player hitboxes take the color of the player's team")
    public static boolean teamColoredHitbox;

}
