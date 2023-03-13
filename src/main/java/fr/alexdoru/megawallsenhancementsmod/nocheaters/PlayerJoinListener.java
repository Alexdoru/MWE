package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerJoinListener {

    private long lastDeathTime;

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (System.currentTimeMillis() - lastDeathTime > 5000L) {
            NameUtil.clearWarningMessagesPrinted();
        }
    }

    @SubscribeEvent
    public void onGuiScreen(GuiScreenEvent.InitGuiEvent event) {
        if (event.gui instanceof GuiGameOver) {
            lastDeathTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            try {
                if (event.entity instanceof EntityPlayerSP) {
                    /* Delaying the transformation for self because :
                        - certain fields such as mc.theWorld.getScoreboard().getPlayersTeam(username) are null when you just joined the world
                        - for self the player spawn before receiving a networkplayerinfo packet
                     */
                    new DelayedTask(() -> {
                        NameUtil.getMWPlayerData(((EntityPlayerSP) event.entity).getGameProfile(), true);
                        NameUtil.updateEntityPlayerFields((EntityPlayer) event.entity, true);
                    }, 1);
                } else {
                    NameUtil.updateEntityPlayerFields((EntityPlayer) event.entity, true);
                }
            } catch (Exception e) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Caught an exception when spawning " + event.entity.getName());
                e.printStackTrace();
            }
        }
    }

}
