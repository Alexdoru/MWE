package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.updater.ModUpdater;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class UpdateNotifier {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean hasTriggered = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld != null && mc.thePlayer != null && !hasTriggered) {
            hasTriggered = true;
            Multithreading.addTaskToQueue(() -> {
                try {
                    ModUpdater.checkForUpdate();
                } catch (ApiException e) {
                    e.printStackTrace();
                }
                return null;
            });
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

}
