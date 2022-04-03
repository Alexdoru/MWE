package fr.alexdoru.megawallsenhancementsmod.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.GeneralInfo;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsStats;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.utils.*;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class CommandScanGame extends CommandBase {

    /*
     * fills the hashmap with this instead of null when there is no match
     */
    public static final IChatComponent nomatch = new ChatComponentText("none");
    private static final HashMap<String, IChatComponent> scanmap = new HashMap<>();
    private static String scanGameId;

    public static void clearScanGameData() {
        scanGameId = null;
        scanmap.clear();
    }

    public static void onGameStart() {
        String currentGameId = GameInfoGrabber.getGameIDfromscoreboard();
        if (!currentGameId.equals("?") && scanGameId != null && !scanGameId.equals(currentGameId)) {
            clearScanGameData();
        }
    }

    public static boolean doesPlayerFlag(String uuid) {
        IChatComponent imsg = scanmap.get(uuid);
        return imsg != null && !imsg.equals(CommandScanGame.nomatch);
    }

    public static void put(String uuid, IChatComponent msg) {
        scanmap.put(uuid, msg);
    }

    protected static IChatComponent getMessageStart(String playername) {
        IChatComponent imsg = new ChatComponentText(ChatUtil.getTagMW());
        if (FKCounterMod.isInMwGame) {
            imsg.appendSibling(ChatUtil.makeReportButtons(playername, "cheating", "", ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.SUGGEST_COMMAND));
        }
        return imsg;
    }

    @Override
    public String getCommandName() {
        return "scangame";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/scangame";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.apikeyMissingErrorMsg()));
            return;
        }

        String currentGameId = GameInfoGrabber.getGameIDfromscoreboard();
        Collection<NetworkPlayerInfo> playerCollection = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();

        int i = 0;

        if (!currentGameId.equals("?") && currentGameId.equals(scanGameId)) {

            for (NetworkPlayerInfo networkPlayerInfo : playerCollection) {

                String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
                IChatComponent imsg = scanmap.get(uuid);

                if (imsg == null) {
                    i++;
                    Multithreading.addTaskToQueue(new ScanPlayerTask(networkPlayerInfo));
                } else if (!imsg.equals(nomatch)) {
                    ChatUtil.addChatMessage(getMessageStart(networkPlayerInfo.getGameProfile().getName()).appendSibling(imsg));
                }

            }
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Scanning " + i + " more players..."));

        } else {

            scanGameId = GameInfoGrabber.getGameIDfromscoreboard();

            for (NetworkPlayerInfo networkPlayerInfo : playerCollection) {
                i++;
                Multithreading.addTaskToQueue(new ScanPlayerTask(networkPlayerInfo));
            }

            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Scanning " + i + " players..."));

        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

}

class ScanPlayerTask implements Callable<String> {

    final NetworkPlayerInfo networkPlayerInfo;

    public ScanPlayerTask(NetworkPlayerInfo networkPlayerInfoIn) {
        this.networkPlayerInfo = networkPlayerInfoIn;
    }

    @Override
    public String call() {

        String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");

        try {

            String playername = networkPlayerInfo.getGameProfile().getName();
            HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
            MegaWallsStats megawallsstats = new MegaWallsStats(playerdata.getPlayerData());
            IChatComponent imsg = null;

            if ((megawallsstats.getGames_played() <= 25 && megawallsstats.getFkdr() > 3.5f) ||
                    (megawallsstats.getGames_played() <= 250 && megawallsstats.getFkdr() > 5f) ||
                    (megawallsstats.getGames_played() <= 500 && megawallsstats.getFkdr() > 8f) ||
                    (megawallsstats.getFkdr() > 10f)) {

                imsg = new ChatComponentText(getFormattedName(networkPlayerInfo)
                        + EnumChatFormatting.GRAY + " played : " + EnumChatFormatting.GOLD + megawallsstats.getGames_played()
                        + EnumChatFormatting.GRAY + " games , fkd : " + EnumChatFormatting.GOLD + String.format("%.1f", megawallsstats.getFkdr())
                        + EnumChatFormatting.GRAY + " FK/game : " + EnumChatFormatting.GOLD + String.format("%.1f", megawallsstats.getFkpergame())
                        + EnumChatFormatting.GRAY + " W/L : " + EnumChatFormatting.GOLD + String.format("%.1f", megawallsstats.getWlr()));

            } else if (megawallsstats.getGames_played() < 15) {

                GeneralInfo generalInfo = new GeneralInfo(playerdata.getPlayerData());
                boolean firstGame = megawallsstats.getGames_played() == 0;
                boolean secondFlag = generalInfo.getCompletedQuests() < 20 && generalInfo.getNetworkLevel() > 42f;
                JsonObject classesdata = megawallsstats.getClassesdata();

                if (FKCounterMod.isInMwGame) {

                    ScorePlayerTeam team = Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(playername);
                    String classTag = EnumChatFormatting.getTextWithoutFormattingCodes(team.getColorSuffix().replace("[", "").replace("]", "").replace(" ", ""));
                    MWClass mwClass = MWClass.fromTag(classTag);
                    if (mwClass != null) {
                        JsonObject entryclassobj = classesdata.getAsJsonObject(mwClass.className.toLowerCase());
                        if (firstGame) {
                            imsg = getMsgFirstGame(mwClass.className, entryclassobj);
                        } else if (secondFlag) {
                            imsg = getMsg(mwClass.className, entryclassobj, generalInfo.getCompletedQuests(), (int) generalInfo.getNetworkLevel(), megawallsstats.getGames_played());
                        }
                    }

                } else {

                    for (Map.Entry<String, JsonElement> entry : classesdata.entrySet()) {
                        if (entry.getValue() != null && entry.getValue().isJsonObject()) {
                            JsonObject entryclassobj = entry.getValue().getAsJsonObject();
                            if (imsg == null) {
                                if (firstGame) {
                                    imsg = getMsgFirstGame(entry.getKey(), entryclassobj);
                                } else if (secondFlag) {
                                    imsg = getMsg(entry.getKey(), entryclassobj, generalInfo.getCompletedQuests(), (int) generalInfo.getNetworkLevel(), megawallsstats.getGames_played());
                                }
                            } else {
                                IChatComponent classMsg = getFormattedClassMsg(entry.getKey(), entryclassobj, firstGame);
                                if (classMsg != null) {
                                    imsg.appendSibling(classMsg);
                                }
                            }
                        }
                    }

                }

            }

            if (imsg != null) {
                ChatUtil.addChatMessage(CommandScanGame.getMessageStart(playername).appendSibling(imsg));
                CommandScanGame.put(uuid, imsg);
                NameUtil.updateGameProfileAndName(networkPlayerInfo);
            } else {
                CommandScanGame.put(uuid, CommandScanGame.nomatch);
            }

        } catch (ApiException ignored) {
            CommandScanGame.put(uuid, CommandScanGame.nomatch);
        }

        return null;

    }

    private IChatComponent getMsgFirstGame(String className, JsonObject entryclassobj) {
        int skill_level_a = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_a"), 1); //skill
        int skill_level_d = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_d"), 1); //kit
        if (skill_level_a >= 4 || skill_level_d >= 4) {
            return new ChatComponentText(getFormattedName(networkPlayerInfo) + EnumChatFormatting.GRAY + " never played and has :").appendSibling(getFormattedClassMsg(className, entryclassobj, true));
        }
        return null;
    }

    private IChatComponent getMsg(String className, JsonObject entryclassobj, int quests, int networklevel, int gameplayed) {
        int skill_level_a = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_a"), 1); //skill
        int skill_level_b = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_b"), 1); //passive1
        int skill_level_c = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_c"), 1); //passive2
        int skill_level_d = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_d"), 1); //kit
        if (skill_level_a == 5 && skill_level_b == 3 && skill_level_c == 3 && skill_level_d == 5) {
            return new ChatComponentText(getFormattedName(networkPlayerInfo)
                    + EnumChatFormatting.GRAY + " played " + EnumChatFormatting.GOLD + gameplayed + EnumChatFormatting.GRAY + " games"
                    + EnumChatFormatting.GRAY + ", network lvl " + EnumChatFormatting.GOLD + networklevel
                    + EnumChatFormatting.GRAY + ", with " + EnumChatFormatting.GOLD + quests + EnumChatFormatting.GRAY + " quests"
                    + EnumChatFormatting.GRAY + " and has :").appendSibling(getFormattedClassMsg(className, entryclassobj, false));
        }
        return null;
    }

    private IChatComponent getFormattedClassMsg(String className, JsonObject entryclassobj, boolean firstgame) {
        int skill_level_a = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_a"), 1); //skill
        int skill_level_b = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_b"), 1); //passive1
        int skill_level_c = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_c"), 1); //passive2
        int skill_level_d = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_d"), 1); //kit
        int skill_level_g = Math.max(JsonUtil.getInt(entryclassobj, "skill_level_g"), 1); //gathering
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

    private String getFormattedName(NetworkPlayerInfo networkPlayerInfoIn) {
        if (networkPlayerInfoIn.getPlayerTeam() == null) {
            return networkPlayerInfoIn.getGameProfile().getName();
        }
        ScorePlayerTeam team = networkPlayerInfoIn.getPlayerTeam();
        return team.getColorPrefix().replace("\u00a7k", "").replace("O", "") + networkPlayerInfoIn.getGameProfile().getName() + team.getColorSuffix();
    }

}

