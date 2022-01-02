package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.fkcountermod.gui.FKCounterGui;
import fr.alexdoru.megawallsenhancementsmod.gui.ArrowHitGui;
import fr.alexdoru.megawallsenhancementsmod.gui.HunterStrengthGui;
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
        this.registeredRenderers.add(new HunterStrengthGui());
        LastWitherHPGui lastWitherHPGui = new LastWitherHPGui();
        this.registeredRenderers.add(lastWitherHPGui);
        MinecraftForge.EVENT_BUS.register(lastWitherHPGui);
        /*
        * FIXME java.lang.NoSuchMethodException: fr.alexdoru.fkcountermod.events.MwGameEvent.<init>()
[04:52:32] [Client thread/INFO] [STDERR]: [net.minecraftforge.fml.common.eventhandler.EventBus:register:117]: 	at java.lang.Class.getConstructor0(Class.java:3082)
[04:52:32] [Client thread/INFO] [STDERR]: [net.minecraftforge.fml.common.eventhandler.EventBus:register:117]: 	at java.lang.Class.getConstructor(Class.java:1825)
[04:52:32] [Client thread/INFO] [STDERR]: [net.minecraftforge.fml.common.eventhandler.EventBus:register:117]: 	at net.minecraftforge.fml.common.eventhandler.EventBus.register(EventBus.java:101)
[04:52:32] [Client thread/INFO] [STDERR]: [net.minecraftforge.fml.common.eventhandler.EventBus:register:117]: 	at net.minecraftforge.fml.common.eventhandler.EventBus.register(EventBus.java:85)
[04:52:32] [Client thread/INFO] [STDERR]: [net.minecraftforge.fml.common.eventhandler.EventBus:register:117]: 	at fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiManager.<init>(GuiManager.java:32)
[04:52:32] [Client thread/INFO] [STDERR]: [net.minecraftforge.fml.common.eventhandler.EventBus:register:117]: 	at fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod.init(MegaWallsEnhancementsMod.java:41)
* */
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
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
