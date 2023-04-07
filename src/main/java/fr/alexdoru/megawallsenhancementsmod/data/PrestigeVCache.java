package fr.alexdoru.megawallsenhancementsmod.data;


import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassStats;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class PrestigeVCache {

    private static final ConcurrentHashMap<String, PlayerPrestigeData> prestigeDataMap = new ConcurrentHashMap<>();

    public static void clearCache() {
        prestigeDataMap.clear();
    }

    public static EnumChatFormatting checkCacheAndUpdate(String uuid, String playername, String classTag) {

        final MWClass mwClass = MWClass.fromTag(classTag);
        if (mwClass == null) {
            return null;
        }

        final PlayerPrestigeData playerPrestigeData = prestigeDataMap.get(uuid);

        if (playerPrestigeData == null) {
            createPlayerPrestigeData(uuid, mwClass, playername);
            return null;
        }

        final EnumChatFormatting chatFormatting = playerPrestigeData.playersPrestigeColors.get(mwClass);
        if (chatFormatting == null) {
            createClassData(uuid, mwClass, playerPrestigeData, playername);
            return null;
        }

        return chatFormatting;

    }

    private static void createPlayerPrestigeData(String uuid, MWClass mwClass, String playername) {

        prestigeDataMap.put(uuid, new PlayerPrestigeData());

        MultithreadingUtil.addTaskToQueue(() -> {

            try {
                final CachedHypixelPlayerData playerdata = new CachedHypixelPlayerData(uuid);
                final MegaWallsClassStats mwclassstats = new MegaWallsClassStats(playerdata.getPlayerData(), mwClass.className);
                final PlayerPrestigeData playerPrestigeData = new PlayerPrestigeData();
                playerPrestigeData.addClass(mwClass, mwclassstats.getClasspoints());
                prestigeDataMap.put(uuid, playerPrestigeData);
                Minecraft.getMinecraft().addScheduledTask(() -> NameUtil.updateMWPlayerDataAndEntityData(playername, false));
            } catch (ApiException e) {
                prestigeDataMap.put(uuid, new PlayerPrestigeData());
            }

            return null;

        });

    }

    private static void createClassData(String uuid, MWClass mwClass, PlayerPrestigeData playerPrestigeData, String playername) {

        playerPrestigeData.addClass(mwClass, 0);

        MultithreadingUtil.addTaskToQueue(() -> {

            try {
                final CachedHypixelPlayerData playerdata = new CachedHypixelPlayerData(uuid);
                final MegaWallsClassStats mwclassstats = new MegaWallsClassStats(playerdata.getPlayerData(), mwClass.className);
                playerPrestigeData.addClass(mwClass, mwclassstats.getClasspoints());
                Minecraft.getMinecraft().addScheduledTask(() -> NameUtil.updateMWPlayerDataAndEntityData(playername, false));
            } catch (ApiException e) {
                playerPrestigeData.addClass(mwClass, 0);
            }

            return null;

        });

    }

}

class PlayerPrestigeData {

    public final HashMap<MWClass, EnumChatFormatting> playersPrestigeColors = new HashMap<>();

    public void addClass(MWClass mwClass, int classpoints) {
        if (classpoints < 10000) {
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
