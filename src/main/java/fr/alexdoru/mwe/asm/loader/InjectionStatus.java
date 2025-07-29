package fr.alexdoru.mwe.asm.loader;

public class InjectionStatus {

    private int injectionCount = -1;

    public void addInjection() {
        injectionCount--;
    }

    public void setInjectionPoints(int injectionPoints) {
        injectionCount = injectionPoints;
    }

    public int getInjectionCount() {
        return injectionCount;
    }

    public boolean isTransformationSuccessful() {
        return injectionCount == 0;
    }

}