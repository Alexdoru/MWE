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
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class ChatEvents {

    // TODO faire une classe dédiée pour tous les bails d'autoreport

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
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^(?:|\\[SHOUT\\] |\\[SPECTATOR\\] )(?:|\\[[A-Z]{3,6}\\] )(?:|\\[((?:MV|VI)P\\+?\\+?)\\] )(\\w{2,16}):.*");
    private static final Pattern REPORT_PATTERN1 = Pattern.compile("(\\w{2,16}) (?:|is )b?hop?ping", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORT_PATTERN2 = Pattern.compile("\\/?(?:wdr|report) (\\w{2,16}) (\\w+)", Pattern.CASE_INSENSITIVE);
    private static final List<StringLong> reportSuggestionList = new ArrayList<>();
    private static final long TIME_BETWEEN_REPORT_SUGGESTION_PLAYER = 40L * 60L * 1000L;
    private static long lastStrength = 0;

    private static boolean parseReportMessage(@Nullable String senderRank, @Nullable String messageSender, @Nullable String squadname, String msgIn, String fmsgIn) {

        if (ConfigHandler.reportsuggestions || ConfigHandler.autoreportSuggestions) {

            Matcher matcher1 = REPORT_PATTERN1.matcher(msgIn);
            Matcher matcher2 = REPORT_PATTERN2.matcher(msgIn);

            if (matcher1.find()) {
                String reportText = matcher1.group();
                String reportedPlayer = matcher1.group(1);
                if (isAValidName(reportedPlayer)) {
                    handleReportSuggestion(reportedPlayer, senderRank, messageSender, squadname, reportText, "bhop", fmsgIn);
                } else {
                    addChatMessage(getIChatComponentWithSquadnameAsSender(fmsgIn, messageSender, squadname));
                }
                return true;
            } else if (matcher2.find()) {
                String reportText = matcher2.group();
                String reportedPlayer = matcher2.group(1);
                String cheat = matcher2.group(2);
                if (isAValidCheat(cheat) && isAValidName(reportedPlayer)) {
                    handleReportSuggestion(reportedPlayer, senderRank, messageSender, squadname, reportText, cheat, fmsgIn);
                } else {
                    addChatMessage(getIChatComponentWithSquadnameAsSender(fmsgIn, messageSender, squadname));
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

    // TODO mettre symbole petit check vert si le player peu envoyer un report
    //  petite croix rouge si il peut pas
    //  faire qu'on accepte qu'un seul reportSender par game ? so use it wiselly !

    /**
     * reportedPlayer is necessarily in the tablist
     */
    private static void handleReportSuggestion(String reportedPlayer, @Nullable String senderRank, @Nullable String messageSender, @Nullable String squadname, String reportText, String cheat, String fmsg) {

        // TODO ca fait quoi si le message sender est null ?
        // TODO check if target is myself, mynick included
        boolean isSenderMyself = isPlayerMyself(messageSender);
        boolean isTargetMyself = isPlayerMyself(reportedPlayer);
        boolean isSenderInTablist = false;
        boolean isSenderNicked = false;
        boolean isSenderFlaging = false;
        boolean isSenderIgnored = false;
        boolean isSenderCheating = false;
        /*Only accepts MVP, MVP+, MVP++*/
        boolean isSenderRankValid = false;

        if (isSenderMyself) {

            isSenderInTablist = true;

        } else {

            NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(messageSender);
            if (networkPlayerInfo != null) {

                isSenderInTablist = true;
                final UUID id = networkPlayerInfo.getGameProfile().getId();
                isSenderNicked = !NameUtil.isRealPlayer(id);
                final String uuid = id.toString().replace("-", "");
                isSenderFlaging = CommandScanGame.doesPlayerFlag(uuid);
                WDR wdr = WdredPlayers.getWdredMap().get(uuid);
                if (wdr != null) {
                    isSenderIgnored = wdr.isIgnored();
                    isSenderCheating = wdr.hasValidCheats();
                } else if (messageSender != null) {
                    wdr = WdredPlayers.getWdredMap().get(messageSender);
                    if (wdr != null) {
                        isSenderCheating = wdr.hasValidCheats();
                    }
                }

            }

        }

        // TODO accepter les VIP+ aussi ?
        if (senderRank != null && (senderRank.equals("MVP") || senderRank.equals("MVP+") || senderRank.equals("MVP++"))) {
            isSenderRankValid = true;
        }

        final boolean gotReported = sendReportSuggestion(messageSender, reportedPlayer, cheat, isSenderInTablist, isSenderNicked, isSenderFlaging, isSenderIgnored, isSenderCheating, isSenderRankValid);
        printCustomReportSuggestionChatText(fmsg, messageSender, reportedPlayer, cheat, reportText, squadname, isSenderMyself, isTargetMyself, isSenderInTablist, isSenderIgnored, isSenderCheating, isSenderFlaging, isSenderNicked, gotReported);

    }

    //TODO add support for own nick
    private static boolean isPlayerMyself(@Nullable String name) {
        return mc.thePlayer != null && mc.thePlayer.getName().equals(name);
    }

    private static void printCustomReportSuggestionChatText(String fmsg, @Nullable String messageSender, String reportedPlayer, String cheat, String reportText, @Nullable String squadname, boolean isSenderMyself, boolean isTargetMyself, boolean isSenderInTablist, boolean isSenderIgnored, boolean isSenderCheating, boolean isSenderFlaging, boolean isSenderNicked, boolean gotautoreported) {

        if (ConfigHandler.reportsuggestions) {

            playReportSuggestionSound(ConfigHandler.suggestionsSound && !isSenderIgnored && !isSenderCheating && !isSenderFlaging);

            if (isSenderInTablist && messageSender != null) {

                if (isSenderIgnored) {
                    // TODO bouton un-ignore ?
                    addChatMessage(new ChatComponentText(StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.GRAY + " (Ignored)", EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH.toString(), true)));
                    return;
                }

                if (isSenderCheating) {
                    addChatMessage(new ChatComponentText(StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.YELLOW + " (Cheater)", EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH.toString(), true)));
                    return;
                }

                if (isSenderFlaging) {
                    final String newFmsg = StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.LIGHT_PURPLE + " (Scangame)", "", true);
                    final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
                    if (FKCounterMod.isMWEnvironement && !isSenderMyself) {
                        imsg.appendSibling(getIgnoreButton(messageSender));
                    }
                    addReportButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotautoreported);
                    addChatMessage(imsg);
                    return;
                }

                if (isSenderNicked) {
                    final String s1 = StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.DARK_PURPLE + " (Nick)", "", false);
                    final String newFmsg = StringUtil.changeColorOf(s1, reportText, EnumChatFormatting.DARK_RED) + " ";
                    final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
                    addReportButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotautoreported);
                    addChatMessage(imsg);
                    return;
                }

                final String newFmsg = StringUtil.changeColorOf(fmsg, reportText, EnumChatFormatting.DARK_RED) + " ";
                final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
                if (FKCounterMod.isMWEnvironement && !isSenderMyself) {
                    imsg.appendSibling(getIgnoreButton(messageSender));
                }
                addReportButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotautoreported);
                addChatMessage(imsg);

            } else {

                final String newFmsg = StringUtil.changeColorOf(fmsg, reportText, EnumChatFormatting.DARK_RED) + " ";
                final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
                addReportButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotautoreported);
                addChatMessage(imsg);

            }

            return;

        }

        addChatMessage(getIChatComponentWithSquadnameAsSender(fmsg, messageSender, squadname));

    }

    private static void addReportButtons(IChatComponent imsg, String reportedPlayer, String cheat, boolean isSenderMyself, boolean isTargetMyself, boolean gotautoreported) {
        if (isTargetMyself) {
            return;
        }
        if (!gotautoreported) {
            imsg.appendSibling(getReportButton(reportedPlayer, "cheating", ClickEvent.Action.RUN_COMMAND));
        }
        if (!isSenderMyself || !gotautoreported) {
            imsg.appendSibling(getWDRButton(reportedPlayer, cheat, ClickEvent.Action.SUGGEST_COMMAND));
        }
    }

    private static IChatComponent getIChatComponentWithSquadnameAsSender(String fmsg, @Nullable String messageSender, @Nullable String squadname) {
        return new ChatComponentText(messageSender != null && squadname != null ? fmsg.replaceFirst(messageSender, squadname) : fmsg);
    }

    // TODO return un objet avec un boolean et un text message qu'on va afficher dans le chat des joueurs
    private static boolean sendReportSuggestion(@Nullable String messageSender, String reportedPlayer, String cheat, boolean isSenderInTablist, boolean isSenderNicked, boolean isSenderFlaging, boolean isSenderIgnored, boolean isSenderCheating, boolean isSenderRankValid) {
        if (ConfigHandler.autoreportSuggestions) { // TODO autoreport suggestion

            if (FKCounterMod.isInMwGame && !FKCounterMod.isitPrepPhase && mc.thePlayer != null && !isPlayerMyself(reportedPlayer) && messageSender != null) {

                if (isPlayerMyself(messageSender)) {
                    if (FKCounterMod.isitPrepPhase) {
                        addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.RED + "Report suggestions aren't working before the walls fall."));
                    }
                    String[] args = new String[]{reportedPlayer, cheat};
                    CommandWDR.handleWDRCommand(args, true);
                    return true;
                }
                if (!FKCounterMod.isitPrepPhase) {
                    if (canReportSuggestionPlayer(reportedPlayer)) {
                        ReportQueue.INSTANCE.addPlayerToQueueRandom(reportedPlayer);
                        return true;
                    }
                }

            }
        }
        return false;
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
        return NetHandlerPlayClientHook.playerInfoMap.get(playername) != null || isPlayerMyself(playername);
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
            String senderRank = null;
            String messageSender = null;
            String squadname = null;
            if (matcher.matches()) {
                senderRank = matcher.group(1);
                messageSender = matcher.group(2);
                squadname = SquadEvent.getSquad().get(messageSender);
            }

            if (parseReportMessage(senderRank, messageSender, squadname, msg, fmsg)) {
                event.setCanceled(true);
                return;
            }

            if (squadname != null) {
                event.message = new ChatComponentText(fmsg.replaceFirst(messageSender, squadname));
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
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Players disconnected in the last 2 seconds, click this message to run : \n\n" + EnumChatFormatting.YELLOW + "/stalk " + recentlyDisconnectedPlayers)))
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
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(STRENGTH_SOUND, 0.0F));
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
