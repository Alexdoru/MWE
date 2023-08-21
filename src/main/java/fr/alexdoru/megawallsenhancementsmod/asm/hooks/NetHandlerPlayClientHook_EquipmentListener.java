package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S04PacketEntityEquipment;

@SuppressWarnings("unused")
public class NetHandlerPlayClientHook_EquipmentListener {
    public static void onArmorEquip(Entity entity, S04PacketEntityEquipment packet) {
        if (ConfigHandler.hackerDetector && entity instanceof EntityPlayer && packet.getEquipmentSlot() != 0) {
            HackerDetector.onEquipmentPacket(((EntityPlayer) entity), packet);
        }
    }
}
