package fr.alexdoru.fkcountermod.hudmanager;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.Set;

public final class HUDManager {

    private final Set<IRenderer> registeredRenderers = Sets.newHashSet();
    private final Minecraft mc = Minecraft.getMinecraft();

    public void register(IRenderer... renderers) {
        this.registeredRenderers.addAll(Arrays.asList(renderers));
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
