package fr.alexdoru.megawallsenhancementsmod.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {

    public static <K extends Comparable<K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByKey());
        final Map<K, V> out = new LinkedHashMap<>();
        for (final Map.Entry<K, V> e : list) {
            out.put(e.getKey(), e.getValue());
        }
        return out;
    }

    public static <K extends Comparable<K>, V> Map<K, V> sortByDecreasingKey(Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getKey().compareTo(o1.getKey()));
        final Map<K, V> out = new LinkedHashMap<>();
        for (final Map.Entry<K, V> e : list) {
            out.put(e.getKey(), e.getValue());
        }
        return out;
    }

    public static <K, V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        final Map<K, V> out = new LinkedHashMap<>();
        for (final Map.Entry<K, V> e : list) {
            out.put(e.getKey(), e.getValue());
        }
        return out;
    }

    public static <K, V extends Comparable<V>> Map<K, V> sortByDecreasingValue(Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        final Map<K, V> out = new LinkedHashMap<>();
        for (final Map.Entry<K, V> e : list) {
            out.put(e.getKey(), e.getValue());
        }
        return out;
    }

}
