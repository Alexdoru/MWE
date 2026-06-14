package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.events.KillCounterEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public final class RenegadeArrowTracker {

    private final Map<String, List<Long>> arrowHitMap = new HashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START || arrowHitMap.isEmpty()) {
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

    @SubscribeEvent
    public void onKill(KillCounterEvent event) {
        arrowHitMap.remove(event.victim);
    }

    public void addArrowOnPlayer(String playername, int currentAmountOfArrows) {
        final long timeOfHit = System.currentTimeMillis();
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

    public void removeArrowsFrom(String playername, int arrowAmount) {
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

    public List<Long> getArrowsForPlayer(String playername) {
        return arrowHitMap.get(playername);
    }

}
