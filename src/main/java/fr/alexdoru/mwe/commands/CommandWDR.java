package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.IPlayerUUID;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.data.NameFormatter;
import fr.alexdoru.mwe.data.PlayerDataManager;
import fr.alexdoru.mwe.data.WdrDataManager;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.features.PartyDetection;
import fr.alexdoru.mwe.http.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.http.cache.CachedHypixelPlayerData;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.parsers.hypixel.LoginData;
import fr.alexdoru.mwe.http.requests.MojangNameToUUID;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

public class CommandWDR extends MyAbstractCommand {

    public static final List<String> cheatsList = Collections.unmodifiableList(Arrays.asList("aura",
            "aimbot",
            "bhop",
            "velocity",
            "reach",
            "speed",
            "ka",
            "killaura",
            "multiaura",
            "forcefield",
            "autoblock",
            "antiknockback",
            "antikb",
            "autoclicker",
            "ac",
            "fly",
            "dolphin",
            "jesus",
            "keepsprint",
            "noslowdown",
            "fastbreak",
            "speedmine",
            "cheating",
            "scaffold"));

    public static final Set<String> cheatsSet = Collections.unmodifiableSet(new HashSet<>(cheatsList));

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
                    return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getPlayersAndAlias());
                } else {
                    final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
                    if (fkCounter == null) return null;
                    final List<String> playersInThisGame = fkCounter.getPlayersInThisGame();
                    playersInThisGame.removeAll(TabCompletionUtil.getPlayersAndAlias());
                    return getListOfStringsMatchingLastWord(args, playersInThisGame);
                }
            }
            return null;
        }
        return args.length > 1 ? getListOfStringsMatchingLastWord(args, cheatsList) : null;
    }

    private static void addPlayerToReportList(String playername, List<String> cheats) {
        final Minecraft mc = Minecraft.getMinecraft();
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                final UUID uuid = netInfo.getGameProfile().getId();
                if (PlayerDataManager.isNickedPlayer(uuid) || PlayerDataManager.isRealPlayer(uuid)) {
                    addPlayerToReportList(
                            uuid,
                            netInfo.getGameProfile().getName(),
                            NameFormatter.getVanillaName(netInfo),
                            cheats
                    );
                    return;
                }
            }
        }
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final IPlayerUUID playerID = MojangNameToUUID.getPlayerUUID(playername);
                if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    mc.addScheduledTask(() -> addPlayerToReportList(playerID.getId(), playerID.getName(), null, cheats));
                    return;
                }
                try {
                    final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(playerID.getId()));
                    if (!loginData.hasNeverJoinedHypixel() && playerID.getName().equals(loginData.getdisplayname())) {
                        // real player
                        mc.addScheduledTask(() -> addPlayerToReportList(playerID.getId(), playerID.getName(), loginData.getFormattedName(), cheats));
                    }
                } catch (ApiException e) {
                    mc.addScheduledTask(() -> addPlayerToReportList(playerID.getId(), playerID.getName(), null, cheats));
                }
            } catch (ApiException ignored) {}
        });
    }

    private static void addPlayerToReportList(UUID uuid, String playername, String formattedName, List<String> cheats) {
        final boolean added = WdrDataManager.addReport(uuid, playername, cheats);
        WdrDataManager.saveReportedPlayers();
        if (added) {
            final boolean isNicked = !PlayerDataManager.isRealPlayer(uuid);
            ChatUtil.addChatMessage(
                    EnumChatFormatting.GREEN + "You reported " + (isNicked ? EnumChatFormatting.GREEN + "the" + EnumChatFormatting.DARK_PURPLE + " nicked player " : "")
                            + EnumChatFormatting.RED + (formattedName == null ? playername : EnumChatFormatting.RESET + formattedName) + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game"
                            + EnumChatFormatting.GREEN + (isNicked ? " for the next 24 hours." : "."));
        }
    }

}