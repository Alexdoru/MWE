package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class EnergyDisplayHUD extends MyCachedHUD {

    public static EnergyDisplayHUD instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "50";
    private long timeStartRender;
    private long renderDuration = 5000L;
    private int energy;
    private int storedEnergy;
    private EnumChatFormatting colorPrefix = EnumChatFormatting.GREEN;

    public EnergyDisplayHUD() {
        super(ConfigHandler.energyDisplayHUDPosition);
        instance = this;
    }

    @Override
    public void render(ScaledResolution resolution) {
        final EntityPlayer player = mc.thePlayer;
        energy = player.experienceLevel;
        if (energy != storedEnergy) {
            timeStartRender = System.currentTimeMillis();
        }
        final int timeLeft = (int) ((timeStartRender + renderDuration - System.currentTimeMillis()) / 1000L);
        final int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);
        displayText = (timeLeft > 0 ? colorPrefix + "" + energy : "");
        drawCenteredString(frObj, displayText, absolutePos[0], absolutePos[1], 0);
        storedEnergy = energy;
    }

    @Override
    public void renderDummy() {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition();
        drawCenteredString(frObj, DUMMY_TEXT, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        //return timeStartRender + renderDuration - currentTimeMillis > 0;
        return true;
    }


}
