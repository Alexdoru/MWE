package fr.alexdoru.mwe.hackerdetector.utils;

public class ViolationLevelTracker {

    private int violationLevel;
    private final int failedCheckWeight;
    private final int successfulCheckWeight;
    private final int flagLevel;

    /**
     * Use this constructor to manage the violation level manually
     * in the {@link fr.alexdoru.mwe.hackerdetector.checks.ICheck#check} calls
     */
    public ViolationLevelTracker(int flagLevel) {
        this.failedCheckWeight = 0;
        this.successfulCheckWeight = 0;
        this.flagLevel = flagLevel;
    }

    /**
     * Use this constructor to have the violation level automatically managed on each tick
     */
    public ViolationLevelTracker(int failedCheckWeight, int successfulCheckWeight, int flagLevel) {
        this.failedCheckWeight = failedCheckWeight;
        this.successfulCheckWeight = successfulCheckWeight;
        this.flagLevel = flagLevel;
    }

    /**
     * Returns true if the player now exceeds flagging level
     */
    public boolean isFlagging(boolean failedCheck) {
        if (failedCheck) {
            return onCheckFail();
        } else {
            onCheckSuccess();
            return false;
        }
    }

    private void onCheckSuccess() {
        this.substract(this.successfulCheckWeight);
        if (this.violationLevel < 0) {
            this.violationLevel = 0;
        }
    }

    /**
     * Returns true if the player now exceeds flagging level
     */
    private boolean onCheckFail() {
        this.add(this.failedCheckWeight);
        if (this.violationLevel >= this.flagLevel) {
            this.violationLevel = 0;
            return true;
        }
        return false;
    }

    /**
     * Method used to manage the violation level manually
     * Needs to be called in {@link fr.alexdoru.mwe.hackerdetector.checks.ICheck#check}
     */
    public void add(int i) {
        this.violationLevel += i;
    }

    /**
     * Method used to manage the violation level manually
     * Needs to be called in {@link fr.alexdoru.mwe.hackerdetector.checks.ICheck#check}
     */
    public void substract(int i) {
        this.violationLevel -= i;
    }

    public int getViolationLevel() {
        return violationLevel;
    }
}
