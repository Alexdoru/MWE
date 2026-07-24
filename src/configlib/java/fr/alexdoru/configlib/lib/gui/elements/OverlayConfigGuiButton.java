package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ConfigProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class OverlayConfigGuiButton extends ConfigGuiButton {

    protected boolean isOverlayOpen;

    protected OverlayConfigGuiButton(Field field, Method event, ConfigProperty annotation) {
        super(field, event, annotation);
    }

    public boolean isOverlayOpen() {
        return isOverlayOpen;
    }

}
