package fr.alexdoru.megawallsenhancementsmod.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    public static <T> List<T> removeDuplicates(List<T> list) {
        final List<T> newList = new ArrayList<>(list.size());
        for (final T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

}
