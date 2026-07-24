package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.IPlayerUUID;
import fr.alexdoru.mwe.api.MWECommandBase;
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

public class CommandWDR extends MWECommandBase {

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
                    final List<String> players = TabCompletionUtil.getAlias();
                    final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
                    if (fkCounter != null) {
                        players.addAll(fkCounter.getPlayersInThisGame());
                        players.removeAll(TabCompletionUtil.getPlayers());
                    }
                    return getListOfStringsMatchingLastWord(args, players);
                }
            }
            return null;
        }
        return args.length > 1 ? getListOfStringsMatchingLastWord(args, cheatsList) : null;
    }

    private void addPlayerToReportList(String playername, List<String> cheats) {
        final Minecraft mc = Minecraft.getMinecraft();
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (PlayerDataManager.isNickedPlayer(netInfo.getGameProfile().getId())) {
                if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                    addPlayerToReportList(
                            netInfo.getGameProfile().getId(),
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
                String name = null;
                if (!HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(playerID.getId()));
                    if (!loginData.hasNeverJoinedHypixel() && playerID.getName().equals(loginData.getdisplayname())) {
                        name = loginData.getFormattedName();
                    }
                }
                final String formattedName = name;
                mc.addScheduledTask(() -> addPlayerToReportList(playerID.getId(), playerID.getName(), formattedName, cheats));
            } catch (ApiException ignored) {}
        });
    }

    private void addPlayerToReportList(UUID uuid, String playername, String formattedName, List<String> cheats) {
        final boolean added = WdrDataManager.addReport(uuid, playername, cheats);
        if (added) {
            final boolean isNicked = !PlayerDataManager.isRealPlayer(uuid);
            ChatUtil.addChatMessage(
                    EnumChatFormatting.GREEN + "You reported " + (isNicked ? EnumChatFormatting.GREEN + "the" + EnumChatFormatting.DARK_PURPLE + " nicked player " : "")
                            + EnumChatFormatting.RED + (formattedName == null ? playername : EnumChatFormatting.RESET + formattedName) + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game"
                            + EnumChatFormatting.GREEN + (isNicked ? " for the next 24 hours." : "."));
        }
    }

}