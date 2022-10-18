package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.megawallsenhancementsmod.gui.*;
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
        this.registeredRenderers.add(new LastWitherHPHUD());
        //this.registeredRenderers.add(new SquadHealthHUD()); // TODO squad health hud
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (event.type == ElementType.TEXT && !(mc.currentScreen instanceof PositionEditGuiScreen)) {
            registeredRenderers.forEach(registeredRenderer -> callRenderer(registeredRenderer, event.resolution));
        }
    }

    private void callRenderer(IRenderer renderer, ScaledResolution resolution) {
        if (renderer.isEnabled()) {
            renderer.render(resolution);
        }
    }

}
