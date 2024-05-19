package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collection;

public class MiniPotionHUD extends AbstractRenderer {

    public static MiniPotionHUD instance;

    public MiniPotionHUD() {
        super(ConfigHandler.miniPotionHUDPosition);
        instance = this;
    }

    @Override
    public void render(ScaledResolution resolution) {
        final Collection<PotionEffect> potionEffects = mc.thePlayer.getActivePotionEffects();
        if (potionEffects.isEmpty()) return;
        this.guiPosition.updateAbsolutePosition(resolution);
        final String[] strings = new String[5];
        final int[] colors = new int[5];
        int index = 0;
        int len = 0;
        for (final PotionEffect effect : potionEffects) {
            switch (effect.getPotionID()) {
                case 1: // speed
                    strings[index] = formatDuration(effect);
                    colors[index] = mc.fontRendererObj.getColorCode('b');
                    len += mc.fontRendererObj.getStringWidth(strings[index]) + 2;
                    index++;
                    break;
                case 5: // strength
                    strings[index] = formatDuration(effect);
                    colors[index] = mc.fontRendererObj.getColorCode('c');
                    len += mc.fontRendererObj.getStringWidth(strings[index]) + 2;
                    index++;
                    break;
                case 8: // jump boost
                    strings[index] = formatDuration(effect);
                    colors[index] = 0xFF4DFF2F;
                    len += mc.fontRendererObj.getStringWidth(strings[index]) + 2;
                    index++;
                    break;
                case 10: // regeneration
                    strings[index] = formatDuration(effect);
                    colors[index] = 0xFFFF66D5;
                    len += mc.fontRendererObj.getStringWidth(strings[index]) + 2;
                    index++;
                    break;
                case 11: // resistance
                    strings[index] = formatDuration(effect);
                    colors[index] = 0xFF515971;
                    len += mc.fontRendererObj.getStringWidth(strings[index]) + 2;
                    index++;
                    break;
                case 14: // invisibility
                    strings[index] = formatDuration(effect);
                    colors[index] = 0xFFF5F0DA;
                    len += mc.fontRendererObj.getStringWidth(strings[index]) + 2;
                    index++;
                    break;
            }
        }
        len -= 2;
        int x = this.guiPosition.getAbsoluteRenderX() - len / 2;
        for (int i = 0; i < 5; i++) {
            if (strings[i] == null) return;
            mc.fontRendererObj.drawStringWithShadow(strings[i], x, this.guiPosition.getAbsoluteRenderY(), colors[i]);
            x += mc.fontRendererObj.getStringWidth(strings[i]) + 2;
        }
    }

    @Override
    public void renderDummy() {
        drawCenteredString(mc.fontRendererObj, EnumChatFormatting.LIGHT_PURPLE + "3", this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showMiniPotionHUD || (ConfigHandler.showMiniPotionHUDOnlyMW && ScoreboardTracker.isInMwGame);
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
