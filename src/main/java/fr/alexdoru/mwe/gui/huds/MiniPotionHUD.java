package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.utils.ColorUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;

public class MiniPotionHUD extends AbstractRenderer {

    private static final int GAP = 2;

    private final String[] strings = new String[7];
    private final int[] colors = new int[7];

    public MiniPotionHUD() {
        super(MWEConfig.miniPotionHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
        int count = 0;
        for (final PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
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
            count++;
        }
        if (count == 0) return;
        this.renderStrings(resolution, count);
    }

    @Override
    public void renderDummy(ScaledResolution resolution) {
        strings[0] = "3";
        colors[0] = ColorUtil.getColorInt(EnumChatFormatting.LIGHT_PURPLE);
        this.renderStrings(resolution, 1);
    }

    private void renderStrings(ScaledResolution resolution, int count) {
        final FontRenderer fr = mc.fontRendererObj;
        final int width;
        final int height;
        if (MWEConfig.verticalMiniPotionHUD) {
            int maxWidth = 0;
            for (int i = 0; i < count; i++) {
                maxWidth = Math.max(maxWidth, fr.getStringWidth(strings[i]));
            }
            width = maxWidth;
            height = count * (fr.FONT_HEIGHT + GAP) - GAP;
        } else {
            int totalWidth = 0;
            for (int i = 0; i < count; i++) {
                totalWidth += fr.getStringWidth(strings[i]) + GAP;
            }
            width = totalWidth - GAP;
            height = fr.FONT_HEIGHT;
        }
        this.rendererPosition.applyTransform(resolution, width, height);
        int x = 0;
        int y = 0;
        for (int i = 0; i < count; i++) {
            final int stringWidth = fr.getStringWidth(strings[i]);
            if (MWEConfig.verticalMiniPotionHUD) {
                fr.drawStringWithShadow(strings[i], (width - stringWidth) / 2F, y, colors[i]);
                y += fr.FONT_HEIGHT + GAP;
            } else {
                fr.drawStringWithShadow(strings[i], x, y, colors[i]);
                x += stringWidth + GAP;
            }
        }
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        if (!this.rendererPosition.isEnabled() || mc.thePlayer.getActivePotionEffects().isEmpty()) {
            return false;
        }
        return !MWEConfig.showMiniPotionHUDOnlyMW || this.parser.isInMwGame();
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
