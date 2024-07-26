package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashSet;

public class EnergyDisplayHUD extends AbstractRenderer {

    private static final HashSet<Item> itemsForRender = new HashSet<>();
    private long timeStartRender;
    private int prevEnergyValue;

    static {
        itemsForRender.add(Items.diamond_sword);
        itemsForRender.add(Items.iron_sword);
        itemsForRender.add(Items.bow);
        itemsForRender.add(Items.diamond_shovel);
    }

    public EnergyDisplayHUD() {
        super(MWEConfig.energyDisplayHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
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
        if (itemsForRender.contains(item)) {
            if (energy != prevEnergyValue) {
                this.timeStartRender = currentTime;
            }
            prevEnergyValue = energy;
        }
        if (energy == 0) {
            return;
        }
        if (this.timeStartRender + 2500L - currentTime > 0L) {
            final String displayText;
            if (energy >= MWEConfig.aquaEnergyDisplayThreshold) {
                displayText = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + energy;
            } else {
                displayText = EnumChatFormatting.GREEN.toString() + energy;
            }
            this.guiPosition.updateAbsolutePosition(resolution);
            drawCenteredString(mc.fontRendererObj, displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
        }
    }

    @Override
    public void renderDummy() {
        drawCenteredString(mc.fontRendererObj, EnumChatFormatting.GREEN + "50", this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return MWEConfig.showEnergyDisplayHUD && ScoreboardTracker.isInMwGame();
    }

}
