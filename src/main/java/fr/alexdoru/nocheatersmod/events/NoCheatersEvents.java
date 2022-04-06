package fr.alexdoru.nocheatersmod.events;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoCheatersEvents {

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            try {
                if (event.entity instanceof EntityPlayerSP) {
                    /*Delaying the transformation for self because certain fields such as mc.theWorld.getScoreboard().getPlayersTeam(username) are null when you just joined the world*/
                    new DelayedTask(() -> NameUtil.transformNametag((EntityPlayer) event.entity, true), 1);
                } else {
                    NameUtil.transformNametag((EntityPlayer) event.entity, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
