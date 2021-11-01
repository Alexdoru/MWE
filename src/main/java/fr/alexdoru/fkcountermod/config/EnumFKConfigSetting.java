package fr.alexdoru.fkcountermod.config;

import fr.alexdoru.fkcountermod.gui.hudapi.HUDPosition;

public enum EnumFKConfigSetting {

    FKCOUNTER_HUD("Show HUD", true, new HUDPosition(0d, 0.1d)),
    COMPACT_HUD("Compact HUD", false, null),
    SHOW_PLAYERS("Show Players", false, null),
    DRAW_BACKGROUND("HUD Background", false, null),
    TEXT_SHADOW("Text Shadow", true, null);
    // TODO add counter to sidebar
    // TODO add the finals to the tablist
    // TODO hud dilation, size

    private final String title;
    private boolean value;
    private final HUDPosition hudPosition;

    EnumFKConfigSetting(String title, boolean defaultValue, HUDPosition hudPosition) {
        this.title = title;
        this.value = defaultValue;
        this.hudPosition = hudPosition;
    }

    public String getTitle() {
        return title;
    }

    public boolean getValue() {
        return value;
    }

    public HUDPosition getHUDPosition() {
        return hudPosition;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void toggleValue() {
        this.value = !value;
    }

}
