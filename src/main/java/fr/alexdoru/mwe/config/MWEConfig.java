package fr.alexdoru.mwe.config;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.mwe.chat.ChatHandler;
import fr.alexdoru.mwe.chat.LocrawListener;
import fr.alexdoru.mwe.config.lib.*;
import fr.alexdoru.mwe.features.LeatherArmorManager;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
import fr.alexdoru.mwe.gui.guiapi.GuiPosition;
import fr.alexdoru.mwe.nocheaters.ReportQueue;
import fr.alexdoru.mwe.nocheaters.WarningMessages;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.NameUtil;
import fr.alexdoru.mwe.utils.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MWEConfig extends AbstractConfig {

    private static AbstractConfig INSTANCE;

    private MWEConfig(File file) {
        super(MWEConfig.class, file);
    }

    public static void loadConfig(File file) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Config already created!");
        }
        INSTANCE = new MWEConfig(file);
        if (!MWEConfig.modVersion.equals(MWE.version)) {
            if (!MWEConfig.modVersion.isEmpty()) {
                MWEConfig.onModUpdate();
            }
            MWEConfig.modVersion = MWE.version;
            MWEConfig.saveConfig();
        }
    }

    public static void saveConfig() {
        if (INSTANCE == null) {
            throw new NullPointerException("Config didn't load when the game started, this shouldn't happen !");
        }
        INSTANCE.save();
    }

    public static void displayConfigGuiScreen() {
        if (INSTANCE == null) {
            throw new NullPointerException("Config didn't load when the game started, this shouldn't happen !");
        }
        new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(INSTANCE.getConfigGuiScreen()));
    }

    private static void onModUpdate() {
        // code to run on mod version update
    }

    @ConfigCategory(displayname = "§6Vanilla")
    public static final String VANILLA = "Vanilla";

    @ConfigCategory(displayname = "§8Hypixel")
    public static final String HYPIXEL = "Hypixel";

    @ConfigCategory(displayname = "§aMega Walls")
    public static final String MEGA_WALLS = "Mega Walls";

    @ConfigCategory(displayname = "§5PVP Stuff")
    public static final String PVP_STUFF = "PVP Stuff";

    @ConfigCategory(
            displayname = "§bFinal Kill Counter",
            comment = "For Mega Walls")
    public static final String FINAL_KILL_COUNTER = "Final Kill Counter";

    @ConfigCategory(
            displayname = "§2Squad",
            comment = "§fAdd players to your squad using the §e/squad§f command!")
    public static final String SQUAD = "Squad";

    @ConfigCategory(
            displayname = "§cNoCheaters",
            comment = "§fNoCheaters saves players reported via §e/wdr name§f (not /report) and warns you about them ingame."
                    + "§fTo remove a player from your report list use : §e/unwdr name§f or click the name on the warning message."
                    + "§fYou can see all the players you have reported using §e/nocheaters reportlist§f.")
    public static final String NOCHEATERS = "NoCheaters";

    @ConfigCategory(
            displayname = "§4Hacker Detector",
            comment = "§eDisclaimer : §fthis is not 100% accurate and can sometimes flag legit players, "
                    + "it won't flag every cheater either, however players that are regularly flagging are definitely cheating")
    public static final String HACKER_DETECTOR = "Hacker Detector";

    @ConfigCategory(
            displayname = "§9Hitboxes, better F3+b",
            comment = "§7You obviously need to press F3+b to enable hitboxes")
    public static final String HITBOXES = "Hitbox";

    @ConfigCategory(displayname = "§dExternal")
    public static final String EXTERNAL = "External";

    @ConfigProperty(
            category = "General",
            name = "Mod Version",
            comment = "The version of the mod the config was saved with",
            hidden = true)
    public static String modVersion = "";

    @ConfigProperty(
            category = "April Fools",
            name = "April Fun",
            comment = "Haha got u")
    public static boolean aprilFools = true;

    @ConfigPropertyHideOverride(name = "April Fun")
    public static boolean hideAprilFoolsSetting() {
        return !"01/04".equals(new SimpleDateFormat("dd/MM").format(new Date().getTime()));
    }

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat",
            name = "Chat Heads",
            comment = "Renders heads of players in front of chat messages")
    public static boolean chatHeads = true;

    @ConfigPropertyHideOverride(name = "Chat Heads")
    public static boolean hideChatHeadSetting() {
        return ASMLoadingPlugin.isFeatherLoaded();
    }

    @ConfigPropertyEvent(name = "Chat Heads")
    public static void onChatHeadsSetting() {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
    }

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat",
            name = "Longer chat",
            comment = "Extends the maximum amount of chat lines to 32 000 (Vanilla 100)")
    public static boolean longerChat = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat Copy",
            name = "Left click to copy chat messages",
            comment = "Left click to copy chat messages, it will do nothing if the message already has a click event")
    public static boolean leftClickChatCopy = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat Copy",
            name = "Right click to copy chat messages",
            comment = "Right click to copy chat messages")
    public static boolean rightClickChatCopy = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat Copy",
            name = "Shift click to copy one chat line",
            comment = "Hold shift while clicking a chat line to only copy one line of chat and not the whole message")
    public static boolean shiftClickChatLineCopy = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat Search",
            name = "Search box in chat",
            comment = "Adds a search box to search the chat")
    public static boolean searchBoxChat = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat Search",
            name = "Always render search box",
            comment = "Always renders the search box icon in the chat."
                    + " If the box is not rendered you can still use the chat search by pressing ctrl + F")
    public static boolean showSearchBoxUnfocused = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat Search",
            name = "Search box shortcuts",
            comment = "Enables using ctrl + F shortcut to enter chat search")
    public static boolean searchBoxChatShortcuts = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat Search",
            name = "Keep previous chat search",
            comment = "Keeps the previous chat search when you re-open the chat")
    public static boolean keepPreviousChatSearch;

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat Search",
            name = "Search box X offset",
            comment = "Allows to move the search box right and left. Positive values move to the right",
            sliderMin = -20, sliderMax = 400)
    public static int searchBoxXOffset = 0;

    @ConfigProperty(
            category = VANILLA, subCategory = "Chat Search",
            name = "Search box Y offset",
            comment = "Allows to move the search box up and down. Positive values move up",
            sliderMin = -20, sliderMax = 400)
    public static int searchBoxYOffset = 0;

    @ConfigProperty(
            category = VANILLA, subCategory = "Hurt Color",
            name = "Custom Hurt Color",
            comment = "Change the color entities take when they get hurt",
            isColor = true)
    public static int hitColor = 0x4CFF0000;

    @ConfigProperty(
            category = VANILLA, subCategory = "Hurt Color",
            name = "Color armor when hurt",
            comment = "The armor will be colored as well when a player is hurt, like it does in 1.7\n"
                    + "§eIf you have a 1.7 Old animation mod, you might need to turn off their \"Red Armor\" setting for this to work.")
    public static boolean colorArmorWhenHurt = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Hurt Color",
            name = "Team colored hurt color for players",
            comment = "When hurt the players will take the color of their team, other entities will take the custom color defined above.\n"
                    + "§eWhen this is enabled, it still uses the alpha level defined in the custom color.")
    public static boolean teamColoredPlayerHurt;

    @ConfigProperty(
            category = VANILLA, subCategory = "Hurt Color",
            name = "Team colored hurt color for withers",
            comment = "When hurt the withers will take the color of their name")
    public static boolean teamColoredWitherHurt = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Performance",
            name = "Limit dropped item rendered",
            comment = "Dynamically modifies the render distance of dropped items entities to preserve performance. It starts reducing the render distance when exceeding the threshold set below.\n"
                    + "There is a keybind (ESC -> options -> controls -> MWE) to toggle it")
    public static boolean limitDroppedEntityRendered = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Performance",
            name = "Max amount of dropped item",
            comment = "Max amount of item rendered",
            sliderMin = 40, sliderMax = 200)
    public static int maxDroppedEntityRendered = 80;

    @ConfigProperty(
            category = VANILLA, subCategory = "Render",
            name = "Cancel Night Vision Effect",
            comment = "Removes the visual effets of night vision")
    public static boolean cancelNightVisionEffect;

    @ConfigProperty(
            category = VANILLA, subCategory = "Render",
            name = "Clear View",
            comment = "Stops rendering particles that are too close (75cm) to the camera for a better visibility")
    public static boolean clearVision = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Render",
            name = "Colored health/scores above head",
            comment = "Renders the health/scores above head in color according to the score's value compared to the player's maximum health points\n"
                    + "\n"
                    + "    §222§c ❤\n"
                    + "    §a17§c ❤\n"
                    + "    §e12§c ❤\n"
                    + "    §c 7§c ❤\n"
                    + "    §4 2§c ❤\n")
    public static boolean coloredScoreAboveHead = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Tablist",
            name = "Colored health/scores in Tablist",
            comment = "Renders the health/scores in the tablist in color according to the score's value compared to the player's maximum health points\n"
                    + "\n"
                    + "§cOrangeMarshall " + "§222§c ❤\n"
                    + "§cOrangeMarshall " + "§a17§c ❤\n"
                    + "§cOrangeMarshall " + "§e12§c ❤\n"
                    + "§cOrangeMarshall " + "§c 7§c ❤\n"
                    + "§cOrangeMarshall " + "§4 2§c ❤\n")
    public static boolean coloredScoresInTablist = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Tablist",
            name = "Hide Header Footer Tablist",
            comment = "Hides the header and footer text at the top and bottom of the Tablist")
    public static boolean hideTablistHeaderFooter;

    @ConfigProperty(
            category = VANILLA, subCategory = "Tablist",
            name = "Hide Header Footer only in MW",
            comment = "Hides the header and footer text only when playing Mega Walls")
    public static boolean hideTablistHeaderFooterOnlyInMW;

    @ConfigProperty(
            category = VANILLA, subCategory = "Tablist",
            name = "Show playercount Tablist",
            comment = "Displays the amount of players in the lobby at the top of the tablist")
    public static boolean showPlayercountTablist = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Tablist",
            name = "Tablist size",
            comment = "Amount of players displayed in the tablist (Vanilla 80)",
            sliderMin = 60, sliderMax = 120)
    public static int tablistSize = 100;

    @ConfigPropertyHideOverride(name = "Tablist size")
    public static boolean hideTabSizeSetting() {
        return ASMLoadingPlugin.isPatcherLoaded();
    }

    @ConfigProperty(
            category = VANILLA, subCategory = "Tablist",
            name = "Hide ping tablist",
            comment = "Stops rendering the ping in the tablist when all values are equal to 1")
    public static boolean hidePingTablist = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Tablist",
            name = "Tablist column separator",
            comment = "Spacing between columns in the tablist, in pixels. (Vanilla 5)",
            sliderMin = 1, sliderMax = 20)
    public static int tablistColumnSpacing = 1;

    @ConfigProperty(
            category = VANILLA, subCategory = "Tablist",
            name = "De-obfuscate names in tab",
            comment = "Removes obfuscation in the names in the tablist")
    public static boolean deobfNamesInTab;

    @ConfigProperty(
            category = VANILLA, subCategory = "Tablist",
            name = "Show fake players in tab",
            comment = "Puts a red star next to fake player names")
    public static boolean showFakePlayersInTab;

    @ConfigProperty(
            category = VANILLA, subCategory = "Bugfix",
            name = "Fix actionbar text overlap",
            comment = "Prevents the actionbar text from overlapping with the armor bar if you have more than 2 rows of health")
    public static boolean fixActionbarTextOverlap = true;

    @ConfigProperty(
            category = VANILLA, subCategory = "Logs",
            name = "Clean chat logs",
            comment = "Removes formatting codes from the chat logs")
    public static boolean cleanChatLogs = true;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Armor HUD",
            name = "Armor HUD",
            comment = "Displays your currently equipped armor")
    public static final GuiPosition armorHUDPositon = new GuiPosition(false, 0.25d, 1d);

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Armor HUD",
            name = "Horizontal Armor HUD",
            comment = "Displays the Armor HUD horizontally")
    public static boolean horizontalArmorHUD = true;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Armor HUD",
            name = "Low Durability Armor HUD",
            comment = "Only renders the Armor HUD when the durability of one of your armor pieces falls below the threshold defined below")
    public static boolean lowDuraArmorHUD;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Armor HUD",
            name = "Low Durability threshold",
            sliderMax = 528)
    public static int lowDuraArmorHUDValue = 50;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Armor HUD",
            name = "Show Armor Durability",
            comment = "Renders the durability of each armor piece")
    public static boolean showArmorDurability = true;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Armor HUD",
            name = "Show Armor Durability as number",
            comment = "Renders the durability of each armor piece as colored numbers instead of a bar")
    public static boolean showArmorDurabilityAsNumber;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Potion HUD",
            name = "Mini Potion HUD",
            comment = "Displays a minimalist potion HUD with the remaining duration of the following potion buffs :"
                    + " §dregeneration§7, §8resistance§7, §bspeed§7, §cstrength§7, §finvisibility§7, §ajump boost§7")
    public static final GuiPosition miniPotionHUDPosition = new GuiPosition(false, 0.5d, 7.5d / 20d);

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Potion HUD",
            name = "Mini Potion HUD only in MW",
            comment = "Displays the mini potion HUD only in Mega Walls")
    public static boolean showMiniPotionHUDOnlyMW;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Potion HUD",
            name = "Potion HUD",
            comment = "Displays your potions effects")
    public static final GuiPosition potionHUDPosition = new GuiPosition(false, 0d, 0.5d);

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Potion HUD",
            name = "Horizontal Potion HUD",
            comment = "Displays the Potion HUD horizontally")
    public static boolean horizontalPotionHUD;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Potion HUD",
            name = "Show Potion names",
            comment = "Displays the full name of the potion effects next to the icons, only works with vertical display")
    public static boolean showPotionEffectNames;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Potion HUD",
            name = "Potion HUD Text Color",
            isColor = true)
    public static int potionHUDTextColor = 0xFFFFFF;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Toggle Sprint",
            name = "Toggle Sprint",
            comment = "Always sprint when you hold the forward key\n"
                    + "There is a keybind (ESC -> options -> controls -> MWE) to toggle it")
    public static boolean toggleSprint;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Health",
            name = "Sound warning low HP",
            comment = "Plays a sound when your health drops below the threshold defined below"
                    + "The sound used is \"note.pling\" check your sound settings to see if it's enabled !")
    public static boolean playSoundLowHP;

    @ConfigPropertyEvent(name = "Sound warning low HP")
    public static void onLowHPSoundSetting() {
        if (MWEConfig.playSoundLowHP) {
            SoundUtil.playLowHPSound();
        }
    }

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Health",
            name = "Health Threshold low HP",
            comment = "The health threshold under which it will play a sound",
            sliderMax = 1)
    public static double healthThreshold = 0.5d;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Inventory",
            name = "Prevent sword dropping",
            comment = "Prevents dropping the sword in your hand when pressing the drop key")
    public static boolean preventSwordDropping = true;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Other HUD",
            name = "Arrow Hit HUD",
            comment = "Displays the HP of opponents on arrow hits, also works with Spider Leap damage in Mega Walls and Renegade Rend")
    public static final GuiPosition arrowHitHUDPosition = new GuiPosition(true, 0.5d, 9d / 20d);

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Other HUD",
            name = "Show head on Arrow Hit HUD",
            comment = "Show head of player shot on the Arrow Hit HUD")
    public static boolean showHeadOnArrowHitHUD;

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Other HUD",
            name = "Speed HUD",
            comment = "Displays your own speed in the XZ plane")
    public static final GuiPosition speedHUDPosition = new GuiPosition(false, 1d, 1d);

    @ConfigProperty(
            category = PVP_STUFF, subCategory = "Other HUD",
            name = "Speed HUD Color",
            isColor = true)
    public static int speedHUDColor = 0x00AA00;

    @ConfigProperty(
            category = HITBOXES,
            name = "Hitbox enabled on start",
            hidden = true)
    public static boolean isDebugHitboxOn;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for players")
    public static boolean drawHitboxForPlayers = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for grounded arrows")
    public static boolean drawHitboxForGroundedArrows = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for pinned arrows")
    public static boolean drawHitboxForPinnedArrows = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for flying arrows")
    public static boolean drawHitboxForFlyingArrows = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for dropped items")
    public static boolean drawHitboxForDroppedItems = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for passive mobs")
    public static boolean drawHitboxForPassiveMobs = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for aggressive mobs")
    public static boolean drawHitboxForAggressiveMobs = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for withers")
    public static boolean drawHitboxForWithers = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for item frame")
    public static boolean drawHitboxItemFrame = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Render hitbox for : ",
            name = "Hitbox for other entity")
    public static boolean drawHitboxForOtherEntity = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Red box",
            name = "Red eye square",
            comment = "Renders a red square at the eye level of entities")
    public static boolean drawRedBox = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Blue Vector",
            name = "Render blue vector",
            comment = "Renders a blue line comming out of the eyes of entities that represent where they look at")
    public static boolean drawBlueVect = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Blue Vector",
            name = "Blue vect for players only",
            comment = "Renders the blue line for players only")
    public static boolean drawBlueVectForPlayersOnly;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Blue Vector",
            name = "Make blue vector 3m long",
            comment = "Make the blue vector 3 meters long, just like the player's attack reach")
    public static boolean makeBlueVect3Meters = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Color",
            name = "Hitbox Color",
            comment = "A custom color for the hitboxes",
            isColor = true)
    public static int hitboxColor = 0xFFFFFF;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Color",
            name = "Team colored arrow hitbox",
            comment = "For arrows, the hitbox will take the color of the shooter's team")
    public static boolean teamColoredArrowHitbox = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Color",
            name = "Team colored player hitbox",
            comment = "The hitbox of players will take the color of their team, other entities will have the custom color defined above.")
    public static boolean teamColoredPlayerHitbox = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Color",
            name = "Team colored wither hitbox",
            comment = "Wither hitboxes take the color of their team")
    public static boolean teamColoredWitherHitbox = true;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Other",
            name = "Real size hitbox",
            comment = "The hitboxes will be larger and accurately represent the hitbox where you can attack the entities")
    public static boolean realSizeHitbox;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Other",
            name = "Hide close hitbox",
            comment = "Stops rendering the hitboxes that are closer than the range defined below")
    public static boolean hideCloseHitbox;

    @ConfigProperty(
            category = HITBOXES, subCategory = "Other",
            name = "Hitbox render range",
            comment = "Doesn't render the hitbox of entities closer than this",
            sliderMax = 64)
    public static double hitboxDrawRange = 8f;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "General",
            name = "Colored leather armor",
            comment = "Changes iron armor worn by other players to colored leather armor matching their team color")
    public static boolean coloredLeatherArmor;

    @ConfigPropertyEvent(name = "Colored leather armor")
    public static void onColoredLeatherArmorSetting() {
        LeatherArmorManager.onSettingChange();
    }

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "General",
            name = "AFK sound warning",
            comment = "Plays a sound when you are about to get kicked for AFK as well as when the walls are about to fall and your game is tabbed out")
    public static boolean afkSoundWarning = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "General",
            name = "Strength particules",
            comment = "Spawns strength particules when a herobrine or dreadlord gets a kill")
    public static boolean strengthParticules = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "General",
            name = "Show pinned arrows as renegade",
            comment = "Renders above player heads the amount of arrows pinned in each player when playing renegade")
    public static boolean renegadeArrowCount = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "General",
            name = "Squad add halo player",
            comment = "Automatically adds to the squad the player you give your halo to")
    public static boolean squadHaloPlayer = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "Chat",
            name = "Print deathmatch damage in chat",
            comment = "Prints the deathmatch damage as a separate message in chat instead of having to hover over the message")
    public static boolean printDeathmatchDamageMessage = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "Chat",
            name = "Hide repetitive chat messages",
            comment = "Hides the following messages :\n"
                    + "\n"
                    + "§cGet to the center to stop the hunger\n"
                    + "§aYou broke your protected chest\n"
                    + "§eYour Salvaging skill returned your arrow to you!\n"
                    + "§eYour Efficiency skill got you an extra drop!")
    public static boolean hideRepetitiveMWChatMsg = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "Inventory",
            name = "Safe Inventory",
            comment = "Prevents hotkeying important kit items out of your inventory")
    public static boolean safeInventory = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "Render",
            name = "Render wither outline",
            comment = "Renders a colored outline around withers\n"
                    + "§cThis doesn't work with Optifine's Fast Render")
    public static boolean renderWitherOutline = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "Screen",
            name = "Hide hunger title",
            comment = "Hide the hunger message that appears in the middle of the screen during deathmatch")
    public static boolean hideHungerTitleInMW = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Kill cooldown HUD",
            comment = "Displays the cooldown of the /kill command")
    public static final GuiPosition killCooldownHUDPosition = new GuiPosition(true, 0d, 0d);

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Kill cooldown HUD Color",
            isColor = true)
    public static int killCooldownHUDColor = 0xAA0000;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Last wither HUD",
            comment = "Displays the time it takes for the last wither to die")
    public static final GuiPosition lastWitherHUDPosition = new GuiPosition(true, 0.75d, 0d);

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Last wither HUD in sidebar",
            comment = "Renders the Last wither HUD in the sidebar")
    public static boolean witherHUDinSidebar = true;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Strength HUD",
            comment = "Displays the duration of the strength effect when you have it or when you are about to have it with Hunter."
                    + " Works with Dreadlord, Herobrine, Hunter and Zombie.")
    public static final GuiPosition strengthHUDPosition = new GuiPosition(true, 0.5d, 8d / 20d);

    @ConfigPropertyEvent(name = "Strength HUD")
    public static void onStrengthHUDSetting() {
        if (MWEConfig.strengthHUDPosition.isEnabled()) {
            SoundUtil.playStrengthSound();
        }
    }

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Creeper primed TNT HUD",
            comment = "Displays the cooldown of the primed TNT when playing Creeper")
    public static final GuiPosition creeperTNTHUDPosition = new GuiPosition(true, 0.5d, 8d / 20d);

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Energy display HUD",
            comment = "Displays a HUD with the amount of energy you have. Turns a different color when your energy level exceeds the amount set below.")
    public static final GuiPosition energyHUDPosition = new GuiPosition(true, 0.5d, 10.5 / 20d);

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "High energy threshold",
            sliderMax = 160)
    public static int highEnergyThreshold = 100;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Low energy color",
            isColor = true)
    public static int lowEnergyHUDColor = 0x55FF55;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "High energy color",
            isColor = true)
    public static int highEnergyHUDColor = 0x55FFFF;

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Phoenix Bond HUD",
            comment = "Displays the hearts healed from a Phoenix bond")
    public static final GuiPosition phxBondHUDPosition = new GuiPosition(true, 0.5d, 0.75d);

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Base Location HUD",
            comment = "Displays in which base you are currently located")
    public static final GuiPosition baseLocationHUDPosition = new GuiPosition(true, 0.90d, 0d);

    @ConfigPropertyEvent(name = "Base Location HUD")
    public static void onBaseLocationSetting() {
        if (MWEConfig.baseLocationHUDPosition.isEnabled() && ScoreboardTracker.isInMwGame()) {
            LocrawListener.setMegaWallsMap();
        }
    }

    @ConfigProperty(
            category = MEGA_WALLS, subCategory = "HUD",
            name = "Warcry HUD",
            comment = "Displays the warcry cooldown")
    public static final GuiPosition warcryHUDPosition = new GuiPosition(true, 0.65d, 1d);

    @ConfigProperty(
            category = FINAL_KILL_COUNTER, subCategory = "HUD",
            name = "Final Kill Counter HUD",
            comment = "Displays the HUD of the final kill counter\n"
                    + "§cThis will only work if you have your Hypixel language set to English")
    public static final GuiPosition fkcounterHUDPosition = new GuiPosition(false, 0d, 0.1d);

    @ConfigProperty(
            category = FINAL_KILL_COUNTER, subCategory = "HUD",
            name = "Compact mode",
            comment = "Use a compact HUD for the final kill counter")
    public static boolean fkcounterHUDCompact = true;

    @ConfigProperty(
            category = FINAL_KILL_COUNTER, subCategory = "HUD",
            name = "Compact HUD in Sidebar",
            comment = "Renders the final kill counter HUD in the sidebar")
    public static boolean fkcounterHUDinSidebar = true;

    @ConfigProperty(
            category = FINAL_KILL_COUNTER, subCategory = "HUD",
            name = "Players mode",
            comment = "Displays players with most finals in each team")
    public static boolean fkcounterHUDShowPlayers;

    @ConfigProperty(
            category = FINAL_KILL_COUNTER, subCategory = "HUD",
            name = "Player amount",
            comment = "Amount of players displayed on screen when you use the Players mode",
            sliderMin = 1, sliderMax = 10)
    public static int fkcounterHUDPlayerAmount = 3;

    @ConfigPropertyEvent(name = {
            "Final Kill Counter HUD",
            "Compact mode",
            "Compact HUD in Sidebar",
            "Players mode",
            "Player amount"})
    public static void onFKSHUDSetting() {
        GuiManager.fkCounterHUD.updateDisplayText();
    }

    @ConfigProperty(
            category = FINAL_KILL_COUNTER, subCategory = "HUD",
            name = "Render HUD background",
            comment = "Renders a background behind the final kill counter HUD")
    public static boolean fkcounterHUDDrawBackground;

    @ConfigProperty(
            category = FINAL_KILL_COUNTER, subCategory = "Tablist",
            name = "Finals in tablist",
            comment = "Renders in the tablist next to their names the amount of final kills that each player has")
    public static boolean fkcounterHUDTablist = true;

    @ConfigProperty(
            category = FINAL_KILL_COUNTER, subCategory = "Chat",
            name = "Show kill diff in chat",
            comment = "Appends at the end of kill messages the amount of final kills the killed player had")
    public static boolean showKillDiffInChat = true;

    @ConfigProperty(
            category = HYPIXEL,
            name = "APIKey",
            comment = "Your Hypixel API Key",
            hidden = true)
    public static String APIKey = "";

    @ConfigProperty(
            category = HYPIXEL,
            name = "Hypixel Nick",
            comment = "Your nick on Hypixel",
            hidden = true)
    public static String hypixelNick = "";

    @ConfigProperty(
            category = HYPIXEL,
            name = "Short coin messages",
            comment = "Makes the §6coins §7and §2tokens§7 messages shorter by removing the network booster info. It also compacts the guild bonus message and coin message into one.\n"
                    + "\n"
                    + "§6+100 coins! (hypixel's Network booster) §bFINAL KILL\n"
                    + "§fWill become : \n"
                    + "§6+100 coins!§b FINAL KILL")
    public static boolean shortCoinMessage;

    @ConfigProperty(
            category = HYPIXEL,
            name = "Warp Protection",
            comment = "Adds confirmation when clicking the \"Play Again\" paper if you have players in your squad")
    public static boolean warpProtection = true;

    @ConfigProperty(
            category = NOCHEATERS, subCategory = "General",
            name = "Warning messages in chat",
            comment = "Prints a warning message in chat when a reported player joins your world, these messages have built in compact chat")
    public static boolean warningMessages;

    @ConfigPropertyEvent(name = "Warning messages in chat")
    public static void onWarningMessageSetting() {
        if (MWEConfig.warningMessages) {
            WarningMessages.printReportMessagesForWorld(false);
        } else {
            ChatHandler.deleteAllWarningMessages();
        }
    }

    @ConfigProperty(
            category = NOCHEATERS, subCategory = "General",
            name = "Show banned players",
            comment = "Reveals the name of the player getting disconnected after a ban when playing on hypixel")
    public static boolean showBannedPlayers = true;

    @ConfigProperty(
            category = NOCHEATERS, subCategory = "General",
            name = "Delete Old Report",
            comment = "Deletes reports older than the specified value, the deletion occurs when you start game")
    public static boolean deleteOldReports;

    @ConfigProperty(
            category = NOCHEATERS, subCategory = "General",
            name = "Time delete reports",
            comment = "Reports older than this will be deleted on game start (days)",
            sliderMin = 1, sliderMax = 365 * 2)
    public static int timeDeleteReport = 365;

    @ConfigProperty(
            category = NOCHEATERS, subCategory = "Icons",
            name = "Show Warning Icons",
            comment = "Displays a warning icon in front of names of reported players on their nametags and in the tablist\n"
                    + "\n"
                    + "§4§l⚠ §r§7: players reported for blatant cheats\n"
                    + "§e§l⚠ §r§7: players reported for other cheats\n"
                    + "\n"
                    + "You can define in the config file the lists of cheats that give a red icon and cheats that don't give any icon")
    public static boolean warningIconsOnNames = true;

    @ConfigProperty(
            category = NOCHEATERS, subCategory = "Icons",
            name = "Warning Icons In Tab Only",
            comment = "Displays the warning icons in the tablist only, not on nametags")
    public static boolean warningIconsTabOnly;

    @ConfigPropertyEvent(name = {
            "Show fake players in tab",
            "De-obfuscate names in tab",
            "Show Squad Icons",
            "Squad Icons In Tab Only",
            "Show Warning Icons",
            "Warning Icons In Tab Only",
            "Pink squadmates"})
    public static void refreshAllNames() {
        NameUtil.refreshAllNamesInWorld();
    }

    @ConfigProperty(
            category = NOCHEATERS, subCategory = "Chat",
            name = "Report suggestions",
            comment = "Highlights chat messages that calls out a player for cheating. It will match the messages that respect the following patterns :\n"
                    + "\n"
                    + "§aPlayer: §fplayername is bhoping\n"
                    + "§aPlayer: §fwdr playername cheat\n"
                    + "§aPlayer: §freport playername cheat")
    public static boolean reportSuggestions = true;

    @ConfigPropertyEvent(name = "Report suggestions")
    public static void onReportSuggestionSetting() {
        if (MWEConfig.reportSuggestions) {
            SoundUtil.playChatNotifSound();
        }
    }

    @ConfigProperty(
            category = NOCHEATERS, subCategory = "Chat",
            name = "Censor cheaters messages in chat",
            comment = "Censors chat messages sent by reported players")
    public static boolean censorCheaterChatMsg;

    @ConfigProperty(
            category = NOCHEATERS, subCategory = "Chat",
            name = "Delete cheaters messages in chat",
            comment = "Deletes chat messages sent by reported players")
    public static boolean deleteCheaterChatMsg;

    @ConfigProperty(
            category = NOCHEATERS,
            name = "List of cheats that give a red icon",
            comment = "Players reported with one of theses cheats will appear with a red icon on their name",
            hidden = true)
    public static final List<String> redIconCheats = new ArrayList<>(Arrays.asList("autoblock", "bhop", "fastbreak", "noslowdown", "scaffold"));

    @ConfigProperty(
            category = NOCHEATERS,
            name = "List of cheats that don't give an icon",
            comment = "Players reported with only theses cheats will have no icon on their name",
            hidden = true)
    public static final List<String> noIconCheats = new ArrayList<>();

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "General",
            name = "Hacker Detector",
            comment = "Analyses movements and actions of players around you")
    public static boolean hackerDetector = true;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "General",
            name = "Add to report list",
            comment = "Saves flagged players in NoCheaters to get warnings about them")
    public static boolean addToReportList = true;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Flags",
            name = "Show flag messages",
            comment = "Prints a message in chat when it detects a player using cheats")
    public static boolean showFlagMessages = true;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Flags",
            name = "Show flag type",
            comment = "Shows the flag type on the flag message. For example : Killaura(A), Killaura(B)...")
    public static boolean showFlagMessageType = true;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Flags",
            name = "Compact flags in chat",
            comment = "Deletes previous flag message when printing a new identical flag message")
    public static boolean compactFlagMessages = true;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Flags",
            name = "Show single flag message",
            comment = "Prints flag messages only once per game per player")
    public static boolean oneFlagMessagePerGame;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Flags",
            name = "Sound when flagging",
            comment = "Plays a sound when it flags a player")
    public static boolean soundWhenFlagging;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Flags",
            name = "Show report button on flags",
            comment = "Shows the report buttons on flag messages")
    public static boolean showReportButtonOnFlags = true;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Flags",
            name = "Flag message prefix",
            comment = "Lets you chose the prefix of flags messages",
            hidden = true)
    public static String flagMessagePrefix = EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_GRAY + "NoCheaters" + EnumChatFormatting.GOLD + "]";

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Report",
            name = "Auto-report cheaters",
            comment = "Sends a /report automatically to Hypixel when it flags a cheater\n"
                    + "§eOnly works in Mega Walls, sends one report per game per player, you need to stand still for the mod to type the report." +
                    " It will not send the report if you wait more than 30 seconds to send it.")
    public static boolean autoreportFlaggedPlayers = true;

    @ConfigPropertyEvent(name = "Auto-report cheaters")
    public static void onAutoreportSetting() {
        if (!MWEConfig.autoreportFlaggedPlayers) {
            ReportQueue.INSTANCE.queueList.clear();
        }
    }

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Report",
            name = "Report HUD",
            comment = "Displays a small text when the mod has reports to send to the server and when it is typing the report")
    public static final GuiPosition reportHUDPosition = new GuiPosition(true, 0d, 1d);

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Report",
            name = "Report HUD in chat only",
            comment = "Displays the report HUD only when the chat is open")
    public static boolean showReportHUDonlyInChat;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Bypass",
            name = "Fix autoblock animation bypass",
            comment = "Patches a bypass that allows cheaters to not appear to be blocking their sword when they are in fact blocking and attacking at the same time")
    public static boolean fixAutoblockAnimationBypass = true;

    public static boolean debugLogging;

    @ConfigProperty(
            category = HACKER_DETECTOR, subCategory = "Debug",
            name = "Replay Killaura Flags",
            comment = "Prints a chat message whenever someone attacks another player through blocks, only works in replay, the attack detection system isn't accurate outside of replay")
    public static boolean debugKillauraFlags;

    @ConfigProperty(
            category = EXTERNAL,
            name = "Hide Optifine Hats",
            comment = "Hides the hats added by Optifine during Halloween and Christmas\n"
                    + "§eRequires game restart to be fully effective")
    public static boolean hideOptifineHats;

    @ConfigPropertyHideOverride(name = "Hide Optifine Hats")
    public static boolean hideOptifineHatsSetting() {
        return !FMLClientHandler.instance().hasOptifine();
    }

    @ConfigProperty(
            category = EXTERNAL,
            name = "Hide Orange's Toggle Sprint HUD",
            comment = "Hides the Toggle Sprint HUD from Orange's Marshall Simple Mod")
    public static boolean hideToggleSprintText;

    @ConfigPropertyHideOverride(name = "Hide Orange's Toggle Sprint HUD")
    public static boolean hideOrangeToggleSprintSetting() {
        return !Loader.isModLoaded("orangesimplemod");
    }

    @ConfigProperty(
            category = SQUAD, subCategory = "General",
            name = "Pink squadmates",
            comment = "Your squadmates will have a pink nametag, hitbox color and hurt color")
    public static boolean pinkSquadmates = true;

    @ConfigProperty(
            category = SQUAD, subCategory = "General",
            name = "Nick Hider",
            comment = "Shows your real name instead of your nick in the chat and tablist")
    public static boolean nickHider = true;

    @ConfigProperty(
            category = SQUAD, subCategory = "General",
            name = "Keep first letter squadname",
            comment = "§7When adding a player to the squad with a custom name of your choice,"
                    + " using§e /squad add <name> as <custom name>§7,"
                    + " it will keep the first letter of their real name so that you can track them on the compass")
    public static boolean keepFirstLetterSquadnames = true;

    @ConfigProperty(
            category = SQUAD, subCategory = "Icons",
            name = "Show Squad Icons",
            comment = "Displays a squad icon for squad members on their nametag and in the tablist\n"
                    + "\n"
                    + "§6[§2S§6] §r§7: players in your squad")
    public static boolean squadIconOnNames = true;

    @ConfigProperty(
            category = SQUAD, subCategory = "Icons",
            name = "Squad Icons In Tab Only",
            comment = "Displays the squad icons in the tablist only, not on nametags")
    public static boolean squadIconTabOnly;

    @ConfigProperty(
            category = SQUAD, subCategory = "HUD",
            name = "Squad HUD",
            comment = "Displays a mini-tablist with your squadmates")
    public static final GuiPosition squadHUDPosition = new GuiPosition(true, 0.25d, 0d);

    @ConfigProperty(
            category = "Updates",
            name = "Automatic Update",
            comment = "Updates the mod automatically")
    public static boolean automaticUpdate = true;

}
