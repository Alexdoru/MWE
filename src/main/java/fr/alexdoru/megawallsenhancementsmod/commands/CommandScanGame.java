package fr.alexdoru.megawallsenhancementsmod.commands;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.RateLimitException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.GeneralInfo;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassStats;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsStats;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatListener;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.chat.ScanFlagChatComponent;
import fr.alexdoru.megawallsenhancementsmod.data.ScangameData;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.UUID;

import static net.minecraft.util.EnumChatFormatting.*;

public class CommandScanGame extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "scangame";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            ChatUtil.printApikeySetupInfo();
            return;
        }
        if (!ScoreboardTracker.isInMwGame && !ScoreboardTracker.isPreGameLobby) {
            ChatUtil.addChatMessage(RED + "This is only available in Mega Walls!");
            return;
        }
        final String currentGameId = ScoreboardUtils.getGameIdFromScoreboard();
        if (currentGameId == null) {
            ChatListener.runScangame();
        } else {
            handleScangameCommand(currentGameId);
        }
    }

    public static void handleScangameCommand(String currentGameId) {
        if (!currentGameId.equals(ScangameData.getScanGameId())) {
            ScangameData.clearScanGameData();
            if (ScoreboardTracker.isPreGameLobby) ScangameData.clearRandomKits();
            ScangameData.setScanGameId(currentGameId);
            NameUtil.refreshAllNamesInWorld();
        }
        int i = 0;
        final boolean isMythicHour = ScoreboardUtils.isMegaWallsMythicGame();
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (mc.thePlayer != null && netInfo.getGameProfile().getId().equals(mc.thePlayer.getUniqueID())) continue;
            if (scanPlayer(netInfo, isMythicHour)) i++;
        }
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + GREEN + "Scanning " + i + " players...");
    }

    private static boolean scanPlayer(NetworkPlayerInfo netInfo, boolean isMythicHourInPreGameLobby) {

        final UUID uuid = netInfo.getGameProfile().getId();
        if (NameUtil.isntRealPlayer(uuid) || ScangameData.skipScan(uuid)) {
            return false;
        }

        final ScangameData.ScanResult scanResult = ScangameData.get(uuid);
        final boolean doRandomKitCheck = isMythicHourInPreGameLobby && NameUtil.isPlayerUsingRandom(netInfo) || ScangameData.didPlayerPickRandom(uuid);
        if (scanResult != null) {
            if (scanResult.msg != null) {
                addScanMessageToChat(netInfo, scanResult.msg);
                return false;
            } else if (scanResult.isLowLevelAccount() && doRandomKitCheck) {
                scanResult.msg = getMythicRandomMsg(scanResult.networkLvl, scanResult.questamount);
                addScanMessageToChat(netInfo, scanResult.msg);
                NameUtil.updateMWPlayerDataAndEntityData(netInfo, false);
            }
            return false;
        }

        fetchPlayerData(netInfo, doRandomKitCheck);
        return true;

    }

    private static void fetchPlayerData(NetworkPlayerInfo networkPlayerInfo, boolean doRandomKitCheck) {
        final String uuid = networkPlayerInfo.getGameProfile().getId().toString();
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final HypixelPlayerData playerdata = new HypixelPlayerData(uuid);
                Minecraft.getMinecraft().addScheduledTask(() -> checkPlayerStats(networkPlayerInfo, playerdata, doRandomKitCheck));
                return null;
            } catch (RateLimitException e) {
                // return here to not fill the data map with requests
                // that failed and have a chance to retry later
                return null;
            } catch (Exception ignored) {}
            return null;
        });
    }

    private static void checkPlayerStats(NetworkPlayerInfo networkPlayerInfo, HypixelPlayerData playerdata, boolean doRandomKitCheck) {
        final UUID uuid = networkPlayerInfo.getGameProfile().getId();
        final String playername = networkPlayerInfo.getGameProfile().getName();
        final MegaWallsStats megaWallsStats = new MegaWallsStats(playerdata.getPlayerData());

        if (megaWallsStats.getGamesPlayed() > 500) {
            ScangameData.addToSkipSet(uuid);
            return;
        }

        final GeneralInfo generalInfo = new GeneralInfo(playerdata.getPlayerData());

        IChatComponent imsg = checkPlayerFkd(megaWallsStats);
        if (imsg == null) imsg = checkMaxKits(playername, playerdata.getPlayerData(), megaWallsStats, generalInfo);
        if (imsg == null) imsg = checkLegendarySkins(megaWallsStats);
        if (imsg == null && doRandomKitCheck) imsg = checkRandomKit(generalInfo, playerdata);

        if (imsg != null) {
            addScanMessageToChat(networkPlayerInfo, imsg);
            ScangameData.put(uuid, imsg);
            NameUtil.updateMWPlayerDataAndEntityData(networkPlayerInfo, false);
        } else {
            ScangameData.put(uuid, (int) generalInfo.getNetworkLevel(), generalInfo.getCompletedQuests());
        }
    }

    private static IChatComponent checkPlayerFkd(MegaWallsStats megaWallsStats) {
        if ((megaWallsStats.getGamesPlayed() <= 25 && megaWallsStats.getFkdr() > 3.5f) ||
                (megaWallsStats.getGamesPlayed() <= 250 && megaWallsStats.getFkdr() > 5f) ||
                (megaWallsStats.getGamesPlayed() <= 500 && megaWallsStats.getFkdr() > 8f) ||
                megaWallsStats.getWlr() * megaWallsStats.getFkdr() > 0.33F * 1F * 6F) { // 6 times the average win/loss times the average fkdr
            return new ChatComponentText(GRAY + " played : " + GOLD + megaWallsStats.getGamesPlayed()
                    + GRAY + " games, fkd : " + GOLD + String.format("%.1f", megaWallsStats.getFkdr())
                    + GRAY + " FK/game : " + GOLD + String.format("%.1f", megaWallsStats.getFkpergame())
                    + GRAY + " W/L : " + GOLD + String.format("%.1f", megaWallsStats.getWlr()));

        }
        return null;
    }

    private static IChatComponent checkMaxKits(String playername, JsonObject playerData, MegaWallsStats mwStats, GeneralInfo generalInfo) {
        if (mwStats.getGamesPlayed() >= 15) {
            return null;
        }
        final JsonObject classesdata = mwStats.getClassesdata();
        if (classesdata == null) {
            return null;
        }
        IChatComponent imsg = null;
        if (ScoreboardTracker.isInMwGame) {
            final MWClass mwClass = MWClass.ofPlayer(playername);
            if (mwClass != null) {
                final MegaWallsClassStats classStats = new MegaWallsClassStats(playerData, mwClass.className);
                imsg = getMaxKitMsg(mwStats, classStats, generalInfo);
            }
            return imsg;
        }
        for (final MWClass mwClass : MWClass.values()) {
            final MegaWallsClassStats classStats = new MegaWallsClassStats(playerData, mwClass.className);
            if (imsg == null) {
                imsg = getMaxKitMsg(mwStats, classStats, generalInfo);
            } else {
                final int[] upgrades = classStats.getKitUpgrades();
                if (mwStats.getGamesPlayed() == 0 ? upgrades[0] >= 4 || upgrades[1] >= 4 : upgrades[0] == 5 && upgrades[1] == 5 && upgrades[2] == 3 && upgrades[3] == 3) {
                    imsg.appendSibling(new ChatComponentText(" " + GOLD + classStats.getClassname() + " " + classStats.getFormattedKitUpgrades()));
                }
            }
        }
        return imsg;
    }

    private static IChatComponent getMaxKitMsg(MegaWallsStats mwStats, MegaWallsClassStats classStats, GeneralInfo generalInfo) {
        final boolean firstGame = mwStats.getGamesPlayed() == 0;
        final boolean secondFlag = generalInfo.getCompletedQuests() < 30 && generalInfo.getNetworkLevel() > 25f || mwStats.getGamesPlayed() < 5;
        final boolean mythicFlag = classStats.isMythic() && mwStats.getGamesPlayed() < 10;
        final int[] upgrades = classStats.getKitUpgrades();
        if (firstGame) {
            if (upgrades[0] >= 4 || upgrades[1] >= 4) {
                return new ChatComponentText(GRAY + " never played and has : " + GOLD + classStats.getClassname() + " " + classStats.getFormattedKitUpgrades());
            }
            return null;
        }
        if ((secondFlag && upgrades[0] == 5 && upgrades[1] == 5 && upgrades[2] == 3 && upgrades[3] == 3) || (mythicFlag && upgrades[0] == 5 && upgrades[1] == 5)) {
            return new ChatComponentText(GRAY + " played " + GOLD + mwStats.getGamesPlayed() + GRAY + " games"
                    + GRAY + ", network lvl " + GOLD + ((int) generalInfo.getNetworkLevel())
                    + GRAY + ", with " + GOLD + generalInfo.getCompletedQuests() + GRAY + " quests"
                    + GRAY + " and has : " + GOLD + classStats.getClassname() + " " + classStats.getFormattedKitUpgrades());
        }
        return null;
    }

    private static IChatComponent checkLegendarySkins(MegaWallsStats megaWallsStats) {
        final float ratio = megaWallsStats.getLegSkins() * 12f / (megaWallsStats.getGamesPlayed() == 0 ? 1 : megaWallsStats.getGamesPlayed());
        if (ratio >= 1) {
            return new ChatComponentText(GRAY + " played : " + GOLD + megaWallsStats.getGamesPlayed()
                    + GRAY + " games, and has : " + GOLD + megaWallsStats.getLegSkins()
                    + GRAY + " legendary skin" + (megaWallsStats.getLegSkins() > 1 ? "s" : ""));
        }
        return null;
    }

    private static IChatComponent getMythicRandomMsg(int networkLevel, int completedQuests) {
        return new ChatComponentText(GRAY + " picked "
                + GOLD + "random"
                + GRAY + ", network lvl " + GOLD + networkLevel
                + GRAY + " and " + GOLD + completedQuests + GRAY + " quests");
    }

    private static IChatComponent checkRandomKit(GeneralInfo generalInfo, HypixelPlayerData playerdata) {
        if (((int) generalInfo.getNetworkLevel() + generalInfo.getCompletedQuests()) < 11) {
            final LoginData loginData = new LoginData(playerdata.getPlayerData());
            if (loginData.isNonRanked()) {
                return new ChatComponentText(GRAY + " picked "
                        + GOLD + "random"
                        + GRAY + ", network lvl " + GOLD + ((int) generalInfo.getNetworkLevel())
                        + GRAY + " and " + GOLD + generalInfo.getCompletedQuests() + GRAY + " quests");
            }
        }
        return null;
    }

    private static void addScanMessageToChat(NetworkPlayerInfo netInfo, IChatComponent imsg) {
        ChatHandler.deleteScanFlagFromChat(netInfo.getGameProfile().getName());
        final IChatComponent msg = new ScanFlagChatComponent(netInfo.getGameProfile().getName(), ChatUtil.getTagMW())
                .appendSibling(NameUtil.getFormattedNameWithPlanckeClickEvent(netInfo))
                .appendSibling(imsg);
        ChatUtil.addSkinToComponent(msg, netInfo.getGameProfile().getName());
        ChatUtil.addChatMessage(msg);
    }

}