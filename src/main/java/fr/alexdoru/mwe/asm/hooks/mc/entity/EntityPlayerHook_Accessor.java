package fr.alexdoru.mwe.asm.hooks.mc.entity;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.hackerdetector.data.PlayerDataSamples;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityPlayerHook_Accessor {

    public static PlayerDataSamples createDataSample(World world) {
        if (world.isRemote && MWEConfig.hackerDetector) {
            return new PlayerDataSamples();
        }
        return null;
    }

    public static PlayerDataSamples getPlayerDataSamples(EntityPlayer player, PlayerDataSamples data) {
        if (data == null) {
            return lazilyCreateData(player);
        }
        return data;
    }

    private static PlayerDataSamples lazilyCreateData(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            MWE.logger.warn("PlayerDataSamples accessed for non client player", new Throwable());
        }
        final PlayerDataSamples data = new PlayerDataSamples();
        ((EntityPlayerAccessor) player).setPlayerDataSamples(data);
        return data;
    }

}
