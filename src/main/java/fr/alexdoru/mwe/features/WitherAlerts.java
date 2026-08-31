package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.api.events.WitherHealthDecayEvent;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.SoundUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class WitherAlerts {

    private boolean triggeredBlueWitherAlert;
    private boolean triggeredGreenWitherAlert;
    private boolean triggeredRedWitherAlert;
    private boolean triggeredYellowWitherAlert;

    @SubscribeEvent
    public void onGameStart(MegaWallsGameEvent event) {
        if (event.type == Type.GAME_START) {
            triggeredBlueWitherAlert = false;
            triggeredGreenWitherAlert = false;
            triggeredRedWitherAlert = false;
            triggeredYellowWitherAlert = false;
        }
    }

    @SubscribeEvent
    public void onWitherHealthDecay(WitherHealthDecayEvent event) {
        if (MWEConfig.witherAlerts && event.health < MWEConfig.witherAlertsThreshold) {
            boolean playNotif = false;
            switch (event.team) {
                case BLUE: {
                    if (!triggeredBlueWitherAlert) {
                        triggeredBlueWitherAlert = true;
                        playNotif = true;
                    }
                    break;
                }
                case GREEN: {
                    if (!triggeredGreenWitherAlert) {
                        triggeredGreenWitherAlert = true;
                        playNotif = true;
                    }
                    break;
                }
                case RED: {
                    if (!triggeredRedWitherAlert) {
                        triggeredRedWitherAlert = true;
                        playNotif = true;
                    }
                    break;
                }
                case YELLOW: {
                    if (!triggeredYellowWitherAlert) {
                        triggeredYellowWitherAlert = true;
                        playNotif = true;
                    }
                    break;
                }
            }
            if (playNotif) {
                ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "The " + event.team.getColorPrefix() + event.team.getName() + " Wither " + EnumChatFormatting.GREEN + "is below " + EnumChatFormatting.YELLOW + MWEConfig.witherAlertsThreshold + "HP!");
                SoundUtil.playNotePling();
            }
        }
    }

}
