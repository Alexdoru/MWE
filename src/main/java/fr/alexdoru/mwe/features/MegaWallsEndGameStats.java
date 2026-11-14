package fr.alexdoru.mwe.features;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.enums.MWClass;
import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.http.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.parsers.hypixel.LoginData;
import fr.alexdoru.mwe.http.parsers.hypixel.MegaWallsClassStats;
import fr.alexdoru.mwe.http.requests.HypixelPlayerData;
import fr.alexdoru.mwe.utils.DateUtil;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class MegaWallsEndGameStats {

    public static final String MW_GAME_END_STATS_COMMAND = "/gamestatsmw";

    private MWClass selectedClass;
    private MegaWallsClassStats startGameStats;
    private long timestampGameStart;
    private IChatComponent endGameStatsMessage;

    @SubscribeEvent
    public void onMwGame(MegaWallsGameEvent event) {
        if (event.type == Type.GAME_START) {
            this.onGameStart();
        } else if (event.type == Type.GAME_END) {
            new DelayedTask(this::onGameEnd, 15 * 20);
        }
    }

    private void onGameStart() {
        this.selectedClass = null;
        this.startGameStats = null;
        this.timestampGameStart = System.currentTimeMillis();
        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            return;
        }
        final EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        if (thePlayer == null) {
            return;
        }
        final MWSkin skin = MWSkin.ofPlayer(thePlayer);
        if (skin == null || skin.mwClass == null) {
            return;
        }
        final MWClass mwClass = skin.mwClass;
        final String uuid = thePlayer.getUniqueID().toString();
        final long gameStart = this.timestampGameStart;
        MultithreadingUtil.addTaskToQueue(() -> {
            final MegaWallsClassStats stats;
            try {
                stats = new MegaWallsClassStats(new HypixelPlayerData(uuid).getPlayerData(), mwClass.className);
            } catch (ApiException ignored) {
                return;
            }
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (this.timestampGameStart == gameStart) {
                    this.selectedClass = mwClass;
                    this.startGameStats = stats;
                }
            });
        });
    }

    private void onGameEnd() {
        final MWClass mwClass = this.selectedClass;
        final MegaWallsClassStats startStats = this.startGameStats;
        if (HypixelApiKeyUtil.apiKeyIsNotSetup() || mwClass == null || startStats == null) {
            return;
        }
        final EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        if (thePlayer == null) {
            return;
        }
        this.startGameStats = null;
        final String uuid = thePlayer.getUniqueID().toString();
        final String gameDuration = DateUtil.timeSince(this.timestampGameStart);
        MultithreadingUtil.addTaskToQueue(() -> {
            final IChatComponent message;
            try {
                final JsonObject playerData = new HypixelPlayerData(uuid).getPlayerData();
                final MegaWallsClassStats endGameStats = new MegaWallsClassStats(playerData, mwClass.className);
                endGameStats.minus(startStats);
                message = endGameStats.getGameStatMessage(new LoginData(playerData).getFormattedName(), gameDuration);
            } catch (ApiException ignored) {
                return;
            } catch (IllegalAccessException e) {
                MWE.logger.error("Failed to compute Mega Walls game stats", e);
                return;
            }
            Minecraft.getMinecraft().addScheduledTask(() -> {
                this.endGameStatsMessage = message;
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + "Click to view the stats of your " + EnumChatFormatting.AQUA + "Mega Walls " + EnumChatFormatting.YELLOW + "game!")
                        .setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, MW_GAME_END_STATS_COMMAND))));
            });
        });
    }

    public void printGameStatsMessage() {
        if (this.endGameStatsMessage != null) {
            ChatUtil.addChatMessage(this.endGameStatsMessage.createCopy());
        } else {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "No game stats available");
        }
    }

}
