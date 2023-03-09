package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.DelayedTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class PositionEditGuiScreen extends GuiScreen {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private final IRenderer renderer;
    private final GuiPosition guiPosition;
    private final GuiScreen parent;
    private boolean dragging;
    private int prevX, prevY;

    public PositionEditGuiScreen(IRenderer renderer, GuiScreen parent) {
        this.renderer = renderer;
        this.guiPosition = renderer.getGuiPosition();
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.guiPosition.updateAbsolutePosition();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        renderer.renderDummy();
        if (this.dragging) {
            this.guiPosition.setAbsolutePositionForRender(
                    this.guiPosition.getAbsoluteRenderX() + mouseX - this.prevX,
                    this.guiPosition.getAbsoluteRenderY() + mouseY - this.prevY
            );
        }
        this.prevX = mouseX;
        this.prevY = mouseY;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.dragging = true;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.dragging = false;
    }

    @Override
    public void onGuiClosed() {
        this.guiPosition.saveAbsoluteToRelative();
        ConfigHandler.saveConfig();
        new DelayedTask(() -> mc.displayGuiScreen(parent), 0);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
