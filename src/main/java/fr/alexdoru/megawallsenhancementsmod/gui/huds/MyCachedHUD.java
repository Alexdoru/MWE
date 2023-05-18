package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public abstract class MyCachedHUD extends Gui implements IRenderer {

    protected static final Minecraft mc = Minecraft.getMinecraft();
    public String displayText = "";
    protected final GuiPosition guiPosition;

    public MyCachedHUD(GuiPosition guiPosition) {
        this.guiPosition = guiPosition;
    }

    @Override
    public int getHeight() {
        return mc.fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public int getWidth() {
        return mc.fontRendererObj.getStringWidth(displayText);
    }

    @Override
    public void render(ScaledResolution resolution) {}

    @Override
    public void renderDummy() {}

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return true;
    }

    @Override
    public GuiPosition getGuiPosition() {
        return this.guiPosition;
    }

}
