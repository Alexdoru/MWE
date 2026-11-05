package fr.alexdoru.mwe.asm.hooks.mc.gui;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.PlayerDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

public class GuiPlayerTabOverlayHook_PlayerCount {

    private static int textWidth;

    @Nullable
    public static List<String> modifyList(@Nullable List<String> listIn) {
        if (MWEConfig.showPlayercountTablist) {
            final String text = getPlayerCountText();
            textWidth = text == null ? 0 : Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
            return addToList(listIn, text);
        }
        textWidth = 0;
        return listIn;
    }

    public static int modifyWidth(int original) {
        return Math.max(original, textWidth);
    }

    @Nullable
    private static String getPlayerCountText() {
        final Collection<NetworkPlayerInfo> playerInfoMap = Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap();
        final int playerCount = playerInfoMap.size();
        if (playerCount < 2) {
            return null;
        }
        if (MWEConfig.showFakePlayersInTab) {
            int nicks = 0;
            for (final NetworkPlayerInfo netInfo : playerInfoMap) {
                if (PlayerDataManager.isNickedPlayer(netInfo.getGameProfile().getId())) {
                    nicks++;
                }
            }
            return GREEN + "Players: " + GOLD + playerCount + (nicks == 0 ? "" : WHITE + " (" + DARK_RED + nicks + WHITE + ")");
        } else {
            return GREEN + "Players: " + GOLD + playerCount;
        }
    }

    @Nullable
    private static List<String> addToList(@Nullable List<String> listIn, @Nullable String text) {
        if (text == null) {
            return listIn;
        }
        if (listIn == null) {
            return Collections.singletonList(text);
        }
        final List<String> list = new ArrayList<>(1 + listIn.size());
        list.add(text);
        list.addAll(listIn);
        return list;
    }

}
