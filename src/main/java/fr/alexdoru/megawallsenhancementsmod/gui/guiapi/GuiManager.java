package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.megawallsenhancementsmod.gui.huds.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public final class GuiManager {

    private final List<IRenderer> registeredRenderers = new ArrayList<>();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final PlayerRenderHUD playerRenderHUD = new PlayerRenderHUD();

    /**
     * Register your Guis here
     */
    public GuiManager() {
        this.registeredRenderers.add(new FKCounterHUD());
        this.registeredRenderers.add(new ArrowHitHUD());
        this.registeredRenderers.add(new KillCooldownHUD());
        this.registeredRenderers.add(new HunterStrengthHUD());
        this.registeredRenderers.add(new CreeperPrimedTntHUD());
        this.registeredRenderers.add(new LastWitherHPHUD());
        this.registeredRenderers.add(new EnergyDisplayHUD());
        this.registeredRenderers.add(new SquadHealthHUD());
        this.registeredRenderers.add(new SpeedHUD());
        this.registeredRenderers.add(new PhoenixBondHUD());
    }

    /**
     * If you have HUD Caching this method will only run 20 times per second
     */
    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (event.type == ElementType.TEXT && !(mc.currentScreen instanceof PositionEditGuiScreen)) {
            final long time = System.currentTimeMillis();
            mc.mcProfiler.startSection("MWE HUD");
            registeredRenderers.forEach(renderer -> callRenderer(renderer, event.resolution, time));
            mc.mcProfiler.endSection();
        }
    }

    /**
     * This will run once per frame even with HUD Caching
     * Hook injected in {@link net.minecraft.client.renderer.EntityRenderer#updateCameraAndRender}
     * after this.mc.ingameGUI.renderGameOverlay(partialTicks) call
     */
    public static void onPostRenderGameOverlay(float partialTicks) {
        mc.mcProfiler.startSection("MWE HUD");
        if (playerRenderHUD.isEnabled(0L)) {
            playerRenderHUD.setPartialTickTime(partialTicks);
            playerRenderHUD.render(new ScaledResolution(mc));
        }
        mc.mcProfiler.endSection();
    }

    private static void callRenderer(IRenderer renderer, ScaledResolution resolution, long currentTimeMillis) {
        if (renderer.isEnabled(currentTimeMillis)) {
            renderer.render(resolution);
        }
    }

}
