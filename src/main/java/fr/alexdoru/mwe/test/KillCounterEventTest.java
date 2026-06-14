package fr.alexdoru.mwe.test;

import fr.alexdoru.mwe.api.events.KillCounterEvent;
import fr.alexdoru.mwe.chat.ChatUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class KillCounterEventTest {

    @SubscribeEvent
    public void on(KillCounterEvent event) {
        ChatUtil.debug("KillCounterEvent " + event.type);
    }

    @SubscribeEvent
    public void on(KillCounterEvent.NormalKill event) {
        ChatUtil.debug("KillCounterEvent.NormalKill " + event.toString());
    }

    @SubscribeEvent
    public void on(KillCounterEvent.FinalKill event) {
        ChatUtil.debug("KillCounterEvent.FinalKill " + event.toString());
    }

    @SubscribeEvent
    public void on(KillCounterEvent.NormalDeath event) {
        ChatUtil.debug("KillCounterEvent.NormalDeath " + event.toString());
    }

    @SubscribeEvent
    public void on(KillCounterEvent.FinalDeath event) {
        ChatUtil.debug("KillCounterEvent.FinalDeath " + event.toString());
    }

}
