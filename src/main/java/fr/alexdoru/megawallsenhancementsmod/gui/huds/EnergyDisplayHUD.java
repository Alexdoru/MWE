package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import jline.internal.Nullable;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;

public class EnergyDisplayHUD extends MyCachedHUD {

    public static EnergyDisplayHUD instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "50";
    private long timeStartRender;
    private long renderDuration = 5000L;
    private int energy;
    private int storedEnergy;
    private EnumChatFormatting colorPrefix = EnumChatFormatting.GREEN;
    private String boldPrefix = "";
    private Item[] itemsToRenderHUD = new Item[]{Items.diamond_sword, Items.iron_sword, Items.stone_sword, Items.golden_sword, Items.wooden_sword, Items.bow, Items.diamond_shovel, Items.iron_shovel, Items.stone_shovel, Items.golden_shovel, Items.wooden_shovel};

    public EnergyDisplayHUD() {
        super(ConfigHandler.energyDisplayHUDPosition);
        instance = this;
    }

    @Override
    public void render(ScaledResolution resolution) {
        if (FKCounterMod.isInMwGame) {
            final EntityPlayer player = mc.thePlayer;
            if (player.getHeldItem() != null && player.getHeldItem().getItem() != null) {
                if (Arrays.asList(itemsToRenderHUD).contains(player.getHeldItem().getItem())) {
                    energy = player.experienceLevel;
                    if (energy >= ConfigHandler.aquaEnergyDisplayThreshold) {
                        colorPrefix = EnumChatFormatting.AQUA;
                        boldPrefix = "\u00a7l";
                    } else {
                        colorPrefix = EnumChatFormatting.GREEN;
                        boldPrefix = "";
                    }
                    if (energy != storedEnergy) {
                        timeStartRender = System.currentTimeMillis();
                    }
                    final int timeLeft = (int) ((timeStartRender + renderDuration - System.currentTimeMillis()) / 1000L);
                    final int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);
                    displayText = (timeLeft > 0 && energy != 0 ? colorPrefix + boldPrefix + "" + energy : "");
                    drawCenteredString(frObj, displayText, absolutePos[0], absolutePos[1], 0);
                    storedEnergy = energy;
            }

            }

        }

    }

    @Override
    public void renderDummy() {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition();
        drawCenteredString(frObj, DUMMY_TEXT, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showEnergyDisplayHUD;
    }


}
