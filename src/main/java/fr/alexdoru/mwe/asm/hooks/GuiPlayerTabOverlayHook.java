package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook {

    @Nonnull
    public static List<String> addPlayerCountInHeader(@Nonnull List<String> listIn) {
        if (!ConfigHandler.showPlayercountTablist) {
            return listIn;
        }
        final int i = Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap().size();
        if (i < 2) {
            return GuiPlayerTabOverlayHook_HideHeaderFooter.shouldHideFooter() ? new ArrayList<>() : listIn;
        }
        final List<String> list;
        if (GuiPlayerTabOverlayHook_HideHeaderFooter.shouldHideFooter()) {
            list = new ArrayList<>();
        } else {
            list = new ArrayList<>(listIn);
        }
        list.add(0, EnumChatFormatting.GREEN + "Players: " + EnumChatFormatting.GOLD + i);
        return list;
    }

    public static int fixMissplacedDrawRect(int l1) {
        return l1 % 2;
    }

}
