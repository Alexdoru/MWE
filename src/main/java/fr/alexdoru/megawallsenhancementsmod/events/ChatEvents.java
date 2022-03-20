package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.fkcountermod.events.MwGameEvent;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.fkcountermod.utils.MinecraftUtils;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.gui.ArrowHitGui;
import fr.alexdoru.megawallsenhancementsmod.gui.HunterStrengthGui;
import fr.alexdoru.megawallsenhancementsmod.gui.KillCooldownGui;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.nocheatersmod.commands.CommandReport;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import fr.alexdoru.nocheatersmod.events.ReportQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class ChatEvents {

    public static final ResourceLocation reportSuggestionSound = new ResourceLocation("random.orb");
    public static final ResourceLocation strengthSound = new ResourceLocation("item.fireCharge.use"); // item.fireCharge.use  usefireworks.twinkle
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern HUNTER_PRE_STRENGTH_PATTERN = Pattern.compile(".*\u00a7a\u00a7lF\\.O\\.N\\. \u00a77\\(\u00a7l\u00a7c\u00a7lStrength\u00a77\\) \u00a7e\u00a7l([0-9]+).*");
    private static final String HUNTER_STRENGTH_MESSAGE = "Your Force of Nature gave you a 5 second Strength I buff.";
    private static final String GENERAL_START_MESSAGE = "The game starts in 1 second!";
    private static final String OWN_WITHER_DEATH_MESSAGE = "Your wither has died. You can no longer respawn!";
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^(?:|\\[SHOUT\\] |\\[SPECTATOR\\] )(?:|\\[[A-Z]+\\] )(?:|\\[[a-zA-Z0-9_+]{1,7}\\] )(\\w{2,16}):.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORT_PATTERN1 = Pattern.compile("^.+?(\\w{2,16}) (?:|is )b?hop?ping.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORT_PATTERN2 = Pattern.compile("^.+?(?:wdr|report) (\\w{2,16}) (\\w+).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern COINS_PATTERN = Pattern.compile("^\\+\\d+ coins!( \\((?:Triple Coins \\+ EXP, |)(?:Active Booster, |)\\w+'s Network Booster\\)).*");
    private static final Pattern API_KEY_PATTERN = Pattern.compile("^Your new API key is ([a-zA-Z0-9-]+)");
    private static final List<StringLong> reportSuggestionList = new ArrayList<>();
    private static final Random random = new Random();
    private static final long TIME_BETWEEN_REPORT_SUGGESTION_PLAYER = 20L * 60L * 1000L;
    private static long lastStrength = 0;

    private static boolean parseReportMessage(String messageSender, String msgIn) {

        if (ConfigHandler.reportsuggestions || ConfigHandler.autoreportSuggestions) {

            Matcher matcher1 = REPORT_PATTERN1.matcher(msgIn);
            Matcher matcher2 = REPORT_PATTERN2.matcher(msgIn);

            if (matcher1.matches()) {
                String name = matcher1.group(1);
                if (isAValidName(name)) {
                    handleReportSuggestion(name, "bhop");
                    handleAutoReportSuggestion(messageSender, name, "bhop");
                }
                return true;
            } else if (matcher2.matches()) {
                String name = matcher2.group(1);
                String cheat = matcher2.group(2);
                if (isAValidCheat(cheat) && isAValidName(name)) {
                    handleReportSuggestion(name, cheat);
                    handleAutoReportSuggestion(messageSender, name, cheat);
                }
                return true;
            }
        }

        return false;

    }

    private static void handleReportSuggestion(String playername, String cheat) {
        if (ConfigHandler.reportsuggestions) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(reportSuggestionSound, 1.0F));
            IChatComponent imsg = new ChatComponentText(getTagMW() + EnumChatFormatting.DARK_RED + "Command suggestion : ")
                    .appendSibling(makeReportButtons(playername, "cheating", cheat, ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.SUGGEST_COMMAND));
            new DelayedTask(() -> addChatMessage(imsg), 0);
        }
    }

    private static void handleAutoReportSuggestion(String messageSender, String reportedPlayer, String cheat) {
        if (!FKCounterMod.isInMwGame || !ConfigHandler.autoreportSuggestions || mc.thePlayer == null || mc.thePlayer.getName().equals(reportedPlayer) || messageSender == null) {
            return;
        }
        if (mc.thePlayer.getName().equals(messageSender)) {
            if(FKCounterMod.isitPrepPhase) {
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Report suggestions aren't working before the walls fall."));
            }
            ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/wdr " + reportedPlayer + " " + cheat);
            return;
        }
        if (!FKCounterMod.isitPrepPhase) {
            NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(messageSender);
            if (networkPlayerInfo == null) {
                return;
            }
            String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
            WDR wdr = WdredPlayers.getWdredMap().get(uuid);
            if (wdr != null) {
                return;
            }
            wdr = WdredPlayers.getWdredMap().get(messageSender);
            if (wdr != null) {
                return;
            }
            if (canReportSuggestionPlayer(reportedPlayer)) {
                sendAutoreportSuggestion(reportedPlayer);
            }
        }
    }

    private static void sendAutoreportSuggestion(String reportedPlayer) {
        ReportQueue.INSTANCE.addPlayerToQueue(reportedPlayer, 20 + random.nextInt(180));
    }

    private static boolean canReportSuggestionPlayer(String playername) {
        long timestamp = System.currentTimeMillis();
        reportSuggestionList.removeIf(o -> (o.timestamp + TIME_BETWEEN_REPORT_SUGGESTION_PLAYER < timestamp));
        for (StringLong stringLong : reportSuggestionList) {
            if (stringLong.message != null && stringLong.message.equals(playername)) {
                return false;
            }
        }
        reportSuggestionList.add(new StringLong(timestamp, playername));
        return true;
    }

    private static boolean isAValidName(String playername) {
        return NetHandlerPlayClientHook.playerInfoMap.get(playername) != null;
    }

    private static boolean isAValidCheat(String cheat) {
        return CommandReport.cheatsList.contains(cheat);
    }

    @SubscribeEvent
    public void onMWGameStart(MwGameEvent event) {
        if (event.getType() == MwGameEvent.EventType.GAME_START || event.getType() == MwGameEvent.EventType.DEATHMATCH_START) {
            reportSuggestionList.clear();
        }
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {

        String msg = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());
        String fmsg = event.message.getFormattedText();

        /*normal chat messages*/
        if (event.type == 0) {

            /*
             *  cancels hunger message in mega walls
             */
            if (msg.equals("Get to the middle to stop the hunger!")) {
                event.setCanceled(true);
                return;
            }

            if (msg.equals(GENERAL_START_MESSAGE)) {
                GameInfoGrabber.saveinfoOnGameStart();
                SquadEvent.formSquad();
                return;
            }

            if (msg.equals(OWN_WITHER_DEATH_MESSAGE)) {
                KillCooldownGui.hideGUI();
                return;
            }

            /*
             * shortens the coins messages removing the booster info
             */
            if (ConfigHandler.shortencoinmessage) {
                Matcher matchercoins = COINS_PATTERN.matcher(msg);
                if (matchercoins.matches()) {
                    event.message = new ChatComponentText(fmsg.replace(matchercoins.group(1), ""));
                    return;
                }
            }

            if (KillCounter.processMessage(fmsg, msg)) {
                event.setCanceled(true);
                return;
            }

            if (ConfigHandler.show_ArrowHitHUD && ArrowHitGui.processMessage(msg)) {
                return;
            }

            Matcher matcher = MESSAGE_PATTERN.matcher(msg);
            String messageSender = null;
            if (matcher.matches()) {
                messageSender = matcher.group(1);
                String squadmate = SquadEvent.getSquad().get(messageSender);
                if (squadmate != null) {
                    event.message = new ChatComponentText(fmsg.replaceFirst(messageSender, squadmate));
                }
            }

            if (parseReportMessage(messageSender, msg)) {
                return;
            }

            if (MWGameStatsEvent.processMessage(msg)) {
                return;
            }

            if (ConfigHandler.hunterStrengthHUD && msg.equals(HUNTER_STRENGTH_MESSAGE)) {
                HunterStrengthGui.instance.setStrengthRenderStart();
                return;
            }

            if (parseAPIKey(msg)) {
                return;
            }

            if (msg.equals("A player has been removed from your game.")) {
                new DelayedTask(() -> {
                    String recentlyDisconnectedPlayers = NetHandlerPlayClientHook.getRecentlyDisconnectedPlayers();
                    if (!recentlyDisconnectedPlayers.equals("")) {
                        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Player(s) recently disconnected :\n" + ChatUtil.getTagNoCheaters() + EnumChatFormatting.AQUA + recentlyDisconnectedPlayers).setChatStyle(new ChatStyle()
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Those are the players disconnected in the last 2 seconds, click this message to run : \n\n" + EnumChatFormatting.YELLOW + "/stalk" + recentlyDisconnectedPlayers)))
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk" + recentlyDisconnectedPlayers))));
                    }
                }, 20);
                //return;
            }

            /*Status messages*/
        } else if (ConfigHandler.hunterStrengthHUD && event.type == 2) {
            Matcher preStrengthMatcher = HUNTER_PRE_STRENGTH_PATTERN.matcher(fmsg);
            if (preStrengthMatcher.matches()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastStrength > 11000L) {
                    lastStrength = currentTime;
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(strengthSound, 0.0F));
                }
                String preStrengthTimer = preStrengthMatcher.group(1);
                HunterStrengthGui.instance.setPreStrengthTime(preStrengthTimer, currentTime);
            }
        }

    }

    /*
     * automatically sets up the api key on hypixel when you type /api new
     */
    private boolean parseAPIKey(String msg) {
        Matcher matcherapikey = API_KEY_PATTERN.matcher(msg);
        if (matcherapikey.matches()) {
            if (!MinecraftUtils.isHypixel()) {
                return false;
            }
            HypixelApiKeyUtil.setApiKey(matcherapikey.group(1));
            return true;
        }
        return false;
    }

}
