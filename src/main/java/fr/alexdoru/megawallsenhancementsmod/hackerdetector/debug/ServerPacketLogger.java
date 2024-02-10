package fr.alexdoru.megawallsenhancementsmod.hackerdetector.debug;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.S19PacketEntityStatusAccessor;
import fr.alexdoru.megawallsenhancementsmod.utils.FileLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;

import java.text.SimpleDateFormat;

public class ServerPacketLogger {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    public static final FileLogger logger = new FileLogger("ServerPacketLogger.log", null);

    public static void logPacket(Packet<?> packet) {
        final String time = formatTime();
        mc.addScheduledTask(() -> logger.log(time + packetToString(packet)));
    }

    // has to be called on the main thread
    public static String packetToString(Packet<?> packet) {
        if (packet instanceof S14PacketEntity) {
            final S14PacketEntity packetEntity = (S14PacketEntity) packet;
            final Entity entity = packetEntity.getEntity(mc.theWorld);
            if (entity == null) {
                return stripClassName(packet.getClass().getName()) + formatEntityName(null);
            } else {
                return stripClassName(packet.getClass().getName()) +
                        formatEntityName(entity) +
                        " | serverPosDiffXYZ : " + formatXYZ(packetEntity.func_149062_c() / 32.0D, packetEntity.func_149061_d() / 32.0D, packetEntity.func_149064_e() / 32.0D) +
                        (packetEntity.func_149060_h() ?
                                " | yaw : " + String.format("%.2f", (float) (packetEntity.func_149066_f() * 360) / 256.0F) +
                                        " | pitch " + String.format("%.2f", (float) (packetEntity.func_149063_g() * 360) / 256.0F) : ""
                        ) +
                        " | onGround " + packetEntity.getOnGround();
            }
        } else if (packet instanceof S18PacketEntityTeleport) {
            final S18PacketEntityTeleport packetTeleport = (S18PacketEntityTeleport) packet;
            final Entity entity = mc.theWorld.getEntityByID(packetTeleport.getEntityId());
            if (entity == null) {
                return stripClassName(packet.getClass().getName()) + formatEntityName(null);
            } else {
                return stripClassName(packet.getClass().getName()) + formatEntityName(entity) +
                        " | PosXYZ : " + formatXYZ(packetTeleport.getX() / 32.0D, packetTeleport.getY() / 32.0D, packetTeleport.getZ() / 32.0D) +
                        " | yaw : " + String.format("%.2f", (float) (packetTeleport.getYaw() * 360) / 256.0F) +
                        " | pitch " + String.format("%.2f", (float) (packetTeleport.getPitch() * 360) / 256.0F) +
                        " | onGround " + packetTeleport.getOnGround();
            }
        } else if (packet instanceof S19PacketEntityHeadLook) {
            final Entity entity = ((S19PacketEntityHeadLook) packet).getEntity(mc.theWorld);
            if (entity == null) {
                return stripClassName(packet.getClass().getName()) + formatEntityName(null);
            } else {
                return stripClassName(packet.getClass().getName()) + formatEntityName(entity) +
                        " | yaw : " + String.format("%.2f", ((float) (((S19PacketEntityHeadLook) packet).getYaw() * 360) / 256.0F));
            }
        } else if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity packetVelo = (S12PacketEntityVelocity) packet;
            final Entity entity = mc.theWorld.getEntityByID(packetVelo.getEntityID());
            if (entity == null) {
                return stripClassName(packet.getClass().getName()) + formatEntityName(null);
            } else {
                return stripClassName(packet.getClass().getName()) + formatEntityName(entity) +
                        " | motionXYZ : " + formatXYZ((double) packetVelo.getMotionX() / 8000.0D, (double) packetVelo.getMotionY() / 8000.0D, (double) packetVelo.getMotionZ() / 8000.0D);
            }
        } else if (packet instanceof S20PacketEntityProperties) {
            final StringBuilder str = new StringBuilder();
            final Entity entity = mc.theWorld.getEntityByID(((S20PacketEntityProperties) packet).getEntityId());
            if (entity instanceof EntityLivingBase) {
                for (final S20PacketEntityProperties.Snapshot snapshot : ((S20PacketEntityProperties) packet).func_149441_d()) {
                    str.append(snapshot.func_151409_a()).append(" ");
                }
            }
            return stripClassName(packet.getClass().getName()) + formatEntityName(entity) +
                    " | attribues updated " + str;
        } else if (packet instanceof S2APacketParticles) {
            final S2APacketParticles packetParticles = (S2APacketParticles) packet;
            return stripClassName(packet.getClass().getName()) +
                    " | particleType : " + packetParticles.getParticleType().name() +
                    " | particleCount : " + packetParticles.getParticleCount() +
                    " | posXYZ : " + formatXYZ(packetParticles.getXCoordinate(), packetParticles.getYCoordinate(), packetParticles.getZCoordinate());
        } else if (packet instanceof S0BPacketAnimation) {
            final String type;
            switch (((S0BPacketAnimation) packet).getAnimationType()) {
                case 0:
                    type = "(swing)";
                    break;
                case 1:
                    type = "(hurt)";
                    break;
                case 4:
                    type = "(critical)";
                    break;
                case 5:
                    type = "(sharpness)";
                    break;
                default:
                    type = "";
            }
            return stripClassName(packet.getClass().getName()) +
                    formatEntityID(((S0BPacketAnimation) packet).getEntityID()) +
                    " | type : " + ((S0BPacketAnimation) packet).getAnimationType() + type;
        } else if (packet instanceof S19PacketEntityStatus) {
            final String type;
            switch (((S19PacketEntityStatus) packet).getOpCode()) {
                case 2:
                    type = "(hurt)";
                    break;
                case 3:
                    type = "(death)";
                    break;
                default:
                    type = "";
            }
            return stripClassName(packet.getClass().getName()) +
                    formatEntityID(((S19PacketEntityStatusAccessor) packet).getEntityId()) +
                    " | logicOpcode : " + ((S19PacketEntityStatus) packet).getOpCode() + type;
        } else if (packet instanceof S04PacketEntityEquipment) {
            final Entity entity = mc.theWorld.getEntityByID(((S04PacketEntityEquipment) packet).getEntityID());
            if (entity == null) {
                return stripClassName(packet.getClass().getName()) + formatEntityName(null);
            } else {
                final ItemStack itemStack = ((S04PacketEntityEquipment) packet).getItemStack();
                final Item item = itemStack == null ? null : itemStack.getItem();
                return stripClassName(packet.getClass().getName()) + formatEntityName(entity) +
                        " | equipmentSlot : " + ((S04PacketEntityEquipment) packet).getEquipmentSlot() +
                        (item == null ? "" : " | item : " + item.getUnlocalizedName() + " | count " + itemStack.stackSize);
            }
        } else if (packet instanceof S29PacketSoundEffect) {
            return stripClassName(packet.getClass().getName()) +
                    " | soundName : " + ((S29PacketSoundEffect) packet).getSoundName();
        } else if (packet instanceof S06PacketUpdateHealth) {
            return stripClassName(packet.getClass().getName()) +
                    " | health : " + ((S06PacketUpdateHealth) packet).getHealth() +
                    " | foodLevel : " + ((S06PacketUpdateHealth) packet).getFoodLevel() +
                    " | saturationLevel : " + ((S06PacketUpdateHealth) packet).getSaturationLevel();
        } else if (packet instanceof S1CPacketEntityMetadata) {
            final StringBuilder str = new StringBuilder();
            final Entity entity = mc.theWorld.getEntityByID(((S1CPacketEntityMetadata) packet).getEntityId());
            if (entity != null && ((S1CPacketEntityMetadata) packet).func_149376_c() != null) {
                for (final DataWatcher.WatchableObject watchableObject : ((S1CPacketEntityMetadata) packet).func_149376_c()) {
                    if (entity instanceof EntityPlayer) {
                        switch (watchableObject.getDataValueId()) {
                            case 6:
                                str.append(" | health ").append(watchableObject.getObject());
                                break;
                            case 7:
                                str.append(" | potionParticleColor ").append(watchableObject.getObject());
                                break;
                            case 17:
                                str.append(" | absorptionAmount ").append(watchableObject.getObject());
                                break;
                            case 0:
                                if (watchableObject.getObject() instanceof Byte) {
                                    final byte i = (Byte) watchableObject.getObject();
                                    str.append(" | isBurning ").append((i & 1) != 0);
                                    str.append(" | isSneaking ").append((i & 1 << 1) != 0);
                                    str.append(" | isSprinting ").append((i & 1 << 3) != 0);
                                    str.append(" | isEating ").append((i & 1 << 4) != 0);
                                    str.append(" | isInvisible ").append((i & 1 << 5) != 0);
                                    break;
                                }
                            default:
                                str.append(" | DataValueId ").append(watchableObject.getDataValueId()).append(" object ").append(watchableObject.getObject());
                                break;
                        }
                    } else {
                        str.append(" | DataValueId ").append(watchableObject.getDataValueId()).append(" object ").append(watchableObject.getObject());
                    }
                }
            }
            return stripClassName(packet.getClass().getName()) + formatEntityName(entity) + str;
        } else if (packet instanceof S25PacketBlockBreakAnim) {
            final S25PacketBlockBreakAnim packetBlockBreakAnim = (S25PacketBlockBreakAnim) packet;
            return stripClassName(packet.getClass().getName())
                    + formatEntityID(packetBlockBreakAnim.getBreakerId())
                    + " | position " + packetBlockBreakAnim.getPosition()
                    + " | progress " + packetBlockBreakAnim.getProgress();
        } else {
            return stripClassName(packet.getClass().getName());
        }
    }

    private static String formatEntityName(Entity entity) {
        if (entity == null) {
            return " | entity NULL";
        } else if (entity instanceof EntityPlayer) {
            return " " + entity.getName();
        } else {
            return " | entityId : " + entity.getEntityId();
        }
    }

    private static String formatEntityID(int id) {
        final Entity entity = mc.theWorld.getEntityByID(id);
        if (entity instanceof EntityPlayer) {
            return " " + entity.getName();
        } else if (entity == null) {
            return " | entity NULL";
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
