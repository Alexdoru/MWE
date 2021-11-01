package fr.alexdoru.fkcountermod.gui.hudapi;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class PropertyGuiScreen extends GuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft();
    private int prevX, prevY;
    private final IRenderer renderer;
    private final HUDPosition hudPosition;
    private final GuiScreen parent;

    public PropertyGuiScreen(IRenderer renderer, GuiScreen parent) {

        HUDPosition pos = renderer.getHUDPosition();

        if (pos == null) {
            pos = new HUDPosition(0.5d, 0.5d);
        }

        this.renderer = renderer;
        this.hudPosition = pos;
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
        int[] absolutePos = hudPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];

        hudPosition.setAbsolute(x + offsetX, y + offsetY);
        adjustBounds();
    }

    @Override
    public void onGuiClosed() {
        renderer.save();
        new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(parent), 0);
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

        int absoluteX = Math.max(0, Math.min((int) (hudPosition.getRelativeX() * res.getScaledWidth()), Math.max(screenWidth - this.renderer.getWidth(), 0)));
        int absoluteY = Math.max(0, Math.min((int) (hudPosition.getRelativeY() * res.getScaledHeight()), Math.max(screenHeight - this.renderer.getHeight(), 0)));

        this.hudPosition.setAbsolute(absoluteX, absoluteY);
    }

}
