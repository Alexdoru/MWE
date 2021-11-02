package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.fkcountermod.gui.FKCounterGui;
import fr.alexdoru.megawallsenhancementsmod.gui.ArrowHitGui;
import fr.alexdoru.megawallsenhancementsmod.gui.KillCooldownGui;
import fr.alexdoru.megawallsenhancementsmod.gui.LastWitherHPGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
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
        LastWitherHPGui lastWitherHPGui = new LastWitherHPGui();
        this.registeredRenderers.add(lastWitherHPGui);
        MinecraftForge.EVENT_BUS.register(lastWitherHPGui);
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        // TODO ca s'affiche en dessous du scoreboard

        if (event.type == ElementType.EXPERIENCE && !(mc.currentScreen instanceof PositionEditGuiScreen)) {
            registeredRenderers.forEach(this::callRenderer);
        }

    }

    private void callRenderer(IRenderer renderer) {
        if (renderer.isEnabled()) {
            renderer.render();
        }
    }

}
