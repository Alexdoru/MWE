package fr.alexdoru.mwe.gui;

import fr.alexdoru.configlib.IRenderer;
import fr.alexdoru.configlib.IRendererManager;
import fr.alexdoru.configlib.RendererPosition;
import fr.alexdoru.configlib.gui.RendererEditGuiScreen;
import fr.alexdoru.mwe.gui.huds.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Objects;

public final class MWERendererManager implements IRendererManager {

    private static final ArrayList<IRenderer> RENDERERS = new ArrayList<>();
    public static ArrowHitHUD arrowHitHUD;
    public static BaseLocationHUD baseLocationHUD;
    public static CreeperPrimedTntHUD creeperPrimedTntHUD;
    public static FKCounterHUD fkCounterHUD;
    public static StrengthHUD strengthHUD;
    public static KillCooldownHUD killCooldownHUD;
    public static LastWitherHPHUD lastWitherHPHUD;
    public static PhoenixBondHUD phoenixBondHUD;
    public static WarcryHUD warcryHUD;

    public void loadRenderers() {
        arrowHitHUD = new ArrowHitHUD();
        baseLocationHUD = new BaseLocationHUD();
        creeperPrimedTntHUD = new CreeperPrimedTntHUD();
        fkCounterHUD = new FKCounterHUD();
        strengthHUD = new StrengthHUD();
        killCooldownHUD = new KillCooldownHUD();
        lastWitherHPHUD = new LastWitherHPHUD();
        phoenixBondHUD = new PhoenixBondHUD();
        warcryHUD = new WarcryHUD();
        RENDERERS.add(new ArmorHUD());
        RENDERERS.add(arrowHitHUD);
        RENDERERS.add(baseLocationHUD);
        RENDERERS.add(creeperPrimedTntHUD);
        RENDERERS.add(new EnergyDisplayHUD());
        RENDERERS.add(fkCounterHUD);
        RENDERERS.add(strengthHUD);
        RENDERERS.add(killCooldownHUD);
        RENDERERS.add(lastWitherHPHUD);
        RENDERERS.add(new MiniPotionHUD());
        RENDERERS.add(new PendingReportHUD());
        RENDERERS.add(phoenixBondHUD);
        RENDERERS.add(new PotionHUD());
        RENDERERS.add(new SpeedHUD());
        RENDERERS.add(new SquadHealthHUD());
        RENDERERS.add(warcryHUD);
    }

    public void registerRenderer(IRenderer renderer) {
        Objects.requireNonNull(renderer);
        RENDERERS.add(renderer);
    }

    /**
     * If you have HUD Caching this method will only run 20 times per second
     */
    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.type == ElementType.TEXT && !(mc.currentScreen instanceof RendererEditGuiScreen)) {
            final long time = System.currentTimeMillis();
            mc.mcProfiler.startSection("MWE HUD");
            for (final IRenderer renderer : RENDERERS) {
                if (renderer.isEnabled(time)) {
                    renderer.render(event.resolution);
                }
            }
            mc.mcProfiler.endSection();
        }
    }

    /**
     * This will run once per frame even with HUD Caching
     * Hook injected in {@link net.minecraft.client.renderer.EntityRenderer#updateCameraAndRender}
     * after this.mc.ingameGUI.renderGameOverlay(partialTicks) call
     */
    public void onPostRenderGameOverlay(float partialTicks) {}

    @Override
    public IRenderer getRendererFromPosition(RendererPosition rendererPosition) {
        if (rendererPosition == null) return null;
        for (final IRenderer renderer : RENDERERS) {
            if (rendererPosition == renderer.getPosition()) {
                return renderer;
            }
        }
        return null;
    }

    @Override
    public void renderEditScreenBackground(IRenderer editedRenderer) {
        final ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        for (final IRenderer renderer : RENDERERS) {
            if (renderer.getPosition().isEnabled()) {
                renderer.getPosition().updateAbsolutePosition(resolution);
                renderer.renderDummy();
            }
        }
    }

}
