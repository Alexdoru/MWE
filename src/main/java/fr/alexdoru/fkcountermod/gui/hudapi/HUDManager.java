package fr.alexdoru.fkcountermod.gui.hudapi;

import fr.alexdoru.fkcountermod.gui.FKCounterGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public final class HUDManager {

    private final List<IRenderer> registeredRenderers = new ArrayList<>();
    private final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Register your HUDs here
     */
    public HUDManager() {
        this.registeredRenderers.add(new FKCounterGui());
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        // TODO ca s'affiche en dessous du scoreboard

        if (event.type == ElementType.EXPERIENCE && !(mc.currentScreen instanceof PropertyGuiScreen)) {
            registeredRenderers.forEach(this::callRenderer);
        }

    }

    private void callRenderer(IRenderer renderer) {
        if (renderer.isEnabled()) {
            renderer.render();
        }
    }

}
