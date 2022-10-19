package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassSkinData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassStats;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.GuiScreenHook;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.events.MwGameEvent;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MegaWallsEndGameStats {

    private static final Pattern RANDOM_CLASS_PATTERN = Pattern.compile("^Random class: (\\w+)*");
    private static String selectedClass;
    private static boolean isRandom = false;
    /*Data downloaded at the start of the game*/
    private static MegaWallsClassStats mwClassStartGameStats;
    private static IChatComponent endGameStatsMessage;

    @SubscribeEvent
    public void onMwGame(MwGameEvent event) {
        if (event.getType() == MwGameEvent.EventType.GAME_START) {
            onGameStart();
        } else if (event.getType() == MwGameEvent.EventType.GAME_END) {
            new DelayedTask(MegaWallsEndGameStats::onGameEnd, 300);
        }
    }

    private static void onGameStart() {
        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            return;
        }
        Multithreading.addTaskToQueue(() -> {
            try {
                final EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
                if (thePlayer == null) {
                    return null;
                }
                final HypixelPlayerData playerdata = new HypixelPlayerData(thePlayer.getUniqueID().toString().replace("-", ""));
                if (!isRandom) {
                    selectedClass = new MegaWallsClassSkinData(playerdata.getPlayerData()).getCurrentmwclass().toLowerCase();
                }
                mwClassStartGameStats = new MegaWallsClassStats(playerdata.getPlayerData(), selectedClass);
            } catch (ApiException ignored) {}
            return null;
        });
        isRandom = false;
    }

    private static void onGameEnd() {
        if (HypixelApiKeyUtil.apiKeyIsNotSetup() || mwClassStartGameStats == null) {
            return;
        }
        Multithreading.addTaskToQueue(() -> {
            try {
                final EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
                if (thePlayer == null) {
                    return null;
                }
                final HypixelPlayerData playerdata = new HypixelPlayerData(thePlayer.getUniqueID().toString().replace("-", ""));
                final MegaWallsClassStats endGameStats = new MegaWallsClassStats(playerdata.getPlayerData(), selectedClass);
                endGameStats.minus(mwClassStartGameStats);
                final String formattedName = new LoginData(playerdata.getPlayerData()).getFormattedName();
                endGameStatsMessage = endGameStats.getGameStatMessage(formattedName);
                mwClassStartGameStats = null;
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + "Click to view the stats of your " + EnumChatFormatting.AQUA + "Mega Walls " + EnumChatFormatting.YELLOW + "game!")
                        .setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook.MW_GAME_END_STATS))));
            } catch (Exception ignored) {}
            return null;
        });
    }

    public static void printGameStatsMessage() {
        if (endGameStatsMessage != null) {
            ChatUtil.addChatMessage(endGameStatsMessage);
        } else {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "No game stats available");
        }
    }

    public static boolean processMessage(String msg) {
        final Matcher matcher = RANDOM_CLASS_PATTERN.matcher(msg);
        if (matcher.matches()) {
            selectedClass = matcher.group(1).toLowerCase();
            isRandom = true;
            return true;
        }
        return false;
    }

}
