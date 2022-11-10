package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashSet;

public class EnergyDisplayHUD extends MyCachedHUD {

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
            if (energy >= ConfigHandler.aquaEnergyDisplayThreshold) {
                displayText = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + energy;
            } else {
                displayText = EnumChatFormatting.GREEN.toString() + energy;
            }
            final int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);
            drawCenteredString(frObj, displayText, absolutePos[0], absolutePos[1], 0);
        }
    }

    @Override
    public void renderDummy() {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition();
        drawCenteredString(frObj, DUMMY_TEXT, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showEnergyDisplayHUD && FKCounterMod.isInMwGame && mc.thePlayer != null;
    }

}
