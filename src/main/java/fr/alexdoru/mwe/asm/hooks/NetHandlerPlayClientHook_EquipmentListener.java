package fr.alexdoru.mwe.asm.hooks;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S04PacketEntityEquipment;

@SuppressWarnings("unused")
public class NetHandlerPlayClientHook_EquipmentListener {
    public static void onArmorEquip(Entity entity, S04PacketEntityEquipment packet) {}
}
