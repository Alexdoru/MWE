package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class WorldHook {
    public static void performChecksOnEntity(World world, Entity entity) {
        if (ConfigHandler.hackerDetector && world.isRemote && entity instanceof EntityPlayer) {
            HackerDetector.INSTANCE.performChecksOnPlayer((EntityPlayer) entity);
        }
    }
}
