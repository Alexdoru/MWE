package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.mwe.api.exceptions.ApiException;
import fr.alexdoru.mwe.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.mwe.api.requests.MojangNameToUUID;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.features.PartyDetection;
import fr.alexdoru.mwe.nocheaters.WDR;
import fr.alexdoru.mwe.nocheaters.WdrData;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.NameUtil;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

public class CommandWDR extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "wdr";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendCommand();
            return;
        }
        this.sendCommand(args);
        final String playername = args[0];
        final List<String> cheats = new ArrayList<>();
        if (args.length == 1) {
            cheats.add("cheating");
        } else {
            cheats.addAll(Arrays.asList(args).subList(1, args.length));
        }
        PartyDetection.printBoostingReportAdvice(playername);
        if (ScoreboardTracker.isPreGameLobby()) {
            ChatUtil.printReportingAdvice();
        }
        addPlayerToReportList(playername, cheats);
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("watchdogreport");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            if (ScoreboardTracker.isInMwGame()) {
                if (ScoreboardTracker.isPrepPhase()) {
                    return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
                } else {
                    final List<String> playersInThisGame = FinalKillCounter.getPlayersInThisGame();
                    playersInThisGame.removeAll(TabCompletionUtil.getOnlinePlayersByName());
                    return getListOfStringsMatchingLastWord(args, playersInThisGame);
                }
            }
            return null;
        }
        return args.length > 1 ? getListOfStringsMatchingLastWord(args, CommandReport.cheatsArray) : null;
    }

    private static void addPlayerToReportList(String playername, List<String> cheats) {
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                final UUID uuid = netInfo.getGameProfile().getId();
                if (NameUtil.isNickedPlayer(uuid) || NameUtil.isRealPlayer(uuid)) {
                    addPlayerToReportList(
                            uuid,
                            netInfo.getGameProfile().getName(),
                            ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName()),
                            cheats);
                    return;
                }
            }
        }
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final MojangNameToUUID mojangReq = new MojangNameToUUID(playername);
                if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    mc.addScheduledTask(() -> addPlayerToReportList(mojangReq.getUUID(), mojangReq.getName(), null, cheats));
                    return null;
                }
                try {
                    final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(mojangReq.getUuid()));
                    if (!loginData.hasNeverJoinedHypixel() && mojangReq.getName().equals(loginData.getdisplayname())) {
                        // real player
                        mc.addScheduledTask(() -> addPlayerToReportList(mojangReq.getUUID(), mojangReq.getName(), loginData.getFormattedName(), cheats));
                        return null;
                    }
                } catch (ApiException e) {
                    mc.addScheduledTask(() -> addPlayerToReportList(mojangReq.getUUID(), mojangReq.getName(), null, cheats));
                    return null;
                }
            } catch (ApiException ignored) {}
            return null;
        });
    }

    private static void addPlayerToReportList(UUID uuid, String playername, String formattedName, List<String> cheats) {
        final WDR wdr = WdrData.getWdr(uuid, playername);
        if (wdr == null) {
            WdrData.put(uuid, playername, new WDR(cheats));
        } else {
            wdr.addCheats(cheats);
        }
        WdrData.saveReportedPlayers();
        NameUtil.updateMWPlayerDataAndEntityData(playername, false);
        if (wdr == null) {
            final boolean isNicked = !NameUtil.isRealPlayer(uuid);
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() +
                    EnumChatFormatting.GREEN + "You reported " + (isNicked ? EnumChatFormatting.GREEN + "the" + EnumChatFormatting.DARK_PURPLE + " nicked player " : "")
                    + EnumChatFormatting.RED + (formattedName == null ? playername : EnumChatFormatting.RESET + formattedName) + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game"
                    + EnumChatFormatting.GREEN + (isNicked ? " for the next 24 hours." : "."));
        }
    }

}