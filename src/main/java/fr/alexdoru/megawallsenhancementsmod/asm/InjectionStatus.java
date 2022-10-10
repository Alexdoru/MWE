package fr.alexdoru.megawallsenhancementsmod.asm;

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

    public boolean isTransformationSuccessfull() {
        return injectionCount == 0;
    }

}