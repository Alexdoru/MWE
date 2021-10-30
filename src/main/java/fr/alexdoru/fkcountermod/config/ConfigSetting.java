package fr.alexdoru.fkcountermod.config;

public enum ConfigSetting {

    FKCOUNTER_HUD("Show HUD", true, new SettingData(0, 0.1)),
    COMPACT_HUD("Compact HUD", false, null),
    SHOW_PLAYERS("Show Players", false, null),
    DRAW_BACKGROUND("HUD Background", false, null),
    TEXT_SHADOW("Text Shadow", true, null);
    // TODO add counter to sidebar
    // TODO add the finals to the tablist

    private final String title;
    private boolean value;
    private final SettingData data;

    ConfigSetting(String title, boolean defaultValue, SettingData data) {
        this.title = title;
        this.value = defaultValue;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public boolean getValue() {
        return value;
    }

    public SettingData getData() {
        return data;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void toggleValue() {
        this.value = !value;
    }


}
