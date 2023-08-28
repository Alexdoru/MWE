package fr.alexdoru.megawallsenhancementsmod.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.RateLimitException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.GeneralInfo;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsStats;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatListener;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.data.ScangameData;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Map;
import java.util.UUID;

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
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "This is only available in Mega Walls!");
            return;
        }
        final String currentGameId = ScoreboardUtils.getGameIdFromScoreboard();
        if (currentGameId.equals("?")) {
            sendChatMessage("/locraw");
            ChatListener.interceptLocrawAndRunScangame();
        } else {
            handleScangameCommand(currentGameId);
        }
    }

    public static void handleScangameCommand(String currentGameId) {
        if (!currentGameId.equals(ScangameData.getScanGameId())) {
            ScangameData.clearScanGameData();
            ScangameData.setScanGameId(currentGameId);
        }
        int i = 0;
        final boolean isMythicHour = ScoreboardUtils.isMegaWallsMythicGame();
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (scanPlayer(netInfo, isMythicHour)) i++;
        }
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Scanning " + i + " players...");
    }

    private static boolean scanPlayer(NetworkPlayerInfo netInfo, boolean isMythicHourInPreGameLobby) {

        final UUID uuid = netInfo.getGameProfile().getId();
        if (NameUtil.isntRealPlayer(uuid) || ScangameData.skipScan(uuid)) {
            return false;
        }

        final ScangameData.ScanResult scanResult = ScangameData.get(uuid);
        if (scanResult != null) {
            if (scanResult.msg != null) {
                addScanMessageToChat(netInfo, scanResult.msg);
                return false;
            } else if (scanResult.isLowLevelAccount()) {
                if (isMythicHourInPreGameLobby && NameUtil.isPlayerUsingRandom(netInfo)) {
                    addScanMessageToChat(netInfo, getMythicRandomMsg(scanResult.networkLvl, scanResult.questamount));
                } else if (ScangameData.didPlayerPickRandom(uuid)) {
                    scanResult.msg = getMythicRandomMsg(scanResult.networkLvl, scanResult.questamount);
                    addScanMessageToChat(netInfo, scanResult.msg);
                }
            }
            return false;
        }

        fetchPlayerData(netInfo, isMythicHourInPreGameLobby && NameUtil.isPlayerUsingRandom(netInfo));
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

        if (megaWallsStats.getGames_played() > 500) {
            ScangameData.addToSkipSet(uuid);
            return;
        }

        final GeneralInfo generalInfo = new GeneralInfo(playerdata.getPlayerData());

        IChatComponent imsg = checkPlayerFkd(megaWallsStats);
        if (imsg == null) imsg = checkMaxKits(playername, megaWallsStats, generalInfo);
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
        if ((megaWallsStats.getGames_played() <= 25 && megaWallsStats.getFkdr() > 3.5f) ||
                (megaWallsStats.getGames_played() <= 250 && megaWallsStats.getFkdr() > 5f) ||
                (megaWallsStats.getGames_played() <= 500 && megaWallsStats.getFkdr() > 8f) ||
                megaWallsStats.getWlr() * megaWallsStats.getFkdr() > 0.33F * 1F * 6F) { // 6 times the average win/loss times the average fkdr
            return new ChatComponentText(EnumChatFormatting.GRAY + " played : " + EnumChatFormatting.GOLD + megaWallsStats.getGames_played()
                    + EnumChatFormatting.GRAY + " games, fkd : " + EnumChatFormatting.GOLD + String.format("%.1f", megaWallsStats.getFkdr())
                    + EnumChatFormatting.GRAY + " FK/game : " + EnumChatFormatting.GOLD + String.format("%.1f", megaWallsStats.getFkpergame())
                    + EnumChatFormatting.GRAY + " W/L : " + EnumChatFormatting.GOLD + String.format("%.1f", megaWallsStats.getWlr()));

        }
        return null;
    }

    private static IChatComponent checkMaxKits(String playername, MegaWallsStats megaWallsStats, GeneralInfo generalInfo) {
        if (megaWallsStats.getGames_played() >= 15 || generalInfo.getNetworkLevel() > 100F) {
            return null;
        }
        final boolean firstGame = megaWallsStats.getGames_played() == 0;
        final boolean secondFlag = generalInfo.getCompletedQuests() < 30 && generalInfo.getNetworkLevel() > 25f || megaWallsStats.getGames_played() < 3;
        final JsonObject classesdata = megaWallsStats.getClassesdata();
        IChatComponent imsg = null;
        if (classesdata == null) {
            return null;
        }
        if (ScoreboardTracker.isInMwGame) {
            final ScorePlayerTeam team = Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(playername);
            final String classTag = EnumChatFormatting.getTextWithoutFormattingCodes(team.getColorSuffix().replace("[", "").replace("]", "").replace(" ", ""));
            final MWClass mwClass = MWClass.fromTag(classTag);
            if (mwClass != null) {
                final JsonObject entryclassobj = JsonUtil.getJsonObject(classesdata, mwClass.className.toLowerCase());
                if (entryclassobj != null) {
                    if (firstGame) {
                        imsg = getMaxKitMsgFirstGame(mwClass.className, entryclassobj);
                    } else if (secondFlag) {
                        imsg = getMaxKitMsg(mwClass.className, entryclassobj, generalInfo.getCompletedQuests(), (int) generalInfo.getNetworkLevel(), megaWallsStats.getGames_played());
                    }
                }
            }
            return imsg;
        }
        for (final Map.Entry<String, JsonElement> entry : classesdata.entrySet()) {
            if (entry.getValue() instanceof JsonObject) {
                final JsonObject entryclassobj = entry.getValue().getAsJsonObject();
                if (imsg == null) {
                    if (firstGame) {
                        imsg = getMaxKitMsgFirstGame(entry.getKey(), entryclassobj);
                    } else if (secondFlag) {
                        imsg = getMaxKitMsg(entry.getKey(), entryclassobj, generalInfo.getCompletedQuests(), (int) generalInfo.getNetworkLevel(), megaWallsStats.getGames_played());
                    }
                } else {
                    final IChatComponent classMsg = getFormattedClassMsg(entry.getKey(), entryclassobj, firstGame);
                    if (classMsg != null) {
                        imsg.appendSibling(classMsg);
                    }
                }
            }
        }
        return imsg;
    }

    private static IChatComponent getMaxKitMsgFirstGame(String className, JsonObject entryclassobj) {
        final int skill_level_a = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_a"), 1); //skill
        final int skill_level_d = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_d"), 1); //kit
        if (skill_level_a >= 4 || skill_level_d >= 4) {
            return new ChatComponentText(EnumChatFormatting.GRAY + " never played and has :").appendSibling(getFormattedClassMsg(className, entryclassobj, true));
        }
        return null;
    }

    private static IChatComponent getMaxKitMsg(String className, JsonObject entryclassobj, int quests, int networklevel, int gameplayed) {
        final int skill_level_a = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_a"), 1); //skill
        final int skill_level_b = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_b"), 1); //passive1
        final int skill_level_c = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_c"), 1); //passive2
        final int skill_level_d = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_d"), 1); //kit
        if (skill_level_a == 5 && skill_level_b == 3 && skill_level_c == 3 && skill_level_d == 5) {
            return new ChatComponentText(EnumChatFormatting.GRAY + " played " + EnumChatFormatting.GOLD + gameplayed + EnumChatFormatting.GRAY + " games"
                    + EnumChatFormatting.GRAY + ", network lvl " + EnumChatFormatting.GOLD + networklevel
                    + EnumChatFormatting.GRAY + ", with " + EnumChatFormatting.GOLD + quests + EnumChatFormatting.GRAY + " quests"
                    + EnumChatFormatting.GRAY + " and has :").appendSibling(getFormattedClassMsg(className, entryclassobj, false));
        }
        return null;
    }

    private static IChatComponent getFormattedClassMsg(String className, JsonObject entryclassobj, boolean firstgame) {
        final int skill_level_a = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_a"), 1); //skill
        final int skill_level_b = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_b"), 1); //passive1
        final int skill_level_c = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_c"), 1); //passive2
        final int skill_level_d = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_d"), 1); //kit
        final int skill_level_g = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_g"), 1); //gathering
        if (firstgame ? skill_level_a >= 4 || skill_level_d >= 4 : skill_level_a == 5 && skill_level_b == 3 && skill_level_c == 3 && skill_level_d == 5) {
            return new ChatComponentText(" " + EnumChatFormatting.GOLD + className + " "
                    + (skill_level_d == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_d) + " "
                    + (skill_level_a == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_a) + " "
                    + (skill_level_b == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_b) + " "
                    + (skill_level_c == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_c) + " "
                    + (skill_level_g == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_g));
        }
        return null;
    }

    private static IChatComponent checkLegendarySkins(MegaWallsStats megaWallsStats) {
        final float ratio = megaWallsStats.getLegSkins() * 12f / (megaWallsStats.getGames_played() == 0 ? 1 : megaWallsStats.getGames_played());
        if (ratio >= 1) {
            return new ChatComponentText(EnumChatFormatting.GRAY + " played : " + EnumChatFormatting.GOLD + megaWallsStats.getGames_played()
                    + EnumChatFormatting.GRAY + " games, and has : " + EnumChatFormatting.GOLD + megaWallsStats.getLegSkins()
                    + EnumChatFormatting.GRAY + " legendary skin" + (megaWallsStats.getLegSkins() > 1 ? "s" : ""));
        }
        return null;
    }

    private static IChatComponent getMythicRandomMsg(int networkLevel, int completedQuests) {
        return new ChatComponentText(EnumChatFormatting.GRAY + " picked "
                + EnumChatFormatting.GOLD + "random"
                + EnumChatFormatting.GRAY + ", network lvl " + EnumChatFormatting.GOLD + networkLevel
                + EnumChatFormatting.GRAY + " and " + EnumChatFormatting.GOLD + completedQuests + EnumChatFormatting.GRAY + " quests");
    }

    private static IChatComponent checkRandomKit(GeneralInfo generalInfo, HypixelPlayerData playerdata) {
        if (((int) generalInfo.getNetworkLevel() + generalInfo.getCompletedQuests()) < 11) {
            final LoginData loginData = new LoginData(playerdata.getPlayerData());
            if (loginData.isNonRanked()) {
                return new ChatComponentText(EnumChatFormatting.GRAY + " picked "
                        + EnumChatFormatting.GOLD + "random"
                        + EnumChatFormatting.GRAY + ", network lvl " + EnumChatFormatting.GOLD + ((int) generalInfo.getNetworkLevel())
                        + EnumChatFormatting.GRAY + " and " + EnumChatFormatting.GOLD + generalInfo.getCompletedQuests() + EnumChatFormatting.GRAY + " quests");
            }
        }
        return null;
    }

    private static void addScanMessageToChat(NetworkPlayerInfo netInfo, IChatComponent imsg) {
        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW())
                .appendSibling(NameUtil.getFormattedNameWithPlanckeClickEvent(netInfo))
                .appendSibling(imsg)
        );
    }

}