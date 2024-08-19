package fr.alexdoru.mwe.gui.guiapi;

import fr.alexdoru.mwe.gui.huds.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public final class GuiManager {

    private static final ArrayList<IRenderer> registeredRenderers = new ArrayList<>();
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final ArmorHUD armorHUD = new ArmorHUD();
    public static final ArrowHitHUD arrowHitHUD = new ArrowHitHUD();
    public static final BaseLocationHUD baseLocationHUD = new BaseLocationHUD();
    public static final CreeperPrimedTntHUD creeperPrimedTntHUD = new CreeperPrimedTntHUD();
    public static final EnergyDisplayHUD energyDisplayHUD = new EnergyDisplayHUD();
    public static final FKCounterHUD fkCounterHUD = new FKCounterHUD();
    public static final StrengthHUD strengthHUD = new StrengthHUD();
    public static final KillCooldownHUD killCooldownHUD = new KillCooldownHUD();
    public static final LastWitherHPHUD lastWitherHPHUD = new LastWitherHPHUD();
    public static final MiniPotionHUD miniPotionHUD = new MiniPotionHUD();
    public static final PendingReportHUD pendingReportHUD = new PendingReportHUD();
    public static final PhoenixBondHUD phoenixBondHUD = new PhoenixBondHUD();
    public static final PotionHUD potionHUD = new PotionHUD();
    public static final SpeedHUD speedHUD = new SpeedHUD();
    public static final SquadHealthHUD squadHealthHUD = new SquadHealthHUD();
    public static final WarcryHUD warcryHUD = new WarcryHUD();

    static {
        registeredRenderers.add(armorHUD);
        registeredRenderers.add(arrowHitHUD);
        registeredRenderers.add(baseLocationHUD);
        registeredRenderers.add(creeperPrimedTntHUD);
        registeredRenderers.add(energyDisplayHUD);
        registeredRenderers.add(fkCounterHUD);
        registeredRenderers.add(strengthHUD);
        registeredRenderers.add(killCooldownHUD);
        registeredRenderers.add(lastWitherHPHUD);
        registeredRenderers.add(miniPotionHUD);
        registeredRenderers.add(pendingReportHUD);
        registeredRenderers.add(phoenixBondHUD);
        registeredRenderers.add(potionHUD);
        registeredRenderers.add(speedHUD);
        registeredRenderers.add(squadHealthHUD);
        registeredRenderers.add(warcryHUD);
        registeredRenderers.trimToSize();
    }

    /**
     * If you have HUD Caching this method will only run 20 times per second
     */
    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (event.type == ElementType.TEXT && !(mc.currentScreen instanceof PositionEditGuiScreen)) {
            final long time = System.currentTimeMillis();
            mc.mcProfiler.startSection("MWE HUD");
            for (final IRenderer renderer : registeredRenderers) {
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
        for (final IRenderer renderer : registeredRenderers) {
            if (guiPosition == renderer.getGuiPosition())
                return renderer;
        }
        return null;
    }

    public static void renderAllDummy() {
        final ScaledResolution resolution = new ScaledResolution(mc);
        for (final IRenderer renderer : registeredRenderers) {
            if (renderer.getGuiPosition().isEnabled()) {
                renderer.getGuiPosition().updateAbsolutePosition(resolution);
                renderer.renderDummy();
            }
        }
    }

}
