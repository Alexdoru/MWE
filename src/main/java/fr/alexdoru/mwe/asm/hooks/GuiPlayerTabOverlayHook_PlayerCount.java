package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        list.add(EnumChatFormatting.GREEN + "Players: " + EnumChatFormatting.GOLD + i);
        if (GuiPlayerTabOverlayHook_HideHeaderFooter.shouldRenderHeaderFooter()) {
            list.addAll(listIn);
        }
        return list;
    }

}
