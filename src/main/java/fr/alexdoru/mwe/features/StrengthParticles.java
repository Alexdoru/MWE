package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.enums.MWClass;
import fr.alexdoru.mwe.api.events.KillCounterEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.DelayedTask;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public final class StrengthParticles {

    private final Random random = new Random();

    @SubscribeEvent
    public void onFinalKill(KillCounterEvent.FinalKill event) {
        if (!MWEConfig.strengthParticles) {
            return;
        }

        final MWClass mwClass = event.killerClass;
        if (mwClass == null) {
            return;
        }

        final int duration;
        if (mwClass == MWClass.DREADLORD) {
            duration = 5 * 20;
        } else if (mwClass == MWClass.HEROBRINE) {
            duration = 6 * 20;
        } else {
            return;
        }

        final EntityPlayer player = NameFormatter.getPlayerEntityByName(event.killer);
        if (player == null) {
            return;
        }

        for (int i = 0; i < duration / 10; i++) {
            new DelayedTask(() -> {
                for (int j = 0; j < 5; ++j) {
                    final double d0 = random.nextGaussian() * 0.02D;
                    final double d1 = random.nextGaussian() * 0.02D;
                    final double d2 = random.nextGaussian() * 0.02D;
                    if (Minecraft.getMinecraft().theWorld != null) {
                        Minecraft.getMinecraft().theWorld.spawnParticle(
                                EnumParticleTypes.VILLAGER_ANGRY,
                                player.posX + (double) (random.nextFloat() * player.width * 2.0F) - (double) player.width,
                                player.posY + 1.0D + (double) (random.nextFloat() * player.height),
                                player.posZ + (double) (random.nextFloat() * player.width * 2.0F) - (double) player.width,
                                d0,
                                d1,
                                d2
                        );
                    }
                }
            }, i * 10);
        }

    }

}
