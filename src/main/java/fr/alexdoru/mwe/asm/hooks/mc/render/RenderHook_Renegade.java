package fr.alexdoru.mwe.asm.hooks.mc.render;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class RenderHook_Renegade {

    public static StringBuilder appendToScore(StringBuilder sb, AbstractClientPlayer entity) {
        if (MWEConfig.renegadeArrowCount && ScoreboardTracker.isInMwGame()) {
            final List<Long> list = MWE.INSTANCE().getRenegadeTracker().getArrowsForPlayer(entity.getName());
            if (list == null || list.isEmpty()) {
                return sb;
            }
            return format(sb, list.size(), entity.getHealth());
        }
        return sb;
    }

    public static String appendToNametag(String original, EntityLivingBase entity, double distSq) {
        if (distSq >= 100.0D && MWEConfig.renegadeArrowCount && ScoreboardTracker.isInMwGame()) {
            final List<Long> list = MWE.INSTANCE().getRenegadeTracker().getArrowsForPlayer(entity.getName());
            if (list == null || list.isEmpty()) {
                return original;
            }
            return format(new StringBuilder().append(original), list.size(), entity.getHealth()).toString();
        }
        return original;
    }

    private static StringBuilder format(StringBuilder sb, int size, float health) {
        sb.append(EnumChatFormatting.RESET);
        if (health <= size * 2) {
            sb.append(EnumChatFormatting.GOLD);
        }
        return sb.append("  ").append(size).append(EnumChatFormatting.GREEN).append(" ➹");
    }

}
