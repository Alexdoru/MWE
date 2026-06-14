package fr.alexdoru.mwe.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class MapUtil {

    private MapUtil() {}

    public static <K extends Comparable<K>, V> List<Map.Entry<K, V>> sortByKey(Map<K, V> map) {
        return sort(map, Map.Entry.comparingByKey());
    }

    public static <K extends Comparable<K>, V> List<Map.Entry<K, V>> sortByKeyReversed(Map<K, V> map) {
        return sort(map, Map.Entry.<K, V>comparingByKey().reversed());
    }

    public static <K, V extends Comparable<V>> List<Map.Entry<K, V>> sortByValue(Map<K, V> map) {
        return sort(map, Map.Entry.comparingByValue());
    }

    public static <K, V extends Comparable<V>> List<Map.Entry<K, V>> sortByValueReversed(Map<K, V> map) {
        return sort(map, Map.Entry.<K, V>comparingByValue().reversed());
    }

    private static <K, V> List<Map.Entry<K, V>> sort(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
        final List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(comparator);
        return list;
    }

}
