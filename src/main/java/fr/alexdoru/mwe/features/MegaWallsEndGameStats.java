package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.asm.hooks.GuiScreenHook_CustomChatClickEvent;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.enums.MWClass;
import fr.alexdoru.mwe.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.http.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.parsers.hypixel.LoginData;
import fr.alexdoru.mwe.http.parsers.hypixel.MegaWallsClassSkinData;
import fr.alexdoru.mwe.http.parsers.hypixel.MegaWallsClassStats;
import fr.alexdoru.mwe.http.requests.HypixelPlayerData;
import fr.alexdoru.mwe.utils.DateUtil;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MegaWallsEndGameStats {

    private static final Pattern RANDOM_CLASS_PATTERN = Pattern.compile("^Random class: (\\w+)");
    private static MWClass selectedClass;
    private static boolean isRandom = false;
    /*Data downloaded at the start of the game*/
    private static MegaWallsClassStats mwClassStartGameStats;
    private static long timestampGameStart;
    private static IChatComponent endGameStatsMessage;

    @SubscribeEvent
    public void onMwGame(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_START) {
            onGameStart();
        } else if (event.getType() == MegaWallsGameEvent.EventType.GAME_END) {
            new DelayedTask(MegaWallsEndGameStats::onGameEnd, 300);
        }
    }

    private static void onGameStart() {
        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            return;
        }
        timestampGameStart = (new Date()).getTime();
        final EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        if (thePlayer == null) {
            return;
        }
        final String uuid = thePlayer.getUniqueID().toString();
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final HypixelPlayerData playerdata = new HypixelPlayerData(uuid);
                if (!isRandom) {
                    selectedClass = MWClass.fromTagOrName(new MegaWallsClassSkinData(playerdata.getPlayerData()).getCurrentmwclass());
                }
                if (selectedClass == null) {
                    return null;
                }
                mwClassStartGameStats = new MegaWallsClassStats(playerdata.getPlayerData(), selectedClass.className);
            } catch (ApiException ignored) {}
            isRandom = false;
            return null;
        });
    }

    private static void onGameEnd() {
        if (HypixelApiKeyUtil.apiKeyIsNotSetup() || mwClassStartGameStats == null || selectedClass == null) {
            return;
        }
        final EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        if (thePlayer == null) {
            return;
        }
        final String uuid = thePlayer.getUniqueID().toString();
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final HypixelPlayerData playerdata = new HypixelPlayerData(uuid);
                final MegaWallsClassStats endGameStats = new MegaWallsClassStats(playerdata.getPlayerData(), selectedClass.className);
                endGameStats.minus(mwClassStartGameStats);
                final String formattedName = new LoginData(playerdata.getPlayerData()).getFormattedName();
                final String gameDuration = DateUtil.timeSince(timestampGameStart);
                endGameStatsMessage = endGameStats.getGameStatMessage(formattedName, gameDuration);
                mwClassStartGameStats = null;
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + "Click to view the stats of your " + EnumChatFormatting.AQUA + "Mega Walls " + EnumChatFormatting.YELLOW + "game!")
                        .setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook_CustomChatClickEvent.MW_GAME_END_STATS))));
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
        if (matcher.find()) {
            selectedClass = MWClass.fromTagOrName(matcher.group(1));
            isRandom = true;
            return true;
        }
        return false;
    }

}
