package fr.alexdoru.mwe.config;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.gui.guiapi.GuiPosition;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigHandler extends AbstractConfig {

    private static ConfigHandler INSTANCE;

    private ConfigHandler(File file) {
        super(ConfigHandler.class, file);
    }

    public static void loadConfig(File file) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Config already created!");
        }
        INSTANCE = new ConfigHandler(file);
        ConfigHandler.onModUpdate();
    }

    public static void saveConfig() {
        if (INSTANCE == null) {
            throw new NullPointerException("Config didn't load when the game started, this shouldn't happen !");
        }
        INSTANCE.save();
    }

    private static void onModUpdate() {
        if (!modVersion.equals(MWE.version)) {
            if (ConfigHandler.lastWitherHUDPosition.getRelativeX() == 0.75d && ConfigHandler.lastWitherHUDPosition.getRelativeY() == 0.05d) {
                ConfigHandler.lastWitherHUDPosition.resetToDefault();
            }
            if (ConfigHandler.baseLocationHUDPosition.getRelativeX() == 0.5d && ConfigHandler.baseLocationHUDPosition.getRelativeY() == 0.15d) {
                ConfigHandler.baseLocationHUDPosition.resetToDefault();
            }
            ConfigHandler.modVersion = MWE.version;
            ConfigHandler.saveConfig();
        }
    }

    @ConfigProperty(
            category = "General",
            name = "Mod Version",
            comment = "The version of the mod the config was saved with")
    protected static String modVersion = "";

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "Show FKCounter HUD",
            comment = "Displays the HUD of the final kill counter")
    public static boolean showfkcounterHUD;

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "FKCounter HUD",
            comment = "position of the final kill counter HUD")
    public static final GuiPosition fkcounterHUDPosition = new GuiPosition(0d, 0.1d);

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "Compact FKCounter HUD",
            comment = "Use a compact HUD for the final kill counter")
    public static boolean fkcounterHUDCompact = true;

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "Show players",
            comment = "Displays players with most finals in each team")
    public static boolean fkcounterHUDShowPlayers;

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "HUD in sidebar",
            comment = "Places the fkcounter in the sidebar")
    public static boolean fkcounterHUDinSidebar = true;

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "Draw background",
            comment = "Draws a box around the HUD of the final kill counter")
    public static boolean fkcounterHUDDrawBackground;

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "HUD Size",
            comment = "Size of the final kill counter HUD")
    public static double fkcounterHUDSize = 1.0d;

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "Player amount",
            comment = "Amount of players displayed on screen when you use the \"Show players\" setting")
    public static int fkcounterHUDPlayerAmount = 3;

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "Fks in tablist",
            comment = "Draws the finals in the tablist")
    public static boolean fkcounterHUDTablist = true;

    @ConfigProperty(
            category = "Final Kill Counter",
            name = "Kill diff in Chat",
            comment = "Kill diff in Chat")
    public static boolean showKillDiffInChat;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "APIKey",
            comment = "Your Hypixel API Key")
    public static String APIKey = "";

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Hypixel Nick",
            comment = "Your nick on Hypixel")
    public static String hypixelNick = "";

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Shorten coin message",
            comment = "Shorten the coins messages by removing the network booster info")
    public static boolean shortCoinMessage;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Sound low HP",
            comment = "Plays a sound when your health falls below a certain threshold")
    public static boolean playSoundLowHP;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Health Threshold",
            comment = "The health threshold at witch it will play a sound, value ranges from 0 to 1")
    public static double healthThreshold = 0.5d;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Cancel Night Vision Effect",
            comment = "Removes the visual effets of night vision")
    public static boolean cancelNightVisionEffect;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Colored Tablist Scores",
            comment = "Makes the scores in the tablist use a greend to red color gradient depending of the value")
    public static boolean useColoredScores = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Safe Inventory",
            comment = "Prevents sword dropping and hotkeying kit items")
    public static boolean safeInventory = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Limit dropped item rendered",
            comment = "Limit dropped item rendered")
    public static boolean limitDroppedEntityRendered = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Max amount of item rendered",
            comment = "Max amount of item rendered")
    public static int maxDroppedEntityRendered = 80;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Delete repetitive chat messages in mw",
            comment = "Delete repetitive chat messages in mw")
    public static boolean hideRepetitiveMWChatMsg = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Hide hunger title in MW",
            comment = "Hide hunger title in MW")
    public static boolean hideHungerTitleInMW = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Clear Vision",
            comment = "Hides particles too close to the camera")
    public static boolean clearVision = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Automatic Update",
            comment = "Updates the mod automatically")
    public static boolean automaticUpdate = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Nick Hider",
            comment = "Shows your real name instead of your nick when in squad")
    public static boolean nickHider = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Hide Header Footer Tablist",
            comment = "Hides the header and footer text in the Tablist")
    public static boolean hideTablistHeaderFooter;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Show Header Footer outside MW",
            comment = "Show Header Footer outside of mega walls")
    public static boolean showHeaderFooterOutsideMW;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Show playercount Tablist",
            comment = "Shows the amount of players in the lobby at the top of the Tablist")
    public static boolean showPlayercountTablist = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Tablist size",
            comment = "Amount of players displayed in the tablist (Vanilla 80)")
    public static int tablistSize = 100;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Fix actionbar text overlap",
            comment = "Prevents the actionbar text from overlapping with the armor bar")
    public static boolean fixActionbarTextOverlap = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Hide ping tablist",
            comment = "Don't render the ping in the tablist if all values are equal to 1")
    public static boolean hidePingTablist = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Color when an entity gets hurt",
            comment = "Color when an entity gets hurt")
    public static int hitColor = 0x4CFF0000;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Color armor when hurt",
            comment = "Color armor when hurt")
    public static boolean colorArmorWhenHurt = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Use team color when hurt",
            comment = "Use team color when hurt")
    public static boolean useTeamColorWhenHurt;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Show pined arrows as renegade",
            comment = "Show pined arrows as renegade above player heads")
    public static boolean renegadeArrowCount;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Hide Orange's Toggle Sprint text",
            comment = "Hide Orange's Toggle Sprint text")
    public static boolean hideToggleSprintText;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Colored scores above head",
            comment = "Colored scores above head")
    public static boolean coloredScoreAboveHead;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Pink squadmates",
            comment = "Colors your squadmates' nametag, hitbox and hit color pink")
    public static boolean pinkSquadmates = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Keep first letter squadnames",
            comment = "Keeps the first letter of squadnames to be able to track them with compass")
    public static boolean keepFirstLetterSquadnames = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Chat Heads",
            comment = "Renders heads of players in front of chat messages")
    public static boolean chatHeads = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Hide Optifine Hats",
            comment = "Stops rendering the hats added by Optifine")
    public static boolean hideOptifineHats;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Show Squad Icons",
            comment = "Display squad icon on names of squad members")
    public static boolean squadIconOnNames = true;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Squad Icons In Tab Only",
            comment = "Display squad icon only in the tablist")
    public static boolean squadIconTabOnly;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "Colored Leather Armor",
            comment = "Changes iron armor to team colored leather armor")
    public static boolean coloredLeatherArmor;

    @ConfigProperty(
            category = "MegaWallsEnhancements",
            name = "AFK sound warning",
            comment = "Plays sound when you are about to get kicked for AFK in Mega Walls")
    public static boolean afkSoundWarning = true;

    @ConfigProperty(
            category = "GUI",
            name = "Show kill cooldown HUD",
            comment = "Displays the cooldown for the /kill command when in MegaWalls")
    public static boolean showKillCooldownHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "kill cooldown HUD",
            comment = "position of the killcooldown HUD")
    public static final GuiPosition killCooldownHUDPosition = new GuiPosition(0d, 0d);

    @ConfigProperty(
            category = "GUI",
            name = "Show Arrow Hit HUD",
            comment = "Displays the HP of opponents on arrow hits")
    public static boolean showArrowHitHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "Arrow Hit HUD",
            comment = "position of the ArrowHitHUD")
    public static final GuiPosition arrowHitHUDPosition = new GuiPosition(0.5d, 9d / 20d);

    @ConfigProperty(
            category = "GUI",
            name = "Show head on Arrow Hit HUD",
            comment = "Show head of player hit on Arrow Hit HUD")
    public static boolean showHeadOnArrowHitHUD;

    @ConfigProperty(
            category = "GUI",
            name = "Show last wither HUD",
            comment = "Displays the time it takes for the last wither to die")
    public static boolean showLastWitherHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "last wither HUD",
            comment = "position of the LastWitherHUD")
    public static final GuiPosition lastWitherHUDPosition = new GuiPosition(0.75d, 0d);

    @ConfigProperty(
            category = "GUI",
            name = "Wiher HUD in sidebar",
            comment = "Displays the time it takes for the last wither to die in the sidebar")
    public static boolean witherHUDinSidebar = true;

    @ConfigProperty(
            category = "GUI",
            name = "Show Strength HUD",
            comment = "Displays HUD and plays a sound 10 seconds before getting strength with hunter")
    public static boolean showStrengthHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "Strength HUD",
            comment = "position of the Strength HUD")
    public static final GuiPosition strengthHUDPosition = new GuiPosition(0.5d, 8d / 20d);

    @ConfigProperty(
            category = "GUI",
            name = "Squad HUD",
            comment = "Displays a mini-tablist with only your squadmates")
    public static boolean showSquadHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "squad HUD",
            comment = "position of the squad HUD")
    public static final GuiPosition squadHUDPosition = new GuiPosition(0.25d, 0d);

    @ConfigProperty(
            category = "GUI",
            name = "Creeper Primed TNT HUD",
            comment = "Displays HUD showing the cooldown on your primed tnt with creeper")
    public static boolean showPrimedTNTHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "creeper primed TNT HUD",
            comment = "position of the Creeper Primed TNT HUD")
    public static final GuiPosition creeperTNTHUDPosition = new GuiPosition(0.5d, 8d / 20d);

    @ConfigProperty(
            category = "GUI",
            name = "Energy Display HUD",
            comment = "Displays HUD showing your current energy when you hit someone")
    public static boolean showEnergyDisplayHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "energy display HUD",
            comment = "position of the Energy Display HUD")
    public static final GuiPosition energyDisplayHUDPosition = new GuiPosition(0.5d, 10.5 / 20d);

    @ConfigProperty(
            category = "GUI",
            name = "Threshold to make energy display aqua",
            comment = "The threshold number that when hit will cause the energy display to turn aqua")
    public static int aquaEnergyDisplayThreshold = 100;

    @ConfigProperty(
            category = "GUI",
            name = "Speed HUD",
            comment = "Displays your own speed")
    public static boolean showSpeedHUD;

    @ConfigProperty(
            category = "GUI",
            name = "Speed HUD position",
            comment = "position of the speed HUD")
    public static final GuiPosition speedHUDPosition = new GuiPosition(1d, 1d);

    @ConfigProperty(
            category = "GUI",
            name = "Phoenix Bond HUD",
            comment = "Displays the hearts healed from phoenix bond")
    public static boolean showPhxBondHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "Phoenix Bond HUD position",
            comment = "position of the phoenix bond HUD")
    public static final GuiPosition phxBondHUDPosition = new GuiPosition(0.5d, 0.75d);

    @ConfigProperty(
            category = "GUI",
            name = "Base Location HUD",
            comment = "Displays in which base you are located in Mega Walls")
    public static boolean showBaseLocationHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "Base Location HUD position",
            comment = "position of the base location HUD")
    public static final GuiPosition baseLocationHUDPosition = new GuiPosition(0.90d, 0d);

    @ConfigProperty(
            category = "GUI",
            name = "Mini Potion HUD",
            comment = "Displays a minimalist potion HUD")
    public static boolean showMiniPotionHUD;

    @ConfigProperty(
            category = "GUI",
            name = "Mini Potion HUD position",
            comment = "position of the mini potion HUD")
    public static final GuiPosition miniPotionHUDPosition = new GuiPosition(0.5d, 7.5d / 20d);

    @ConfigProperty(
            category = "GUI",
            name = "Mini Potion HUD only in MW",
            comment = "Displays a minimalist potion HUD only in MW")
    public static boolean showMiniPotionHUDOnlyMW;

    @ConfigProperty(
            category = "GUI",
            name = "Show warcry HUD",
            comment = "Displays a HUD with the cooldown of the warcry in Mega Walls")
    public static boolean showWarcryHUD = true;

    @ConfigProperty(
            category = "GUI",
            name = "Warcry HUD position",
            comment = "position of the warcry HUD")
    public static final GuiPosition warcryHUDPosition = new GuiPosition(0.65d, 1d);

    @ConfigProperty(
            category = "NoCheaters",
            name = "Show Warning Icons",
            comment = "Display warning icon on names of reported players")
    public static boolean warningIconsOnNames = true;

    @ConfigProperty(
            category = "NoCheaters",
            name = "Warning Icons In Tab Only",
            comment = "Display warning icon only in the tablist")
    public static boolean warningIconsTabOnly;

    @ConfigProperty(
            category = "NoCheaters",
            name = "List of cheats considered blatant",
            comment = "Players reported with theses cheats will appear with a red icon on their name")
    public static final List<String> redIconCheats = new ArrayList<>(Arrays.asList("autoblock", "bhop", "fastbreak", "noslowdown", "scaffold"));

    @ConfigProperty(
            category = "NoCheaters",
            name = "Toggle Warnings",
            comment = "Gives warning messages in chat for reported players")
    public static boolean warningMessages;

    @ConfigProperty(
            category = "NoCheaters",
            name = "Report suggestion",
            comment = "Give report suggestions in the chat based on messages in shouts")
    public static boolean reportSuggestions = true;

    @ConfigProperty(
            category = "NoCheaters",
            name = "Delete Old Report",
            comment = "Deletes reports older than the specified value")
    public static boolean deleteOldReports;

    @ConfigProperty(
            category = "NoCheaters",
            name = "Time delete reports",
            comment = "Reports older than this will be deleted on game start (days)")
    public static int timeDeleteReport = 365;

    @ConfigProperty(
            category = "NoCheaters",
            name = "Censor Cheater Chat",
            comment = "Censors chat messages sent by reported cheaters")
    public static boolean censorCheaterChatMsg;

    @ConfigProperty(
            category = "NoCheaters",
            name = "Delete Cheater Chat",
            comment = "Deletes chat messages sent by reported cheaters")
    public static boolean deleteCheaterChatMsg;

    @ConfigProperty(
            category = "NoCheaters",
            name = "Report HUD",
            comment = "Displays when the mod has reports to send")
    public static boolean showReportHUD = true;

    @ConfigProperty(
            category = "NoCheaters",
            name = "Report HUD in chat only",
            comment = "Displays report hud only in chat")
    public static boolean showReportHUDonlyInChat;

    @ConfigProperty(
            category = "NoCheaters",
            name = "Report HUD position",
            comment = "position of the report HUD")
    public static final GuiPosition reportHUDPosition = new GuiPosition(0d, 1d);

    @ConfigProperty(
            category = "HackerDetector",
            name = "Hacker Detector",
            comment = "Detects cheaters in your game")
    public static boolean hackerDetector = true;

    public static boolean debugLogging;

    @ConfigProperty(
            category = "HackerDetector",
            name = "Add to report list",
            comment = "Adds flagged players to your report list")
    public static boolean addToReportList = true;

    @ConfigProperty(
            category = "HackerDetector",
            name = "Show flag messages",
            comment = "Prints a message in chat when a player flags")
    public static boolean showFlagMessages = true;

    @ConfigProperty(
            category = "HackerDetector",
            name = "Show flag message type",
            comment = "Additionally prints the type of flag on the alert message")
    public static boolean showFlagMessageType = true;

    @ConfigProperty(
            category = "HackerDetector",
            name = "Compact alerts",
            comment = "Compact flag messages with previous ones")
    public static boolean compactFlagMessages = true;

    @ConfigProperty(
            category = "HackerDetector",
            name = "One message per game",
            comment = "Prints flag message for each player once per game")
    public static boolean oneFlagMessagePerGame;

    @ConfigProperty(
            category = "HackerDetector",
            name = "Report flagged players",
            comment = "Sends a report for flagged players")
    public static boolean autoreportFlaggedPlayers = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Toggle hitbox",
            comment = "Toggle hitbox when starting game")
    public static boolean isDebugHitboxOn;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox for players",
            comment = "Hitbox for players")
    public static boolean drawHitboxForPlayers = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox for grounded arrows",
            comment = "Hitbox for grounded arrows")
    public static boolean drawHitboxForGroundedArrows = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox for pinned arrows",
            comment = "Hitbox for pinned arrows")
    public static boolean drawHitboxForPinnedArrows = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox for flying arrows",
            comment = "Hitbox for flying arrows")
    public static boolean drawHitboxForFlyingArrows = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox for dropped items",
            comment = "Hitbox for dropped items")
    public static boolean drawHitboxForDroppedItems = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox for passive mobs",
            comment = "Hitbox for passive mobs")
    public static boolean drawHitboxForPassiveMobs = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox for aggressive mobs",
            comment = "Hitbox for aggressive mobs")
    public static boolean drawHitboxForAggressiveMobs = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox for item frame",
            comment = "Hitbox for item frame")
    public static boolean drawHitboxItemFrame = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox for other entity",
            comment = "Hitbox for other entity")
    public static boolean drawHitboxForOtherEntity = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Draw red box",
            comment = "Draw red box")
    public static boolean drawRedBox = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Draw blue vector",
            comment = "Draw blue vector")
    public static boolean drawBlueVect = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Blue vect for players only",
            comment = "Blue vect for players only")
    public static boolean drawBlueVectForPlayersOnly;

    @ConfigProperty(
            category = "Hitbox",
            name = "Make blue vector 3m long",
            comment = "Make blue vector 3m long")
    public static boolean makeBlueVect3Meters;

    @ConfigProperty(
            category = "Hitbox",
            name = "Real size hitbox",
            comment = "Make hitbox their real size")
    public static boolean realSizeHitbox;

    @ConfigProperty(
            category = "Hitbox",
            name = "Don't render close hitbox",
            comment = "Doesn't render the hitbox of entities close to you")
    public static boolean hideCloseHitbox;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox render range",
            comment = "Doesn't render the hitbox of entities closer than this")
    public static double hitboxDrawRange = 6f;

    @ConfigProperty(
            category = "Hitbox",
            name = "Team colored hitbox",
            comment = "Makes the player hitboxes take the color of the player's team")
    public static boolean teamColoredHitbox = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Team colored arrow hitbox",
            comment = "Makes the hitbox of arrows take the color of the shooter's team")
    public static boolean teamColoredArrowHitbox = true;

    @ConfigProperty(
            category = "Hitbox",
            name = "Hitbox Color",
            comment = "A custom color for the hitboxes")
    public static int hitboxColor = 0xFFFFFF;

}
