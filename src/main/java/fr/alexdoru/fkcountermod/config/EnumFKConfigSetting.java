package fr.alexdoru.fkcountermod.config;

import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;

public enum EnumFKConfigSetting {

    FKCOUNTER_HUD("Show HUD", true, new GuiPosition(0d, 0.1d)),
    COMPACT_HUD("Compact HUD", false, null),
    SHOW_PLAYERS("Show Players", false, null),
    DRAW_BACKGROUND("HUD Background", false, null),
    TEXT_SHADOW("Text Shadow", true, null);
    // TODO add counter to sidebar
    // TODO add the finals to the tablist
    // TODO hud dilation, size

    private final String title;
    private boolean value;
    private final GuiPosition guiPosition;

    EnumFKConfigSetting(String title, boolean defaultValue, GuiPosition guiPosition) {
        this.title = title;
        this.value = defaultValue;
        this.guiPosition = guiPosition;
    }

    public String getTitle() {
        return title;
    }

    public boolean getValue() {
        return value;
    }

    public GuiPosition getHUDPosition() {
        return guiPosition;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void toggleValue() {
        this.value = !value;
    }

}
