package fr.alexdoru.megawallsenhancementsmod.hackerdetector.debug;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.S19PacketEntityStatusAccessor;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;

public class ServerPacketLogger {

    private static final Logger logger = LogManager.getLogger("ServerPacketListener");
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    public static void logPacket(Packet<?> packet) {
        final String time = formatTime();
        mc.addScheduledTask(() -> logger.info(time + packetToString(packet)));
    }

    // has to be called on the main thread
    public static String packetToString(Packet<?> packet) {
        if (packet instanceof S14PacketEntity) {
            final S14PacketEntity packetEntity = (S14PacketEntity) packet;
            return stripClassName(packet.getClass().getName()) +
                    formatEntityName(packetEntity.getEntity(mc.theWorld)) +
                    " | serverPosDiffXYZ : " + formatXYZ(packetEntity.func_149062_c() / 32.0D, packetEntity.func_149061_d() / 32.0D, packetEntity.func_149064_e() / 32.0D) +
                    " | setRotation " + packetEntity.func_149060_h() +
                    " | yaw : " + String.format("%.2f", (float) (packetEntity.func_149066_f() * 360) / 256.0F) +
                    " | pitch " + String.format("%.2f", (float) (packetEntity.func_149063_g() * 360) / 256.0F) +
                    " | onGround " + packetEntity.getOnGround();
        } else if (packet instanceof S19PacketEntityHeadLook) {
            return stripClassName(packet.getClass().getName()) +
                    formatEntityName(((S19PacketEntityHeadLook) packet).getEntity(mc.theWorld)) +
                    " | yaw : " + String.format("%.2f", ((float) (((S19PacketEntityHeadLook) packet).getYaw() * 360) / 256.0F));
        } else if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity packetVelo = (S12PacketEntityVelocity) packet;
            return stripClassName(packet.getClass().getName()) +
                    formatEntityID(packetVelo.getEntityID()) +
                    " | motionXYZ : " + formatXYZ((double) packetVelo.getMotionX() / 8000.0D, (double) packetVelo.getMotionY() / 8000.0D, (double) packetVelo.getMotionZ() / 8000.0D);
        } else if (packet instanceof S20PacketEntityProperties) {
            final StringBuilder str = new StringBuilder();
            for (final S20PacketEntityProperties.Snapshot snapshot : ((S20PacketEntityProperties) packet).func_149441_d()) {
                str.append(snapshot.func_151409_a());
            }
            return stripClassName(packet.getClass().getName()) +
                    formatEntityID(((S20PacketEntityProperties) packet).getEntityId()) +
                    " | attribues updated " + str;
        } else if (packet instanceof S2APacketParticles) {
            final S2APacketParticles packetParticles = (S2APacketParticles) packet;
            return stripClassName(packet.getClass().getName()) +
                    " | particleType : " + packetParticles.getParticleType().name() +
                    " | particleCount : " + packetParticles.getParticleCount() +
                    " | posXYZ : " + formatXYZ(packetParticles.getXCoordinate(), packetParticles.getYCoordinate(), packetParticles.getZCoordinate());
        } else if (packet instanceof S0BPacketAnimation) {
            return stripClassName(packet.getClass().getName()) +
                    formatEntityID(((S0BPacketAnimation) packet).getEntityID()) +
                    " | type : " + ((S0BPacketAnimation) packet).getAnimationType();
        } else if (packet instanceof S19PacketEntityStatus) {
            return stripClassName(packet.getClass().getName()) +
                    formatEntityID(((S19PacketEntityStatusAccessor) packet).getEntityId()) +
                    " | logicOpcode : " + ((S19PacketEntityStatus) packet).getOpCode();
        } else if (packet instanceof S04PacketEntityEquipment) {
            return stripClassName(packet.getClass().getName()) +
                    formatEntityID(((S04PacketEntityEquipment) packet).getEntityID()) +
                    " | equipmentSlot : " + ((S04PacketEntityEquipment) packet).getEquipmentSlot() +
                    (((S04PacketEntityEquipment) packet).getItemStack().getItem() == null ? "" :
                            " | itemStack : " + ((S04PacketEntityEquipment) packet).getItemStack().getItem().getUnlocalizedName());
        } else if (packet instanceof S29PacketSoundEffect) {
            return stripClassName(packet.getClass().getName()) +
                    " | soundName : " + ((S29PacketSoundEffect) packet).getSoundName();
        } else if (packet instanceof S06PacketUpdateHealth) {
            return stripClassName(packet.getClass().getName()) +
                    " | health : " + ((S06PacketUpdateHealth) packet).getHealth() +
                    " | foodLevel : " + ((S06PacketUpdateHealth) packet).getFoodLevel() +
                    " | saturationLevel : " + ((S06PacketUpdateHealth) packet).getSaturationLevel();
        } else {
            return stripClassName(packet.getClass().getName());
        }
    }

    private static String formatEntityName(Entity entity) {
        if (entity == null) {
            return " | entity NULL ";
        } else if (entity instanceof EntityPlayer) {
            return NameUtil.getFormattedNameWithoutIcons(entity.getName()) + EnumChatFormatting.RESET;
        } else {
            return " | entityId : " + entity.getEntityId();
        }
    }

    private static String formatEntityID(int id) {
        final Entity entity = mc.theWorld.getEntityByID(id);
        if (entity instanceof EntityPlayer) {
            return NameUtil.getFormattedNameWithoutIcons(entity.getName()) + EnumChatFormatting.RESET;
        } else if (entity == null) {
            return " | entity NULL ";
        } else {
            return " | entityId : " + id;
        }
    }

    private static String stripClassName(String targetClassName) {
        final String[] split = targetClassName.split("\\.");
        return split[split.length - 1];
    }

    private static String formatXYZ(double x, double y, double z) {
        return "{x=" + String.format("%.2f", x) + ", y=" + String.format("%.2f", y) + ", z=" + String.format("%.2f", z) + '}';
    }

    public static String formatTime() {
        return "[" + timeFormat.format(System.currentTimeMillis()) + "] ";
    }

}
