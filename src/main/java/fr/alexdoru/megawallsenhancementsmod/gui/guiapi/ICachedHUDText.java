package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

public interface ICachedHUDText {

    /**
     * Returns the stored text to be displayed
     */
    String getDisplayText();

    /**
     * Updates the display text when it changes and not on every frame
     */
    void updateDisplayText();

}
