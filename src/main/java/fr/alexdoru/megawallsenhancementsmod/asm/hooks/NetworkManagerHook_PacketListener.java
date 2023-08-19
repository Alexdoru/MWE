package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import net.minecraft.network.Packet;

@SuppressWarnings("unused")
public class NetworkManagerHook_PacketListener {
    // This code isn't called from the main thread
    public static void listen(@SuppressWarnings("rawtypes") Packet packet) {
        try { // We need a try catch block to prevent any exception from being throwned, it would discard the packet

        } catch (Throwable ignored) {}
    }
}
