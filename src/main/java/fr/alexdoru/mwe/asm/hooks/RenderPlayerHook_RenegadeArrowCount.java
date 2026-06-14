package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class RenderPlayerHook_RenegadeArrowCount {

    public static StringBuilder getArrowCount(StringBuilder original, AbstractClientPlayer entityIn) {
        if (MWEConfig.renegadeArrowCount && ScoreboardTracker.isInMwGame()) {
            final List<Long> list = MWE.INSTANCE().getRenegadeTracker().getArrowsForPlayer(entityIn.getName());
            if (list == null || list.isEmpty()) {
                return original;
            }
            return original.append(EnumChatFormatting.RESET).append("  ").append(list.size()).append(EnumChatFormatting.GREEN).append(" ➹");
        }
        return original;
    }

}
