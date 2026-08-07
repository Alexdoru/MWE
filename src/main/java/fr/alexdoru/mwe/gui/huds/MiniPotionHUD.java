package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collection;

public class MiniPotionHUD extends AbstractRenderer {

    private final String[] strings = new String[7];
    private final int[] colors = new int[7];

    public MiniPotionHUD() {
        super(MWEConfig.miniPotionHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Collection<PotionEffect> potionEffects = mc.thePlayer.getActivePotionEffects();
        if (potionEffects.isEmpty()) return;
        this.rendererPosition.updateAbsolutePosition(resolution);
        int count = 0;
        int len = 0;
        for (final PotionEffect effect : potionEffects) {
            if (count >= strings.length) break;
            switch (effect.getPotionID()) {
                case 1: // speed
                    colors[count] = ColorUtil.getColorInt(EnumChatFormatting.AQUA);
                    break;
                case 5: // strength
                    colors[count] = ColorUtil.getColorInt(EnumChatFormatting.RED);
                    break;
                case 8: // jump boost
                    colors[count] = 0xFF4DFF2F;
                    break;
                case 10: // regeneration
                    colors[count] = 0xFFFF66D5;
                    break;
                case 11: // resistance
                    colors[count] = 0xFF515971;
                    break;
                case 14: // invisibility
                    colors[count] = 0xFFF5F0DA;
                    break;
                case 22: // absorption
                    if (mc.thePlayer.getAbsorptionAmount() <= 0F) {
                        continue;
                    }
                    colors[count] = 0xFFFFEB13;
                    break;
                default:
                    continue;
            }
            strings[count] = formatDuration(effect);
            len += mc.fontRendererObj.getStringWidth(strings[count]) + 2;
            count++;
        }
        len -= 2;
        int x = this.rendererPosition.getAbsoluteRenderX() - len / 2;
        for (int i = 0; i < count; i++) {
            mc.fontRendererObj.drawStringWithShadow(strings[i], x, this.rendererPosition.getAbsoluteRenderY(), colors[i]);
            x += mc.fontRendererObj.getStringWidth(strings[i]) + 2;
        }
    }

    @Override
    public void renderDummy() {
        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, EnumChatFormatting.LIGHT_PURPLE + "3", this.rendererPosition.getAbsoluteRenderX(), this.rendererPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        if (MWEConfig.showMiniPotionHUDOnlyMW) {
            return this.rendererPosition.isEnabled() && ScoreboardTracker.isInMwGame();
        }
        return this.rendererPosition.isEnabled();
    }

    private String formatDuration(PotionEffect effect) {
        if (effect.getDuration() > 999 * 20) return "∞";
        if (effect.getDuration() >= 60) {
            return String.valueOf(effect.getDuration() / 20);
        } else if (effect.getDuration() >= 0) {
            return effect.getDuration() / 20 + "." + effect.getDuration() % 20 / 2;
        }
        return "";
    }

}
