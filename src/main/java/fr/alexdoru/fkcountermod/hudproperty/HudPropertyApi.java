package fr.alexdoru.fkcountermod.hudproperty;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public final class HudPropertyApi {

    public static HudPropertyApi newInstance() {
        HudPropertyApi api = new HudPropertyApi();
        MinecraftForge.EVENT_BUS.register(api);
        return api;
    }

    private final Set<IRenderer> registeredRenderers = Sets.newHashSet();
    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean renderOutlines = true;

    private HudPropertyApi() {
    }

    public void register(IRenderer... renderers) {
        this.registeredRenderers.addAll(Arrays.asList(renderers));
    }

    public void unregister(IRenderer... renderers) {
        for (IRenderer renderer : renderers) {
            this.registeredRenderers.remove(renderer);
        }
    }

    public Collection<IRenderer> getRegisteredRenderers() {
        return Sets.newHashSet(registeredRenderers);
    }

    public boolean getRenderOutlines() {
        return renderOutlines;
    }

    public void setRenderOutlines(boolean renderOutlines) {
        this.renderOutlines = renderOutlines;
    }

    public void openConfigScreen() {
        mc.displayGuiScreen(new PropertyScreen(this));
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        // TODO ca s'affiche en dessous du scoreboard

        if (event.type != ElementType.EXPERIENCE) {
            return;
        }

        if (!(mc.currentScreen instanceof PropertyScreen)) {
            registeredRenderers.forEach(this::callRenderer);
        }

    }

    private void callRenderer(IRenderer renderer) {
        if (!renderer.isEnabled()) {
            return;
        }

        ScreenPosition position = renderer.load();

        if (position == null) {
            position = ScreenPosition.fromRelativePosition(0.5d, 0.5d);
        }

        renderer.render(position);
    }

}
