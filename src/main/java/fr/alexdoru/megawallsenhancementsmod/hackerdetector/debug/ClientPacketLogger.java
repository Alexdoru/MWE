package fr.alexdoru.megawallsenhancementsmod.hackerdetector.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;

public class ClientPacketLogger {

    private static final Logger logger = LogManager.getLogger("ClientPacketListener");

    static {
        MinecraftForge.EVENT_BUS.register(new ClientPacketLogger());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiIngameMenu) return;
        if (event.phase == TickEvent.Phase.START) {
            log("------ Tick Start ------");
        } else if (event.phase == TickEvent.Phase.END) {
            log("------ Tick End ------");
        }
    }

    public static void logPacket(Packet<?> packet) {
        if (packet instanceof C0BPacketEntityAction) {
            log(
                    stripClassName(packet.getClass().getName())
                            + " " + ((C0BPacketEntityAction) packet).getAction().name()
            );
        } else if (packet instanceof C02PacketUseEntity) {
            log(
                    stripClassName(packet.getClass().getName())
                            + " " + ((C02PacketUseEntity) packet).getAction().name()
            );
        } else {
            log(stripClassName(packet.getClass().getName()));
        }
    }

    private static String stripClassName(String targetClassName) {
        final String[] split = targetClassName.split("\\.");
        return split[split.length - 1];
    }

    private static String formatXYZ(double x, double y, double z) {
        return "{x=" + String.format("%.2f", x) + ", y=" + String.format("%.2f", y) + ", z=" + String.format("%.2f", z) + '}';
    }

    private static void log(String message) {
        final String time = new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis());
        logger.info("[" + time + "] " + message);
    }

}
