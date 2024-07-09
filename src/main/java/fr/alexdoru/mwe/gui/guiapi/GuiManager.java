package fr.alexdoru.mwe.gui.guiapi;

import fr.alexdoru.mwe.gui.huds.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public final class GuiManager {

    private final ArrayList<IRenderer> registeredRenderers = new ArrayList<>();
    private static final Minecraft mc = Minecraft.getMinecraft();
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
    public static final SpeedHUD speedHUD = new SpeedHUD();
    public static final SquadHealthHUD squadHealthHUD = new SquadHealthHUD();
    public static final WarcryHUD warcryHUD = new WarcryHUD();

    /**
     * Register your Guis here
     */
    public GuiManager() {
        this.registeredRenderers.add(arrowHitHUD);
        this.registeredRenderers.add(baseLocationHUD);
        this.registeredRenderers.add(creeperPrimedTntHUD);
        this.registeredRenderers.add(energyDisplayHUD);
        this.registeredRenderers.add(fkCounterHUD);
        this.registeredRenderers.add(strengthHUD);
        this.registeredRenderers.add(killCooldownHUD);
        this.registeredRenderers.add(lastWitherHPHUD);
        this.registeredRenderers.add(miniPotionHUD);
        this.registeredRenderers.add(pendingReportHUD);
        this.registeredRenderers.add(phoenixBondHUD);
        this.registeredRenderers.add(speedHUD);
        this.registeredRenderers.add(squadHealthHUD);
        this.registeredRenderers.add(warcryHUD);
        this.registeredRenderers.trimToSize();
    }

    /**
     * If you have HUD Caching this method will only run 20 times per second
     */
    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (event.type == ElementType.TEXT && !(mc.currentScreen instanceof PositionEditGuiScreen)) {
            final long time = System.currentTimeMillis();
            mc.mcProfiler.startSection("MWE HUD");
            for (final IRenderer renderer : this.registeredRenderers) {
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

}
