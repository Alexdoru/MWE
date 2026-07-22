package fr.alexdoru.configlib.lib.gui;

import org.jetbrains.annotations.NotNull;

public enum MouseButton {

    LEFT, RIGHT, MIDDLE, OTHER;

    public boolean isLeft() {
        return this == LEFT;
    }

    public boolean isRight() {
        return this == RIGHT;
    }

    public boolean isMiddle() {
        return this == MIDDLE;
    }

    @NotNull
    public static MouseButton from(int button) {
        switch (button) {
            case 0:
                return LEFT;
            case 1:
                return RIGHT;
            case 2:
                return MIDDLE;
        }
        return OTHER;
    }

}
