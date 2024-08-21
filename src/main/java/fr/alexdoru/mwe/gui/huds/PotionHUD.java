package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PotionHUD extends AbstractRenderer {

    private final List<PotionEffect> dummyList = new ArrayList<>();

    public PotionHUD() {
        super(MWEConfig.potionHUDPosition);
        dummyList.add(new PotionEffect(Potion.absorption.id, 600, 0));
        dummyList.add(new PotionEffect(Potion.regeneration.id, 50, 0));
        dummyList.add(new PotionEffect(Potion.resistance.id, 100, 1));
    }

    @Override
    public void render(ScaledResolution resolution) {
        final Collection<PotionEffect> potionEffects = mc.thePlayer.getActivePotionEffects();
        if (potionEffects.isEmpty()) return;
        int amountEffects = 0;
        int maxWidth = 0;
        for (final PotionEffect potioneffect : potionEffects) {
            if (potioneffect.getDuration() == 0) continue;
            final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
            if (potion.hasStatusIcon()) {
                amountEffects++;
                if (!MWEConfig.horizontalPotionHUD && MWEConfig.showPotionEffectNames) {
                    maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(getFormattedEffect(potioneffect, potion)));
                }
            }
        }
        if (amountEffects == 0) return;
        final int width;
        final int height;
        if (MWEConfig.horizontalPotionHUD) {
            width = (27 + 2) * amountEffects - 2;
            height = 27 + 5;
        } else {
            if (MWEConfig.showPotionEffectNames) {
                width = 27 + 4 + maxWidth;
            } else {
                width = 27;
            }
            height = (27 + 7) * amountEffects - 7;
        }
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, width, height, -width / 2, -height / 2);
        this.renderPotionEffects(potionEffects, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY());
    }

    @Override
    public void renderDummy() {
        int amountEffects = 0;
        int maxWidth = 0;
        for (final PotionEffect potioneffect : dummyList) {
            if (potioneffect.getDuration() == 0) continue;
            final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
            if (potion.hasStatusIcon()) {
                amountEffects++;
                if (!MWEConfig.horizontalPotionHUD && MWEConfig.showPotionEffectNames) {
                    maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(getFormattedEffect(potioneffect, potion)));
                }
            }
        }
        final int width;
        final int height;
        if (MWEConfig.horizontalPotionHUD) {
            width = (27 + 2) * amountEffects - 2;
            height = 27 + 5;
        } else {
            if (MWEConfig.showPotionEffectNames) {
                width = 27 + 4 + maxWidth;
            } else {
                width = 27;
            }
            height = (27 + 7) * amountEffects - 7;
        }
        final int xDrawPots = this.guiPosition.getAbsoluteRenderX() - width / 2;
        final int yDrawPots = this.guiPosition.getAbsoluteRenderY() - height / 2;
        this.renderPotionEffects(dummyList, xDrawPots, yDrawPots);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.guiPosition.isEnabled();
    }

    private void renderPotionEffects(Collection<PotionEffect> potionEffects, int xDrawPots, int yDrawPots) {
        final FontRenderer fr = mc.fontRendererObj;
        for (final PotionEffect potioneffect : potionEffects) {
            if (potioneffect.getDuration() == 0) continue;
            final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
            if (potion.hasStatusIcon()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(inventoryBackground);
                final int index = potion.getStatusIconIndex();
                GlStateManager.translate(xDrawPots, yDrawPots, 0d);
                GlStateManager.scale(1.5d, 1.5d, 1.5d);
                this.drawTexturedModalRect(0, 0, index % 8 * 18, 198 + index / 8 * 18, 18, 18);
                GlStateManager.scale(2d / 3d, 2d / 3d, 2d / 3d);
                GlStateManager.translate(-xDrawPots, -yDrawPots, 0d);
                final String potDuration = potioneffect.getDuration() > 60 * 60 * 20 ? "**:**" : Potion.getDurationString(potioneffect);
                if (!MWEConfig.horizontalPotionHUD && MWEConfig.showPotionEffectNames) {
                    final String fullName = getFormattedEffect(potioneffect, potion);
                    fr.drawStringWithShadow(fullName, xDrawPots + 27 + 4, yDrawPots + 2, MWEConfig.potionHUDTextColor);
                    fr.drawStringWithShadow(potDuration, xDrawPots + 27 + 4, yDrawPots + 27 - fr.FONT_HEIGHT - 2, MWEConfig.potionHUDTextColor);
                } else {
                    fr.drawStringWithShadow(potDuration, xDrawPots + 27 - fr.getStringWidth(potDuration) - 1, yDrawPots + 27 - fr.FONT_HEIGHT + 5, MWEConfig.potionHUDTextColor);
                    if (potioneffect.getAmplifier() != 0) {
                        final String potionLevel = ChatUtil.intToRoman(potioneffect.getAmplifier() + 1);
                        fr.drawStringWithShadow(potionLevel, xDrawPots + 27 - fr.getStringWidth(potionLevel) - 1, yDrawPots, MWEConfig.potionHUDTextColor);
                    }
                }
                if (MWEConfig.horizontalPotionHUD) {
                    xDrawPots += 27 + 2;
                } else {
                    yDrawPots += 27 + 7;
                }
            }
        }
    }

    private static String getFormattedEffect(PotionEffect potioneffect, Potion potion) {
        final String name;
        if (potioneffect.getAmplifier() == 0) {
            name = I18n.format(potion.getName());
        } else {
            name = I18n.format(potion.getName()) + " " + ChatUtil.intToRoman(potioneffect.getAmplifier() + 1);
        }
        return name;
    }

}
