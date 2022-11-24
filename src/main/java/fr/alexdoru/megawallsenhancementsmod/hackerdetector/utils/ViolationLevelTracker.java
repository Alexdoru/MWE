package fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils;

public class ViolationLevelTracker {

    private int violationLevel;
    private final int failedCheckWeight;
    private final int successfulCheckWeight;
    private final int flagLevel;

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
        this.violationLevel -= this.successfulCheckWeight;
        if (this.violationLevel < 0) {
            this.violationLevel = 0;
        }
    }

    /**
     * Returns true if the player now exceeds flagging level
     */
    private boolean onCheckFail() {
        this.violationLevel += this.failedCheckWeight;
        if (this.violationLevel >= this.flagLevel) {
            this.violationLevel = 0;
            return true;
        }
        return false;
    }

}
