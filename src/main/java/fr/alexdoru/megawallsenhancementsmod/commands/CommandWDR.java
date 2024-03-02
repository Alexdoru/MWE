package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import fr.alexdoru.megawallsenhancementsmod.features.PartyDetection;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WDR;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WdrData;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CommandWDR extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "watchdogreport";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendChatMessage("/wdr");
            return;
        }
        handleWDRCommand(args, true, false);
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("wdr");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            if (ScoreboardTracker.isInMwGame) {
                if (ScoreboardTracker.isPrepPhase) {
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

    public static void handleWDRCommand(String[] args, boolean sendReport, boolean showReportMessage) {
        final String playername = args[0];
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                if (netInfo.getGameProfile().getId().version() == 4) {
                    handleWDRCommand(
                            netInfo.getGameProfile().getId().toString(),
                            netInfo.getGameProfile().getName(),
                            ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName()),
                            args,
                            sendReport,
                            showReportMessage
                    );
                    return;
                } else if (netInfo.getGameProfile().getId().version() == 1) {
                    handleWDRCommand(
                            null,
                            netInfo.getGameProfile().getName(),
                            ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName()),
                            args,
                            sendReport,
                            showReportMessage
                    );
                    return;
                }
            }
        }
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final MojangPlayernameToUUID mojangReq = new MojangPlayernameToUUID(playername);
                if (!HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    try {
                        final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(mojangReq.getUuid()));
                        if (!loginData.hasNeverJoinedHypixel() && mojangReq.getName().equals(loginData.getdisplayname())) {
                            // real player
                            mc.addScheduledTask(() ->
                                    handleWDRCommand(
                                            mojangReq.getUuid(),
                                            mojangReq.getName(),
                                            loginData.getFormattedName(),
                                            args,
                                            sendReport,
                                            showReportMessage
                                    )
                            );
                            return null;
                        }
                    } catch (ApiException ignored) {}
                }
            } catch (ApiException ignored) {}
            // nicked player
            mc.addScheduledTask(() -> sendChatMessage("/wdr ", args));
            return null;
        });
    }

    private static void handleWDRCommand(String uuid, String playername, String formattedPlayername, String[] args, boolean sendReport, boolean showReportMessage) {
        final boolean isaNick = uuid == null;
        if (isaNick) {
            uuid = playername;
        } else {
            uuid = uuid.replace("-", "");
        }
        final ArrayList<String> cheats = new ArrayList<>();
        final StringBuilder message = new StringBuilder("/wdr " + playername);
        final long time = new Date().getTime();

        if (args.length == 1) {
            cheats.add("cheating");
        } else {
            for (int i = 1; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("fastbreak")) {
                    cheats.add(args[i]);
                    message.append(" speed");
                } else if (args[i].equalsIgnoreCase("autoblock") || args[i].equalsIgnoreCase("multiaura")) {
                    cheats.add(args[i]);
                    message.append(" killaura");
                } else if (args[i].equalsIgnoreCase("noslowdown") || args[i].equalsIgnoreCase("keepsprint")) {
                    cheats.add(args[i]);
                    message.append(" velocity");
                } else {
                    cheats.add(args[i]);
                    message.append(" ").append(args[i]);
                }
            }
        }

        if (sendReport) {
            sendChatMessage(message.toString());
            ReportQueue.INSTANCE.addPlayerReportedThisGame(playername);
        }

        PartyDetection.printBoostingReportAdvice(playername);

        if (ScoreboardTracker.isPreGameLobby) {
            ChatUtil.printReportingAdvice();
        }

        final WDR wdr = WdrData.getWdr(uuid);

        if (wdr == null) {
            if (isaNick) {
                cheats.add(WDR.NICK);
            }
            WdrData.put(uuid, new WDR(time, cheats));
        } else {
            wdr.time = time;
            cheats.removeAll(wdr.hacks);
            wdr.hacks.addAll(cheats);
            wdr.hacks.trimToSize();
        }
        WdrData.saveReportedPlayers();

        NameUtil.updateMWPlayerDataAndEntityData(playername, false);
        if (showReportMessage || wdr == null) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() +
                    EnumChatFormatting.GREEN + "You reported " + (isaNick ? EnumChatFormatting.GREEN + "the" + EnumChatFormatting.DARK_PURPLE + " nicked player " : "")
                    + EnumChatFormatting.RED + (formattedPlayername == null ? playername : EnumChatFormatting.RESET + formattedPlayername) + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game"
                    + EnumChatFormatting.GREEN + (isaNick ? " for the next 24 hours." : "."));
        }
    }

}