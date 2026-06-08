package fr.alexdoru.mwe.gui;

import fr.alexdoru.mwe.api.GuiPosition;
import fr.alexdoru.mwe.api.IRenderer;
import fr.alexdoru.mwe.gui.huds.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Objects;

public final class HUDRenderer {

    private static final ArrayList<IRenderer> RENDERERS = new ArrayList<>();
    public static final ArrowHitHUD arrowHitHUD = new ArrowHitHUD();
    public static final BaseLocationHUD baseLocationHUD = new BaseLocationHUD();
    public static final CreeperPrimedTntHUD creeperPrimedTntHUD = new CreeperPrimedTntHUD();
    public static final FKCounterHUD fkCounterHUD = new FKCounterHUD();
    public static final StrengthHUD strengthHUD = new StrengthHUD();
    public static final KillCooldownHUD killCooldownHUD = new KillCooldownHUD();
    public static final LastWitherHPHUD lastWitherHPHUD = new LastWitherHPHUD();
    public static final PhoenixBondHUD phoenixBondHUD = new PhoenixBondHUD();
    public static final WarcryHUD warcryHUD = new WarcryHUD();

    static {
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

    public static void registerRenderer(IRenderer renderer) {
        Objects.requireNonNull(renderer);
        RENDERERS.add(renderer);
    }

    /**
     * If you have HUD Caching this method will only run 20 times per second
     */
    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.type == ElementType.TEXT && !(mc.currentScreen instanceof PositionEditGuiScreen)) {
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
    public static void onPostRenderGameOverlay(float partialTicks) {}

    public static IRenderer getRendererFromPosition(GuiPosition guiPosition) {
        if (guiPosition == null) return null;
        for (final IRenderer renderer : RENDERERS) {
            if (guiPosition == renderer.getGuiPosition()) {
                return renderer;
            }
        }
        return null;
    }

    public static void renderAllDummy() {
        final ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        for (final IRenderer renderer : RENDERERS) {
            if (renderer.getGuiPosition().isEnabled()) {
                renderer.getGuiPosition().updateAbsolutePosition(resolution);
                renderer.renderDummy();
            }
        }
    }

}
