package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook_PlayerCount {

    @Nonnull
    public static List<String> addPlayerCountInHeader(@Nonnull List<String> listIn) {
        if (!ConfigHandler.showPlayercountTablist) {
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
