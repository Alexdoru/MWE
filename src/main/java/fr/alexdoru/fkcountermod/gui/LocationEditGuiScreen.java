package fr.alexdoru.fkcountermod.gui;

import fr.alexdoru.fkcountermod.hudproperty.HudPropertyApi;
import fr.alexdoru.fkcountermod.hudproperty.IRenderer;
import fr.alexdoru.fkcountermod.hudproperty.ScreenPosition;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Predicate;

public class LocationEditGuiScreen extends GuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft();

    private final HashMap<IRenderer, ScreenPosition> renderers = new HashMap<>();
    private Optional<IRenderer> selectedRenderer = Optional.empty();

    private int prevX, prevY;

    private GuiScreen parent;

    public LocationEditGuiScreen(HudPropertyApi api, GuiScreen parent) {
        this(api);
        this.parent = parent;
    }

    public LocationEditGuiScreen(HudPropertyApi api) {
        Collection<IRenderer> registeredRenderers = api.getHandlers();

        for (IRenderer ren : registeredRenderers) {

            ScreenPosition pos = ren.load();

            if (pos == null) {
                pos = ScreenPosition.fromRelativePosition(0.5, 0.5);
            }

            adjustBounds(ren, pos);

            this.renderers.put(ren, pos);
        }

    }

    @Override
    public void drawScreen(int x, int y, float partialTicks) {
        super.drawDefaultBackground();

        float zBackup = this.zLevel;
        this.zLevel = 200;

        renderers.forEach(IRenderer::renderDummy);

        this.zLevel = zBackup;
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        prevX = x;
        prevY = y;
        loadMouseOver(x, y);
    }

    @Override
    protected void mouseClickMove(int x, int y, int button, long time) {
        if (selectedRenderer.isPresent()) {
            moveSelectedRendererBy(x - prevX, y - prevY);
        }

        this.prevX = x;
        this.prevY = y;
    }

    private void moveSelectedRendererBy(int offsetX, int offsetY) {
        IRenderer renderer = selectedRenderer.get();
        ScreenPosition position = renderers.get(renderer);

        position.setAbsolute(position.getAbsoluteX() + offsetX, position.getAbsoluteY() + offsetY);

        //adjustBounds(renderer, position);
    }

    @Override
    public void onGuiClosed() {
        renderers.forEach(IRenderer::save);
        new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(parent), 0);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    private void adjustBounds(IRenderer renderer, ScreenPosition pos) {
        ScaledResolution res = new ScaledResolution(mc);

        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        int absoluteX = Math.max(0, Math.min(pos.getAbsoluteX(), Math.max(screenWidth - renderer.getWidth(), 0)));
        int absoluteY = Math.max(0, Math.min(pos.getAbsoluteY(), Math.max(screenHeight - renderer.getHeight(), 0)));

        pos.setAbsolute(absoluteX, absoluteY);
    }

    private void loadMouseOver(int x, int y) {
        this.selectedRenderer = renderers.keySet().stream()
                .filter(new MouseOverFinder(x, y))
                .findFirst();
    }

    private class MouseOverFinder implements Predicate<IRenderer> {

        private final int mouseX;
        private final int mouseY;

        public MouseOverFinder(int mouseX, int mouseY) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        @Override
        public boolean test(IRenderer renderer) {
            ScreenPosition pos = renderers.get(renderer);

            int absoluteX = pos.getAbsoluteX();
            int absoluteY = pos.getAbsoluteY();

            if (mouseX >= absoluteX && mouseX <= absoluteX + renderer.getWidth()) {
                return mouseY >= absoluteY && mouseY <= absoluteY + renderer.getHeight();
            }

            return false;
        }

    }
}
