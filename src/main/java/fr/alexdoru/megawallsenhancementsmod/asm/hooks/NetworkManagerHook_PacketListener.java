package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.S19PacketEntityStatusAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class NetworkManagerHook_PacketListener {

    private static final Logger logger = LogManager.getLogger("PacketListener");
    private static boolean lastPacketWasSwing;
    private static int attackingEntityID;

    // Carefull, this code isn't called from the main thread
    public static void listen(@SuppressWarnings("rawtypes") Packet packet) {
        try { // We need a try catch block to prevent any exception from being throwned, it would discard the packet

        } catch (Throwable ignored) {}
    }

    private static void logPacket(@SuppressWarnings("rawtypes") Packet packet) {
        if (packet instanceof S2APacketParticles) return;
        if (packet instanceof S0BPacketAnimation) {
            logger.info(
                    "S0BPacketAnimation" +
                            " entityId : " + ((S0BPacketAnimation) packet).getEntityID() +
                            " type : " + ((S0BPacketAnimation) packet).getAnimationType()
            );
        } else if (packet instanceof S19PacketEntityStatusAccessor) {
            logger.info(
                    stripClassName(packet.getClass().getName()) +
                            " entityId : " + ((S19PacketEntityStatusAccessor) packet).getEntityId() +
                            " logicOpcode : " + ((S19PacketEntityStatus) packet).getOpCode()
            );
        } else if (packet instanceof S04PacketEntityEquipment) {
            logger.info(
                    stripClassName(packet.getClass().getName()) +
                            " entityId : " + ((S04PacketEntityEquipment) packet).getEntityID() +
                            " equipmentSlot : " + ((S04PacketEntityEquipment) packet).getEquipmentSlot() +
                            (((S04PacketEntityEquipment) packet).getItemStack().getItem() == null ? "" :
                                    " itemStack : " + ((S04PacketEntityEquipment) packet).getItemStack().getItem().getUnlocalizedName())
            );
        } else if (packet instanceof S29PacketSoundEffect) {
            logger.info(
                    stripClassName(packet.getClass().getName()) +
                            " soundName : " + ((S29PacketSoundEffect) packet).getSoundName()
            );
        } else if (packet instanceof S06PacketUpdateHealth) {
            logger.info(
                    stripClassName(packet.getClass().getName()) +
                            " health : " + ((S06PacketUpdateHealth) packet).getHealth() +
                            " foodLevel : " + ((S06PacketUpdateHealth) packet).getFoodLevel() +
                            " saturationLevel : " + ((S06PacketUpdateHealth) packet).getSaturationLevel()
            );
        } else {
            logger.info(stripClassName(packet.getClass().getName()));
        }
    }

    private static void checkAttackEvent(int attackingEntityID, int targetId) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            // TODO pour le player SP regarder le paquet S06PacketUpdateHealth handleUpdateHealth mais
            //  que quand ca fait baisser la vie dans {@link EntityPlayerSP#setPlayerSPHealth}
            if (Minecraft.getMinecraft().theWorld != null) {
                final Entity attacker = Minecraft.getMinecraft().theWorld.getEntityByID(attackingEntityID);
                final Entity target = Minecraft.getMinecraft().theWorld.getEntityByID(targetId);
                if (attacker instanceof EntityPlayer && target instanceof EntityPlayer && attacker != target) {
                    logger.info(
                            attacker.getName() + " (" + attacker.getEntityId() + ")" +
                                    " attacked " +
                                    target.getName() + " (" + target.getEntityId() + ")" +
                                    " distance " + target.getDistanceToEntity(attacker)
                    );
                    ChatUtil.debug(
                            NameUtil.getFormattedNameWithoutIcons(attacker.getName())
                                    + EnumChatFormatting.RESET + " attacked "
                                    + NameUtil.getFormattedNameWithoutIcons(target.getName())
                    );
                }
            }
        });
    }

    private static String stripClassName(String targetClassName) {
        final String[] split = targetClassName.split("\\.");
        return split[split.length - 1];
    }

    private static void lookForAttacks(@SuppressWarnings("rawtypes") Packet packet) {
        if (lastPacketWasSwing) {
            if (packet instanceof S19PacketEntityStatusAccessor && ((S19PacketEntityStatus) packet).getOpCode() == 2) { // Entity gets hurt
                checkAttackEvent(attackingEntityID, ((S19PacketEntityStatusAccessor) packet).getEntityId());
            } else if (packet instanceof S0BPacketAnimation && (((S0BPacketAnimation) packet).getAnimationType() == 4 || ((S0BPacketAnimation) packet).getAnimationType() == 5)) { // crit particle packet
                checkAttackEvent(attackingEntityID, ((S0BPacketAnimation) packet).getEntityID());
            }
        }
        if (packet instanceof S0BPacketAnimation && ((S0BPacketAnimation) packet).getAnimationType() == 0) { // Swing packet
            lastPacketWasSwing = true;
            attackingEntityID = ((S0BPacketAnimation) packet).getEntityID();
        } else {
            lastPacketWasSwing = false;
        }
    }

}
