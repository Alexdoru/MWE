package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Used to store samples
 * Added item are added at the 0 index and the last item gets removed
 * <p>
 * Using a LinkedList for performance because it's O(1) for
 * get(int index), add(int index, E element), remove(int index)
 * if index = first or last element
 */
public final class SampleList<T> extends LinkedList<T> {

    private final int maxSize;

    public SampleList(int maxSize) {
        if (maxSize < 2) {
            throw new IllegalArgumentException("maxSize can't be smaller than 2");
        }
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T elem) {
        if (super.size() >= maxSize) {
            super.removeLast();
        }
        super.addFirst(elem);
        return true;
    }

    public boolean hasCollectedSample() {
        return super.size() == maxSize;
    }

    @Override
    public void add(int index, T element) {
        throw new IllegalStateException("Can't use add(int index, T element) with SampleList !");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new IllegalStateException("Can't use addAll with SampleList !");
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new IllegalStateException("Can't use addAll with SampleList !");
    }

}
