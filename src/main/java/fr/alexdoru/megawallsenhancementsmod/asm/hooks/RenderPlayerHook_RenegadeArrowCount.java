package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.TimerUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class RenderPlayerHook_RenegadeArrowCount {

    private static final TimerUtil timer = new TimerUtil(1000L);
    private static final HashMap<String, List<Long>> arrowHitMap = new HashMap<>();

    @SuppressWarnings("unused")
    public static StringBuilder getArrowCount(StringBuilder str, AbstractClientPlayer entityIn) {
        if (ConfigHandler.renegadeArrowCount) {
            final List<Long> list = arrowHitMap.get(entityIn.getName());
            if (list == null || list.isEmpty()) {
                return str;
            }
            return str.append(EnumChatFormatting.RESET).append("  ").append(list.size()).append(EnumChatFormatting.GREEN).append(" ➹");
        }
        return str;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START || !timer.update() || arrowHitMap.isEmpty()) {
            return;
        }
        final long currentTime = System.currentTimeMillis();
        final Iterator<List<Long>> iterator = arrowHitMap.values().iterator();
        while (iterator.hasNext()) {
            final List<Long> list = iterator.next();
            for (int i = 0; i < list.size(); i++) {
                if (currentTime - list.get(i) > (i == 0 ? 60000L : 180000L)) {
                    list.remove(i);
                    i--;
                }
            }
            if (list.isEmpty()) {
                iterator.remove();
            }
        }
    }

    public static void addArrowOnPlayer(String playername, long timeOfHit, int currentAmountOfArrows) {
        final List<Long> list = arrowHitMap.get(playername);
        if (list == null) {
            final List<Long> newlist = new ArrayList<>();
            newlist.add(timeOfHit);
            arrowHitMap.put(playername, newlist);
        } else {
            if (list.size() == 6) {
                list.remove(list.size() - 1);
            }
            list.add(0, timeOfHit);
            if (list.size() > currentAmountOfArrows) {
                removeArrowsFrom(playername, list.size() - currentAmountOfArrows);
            }
        }
    }

    public static void removeArrowsFrom(String playername, int arrowAmount) {
        if (arrowAmount < 0) {
            arrowHitMap.remove(playername);
            return;
        }
        final List<Long> list = arrowHitMap.get(playername);
        if (list == null) {
            return;
        }
        if (arrowAmount >= list.size()) {
            arrowHitMap.remove(playername);
            return;
        }
        int removed = 0;
        for (int i = list.size() - 1; i >= 0 && arrowAmount > removed; i--) {
            list.remove(i);
            removed++;
        }
    }

}
