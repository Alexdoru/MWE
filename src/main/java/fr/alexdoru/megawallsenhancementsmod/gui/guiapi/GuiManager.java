package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.fkcountermod.gui.FKCounterGui;
import fr.alexdoru.megawallsenhancementsmod.gui.ArrowHitGui;
import fr.alexdoru.megawallsenhancementsmod.gui.HunterStrengthGui;
import fr.alexdoru.megawallsenhancementsmod.gui.KillCooldownGui;
import fr.alexdoru.megawallsenhancementsmod.gui.LastWitherHPGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public final class GuiManager {

    private final List<IRenderer> registeredRenderers = new ArrayList<>();
    private final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Register your Guis here
     */
    public GuiManager() {
        this.registeredRenderers.add(new FKCounterGui());
        this.registeredRenderers.add(new ArrowHitGui());
        this.registeredRenderers.add(new KillCooldownGui());
        this.registeredRenderers.add(new HunterStrengthGui());
        this.registeredRenderers.add(new LastWitherHPGui());
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
