package fr.alexdoru.mwe.asm.loader;

public class InjectionCallback {

    private int count = -1;

    public void addInjection() {
        count--;
    }

    public void setInjectionPoints(int injectionPoints) {
        if (count!=-1) {
            throw new IllegalStateException("Count can only be set once");
        }
        count = injectionPoints;
    }

    public int getCount() {
        return count;
    }

    public boolean isTransformationSuccessful() {
        return count == 0;
    }

}