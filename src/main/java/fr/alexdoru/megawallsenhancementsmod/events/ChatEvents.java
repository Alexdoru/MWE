package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.fkcountermod.events.MwGameEvent;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.fkcountermod.utils.MinecraftUtils;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.gui.ArrowHitGui;
import fr.alexdoru.megawallsenhancementsmod.gui.HunterStrengthGui;
import fr.alexdoru.megawallsenhancementsmod.gui.KillCooldownGui;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.nocheatersmod.commands.CommandReport;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
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
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class ChatEvents {

    public static final ResourceLocation REPORT_SUGGESTION_SOUND = new ResourceLocation("random.orb");
    public static final ResourceLocation STRENGTH_SOUND = new ResourceLocation("item.fireCharge.use"); // item.fireCharge.use  usefireworks.twinkle
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String BAN_MESSAGE = "A player has been removed from your game.";
    private static final String HUNGER_MESSAGE = "Get to the middle to stop the hunger!";
    private static final String HUNTER_STRENGTH_MESSAGE = "Your Force of Nature gave you a 5 second Strength I buff.";
    private static final String GENERAL_START_MESSAGE = "The game starts in 1 second!";
    private static final String OWN_WITHER_DEATH_MESSAGE = "Your wither has died. You can no longer respawn!";
    private static final String PREP_PHASE = "Prepare your defenses!";
    private static final Pattern API_KEY_PATTERN = Pattern.compile("^Your new API key is ([a-zA-Z0-9-]+)");
    private static final Pattern COINS_PATTERN = Pattern.compile("^\\+\\d+ coins!( \\((?:Triple Coins \\+ EXP, |)(?:Active Booster, |)\\w+'s Network Booster\\)).*");
    private static final Pattern HUNTER_PRE_STRENGTH_PATTERN = Pattern.compile("\u00a7a\u00a7lF\\.O\\.N\\. \u00a77\\(\u00a7l\u00a7c\u00a7lStrength\u00a77\\) \u00a7e\u00a7l([0-9]+)");
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^(?:|\\[SHOUT\\] |\\[SPECTATOR\\] )(?:|\\[[A-Z]{3,6}\\] )(?:|\\[(?:MV|VI)P\\+?\\+?\\] )(\\w{2,16}):.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORT_PATTERN1 = Pattern.compile("(\\w{2,16}) (?:|is )b?hop?ping", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORT_PATTERN2 = Pattern.compile("\\/?(?:wdr|report) (\\w{2,16}) (\\w+)", Pattern.CASE_INSENSITIVE);
    private static final List<StringLong> reportSuggestionList = new ArrayList<>();
    private static final long TIME_BETWEEN_REPORT_SUGGESTION_PLAYER = 40L * 60L * 1000L;
    private static long lastStrength = 0;

    private static boolean parseReportMessage(String messageSender, String msgIn) {

        if (ConfigHandler.reportsuggestions || ConfigHandler.autoreportSuggestions) {

            Matcher matcher1 = REPORT_PATTERN1.matcher(msgIn);
            Matcher matcher2 = REPORT_PATTERN2.matcher(msgIn);

            if (matcher1.find()) {
                String reportedPlayer = matcher1.group(1);
                if (isAValidName(reportedPlayer)) {
                    handleReportSuggestion(messageSender, reportedPlayer, "bhop");
                }
                return true;
            } else if (matcher2.find()) {
                String reportedPlayer = matcher2.group(1);
                String cheat = matcher2.group(2);
                if (isAValidCheat(cheat) && isAValidName(reportedPlayer)) {
                    handleReportSuggestion(messageSender, reportedPlayer, cheat);
                }
                return true;
            }

        }

        return false;

    }

    public static void playReportSuggestionSound(boolean playSound) {
        if (playSound) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(REPORT_SUGGESTION_SOUND, 1.0F));
        }
    }

    private static void handleReportSuggestion(String messageSender, String reportedPlayer, String cheat) {
        if (ConfigHandler.reportsuggestions) {
            playReportSuggestionSound(ConfigHandler.suggestionsSound);
            IChatComponent imsg = new ChatComponentText(getTagMW() + EnumChatFormatting.DARK_RED + "Command suggestion : ")
                    .appendSibling(makeReportButtons(reportedPlayer, "cheating", cheat, ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.SUGGEST_COMMAND));
            new DelayedTask(() -> addChatMessage(imsg), 0);
        }
        if (!FKCounterMod.isInMwGame || !ConfigHandler.autoreportSuggestions || mc.thePlayer == null || mc.thePlayer.getName().equals(reportedPlayer) || messageSender == null) {
            return;
        }
        if (mc.thePlayer.getName().equals(messageSender)) {
            if (FKCounterMod.isitPrepPhase) {
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Report suggestions aren't working before the walls fall."));
            }
            String[] args = new String[]{reportedPlayer, cheat};
            CommandWDR.handleWDRCommand(args, true);
            return;
        }
        if (!FKCounterMod.isitPrepPhase) {
            NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(messageSender);
            if (networkPlayerInfo == null) {
                return;
            }
            UUID id = networkPlayerInfo.getGameProfile().getId();
            if (!NameUtil.isRealPlayer(id)) {
                return;
            }
            String uuid = id.toString().replace("-", "");
            if (CommandScanGame.doesPlayerFlag(uuid)) {
                return;
            }
            WDR wdr = WdredPlayers.getWdredMap().get(uuid);
            if (wdr != null) {
                return;
            }
            wdr = WdredPlayers.getWdredMap().get(messageSender);
            if (wdr != null) {
                return;
            }
            if (canReportSuggestionPlayer(reportedPlayer)) {
                ReportQueue.INSTANCE.addPlayerToQueueRandom(reportedPlayer);
            }
        }
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
        if (event.getType() == MwGameEvent.EventType.GAME_START) {
            reportSuggestionList.clear();
        }
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {

        /*normal chat messages*/
        if (event.type == 0) {

            String msg = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());
            String fmsg = event.message.getFormattedText();

            /*
             *  cancels hunger message in mega walls
             */
            if (msg.equals(HUNGER_MESSAGE)) {
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

            if (msg.equals(PREP_PHASE)) {
                MinecraftForge.EVENT_BUS.post(new MwGameEvent(MwGameEvent.EventType.GAME_START));
                return;
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

            if (msg.equals(BAN_MESSAGE)) {
                new DelayedTask(() -> {
                    String recentlyDisconnectedPlayers = NetHandlerPlayClientHook.getRecentlyDisconnectedPlayers();
                    if (!recentlyDisconnectedPlayers.equals("")) {
                        addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.RED + "Player(s) disconnected : " + EnumChatFormatting.AQUA + recentlyDisconnectedPlayers).setChatStyle(new ChatStyle()
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Players disconnected in the last 2 seconds, click this message to run : \n\n" + EnumChatFormatting.YELLOW + "/stalk" + recentlyDisconnectedPlayers)))
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk " + recentlyDisconnectedPlayers))));
                    }
                }, 20);
                //return;
            }

            /*Status messages*/
        } else if (ConfigHandler.hunterStrengthHUD && event.type == 2) {
            String fmsg = event.message.getFormattedText();
            Matcher preStrengthMatcher = HUNTER_PRE_STRENGTH_PATTERN.matcher(fmsg);
            if (preStrengthMatcher.find()) {
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
