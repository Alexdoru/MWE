package fr.alexdoru.megawallsenhancementsmod.api.cache;


import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassStats;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;

public class PrestigeVCache {

    private static final HashMap<String, PlayerPrestigeData> prestigeDataMap = new HashMap<>();

    public static void clearCache() {
        prestigeDataMap.clear();
    }

    public static EnumChatFormatting checkCacheAndUpdate(String uuid, String playername, String classTag) {

        MWClass mwClass = MWClass.fromTag(classTag);
        if (mwClass == null) {
            return null;
        }

        PlayerPrestigeData playerPrestigeData = prestigeDataMap.get(uuid);

        if (playerPrestigeData == null) {
            createPlayerPrestigeData(uuid, mwClass, playername);
            return null;
        }

        EnumChatFormatting chatFormatting = playerPrestigeData.playersPrestigeColors.get(mwClass);
        if (chatFormatting == null) {
            createClassData(uuid, mwClass, playerPrestigeData, playername);
            return null;
        }

        return chatFormatting;

    }

    private static void createPlayerPrestigeData(String uuid, MWClass mwClass, String playername) {

        Multithreading.addTaskToQueue(() -> {

            CachedHypixelPlayerData playerdata;
            try {
                playerdata = new CachedHypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                MegaWallsClassStats mwclassstats = new MegaWallsClassStats(playerdata.getPlayerData(), mwClass.className);
                PlayerPrestigeData playerPrestigeData = new PlayerPrestigeData();
                playerPrestigeData.addClass(mwClass, mwclassstats.getClasspoints(), mwclassstats.getCoins());
                prestigeDataMap.put(uuid, playerPrestigeData);
                NameUtil.updateGameProfileAndName(playername, false);
            } catch (ApiException e) {
                prestigeDataMap.put(uuid, new PlayerPrestigeData());
            }

            return null;

        });

    }

    private static void createClassData(String uuid, MWClass mwClass, PlayerPrestigeData playerPrestigeData, String playername) {

        Multithreading.addTaskToQueue(() -> {

            CachedHypixelPlayerData playerdata;
            try {
                playerdata = new CachedHypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                MegaWallsClassStats mwclassstats = new MegaWallsClassStats(playerdata.getPlayerData(), mwClass.className);
                playerPrestigeData.addClass(mwClass, mwclassstats.getClasspoints(), mwclassstats.getCoins());
                NameUtil.updateGameProfileAndName(playername, false);
            } catch (ApiException e) {
                playerPrestigeData.addClass(mwClass, 0, 0);
            }

            return null;

        });

    }

}

class PlayerPrestigeData {

    public final HashMap<MWClass, EnumChatFormatting> playersPrestigeColors = new HashMap<>();

    public void addClass(MWClass mwClass, int classpoints, int coins) {
        if (coins < 1000000 || classpoints < 10000) {
            playersPrestigeColors.put(mwClass, EnumChatFormatting.GOLD);
        } else if (classpoints < 13000) {
            playersPrestigeColors.put(mwClass, EnumChatFormatting.DARK_PURPLE);
        } else if (classpoints < 19000) {
            playersPrestigeColors.put(mwClass, EnumChatFormatting.DARK_BLUE);
        } else if (classpoints < 28000) {
            playersPrestigeColors.put(mwClass, EnumChatFormatting.DARK_AQUA);
        } else if (classpoints < 40000) {
            playersPrestigeColors.put(mwClass, EnumChatFormatting.DARK_GREEN);
        } else {
            playersPrestigeColors.put(mwClass, EnumChatFormatting.DARK_RED);
        }
    }

}
