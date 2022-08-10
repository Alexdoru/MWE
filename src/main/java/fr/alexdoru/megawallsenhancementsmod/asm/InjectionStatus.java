package fr.alexdoru.megawallsenhancementsmod.asm;

public class InjectionStatus {

    private int amount_of_injection;

    public void addInjection() {
        amount_of_injection--;
    }

    public void setInjectionPoints(int injectionPoints) {
        amount_of_injection = injectionPoints;
    }

    public int getAmount_of_injection() {
        return amount_of_injection;
    }

    public boolean isTransformationSuccessfull() {
        return amount_of_injection == 0;
    }

}