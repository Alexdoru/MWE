package fr.alexdoru.megawallsenhancementsmod.chat;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.ScangameData;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import fr.alexdoru.megawallsenhancementsmod.features.MegaWallsEndGameStats;
import fr.alexdoru.megawallsenhancementsmod.features.PartyDetection;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.*;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.Check;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportSuggestionHandler;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WDR;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WdrData;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener {

    private static final String BAN_MESSAGE = "A player has been removed from your game.";
    private static final String HUNTER_STRENGTH_MESSAGE = "Your Force of Nature gave you a 5 second Strength I buff.";
    private static final String GENERAL_START_MESSAGE = "The game starts in 1 second!";
    private static final String OWN_WITHER_DEATH_MESSAGE = "Your wither has died. You can no longer respawn!";
    private static final String PREP_PHASE = "Prepare your defenses!";
    private static final Pattern BLOCKED_MESSAGE = Pattern.compile("^We blocked your comment \"(.+)\" as it is breaking our rules because[a-zA-Z\\s]+\\. https:\\/\\/www.hypixel.net\\/rules\\/.*");
    private static final Pattern COINS_DOUBLED_GUILD_PATTERN = Pattern.compile("^(?:Tokens|Coins) just earned DOUBLED as a Guild Level Reward!$");
    private static final Pattern COINS_PATTERN = Pattern.compile("^\\+\\d+ (tokens|coins)!.*");
    private static final Pattern COINS_BOOSTER_PATTERN = Pattern.compile("^\\+\\d+ (tokens|coins)!( \\([^\\(\\)]*(?:Coins \\+ EXP|Booster)[^\\(\\)]*\\)).*");
    private static final Pattern DREADLORD_STRENGTH_PATTERN = Pattern.compile("§4§lSOUL SIPHON §c§l85% ([0-9])s");
    private static final Pattern HEROBRINE_STRENGTH_PATTERN = Pattern.compile("§e§lPOWER §c§l85% ([0-9])s");
    private static final Pattern HUNTER_PRE_STRENGTH_PATTERN = Pattern.compile("§a§lF\\.O\\.N\\. §7\\(§l§c§lStrength§7\\) §e§l([0-9]+)");
    private static final Pattern CREEPER_FISSION_HEART_PATTERN = Pattern.compile("^§a§lFISSION HEART §c§l([0-9])s");
    private static final Pattern LOCRAW_PATTERN = Pattern.compile("^\\{\"server\":\"(\\w+)\",\"gametype\":\"\\w+\"(?:|,\"lobbyname\":\"\\w+\")(?:|,\"mode\":\"\\w+\")(?:|,\"map\":\"([a-zA-Z0-9_ ]+)\")\\}$");
    private static final Pattern PLAYER_JOIN_PATTERN = Pattern.compile("^(\\w{1,16}) has joined \\([0-9]{1,3}/[0-9]{1,3}\\)!");
    private static final Pattern ZOMBIE_STRENGTH_PATTERN = Pattern.compile("§2§lBERSERK §c§l75% ([0-9])s");
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^(?:\\[[^\\[\\]]+\\] )*(\\w{2,16}):.*");
    private static final HashSet<String> MW_REPETITVE_MSG = new HashSet<>();
    private static final TimerUtil timerStrength = new TimerUtil(11000L);
    private static boolean addGuildCoinsBonus;

    static {
        MW_REPETITVE_MSG.add("You broke your protected chest");
        MW_REPETITVE_MSG.add("You broke your protected trapped chest");
        MW_REPETITVE_MSG.add("Get to the center to stop the hunger");
        MW_REPETITVE_MSG.add("Your Salvaging skill returned your arrow to you!");
        MW_REPETITVE_MSG.add("Your Efficiency skill got you an extra drop!");
        MW_REPETITVE_MSG.add("Your Soothing Moo Skill is ready!");
        MW_REPETITVE_MSG.add("Click your sword or bow to activate your skill!");
        MW_REPETITVE_MSG.add("Your Eagles Eye Skill is ready!");
        MW_REPETITVE_MSG.add("Left Click with any bow to activate your skill!");
        MW_REPETITVE_MSG.add("Your From the Depths Skill is ready!");
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {

        /*normal chat messages*/
        if (event.type == 0) {

            final String msg = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());
            final String fmsg = event.message.getFormattedText();

            if (ScoreboardTracker.isInMwGame && ConfigHandler.hideRepetitiveMWChatMsg && MW_REPETITVE_MSG.contains(msg)) {
                event.setCanceled(true);
                return;
            }

            if (!ScoreboardTracker.isInMwGame && msg.equals(GENERAL_START_MESSAGE)) {
                SquadHandler.formSquad();
                Check.clearFlagMessages();
                ScangameData.fectchRandomClasses();
                return;
            }

            if (msg.equals(OWN_WITHER_DEATH_MESSAGE)) {
                KillCooldownHUD.instance.hideHUD();
                return;
            }

            if (ConfigHandler.shortCoinMessage) {
                if (COINS_DOUBLED_GUILD_PATTERN.matcher(msg).matches()) {
                    event.setCanceled(true);
                    addGuildCoinsBonus = true;
                    return;
                }
                final Matcher matcherBooster = COINS_BOOSTER_PATTERN.matcher(msg);
                if (matcherBooster.matches()) {
                    if (addGuildCoinsBonus) {
                        final String currency = matcherBooster.group(1);
                        final boolean isCoins = "coins".equals(currency);
                        event.message = new ChatComponentText(fmsg
                                .replaceFirst(currency + "!", currency + "! (" + (isCoins ? EnumChatFormatting.DARK_GREEN : "") + "Guild " + (isCoins ? EnumChatFormatting.GOLD : "") + "bonus)")
                                .replace(matcherBooster.group(2), ""));
                        addGuildCoinsBonus = false;
                    } else {
                        event.message = new ChatComponentText(fmsg.replace(matcherBooster.group(2), ""));
                    }
                    return;
                }
                final Matcher matcherCoins = COINS_PATTERN.matcher(msg);
                if (matcherCoins.matches()) {
                    if (addGuildCoinsBonus) {
                        final String currency = matcherCoins.group(1);
                        final boolean isCoins = "coins".equals(currency);
                        event.message = new ChatComponentText(fmsg.replaceFirst(currency + "!", currency + "! (" + (isCoins ? EnumChatFormatting.DARK_GREEN : "") + "Guild " + (isCoins ? EnumChatFormatting.GOLD : "") + "bonus)"));
                        addGuildCoinsBonus = false;
                        return;
                    }
                }
                if (addGuildCoinsBonus) {
                    addGuildCoinsBonus = false;
                }
            }

            if (msg.equals(PREP_PHASE)) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.GAME_START));
                return;
            }

            if (FinalKillCounter.processMessage(event, fmsg, msg)) {
                return;
            }

            if (ArrowHitHUD.instance.processMessage(event, msg, fmsg)) {
                return;
            }

            if (PhoenixBondHUD.instance.processMessage(event.message, msg)) {
                return;
            }

            final Matcher matcher = MESSAGE_PATTERN.matcher(msg);
            String messageSender = null;
            String squadname = null;
            if (matcher.matches()) {
                messageSender = matcher.group(1);
                squadname = SquadHandler.getSquad().get(messageSender);
            }

            final Matcher matcherBlockedMessage = BLOCKED_MESSAGE.matcher(msg);
            if (matcherBlockedMessage.matches()) {
                final String blockedMessage = matcherBlockedMessage.group(1);
                ReportSuggestionHandler.processBlockedMessage(blockedMessage);
                return;
            }

            if (ReportSuggestionHandler.parseReportMessage(event, messageSender, squadname, msg, fmsg)) {
                ChatUtil.addSkinToComponent(event.message, messageSender);
                return;
            }

            // Chat Censoring
            if (ConfigHandler.censorCheaterChatMsg && messageSender != null) {
                final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.getPlayerInfo(messageSender);
                if (networkPlayerInfo != null) {
                    final WDR wdr = WdrData.getWdr(networkPlayerInfo.getGameProfile().getId(), messageSender);
                    if (wdr != null && wdr.hasValidCheats()) {
                        if (ConfigHandler.deleteCheaterChatMsg) {
                            event.setCanceled(true);
                        } else {
                            event.message = StringUtil.censorChatMessage(fmsg, messageSender);
                            ChatUtil.addSkinToComponent(event.message, messageSender);
                        }
                        return;
                    }
                }
            }

            if (squadname != null && messageSender != null) {
                event.message = new ChatComponentText(fmsg.replaceFirst(messageSender, squadname));
            }

            if (messageSender != null) {
                ChatUtil.addSkinToComponent(event.message, messageSender);
            }

            if (MegaWallsEndGameStats.processMessage(msg)) {
                return;
            }

            if (ConfigHandler.showStrengthHUD && msg.equals(HUNTER_STRENGTH_MESSAGE)) {
                HunterStrengthHUD.instance.setStrengthRenderStart(5000L);
                return;
            }

            if (ScoreboardTracker.isPreGameLobby) {
                final Matcher playerJoinMatcher = PLAYER_JOIN_PATTERN.matcher(msg);
                if (playerJoinMatcher.matches()) {
                    final String playername = playerJoinMatcher.group(1);
                    PartyDetection.onPlayerJoin(playername, System.currentTimeMillis());
                    ChatUtil.addSkinToComponent(event.message, playername);
                    return;
                }
            }

            if (msg.equals(BAN_MESSAGE)) {
                new DelayedTask(NetHandlerPlayClientHook::printDisconnectedPlayers, 10);
                return;
            }

            if (interceptLocraw) {
                if (System.currentTimeMillis() - timeInterceptLocraw < 1001L) {
                    final Matcher locrawMatcher = LOCRAW_PATTERN.matcher(msg);
                    if (locrawMatcher.matches()) {
                        final String gameId = locrawMatcher.group(1).replace("mega", "M");
                        final String map = locrawMatcher.group(2);
                        if (locrawAction == LocrawAction.RUNSCANGAME) {
                            CommandScanGame.handleScangameCommand(gameId);
                        }
                        BaseLocationHUD.instance.setCurrentMap(map); // can't hurt to set the map everytime
                        interceptLocraw = false;
                        event.setCanceled(true);
                    }
                } else {
                    interceptLocraw = false;
                }
            }

            /*Status messages*/
        } else if (event.type == 2) {

            final String fmsg = event.message.getFormattedText();

            if (ConfigHandler.showStrengthHUD) {
                final Matcher dreadStrenghtMatcher = DREADLORD_STRENGTH_PATTERN.matcher(fmsg);
                if (dreadStrenghtMatcher.find()) {
                    HunterStrengthHUD.instance.setStrengthRenderStart(Long.parseLong(dreadStrenghtMatcher.group(1)) * 1000L);
                    return;
                }
                final Matcher preStrengthMatcher = HUNTER_PRE_STRENGTH_PATTERN.matcher(fmsg);
                if (preStrengthMatcher.find()) {
                    if (timerStrength.update()) {
                        SoundUtil.playStrengthSound();
                    }
                    final String preStrengthTimer = preStrengthMatcher.group(1);
                    HunterStrengthHUD.instance.setPreStrengthTime(preStrengthTimer);
                    return;
                }
                final Matcher herobrineStrenghtMatcher = HEROBRINE_STRENGTH_PATTERN.matcher(fmsg);
                if (herobrineStrenghtMatcher.find()) {
                    HunterStrengthHUD.instance.setStrengthRenderStart(Long.parseLong(herobrineStrenghtMatcher.group(1)) * 1000L);
                    return;
                }
                final Matcher zombieStrenghtMatcher = ZOMBIE_STRENGTH_PATTERN.matcher(fmsg);
                if (zombieStrenghtMatcher.find()) {
                    HunterStrengthHUD.instance.setStrengthRenderStart(Long.parseLong(zombieStrenghtMatcher.group(1)) * 1000L);
                }
            }

            if (ConfigHandler.showPrimedTNTHUD) {
                final Matcher creeperMatcher = CREEPER_FISSION_HEART_PATTERN.matcher(fmsg);
                if (creeperMatcher.find()) {
                    CreeperPrimedTntHUD.instance.setCooldownRenderStart(creeperMatcher.group(1));
                }
            }

        }

    }

    private static boolean interceptLocraw;
    private static long timeInterceptLocraw;
    private static LocrawAction locrawAction;

    public static void runScangame() {
        sendLocraw(LocrawAction.RUNSCANGAME);
    }

    public static void setMegaWallsMap() {
        sendLocraw(LocrawAction.SETMEGAWALLSMAP);
    }

    private static void sendLocraw(LocrawAction action) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");
            interceptLocraw = true;
            timeInterceptLocraw = System.currentTimeMillis();
            locrawAction = action;
        }
    }

    private enum LocrawAction {
        RUNSCANGAME, SETMEGAWALLSMAP
    }

}
