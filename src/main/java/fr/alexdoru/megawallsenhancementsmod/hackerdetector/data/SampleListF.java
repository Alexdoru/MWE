package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

public class SampleListF {

    private final float[] data;
    /** The maximum size */
    private final int capacity;
    /** The current size of the list */
    private int size;
    /** The array index of the last element inserted */
    private int latestIndex;

    public SampleListF(int capacity) {
        if (capacity < 2) {
            throw new IllegalArgumentException("Size must be at least 2");
        }
        this.data = new float[capacity];
        this.capacity = capacity;
        this.size = 0;
        this.latestIndex = -1;
    }

    public void add(float f) {
        this.latestIndex = (this.latestIndex + 1) % this.capacity;
        this.data[this.latestIndex] = f;
        if (this.size < this.capacity) this.size++;
    }

    /**
     * get(0) will return the latest element insert,
     * get(capacity - 1) will return the oldest element
     */
    public float get(int index) {
        if (index < 0 || index > this.size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final int i = this.latestIndex - index;
        return this.data[i < 0 ? i + this.capacity : i];
    }

    public void clear() {
        this.size = 0;
        this.latestIndex = -1;
    }

    public int size() {
        return this.size;
    }

    public boolean hasCollected() {
        return size == capacity;
    }

    public float sum() {
        float s = 0F;
        for (int i = 0; i < this.size; i++) {
            s += this.data[i];
        }
        return s;
    }

    public float average() {
        return this.sum() / this.size;
    }

}
