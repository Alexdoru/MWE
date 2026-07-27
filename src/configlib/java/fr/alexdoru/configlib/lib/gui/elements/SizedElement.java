package fr.alexdoru.configlib.lib.gui.elements;

public interface SizedElement {

    int getHeight();

    default int getTopMargin() { return 2; }

    default int getBottomMargin() { return 2; }
}
