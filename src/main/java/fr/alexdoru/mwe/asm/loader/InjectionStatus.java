package fr.alexdoru.mwe.asm.loader;

public class InjectionStatus {

    private int injectionCount = -1;
    private boolean skipTransformation = false;

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

    public void skipTransformation() {
        this.skipTransformation = true;
    }

    public boolean isSkippingTransformation() {
        return this.skipTransformation;
    }

}