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

    /**
     * Register your Guis here
     */
    public GuiManager() {
        this.registeredRenderers.add(new FKCounterHUD());
        this.registeredRenderers.add(new ArrowHitHUD());
        this.registeredRenderers.add(new KillCooldownHUD());
        this.registeredRenderers.add(new HunterStrengthHUD());
        this.registeredRenderers.add(new CreeperPrimedTNTHUD());
        this.registeredRenderers.add(new LastWitherHPHUD());
        this.registeredRenderers.add(new EnergyDisplayHUD());
        this.registeredRenderers.add(new SquadHealthHUD());
        this.registeredRenderers.add(new SpeeedHUD());
        this.registeredRenderers.add(new PhxBondHud());
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (event.type == ElementType.TEXT && !(mc.currentScreen instanceof PositionEditGuiScreen)) {
            final long time = System.currentTimeMillis();
            registeredRenderers.forEach(registeredRenderer -> callRenderer(registeredRenderer, event.resolution, time));
        }
    }

    private void callRenderer(IRenderer renderer, ScaledResolution resolution, long currentTimeMillis) {
        if (renderer.isEnabled(currentTimeMillis)) {
            renderer.render(resolution);
        }
    }

}
