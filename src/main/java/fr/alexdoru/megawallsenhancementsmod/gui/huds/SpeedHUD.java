package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class SpeedHUD implements IRenderer {

    public static SpeedHUD instance;
    private final GuiPosition guiPosition;
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final String DUMMY_TEXT = EnumChatFormatting.DARK_GREEN + "Speed: " + EnumChatFormatting.WHITE + "5.65m/s";

    public SpeedHUD() {
        instance = this;
        guiPosition = ConfigHandler.speedHUDPosition;
    }

    @Override
    public int getHeight() {
        return mc.fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public int getWidth() {
        return mc.fontRendererObj.getStringWidth(DUMMY_TEXT);
    }

    @Override
    public void render(ScaledResolution resolution) {
        final double velocity = new Vector2D(mc.thePlayer.motionX, mc.thePlayer.motionZ).norm() * 20d;
        final String displayText = EnumChatFormatting.DARK_GREEN + "Speed: " + EnumChatFormatting.WHITE + String.format("%.2f", velocity) + "m/s";
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, mc.fontRendererObj.getStringWidth(displayText), mc.fontRendererObj.FONT_HEIGHT);
        mc.fontRendererObj.drawStringWithShadow(displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public void renderDummy() {
        mc.fontRendererObj.drawStringWithShadow(DUMMY_TEXT, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showSpeedHUD;
    }

    @Override
    public GuiPosition getGuiPosition() {
        return this.guiPosition;
    }

}
