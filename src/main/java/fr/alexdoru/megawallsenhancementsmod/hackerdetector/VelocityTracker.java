package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class VelocityTracker {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private double x, y, z, vx, vy, vz, ax, ay, az;
    private final KeyBinding logginKeybind = new KeyBinding("Velo logging", 0, "Debug");
    private boolean logValues;

    public VelocityTracker() {
        ClientRegistry.registerKeyBinding(logginKeybind);
    }

    @SubscribeEvent
    public void onKeyPressed(KeyInputEvent event) {
        if (logginKeybind.isPressed()) {
            logValues = !logValues;
        }
    }

    @SubscribeEvent
    public void onTickStart(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && mc.thePlayer != null) {
            final double prevx = x;
            final double prevy = y;
            final double prevz = z;
            final double prevvx = vx;
            final double prevvy = vy;
            final double prevvz = vz;
            x = mc.thePlayer.posX;
            y = mc.thePlayer.posY;
            z = mc.thePlayer.posZ;
            vx = (x - prevx) * 20D;
            vy = (y - prevy) * 20D;
            vz = (z - prevz) * 20D;
            ax = (vx - prevvx) * 20D;
            ay = (vy - prevvy) * 20D;
            az = (vz - prevvz) * 20D;
            if (logValues) {
                MegaWallsEnhancementsMod.logger.info("Pos " + new Vector3D(x, y, z));
                final Vector3D veloVect = new Vector3D(vx, vy, vz);
                MegaWallsEnhancementsMod.logger.info("Velo " + veloVect + " " + String.format("%.4f", veloVect.norm()));
                final Vector3D accelVect = new Vector3D(ax, ay, az);
                MegaWallsEnhancementsMod.logger.info("Accel " + accelVect + " " + String.format("%.4f", accelVect.norm()));
            }
        }
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            final int drawX = event.resolution.getScaledWidth() / 8;
            int drawY = event.resolution.getScaledHeight() / 4;
            mc.fontRendererObj.drawStringWithShadow("Pos " + new Vector3D(x, y, z), drawX, drawY, 0xFFFFFFFF);
            drawY += mc.fontRendererObj.FONT_HEIGHT;
            final Vector3D veloVect = new Vector3D(vx, vy, vz);
            mc.fontRendererObj.drawStringWithShadow("Velo " + veloVect + " " + String.format("%.4f", veloVect.norm()), drawX, drawY, 0xFFFFFFFF);
            drawY += mc.fontRendererObj.FONT_HEIGHT;
            final Vector3D accelVect = new Vector3D(ax, ay, az);
            mc.fontRendererObj.drawStringWithShadow("Accel " + accelVect + " " + String.format("%.4f", accelVect.norm()), drawX, drawY, 0xFFFFFFFF);
        }
    }

}
