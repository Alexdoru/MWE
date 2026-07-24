package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.PlayerDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

public class GuiPlayerTabOverlayHook_PlayerCount {

    @NotNull
    public static List<String> addPlayerCountInHeader(@NotNull List<String> listIn) {
        if (!MWEConfig.showPlayercountTablist) {
            return listIn;
        }
        final Collection<NetworkPlayerInfo> playerInfoMap = Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap();
        final int i = playerInfoMap.size();
        final List<String> list = new ArrayList<>();
        if (i < 2) {
            return GuiPlayerTabOverlayHook_HideHeaderFooter.shouldRenderHeaderFooter() ? listIn : list;
        }
        if (MWEConfig.showFakePlayersInTab) {
            int nicks = 0;
            for (final NetworkPlayerInfo netInfo : playerInfoMap) {
                if (PlayerDataManager.isNickedPlayer(netInfo.getGameProfile().getId())) {
                    nicks++;
                }
            }
            list.add(GREEN + "Players: " + GOLD + i + (nicks == 0 ? "" : WHITE + " (" + GOLD + nicks + WHITE + ")"));
        } else {
            list.add(GREEN + "Players: " + GOLD + i);
        }
        if (GuiPlayerTabOverlayHook_HideHeaderFooter.shouldRenderHeaderFooter()) {
            list.addAll(listIn);
        }
        return list;
    }

}
