package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashSet;
import java.util.Set;

public class EnergyDisplayHUD extends AbstractRenderer {

    private static final Set<Item> VALID_ITEMS = new HashSet<>();

    private long timeStartRender;
    private int prevEnergyValue;

    static {
        VALID_ITEMS.add(Items.diamond_sword);
        VALID_ITEMS.add(Items.iron_sword);
        VALID_ITEMS.add(Items.golden_sword);
        VALID_ITEMS.add(Items.bow);
        VALID_ITEMS.add(Items.diamond_shovel);
    }

    public EnergyDisplayHUD() {
        super(MWEConfig.energyHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
        final Minecraft mc = Minecraft.getMinecraft();
        final ItemStack itemStack = mc.thePlayer.getHeldItem();
        if (itemStack == null) {
            return;
        }
        final Item item = itemStack.getItem();
        if (item == null) {
            return;
        }
        final int energy = mc.thePlayer.experienceLevel;
        final long currentTime = System.currentTimeMillis();
        if (VALID_ITEMS.contains(item)) {
            if (energy != prevEnergyValue) {
                this.timeStartRender = currentTime;
            }
            prevEnergyValue = energy;
        }
        if (energy == 0) {
            return;
        }
        if (this.timeStartRender + 2500L - currentTime > 0L) {
            this.guiPosition.updateAbsolutePosition(resolution);
            if (energy >= MWEConfig.highEnergyThreshold) {
                drawCenteredString(mc.fontRendererObj, EnumChatFormatting.BOLD.toString() + energy, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), MWEConfig.highEnergyHUDColor);
            } else {
                drawCenteredString(mc.fontRendererObj, String.valueOf(energy), this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), MWEConfig.lowEnergyHUDColor);
            }
        }
    }

    @Override
    public void renderDummy() {
        final int energy = 50;
        if (energy >= MWEConfig.highEnergyThreshold) {
            drawCenteredString(Minecraft.getMinecraft().fontRendererObj, EnumChatFormatting.BOLD.toString() + energy, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), MWEConfig.highEnergyHUDColor);
        } else {
            drawCenteredString(Minecraft.getMinecraft().fontRendererObj, String.valueOf(energy), this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), MWEConfig.lowEnergyHUDColor);
        }
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.guiPosition.isEnabled() && ScoreboardTracker.isInMwGame();
    }

}
