package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashSet;

public class EnergyDisplayHUD extends AbstractRenderer {

    public static EnergyDisplayHUD instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "50";
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
        super(ConfigHandler.energyDisplayHUDPosition);
        instance = this;
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
            if (energy >= ConfigHandler.aquaEnergyDisplayThreshold) {
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
        drawCenteredString(mc.fontRendererObj, DUMMY_TEXT, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showEnergyDisplayHUD && ScoreboardTracker.isInMwGame && mc.thePlayer != null;
    }

}
