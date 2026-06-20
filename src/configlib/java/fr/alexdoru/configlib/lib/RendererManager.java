package fr.alexdoru.configlib.lib;

import fr.alexdoru.configlib.api.IRenderer;
import fr.alexdoru.configlib.api.IRendererManager;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.gui.RendererEditGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RendererManager implements IRendererManager {

    private final String configName;
    private final List<IRenderer> ALL_RENDERERS = new ArrayList<>();
    private final List<IRenderer> HUD_RENDERERS = new ArrayList<>();

    public RendererManager(String configName) {
        this.configName = configName;
    }

    @Override
    public void registerHUDRenderer(@NotNull IRenderer renderer) {
        Objects.requireNonNull(renderer);
        if (HUD_RENDERERS.isEmpty()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
        ALL_RENDERERS.add(renderer);
        HUD_RENDERERS.add(renderer);
    }

    @Override
    public void registerRenderer(@NotNull IRenderer renderer) {
        Objects.requireNonNull(renderer);
        ALL_RENDERERS.add(renderer);
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT && !(mc.currentScreen instanceof RendererEditGuiScreen)) {
            final long time = System.currentTimeMillis();
            mc.mcProfiler.startSection(this.configName + " HUD");
            for (final IRenderer renderer : HUD_RENDERERS) {
                if (renderer.isEnabled(time)) {
                    renderer.render(event.resolution);
                }
            }
            mc.mcProfiler.endSection();
        }
    }

    public IRenderer getRendererFromPosition(RendererPosition rendererPosition) {
        Objects.requireNonNull(rendererPosition);
        for (final IRenderer renderer : ALL_RENDERERS) {
            if (rendererPosition == renderer.getPosition()) {
                return renderer;
            }
        }
        return null;
    }

    public void renderEditScreenBackground(IRenderer editedRenderer) {
        final ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        for (final IRenderer renderer : HUD_RENDERERS) {
            if (renderer.getPosition().isEnabled()) {
                renderer.getPosition().updateAbsolutePosition(resolution);
                renderer.renderDummy();
            }
        }
    }

}
