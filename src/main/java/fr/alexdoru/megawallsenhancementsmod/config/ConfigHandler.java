package fr.alexdoru.megawallsenhancementsmod.config;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;

public class ConfigHandler extends AbstractConfig {

    private static final String FKCOUNTER = "Final Kill Counter";
    private static final String MWENHANCEMENTS = "MegaWallsEnhancements";
    private static final String GUI = "GUI";
    private static final String NOCHEATERS = "NoCheaters";
    private static final String HACKERDETECTOR = "HackerDetector";
    private static final String HITBOX = "Hitbox";

    protected static void onModUpdate() {
        if (!modVersion.equals(MegaWallsEnhancementsMod.version)) {
            ConfigHandler.hackerDetector = true;
            ConfigHandler.autoreportFlaggedPlayers = true;
            ConfigHandler.showReportHUD = true;
            ConfigHandler.showReportHUDonlyInChat = false;
            ConfigHandler.modVersion = MegaWallsEnhancementsMod.version;
            ConfigHandler.saveConfig();
        }
    }

    @ConfigProperty(
            category = "General",
            name = "Mod Version",
            comment = "The version of the mod the config was saved with")
    protected static String modVersion = "";

    @ConfigProperty(
            category = FKCOUNTER,
            name = "FKCounter HUD",
            comment = "position of the final kill counter HUD")
    public static final GuiPosition fkcounterHUDPosition = new GuiPosition(0d, 0.1d);

    @ConfigProperty(
            category = FKCOUNTER,
            name = "Show FKCounter HUD",
            comment = "Displays the HUD of the final kill counter")
    public static boolean showfkcounterHUD = true;

    @ConfigProperty(
            category = FKCOUNTER,
            name = "Compact FKCounter HUD",
            comment = "Use a compact HUD for the final kill counter")
    public static boolean fkcounterHUDCompact;

    @ConfigProperty(
            category = FKCOUNTER,
            name = "Show players",
            comment = "Displays players with most finals in each team")
    public static boolean fkcounterHUDShowPlayers;

    @ConfigProperty(
            category = FKCOUNTER,
            name = "HUD in sidebar",
            comment = "Places the fkcounter in the sidebar")
    public static boolean fkcounterHUDinSidebar;

    @ConfigProperty(
            category = FKCOUNTER,
            name = "Draw background",
            comment = "Draws a box around the HUD of the final kill counter")
    public static boolean fkcounterHUDDrawBackground;

    @ConfigProperty(
            category = FKCOUNTER,
            name = "HUD Size",
            comment = "Size of the final kill counter HUD")
    public static double fkcounterHUDSize = 1.0d;

    @ConfigProperty(
            category = FKCOUNTER,
            name = "Player amount",
            comment = "Amount of players displayed on screen when you use the \"Show players\" setting")
    public static int fkcounterHUDPlayerAmount = 3;

    @ConfigProperty(
            category = FKCOUNTER,
            name = "Fks in tablist",
            comment = "Draws the finals in the tablist")
    public static boolean fkcounterHUDTablist = true;

    @ConfigProperty(
            category = FKCOUNTER,
            name = "Kill diff in Chat",
            comment = "Kill diff in Chat")
    public static boolean showKillDiffInChat = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "APIKey",
            comment = "Your Hypixel API Key")
    public static String APIKey = "";

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Hypixel Nick",
            comment = "Your nick on Hypixel")
    public static String hypixelNick = "";

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Strength particules",
            comment = "Spawns strength particules when an herobrine or dreadlord get a final")
    public static boolean strengthParticules = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Shorten coin message",
            comment = "Shorten the coins messages by removing the network booster info")
    public static boolean shortCoinMessage;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Sound low HP",
            comment = "Plays a sound when your health falls below a certain threshold")
    public static boolean playSoundLowHP;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Health Threshold",
            comment = "The health threshold at witch it will play a sound, value ranges from 0 to 1")
    public static double healthThreshold = 0.5d;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Cancel Night Vision Effect",
            comment = "Removes the visual effets of night vision")
    public static boolean cancelNightVisionEffect;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Colored Tablist Scores",
            comment = "Makes the scores in the tablist use a greend to red color gradient depending of the value")
    public static boolean useColoredScores = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Safe Inventory",
            comment = "Prevents sword dropping and hotkeying kit items")
    public static boolean safeInventory = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Limit dropped item rendered",
            comment = "Limit dropped item rendered")
    public static boolean limitDroppedEntityRendered = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Max amount of item rendered",
            comment = "Max amount of item rendered")
    public static int maxDroppedEntityRendered = 80;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Prestige V colored Tag",
            comment = "Prestige V colored Tag")
    public static boolean prestigeV;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Delete repetitive chat messages in mw",
            comment = "Delete repetitive chat messages in mw")
    public static boolean hideRepetitiveMWChatMsg = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Hide hunger title in MW",
            comment = "Hide hunger title in MW")
    public static boolean hideHungerTitleInMW = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Clear Vision",
            comment = "Hides particles too close to the camera")
    public static boolean clearVision = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Automatic Update",
            comment = "Updates the mod automatically")
    public static boolean automaticUpdate = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Nick Hider",
            comment = "Shows your real name instead of your nick when in squad")
    public static boolean nickHider = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Hide Header Footer Tablist",
            comment = "Hides the header and footer text in the Tablist")
    public static boolean hideTablistHeaderFooter;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Show Header Footer outside MW",
            comment = "Show Header Footer outside of mega walls")
    public static boolean showHeaderFooterOutsideMW;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Show playercount Tablist",
            comment = "Shows the amount of players in the lobby at the top of the Tablist")
    public static boolean showPlayercountTablist = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Tablist size",
            comment = "Amount of players displayed in the tablist (Vanilla 80)")
    public static int tablistSize = 100;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Fix actionbar text overlap",
            comment = "Prevents the actionbar text from overlapping with the armor bar")
    public static boolean fixActionbarTextOverlap = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Hide ping tablist",
            comment = "Don't render the ping in the tablist if all values are equal to 1")
    public static boolean hidePingTablist = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Color when an entity gets hurt",
            comment = "Color when an entity gets hurt")
    public static int hitColor = 0x4CFF0000;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Color armor when hurt",
            comment = "Color armor when hurt")
    public static boolean colorArmorWhenHurt = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Use team color when hurt",
            comment = "Use team color when hurt")
    public static boolean useTeamColorWhenHurt;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Show pined arrows as renegade",
            comment = "Show pined arrows as renegade above player heads")
    public static boolean renegadeArrowCount;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Hide Orange's Toggle Sprint text",
            comment = "Hide Orange's Toggle Sprint text")
    public static boolean hideToggleSprintText;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Colored scores above head",
            comment = "Colored scores above head")
    public static boolean coloredScoreAboveHead;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Pink squadmates",
            comment = "Colors your squadmates' nametag, hitbox and hit color pink")
    public static boolean pinkSquadmates = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Keep first letter squadnames",
            comment = "Keeps the first letter of squadnames to be able to track them with compass")
    public static boolean keepFirstLetterSquadnames = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Chat Heads",
            comment = "Renders heads of players in front of chat messages")
    public static boolean chatHeads = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Hide Optifine Hats",
            comment = "Stops rendering the hats added by Optifine")
    public static boolean hideOptifineHats;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Show Squad Icons",
            comment = "Display squad icon on names of squad members")
    public static boolean squadIconOnNames = true;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Squad Icons In Tab Only",
            comment = "Display squad icon only in the tablist")
    public static boolean squadIconTabOnly;

    @ConfigProperty(
            category = MWENHANCEMENTS,
            name = "Colored Leather Armor",
            comment = "Changes iron armor to team colored leather armor")
    public static boolean coloredLeatherArmor;

    @ConfigProperty(
            category = GUI,
            name = "Show kill cooldown HUD",
            comment = "Displays the cooldown for the /kill command when in MegaWalls")
    public static boolean showKillCooldownHUD = true;

    @ConfigProperty(
            category = GUI,
            name = "kill cooldown HUD",
            comment = "position of the killcooldown HUD")
    public static final GuiPosition killCooldownHUDPosition = new GuiPosition(0d, 0d);

    @ConfigProperty(
            category = GUI,
            name = "Show Arrow Hit HUD",
            comment = "Displays the HP of opponents on arrow hits")
    public static boolean showArrowHitHUD = true;

    @ConfigProperty(
            category = GUI,
            name = "Show head on Arrow Hit HUD",
            comment = "Show head of player hit on Arrow Hit HUD")
    public static boolean showHeadOnArrowHitHUD;

    @ConfigProperty(
            category = GUI,
            name = "Arrow Hit HUD",
            comment = "position of the ArrowHitHUD")
    public static final GuiPosition arrowHitHUDPosition = new GuiPosition(0.5d, 9d / 20d);

    @ConfigProperty(
            category = GUI,
            name = "Show last wither HUD",
            comment = "Displays the time it takes for the last wither to die")
    public static boolean showLastWitherHUD = true;

    @ConfigProperty(
            category = GUI,
            name = "Wiher HUD in sidebar",
            comment = "Displays the time it takes for the last wither to die in the sidebar")
    public static boolean witherHUDinSidebar = true;

    @ConfigProperty(
            category = GUI,
            name = "last wither HUD",
            comment = "position of the LastWitherHUD")
    public static final GuiPosition lastWitherHUDPosition = new GuiPosition(0.75d, 0.05d);

    @ConfigProperty(
            category = GUI,
            name = "Hunter Strength HUD",
            comment = "Displays HUD and plays a sound 10 seconds before getting strength with hunter")
    public static boolean showStrengthHUD = true;

    @ConfigProperty(
            category = GUI,
            name = "hunter Strength HUD",
            comment = "position of the Hunter Strength HUD")
    public static final GuiPosition hunterStrengthHUDPosition = new GuiPosition(0.5d, 8d / 20d);

    @ConfigProperty(
            category = GUI,
            name = "Squad HUD",
            comment = "Displays a mini-tablist with only your squadmates")
    public static boolean showSquadHUD = true;

    @ConfigProperty(
            category = GUI,
            name = "squad HUD",
            comment = "position of the squad HUD")
    public static final GuiPosition squadHUDPosition = new GuiPosition(0.25d, 0d);

    @ConfigProperty(
            category = GUI,
            name = "Creeper Primed TNT HUD",
            comment = "Displays HUD showing the cooldown on your primed tnt with creeper")
    public static boolean showPrimedTNTHUD = true;

    @ConfigProperty(
            category = GUI,
            name = "creeper primed TNT HUD",
            comment = "position of the Creeper Primed TNT HUD")
    public static final GuiPosition creeperTNTHUDPosition = new GuiPosition(0.5d, 8d / 20d);

    @ConfigProperty(
            category = GUI,
            name = "Energy Display HUD",
            comment = "Displays HUD showing your current energy when you hit someone")
    public static boolean showEnergyDisplayHUD = true;

    @ConfigProperty(
            category = GUI,
            name = "energy display HUD",
            comment = "position of the Energy Display HUD")
    public static final GuiPosition energyDisplayHUDPosition = new GuiPosition(0.5d, 10.5 / 20d);

    @ConfigProperty(
            category = GUI,
            name = "Threshold to make energy display aqua",
            comment = "The threshold number that when hit will cause the energy display to turn aqua")
    public static int aquaEnergyDisplayThreshold = 100;

    @ConfigProperty(
            category = GUI,
            name = "Speed HUD",
            comment = "Displays your own speed")
    public static boolean showSpeedHUD;

    @ConfigProperty(
            category = GUI,
            name = "Speed HUD position",
            comment = "position of the speed HUD")
    public static final GuiPosition speedHUDPosition = new GuiPosition(1d, 1d);

    @ConfigProperty(
            category = GUI,
            name = "Phoenix Bond HUD",
            comment = "Displays the hearts healed from phoenix bond")
    public static boolean showPhxBondHUD = true;

    @ConfigProperty(
            category = GUI,
            name = "Phoenix Bond HUD position",
            comment = "position of the phoenix bond HUD")
    public static final GuiPosition phxBondHUDPosition = new GuiPosition(0.5d, 0.75d);

    @ConfigProperty(
            category = GUI,
            name = "Base Location HUD",
            comment = "Displays in which base you are located in Mega Walls")
    public static boolean showBaseLocationHUD = true;

    @ConfigProperty(
            category = GUI,
            name = "Base Location HUD position",
            comment = "position of the base location HUD")
    public static final GuiPosition baseLocationHUDPosition = new GuiPosition(0.5d, 0.15d);

    @ConfigProperty(
            category = GUI,
            name = "Mini Potion HUD",
            comment = "Displays a minimalist potion HUD")
    public static boolean showMiniPotionHUD;

    @ConfigProperty(
            category = GUI,
            name = "Mini Potion HUD only in MW",
            comment = "Displays a minimalist potion HUD only in MW")
    public static boolean showMiniPotionHUDOnlyMW;

    @ConfigProperty(
            category = GUI,
            name = "Mini Potion HUD position",
            comment = "position of the mini potion HUD")
    public static final GuiPosition miniPotionHUDPosition = new GuiPosition(0.5d, 7.5d / 20d);

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Show Warning Icons",
            comment = "Display warning icon on names of reported players")
    public static boolean warningIconsOnNames = true;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Warning Icons In Tab Only",
            comment = "Display warning icon only in the tablist")
    public static boolean warningIconsTabOnly;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Toggle Warnings",
            comment = "Gives warning messages in chat for reported players")
    public static boolean warningMessages;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Report suggestion",
            comment = "Give report suggestions in the chat based on messages in shouts")
    public static boolean reportSuggestions = true;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Delete Old Report",
            comment = "Deletes reports older than the specified value")
    public static boolean deleteOldReports;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Time delete reports",
            comment = "Reports older than this will be deleted on game start (days)")
    public static int timeDeleteReport = 365;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Censor Cheater Chat",
            comment = "Censors chat messages sent by reported cheaters")
    public static boolean censorCheaterChatMsg;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Delete Cheater Chat",
            comment = "Deletes chat messages sent by reported cheaters")
    public static boolean deleteCheaterChatMsg;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Report HUD",
            comment = "Displays when the mod has reports to send")
    public static boolean showReportHUD = true;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Report HUD in chat only",
            comment = "Displays report hud only in chat")
    public static boolean showReportHUDonlyInChat;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "Report HUD position",
            comment = "position of the report HUD")
    public static final GuiPosition reportHUDPosition = new GuiPosition(0d, 1d);

    @ConfigProperty(
            category = HACKERDETECTOR,
            name = "Hacker Detector",
            comment = "Detects cheaters in your game")
    public static boolean hackerDetector = true;

    public static boolean debugLogging = false;

    @ConfigProperty(
            category = HACKERDETECTOR,
            name = "Add to report list",
            comment = "Adds flagged players to your report list")
    public static boolean addToReportList = true;

    @ConfigProperty(
            category = HACKERDETECTOR,
            name = "Show flag messages",
            comment = "Prints a message in chat when a player flags")
    public static boolean showFlagMessages = true;

    @ConfigProperty(
            category = HACKERDETECTOR,
            name = "Show flag message type",
            comment = "Additionally prints the type of flag on the alert message")
    public static boolean showFlagMessageType = true;

    @ConfigProperty(
            category = HACKERDETECTOR,
            name = "Compact alerts",
            comment = "Compact flag messages with previous ones")
    public static boolean compactFlagMessages = true;

    @ConfigProperty(
            category = HACKERDETECTOR,
            name = "One message per game",
            comment = "Prints flag message for each player once per game")
    public static boolean oneFlagMessagePerGame;

    @ConfigProperty(
            category = HACKERDETECTOR,
            name = "Report flagged players",
            comment = "Sends a report for flagged players")
    public static boolean autoreportFlaggedPlayers = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Toggle hitbox",
            comment = "Toggle hitbox when starting game")
    public static boolean isDebugHitboxOn;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox for players",
            comment = "Hitbox for players")
    public static boolean drawHitboxForPlayers = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox for grounded arrows",
            comment = "Hitbox for grounded arrows")
    public static boolean drawHitboxForGroundedArrows = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox for pinned arrows",
            comment = "Hitbox for pinned arrows")
    public static boolean drawHitboxForPinnedArrows = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox for flying arrows",
            comment = "Hitbox for flying arrows")
    public static boolean drawHitboxForFlyingArrows = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox for dropped items",
            comment = "Hitbox for dropped items")
    public static boolean drawHitboxForDroppedItems = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox for passive mobs",
            comment = "Hitbox for passive mobs")
    public static boolean drawHitboxForPassiveMobs = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox for aggressive mobs",
            comment = "Hitbox for aggressive mobs")
    public static boolean drawHitboxForAggressiveMobs = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox for item frame",
            comment = "Hitbox for item frame")
    public static boolean drawHitboxItemFrame = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox for other entity",
            comment = "Hitbox for other entity")
    public static boolean drawHitboxForOtherEntity = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Draw red box",
            comment = "Draw red box")
    public static boolean drawRedBox = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Draw blue vector",
            comment = "Draw blue vector")
    public static boolean drawBlueVect = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Blue vect for players only",
            comment = "Blue vect for players only")
    public static boolean drawBlueVectForPlayersOnly;

    @ConfigProperty(
            category = HITBOX,
            name = "Make blue vector 3m long",
            comment = "Make blue vector 3m long")
    public static boolean makeBlueVect3Meters;

    @ConfigProperty(
            category = HITBOX,
            name = "Real size hitbox",
            comment = "Make hitbox their real size")
    public static boolean realSizeHitbox;

    @ConfigProperty(
            category = HITBOX,
            name = "Don't render close hitbox",
            comment = "Doesn't render the hitbox of entities close to you")
    public static boolean hideCloseHitbox;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox render range",
            comment = "Doesn't render the hitbox of entities closer than this")
    public static double hitboxDrawRange = 6f;

    @ConfigProperty(
            category = HITBOX,
            name = "Team colored hitbox",
            comment = "Makes the player hitboxes take the color of the player's team")
    public static boolean teamColoredHitbox = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Team colored arrow hitbox",
            comment = "Makes the hitbox of arrows take the color of the shooter's team")
    public static boolean teamColoredArrowHitbox = true;

    @ConfigProperty(
            category = HITBOX,
            name = "Hitbox Color",
            comment = "A custom color for the hitboxes")
    public static int hitboxColor = 0xFFFFFF;

}
