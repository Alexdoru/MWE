package fr.alexdoru.mwe.chat;

import fr.alexdoru.mwe.asm.hooks.NetHandlerPlayClientHook_PlayerMapTracker;
import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.data.ScangameData;
import fr.alexdoru.mwe.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.features.*;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
import fr.alexdoru.mwe.hackerdetector.checks.Check;
import fr.alexdoru.mwe.nocheaters.ReportSuggestionHandler;
import fr.alexdoru.mwe.nocheaters.WDR;
import fr.alexdoru.mwe.nocheaters.WdrData;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.MapUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener {

    private static final String AFK_MESSAGE = "Are you AFK? You could be kicked for AFKing!";
    private static final String BAN_MESSAGE = "A player has been removed from your game.";
    private static final String DEATHMATCH_DAMAGE_MESSAGE = "                  Draw settled by: Deathmatch damage";
    private static final String HUNTER_STRENGTH_MESSAGE = "Your Force of Nature gave you a 5 second Strength I buff.";
    private static final String GENERAL_START_MESSAGE = "The game starts in 1 second!";
    private static final String OWN_WITHER_DEATH_MESSAGE = "Your wither has died. You can no longer respawn!";
    private static final String PREP_PHASE = "Prepare your defenses!";
    private static final Pattern COINS_DOUBLED_GUILD_PATTERN = Pattern.compile("^(?:Tokens|Coins) just earned DOUBLED as a Guild Level Reward!$");
    private static final Pattern COINS_PATTERN = Pattern.compile("^\\+\\d+ (tokens|coins)!.*");
    private static final Pattern COINS_BOOSTER_PATTERN = Pattern.compile("^\\+\\d+ (tokens|coins)!( \\([^()]*(?:Coins \\+ EXP|Booster)[^()]*\\)).*");
    private static final Pattern PLAYER_JOIN_PATTERN = Pattern.compile("^(\\w{1,16}) has joined \\([0-9]{1,3}/[0-9]{1,3}\\)!");
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^(?:\\[[^\\[\\]]+] )*(\\w{2,16}):.*");
    private static final Pattern HALO_GIVE_PATTERN = Pattern.compile("^You gave your halo to (\\w+)!");
    private static final Pattern HALO_HOTBAR_PATTERN = Pattern.compile("^HALO (\\w+) .+");
    private static final HashSet<String> MW_REPETITVE_MSG = new HashSet<>();
    private static boolean addGuildCoinsBonus;

    static {
        MW_REPETITVE_MSG.add("You broke your protected chest");
        MW_REPETITVE_MSG.add("You broke your protected trapped chest");
        MW_REPETITVE_MSG.add("Get to the center to stop the hunger");
        MW_REPETITVE_MSG.add("Your Salvaging skill returned your arrow to you!");
        MW_REPETITVE_MSG.add("Your Efficiency skill got you an extra drop!");
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {

        final String fmsg = event.message.getFormattedText();
        final String msg = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());

        /*normal chat messages*/
        if (event.type == 0) {

            if (ScoreboardTracker.isInMwGame()) {

                if (ConfigHandler.hideRepetitiveMWChatMsg && MW_REPETITVE_MSG.contains(msg)) {
                    event.setCanceled(true);
                    return;
                }

                if (msg.equals(OWN_WITHER_DEATH_MESSAGE)) {
                    GuiManager.killCooldownHUD.hideHUD();
                    return;
                }

                if (msg.equals(AFK_MESSAGE)) {
                    AFKSoundWarning.playAFKKickSound();
                    return;
                }

                if (ConfigHandler.squadHaloPlayer) {
                    final Matcher haloGiveMatcher = HALO_GIVE_PATTERN.matcher(msg);
                    if (haloGiveMatcher.find()) {
                        SquadHandler.addSelf();
                        SquadHandler.addPlayer(haloGiveMatcher.group(1));
                    }
                }

                if (ConfigHandler.printDeathmatchDamageMessage) {
                    if (printDeathmatchDamage(event.message, msg)) {
                        return;
                    }
                }

                if (FinalKillCounter.processMessage(event, fmsg, msg)) {
                    return;
                }

                if (GuiManager.phoenixBondHUD.processMessage(event.message, msg)) {
                    return;
                }

                if (GuiManager.warcryHUD.processMessage(msg)) {
                    return;
                }

                if (ConfigHandler.showStrengthHUD && msg.equals(HUNTER_STRENGTH_MESSAGE)) {
                    GuiManager.strengthHUD.setStrengthRenderStart(5000L);
                    return;
                }

            } else {

                if (msg.equals(GENERAL_START_MESSAGE)) {
                    SquadHandler.formSquad();
                    Check.clearFlagMessages();
                    ScangameData.fectchRandomClasses();
                    return;
                }

            }

            if (processCoinsMessages(event, fmsg, msg)) {
                return;
            }

            if (msg.equals(PREP_PHASE)) {
                MinecraftForge.EVENT_BUS.post(new MegaWallsGameEvent(MegaWallsGameEvent.EventType.GAME_START));
                return;
            }

            if (GuiManager.arrowHitHUD.processMessage(event, fmsg, msg)) {
                return;
            }

            final Matcher matcher = MESSAGE_PATTERN.matcher(msg);
            String messageSender = null;
            String squadname = null;
            if (matcher.matches()) {
                messageSender = matcher.group(1);
                squadname = SquadHandler.getSquad().get(messageSender);
            }

            if (ReportSuggestionHandler.processMessage(event, messageSender, squadname, fmsg, msg)) {
                ChatUtil.addSkinToComponent(event.message, messageSender);
                return;
            }

            // Chat Censoring
            if (ConfigHandler.censorCheaterChatMsg && messageSender != null) {
                final NetworkPlayerInfo netInfo = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(messageSender);
                if (netInfo != null) {
                    final WDR wdr = WdrData.getWdr(netInfo.getGameProfile().getId(), messageSender);
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

            if (ScoreboardTracker.isPreGameLobby()) {
                final Matcher playerJoinMatcher = PLAYER_JOIN_PATTERN.matcher(msg);
                if (playerJoinMatcher.matches()) {
                    final String playername = playerJoinMatcher.group(1);
                    PartyDetection.onPlayerJoin(playername, System.currentTimeMillis());
                    ChatUtil.addSkinToComponent(event.message, playername);
                    return;
                }
            }

            if (ConfigHandler.showBannedPlayers && msg.equals(BAN_MESSAGE)) {
                new DelayedTask(NetHandlerPlayClientHook_PlayerMapTracker::printDisconnectedPlayers, 10);
            }

            /*Status messages*/
        } else if (event.type == 2 && ScoreboardTracker.isInMwGame()) {

            if (GuiManager.strengthHUD.processMessage(fmsg)) {
                return;
            }

            if (GuiManager.creeperPrimedTntHUD.processMessage(fmsg)) {
                return;
            }

            final Matcher haloMatcher = HALO_HOTBAR_PATTERN.matcher(msg);
            if (haloMatcher.find()) {
                final String name = haloMatcher.group(1);
                final String squadname = SquadHandler.getSquadname(name);
                if (!squadname.equals(name)) {
                    if (ConfigHandler.pinkSquadmates) {
                        event.message = new ChatComponentText(fmsg.replaceFirst(name, EnumChatFormatting.LIGHT_PURPLE + squadname));
                    } else {
                        event.message = new ChatComponentText(fmsg.replaceFirst(name, squadname));
                    }
                }
            }

        }

    }

    private boolean printDeathmatchDamage(IChatComponent imsg, String msg) {
        if (!msg.equals(DEATHMATCH_DAMAGE_MESSAGE)) return false;
        for (final IChatComponent iChatComponent : imsg) {
            if (iChatComponent.getChatStyle() == null) continue;
            final HoverEvent hoverEvent = iChatComponent.getChatStyle().getChatHoverEvent();
            if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT && hoverEvent.getValue() != null) {
                final String hoverMsg = EnumChatFormatting.getTextWithoutFormattingCodes(hoverEvent.getValue().getUnformattedText());
                final Matcher matcher = Pattern.compile("([A-Z]+): ([\\d,]+) damage").matcher(hoverMsg);
                final Map<String, Integer> map = new HashMap<>();
                while (matcher.find()) {
                    final String teamname = matcher.group(1);
                    final int damage = Integer.parseInt(matcher.group(2).replace(",", ""));
                    if (damage != 0) {
                        map.put(teamname, damage);
                    }
                }
                if (map.size() > 1) {
                    final Map<String, Integer> sortedMap = MapUtil.sortByDecreasingValue(map);
                    final String hoverFmsg = hoverEvent.getValue().getFormattedText();
                    final StringBuilder sb = new StringBuilder();
                    for (final Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
                        final char color = StringUtil.getLastColorCharBefore(hoverFmsg, entry.getKey());
                        if (color != '\0') sb.append('§').append(color);
                        sb.append(entry.getKey()).append(EnumChatFormatting.WHITE).append(": ").append(entry.getValue()).append(" ");
                    }
                    final String damageMessage = sb.toString();
                    if (ChatUtil.getSeparatorToCenter(damageMessage).isEmpty()) {
                        imsg.appendText("\n").appendText(damageMessage);
                    } else {
                        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
                        final int widthToCenter = fr.getStringWidth("                  ") +
                                fr.getStringWidth("Draw settled by: Deathmatch damage") / 2
                                - fr.getStringWidth(damageMessage) / 2 + 1;
                        imsg.appendText("\n")
                                .appendText(ChatUtil.getSeparatorOfLength(widthToCenter))
                                .appendText(damageMessage);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean processCoinsMessages(ClientChatReceivedEvent event, String fmsg, String msg) {
        if (!ConfigHandler.shortCoinMessage) return false;
        if (COINS_DOUBLED_GUILD_PATTERN.matcher(msg).matches()) {
            event.setCanceled(true);
            addGuildCoinsBonus = true;
            return true;
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
            return true;
        }
        final Matcher matcherCoins = COINS_PATTERN.matcher(msg);
        if (matcherCoins.matches()) {
            if (addGuildCoinsBonus) {
                final String currency = matcherCoins.group(1);
                final boolean isCoins = "coins".equals(currency);
                event.message = new ChatComponentText(fmsg.replaceFirst(currency + "!", currency + "! (" + (isCoins ? EnumChatFormatting.DARK_GREEN : "") + "Guild " + (isCoins ? EnumChatFormatting.GOLD : "") + "bonus)"));
                addGuildCoinsBonus = false;
                return true;
            }
        }
        if (addGuildCoinsBonus) {
            addGuildCoinsBonus = false;
        }
        return false;
    }

}
