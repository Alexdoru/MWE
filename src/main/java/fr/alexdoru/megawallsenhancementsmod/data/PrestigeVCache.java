package fr.alexdoru.megawallsenhancementsmod.data;


import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassStats;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.utils.ColorUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

public class PrestigeVCache {

    private static final Map<String, PlayerPrestigeData> prestigeDataMap = new HashMap<>();

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
                final MegaWallsClassStats mwclassstats = new MegaWallsClassStats(CachedHypixelPlayerData.getPlayerData(uuid), mwClass.className);
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    final PlayerPrestigeData playerPrestigeData = new PlayerPrestigeData();
                    playerPrestigeData.addClass(mwClass, mwclassstats.getClasspoints());
                    prestigeDataMap.put(uuid, playerPrestigeData);
                    NameUtil.updateMWPlayerDataAndEntityData(playername, false);
                });
            } catch (ApiException e) {
                Minecraft.getMinecraft().addScheduledTask(() ->
                        prestigeDataMap.put(uuid, new PlayerPrestigeData())
                );
            }
            return null;
        });
    }

    private static void createClassData(String uuid, MWClass mwClass, PlayerPrestigeData playerPrestigeData, String playername) {
        playerPrestigeData.addClass(mwClass, 2_000);
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final MegaWallsClassStats mwclassstats = new MegaWallsClassStats(CachedHypixelPlayerData.getPlayerData(uuid), mwClass.className);
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    playerPrestigeData.addClass(mwClass, mwclassstats.getClasspoints());
                    NameUtil.updateMWPlayerDataAndEntityData(playername, false);
                });
            } catch (ApiException e) {
                Minecraft.getMinecraft().addScheduledTask(() ->
                        playerPrestigeData.addClass(mwClass, 2_000)
                );
            }
            return null;
        });
    }

    private static class PlayerPrestigeData {

        private final HashMap<MWClass, EnumChatFormatting> playersPrestigeColors = new HashMap<>();

        private void addClass(MWClass mwClass, int classpoints) {
            this.playersPrestigeColors.put(mwClass, ColorUtil.getPrestige4Color(classpoints));
        }

    }

}