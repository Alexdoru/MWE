package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class PositionEditGuiScreen extends GuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft();
    private int prevX, prevY;
    private final IRenderer renderer;
    private final GuiPosition guiPosition;
    private final GuiScreen parent;

    public PositionEditGuiScreen(IRenderer renderer, GuiScreen parent) {

        GuiPosition pos = renderer.getHUDPosition();

        if (pos == null) {
            pos = new GuiPosition(0.5d, 0.5d);
        }

        this.renderer = renderer;
        this.guiPosition = pos;
        this.parent = parent;
        adjustBounds();
    }

    @Override
    public void drawScreen(int x, int y, float partialTicks) {
        super.drawDefaultBackground();
        renderer.renderDummy();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        this.prevX = x;
        this.prevY = y;
    }

    @Override
    protected void mouseClickMove(int x, int y, int button, long time) {
        moveSelectedRendererBy(x - prevX, y - prevY);
        this.prevX = x;
        this.prevY = y;
    }

    private void moveSelectedRendererBy(int offsetX, int offsetY) {
        int[] absolutePos = guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];

        guiPosition.setAbsolute(x + offsetX, y + offsetY);
        adjustBounds();
    }

    @Override
    public void onGuiClosed() {
        renderer.save();
        new DelayedTask(() -> mc.displayGuiScreen(parent), 0);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Makes sure the HUD can't get out of the screen
     */
    private void adjustBounds() {
        ScaledResolution res = new ScaledResolution(mc);

        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        int absoluteX = Math.max(0, Math.min((int) (guiPosition.getRelativeX() * res.getScaledWidth()), Math.max(screenWidth - this.renderer.getWidth(), 0)));
        int absoluteY = Math.max(0, Math.min((int) (guiPosition.getRelativeY() * res.getScaledHeight()), Math.max(screenHeight - this.renderer.getHeight(), 0)));

        this.guiPosition.setAbsolute(absoluteX, absoluteY);
    }

}
